package net.bmahe.genetics4j.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
import net.bmahe.genetics4j.core.replacement.ReplacementStrategyImplementor;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.ImmutableEvolutionResult;
import net.bmahe.genetics4j.core.termination.Termination;

public class EASystem<T extends Comparable<T>> {
	final static public Logger logger = LogManager.getLogger(EASystem.class);

	private final ExecutorService executorService;
	private final EAConfiguration<T> eaConfiguration;
	private final EAExecutionContext<T> eaExecutionContext;
	private final int populationSize;

	private final List<ChromosomeCombinator> chromosomeCombinators;
	private final ChromosomeFactoryProvider chromosomeFactoryProvider;

	private final ReplacementStrategyImplementor<T> replacementStrategyImplementor;

	private final List<Mutator> mutators;

	private final double offspringRatio;

	private Selector<T> parentSelector;

	public EASystem(final EAConfiguration<T> _eaConfiguration, final long _populationSize,
			final List<ChromosomeCombinator> _chromosomeCombinators, final double _offspringRatio,
			final Selector<T> _parentSelectionPolicyHandler, final List<Mutator> _mutators,
			final ReplacementStrategyImplementor<T> _replacementStrategyImplementor,
			final EAExecutionContext<T> _eaExecutionContext, final ExecutorService _executorService) {
		Validate.notNull(_eaConfiguration);
		Validate.isTrue(_populationSize > 0);
		Validate.notNull(_chromosomeCombinators);
		Validate.isTrue(_chromosomeCombinators.size() == _eaConfiguration.numChromosomes());
		Validate.inclusiveBetween(0.0, 1.0, _offspringRatio);
		Validate.notNull(_parentSelectionPolicyHandler);
		Validate.notNull(_replacementStrategyImplementor);
		Validate.notNull(_eaExecutionContext);
		Validate.notNull(_executorService);

		this.executorService = _executorService;
		this.eaConfiguration = _eaConfiguration;
		this.eaExecutionContext = _eaExecutionContext;
		this.populationSize = (int) _populationSize;
		this.chromosomeCombinators = _chromosomeCombinators;
		this.offspringRatio = _offspringRatio;
		this.mutators = _mutators;
		this.chromosomeFactoryProvider = _eaExecutionContext.chromosomeFactoryProvider();

		parentSelector = _parentSelectionPolicyHandler;

		this.replacementStrategyImplementor = _replacementStrategyImplementor;
	}

	public EAConfiguration<T> geteaConfiguration() {
		return eaConfiguration;
	}

	public long getPopulationSize() {
		return populationSize;
	}

	public Fitness<T> getFitness() {
		return eaConfiguration.fitness();
	}

	private List<Genotype> generateGenotype(final EAConfiguration<T> eaConfiguration, final int numPopulation) {
		Validate.notNull(eaConfiguration);
		Validate.isTrue(numPopulation > 0);

		final Optional<Supplier<Genotype>> populationGenerator = eaConfiguration.genotypeGenerator();

		final List<Genotype> population = new ArrayList<>();

		// Override
		if (populationGenerator.isPresent()) {
			final Supplier<Genotype> populationSupplier = populationGenerator.get();

			for (int i = 0; i < numPopulation; i++) {
				population.add(populationSupplier.get());
			}

		} else {

			final int numChromosomes = eaConfiguration.numChromosomes();
			final ChromosomeFactory<? extends Chromosome>[] chromosomeFactories = new ChromosomeFactory<?>[numChromosomes];
			for (int i = 0; i < numChromosomes; i++) {
				chromosomeFactories[i] = chromosomeFactoryProvider
						.provideChromosomeFactory(eaConfiguration.getChromosomeSpec(i));
			}

			for (int i = 0; i < numPopulation; i++) {

				final Chromosome[] chromosomes = new Chromosome[numChromosomes];
				for (int j = 0; j < numChromosomes; j++) {
					chromosomes[j] = chromosomeFactories[j].generate(eaConfiguration.getChromosomeSpec(j));
				}

				population.add(new Genotype(chromosomes));
			}
		}
		return population;
	}

	private List<T> evaluateGenotypes(final List<Genotype> population) {
		Validate.notNull(population);
		Validate.isTrue(population.size() > 0);

		final Fitness<T> fitness = eaConfiguration.fitness();
		final int numPartitions = eaExecutionContext.numberOfPartitions();
		final int partitionSize = (int) Math.ceil(population.size() / numPartitions);

		final List<CompletableFuture<TaskResult<T>>> tasks = new ArrayList<>();
		for (int i = 0; i < population.size();) {
			final int numSubPopulation = population.size() - i > partitionSize ? partitionSize : population.size() - i;
			final int partitionStart = i;
			final int partitionEnd = partitionStart + numSubPopulation;
			final List<Genotype> partition = population.subList(partitionStart, partitionEnd);
			final CompletableFuture<TaskResult<T>> asyncFitnessCompute = CompletableFuture.supplyAsync(() -> {
				final List<T> fitnessPartition = new ArrayList<>(numSubPopulation);
				for (int j = 0; j < partition.size(); j++) {
					final T fitnessIndividual = fitness.compute(partition.get(j));
					fitnessPartition.add(fitnessIndividual);
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

		final List<T> fitnessScores = new ArrayList<>(population.size());
		tasks.stream().map(t -> {
			try {
				return t.get();
			} catch (InterruptedException | ExecutionException e1) {
				throw new RuntimeException(e1);
			}
		}).sorted(Comparator.comparingInt(t -> t.from)).forEach(taskResult -> {
			fitnessScores.addAll(taskResult.fitness);
		});

		return fitnessScores;
	}

	public EvolutionResult<T> evolve() {
		final Termination<T> termination = eaConfiguration.termination();
		final GenotypeCombinator genotypeCombinator = eaConfiguration.genotypeCombinator();

		long generation = 0;
		List<Genotype> genotypes = generateGenotype(eaConfiguration, eaExecutionContext.populationSize());
		final List<T> fitnessScore = evaluateGenotypes(genotypes);

		Population<T> population = eaConfiguration.postEvaluationProcessor()
				.map(pep -> pep.apply(Population.of(genotypes, fitnessScore)))
				.orElseGet(() -> Population.of(genotypes, fitnessScore));

		while (termination.isDone(generation, population.getAllGenotypes(), population.getAllFitnesses()) == false) {

			for (final EvolutionListener<T> evolutionListener : eaExecutionContext.evolutionListeners()) {
				evolutionListener
						.onEvolution(generation, population.getAllGenotypes(), population.getAllFitnesses(), false);
			}

			final int childrenNeeded = (int) (populationSize * offspringRatio);
			final int parentsNeeded = (int) (childrenNeeded * 2);
			logger.trace("Will select {} parents", parentsNeeded);
			final List<Genotype> selectedParents = parentSelector
					.select(eaConfiguration, parentsNeeded, population.getAllGenotypes(), population.getAllFitnesses())
					.getAllGenotypes();
			logger.trace("Selected parents: {}", selectedParents);

			final List<Genotype> children = new ArrayList<>();
			while (selectedParents.isEmpty() == false) {
				final Genotype firstParent = selectedParents.remove(0);
				final Genotype secondParent = selectedParents.remove(0);

				final List<List<Chromosome>> chromosomes = new ArrayList<>();
				for (int chromosomeIndex = 0; chromosomeIndex < eaConfiguration.numChromosomes(); chromosomeIndex++) {

					final Chromosome firstChromosome = firstParent.getChromosome(chromosomeIndex);
					final Chromosome secondChromosome = secondParent.getChromosome(chromosomeIndex);

					final List<Chromosome> combinedChromosomes = chromosomeCombinators.get(chromosomeIndex)
							.combine(firstChromosome, secondChromosome);

					chromosomes.add(combinedChromosomes);
					logger.trace("Combining {} with {} ---> {}",
							firstChromosome,
							secondChromosome,
							combinedChromosomes);
				}

				final List<Genotype> offsprings = genotypeCombinator.combine(eaConfiguration, chromosomes);

				children.addAll(offsprings);
			}

			final List<Genotype> mutatedChildren = children.stream().map(child -> {
				Genotype mutatedChild = child;

				for (final Mutator mutator : mutators) {
					mutatedChild = mutator.mutate(mutatedChild);
				}

				return mutatedChild;
			}).collect(Collectors.toList());
			final List<T> offspringScores = evaluateGenotypes(mutatedChildren);

			final Population<T> childrenPopulation = eaConfiguration.postEvaluationProcessor()
					.map(pep -> pep.apply(Population.of(mutatedChildren, offspringScores)))
					.orElseGet(() -> Population.of(mutatedChildren, offspringScores));

			final int nextGenerationPopulationSize = eaExecutionContext.populationSize();
			final Population<T> newPopulation = replacementStrategyImplementor.select(eaConfiguration,
					nextGenerationPopulationSize,
					population.getAllGenotypes(),
					population.getAllFitnesses(),
					childrenPopulation.getAllGenotypes(),
					childrenPopulation.getAllFitnesses());

			if (newPopulation.size() < nextGenerationPopulationSize) {
				final List<Genotype> additionalIndividuals = generateGenotype(eaConfiguration,
						nextGenerationPopulationSize - newPopulation.size());
				logger.debug("Number of generated individuals: {}", additionalIndividuals.size());
				final List<T> additionalFitness = evaluateGenotypes(additionalIndividuals);

				newPopulation.addAll(Population.of(additionalIndividuals, additionalFitness));
			}

			logger.trace("[Generation {}] New population: {}", generation, Arrays.asList(newPopulation));
			population = newPopulation;
			generation++;
		}

		// isDone has returned true and we want to let the evolutionListeners run a last
		// time
		for (final EvolutionListener<T> evolutionListener : eaExecutionContext.evolutionListeners()) {
			evolutionListener.onEvolution(generation, population.getAllGenotypes(), population.getAllFitnesses(), true);
		}

		return ImmutableEvolutionResult
				.of(eaConfiguration, generation, population.getAllGenotypes(), population.getAllFitnesses());
	}

	private static class TaskResult<T> {
		public int from;
		public List<T> fitness;
	}
}