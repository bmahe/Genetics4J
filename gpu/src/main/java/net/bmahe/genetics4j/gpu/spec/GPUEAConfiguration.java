package net.bmahe.genetics4j.gpu.spec;

import java.util.Arrays;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.gpu.spec.fitness.OpenCLFitness;

@Value.Immutable
public abstract class GPUEAConfiguration<T extends Comparable<T>> extends AbstractEAConfiguration<T> {

	public abstract Program program();

	public abstract OpenCLFitness<T> fitness();

	public static class Builder<T extends Comparable<T>> extends ImmutableGPUEAConfiguration.Builder<T> {

		public final GPUEAConfiguration.Builder<T> chromosomeSpecs(final ChromosomeSpec... elements) {
			return this.chromosomeSpecs(Arrays.asList(elements));
		}

		public final GPUEAConfiguration.Builder<T> mutationPolicies(final MutationPolicy... elements) {
			return this.mutationPolicies(Arrays.asList(elements));
		}
	}

	public static <U extends Comparable<U>> Builder<U> builder() {
		return new Builder<>();
	}
}