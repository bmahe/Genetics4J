package net.bmahe.genetics4j.neat.combination;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.combination.parentcompare.ChosenOtherChromosome;
import net.bmahe.genetics4j.neat.combination.parentcompare.ParentComparisonHandler;
import net.bmahe.genetics4j.neat.spec.combination.NeatCombination;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.ParentComparisonPolicy;

/**
 * Implements genetic crossover for NEAT (NeuroEvolution of Augmenting Topologies) neural network chromosomes.
 * 
 * <p>NeatChromosomeCombinator performs innovation-number-based genetic recombination between two neural
 * network chromosomes, creating offspring that inherit network topology and connection weights from both
 * parents while preserving the historical tracking essential to the NEAT algorithm.
 * 
 * <p>NEAT crossover algorithm:
 * <ol>
 * <li><strong>Parent comparison</strong>: Determine which parent is "fitter" using comparison policy</li>
 * <li><strong>Gene alignment</strong>: Match connections by innovation number between parents</li>
 * <li><strong>Matching genes</strong>: Randomly inherit from either parent (biased by inheritance threshold)</li>
 * <li><strong>Disjoint genes</strong>: Inherit from fitter parent when innovation ranges overlap</li>
 * <li><strong>Excess genes</strong>: Inherit from fitter parent beyond other parent's range</li>
 * <li><strong>Gene re-enabling</strong>: Potentially re-enable disabled genes based on threshold</li>
 * </ol>
 * 
 * <p>Key genetic operations:
 * <ul>
 * <li><strong>Innovation alignment</strong>: Uses innovation numbers to match corresponding genes</li>
 * <li><strong>Fitness-biased inheritance</strong>: Favors genes from fitter parent based on inheritance threshold</li>
 * <li><strong>Gene state management</strong>: Handles enabled/disabled connection states during crossover</li>
 * <li><strong>Topology preservation</strong>: Ensures offspring have valid network topology</li>
 * </ul>
 * 
 * <p>Gene classification:
 * <ul>
 * <li><strong>Matching genes</strong>: Same innovation number in both parents, inherit randomly</li>
 * <li><strong>Disjoint genes</strong>: Innovation number exists in one parent within other's range</li>
 * <li><strong>Excess genes</strong>: Innovation number beyond other parent's highest innovation</li>
 * <li><strong>Disabled genes</strong>: May be re-enabled if other parent has enabled version</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create NEAT chromosome combinator
 * RandomGenerator randomGen = RandomGenerator.getDefault();
 * NeatCombination policy = NeatCombination.builder()
 *     .inheritanceThresold(0.7)  // 70% bias toward fitter parent
 *     .reenableGeneInheritanceThresold(0.25)  // 25% gene re-enabling chance
 *     .parentComparisonPolicy(FitnessComparison.build())
 *     .build();
 * 
 * ParentComparisonHandler comparisonHandler = new FitnessComparisonHandler();
 * NeatChromosomeCombinator<Double> combinator = new NeatChromosomeCombinator<>(
 *     randomGen, policy, comparisonHandler
 * );
 * 
 * // Perform crossover
 * NeatChromosome parent1 = // ... first parent
 * NeatChromosome parent2 = // ... second parent
 * Double fitness1 = 0.85;
 * Double fitness2 = 0.72;
 * 
 * List<Chromosome> offspring = combinator.combine(
 *     eaConfiguration, parent1, fitness1, parent2, fitness2
 * );
 * NeatChromosome child = (NeatChromosome) offspring.get(0);
 * }</pre>
 * 
 * <p>Inheritance threshold effects:
 * <ul>
 * <li><strong>0.5</strong>: Unbiased inheritance, equal probability from both parents</li>
 * <li><strong>&gt; 0.5</strong>: Bias toward fitter parent, promotes convergence</li>
 * <li><strong>&lt; 0.5</strong>: Bias toward less fit parent, increases diversity</li>
 * <li><strong>1.0</strong>: Always inherit from fitter parent (when fitness differs)</li>
 * </ul>
 * 
 * <p>Gene re-enabling mechanism:
 * <ul>
 * <li><strong>Preservation</strong>: Disabled genes maintain connection topology information</li>
 * <li><strong>Re-activation</strong>: Chance to re-enable genes that are enabled in other parent</li>
 * <li><strong>Exploration</strong>: Allows rediscovery of previously disabled connection patterns</li>
 * <li><strong>Genetic diversity</strong>: Prevents permanent loss of structural information</li>
 * </ul>
 * 
 * <p>Duplicate connection prevention:
 * <ul>
 * <li><strong>Links cache</strong>: Tracks already included connections to prevent duplicates</li>
 * <li><strong>Topology validation</strong>: Ensures each connection appears at most once</li>
 * <li><strong>Cache efficiency</strong>: O(1) lookup for connection existence checking</li>
 * <li><strong>Memory management</strong>: Cache cleared after each crossover operation</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Linear time complexity</strong>: O(n + m) where n, m are parent connection counts</li>
 * <li><strong>Innovation sorting</strong>: Leverages pre-sorted connection lists for efficiency</li>
 * <li><strong>Memory efficiency</strong>: Minimal allocation during crossover</li>
 * <li><strong>Cache optimization</strong>: Efficient duplicate detection and prevention</li>
 * </ul>
 * 
 * @param <T> the fitness value type (typically Double)
 * @see NeatCombination
 * @see ParentComparisonHandler
 * @see NeatChromosome
 * @see ChromosomeCombinator
 */
public class NeatChromosomeCombinator<T extends Comparable<T>> implements ChromosomeCombinator<T> {
	public static final Logger logger = LogManager.getLogger(NeatChromosomeCombinator.class);

	private final RandomGenerator randomGenerator;
	private final NeatCombination neatCombination;
	private final ParentComparisonHandler parentComparisonHandler;

	/**
	 * Checks whether a connection already exists in the links cache.
	 * 
	 * <p>The links cache prevents duplicate connections in the offspring by tracking
	 * all connections that have already been added. This ensures each connection
	 * appears at most once in the resulting chromosome.
	 * 
	 * @param linksCache cache mapping from-node indices to sets of to-node indices
	 * @param connection connection to check for existence
	 * @return true if connection already exists in cache, false otherwise
	 * @throws IllegalArgumentException if linksCache or connection is null
	 */
	private boolean linksCacheContainsConnection(final Map<Integer, Set<Integer>> linksCache,
			final Connection connection) {
		Validate.notNull(linksCache);
		Validate.notNull(connection);

		final int fromNodeIndex = connection.fromNodeIndex();
		final int toNodeIndex = connection.toNodeIndex();

		return linksCache.containsKey(fromNodeIndex) == true && linksCache.get(fromNodeIndex)
				.contains(toNodeIndex) == true;
	}

	/**
	 * Adds a connection to the links cache to prevent future duplicates.
	 * 
	 * <p>This method records that a connection from the specified source to target
	 * node has been added to the offspring, preventing the same connection from
	 * being added again during the crossover process.
	 * 
	 * @param linksCache cache mapping from-node indices to sets of to-node indices
	 * @param connection connection to add to the cache
	 * @throws IllegalArgumentException if linksCache or connection is null
	 */
	private void insertInlinksCache(final Map<Integer, Set<Integer>> linksCache, final Connection connection) {
		Validate.notNull(linksCache);
		Validate.notNull(connection);

		final int fromNodeIndex = connection.fromNodeIndex();
		final int toNodeIndex = connection.toNodeIndex();

		linksCache.computeIfAbsent(fromNodeIndex, k -> new HashSet<>())
				.add(toNodeIndex);
	}

	/**
	 * Determines whether a disabled gene should be re-enabled during crossover.
	 * 
	 * <p>If the chosen parent has a disabled connection but the other parent has the
	 * same connection enabled, there is a configurable chance to re-enable the
	 * connection in the offspring. This mechanism prevents permanent loss of
	 * potentially useful connections.
	 * 
	 * @param chosenParent the connection selected for inheritance
	 * @param otherParent the corresponding connection from the other parent
	 * @return true if the disabled connection should be re-enabled, false otherwise
	 * @throws IllegalArgumentException if either connection is null
	 */
	protected boolean shouldReEnable(final Connection chosenParent, final Connection otherParent) {
		Validate.notNull(chosenParent);
		Validate.notNull(otherParent);

		boolean shouldReEnable = false;
		if (chosenParent.isEnabled() == false && otherParent.isEnabled() == true) {
			if (randomGenerator.nextDouble() < neatCombination.reenableGeneInheritanceThresold()) {
				shouldReEnable = true;
			}
		}

		return shouldReEnable;
	}

	/**
	 * Constructs a new NEAT chromosome combinator with the specified components.
	 * 
	 * <p>The combinator uses the random generator for stochastic decisions during crossover,
	 * the combination policy for inheritance parameters, and the comparison handler for
	 * determining parent fitness relationships.
	 * 
	 * @param _randomGenerator random number generator for stochastic crossover decisions
	 * @param _neatCombination crossover policy defining inheritance parameters
	 * @param _parentComparisonHandler handler for comparing parent fitness and determining inheritance bias
	 * @throws IllegalArgumentException if any parameter is null
	 */
	public NeatChromosomeCombinator(final RandomGenerator _randomGenerator, final NeatCombination _neatCombination,
			final ParentComparisonHandler _parentComparisonHandler) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_neatCombination);
		Validate.notNull(_parentComparisonHandler);

		this.randomGenerator = _randomGenerator;
		this.neatCombination = _neatCombination;
		this.parentComparisonHandler = _parentComparisonHandler;
	}

	/**
	 * Performs genetic crossover between two NEAT chromosomes to produce offspring.
	 * 
	 * <p>This method implements the NEAT crossover algorithm, aligning genes by innovation
	 * number and applying inheritance rules based on parent fitness and configuration
	 * parameters. The result is a single offspring chromosome that inherits network
	 * topology and connection weights from both parents.
	 * 
	 * <p>Crossover process:
	 * <ol>
	 * <li>Compare parent fitness to determine inheritance bias</li>
	 * <li>Align genes by innovation number between parents</li>
	 * <li>Process matching genes with random inheritance (biased)</li>
	 * <li>Process disjoint genes based on fitness comparison</li>
	 * <li>Process excess genes from fitter parent</li>
	 * <li>Apply gene re-enabling rules for disabled connections</li>
	 * </ol>
	 * 
	 * @param eaConfiguration evolutionary algorithm configuration containing fitness comparator
	 * @param firstChromosome first parent chromosome (must be NeatChromosome)
	 * @param firstParentFitness fitness value of first parent
	 * @param secondChromosome second parent chromosome (must be NeatChromosome)
	 * @param secondParentFitness fitness value of second parent
	 * @return list containing single offspring chromosome
	 * @throws IllegalArgumentException if chromosomes are not NeatChromosome instances or any parameter is null
	 */
	@Override
	public List<Chromosome> combine(final AbstractEAConfiguration<T> eaConfiguration, final Chromosome firstChromosome,
			final T firstParentFitness, final Chromosome secondChromosome, final T secondParentFitness) {
		Validate.notNull(eaConfiguration);
		Validate.notNull(firstChromosome);
		Validate.notNull(firstParentFitness);
		Validate.isInstanceOf(NeatChromosome.class, firstChromosome);
		Validate.notNull(secondChromosome);
		Validate.notNull(secondParentFitness);
		Validate.isInstanceOf(NeatChromosome.class, secondChromosome);

		final NeatChromosome firstNeatChromosome = (NeatChromosome) firstChromosome;
		final NeatChromosome secondNeatChromosome = (NeatChromosome) secondChromosome;
		final Comparator<T> fitnessComparator = eaConfiguration.fitnessComparator();
		final double inheritanceThresold = neatCombination.inheritanceThresold();
		final ParentComparisonPolicy parentComparisonPolicy = neatCombination.parentComparisonPolicy();

		final int fitnessComparison = fitnessComparator.compare(firstParentFitness, secondParentFitness);
		final ChosenOtherChromosome comparedChromosomes = parentComparisonHandler
				.compare(parentComparisonPolicy, firstNeatChromosome, secondNeatChromosome, fitnessComparison);
		final NeatChromosome bestChromosome = comparedChromosomes.chosen();
		final NeatChromosome worstChromosome = comparedChromosomes.other();

		final List<Connection> combinedConnections = new ArrayList<>();
		final Map<Integer, Set<Integer>> linksCache = new HashMap<>();

		final var bestConnections = bestChromosome.getConnections();
		final var worstConnections = worstChromosome.getConnections();

		int indexBest = 0;
		int indexWorst = 0;

		while (indexBest < bestConnections.size() && indexWorst < worstConnections.size()) {

			final var bestConnection = bestConnections.get(indexBest);
			final var worstConnection = worstConnections.get(indexWorst);

			if (bestConnection.innovation() == worstConnection.innovation()) {
				/**
				 * If innovation is the same, we pick the connection randomly
				 */
				var original = bestConnection;
				var other = worstConnection;
				if (randomGenerator.nextDouble() < 1 - inheritanceThresold) {
					original = worstConnection;
					other = bestConnection;
				}
				if (linksCacheContainsConnection(linksCache, original) == false) {

					/**
					 * If the chosen gene is disabled but the other one is enabled, then there is a
					 * chance we will re-enable it
					 */
					final boolean isEnabled = shouldReEnable(original, other) ? true : original.isEnabled();

					final var childConnection = Connection.builder()
							.from(original)
							.isEnabled(isEnabled)
							.build();
					combinedConnections.add(childConnection);
					insertInlinksCache(linksCache, original);
				}
				indexBest++;
				indexWorst++;
			} else if (bestConnection.innovation() > worstConnection.innovation()) {

				/**
				 * If the fitnesses are equal, then we randomly inherit from the parent
				 * Otherwise, we do not inherit from the lesser gene
				 */
				if (fitnessComparison == 0 && randomGenerator.nextDouble() < 1.0 - inheritanceThresold) {
					final var original = worstConnection;
					if (linksCacheContainsConnection(linksCache, original) == false) {
						combinedConnections.add(Connection.copyOf(original));
						insertInlinksCache(linksCache, original);
					}
				}

				indexWorst++;
			} else {

				/**
				 * If the fitnesses are equal, then we randomly inherit from the parent
				 * Otherwise, we always inherit from the better gene
				 */

				if (fitnessComparison != 0 || randomGenerator.nextDouble() < inheritanceThresold) {
					if (linksCacheContainsConnection(linksCache, bestConnection) == false) {
						combinedConnections.add(Connection.copyOf(bestConnection));
						insertInlinksCache(linksCache, bestConnection);
					}
				}
				indexBest++;
			}
		}

		/*
		 * Case where the best connection has more genes. It's called excess genes
		 */
		while (indexBest < bestConnections.size()) {
			/**
			 * If the fitnesses are equal, then we randomly inherit from the parent
			 * Otherwise, we always inherit from the better gene
			 */
			if (fitnessComparison != 0 || randomGenerator.nextDouble() < inheritanceThresold) {
				final var bestConnection = bestConnections.get(indexBest);
				if (linksCacheContainsConnection(linksCache, bestConnection) == false) {
					combinedConnections.add(Connection.copyOf(bestConnection));
					insertInlinksCache(linksCache, bestConnection);
				}

			}
			indexBest++;
		}

		/*
		 * Case where the worst connection has more genes. It's called excess genes.
		 * Since we don't inherit when their fitness aren't equal, it means we can skip
		 * the excess genes from the weaker connections. However we will randomly
		 * inherit if their fitnesses are equal
		 */
		while (fitnessComparison == 0 && indexWorst < worstConnections.size()) {
			if (randomGenerator.nextDouble() < 1.0 - inheritanceThresold) {
				final var worstConnection = worstConnections.get(indexWorst);
				if (linksCacheContainsConnection(linksCache, worstConnection) == false) {
					combinedConnections.add(Connection.copyOf(worstConnection));
					insertInlinksCache(linksCache, worstConnection);
				}

			}
			indexWorst++;
		}

		return List.of(new NeatChromosome(bestChromosome.getNumInputs(),
				bestChromosome.getNumOutputs(),
				bestChromosome.getMinWeightValue(),
				bestChromosome.getMaxWeightValue(),
				combinedConnections));
	}
}