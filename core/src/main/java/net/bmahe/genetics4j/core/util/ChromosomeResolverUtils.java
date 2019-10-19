package net.bmahe.genetics4j.core.util;

import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public class ChromosomeResolverUtils {

	public static ChromosomeMutationHandler<? extends Chromosome> findMatchingChromosomeMutationPolicyHandler(
			final GeneticSystemDescriptor geneticSystemDescriptor, final MutationPolicy mutationPolicy,
			final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(geneticSystemDescriptor);
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosomeSpec);

		final List<ChromosomeMutationHandler<? extends Chromosome>> chromosomeMutationPolicyHandlers = geneticSystemDescriptor
				.chromosomeMutationPolicyHandlers();

		return chromosomeMutationPolicyHandlers.stream()
				.dropWhile((sph) -> sph.canHandle(mutationPolicy, chromosomeSpec) == false)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Could not find suitable chromosome mutation policy handler for policy: " + mutationPolicy
								+ " and chromosome spec: " + chromosomeSpec));
	}

	@SuppressWarnings("rawtypes")
	public static ChromosomeMutationHandler[] resolveChromosomeMutationHandlers(
			final GeneticSystemDescriptor geneticSystemDescriptor, final GenotypeSpec genotypeSpec,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(geneticSystemDescriptor);
		Validate.notNull(genotypeSpec);
		Validate.notNull(mutationPolicy);

		final ChromosomeMutationHandler[] chromosomePolicyHandlers = new ChromosomeMutationHandler[genotypeSpec
				.chromosomeSpecs()
				.size()];
		final List<ChromosomeSpec> chromosomeSpecs = genotypeSpec.chromosomeSpecs();
		for (int i = 0; i < chromosomeSpecs.size(); i++) {
			final ChromosomeSpec chromosomeSpec = chromosomeSpecs.get(i);
			chromosomePolicyHandlers[i] = findMatchingChromosomeMutationPolicyHandler(geneticSystemDescriptor,
					mutationPolicy, chromosomeSpec);
		}

		return chromosomePolicyHandlers;
	}
}