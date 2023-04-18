package net.bmahe.genetics4j.core.mutation;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.util.ChromosomeResolverUtils;

public class CreepMutationPolicyHandler<T extends Comparable<T>> implements MutationPolicyHandler<T> {

	private final RandomGenerator randomGenerator;

	public CreepMutationPolicyHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof CreepMutation;
	}

	@Override
	public Mutator createMutator(final AbstractEAExecutionContext<T> eaExecutionContext,
			final AbstractEAConfiguration<T> eaConfiguration,
			final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(mutationPolicy);
		Validate.notNull(mutationPolicyHandlerResolver);

		final CreepMutation creepMutationPolicy = (CreepMutation) mutationPolicy;
		final double populationMutationProbability = creepMutationPolicy.populationMutationProbability();

		final ChromosomeMutationHandler<? extends Chromosome>[] chromosomeMutationHandlers = ChromosomeResolverUtils
				.resolveChromosomeMutationHandlers(eaExecutionContext, eaConfiguration, mutationPolicy);

		return new GenericMutatorImpl(randomGenerator,
				chromosomeMutationHandlers,
				mutationPolicy,
				populationMutationProbability);
	}
}