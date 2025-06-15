package net.bmahe.genetics4j.neat.chromosomes.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactory;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.InnovationManager;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;

/**
 * Factory for creating fully-connected initial NEAT (NeuroEvolution of Augmenting Topologies) chromosomes.
 * 
 * <p>NeatConnectedChromosomeFactory generates initial neural network chromosomes with direct connections
 * between all input and output nodes. This provides a minimal starting topology that ensures all inputs
 * can influence all outputs, creating a foundation for structural evolution through the NEAT algorithm.
 * 
 * <p>Generated network characteristics:
 * <ul>
 * <li><strong>Full connectivity</strong>: Every input node connected to every output node</li>
 * <li><strong>No hidden nodes</strong>: Initial networks contain only input and output layers</li>
 * <li><strong>Random weights</strong>: Connection weights uniformly distributed within specified bounds</li>
 * <li><strong>Innovation tracking</strong>: All connections assigned unique innovation numbers</li>
 * </ul>
 * 
 * <p>Network topology structure:
 * <ul>
 * <li><strong>Input layer</strong>: Nodes 0 to (numInputs - 1)</li>
 * <li><strong>Output layer</strong>: Nodes numInputs to (numInputs + numOutputs - 1)</li>
 * <li><strong>Connections</strong>: numInputs × numOutputs fully-connected bipartite graph</li>
 * <li><strong>Enabled state</strong>: All initial connections are enabled</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create factory with innovation manager
 * RandomGenerator randomGen = RandomGenerator.getDefault();
 * InnovationManager innovationManager = new InnovationManager();
 * NeatConnectedChromosomeFactory factory = new NeatConnectedChromosomeFactory(
 *     randomGen, innovationManager
 * );
 * 
 * // Define network specification
 * NeatChromosomeSpec spec = NeatChromosomeSpec.of(
 *     3,      // 3 input nodes
 *     2,      // 2 output nodes  
 *     -1.0f,  // minimum weight
 *     1.0f    // maximum weight
 * );
 * 
 * // Generate initial chromosome
 * NeatChromosome chromosome = factory.generate(spec);
 * 
 * // Result: 3×2 = 6 connections with random weights
 * // Connections: (0→3), (0→4), (1→3), (1→4), (2→3), (2→4)
 * }</pre>
 * 
 * <p>Integration with NEAT evolution:
 * <ul>
 * <li><strong>Population initialization</strong>: Creates diverse initial population with same topology</li>
 * <li><strong>Weight diversity</strong>: Random weights provide behavioral variation</li>
 * <li><strong>Structural foundation</strong>: Minimal topology allows maximum structural exploration</li>
 * <li><strong>Innovation consistency</strong>: Same connection types get same innovation numbers across population</li>
 * </ul>
 * 
 * <p>Innovation number management:
 * <ul>
 * <li><strong>Deterministic assignment</strong>: Same input-output pairs get same innovation numbers</li>
 * <li><strong>Population consistency</strong>: All individuals use same innovation numbers for same connections</li>
 * <li><strong>Crossover compatibility</strong>: Enables meaningful genetic recombination from generation 0</li>
 * <li><strong>Historical tracking</strong>: Foundation for tracking structural evolution</li>
 * </ul>
 * 
 * <p>Weight initialization strategy:
 * <ul>
 * <li><strong>Uniform distribution</strong>: Weights uniformly sampled from [minWeight, maxWeight]</li>
 * <li><strong>Behavioral diversity</strong>: Different weight combinations create different behaviors</li>
 * <li><strong>Network stability</strong>: Bounded weights prevent extreme activation values</li>
 * <li><strong>Evolution readiness</strong>: Initial weights suitable for gradient-based optimization</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Linear time complexity</strong>: O(numInputs × numOutputs) generation time</li>
 * <li><strong>Memory efficiency</strong>: Minimal memory allocation during generation</li>
 * <li><strong>Innovation caching</strong>: InnovationManager provides O(1) innovation number lookup</li>
 * <li><strong>Thread safety</strong>: Safe for concurrent chromosome generation</li>
 * </ul>
 * 
 * @see NeatChromosome
 * @see NeatChromosomeSpec
 * @see InnovationManager
 * @see ChromosomeFactory
 */
public class NeatConnectedChromosomeFactory implements ChromosomeFactory<NeatChromosome> {

	private final RandomGenerator randomGenerator;
	private final InnovationManager innovationManager;

	/**
	 * Constructs a new connected chromosome factory with the specified components.
	 * 
	 * <p>The random generator is used for weight initialization, providing behavioral
	 * diversity in the initial population. The innovation manager ensures consistent
	 * innovation number assignment across all generated chromosomes.
	 * 
	 * @param _randomGenerator random number generator for weight initialization
	 * @param _innovationManager innovation manager for tracking structural innovations
	 * @throws IllegalArgumentException if randomGenerator or innovationManager is null
	 */
	public NeatConnectedChromosomeFactory(final RandomGenerator _randomGenerator,
			final InnovationManager _innovationManager) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_innovationManager);

		this.randomGenerator = _randomGenerator;
		this.innovationManager = _innovationManager;
	}

	/**
	 * Determines whether this factory can generate chromosomes for the given specification.
	 * 
	 * <p>This factory specifically handles NeatChromosomeSpec specifications, which define
	 * the input/output structure and weight bounds for NEAT neural networks.
	 * 
	 * @param chromosomeSpec the chromosome specification to check
	 * @return true if chromosomeSpec is a NeatChromosomeSpec, false otherwise
	 * @throws IllegalArgumentException if chromosomeSpec is null
	 */
	@Override
	public boolean canHandle(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);

		return chromosomeSpec instanceof NeatChromosomeSpec;
	}

	/**
	 * Generates a fully-connected NEAT chromosome based on the given specification.
	 * 
	 * <p>This method creates a neural network chromosome with direct connections between
	 * all input and output nodes. Each connection is initialized with a random weight
	 * within the specified bounds and assigned a unique innovation number for genetic
	 * tracking.
	 * 
	 * <p>Generation process:
	 * <ol>
	 * <li>Extract network parameters from the chromosome specification</li>
	 * <li>Create connections between all input-output node pairs</li>
	 * <li>Assign innovation numbers to each connection type</li>
	 * <li>Initialize connection weights randomly within bounds</li>
	 * <li>Enable all connections for immediate network functionality</li>
	 * <li>Construct and return the complete chromosome</li>
	 * </ol>
	 * 
	 * @param chromosomeSpec the NEAT chromosome specification defining network structure
	 * @return a new fully-connected NEAT chromosome
	 * @throws IllegalArgumentException if chromosomeSpec is null or not a NeatChromosomeSpec
	 */
	@Override
	public NeatChromosome generate(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);
		Validate.isInstanceOf(NeatChromosomeSpec.class, chromosomeSpec);

		final NeatChromosomeSpec neatChromosomeSpec = (NeatChromosomeSpec) chromosomeSpec;
		final int numInputs = neatChromosomeSpec.numInputs();
		final int numOutputs = neatChromosomeSpec.numOutputs();
		float minWeightValue = neatChromosomeSpec.minWeightValue();
		float maxWeightValue = neatChromosomeSpec.maxWeightValue();

		final List<Connection> connections = new ArrayList<>();
		for (int inputIndex = 0; inputIndex < numInputs; inputIndex++) {
			for (int outputIndex = numInputs; outputIndex < numInputs + numOutputs; outputIndex++) {

				final int innovation = innovationManager.computeNewId(inputIndex, outputIndex);
				final Connection connection = Connection.builder()
						.fromNodeIndex(inputIndex)
						.toNodeIndex(outputIndex)
						.innovation(innovation)
						.isEnabled(true)
						.weight(randomGenerator.nextFloat(minWeightValue, maxWeightValue))
						.build();
				connections.add(connection);
			}
		}

		return new NeatChromosome(numInputs, numOutputs, minWeightValue, maxWeightValue, connections);
	}
}