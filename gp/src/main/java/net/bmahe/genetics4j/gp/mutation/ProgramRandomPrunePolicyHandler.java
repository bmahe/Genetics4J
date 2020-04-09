package net.bmahe.genetics4j.gp.mutation;

import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomPrune;

public class ProgramRandomPrunePolicyHandler implements MutationPolicyHandler {

	final Random random;
	final ProgramHelper programHelper;

	public ProgramRandomPrunePolicyHandler(final Random _random, final ProgramHelper _programHelper) {
		Validate.notNull(_random);
		Validate.notNull(_programHelper);

		this.random = _random;
		this.programHelper = _programHelper;
	}

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof ProgramRandomPrune;
	}

	@Override
	public Mutator createMutator(final GeneticSystemDescriptor geneticSystemDescriptor, final GenotypeSpec genotypeSpec,
			final MutationPolicyHandlerResolver mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(geneticSystemDescriptor);
		Validate.notNull(genotypeSpec);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(ProgramRandomPrune.class, mutationPolicy);

		final ProgramRandomPrune programRandomPrune = (ProgramRandomPrune) mutationPolicy;
		final double populationMutationProbability = programRandomPrune.populationMutationProbability();

		return new ProgramRandomPruneMutator(programHelper, random, genotypeSpec, populationMutationProbability);
	}
}