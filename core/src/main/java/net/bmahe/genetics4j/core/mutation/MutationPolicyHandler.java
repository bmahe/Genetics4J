package net.bmahe.genetics4j.core.mutation;

import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public interface MutationPolicyHandler {

	boolean canHandle(MutationPolicyHandlerResolver mutationPolicyHandlerResolver, MutationPolicy mutationPolicy);

	Mutator createMutator(EAExecutionContext eaExecutionContext, EAConfiguration eaConfiguration,
			MutationPolicyHandlerResolver mutationPolicyHandlerResolver, MutationPolicy mutationPolicy);
}