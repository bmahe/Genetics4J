package net.bmahe.genetics4j.gp.mutation;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramApplyRules;
import net.bmahe.genetics4j.gp.spec.mutation.Rule;

public class ProgramRulesApplicatorPolicyHandler implements MutationPolicyHandler {
	final static public Logger logger = LogManager.getLogger(ProgramRulesApplicatorPolicyHandler.class);

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof ProgramApplyRules;
	}

	@Override
	public Mutator createMutator(final GeneticSystemDescriptor geneticSystemDescriptor, final GenotypeSpec genotypeSpec,
			final MutationPolicyHandlerResolver mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(geneticSystemDescriptor);
		Validate.notNull(genotypeSpec);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(ProgramApplyRules.class, mutationPolicy);

		final ProgramApplyRules programApplyRules = (ProgramApplyRules) mutationPolicy;
		final List<Rule> rules = programApplyRules.rules();

		return new ProgramRulesApplicatorMutator(rules, genotypeSpec);
	}
}