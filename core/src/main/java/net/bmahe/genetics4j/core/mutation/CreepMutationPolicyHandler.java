package net.bmahe.genetics4j.core.mutation;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.util.ChromosomeResolverUtils;

public class CreepMutationPolicyHandler implements MutationPolicyHandler {

	private final RandomGenerator randomGenerator;

	public CreepMutationPolicyHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof CreepMutation;
	}

	@Override
	public Mutator createMutator(final AbstractEAExecutionContext eaExecutionContext,
			final AbstractEAConfiguration eaConfiguration,
			final MutationPolicyHandlerResolver mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(mutationPolicy);
		Validate.notNull(mutationPolicyHandlerResolver);

		final CreepMutation creepMutationPolicy = (CreepMutation) mutationPolicy;
		final double populationMutationProbability = creepMutationPolicy.populationMutationProbability();

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