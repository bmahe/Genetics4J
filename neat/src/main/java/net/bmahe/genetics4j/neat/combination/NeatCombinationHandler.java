package net.bmahe.genetics4j.neat.combination;

import java.util.Optional;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorHandler;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorResolver;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.neat.combination.parentcompare.ParentComparisonHandler;
import net.bmahe.genetics4j.neat.combination.parentcompare.ParentComparisonHandlerLocator;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.combination.NeatCombination;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.ParentComparisonPolicy;

/**
 * Chromosome combinator handler for NEAT (NeuroEvolution of Augmenting Topologies) genetic crossover.
 * 
 * <p>NeatCombinationHandler manages the genetic recombination process for NEAT neural networks,
 * implementing innovation-number-based gene alignment and parent comparison strategies. This handler
 * resolves NEAT-specific combination policies into concrete chromosome combinators that understand
 * neural network topology and can perform meaningful genetic crossover between different network structures.
 * 
 * <p>Key responsibilities:
 * <ul>
 * <li><strong>Policy resolution</strong>: Converts NeatCombination policies into executable combinators</li>
 * <li><strong>Parent comparison</strong>: Manages strategies for determining genetic inheritance patterns</li>
 * <li><strong>Innovation alignment</strong>: Ensures proper gene alignment using innovation numbers</li>
 * <li><strong>Topology crossover</strong>: Handles recombination of different network topologies</li>
 * </ul>
 * 
 * <p>NEAT genetic crossover features:
 * <ul>
 * <li><strong>Innovation-based alignment</strong>: Genes matched by innovation number for meaningful recombination</li>
 * <li><strong>Matching genes</strong>: Genes with same innovation number can be inherited from either parent</li>
 * <li><strong>Disjoint genes</strong>: Genes unique to one parent in middle innovation number ranges</li>
 * <li><strong>Excess genes</strong>: Genes beyond the highest innovation number of less fit parent</li>
 * </ul>
 * 
 * <p>Parent comparison strategies:
 * <ul>
 * <li><strong>Fitness-based inheritance</strong>: More fit parent contributes disjoint and excess genes</li>
 * <li><strong>Equal fitness handling</strong>: Special rules when parents have equal fitness</li>
 * <li><strong>Random inheritance</strong>: Stochastic gene inheritance for matching genes</li>
 * <li><strong>Topology preservation</strong>: Ensures offspring have valid network topologies</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create NEAT combination handler
 * RandomGenerator randomGen = RandomGenerator.getDefault();
 * NeatCombinationHandler<Double> handler = new NeatCombinationHandler<>(randomGen);
 * 
 * // Check if handler can process combination policy
 * NeatCombination policy = NeatCombination.builder()
 *     .parentComparisonPolicy(new FitnessComparison())
 *     .build();
 * NeatChromosomeSpec spec = NeatChromosomeSpec.of(3, 2, -1.0f, 1.0f);
 * 
 * boolean canHandle = handler.canHandle(resolver, policy, spec);
 * 
 * // Resolve to concrete combinator
 * ChromosomeCombinator<Double> combinator = handler.resolve(resolver, policy, spec);
 * }</pre>
 * 
 * <p>Integration with genetic algorithm framework:
 * <ul>
 * <li><strong>Handler registration</strong>: Registered in EA execution context for automatic resolution</li>
 * <li><strong>Policy-driven configuration</strong>: Behavior controlled by NeatCombination specifications</li>
 * <li><strong>Resolver integration</strong>: Works with chromosome combinator resolver system</li>
 * <li><strong>Type safety</strong>: Ensures compatibility between policies and chromosome specifications</li>
 * </ul>
 * 
 * <p>Parent comparison handler management:
 * <ul>
 * <li><strong>Handler locator</strong>: Uses ParentComparisonHandlerLocator for strategy resolution</li>
 * <li><strong>Strategy flexibility</strong>: Supports different parent comparison approaches</li>
 * <li><strong>Extensibility</strong>: New comparison strategies can be easily added</li>
 * <li><strong>Policy validation</strong>: Ensures configured strategies are available</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Handler caching</strong>: Parent comparison handlers cached for reuse</li>
 * <li><strong>Innovation sorting</strong>: Leverages pre-sorted connection lists for efficiency</li>
 * <li><strong>Memory efficiency</strong>: Minimal memory allocation during crossover</li>
 * <li><strong>Parallel processing</strong>: Thread-safe operations for concurrent crossover</li>
 * </ul>
 * 
 * @param <T> the fitness value type (typically Double)
 * @see NeatCombination
 * @see NeatChromosomeCombinator
 * @see ParentComparisonHandler
 * @see ChromosomeCombinatorHandler
 */
public class NeatCombinationHandler<T extends Comparable<T>> implements ChromosomeCombinatorHandler<T> {

	private final RandomGenerator randomGenerator;
	private final ParentComparisonHandlerLocator parentComparisonHandlerLocator;

	/**
	 * Constructs a new NEAT combination handler with the specified random generator.
	 * 
	 * <p>The random generator is used for stochastic decisions during genetic crossover,
	 * such as choosing which parent contributes genes when multiple options are available.
	 * A parent comparison handler locator is automatically created to manage comparison strategies.
	 * 
	 * @param _randomGenerator random number generator for stochastic crossover operations
	 * @throws IllegalArgumentException if randomGenerator is null
	 */
	public NeatCombinationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
		this.parentComparisonHandlerLocator = new ParentComparisonHandlerLocator();
	}

	/**
	 * Determines whether this handler can process the given combination policy and chromosome specification.
	 * 
	 * <p>This handler specifically processes NeatCombination policies applied to NeatChromosomeSpec
	 * specifications, ensuring type compatibility for NEAT neural network genetic crossover.
	 * 
	 * @param chromosomeCombinatorResolver resolver for nested combination policies
	 * @param combinationPolicy the combination policy to check
	 * @param chromosome the chromosome specification to check
	 * @return true if policy is NeatCombination and chromosome is NeatChromosomeSpec, false otherwise
	 * @throws IllegalArgumentException if any parameter is null
	 */
	@Override
	public boolean canHandle(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return combinationPolicy instanceof NeatCombination && chromosome instanceof NeatChromosomeSpec;
	}

	/**
	 * Resolves a NEAT combination policy into a concrete chromosome combinator.
	 * 
	 * <p>This method creates a NeatChromosomeCombinator configured with the appropriate parent
	 * comparison strategy. The parent comparison policy is resolved using the handler locator
	 * to determine how genetic inheritance should be managed during crossover.
	 * 
	 * <p>Resolution process:
	 * <ol>
	 * <li>Extract parent comparison policy from the NEAT combination configuration</li>
	 * <li>Locate appropriate parent comparison handler for the policy</li>
	 * <li>Create NeatChromosomeCombinator with resolved components</li>
	 * <li>Return configured combinator ready for genetic crossover</li>
	 * </ol>
	 * 
	 * @param chromosomeCombinatorResolver resolver for nested combination policies
	 * @param combinationPolicy the NEAT combination policy to resolve
	 * @param chromosome the NEAT chromosome specification
	 * @return a configured chromosome combinator for NEAT genetic crossover
	 * @throws IllegalArgumentException if any parameter is null or of wrong type
	 * @throws IllegalStateException if no parent comparison handler found for the policy
	 */
	@Override
	public ChromosomeCombinator<T> resolve(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(NeatCombination.class, combinationPolicy);
		Validate.isInstanceOf(NeatChromosomeSpec.class, chromosome);

		final var neatCombination = (NeatCombination) combinationPolicy;

		final ParentComparisonPolicy parentComparisonPolicy = neatCombination.parentComparisonPolicy();
		final Optional<ParentComparisonHandler> parentComparisonHandlerOpt = parentComparisonHandlerLocator
				.find(parentComparisonPolicy);
		final ParentComparisonHandler parentComparisonHandler = parentComparisonHandlerOpt
				.orElseThrow(() -> new IllegalStateException(
						"Could not find a parent comparison handler for policy: " + parentComparisonPolicy));

		return new NeatChromosomeCombinator<>(randomGenerator, neatCombination, parentComparisonHandler);
	}
}