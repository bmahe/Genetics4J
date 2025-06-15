package net.bmahe.genetics4j.neat;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder;
import net.bmahe.genetics4j.neat.chromosomes.factory.NeatConnectedChromosomeFactory;
import net.bmahe.genetics4j.neat.combination.NeatCombinationHandler;
import net.bmahe.genetics4j.neat.mutation.AddConnectionPolicyHandler;
import net.bmahe.genetics4j.neat.mutation.AddNodePolicyHandler;
import net.bmahe.genetics4j.neat.mutation.DeleteConnectionPolicyHandler;
import net.bmahe.genetics4j.neat.mutation.DeleteNodePolicyHandler;
import net.bmahe.genetics4j.neat.mutation.NeatConnectionWeightPolicyHandler;
import net.bmahe.genetics4j.neat.mutation.NeatSwitchStatePolicyHandler;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeAddConnection;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeAddNodeMutationHandler;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeConnectionWeightMutationHandler;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeCreepMutationHandler;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeDeleteConnection;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeDeleteNodeMutationHandler;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeRandomMutationHandler;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeSwitchStateHandler;
import net.bmahe.genetics4j.neat.selection.NeatSelectionPolicyHandler;

/**
 * Factory class for creating NEAT (NeuroEvolution of Augmenting Topologies) execution contexts.
 * 
 * <p>NeatEAExecutionContexts provides convenient factory methods for setting up evolutionary algorithm
 * execution contexts with all the necessary NEAT-specific components, including innovation management,
 * species-based selection, structural mutations, and neural network-specific genetic operators.
 * This class serves as the primary entry point for configuring NEAT evolutionary systems.
 * 
 * <p>Key NEAT components integrated:
 * <ul>
 * <li><strong>Innovation management</strong>: Historical marking system for structural mutations</li>
 * <li><strong>Species-based selection</strong>: Population organization by genetic similarity</li>
 * <li><strong>Structural mutations</strong>: Add/delete nodes and connections with innovation tracking</li>
 * <li><strong>Neural network crossover</strong>: Topology-aware genetic recombination</li>
 * <li><strong>Chromosome factories</strong>: Initial network generation with proper connectivity</li>
 * </ul>
 * 
 * <p>NEAT algorithm configuration:
 * <ul>
 * <li><strong>Mutation operators</strong>: Weight perturbation, add/delete nodes, add/delete connections</li>
 * <li><strong>Selection strategy</strong>: Species-based selection with fitness sharing</li>
 * <li><strong>Crossover mechanism</strong>: Innovation-number-based gene alignment</li>
 * <li><strong>Network initialization</strong>: Configurable initial topology generation</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Standard NEAT setup with default components
 * EAExecutionContext<Double> context = NeatEAExecutionContexts.<Double>standard().build();
 * 
 * // Custom NEAT setup with existing builder
 * var builder = EAExecutionContexts.<Double>forScalarFitness()
 *     .randomGenerator(myRandomGenerator)
 *     .termination(myTerminationCondition);
 * EAExecutionContext<Double> context = NeatEAExecutionContexts.enrichWithNeat(builder).build();
 * 
 * // Advanced setup with custom innovation manager
 * InnovationManager customInnovationManager = new InnovationManager(1000);
 * SpeciesIdGenerator customSpeciesIdGenerator = new SpeciesIdGenerator();
 * ChromosomeFactoryProvider customFactoryProvider = // ... create custom provider
 * 
 * var context = NeatEAExecutionContexts.enrichWithNeat(
 *     builder, customInnovationManager, customSpeciesIdGenerator, customFactoryProvider
 * ).build();
 * }</pre>
 * 
 * <p>Integrated NEAT genetic operators:
 * <ul>
 * <li><strong>Weight mutations</strong>: Gaussian perturbation, random replacement, creep mutation</li>
 * <li><strong>Structural mutations</strong>: Add node, add connection, delete node, delete connection</li>
 * <li><strong>State mutations</strong>: Enable/disable connections for network topology exploration</li>
 * <li><strong>Crossover operations</strong>: Innovation-guided gene alignment and inheritance</li>
 * </ul>
 * 
 * <p>Species-based evolution:
 * <ul>
 * <li><strong>Compatibility distance</strong>: Genetic similarity measurement for speciation</li>
 * <li><strong>Fitness sharing</strong>: Population diversity maintenance through species-based selection</li>
 * <li><strong>Species management</strong>: Dynamic species formation and extinction</li>
 * <li><strong>Representative selection</strong>: Species representative selection for next generation</li>
 * </ul>
 * 
 * <p>Performance and scalability:
 * <ul>
 * <li><strong>Efficient innovation tracking</strong>: O(1) innovation number lookup</li>
 * <li><strong>Concurrent execution</strong>: Thread-safe components for parallel evolution</li>
 * <li><strong>Memory management</strong>: Configurable cache management for large populations</li>
 * <li><strong>Modular design</strong>: Customizable components for specific problem domains</li>
 * </ul>
 * 
 * @see InnovationManager
 * @see SpeciesIdGenerator
 * @see NeatChromosome
 * @see net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec
 */
public class NeatEAExecutionContexts {

	private NeatEAExecutionContexts() {
	}

	/**
	 * Enriches an existing EA execution context builder with standard NEAT components.
	 * 
	 * <p>This method configures the builder with default NEAT components including a new innovation
	 * manager, species ID generator, and connected chromosome factory. All NEAT-specific genetic
	 * operators, selection mechanisms, and mutation handlers are automatically registered.
	 * 
	 * @param <T> the fitness value type
	 * @param builder the execution context builder to enrich with NEAT capabilities
	 * @return the builder configured with NEAT components
	 * @throws IllegalArgumentException if builder is null
	 */
	public static <T extends Number & Comparable<T>> Builder<T> enrichWithNeat(final Builder<T> builder) {
		Validate.notNull(builder);

		final var innovationManager = new InnovationManager();
		final var speciesIdGenerator = new SpeciesIdGenerator();

		final var cfp = ImmutableChromosomeFactoryProvider.builder()
				.randomGenerator(RandomGenerator.getDefault())
				.addChromosomeFactoriesGenerator(
						cdp -> new NeatConnectedChromosomeFactory(cdp.randomGenerator(), innovationManager))
				.build();

		return enrichWithNeat(builder, innovationManager, speciesIdGenerator, cfp);
	}

	/**
	 * Enriches an EA execution context builder with custom NEAT components.
	 * 
	 * <p>This method allows full customization of NEAT components while still configuring all
	 * necessary genetic operators and handlers. This is useful when you need custom innovation
	 * tracking, species management, or initial network topology generation.
	 * 
	 * @param <T> the fitness value type
	 * @param builder the execution context builder to enrich
	 * @param innovationManager custom innovation manager for structural mutation tracking
	 * @param speciesIdGenerator custom species ID generator for population organization
	 * @param chromosomeFactoryProvider custom factory provider for initial network generation
	 * @return the builder configured with custom NEAT components
	 * @throws IllegalArgumentException if any parameter is null
	 */
	public static <T extends Number & Comparable<T>> Builder<T> enrichWithNeat(final Builder<T> builder,
			final InnovationManager innovationManager, final SpeciesIdGenerator speciesIdGenerator,
			final ChromosomeFactoryProvider chromosomeFactoryProvider) {
		Validate.notNull(builder);
		Validate.notNull(innovationManager);
		Validate.notNull(speciesIdGenerator);
		Validate.notNull(chromosomeFactoryProvider);

		builder.chromosomeFactoryProvider(chromosomeFactoryProvider)
				.addSelectionPolicyHandlerFactories(
						ec -> new NeatSelectionPolicyHandler<>(ec.randomGenerator(), speciesIdGenerator))
				.addMutationPolicyHandlerFactories(ec -> new NeatSwitchStatePolicyHandler<>(ec.randomGenerator()),
						ec -> new AddNodePolicyHandler<>(ec.randomGenerator()),
						ec -> new DeleteNodePolicyHandler<>(ec.randomGenerator()),
						ec -> new AddConnectionPolicyHandler<>(ec.randomGenerator()),
						ec -> new DeleteConnectionPolicyHandler<>(ec.randomGenerator()),
						ec -> new NeatConnectionWeightPolicyHandler<>(ec.randomGenerator()))
				.addChromosomeCombinatorHandlerFactories(ec -> new NeatCombinationHandler<>(ec.randomGenerator()))
				.addChromosomeMutationPolicyHandlerFactories(
						ec -> new NeatChromosomeSwitchStateHandler(ec.randomGenerator()),
						ec -> new NeatChromosomeCreepMutationHandler(ec.randomGenerator()),
						ec -> new NeatChromosomeRandomMutationHandler(ec.randomGenerator()),
						ec -> new NeatChromosomeAddNodeMutationHandler(ec.randomGenerator(), innovationManager),
						ec -> new NeatChromosomeDeleteNodeMutationHandler(ec.randomGenerator()),
						ec -> new NeatChromosomeAddConnection(ec.randomGenerator(), innovationManager),
						ec -> new NeatChromosomeDeleteConnection(ec.randomGenerator()),
						ec -> new NeatChromosomeConnectionWeightMutationHandler(ec.randomGenerator()));

		return builder;
	}

	/**
	 * Creates a standard NEAT execution context builder with default configuration.
	 * 
	 * <p>This is the most convenient method for setting up a NEAT evolutionary algorithm with
	 * standard components and configurations. The returned builder is pre-configured with:
	 * <ul>
	 * <li>Scalar fitness evaluation</li>
	 * <li>Default innovation manager and species ID generator</li>
	 * <li>Connected initial network topology</li>
	 * <li>All standard NEAT genetic operators</li>
	 * <li>Species-based selection mechanism</li>
	 * </ul>
	 * 
	 * @param <T> the fitness value type (typically Double)
	 * @return a builder configured with standard NEAT components
	 */
	public static <T extends Number & Comparable<T>> Builder<T> standard() {

		final var scalarEAExecutionContext = EAExecutionContexts.<T>forScalarFitness();
		return enrichWithNeat(scalarEAExecutionContext);
	}
}