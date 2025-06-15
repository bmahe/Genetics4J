package net.bmahe.genetics4j.core.mutation;

import net.bmahe.genetics4j.core.Genotype;

/**
 * Functional interface for applying mutation operations to genotypes in evolutionary algorithms.
 * 
 * <p>Mutation is a crucial genetic operator that introduces variation into the population by making
 * small random changes to individual genotypes. This helps maintain genetic diversity and enables
 * the exploration of new areas in the solution space.
 * 
 * <p>The mutator operates on the genotype level, potentially modifying one or more chromosomes
 * within the genotype according to the specific mutation strategy being applied.
 * 
 * <p>Common mutation strategies include:
 * <ul>
 * <li><strong>Random mutation</strong>: Randomly change allele values with a given probability</li>
 * <li><strong>Creep mutation</strong>: Make small incremental changes to numeric values</li>
 * <li><strong>Swap mutation</strong>: Exchange positions of two alleles (useful for permutations)</li>
 * <li><strong>Partial mutation</strong>: Apply mutation to only a subset of chromosomes</li>
 * </ul>
 * 
 * <p>Implementations should be:
 * <ul>
 * <li><strong>Stateless</strong>: The same input should produce consistent probabilistic behavior</li>
 * <li><strong>Thread-safe</strong>: May be called concurrently during parallel evolution</li>
 * <li><strong>Preserve structure</strong>: Maintain genotype integrity and constraints</li>
 * </ul>
 * 
 * @see net.bmahe.genetics4j.core.spec.mutation.MutationPolicy
 * @see net.bmahe.genetics4j.core.mutation.MutationPolicyHandler
 * @see Genotype
 */
@FunctionalInterface
public interface Mutator {

	/**
	 * Applies mutation to the given genotype and returns a new mutated genotype.
	 * 
	 * <p>The original genotype should not be modified; instead, a new genotype with
	 * the mutations applied should be returned. The specific mutation behavior depends
	 * on the implementation and the mutation policy being used.
	 * 
	 * <p>Mutation may affect:
	 * <ul>
	 * <li>Individual alleles within chromosomes (bit flips, value changes)</li>
	 * <li>Chromosome structure (for variable-length representations)</li>
	 * <li>Multiple chromosomes simultaneously</li>
	 * </ul>
	 * 
	 * @param original the genotype to mutate, must not be null
	 * @return a new genotype with mutations applied, never null
	 * @throws IllegalArgumentException if original genotype is null or invalid
	 * @throws RuntimeException if mutation fails due to constraint violations
	 */
	Genotype mutate(Genotype original);
}