package net.bmahe.genetics4j.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactory;
import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.GenotypeCombinator;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListener;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.ImmutableEvolutionResult;
import net.bmahe.genetics4j.core.termination.Termination;

public class GeneticSystem<T extends Comparable<T>> {
	final static public Logger logger = LogManager.getLogger(GeneticSystem.class);

	private final ExecutorService executorService;
	private final GenotypeSpec<T> genotypeSpec;
	private final GeneticSystemDescriptor<T> geneticSystemDescriptor;
	private final int populationSize;

	private final List<ChromosomeCombinator> chromosomeCombinators;
	private final ChromosomeFactoryProvider chromosomeFactoryProvider;

	private final List<Mutator> mutators;

	private final double offspringRatio;

	private Selector<T> parentSelector;
	private Selector<T> survivorSelector;

	public GeneticSystem(final GenotypeSpec<T> _genotypeSpec, final long _populationSize,
			final List<ChromosomeCombinator> _chromosomeCombinators, final double _offspringRatio,
			final Selector<T> _parentSelectionPolicyHandler, final Selector<T> _survivorSelector,
			final List<Mutator> _mutators, final GeneticSystemDescriptor<T> _geneticSystemDescriptor,
			final ExecutorService _executorService) {
		Validate.notNull(_genotypeSpec);
		Validate.isTrue(_populationSize > 0);
		Validate.notNull(_chromosomeCombinators);
		Validate.isTrue(_chromosomeCombinators.size() == _genotypeSpec.numChromosomes());
		Validate.inclusiveBetween(0.0, 1.0, _offspringRatio);
		Validate.notNull(_parentSelectionPolicyHandler);
		Validate.notNull(_survivorSelector);
		Validate.notNull(_geneticSystemDescriptor);
		Validate.notNull(_executorService);

		this.executorService = _executorService;
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

	public GenotypeSpec<T> getGenotypeSpec() {
		return genotypeSpec;
	}

	public long getPopulationSize() {
		return populationSize;
	}

	public Fitness<T> getFitness() {
		return genotypeSpec.fitness();
	}

	private Genotype[] generatePopulation(final GenotypeSpec<T> genotypeSpec, final int numPopulation) {
		Validate.notNull(genotypeSpec);
		Validate.isTrue(numPopulation > 0);

		final Optional<Supplier<Genotype>> populationGenerator = genotypeSpec.populationGenerator();

		final Genotype[] population = new Genotype[numPopulation];

		// Override
		if (populationGenerator.isPresent()) {
			final Supplier<Genotype> populationSupplier = populationGenerator.get();

			for (int i = 0; i < numPopulation; i++) {
				population[i] = populationSupplier.get();
			}

		} else {

			final int numChromosomes = genotypeSpec.numChromosomes();
			final ChromosomeFactory<? extends Chromosome>[] chromosomeFactories = new ChromosomeFactory<?>[numChromosomes];
			for (int i = 0; i < numChromosomes; i++) {
				chromosomeFactories[i] = chromosomeFactoryProvider
						.provideChromosomeFactory(genotypeSpec.getChromosomeSpec(i));
			}

			for (int i = 0; i < numPopulation; i++) {

				final Chromosome[] chromosomes = new Chromosome[numChromosomes];
				for (int j = 0; j < numChromosomes; j++) {
					chromosomes[j] = chromosomeFactories[j].generate(genotypeSpec.getChromosomeSpec(j));
				}

				population[i] = new Genotype(chromosomes);
			}
		}
		return population;
	}

	public EvolutionResult<T> evolve() {
		final Termination<T> termination = genotypeSpec.termination();

		final Fitness<T> fitness = genotypeSpec.fitness();

		long generation = 0;
		Genotype[] population = generatePopulation(genotypeSpec, populationSize);

		final ArrayList<T> fitnessScore = new ArrayList<>(populationSize);
		for (int i = 0; i < populationSize; i++) {
			fitnessScore.add(fitness.compute(population[i]));
		}

		while (termination.isDone(generation, population, fitnessScore) == false) {

			for (final EvolutionListener<T> evolutionListener : geneticSystemDescriptor.evolutionListeners()) {
				evolutionListener.onEvolution(generation, population, fitnessScore);
			}

			final int childrenNeeded = (int) (populationSize * offspringRatio);
			final int parentsNeeded = (int) (childrenNeeded * 2);
			logger.trace("Will select {} parents", parentsNeeded);
			final List<Genotype> selectedParents = parentSelector
					.select(genotypeSpec, parentsNeeded, population, fitnessScore);
			logger.trace("Selected parents: {}", selectedParents);

			final List<Genotype> children = new ArrayList<>();
			while (selectedParents.isEmpty() == false) {
				final Genotype firstParent = selectedParents.remove(0);
				final Genotype secondParent = selectedParents.remove(0);

				final List<List<Chromosome>> chromosomes = new ArrayList<>();
				for (int chromosomeIndex = 0; chromosomeIndex < genotypeSpec.numChromosomes(); chromosomeIndex++) {

					final Chromosome firstChromosome = firstParent.getChromosome(chromosomeIndex);
					final Chromosome secondChromosome = secondParent.getChromosome(chromosomeIndex);

					final List<Chromosome> combinedChromosomes = chromosomeCombinators.get(chromosomeIndex)
							.combine(firstChromosome, secondChromosome);
//XXXX
					chromosomes.add(combinedChromosomes);
					logger.trace("Combining {} with {} ---> {}",
							firstChromosome,
							secondChromosome,
							combinedChromosomes);
				}

				final GenotypeCombinator genotypeCombinator = genotypeSpec.genotypeCombinator();
				final List<Genotype> offsprings = genotypeCombinator.combine(genotypeSpec, chromosomes);

				children.addAll(offsprings);

			}

			final List<Genotype> selectedChildren = children.size() <= childrenNeeded
					? children.stream().limit(childrenNeeded).collect(Collectors.toList())
					: geneticSystemDescriptor.random()
							.ints(0, children.size())
							.distinct()
							.limit(childrenNeeded)
							.boxed()
							.map(idx -> children.get(idx))
							.collect(Collectors.toList());

			final List<Genotype> mutatedChildren = selectedChildren.stream().map(child -> {
				Genotype mutatedChild = child;

				for (final Mutator mutator : mutators) {
					mutatedChild = mutator.mutate(mutatedChild);
				}

				return mutatedChild;
			}).collect(Collectors.toList());

			// TODO make a List<Genotype>
			Genotype[] newPopulation = new Genotype[populationSize];
			int populationIndex = 0;

			logger.debug("Number of children: {}", mutatedChildren.size());
			for (final Genotype mutatedChild : mutatedChildren) {

				newPopulation[populationIndex] = mutatedChild;
				populationIndex++;
			}

			final List<Genotype> survivors = survivorSelector
					.select(genotypeSpec, populationSize - childrenNeeded, population, fitnessScore);
			logger.debug("Number of survivors: {}", survivors.size());
			for (final Genotype genotype : survivors) {
				newPopulation[populationIndex] = genotype;
				populationIndex++;
			}

			if (populationIndex < populationSize) {
				final Genotype[] additionalIndividuals = generatePopulation(genotypeSpec,
						populationSize - populationIndex);
				logger.debug("Number of generated individuals: {}", additionalIndividuals.length);

				for (final Genotype genotype : additionalIndividuals) {
					newPopulation[populationIndex] = genotype;
					populationIndex++;
				}
			}

			logger.trace("[Generation {}] New population: {}", generation, Arrays.asList(newPopulation));
			population = newPopulation;
			generation++;

			final int numPartitions = geneticSystemDescriptor.numberOfPartitions();
			final int partitionSize = (int) Math.ceil(populationSize / numPartitions);
			final List<CompletableFuture<TaskResult<T>>> tasks = new ArrayList<>();
			for (int i = 0; i < populationSize;) {
				final int numSubPopulation = populationSize - i > partitionSize ? partitionSize : populationSize - i;
				final int partitionStart = i;
				final int partitionEnd = partitionStart + numSubPopulation;
				final Genotype[] partition = Arrays.copyOfRange(population, partitionStart, partitionEnd);
				final CompletableFuture<TaskResult<T>> asyncFitnessCompute = CompletableFuture.supplyAsync(() -> {
					final List<T> fitnessPartition = new ArrayList<>(numSubPopulation);
					for (int j = 0; j < partition.length; j++) {
						fitnessPartition.add(fitness.compute(partition[j]));
					}
					final TaskResult<T> taskResult = new TaskResult<>();
					taskResult.from = partitionStart;
					taskResult.fitness = fitnessPartition;
					return taskResult;
				}, executorService);
				tasks.add(asyncFitnessCompute);

				i += numSubPopulation;
			}

			CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()]));
			for (final CompletableFuture<TaskResult<T>> taskResultCF : tasks) {
				try {
					final TaskResult<T> taskResult = taskResultCF.get();
					final int offset = taskResult.from;
					for (int i = 0; i < taskResult.fitness.size(); i++) {
						fitnessScore.set(i + offset, taskResult.fitness.get(i));
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		// isDone has returned true and we want to let the evolutionListeners run a last
		// time
		for (final EvolutionListener<T> evolutionListener : geneticSystemDescriptor.evolutionListeners()) {
			evolutionListener.onEvolution(generation, population, fitnessScore);
		}

		return ImmutableEvolutionResult.of(genotypeSpec, generation, population, fitnessScore);
	}

	private static class TaskResult<T> {
		public int from;
		public List<T> fitness;
	}
}