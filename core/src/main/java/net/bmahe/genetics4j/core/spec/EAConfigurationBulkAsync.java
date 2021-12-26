package net.bmahe.genetics4j.core.spec;

import java.util.Arrays;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.FitnessBulkAsync;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

/**
 * Evolutionary Algorithm Configuration.
 * <p>
 * This describe the set of strategies to use. They describe the genotype, the
 * different policies for selection, combination as well as mutation, and other
 * relevant parameters such as how to determine the fitness of a given
 * individual.
 * 
 * @param <T> Type of the fitness measurement
 */
@Value.Immutable
public abstract class EAConfigurationBulkAsync<T extends Comparable<T>> extends EAConfiguration<T> {

	/**
	 * Defines how should individuals' fitness be assessed
	 * 
	 * @return
	 */
	public abstract FitnessBulkAsync<T> fitness();

	public static class Builder<T extends Comparable<T>> extends ImmutableEAConfigurationBulkAsync.Builder<T> {

		public final EAConfigurationBulkAsync.Builder<T> chromosomeSpecs(final ChromosomeSpec... elements) {
			return this.chromosomeSpecs(Arrays.asList(elements));
		}

		public final EAConfigurationBulkAsync.Builder<T> mutationPolicies(final MutationPolicy... elements) {
			return this.mutationPolicies(Arrays.asList(elements));
		}
	}
}