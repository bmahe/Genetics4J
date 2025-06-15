package net.bmahe.genetics4j.neat.mutation.chromosome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.InnovationManager;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.mutation.AddConnection;

/**
 * Chromosome mutation handler that adds new connections to NEAT (NeuroEvolution of Augmenting Topologies) neural networks.
 * 
 * <p>NeatChromosomeAddConnection implements the add-connection structural mutation for NEAT chromosomes,
 * which increases network connectivity by creating new weighted links between existing nodes. This mutation
 * is essential for NEAT's ability to explore different network topologies and discover optimal connectivity patterns.
 * 
 * <p>Add-connection mutation process:
 * <ol>
 * <li><strong>Node selection</strong>: Randomly select source and target nodes from all available nodes</li>
 * <li><strong>Validity checking</strong>: Ensure the connection doesn't violate network constraints</li>
 * <li><strong>Duplicate prevention</strong>: Verify the connection doesn't already exist</li>
 * <li><strong>Innovation assignment</strong>: Assign unique innovation number via InnovationManager</li>
 * <li><strong>Weight initialization</strong>: Set random weight within chromosome bounds</li>
 * <li><strong>Connection creation</strong>: Add enabled connection to the chromosome</li>
 * </ol>
 * 
 * <p>Connection constraints:
 * <ul>
 * <li><strong>No self-connections</strong>: Source and target nodes must be different</li>
 * <li><strong>No duplicate connections</strong>: Connection between same nodes cannot already exist</li>
 * <li><strong>Feed-forward topology</strong>: Output nodes cannot be sources, input nodes cannot be targets</li>
 * <li><strong>Valid node references</strong>: Both nodes must exist in the network</li>
 * </ul>
 * 
 * <p>Node selection strategy:
 * <ul>
 * <li><strong>Available nodes</strong>: All input, output, and hidden nodes are potential connection endpoints</li>
 * <li><strong>Dynamic range</strong>: Node range adapts to include any hidden nodes created by add-node mutations</li>
 * <li><strong>Uniform selection</strong>: All valid nodes have equal probability of being selected</li>
 * <li><strong>Constraint filtering</strong>: Invalid connections are rejected and no mutation occurs</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create add-connection mutation handler
 * RandomGenerator randomGen = RandomGenerator.getDefault();
 * InnovationManager innovationManager = new InnovationManager();
 * NeatChromosomeAddConnection handler = new NeatChromosomeAddConnection(
 *     randomGen, innovationManager
 * );
 * 
 * // Check if handler can process mutation
 * AddConnection mutationPolicy = AddConnection.of(0.1);
 * NeatChromosomeSpec chromosomeSpec = NeatChromosomeSpec.of(3, 2, -1.0f, 1.0f);
 * boolean canHandle = handler.canHandle(mutationPolicy, chromosomeSpec);
 * 
 * // Apply mutation to chromosome
 * NeatChromosome originalChromosome = // ... existing chromosome
 * NeatChromosome mutatedChromosome = handler.mutate(mutationPolicy, originalChromosome);
 * 
 * // Result: chromosome may have one additional connection (if valid connection found)
 * }</pre>
 * 
 * <p>Integration with NEAT algorithm:
 * <ul>
 * <li><strong>Innovation tracking</strong>: Uses InnovationManager for consistent innovation number assignment</li>
 * <li><strong>Population consistency</strong>: Same connection types get same innovation numbers across population</li>
 * <li><strong>Genetic alignment</strong>: Innovation numbers enable proper crossover alignment</li>
 * <li><strong>Structural diversity</strong>: Increases topological diversity in the population</li>
 * </ul>
 * 
 * <p>Weight initialization:
 * <ul>
 * <li><strong>Random weights</strong>: New connections get random weights within chromosome bounds</li>
 * <li><strong>Uniform distribution</strong>: Weights uniformly distributed between min and max values</li>
 * <li><strong>Immediate activation</strong>: New connections are enabled and immediately affect network behavior</li>
 * <li><strong>Bounded values</strong>: Weights respect chromosome's min/max weight constraints</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Efficient validation</strong>: Fast checks for connection existence and validity</li>
 * <li><strong>Innovation caching</strong>: Leverages InnovationManager's O(1) innovation lookup</li>
 * <li><strong>Memory efficiency</strong>: Minimal allocation during mutation</li>
 * <li><strong>Failed mutation handling</strong>: Gracefully handles cases where no valid connection can be added</li>
 * </ul>
 * 
 * @see AddConnection
 * @see NeatChromosome
 * @see InnovationManager
 * @see ChromosomeMutationHandler
 */
public class NeatChromosomeAddConnection implements ChromosomeMutationHandler<NeatChromosome> {

	public static final Logger logger = LogManager.getLogger(NeatChromosomeAddConnection.class);

	private final RandomGenerator randomGenerator;
	private final InnovationManager innovationManager;

	/**
	 * Constructs a new add-connection mutation handler with the specified components.
	 * 
	 * <p>The random generator is used for node selection and weight initialization.
	 * The innovation manager provides unique innovation numbers for new connections,
	 * ensuring consistent tracking across the population.
	 * 
	 * @param _randomGenerator random number generator for stochastic operations
	 * @param _innovationManager innovation manager for tracking structural changes
	 * @throws IllegalArgumentException if randomGenerator or innovationManager is null
	 */
	public NeatChromosomeAddConnection(final RandomGenerator _randomGenerator,
			final InnovationManager _innovationManager) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_innovationManager);

		this.randomGenerator = _randomGenerator;
		this.innovationManager = _innovationManager;
	}

	/**
	 * Determines whether this handler can process the given mutation policy and chromosome specification.
	 * 
	 * <p>This handler specifically processes AddConnection mutations applied to NeatChromosomeSpec
	 * specifications, ensuring type compatibility for NEAT neural network connection addition.
	 * 
	 * @param mutationPolicy the mutation policy to check
	 * @param chromosome the chromosome specification to check
	 * @return true if policy is AddConnection and chromosome is NeatChromosomeSpec, false otherwise
	 * @throws IllegalArgumentException if any parameter is null
	 */
	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof AddConnection && chromosome instanceof NeatChromosomeSpec;
	}

	/**
	 * Applies add-connection mutation to a NEAT chromosome.
	 * 
	 * <p>This method attempts to add a new connection between two randomly selected nodes
	 * in the neural network. The mutation may fail if no valid connection can be found
	 * (e.g., all possible connections already exist or violate network constraints).
	 * 
	 * <p>Mutation algorithm:
	 * <ol>
	 * <li>Determine the range of available nodes (inputs, outputs, and hidden nodes)</li>
	 * <li>Randomly select source and target nodes</li>
	 * <li>Validate the connection doesn't violate constraints</li>
	 * <li>If valid, create new connection with innovation number and random weight</li>
	 * <li>Add connection to chromosome and return modified chromosome</li>
	 * <li>If invalid, return chromosome unchanged</li>
	 * </ol>
	 * 
	 * @param mutationPolicy the add-connection mutation policy
	 * @param chromosome the NEAT chromosome to mutate
	 * @return a new chromosome with potentially one additional connection
	 * @throws IllegalArgumentException if policy is not AddConnection or chromosome is not NeatChromosome
	 */
	@Override
	public NeatChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(AddConnection.class, mutationPolicy);
		Validate.isInstanceOf(NeatChromosome.class, chromosome);

		final var neatChromosome = (NeatChromosome) chromosome;
		final var numInputs = neatChromosome.getNumInputs();
		final var numOutputs = neatChromosome.getNumOutputs();
		final var minValue = neatChromosome.getMinWeightValue();
		final var maxValue = neatChromosome.getMaxWeightValue();

		final var oldConnections = neatChromosome.getConnections();
		final List<Connection> newConnections = new ArrayList<>(oldConnections);

		final int maxNodeConnectionsValue = neatChromosome.getConnections()
				.stream()
				.map(connection -> Math.max(connection.fromNodeIndex(), connection.toNodeIndex()))
				.max(Comparator.naturalOrder())
				.orElse(0);

		final int maxNodeValue = Math.max(maxNodeConnectionsValue,
				neatChromosome.getNumInputs() + neatChromosome.getNumOutputs() - 1);

		final int fromNode = randomGenerator.nextInt(maxNodeValue + 1);
		final int toNode = randomGenerator.nextInt(maxNodeValue + 1);

		final boolean isConnectionExist = oldConnections.stream()
				.anyMatch(connection -> connection.fromNodeIndex() == fromNode && connection.toNodeIndex() == toNode);

		final boolean isFromNodeAnOutput = fromNode < numInputs + numOutputs && fromNode >= numInputs;
		final boolean isToNodeAnInput = toNode < numInputs;

		if (fromNode != toNode && isConnectionExist == false && isToNodeAnInput == false && isFromNodeAnOutput == false) {
			final int innovation = innovationManager.computeNewId(fromNode, toNode);

			final var newConnection = Connection.builder()
					.fromNodeIndex(fromNode)
					.toNodeIndex(toNode)
					.innovation(innovation)
					.weight(randomGenerator.nextFloat(minValue, maxValue))
					.isEnabled(true)
					.build();

			newConnections.add(newConnection);
		}

		return new NeatChromosome(numInputs, numOutputs, minValue, maxValue, newConnections);
	}
}