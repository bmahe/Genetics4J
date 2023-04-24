package net.bmahe.genetics4j.neat.selection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.util.IndividualUtils;
import net.bmahe.genetics4j.neat.NeatUtils;
import net.bmahe.genetics4j.neat.Species;
import net.bmahe.genetics4j.neat.SpeciesIdGenerator;
import net.bmahe.genetics4j.neat.spec.selection.NeatSelection;

public class NeatSelectorImpl<T extends Number & Comparable<T>> implements Selector<T> {
	public static final Logger logger = LogManager.getLogger(NeatSelectorImpl.class);

	private final RandomGenerator randomGenerator;
	private final NeatSelection<T> neatSelection;
	private final SpeciesIdGenerator speciesIdGenerator;
	private final Selector<T> speciesSelector;

	private List<Species<T>> previousSpecies;

	public NeatSelectorImpl(final RandomGenerator _randomGenerator, final NeatSelection<T> _neatSelection,
			final SpeciesIdGenerator _speciesIdGenerator, final Selector<T> _speciesSelector) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_neatSelection);
		Validate.notNull(_speciesIdGenerator);
		Validate.notNull(_speciesSelector);

		this.randomGenerator = _randomGenerator;
		this.neatSelection = _neatSelection;
		this.speciesIdGenerator = _speciesIdGenerator;
		this.speciesSelector = _speciesSelector;

		this.previousSpecies = new ArrayList<>();
	}

	protected Species<T> trimSpecies(final Species<T> species, final Comparator<Individual<T>> comparator,
			final int minSpeciesSize, final float perSpeciesKeepRatio) {
		Validate.notNull(species);

		final List<Individual<T>> members = species.getMembers();
		final float speciesSize = members.size();
		final int numIndividualtoKeep = (int) Math.max(minSpeciesSize, speciesSize * perSpeciesKeepRatio);

		if (logger.isDebugEnabled()) {
			logger.debug(
					"Species id: {}, size: {}, perSepciesKeepRatio: {}, we want to keep {} members - best fitness: {}",
					species.getId(),
					speciesSize,
					perSpeciesKeepRatio,
					numIndividualtoKeep,
					members.stream()
							.max(comparator)
							.map(Individual::fitness));
		}

		final Species<T> trimmedSpecies = new Species<>(species.getId(), List.of());
		if (numIndividualtoKeep > 0) {
			final var selectedIndividuals = members.stream()
					.sorted(comparator.reversed())
					.limit(numIndividualtoKeep)
					.toList();

			trimmedSpecies.addAllMembers(selectedIndividuals);
		}
		return trimmedSpecies;

	}

	protected List<Species<T>> eliminateLowestPerformers(final AbstractEAConfiguration<T> eaConfiguration,
			final List<Species<T>> allSpecies) {
		Validate.notNull(eaConfiguration);
		Validate.notNull(allSpecies);

		final Comparator<Individual<T>> comparator = IndividualUtils.fitnessBasedComparator(eaConfiguration);

		final float perSpeciesKeepRatio = neatSelection.perSpeciesKeepRatio();
		logger.trace("Keeping only the best {} number of individuals per species", perSpeciesKeepRatio);

		final int minSpeciesSize = neatSelection.minSpeciesSize();

		return allSpecies.stream()
				.map(species -> trimSpecies(species, comparator, minSpeciesSize, perSpeciesKeepRatio))
				.filter(species -> species.getNumMembers() > 0)
				.toList();
	}

	@Override
	public Population<T> select(final AbstractEAConfiguration<T> eaConfiguration, final int numIndividuals,
			final List<Genotype> genotypes, final List<T> fitnessScore) {
		Validate.notNull(eaConfiguration);
		Validate.notNull(genotypes);
		Validate.notNull(fitnessScore);
		Validate.isTrue(numIndividuals > 0);
		Validate.isTrue(genotypes.size() == fitnessScore.size());

		final Population<T> population = Population.of(genotypes, fitnessScore);

		final List<Species<T>> allSpecies = NeatUtils.speciate(randomGenerator,
				speciesIdGenerator,
				previousSpecies,
				population,
				neatSelection.speciesPredicate());

		logger.debug("Number of species found: {}", allSpecies.size());
		logger.trace("Species: {}", allSpecies);

		/**
		 * We want to remove the bottom performers of each species
		 */
		final var allTrimmedSpecies = eliminateLowestPerformers(eaConfiguration, allSpecies);
		logger.debug("After trimming, we have {} species", allTrimmedSpecies.size());

		previousSpecies = allTrimmedSpecies;
		if (allTrimmedSpecies.size() == 0) {
			return Population.empty();
		}

		/**
		 * Now we want to select the next generation on a per species basis and
		 * proportionally to the sum of the fitnesses of each members
		 */

		final double[] sumFitnesses = new double[allTrimmedSpecies.size()];
		double totalSum = 0;
		for (int i = 0; i < allTrimmedSpecies.size(); i++) {
			final var species = allTrimmedSpecies.get(i);
			sumFitnesses[i] = species.getMembers()
					.stream()
					.mapToDouble(individual -> individual.fitness()
							.doubleValue())
					.sum() / (float) species.getNumMembers();
			totalSum += sumFitnesses[i];
		}

		final List<Integer> decreasingFitnessIndex = IntStream.range(0, sumFitnesses.length)
				.boxed()
				.sorted(Comparator.comparing(i -> sumFitnesses[(int) i])
						.reversed())
				.toList();

		final Population<T> selected = new Population<>();

		final List<Population<T>> trimmedPopulations = allTrimmedSpecies.stream()
				.map(species -> Population.of(species.getMembers()))
				.toList();

		int i = 0;
		while (selected.size() < numIndividuals && i < sumFitnesses.length) {
			int speciesIndex = decreasingFitnessIndex.get(i);

			int numIndividualSpecies = (int) (numIndividuals * sumFitnesses[speciesIndex] / totalSum);
			if (numIndividualSpecies > numIndividuals - selected.size()) {
				numIndividualSpecies = numIndividuals - selected.size();
			}

			if (numIndividualSpecies > 0) {
				final Population<T> speciesPopulation = trimmedPopulations.get(speciesIndex);

				logger.debug("sub selecting {} for index {} - species id: {}",
						numIndividualSpecies,
						speciesIndex,
						allTrimmedSpecies.get(speciesIndex)
								.getId());

				final var selectedFromSpecies = speciesSelector.select(eaConfiguration,
						numIndividualSpecies,
						speciesPopulation.getAllGenotypes(),
						speciesPopulation.getAllFitnesses());

				selected.addAll(selectedFromSpecies);
			}

			i++;
		}

		if (selected.size() < numIndividuals) {
			logger.debug("There are less selected individual [{}] than desired [{}]. Will include additional invididuals",
					selected.size(),
					numIndividuals);
			final Population<T> speciesPopulation = trimmedPopulations.get(decreasingFitnessIndex.get(0));

			selected.addAll(speciesSelector.select(eaConfiguration,
					numIndividuals - selected.size(),
					speciesPopulation.getAllGenotypes(),
					speciesPopulation.getAllFitnesses()));
		}

		return selected;
	}
}