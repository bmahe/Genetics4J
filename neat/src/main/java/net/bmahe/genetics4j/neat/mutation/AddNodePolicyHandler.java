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
import net.bmahe.genetics4j.neat.spec.mutation.AddNode;

/**
 * Mutation policy handler for NEAT (NeuroEvolution of Augmenting Topologies) add-node mutations.
 * 
 * <p>AddNodePolicyHandler manages the structural mutation that adds new hidden nodes to NEAT neural
 * networks by splitting existing connections. This is one of the most important structural mutations
 * in NEAT as it enables the evolution of increasingly complex network topologies.
 * 
 * <p>Add-node mutation process:
 * <ol>
 * <li><strong>Connection selection</strong>: Choose an existing enabled connection to split</li>
 * <li><strong>Connection disabling</strong>: Disable the original connection</li>
 * <li><strong>Node creation</strong>: Create a new hidden node between the connection endpoints</li>
 * <li><strong>Connection replacement</strong>: Create two new connections through the new node</li>
 * <li><strong>Innovation tracking</strong>: Assign innovation numbers to new connections</li>
 * <li><strong>Weight preservation</strong>: Set weights to preserve network function</li>
 * </ol>
 * 
 * <p>Key characteristics:
 * <ul>
 * <li><strong>Topology complexity</strong>: Increases network depth and node count</li>
 * <li><strong>Function preservation</strong>: Maintains network behavior through careful weight setting</li>
 * <li><strong>Innovation tracking</strong>: New connections receive unique innovation numbers</li>
 * <li><strong>Gradual growth</strong>: Incrementally increases network complexity</li>
 * </ul>
 * 
 * <p>Network transformation:
 * <ul>
 * <li><strong>Before</strong>: Direct connection A → B with weight W</li>
 * <li><strong>After</strong>: Path A → NewNode → B with weights W₁ and W₂</li>
 * <li><strong>Weight strategy</strong>: Often W₁ = 1.0, W₂ = W to preserve function</li>
 * <li><strong>Node placement</strong>: New node gets next available index</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create add-node mutation policy
 * AddNode addNodePolicy = AddNode.of(0.05);  // 5% mutation rate
 * 
 * // Create policy handler
 * RandomGenerator randomGen = RandomGenerator.getDefault();
 * AddNodePolicyHandler<Double> handler = new AddNodePolicyHandler<>(randomGen);
 * 
 * // Check if handler can process the policy
 * boolean canHandle = handler.canHandle(resolver, addNodePolicy);
 * 
 * // Create mutator for the policy
 * Mutator mutator = handler.createMutator(
 *     executionContext, configuration, resolver, addNodePolicy
 * );
 * 
 * // Apply mutation to population
 * List<Individual<Double>> mutatedPopulation = mutator.mutate(
 *     configuration, population
 * );
 * }</pre>
 * 
 * <p>Integration with NEAT algorithm:
 * <ul>
 * <li><strong>Innovation management</strong>: Coordinates with InnovationManager for new connections</li>
 * <li><strong>Chromosome mutation</strong>: Delegates to NeatChromosomeAddNodeMutationHandler</li>
 * <li><strong>Population evolution</strong>: Applied based on configured mutation probability</li>
 * <li><strong>Complexity growth</strong>: Primary mechanism for increasing network complexity</li>
 * </ul>
 * 
 * <p>Structural impact:
 * <ul>
 * <li><strong>Hidden layer growth</strong>: Creates new hidden nodes that can form layers</li>
 * <li><strong>Computational depth</strong>: Increases potential computational complexity</li>
 * <li><strong>Feature detection</strong>: New nodes can detect intermediate features</li>
 * <li><strong>Representation power</strong>: Enhances network's representational capacity</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Conservative application</strong>: Typically applied less frequently than weight mutations</li>
 * <li><strong>Innovation caching</strong>: Leverages InnovationManager for efficient tracking</li>
 * <li><strong>Memory efficiency</strong>: Minimal allocation during mutation operations</li>
 * <li><strong>Function preservation</strong>: Weight setting strategies maintain network behavior</li>
 * </ul>
 * 
 * @param <T> the fitness value type (typically Double)
 * @see AddNode
 * @see net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeAddNodeMutationHandler
 * @see MutationPolicyHandler
 * @see InnovationManager
 */
public class AddNodePolicyHandler<T extends Comparable<T>> implements MutationPolicyHandler<T> {

	private final RandomGenerator randomGenerator;

	/**
	 * Constructs a new add-node policy handler with the specified random generator.
	 * 
	 * <p>The random generator is used for stochastic decisions during mutation application,
	 * including selection of individuals to mutate and selection of connections to split.
	 * 
	 * @param _randomGenerator random number generator for stochastic mutation operations
	 * @throws IllegalArgumentException if randomGenerator is null
	 */
	public AddNodePolicyHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	/**
	 * Determines whether this handler can process the given mutation policy.
	 * 
	 * <p>This handler specifically processes AddNode mutation policies, which configure
	 * the parameters for adding new hidden nodes to NEAT neural networks.
	 * 
	 * @param mutationPolicyHandlerResolver resolver for nested mutation policies
	 * @param mutationPolicy the mutation policy to check
	 * @return true if the policy is an AddNode instance, false otherwise
	 * @throws IllegalArgumentException if any parameter is null
	 */
	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof AddNode;
	}

	/**
	 * Creates a concrete mutator for add-node mutations.
	 * 
	 * <p>This method resolves the appropriate chromosome mutation handlers for NEAT chromosomes
	 * and creates a generic mutator that applies add-node mutations according to the
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
	 * @param mutationPolicy the add-node mutation policy
	 * @return a configured mutator for applying add-node mutations
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

		final AddNode addNodeMutationPolicy = (AddNode) mutationPolicy;
		final double populationMutationProbability = addNodeMutationPolicy.populationMutationProbability();

		final ChromosomeMutationHandler<? extends Chromosome>[] chromosomeMutationHandlers = ChromosomeResolverUtils
				.resolveChromosomeMutationHandlers(eaExecutionContext, eaConfiguration, mutationPolicy);

		return new GenericMutatorImpl(randomGenerator,
				chromosomeMutationHandlers,
				mutationPolicy,
				populationMutationProbability);
	}
}