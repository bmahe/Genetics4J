package net.bmahe.genetics4j.core;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactory;
import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.ImmutableEvolutionResult;

public class GeneticSystem {
	final static public Logger logger = LogManager.getLogger(GeneticSystem.class);

	private final GenotypeSpec genotypeSpec;
	private final GeneticSystemDescriptor geneticSystemDescriptor;
	private final int populationSize;

	private final List<ChromosomeCombinator> chromosomeCombinators;
	private final ChromosomeFactoryProvider chromosomeFactoryProvider;

	private final List<Mutator> mutators;

	private final double offspringRatio;

	private Selector parentSelector;
	private Selector survivorSelector;

	public GeneticSystem(final GenotypeSpec _genotypeSpec, final long _populationSize,
			final List<ChromosomeCombinator> _chromosomeCombinators, final double _offspringRatio,
			final Selector _parentSelectionPolicyHandler, final Selector _survivorSelector, final List<Mutator> _mutators,
			final GeneticSystemDescriptor _geneticSystemDescriptor) {
		Validate.notNull(_genotypeSpec);
		Validate.isTrue(_populationSize > 0);
		Validate.notNull(_chromosomeCombinators);
		Validate.isTrue(_chromosomeCombinators.size() == _genotypeSpec.numChromosomes());
		Validate.inclusiveBetween(0.0, 1.0, _offspringRatio);
		Validate.notNull(_parentSelectionPolicyHandler);
		Validate.notNull(_survivorSelector);
		Validate.notNull(_geneticSystemDescriptor);

		this.genotypeSpec = _genotypeSpec;
		this.geneticSystemDescriptor = _geneticSystemDescriptor;
		this.populationSize = (int) _populationSize;
		this.chromosomeCombinators = _chromosomeCombinators;
		this.offspringRatio = _offspringRatio;
		this.mutators = _mutators;
		this.chromosomeFactoryProvider = _geneticSystemDescriptor.chromosomeFactoryProvider();

		parentSelector = _parentSelectionPolicyHandler;
		survivorSelector = _survivorSelector;
	}

	public GenotypeSpec getGenotypeSpec() {
		return genotypeSpec;
	}

	public long getPopulationSize() {
		return populationSize;
	}

	public Fitness getFitness() {
		return genotypeSpec.fitness();
	}

	private Genotype[] generatePopulation(final GenotypeSpec genotypeSpec) {
		Validate.notNull(genotypeSpec);

		final Optional<Supplier<Genotype>> populationGenerator = genotypeSpec.populationGenerator();

		final Genotype[] population = new Genotype[populationSize];

		// Override
		if (populationGenerator.isPresent()) {
			final Supplier<Genotype> populationSupplier = populationGenerator.get();

			for (int i = 0; i < populationSize; i++) {
				population[i] = populationSupplier.get();
			}

		} else {

			final int numChromosomes = genotypeSpec.numChromosomes();
			final ChromosomeFactory<? extends Chromosome>[] chromosomeFactories = new ChromosomeFactory<?>[numChromosomes];
			for (int i = 0; i < numChromosomes; i++) {
				chromosomeFactories[i] = chromosomeFactoryProvider
						.provideChromosomeFactory(genotypeSpec.getChromosomeSpec(i));
			}

			for (int i = 0; i < populationSize; i++) {

				final Chromosome[] chromosomes = new Chromosome[numChromosomes];
				for (int j = 0; j < numChromosomes; j++) {
					chromosomes[j] = chromosomeFactories[j].generate(genotypeSpec.getChromosomeSpec(j));
				}

				population[i] = new Genotype(chromosomes);
			}
		}
		return population;
	}

	public EvolutionResult evolve() {
		final Termination termination = genotypeSpec.termination();

		final Fitness fitness = genotypeSpec.fitness();

		long generation = 0;
		Genotype[] population = generatePopulation(genotypeSpec);

		double[] fitnessScore = new double[populationSize];
		for (int i = 0; i < populationSize; i++) {
			fitnessScore[i] = fitness.compute(population[i]);
		}

		while (termination.isDone(generation, population, fitnessScore) == false) {

			for (final EvolutionListener evolutionListener : geneticSystemDescriptor.evolutionListeners()) {
				evolutionListener.onEvolution(generation, population, fitnessScore);
			}

			final int parentsNeeded = (int) (populationSize * offspringRatio * 2);
			logger.trace("Will select {} parents", parentsNeeded);
			final List<Genotype> selectedParents = parentSelector.select(genotypeSpec, parentsNeeded, population,
					fitnessScore);
			logger.trace("Selected parents: {}", selectedParents);

			Genotype[] newPopulation = new Genotype[populationSize];
			int populationIndex = 0;
			while (selectedParents.isEmpty() == false) {
				final Genotype firstParent = selectedParents.remove(0);
				final Genotype secondParent = selectedParents.remove(0);

				final Chromosome[] chromosomes = new Chromosome[genotypeSpec.numChromosomes()];
				for (int chromosomeIndex = 0; chromosomeIndex < genotypeSpec.numChromosomes(); chromosomeIndex++) {

					final Chromosome firstChromosome = firstParent.getChromosome(chromosomeIndex);
					final Chromosome secondChromosome = secondParent.getChromosome(chromosomeIndex);

					final Chromosome combinedChromosome = chromosomeCombinators.get(chromosomeIndex)
							.combine(firstChromosome, secondChromosome);

					chromosomes[chromosomeIndex] = combinedChromosome;
					logger.trace("Combining {} with {} ---> {}", firstChromosome, secondChromosome, combinedChromosome);
				}

				Genotype offspring = new Genotype(chromosomes);

				for (final Mutator mutator : mutators) {
					offspring = mutator.mutate(offspring);
				}

				newPopulation[populationIndex] = offspring;
				populationIndex++;
			}

			final List<Genotype> survivors = survivorSelector.select(genotypeSpec, populationSize - populationIndex,
					population, fitnessScore);
			for (final Genotype genotype : survivors) {
				newPopulation[populationIndex] = genotype;
				populationIndex++;
			}

			logger.trace("[Generation {}] New population: {}", generation, Arrays.asList(newPopulation));
			population = newPopulation;
			generation++;

			for (int i = 0; i < populationSize; i++) {
				fitnessScore[i] = fitness.compute(population[i]);
				logger.trace("Score {} --> {}", fitnessScore[i], population[i]);
			}
		}

		return ImmutableEvolutionResult.of(genotypeSpec, generation, population, fitnessScore);
	}
}