package net.bmahe.genetics4j.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

/**
 * Main class used to manage and execute the evolution process
 *
 * @param <T>
 */
public class EASystem<T extends Comparable<T>> {
	final static public Logger logger = LogManager.getLogger(EASystem.class);

	private final FitnessEvaluator<T> fitnessEvaluator;
	private final AbstractEAConfiguration<T> eaConfiguration;
	private final AbstractEAExecutionContext<T> eaExecutionContext;
	private final int populationSize;

	private final List<ChromosomeCombinator> chromosomeCombinators;
	private final ChromosomeFactoryProvider chromosomeFactoryProvider;

	private final ReplacementStrategyImplementor<T> replacementStrategyImplementor;

	private final List<Mutator> mutators;

	private final double offspringRatio;

	private Selector<T> parentSelector;

	public EASystem(final AbstractEAConfiguration<T> _eaConfiguration, final long _populationSize,
			final List<ChromosomeCombinator> _chromosomeCombinators, final double _offspringRatio,
			final Selector<T> _parentSelectionPolicyHandler, final List<Mutator> _mutators,
			final ReplacementStrategyImplementor<T> _replacementStrategyImplementor,
			final AbstractEAExecutionContext<T> _eaExecutionContext, final FitnessEvaluator<T> _fitnessEvaluator) {
		Validate.notNull(_eaConfiguration);
		Validate.isTrue(_populationSize > 0);
		Validate.notNull(_chromosomeCombinators);
		Validate.isTrue(_chromosomeCombinators.size() == _eaConfiguration.numChromosomes());
		Validate.inclusiveBetween(0.0, 1.0, _offspringRatio);
		Validate.notNull(_parentSelectionPolicyHandler);
		Validate.notNull(_replacementStrategyImplementor);
		Validate.notNull(_eaExecutionContext);
		Validate.notNull(_fitnessEvaluator);

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
	}

	private List<Genotype> generateGenotype(final AbstractEAConfiguration<T> eaConfiguration, final int numPopulation) {
		Validate.notNull(eaConfiguration);
		Validate.isTrue(numPopulation > 0);

		logger.info("Generating {} individuals", numPopulation);

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

	private List<T> evaluate(final long generation, final List<Genotype> population) {
		Validate.isTrue(generation >= 0);
		Validate.notNull(population);
		Validate.isTrue(population.size() > 0);

		logger.debug("Evaluating population of size {}", population.size());
		final List<T> fitnesses = fitnessEvaluator.evaluate(generation, population);

		logger.debug("Done evaluating population of size {}", population.size());
		return fitnesses;
	}

	public AbstractEAConfiguration<T> getEAConfiguration() {
		return eaConfiguration;
	}

	public long getPopulationSize() {
		return populationSize;
	}

	/**
	 * Triggers the evolutionary process
	 * 
	 * @return
	 */
	public EvolutionResult<T> evolve() {
		final Termination<T> termination = eaConfiguration.termination();
		final GenotypeCombinator genotypeCombinator = eaConfiguration.genotypeCombinator();

		logger.info("Starting evolution");

		fitnessEvaluator.preEvaluation();

		final int initialPopulationSize = eaExecutionContext.populationSize();
		logger.info("Generating initial population of {} individuals", initialPopulationSize);

		long generation = 0;
		final List<Genotype> genotypes = generateGenotype(eaConfiguration, initialPopulationSize);

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

			final int childrenNeeded = (int) (populationSize * offspringRatio);
			final int parentsNeeded = (int) (childrenNeeded * 2);
			logger.info("Selecting {} parents as we expect to generate {} children", parentsNeeded, childrenNeeded);
			final List<Genotype> selectedParents = parentSelector
					.select(eaConfiguration, parentsNeeded, population.getAllGenotypes(), population.getAllFitnesses())
					.getAllGenotypes();
			logger.trace("Selected parents: {}", selectedParents);

			logger.info("Combining parents into offsprings");
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
					logger.trace("Combining {} with {} ---> {}", firstChromosome, secondChromosome, combinedChromosomes);
				}

				final List<Genotype> offsprings = genotypeCombinator.combine(eaConfiguration, chromosomes);

				children.addAll(offsprings);
			}
			logger.info("Generated {} offsprings", children.size());

			logger.info("Mutating children");
			final List<Genotype> mutatedChildren = children.stream()
					.map(child -> {
						Genotype mutatedChild = child;

						for (final Mutator mutator : mutators) {
							mutatedChild = mutator.mutate(mutatedChild);
						}

						return mutatedChild;
					})
					.collect(Collectors.toList());

			logger.info("Evaluating offsprings");
			final List<T> offspringScores = evaluate(generation, mutatedChildren);

			final Population<T> childrenPopulation = eaConfiguration.postEvaluationProcessor()
					.map(pep -> pep.apply(Population.of(mutatedChildren, offspringScores)))
					.orElseGet(() -> Population.of(mutatedChildren, offspringScores));

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
				final List<Genotype> additionalIndividuals = generateGenotype(eaConfiguration,
						nextGenerationPopulationSize - newPopulation.size());
				logger.debug("Number of generated individuals: {}", additionalIndividuals.size());

				if (additionalIndividuals.size() > 0) {

					final List<T> additionalFitness = evaluate(generation, additionalIndividuals);
					newPopulation.addAll(Population.of(additionalIndividuals, additionalFitness));
				}
			}

			logger.trace("[Generation {}] New population: {}", generation, Arrays.asList(newPopulation));
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

	public List<T> evaluateOnce(final long generation, final List<Genotype> population) {
		Validate.isTrue(generation >= 0);
		Validate.notNull(population);
		Validate.isTrue(population.size() > 0);

		fitnessEvaluator.preEvaluation();
		final var fitness = evaluate(generation, population);
		fitnessEvaluator.postEvaluation();

		return fitness;
	}
}