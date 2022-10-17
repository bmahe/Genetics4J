package net.bmahe.genetics4j.core.mutation;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;
import net.bmahe.genetics4j.core.util.ChromosomeResolverUtils;

public class SwapMutationPolicyHandler<T extends Comparable<T>> implements MutationPolicyHandler<T> {

	private final RandomGenerator randomGenerator;

	public SwapMutationPolicyHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof SwapMutation;
	}

	@Override
	public Mutator createMutator(final AbstractEAExecutionContext<T> eaExecutionContext,
			final AbstractEAConfiguration<T> eaConfiguration,
			final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(SwapMutation.class, mutationPolicy);

		final SwapMutation swapMutation = (SwapMutation) mutationPolicy;
		final double populationMutationProbability = swapMutation.populationMutationProbability();

		@SuppressWarnings("rawtypes")
		final ChromosomeMutationHandler[] chromosomeMutationHandlers = ChromosomeResolverUtils
				.resolveChromosomeMutationHandlers(eaExecutionContext, eaConfiguration, mutationPolicy);

		return new Mutator() {

			@Override
			public Genotype mutate(final Genotype original) {
				Validate.notNull(original);

				final Chromosome[] chromosomes = original.getChromosomes();
				final Chromosome[] newChromosomes = new Chromosome[chromosomes.length];

				if (randomGenerator.nextDouble() < populationMutationProbability) {

					for (int i = 0; i < chromosomes.length; i++) {
						final Chromosome chromosome = chromosomes[i];
						final Chromosome mutatedChromosome = chromosomeMutationHandlers[i].mutate(mutationPolicy, chromosome);

						newChromosomes[i] = mutatedChromosome;
					}
				} else {
					for (int i = 0; i < chromosomes.length; i++) {
						final Chromosome chromosome = chromosomes[i];
						newChromosomes[i] = chromosome;
					}
				}

				return new Genotype(newChromosomes);
			}
		};
	}
}