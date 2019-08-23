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
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.ImmutableEvolutionResult;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public class GeneticSystem {
	final static public Logger logger = LogManager.getLogger(GeneticSystem.class);

	private final GenotypeSpec genotypeSpec;
	private final GeneticSystemDescriptor geneticSystemDescriptor;
	private final int populationSize;

	private final Fitness fitness;

	private final List<ChromosomeCombinator> chromosomeCombinators;
	private final List<MutationPolicyHandler> mutationPolicyHandlers;
	private final List<List<ChromosomeMutationHandler<? extends Chromosome>>> allChromosomeMutationHandlers;
	private final ChromosomeFactoryProvider chromosomeFactoryProvider;

	private final double offspringRatio;

	private SelectionPolicyHandler parentSelector;
	private SelectionPolicyHandler survivorSelector;

	public GeneticSystem(final GenotypeSpec _genotypeSpec, final Fitness _fitness, final long _populationSize,
			final List<ChromosomeCombinator> _chromosomeCombinators, final double _offspringRatio,
			final SelectionPolicyHandler _parentSelectionPolicyHandler, final SelectionPolicyHandler _survivorSelector,
			final List<MutationPolicyHandler> _mutationPolicyHandlers,
			final List<List<ChromosomeMutationHandler<? extends Chromosome>>> _chromosomeMutationHandlers,
			final GeneticSystemDescriptor _geneticSystemDescriptor) {
		Validate.notNull(_genotypeSpec);
		Validate.notNull(_fitness);
		Validate.isTrue(_populationSize > 0);
		Validate.notNull(_chromosomeCombinators);
		Validate.isTrue(_chromosomeCombinators.size() == _genotypeSpec.numChromosomes());
		Validate.inclusiveBetween(0.0, 1.0, _offspringRatio);
		Validate.notNull(_parentSelectionPolicyHandler);
		Validate.notNull(_survivorSelector);
		Validate.notNull(_geneticSystemDescriptor);

		this.genotypeSpec = _genotypeSpec;
		this.geneticSystemDescriptor = _geneticSystemDescriptor;
		this.fitness = _fitness;
		this.populationSize = (int) _populationSize;
		this.chromosomeCombinators = _chromosomeCombinators;
		this.offspringRatio = _offspringRatio;
		this.mutationPolicyHandlers = _mutationPolicyHandlers;
		this.allChromosomeMutationHandlers = _chromosomeMutationHandlers;
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
		return fitness;
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
			final List<Genotype> selectedParents = parentSelector.select(genotypeSpec,
					genotypeSpec.parentSelectionPolicy(), parentsNeeded, population, fitnessScore);
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
							.combine(genotypeSpec.combinationPolicy(), firstChromosome, secondChromosome);

					chromosomes[chromosomeIndex] = combinedChromosome;
					logger.trace("Combining {} with {} ---> {}", firstChromosome, secondChromosome, combinedChromosome);
				}

				Genotype offspring = new Genotype(chromosomes);
				for (int i = 0; i < genotypeSpec.mutationPolicies().size(); i++) {
					final MutationPolicy mutationPolicy = genotypeSpec.mutationPolicies().get(i);
					final MutationPolicyHandler mutationPolicyHandler = mutationPolicyHandlers.get(i);
					final List<ChromosomeMutationHandler<? extends Chromosome>> chromosomeMutationHandlers = allChromosomeMutationHandlers
							.get(i);

					offspring = mutationPolicyHandler.mutate(mutationPolicy, offspring, chromosomeMutationHandlers);
				}
				newPopulation[populationIndex] = offspring;
				populationIndex++;
			}

			final List<Genotype> survivors = survivorSelector.select(genotypeSpec, genotypeSpec.survivorSelectionPolicy(),
					populationSize - populationIndex, population, fitnessScore);
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