package net.bmahe.genetics4j.gp.mutation;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.gp.program.ProgramGenerator;
import net.bmahe.genetics4j.gp.spec.mutation.TrimTree;

public class TrimTreePolicyHandler<T extends Comparable<T>> implements MutationPolicyHandler<T> {
	final static public Logger logger = LogManager.getLogger(TrimTreePolicyHandler.class);

	final RandomGenerator randomGenerator;
	final ProgramGenerator programGenerator;

	public TrimTreePolicyHandler(final RandomGenerator _randomGenerator, final ProgramGenerator _programGenerator) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_programGenerator);

		this.randomGenerator = _randomGenerator;
		this.programGenerator = _programGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof TrimTree;
	}

	@Override
	public Mutator createMutator(final AbstractEAExecutionContext<T> eaExecutionContext,
			final AbstractEAConfiguration<T> eaConfiguration,
			final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(TrimTree.class, mutationPolicy);

		final TrimTree trimTree = (TrimTree) mutationPolicy;

		return new TrimTreeMutator(programGenerator, randomGenerator, eaConfiguration, trimTree);
	}
}