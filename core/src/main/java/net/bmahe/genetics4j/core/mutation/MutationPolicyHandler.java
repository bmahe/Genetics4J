package net.bmahe.genetics4j.core.mutation;

import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public interface MutationPolicyHandler {

	boolean canHandle(final MutationPolicyHandlerResolver mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy);

	Mutator createMutator(final AbstractEAExecutionContext eaExecutionContext,
			final AbstractEAConfiguration eaConfiguration,
			final MutationPolicyHandlerResolver mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy);
}