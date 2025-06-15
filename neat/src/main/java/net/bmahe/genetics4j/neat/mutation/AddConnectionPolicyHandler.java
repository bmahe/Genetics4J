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
import net.bmahe.genetics4j.neat.spec.mutation.AddConnection;

/**
 * Mutation policy handler for NEAT (NeuroEvolution of Augmenting Topologies) add-connection mutations.
 * 
 * <p>AddConnectionPolicyHandler manages the structural mutation that adds new connections between
 * existing nodes in NEAT neural networks. This is one of the fundamental structural mutations that
 * enables NEAT to explore different network topologies by increasing connectivity complexity.
 * 
 * <p>Add-connection mutation process:
 * <ol>
 * <li><strong>Node selection</strong>: Choose source and target nodes for the new connection</li>
 * <li><strong>Validity checking</strong>: Ensure connection doesn't already exist and doesn't create cycles</li>
 * <li><strong>Innovation assignment</strong>: Assign unique innovation number to the new connection</li>
 * <li><strong>Weight initialization</strong>: Set initial weight for the new connection</li>
 * <li><strong>Connection creation</strong>: Add enabled connection to the chromosome</li>
 * </ol>
 * 
 * <p>Key characteristics:
 * <ul>
 * <li><strong>Structural evolution</strong>: Increases network connectivity without adding nodes</li>
 * <li><strong>Innovation tracking</strong>: New connections receive unique innovation numbers</li>
 * <li><strong>Topology exploration</strong>: Enables discovery of useful connection patterns</li>
 * <li><strong>Gradual complexity</strong>: Incrementally increases network complexity</li>
 * </ul>
 * 
 * <p>Integration with NEAT algorithm:
 * <ul>
 * <li><strong>Innovation management</strong>: Coordinates with InnovationManager for tracking</li>
 * <li><strong>Chromosome mutation</strong>: Delegates to NeatChromosomeAddConnection handler</li>
 * <li><strong>Population evolution</strong>: Applied based on configured mutation probability</li>
 * <li><strong>Genetic diversity</strong>: Maintains structural diversity in the population</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create add-connection mutation policy
 * AddConnection addConnectionPolicy = AddConnection.of(0.1);  // 10% mutation rate
 * 
 * // Create policy handler
 * RandomGenerator randomGen = RandomGenerator.getDefault();
 * AddConnectionPolicyHandler<Double> handler = new AddConnectionPolicyHandler<>(randomGen);
 * 
 * // Check if handler can process the policy
 * boolean canHandle = handler.canHandle(resolver, addConnectionPolicy);
 * 
 * // Create mutator for the policy
 * Mutator mutator = handler.createMutator(
 *     executionContext, configuration, resolver, addConnectionPolicy
 * );
 * 
 * // Apply mutation to population
 * List<Individual<Double>> mutatedPopulation = mutator.mutate(
 *     configuration, population
 * );
 * }</pre>
 * 
 * <p>Mutation policy configuration:
 * <ul>
 * <li><strong>Population probability</strong>: Fraction of population to apply mutation to</li>
 * <li><strong>Individual probability</strong>: Probability of mutating each selected individual</li>
 * <li><strong>Connection constraints</strong>: Rules governing valid connection additions</li>
 * <li><strong>Weight initialization</strong>: Strategy for setting initial connection weights</li>
 * </ul>
 * 
 * <p>Structural constraints:
 * <ul>
 * <li><strong>Duplicate prevention</strong>: Avoids creating connections that already exist</li>
 * <li><strong>Cycle detection</strong>: Prevents creation of cycles in feed-forward networks</li>
 * <li><strong>Node validity</strong>: Ensures source and target nodes exist in the network</li>
 * <li><strong>Self-connection handling</strong>: May prevent or allow self-connections based on policy</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Innovation caching</strong>: Leverages InnovationManager for efficient number assignment</li>
 * <li><strong>Connection checking</strong>: Efficient algorithms for duplicate and cycle detection</li>
 * <li><strong>Memory efficiency</strong>: Minimal allocation during mutation operations</li>
 * <li><strong>Concurrent safety</strong>: Thread-safe for parallel mutation application</li>
 * </ul>
 * 
 * @param <T> the fitness value type (typically Double)
 * @see AddConnection
 * @see net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeAddConnection
 * @see MutationPolicyHandler
 * @see InnovationManager
 */
public class AddConnectionPolicyHandler<T extends Comparable<T>> implements MutationPolicyHandler<T> {

	private final RandomGenerator randomGenerator;

	/**
	 * Constructs a new add-connection policy handler with the specified random generator.
	 * 
	 * <p>The random generator is used for stochastic decisions during mutation application,
	 * including selection of individuals to mutate and choices within the mutation process.
	 * 
	 * @param _randomGenerator random number generator for stochastic mutation operations
	 * @throws IllegalArgumentException if randomGenerator is null
	 */
	public AddConnectionPolicyHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	/**
	 * Determines whether this handler can process the given mutation policy.
	 * 
	 * <p>This handler specifically processes AddConnection mutation policies, which configure
	 * the parameters for adding new connections to NEAT neural networks.
	 * 
	 * @param mutationPolicyHandlerResolver resolver for nested mutation policies
	 * @param mutationPolicy the mutation policy to check
	 * @return true if the policy is an AddConnection instance, false otherwise
	 * @throws IllegalArgumentException if any parameter is null
	 */
	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof AddConnection;
	}

	/**
	 * Creates a concrete mutator for add-connection mutations.
	 * 
	 * <p>This method resolves the appropriate chromosome mutation handlers for NEAT chromosomes
	 * and creates a generic mutator that applies add-connection mutations according to the
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
	 * @param mutationPolicy the add-connection mutation policy
	 * @return a configured mutator for applying add-connection mutations
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

		final AddConnection addConnectionMutationPolicy = (AddConnection) mutationPolicy;
		final double populationMutationProbability = addConnectionMutationPolicy.populationMutationProbability();

		final ChromosomeMutationHandler<? extends Chromosome>[] chromosomeMutationHandlers = ChromosomeResolverUtils
				.resolveChromosomeMutationHandlers(eaExecutionContext, eaConfiguration, mutationPolicy);

		return new GenericMutatorImpl(randomGenerator,
				chromosomeMutationHandlers,
				mutationPolicy,
				populationMutationProbability);
	}
}