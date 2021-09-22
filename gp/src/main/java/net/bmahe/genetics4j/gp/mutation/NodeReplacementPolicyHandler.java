package net.bmahe.genetics4j.gp.mutation;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.spec.mutation.NodeReplacement;

public class NodeReplacementPolicyHandler implements MutationPolicyHandler {

	final RandomGenerator randomGenerator;
	final ProgramHelper programHelper;

	public NodeReplacementPolicyHandler(final RandomGenerator _randomGenerator, final ProgramHelper _programHelper) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_programHelper);

		this.randomGenerator = _randomGenerator;
		this.programHelper = _programHelper;
	}

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof NodeReplacement;
	}

	@Override
	public Mutator createMutator(final EAExecutionContext eaExecutionContext, final EAConfiguration eaConfiguration,
			final MutationPolicyHandlerResolver mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(NodeReplacement.class, mutationPolicy);

		final NodeReplacement nodeReplacementMutate = (NodeReplacement) mutationPolicy;
		final double populationMutationProbability = nodeReplacementMutate.populationMutationProbability();

		return new NodeReplacementMutator(programHelper, randomGenerator, eaConfiguration, populationMutationProbability);
	}
}