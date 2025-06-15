package net.bmahe.genetics4j.core.spec.mutation;

/**
 * Marker interface for mutation policy specifications in evolutionary algorithms.
 * 
 * <p>MutationPolicy defines the contract for specifying mutation strategies and their parameters.
 * Mutation policies determine how genetic variation is introduced into the population by making
 * random changes to individual genotypes. Different mutation policies implement various strategies
 * for balancing exploration and exploitation during the evolutionary process.
 * 
 * <p>Mutation policies are used by:
 * <ul>
 * <li><strong>EA Configuration</strong>: To specify the mutation strategy for the algorithm</li>
 * <li><strong>Mutation handlers</strong>: To create appropriate mutator implementations</li>
 * <li><strong>Strategy resolution</strong>: To select chromosome-specific mutation operators</li>
 * <li><strong>Parameter configuration</strong>: To define mutation rates and other parameters</li>
 * </ul>
 * 
 * <p>The framework provides several concrete mutation policy implementations:
 * <ul>
 * <li>{@link RandomMutation}: Randomly changes allele values with specified probability</li>
 * <li>{@link CreepMutation}: Makes small incremental changes to numeric values</li>
 * <li>{@link SwapMutation}: Exchanges positions of alleles (useful for permutations)</li>
 * <li>{@link PartialMutation}: Applies mutation to only a subset of chromosomes</li>
 * <li>{@link MultiMutations}: Combines multiple mutation strategies</li>
 * </ul>
 * 
 * <p>Mutation strategies vary by chromosome type and problem domain:
 * <ul>
 * <li><strong>Binary chromosomes</strong>: Bit flip mutations</li>
 * <li><strong>Numeric chromosomes</strong>: Gaussian noise, uniform perturbation, creep mutation</li>
 * <li><strong>Permutation chromosomes</strong>: Swap, insert, inversion mutations</li>
 * <li><strong>Tree chromosomes</strong>: Node replacement, subtree mutation, growth/pruning</li>
 * </ul>
 * 
 * <p>Key considerations for mutation policy design:
 * <ul>
 * <li><strong>Mutation rate</strong>: Balance between exploration and exploitation</li>
 * <li><strong>Chromosome compatibility</strong>: Ensure mutation respects chromosome constraints</li>
 * <li><strong>Problem-specific operations</strong>: Adapt mutation to problem characteristics</li>
 * <li><strong>Adaptive strategies</strong>: Allow mutation parameters to evolve during the run</li>
 * </ul>
 * 
 * <p>Example usage in genetic algorithm configuration:
 * <pre>{@code
 * // Random mutation with 5% probability per allele
 * MutationPolicy randomMutation = RandomMutation.of(0.05);
 * 
 * // Creep mutation for numeric optimization
 * MutationPolicy creepMutation = CreepMutation.of(0.1, 1.0);
 * 
 * // Combination of multiple mutation strategies
 * MutationPolicy multiMutation = MultiMutations.of(
 *     Tuple.of(0.7, randomMutation),
 *     Tuple.of(0.3, creepMutation)
 * );
 * 
 * // Use in EA configuration
 * EAConfiguration<Double> config = EAConfigurationBuilder.<Double>builder()
 *     .chromosomeSpecs(chromosomeSpec)
 *     .mutationPolicy(randomMutation)
 *     .build();
 * }</pre>
 * 
 * @see net.bmahe.genetics4j.core.mutation.Mutator
 * @see net.bmahe.genetics4j.core.mutation.MutationPolicyHandler
 * @see RandomMutation
 * @see CreepMutation
 * @see SwapMutation
 * @see PartialMutation
 * @see MultiMutations
 */
public interface MutationPolicy {

}