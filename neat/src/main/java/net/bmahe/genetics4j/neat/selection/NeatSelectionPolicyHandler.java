package net.bmahe.genetics4j.neat.selection;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.neat.SpeciesIdGenerator;
import net.bmahe.genetics4j.neat.spec.selection.NeatSelection;

/**
 * Selection policy handler for NEAT (NeuroEvolution of Augmenting Topologies) species-based selection.
 * 
 * <p>NeatSelectionPolicyHandler implements the species-based selection mechanism that is fundamental to
 * the NEAT algorithm. It organizes the population into species based on genetic compatibility, applies
 * fitness sharing within species, and manages reproduction allocation across species to maintain
 * population diversity and protect innovative topologies.
 * 
 * <p>Key responsibilities:
 * <ul>
 * <li><strong>Species formation</strong>: Groups genetically similar individuals into species</li>
 * <li><strong>Fitness sharing</strong>: Adjusts individual fitness based on species membership</li>
 * <li><strong>Reproduction allocation</strong>: Distributes offspring across species based on average fitness</li>
 * <li><strong>Diversity preservation</strong>: Protects innovative topologies from elimination by established forms</li>
 * </ul>
 * 
 * <p>NEAT species-based selection process:
 * <ol>
 * <li><strong>Compatibility calculation</strong>: Measure genetic distance between individuals</li>
 * <li><strong>Species assignment</strong>: Assign individuals to species based on compatibility thresholds</li>
 * <li><strong>Fitness adjustment</strong>: Apply fitness sharing within each species</li>
 * <li><strong>Species evaluation</strong>: Calculate average fitness for each species</li>
 * <li><strong>Reproduction allocation</strong>: Determine offspring count for each species</li>
 * <li><strong>Within-species selection</strong>: Select parents within each species for reproduction</li>
 * </ol>
 * 
 * <p>Species management features:
 * <ul>
 * <li><strong>Dynamic speciation</strong>: Species boundaries adjust as population evolves</li>
 * <li><strong>Species extinction</strong>: Poor-performing species are eliminated</li>
 * <li><strong>Representative tracking</strong>: Maintains species representatives for compatibility testing</li>
 * <li><strong>Population diversity</strong>: Prevents single topology from dominating</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create NEAT selection policy handler
 * RandomGenerator randomGen = RandomGenerator.getDefault();
 * SpeciesIdGenerator speciesIdGen = new SpeciesIdGenerator();
 * 
 * NeatSelectionPolicyHandler<Double> handler = new NeatSelectionPolicyHandler<>(
 *     randomGen, speciesIdGen
 * );
 * 
 * // Configure NEAT selection policy
 * NeatSelection<Double> neatSelection = NeatSelection.<Double>builder()
 *     .compatibilityThreshold(3.0)
 *     .speciesSelection(new TournamentSelection(3))  // Within-species selection
 *     .build();
 * 
 * // Resolve selector for EA execution
 * Selector<Double> selector = handler.resolve(
 *     executionContext, configuration, resolverRegistry, neatSelection
 * );
 * }</pre>
 * 
 * <p>Integration with genetic operators:
 * <ul>
 * <li><strong>Crossover compatibility</strong>: Species ensure genetic compatibility for meaningful recombination</li>
 * <li><strong>Mutation guidance</strong>: Species composition influences structural mutation rates</li>
 * <li><strong>Innovation protection</strong>: New topologies get time to optimize within their species</li>
 * <li><strong>Diversity maintenance</strong>: Multiple species explore different regions of topology space</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Compatibility caching</strong>: Genetic distances cached for efficiency</li>
 * <li><strong>Species reuse</strong>: Species structures maintained across generations</li>
 * <li><strong>Parallel processing</strong>: Species-based selection enables concurrent evaluation</li>
 * <li><strong>Memory management</strong>: Efficient species membership tracking</li>
 * </ul>
 * 
 * <p>Selection policy delegation:
 * <ul>
 * <li><strong>Within-species selection</strong>: Delegates to standard selection policies (tournament, roulette, etc.)</li>
 * <li><strong>Composable policies</strong>: Can combine with any standard selection mechanism</li>
 * <li><strong>Flexible configuration</strong>: Different species can use different selection strategies</li>
 * <li><strong>Performance optimization</strong>: Leverages existing high-performance selectors</li>
 * </ul>
 * 
 * @param <T> the fitness value type (typically Double)
 * @see NeatSelection
 * @see NeatSelectorImpl
 * @see Species
 * @see SpeciesIdGenerator
 * @see SelectionPolicyHandler
 */
public class NeatSelectionPolicyHandler<T extends Number & Comparable<T>> implements SelectionPolicyHandler<T> {
	public static final Logger logger = LogManager.getLogger(NeatSelectionPolicyHandler.class);

	private final RandomGenerator randomGenerator;
	private final SpeciesIdGenerator speciesIdGenerator;

	/**
	 * Constructs a new NEAT selection policy handler with the specified components.
	 * 
	 * <p>The random generator is used for stochastic operations during species formation
	 * and selection. The species ID generator provides unique identifiers for newly
	 * created species throughout the evolutionary process.
	 * 
	 * @param _randomGenerator random number generator for stochastic operations
	 * @param _speciesIdGenerator generator for unique species identifiers
	 * @throws IllegalArgumentException if randomGenerator or speciesIdGenerator is null
	 */
	public NeatSelectionPolicyHandler(final RandomGenerator _randomGenerator,
			final SpeciesIdGenerator _speciesIdGenerator) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_speciesIdGenerator);

		this.randomGenerator = _randomGenerator;
		this.speciesIdGenerator = _speciesIdGenerator;
	}

	/**
	 * Determines whether this handler can process the given selection policy.
	 * 
	 * <p>This handler specifically processes NeatSelection policies, which configure
	 * species-based selection with compatibility thresholds and within-species
	 * selection strategies.
	 * 
	 * @param selectionPolicy the selection policy to check
	 * @return true if the policy is a NeatSelection instance, false otherwise
	 * @throws IllegalArgumentException if selectionPolicy is null
	 */
	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);

		return selectionPolicy instanceof NeatSelection;
	}

	/**
	 * Resolves a NEAT selection policy into a concrete selector implementation.
	 * 
	 * <p>This method creates a NeatSelectorImpl that implements the species-based selection
	 * mechanism. It resolves the within-species selection policy using the provided
	 * resolver and configures the selector with the necessary NEAT components.
	 * 
	 * <p>Resolution process:
	 * <ol>
	 * <li>Extract the within-species selection policy from the NEAT selection configuration</li>
	 * <li>Resolve the within-species selection policy to a concrete selector</li>
	 * <li>Create a NeatSelectorImpl with all necessary components</li>
	 * <li>Return the configured selector ready for use in evolution</li>
	 * </ol>
	 * 
	 * @param eaExecutionContext the execution context for the evolutionary algorithm
	 * @param eaConfiguration the configuration for the evolutionary algorithm
	 * @param selectionPolicyHandlerResolver resolver for nested selection policies
	 * @param selectionPolicy the NEAT selection policy to resolve
	 * @return a configured selector implementing NEAT species-based selection
	 * @throws IllegalArgumentException if selectionPolicy is null or not a NeatSelection
	 */
	@Override
	public Selector<T> resolve(final AbstractEAExecutionContext<T> eaExecutionContext,
			final AbstractEAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(NeatSelection.class, selectionPolicy);

		final NeatSelection<T> neatSelection = (NeatSelection<T>) selectionPolicy;

		final SelectionPolicy speciesSelection = neatSelection.speciesSelection();
		final SelectionPolicyHandler<T> speciesSelectionPolicyHandler = selectionPolicyHandlerResolver
				.resolve(speciesSelection);
		final Selector<T> speciesSelector = speciesSelectionPolicyHandler
				.resolve(eaExecutionContext, eaConfiguration, selectionPolicyHandlerResolver, speciesSelection);

		return new NeatSelectorImpl<>(randomGenerator, neatSelection, speciesIdGenerator, speciesSelector);
	}
}