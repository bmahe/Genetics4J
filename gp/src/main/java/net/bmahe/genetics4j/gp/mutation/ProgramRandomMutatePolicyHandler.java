package net.bmahe.genetics4j.gp.mutation;

import java.util.Random;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.gp.program.ProgramGenerator;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomMutate;

public class ProgramRandomMutatePolicyHandler implements MutationPolicyHandler {
	final static public Logger logger = LogManager.getLogger(ProgramRandomMutatePolicyHandler.class);

	final Random random;
	final ProgramGenerator programGenerator;

	public ProgramRandomMutatePolicyHandler(final Random _random, final ProgramGenerator _programGenerator) {
		Validate.notNull(_random);
		Validate.notNull(_programGenerator);

		this.random = _random;
		this.programGenerator = _programGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof ProgramRandomMutate;
	}

	@Override
	public Mutator createMutator(final EAExecutionContext eaExecutionContext, final EAConfiguration eaConfiguration,
			final MutationPolicyHandlerResolver mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(ProgramRandomMutate.class, mutationPolicy);

		final ProgramRandomMutate programRandomMutate = (ProgramRandomMutate) mutationPolicy;
		final double populationMutationProbability = programRandomMutate.populationMutationProbability();

		return new ProgramRandomMutateMutator(programGenerator, random, eaConfiguration, populationMutationProbability);
	}
}