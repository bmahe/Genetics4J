package net.bmahe.genetics4j.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.GenotypeCombinator;
import net.bmahe.genetics4j.core.evaluation.FitnessEvaluator;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListener;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.replacement.ReplacementStrategyImplementor;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.ImmutableEvolutionResult;
import net.bmahe.genetics4j.core.termination.Termination;
import net.bmahe.genetics4j.core.util.GenotypeGenerator;

/**
 * Main class used to manage and execute the evolution process
 *
 * @param <T>
 */
public class EASystem<T extends Comparable<T>> {
	final static public Logger logger = LogManager.getLogger(EASystem.class);

	private final GenotypeGenerator<T> genotypeGenerator;
	private final FitnessEvaluator<T> fitnessEvaluator;
	private final AbstractEAConfiguration<T> eaConfiguration;
	private final AbstractEAExecutionContext<T> eaExecutionContext;
	private final int populationSize;

	private final List<ChromosomeCombinator<T>> chromosomeCombinators;
	private final ChromosomeFactoryProvider chromosomeFactoryProvider;

	private final ReplacementStrategyImplementor<T> replacementStrategyImplementor;

	private final List<Mutator> mutators;

	private final double offspringRatio;

	private Selector<T> parentSelector;

	public EASystem(final AbstractEAConfiguration<T> _eaConfiguration, final long _populationSize,
			final List<ChromosomeCombinator<T>> _chromosomeCombinators, final double _offspringRatio,
			final Selector<T> _parentSelectionPolicyHandler, final List<Mutator> _mutators,
			final ReplacementStrategyImplementor<T> _replacementStrategyImplementor,
			final AbstractEAExecutionContext<T> _eaExecutionContext, final FitnessEvaluator<T> _fitnessEvaluator) {
		Objects.requireNonNull(_eaConfiguration);
		Validate.isTrue(_populationSize > 0);
		Objects.requireNonNull(_chromosomeCombinators);
		Validate.isTrue(_chromosomeCombinators.size() == _eaConfiguration.numChromosomes());
		Validate.inclusiveBetween(0.0, 1.0, _offspringRatio);
		Objects.requireNonNull(_parentSelectionPolicyHandler);
		Objects.requireNonNull(_replacementStrategyImplementor);
		Objects.requireNonNull(_eaExecutionContext);
		Objects.requireNonNull(_fitnessEvaluator);

		this.eaConfiguration = _eaConfiguration;
		this.eaExecutionContext = _eaExecutionContext;
		this.populationSize = (int) _populationSize;
		this.chromosomeCombinators = _chromosomeCombinators;
		this.offspringRatio = _offspringRatio;
		this.mutators = _mutators;
		this.chromosomeFactoryProvider = _eaExecutionContext.chromosomeFactoryProvider();
		this.fitnessEvaluator = _fitnessEvaluator;

		parentSelector = _parentSelectionPolicyHandler;

		this.replacementStrategyImplementor = _replacementStrategyImplementor;
		this.genotypeGenerator = new GenotypeGenerator<>(chromosomeFactoryProvider, eaConfiguration);
	}

	private List<T> evaluate(final long generation, final List<Genotype> population) {
		Validate.isTrue(generation >= 0);
		Objects.requireNonNull(population);
		Validate.isTrue(population.size() > 0);

		logger.debug("Evaluating population of size {}", population.size());
		final List<T> fitnesses = fitnessEvaluator.evaluate(generation, population);

		logger.debug("Done evaluating population of size {}", population.size());
		return fitnesses;
	}

	private List<Genotype> initializePopulation() {
		final int initialPopulationSize = eaExecutionContext.populationSize();
		logger.info("Generating initial population of {} individuals", initialPopulationSize);

		final List<Genotype> genotypes = new ArrayList<>(initialPopulationSize);

		final var seedPopulation = eaConfiguration.seedPopulation();
		if (CollectionUtils.isNotEmpty(seedPopulation)) {
			genotypes.addAll(seedPopulation);
		}
		if (genotypes.size() < initialPopulationSize) {
			final var missingInitialIndividualCount = initialPopulationSize - genotypes.size();
			logger.info(
					"{} seed individual(s) added and generating {} individuals to reach the target of {} initial population size",
					genotypes.size(),
					missingInitialIndividualCount,
					initialPopulationSize);

			final var extraIndividuals = genotypeGenerator.generateGenotypes(missingInitialIndividualCount);
			genotypes.addAll(extraIndividuals);
		}

		return genotypes;
	}

	private List<Genotype> mutateGenotypes(final List<Genotype> genotypes) {
		Objects.requireNonNull(genotypes);

		return genotypes.stream()
				.map(child -> {
					Genotype mutatedChild = child;

					for (final Mutator mutator : mutators) {
						mutatedChild = mutator.mutate(mutatedChild);
					}

					return mutatedChild;
				})
				.toList();
	}

	private List<Genotype> combineParents(final Population<T> parents, final GenotypeCombinator genotypeCombinator) {
		Objects.requireNonNull(parents);
		Objects.requireNonNull(genotypeCombinator);

		final List<Genotype> children = new ArrayList<>();
		int parentIndex = 0;
		while (parentIndex + 1 < parents.size()) {
			final Genotype firstParent = parents.getGenotype(parentIndex);
			final T firstParentFitness = parents.getFitness(parentIndex);

			final Genotype secondParent = parents.getGenotype(parentIndex + 1);
			final T secondParentFitness = parents.getFitness(parentIndex + 1);

			final List<List<Chromosome>> chromosomes = new ArrayList<>();
			for (int chromosomeIndex = 0; chromosomeIndex < eaConfiguration.numChromosomes(); chromosomeIndex++) {

				final Chromosome firstChromosome = firstParent.getChromosome(chromosomeIndex);
				final Chromosome secondChromosome = secondParent.getChromosome(chromosomeIndex);

				final List<Chromosome> combinedChromosomes = chromosomeCombinators.get(chromosomeIndex)
						.combine(eaConfiguration, firstChromosome, firstParentFitness, secondChromosome, secondParentFitness);

				chromosomes.add(combinedChromosomes);
				logger.trace("Combining {} with {} ---> {}", firstChromosome, secondChromosome, combinedChromosomes);
			}

			final List<Genotype> offsprings = genotypeCombinator.combine(eaConfiguration, chromosomes);

			children.addAll(offsprings);
			parentIndex += 2;
		}

		return children;
	}

	/**
	 * Create offsprings without mutation
	 *
	 * @param population
	 * @param offspringsNeeded
	 * @return
	 */
	private List<Genotype> createBasicOffsprings(final Population<T> population, final int offspringsNeeded) {
		Objects.requireNonNull(population);
		Validate.isTrue(offspringsNeeded > 0);

		final GenotypeCombinator genotypeCombinator = eaConfiguration.genotypeCombinator();

		final int parentsNeeded = (int) (offspringsNeeded * 2);
		logger.info("Selecting {} parents as we expect to generate {} offsprings", parentsNeeded, offspringsNeeded);
		final Population<T> selectedParents = parentSelector
				.select(eaConfiguration, parentsNeeded, population.getAllGenotypes(), population.getAllFitnesses());
		logger.trace("Selected parents: {}", selectedParents);
		Validate.isTrue(selectedParents.size() % 2 == 0);

		logger.info("Combining parents into offsprings");
		final List<Genotype> offsprings = combineParents(selectedParents, genotypeCombinator);

		return offsprings;
	}

	public AbstractEAConfiguration<T> getEAConfiguration() {
		return eaConfiguration;
	}

	public long getPopulationSize() {
		return populationSize;
	}

	public List<Genotype> createOffsprings(final Population<T> population, final int offspringsNeeded) {
		Objects.requireNonNull(population);
		Validate.isTrue(offspringsNeeded > 0);

		final List<Genotype> offpsrings = createBasicOffsprings(population, offspringsNeeded);
		logger.info("Generated {} offsprings", offpsrings.size());

		logger.info("Mutating offsprigns");
		final List<Genotype> mutatedOffsprings = mutateGenotypes(offpsrings);

		return mutatedOffsprings;
	}

	/**
	 * Triggers the evolutionary process
	 * 
	 * @return
	 */
	public EvolutionResult<T> evolve() {
		final Termination<T> termination = eaConfiguration.termination();

		logger.info("Starting evolution");

		fitnessEvaluator.preEvaluation();

		long generation = 0;
		final List<Genotype> genotypes = initializePopulation();

		logger.info("Evaluating initial population");
		final List<T> fitnessScore = evaluate(generation, genotypes);

		Population<T> population = eaConfiguration.postEvaluationProcessor()
				.map(pep -> pep.apply(Population.of(genotypes, fitnessScore)))
				.orElseGet(() -> Population.of(genotypes, fitnessScore));

		while (termination
				.isDone(eaConfiguration, generation, population.getAllGenotypes(), population.getAllFitnesses()) == false) {
			logger.info("Going through evolution of generation {}", generation);

			for (final EvolutionListener<T> evolutionListener : eaExecutionContext.evolutionListeners()) {
				evolutionListener
						.onEvolution(generation, population.getAllGenotypes(), population.getAllFitnesses(), false);
			}

			final int offspringsNeeded = (int) (populationSize * offspringRatio);
			final List<Genotype> offsprings = createOffsprings(population, offspringsNeeded);

			logger.info("Evaluating offsprings");
			final List<T> offspringScores = evaluate(generation, offsprings);

			final Population<T> childrenPopulation = eaConfiguration.postEvaluationProcessor()
					.map(pep -> pep.apply(Population.of(offsprings, offspringScores)))
					.orElseGet(() -> Population.of(offsprings, offspringScores));

			logger.info("Executing replacement strategy");
			final int nextGenerationPopulationSize = eaExecutionContext.populationSize();
			final Population<T> newPopulation = replacementStrategyImplementor.select(eaConfiguration,
					nextGenerationPopulationSize,
					population.getAllGenotypes(),
					population.getAllFitnesses(),
					childrenPopulation.getAllGenotypes(),
					childrenPopulation.getAllFitnesses());

			if (newPopulation.size() < nextGenerationPopulationSize) {
				logger.info("New population only has {} members. Generating more individuals", newPopulation.size());
				final List<Genotype> additionalIndividuals = genotypeGenerator
						.generateGenotypes(nextGenerationPopulationSize - newPopulation.size());
				logger.debug("Number of generated individuals: {}", additionalIndividuals.size());

				if (additionalIndividuals.size() > 0) {

					final List<T> additionalFitness = evaluate(generation, additionalIndividuals);
					newPopulation.addAll(Population.of(additionalIndividuals, additionalFitness));
				}
			}

			if (logger.isTraceEnabled()) {
				logger.trace("[Generation {}] New population: {}", generation, Arrays.asList(newPopulation));
			}
			population = newPopulation;
			generation++;
		}

		logger.info("Evolution has terminated");

		// isDone has returned true and we want to let the evolutionListeners run a last
		// time
		for (final EvolutionListener<T> evolutionListener : eaExecutionContext.evolutionListeners()) {
			evolutionListener.onEvolution(generation, population.getAllGenotypes(), population.getAllFitnesses(), true);
		}

		fitnessEvaluator.postEvaluation();

		return ImmutableEvolutionResult
				.of(eaConfiguration, generation, population.getAllGenotypes(), population.getAllFitnesses());
	}

	public List<T> evaluateOnce(final long generation, final List<Genotype> genotypes) {
		Validate.isTrue(generation >= 0);
		Objects.requireNonNull(genotypes);
		Validate.isTrue(genotypes.size() > 0);

		fitnessEvaluator.preEvaluation();
		final var fitness = evaluate(generation, genotypes);

		final var population = Population.of(genotypes, fitness);
		for (final EvolutionListener<T> evolutionListener : eaExecutionContext.evolutionListeners()) {
			evolutionListener.onEvolution(generation, population.getAllGenotypes(), population.getAllFitnesses(), true);
		}

		fitnessEvaluator.postEvaluation();

		return fitness;
	}
}