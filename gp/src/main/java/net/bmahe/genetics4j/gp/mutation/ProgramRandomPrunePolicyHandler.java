package net.bmahe.genetics4j.gp.mutation;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomPrune;

public class ProgramRandomPrunePolicyHandler<T extends Comparable<T>> implements MutationPolicyHandler<T> {

	final RandomGenerator randomGenerator;
	final ProgramHelper programHelper;

	public ProgramRandomPrunePolicyHandler(final RandomGenerator _randomGenerator, final ProgramHelper _programHelper) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_programHelper);

		this.randomGenerator = _randomGenerator;
		this.programHelper = _programHelper;
	}

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof ProgramRandomPrune;
	}

	@Override
	public Mutator createMutator(final AbstractEAExecutionContext<T> eaExecutionContext,
			final AbstractEAConfiguration<T> eaConfiguration,
			final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(ProgramRandomPrune.class, mutationPolicy);

		final ProgramRandomPrune programRandomPrune = (ProgramRandomPrune) mutationPolicy;
		final double populationMutationProbability = programRandomPrune.populationMutationProbability();

		return new ProgramRandomPruneMutator(programHelper,
				randomGenerator,
				eaConfiguration,
				populationMutationProbability);
	}
}