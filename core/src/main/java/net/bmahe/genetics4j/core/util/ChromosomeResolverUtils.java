package net.bmahe.genetics4j.core.util;

import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public class ChromosomeResolverUtils {

	public static <T extends Comparable<T>> ChromosomeMutationHandler<? extends Chromosome> findMatchingChromosomeMutationPolicyHandler(
			final AbstractEAExecutionContext<T> eaExecutionContext, final MutationPolicy mutationPolicy,
			final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosomeSpec);

		final List<ChromosomeMutationHandler<? extends Chromosome>> chromosomeMutationPolicyHandlers = eaExecutionContext
				.chromosomeMutationPolicyHandlers();

		return chromosomeMutationPolicyHandlers.stream()
				.dropWhile((sph) -> sph.canHandle(mutationPolicy, chromosomeSpec) == false)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Could not find suitable chromosome mutation policy handler for policy: " + mutationPolicy
								+ " and chromosome spec: " + chromosomeSpec));
	}

	public static <T extends Comparable<T>> ChromosomeMutationHandler<? extends Chromosome>[] resolveChromosomeMutationHandlers(
			final AbstractEAExecutionContext<T> eaExecutionContext, final AbstractEAConfiguration<T> eaConfiguration,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(mutationPolicy);

		final ChromosomeMutationHandler<? extends Chromosome>[] chromosomePolicyHandlers = new ChromosomeMutationHandler[eaConfiguration
				.chromosomeSpecs()
				.size()];
		final List<ChromosomeSpec> chromosomeSpecs = eaConfiguration.chromosomeSpecs();
		for (int i = 0; i < chromosomeSpecs.size(); i++) {
			final ChromosomeSpec chromosomeSpec = chromosomeSpecs.get(i);
			chromosomePolicyHandlers[i] = findMatchingChromosomeMutationPolicyHandler(eaExecutionContext,
					mutationPolicy,
					chromosomeSpec);
		}

		return chromosomePolicyHandlers;
	}
}