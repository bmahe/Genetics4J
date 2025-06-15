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

/**
 * Concrete implementation of species-based selection for NEAT (NeuroEvolution of Augmenting Topologies) algorithm.
 * 
 * <p>NeatSelectorImpl implements the core species-based selection mechanism that is fundamental to NEAT's
 * ability to maintain population diversity and protect structural innovations. It organizes the population
 * into species based on genetic compatibility, applies fitness sharing within species, manages species
 * lifecycle, and allocates reproduction opportunities across species.
 * 
 * <p>NEAT selection process:
 * <ol>
 * <li><strong>Species assignment</strong>: Organize population into species using compatibility predicate</li>
 * <li><strong>Species trimming</strong>: Remove lower-performing individuals within each species</li>
 * <li><strong>Species filtering</strong>: Eliminate species below minimum viable size</li>
 * <li><strong>Reproduction allocation</strong>: Determine number of offspring for each species</li>
 * <li><strong>Parent selection</strong>: Select parents within species using configured selector</li>
 * <li><strong>Species maintenance</strong>: Update species representatives for next generation</li>
 * </ol>
 * 
 * <p>Key features:
 * <ul>
 * <li><strong>Genetic compatibility</strong>: Groups individuals with similar network topologies</li>
 * <li><strong>Fitness sharing</strong>: Reduces competition between similar individuals</li>
 * <li><strong>Innovation protection</strong>: Allows new topologies time to optimize</li>
 * <li><strong>Population diversity</strong>: Maintains multiple species exploring different solutions</li>
 * </ul>
 * 
 * <p>Species management:
 * <ul>
 * <li><strong>Species formation</strong>: Creates new species when compatibility threshold exceeded</li>
 * <li><strong>Species growth</strong>: Assigns compatible individuals to existing species</li>
 * <li><strong>Species extinction</strong>: Eliminates species that fall below minimum size</li>
 * <li><strong>Species continuity</strong>: Maintains species representatives across generations</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create NEAT selector implementation
 * RandomGenerator randomGen = RandomGenerator.getDefault();
 * SpeciesIdGenerator speciesIdGen = new SpeciesIdGenerator();
 * 
 * // Configure species predicate for compatibility
 * BiPredicate<Individual<Double>, Individual<Double>> speciesPredicate = 
 *     (i1, i2) -> NeatUtils.compatibilityDistance(
 *         i1.genotype(), i2.genotype(), 0, 2, 2, 1.0f
 *     ) < 3.0;  // Compatibility threshold
 * 
 * // Configure NEAT selection policy
 * NeatSelection<Double> neatSelection = NeatSelection.<Double>builder()
 *     .perSpeciesKeepRatio(0.8f)  // Keep top 80% of each species
 *     .minSpeciesSize(5)  // Minimum 5 individuals per species
 *     .speciesPredicate(speciesPredicate)
 *     .speciesSelection(Tournament.of(3))  // Tournament within species
 *     .build();
 * 
 * // Create within-species selector
 * Selector<Double> speciesSelector = new TournamentSelector<>(randomGen, 3);
 * 
 * // Create NEAT selector
 * NeatSelectorImpl<Double> selector = new NeatSelectorImpl<>(
 *     randomGen, neatSelection, speciesIdGen, speciesSelector
 * );
 * 
 * // Use in population selection
 * Population<Double> selectedPopulation = selector.select(
 *     eaConfiguration, population, 100  // Select 100 individuals
 * );
 * }</pre>
 * 
 * <p>Species lifecycle management:
 * <ul>
 * <li><strong>Initialization</strong>: Empty species list for first generation</li>
 * <li><strong>Assignment</strong>: Individuals assigned to species based on compatibility</li>
 * <li><strong>Trimming</strong>: Poor performers removed while preserving species diversity</li>
 * <li><strong>Reproduction</strong>: Offspring allocated proportionally to species average fitness</li>
 * <li><strong>Evolution</strong>: Species composition changes as population evolves</li>
 * </ul>
 * 
 * <p>Fitness sharing mechanism:
 * <ul>
 * <li><strong>Within-species competition</strong>: Individuals primarily compete within their species</li>
 * <li><strong>Species-based allocation</strong>: Reproduction opportunities distributed across species</li>
 * <li><strong>Diversity protection</strong>: Prevents single topology from dominating population</li>
 * <li><strong>Innovation preservation</strong>: New structural innovations get time to optimize</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Compatibility caching</strong>: Species assignment optimized for repeated use</li>
 * <li><strong>Efficient trimming</strong>: In-place species membership updates</li>
 * <li><strong>Memory management</strong>: Species structures reused across generations</li>
 * <li><strong>Parallel processing</strong>: Species-based organization enables concurrent operations</li>
 * </ul>
 * 
 * @param <T> the fitness value type (typically Double)
 * @see NeatSelection
 * @see Species
 * @see SpeciesIdGenerator
 * @see Selector
 */
public class NeatSelectorImpl<T extends Number & Comparable<T>> implements Selector<T> {
	public static final Logger logger = LogManager.getLogger(NeatSelectorImpl.class);

	private final RandomGenerator randomGenerator;
	private final NeatSelection<T> neatSelection;
	private final SpeciesIdGenerator speciesIdGenerator;
	private final Selector<T> speciesSelector;

	private List<Species<T>> previousSpecies;

	/**
	 * Constructs a new NEAT selector implementation with the specified components.
	 * 
	 * <p>The selector uses the random generator for stochastic decisions, the NEAT selection
	 * policy for species management parameters, the species ID generator for creating new
	 * species, and the species selector for within-species parent selection.
	 * 
	 * @param _randomGenerator random number generator for stochastic selection operations
	 * @param _neatSelection NEAT selection policy defining species management parameters
	 * @param _speciesIdGenerator generator for unique species identifiers
	 * @param _speciesSelector selector for choosing parents within each species
	 * @throws IllegalArgumentException if any parameter is null
	 */
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

	/**
	 * Trims a species by removing lower-performing individuals while preserving minimum viable size.
	 * 
	 * <p>This method applies fitness-based selection within a species, keeping the best performers
	 * while ensuring the species maintains sufficient size for genetic diversity. The number of
	 * individuals kept is the maximum of the minimum species size and the keep ratio applied to
	 * the current species size.
	 * 
	 * @param species the species to trim
	 * @param comparator comparator for ranking individuals by fitness
	 * @param minSpeciesSize minimum number of individuals to keep
	 * @param perSpeciesKeepRatio proportion of current species to preserve
	 * @return a new species containing only the selected individuals
	 * @throws IllegalArgumentException if species is null
	 */
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

	/**
	 * Eliminates the lowest-performing individuals from all species while maintaining species diversity.
	 * 
	 * <p>This method applies the species trimming process to all species in the population,
	 * removing poor performers within each species while preserving the overall species
	 * structure. Species that become empty after trimming are filtered out.
	 * 
	 * @param eaConfiguration evolutionary algorithm configuration containing fitness comparator
	 * @param allSpecies list of all species in the population
	 * @return list of species after trimming, with empty species removed
	 * @throws IllegalArgumentException if any parameter is null
	 */
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