package net.bmahe.genetics4j.neat.mutation;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.GenericMutatorImpl;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.util.ChromosomeResolverUtils;
import net.bmahe.genetics4j.neat.spec.mutation.NeatConnectionWeight;

/**
 * Mutation policy handler for NEAT (NeuroEvolution of Augmenting Topologies) connection weight mutations.
 * 
 * <p>NeatConnectionWeightPolicyHandler manages weight mutations for NEAT neural network connections,
 * which are essential for fine-tuning network behavior and optimizing performance. Weight mutations
 * are typically the most frequent type of mutation in NEAT evolution, applied to existing connections
 * to improve network functionality.
 * 
 * <p>Weight mutation strategies:
 * <ul>
 * <li><strong>Gaussian perturbation</strong>: Add small random values to existing weights</li>
 * <li><strong>Random replacement</strong>: Replace weights with new random values</li>
 * <li><strong>Creep mutation</strong>: Small incremental changes to weights</li>
 * <li><strong>Uniform perturbation</strong>: Add uniform random noise to weights</li>
 * </ul>
 * 
 * <p>Key characteristics:
 * <ul>
 * <li><strong>Behavioral optimization</strong>: Fine-tunes network behavior without changing topology</li>
 * <li><strong>High frequency</strong>: Applied more often than structural mutations</li>
 * <li><strong>Bounded values</strong>: Respects chromosome weight bounds during mutation</li>
 * <li><strong>Continuous evolution</strong>: Enables continuous improvement of network performance</li>
 * </ul>
 * 
 * <p>Integration with NEAT algorithm:
 * <ul>
 * <li><strong>Chromosome mutation</strong>: Delegates to NEAT chromosome weight mutation handlers</li>
 * <li><strong>Population evolution</strong>: Applied based on configured mutation probability</li>
 * <li><strong>Performance optimization</strong>: Primary mechanism for network performance tuning</li>
 * <li><strong>Genetic diversity</strong>: Maintains behavioral diversity in the population</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create weight mutation policy
 * NeatConnectionWeight weightPolicy = NeatConnectionWeight.builder()
 *     .populationMutationProbability(0.8)  // 80% of population
 *     .chromosomeMutationProbability(0.9)  // 90% of connections per chromosome
 *     .mutationStandardDeviation(0.1)      // Standard deviation for Gaussian perturbation
 *     .build();
 * 
 * // Create policy handler
 * RandomGenerator randomGen = RandomGenerator.getDefault();
 * NeatConnectionWeightPolicyHandler<Double> handler = 
 *     new NeatConnectionWeightPolicyHandler<>(randomGen);
 * 
 * // Check if handler can process the policy
 * boolean canHandle = handler.canHandle(resolver, weightPolicy);
 * 
 * // Create mutator for the policy
 * Mutator mutator = handler.createMutator(
 *     executionContext, configuration, resolver, weightPolicy
 * );
 * }</pre>
 * 
 * <p>Weight mutation effects:
 * <ul>
 * <li><strong>Performance tuning</strong>: Optimizes network output for better fitness</li>
 * <li><strong>Exploration vs exploitation</strong>: Balances exploration of weight space with convergence</li>
 * <li><strong>Gradient-like behavior</strong>: Can simulate gradient-based optimization</li>
 * <li><strong>Population diversity</strong>: Maintains diverse behavioral patterns</li>
 * </ul>
 * 
 * <p>Mutation policy configuration:
 * <ul>
 * <li><strong>Population probability</strong>: Fraction of population to apply mutations to</li>
 * <li><strong>Chromosome probability</strong>: Fraction of connections to mutate per chromosome</li>
 * <li><strong>Mutation magnitude</strong>: Size of weight perturbations</li>
 * <li><strong>Mutation type</strong>: Strategy for weight modification (Gaussian, uniform, etc.)</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>High frequency application</strong>: Efficient implementation for frequent use</li>
 * <li><strong>Memory efficiency</strong>: Minimal allocation during weight modification</li>
 * <li><strong>Numerical stability</strong>: Maintains weight values within valid ranges</li>
 * <li><strong>Concurrent safety</strong>: Thread-safe for parallel mutation application</li>
 * </ul>
 * 
 * @param <T> the fitness value type (typically Double)
 * @see NeatConnectionWeight
 * @see net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeConnectionWeightMutationHandler
 * @see MutationPolicyHandler
 */
public class NeatConnectionWeightPolicyHandler<T extends Comparable<T>> implements MutationPolicyHandler<T> {

	private final RandomGenerator randomGenerator;

	/**
	 * Constructs a new connection weight policy handler with the specified random generator.
	 * 
	 * <p>The random generator is used for stochastic decisions during mutation application,
	 * including selection of individuals to mutate and generation of weight perturbations.
	 * 
	 * @param _randomGenerator random number generator for stochastic mutation operations
	 * @throws IllegalArgumentException if randomGenerator is null
	 */
	public NeatConnectionWeightPolicyHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	/**
	 * Determines whether this handler can process the given mutation policy.
	 * 
	 * <p>This handler specifically processes NeatConnectionWeight mutation policies, which
	 * configure the parameters for mutating connection weights in NEAT neural networks.
	 * 
	 * @param mutationPolicyHandlerResolver resolver for nested mutation policies
	 * @param mutationPolicy the mutation policy to check
	 * @return true if the policy is a NeatConnectionWeight instance, false otherwise
	 * @throws IllegalArgumentException if any parameter is null
	 */
	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof NeatConnectionWeight;
	}

	/**
	 * Creates a concrete mutator for connection weight mutations.
	 * 
	 * <p>This method resolves the appropriate chromosome mutation handlers for NEAT chromosomes
	 * and creates a generic mutator that applies weight mutations according to the
	 * specified policy parameters.
	 * 
	 * <p>Mutator creation process:
	 * <ol>
	 * <li>Extract population mutation probability from the policy</li>
	 * <li>Resolve chromosome-specific mutation handlers</li>
	 * <li>Create generic mutator with resolved components</li>
	 * <li>Return configured mutator ready for population application</li>
	 * </ol>
	 * 
	 * @param eaExecutionContext execution context containing NEAT-specific components
	 * @param eaConfiguration evolutionary algorithm configuration
	 * @param mutationPolicyHandlerResolver resolver for chromosome mutation handlers
	 * @param mutationPolicy the connection weight mutation policy
	 * @return a configured mutator for applying weight mutations
	 * @throws IllegalArgumentException if any parameter is null
	 */
	@Override
	public Mutator createMutator(final AbstractEAExecutionContext<T> eaExecutionContext,
			final AbstractEAConfiguration<T> eaConfiguration,
			final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver, MutationPolicy mutationPolicy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(mutationPolicy);
		Validate.notNull(mutationPolicyHandlerResolver);

		final NeatConnectionWeight neatConnectionWeightMutationPolicy = (NeatConnectionWeight) mutationPolicy;
		final double populationMutationProbability = neatConnectionWeightMutationPolicy.populationMutationProbability();

		final ChromosomeMutationHandler<? extends Chromosome>[] chromosomeMutationHandlers = ChromosomeResolverUtils
				.resolveChromosomeMutationHandlers(eaExecutionContext, eaConfiguration, mutationPolicy);

		return new GenericMutatorImpl(randomGenerator,
				chromosomeMutationHandlers,
				mutationPolicy,
				populationMutationProbability);
	}
}