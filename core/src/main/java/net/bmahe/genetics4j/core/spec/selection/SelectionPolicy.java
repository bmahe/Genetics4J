package net.bmahe.genetics4j.core.spec.selection;

/**
 * Marker interface for selection policy specifications in evolutionary algorithms.
 * 
 * <p>SelectionPolicy defines the contract for specifying selection strategies and their parameters.
 * Selection policies determine how individuals are chosen from the population for reproduction,
 * directly influencing the evolutionary pressure and convergence characteristics of the algorithm.
 * 
 * <p>Selection policies are used by:
 * <ul>
 * <li><strong>EA Configuration</strong>: To specify the parent selection strategy</li>
 * <li><strong>Selection handlers</strong>: To create appropriate selector implementations</li>
 * <li><strong>Strategy resolution</strong>: To match policies with their corresponding handlers</li>
 * <li><strong>Parameter configuration</strong>: To define selection pressure and tournament sizes</li>
 * </ul>
 * 
 * <p>The framework provides several concrete selection policy implementations:
 * <ul>
 * <li>{@link Tournament}: Tournament selection with configurable tournament size</li>
 * <li>{@link RouletteWheel}: Fitness-proportionate selection (roulette wheel)</li>
 * <li>{@link RandomSelection}: Uniform random selection without fitness bias</li>
 * <li>{@link DoubleTournament}: Two-stage tournament for multi-objective optimization</li>
 * <li>{@link ProportionalTournament}: Tournament with proportional selection pressure</li>
 * <li>{@link MultiSelections}: Combination of multiple selection strategies</li>
 * <li>{@link SelectAll}: Selects all individuals (useful for specific algorithms)</li>
 * </ul>
 * 
 * <p>Selection strategies vary in their characteristics:
 * <ul>
 * <li><strong>Selection pressure</strong>: How strongly the algorithm favors high-fitness individuals</li>
 * <li><strong>Diversity preservation</strong>: How well the strategy maintains population diversity</li>
 * <li><strong>Computational complexity</strong>: Efficiency of the selection process</li>
 * <li><strong>Scalability</strong>: Performance with different population sizes</li>
 * </ul>
 * 
 * <p>Common selection patterns:
 * <ul>
 * <li><strong>Exploitative strategies</strong>: High selection pressure (large tournaments, elitism)</li>
 * <li><strong>Explorative strategies</strong>: Low selection pressure (small tournaments, random)</li>
 * <li><strong>Balanced strategies</strong>: Moderate pressure with diversity preservation</li>
 * <li><strong>Adaptive strategies</strong>: Selection pressure that changes during evolution</li>
 * </ul>
 * 
 * <p>Selection policy design considerations:
 * <ul>
 * <li><strong>Problem characteristics</strong>: Multimodal vs unimodal, deceptive problems</li>
 * <li><strong>Population size</strong>: Small populations may need lower selection pressure</li>
 * <li><strong>Fitness landscape</strong>: Rugged landscapes benefit from diversity preservation</li>
 * <li><strong>Convergence requirements</strong>: Balance between speed and solution quality</li>
 * </ul>
 * 
 * <p>Example usage in genetic algorithm configuration:
 * <pre>{@code
 * // Tournament selection with moderate pressure
 * SelectionPolicy tournament = Tournament.of(3);
 * 
 * // Roulette wheel for fitness-proportionate selection
 * SelectionPolicy roulette = RouletteWheel.build();
 * 
 * // Multi-objective selection with double tournament
 * SelectionPolicy doubleTournament = DoubleTournament.of(
 *     firstCriteria: 3,
 *     secondCriteria: 2
 * );
 * 
 * // Combined selection strategies
 * SelectionPolicy multiSelection = MultiSelections.of(
 *     Tuple.of(0.7, tournament),
 *     Tuple.of(0.3, roulette)
 * );
 * 
 * // Use in EA configuration
 * EAConfiguration<Double> config = EAConfigurationBuilder.<Double>builder()
 *     .chromosomeSpecs(chromosomeSpec)
 *     .parentSelectionPolicy(tournament)
 *     .build();
 * }</pre>
 * 
 * @see net.bmahe.genetics4j.core.selection.Selector
 * @see net.bmahe.genetics4j.core.selection.SelectionPolicyHandler
 * @see Tournament
 * @see RouletteWheel
 * @see RandomSelection
 * @see DoubleTournament
 * @see ProportionalTournament
 * @see MultiSelections
 */
public interface SelectionPolicy {

}