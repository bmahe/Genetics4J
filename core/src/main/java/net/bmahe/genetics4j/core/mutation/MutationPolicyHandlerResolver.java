package net.bmahe.genetics4j.core.mutation;

import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public class MutationPolicyHandlerResolver<T extends Comparable<T>> {

	private final AbstractEAExecutionContext<T> eaExecutionContext;
	private final List<MutationPolicyHandler> mutationPolicyHandlers;

	public MutationPolicyHandlerResolver(final AbstractEAExecutionContext<T> _eaExecutionContext) {
		Validate.notNull(_eaExecutionContext);

		this.eaExecutionContext = _eaExecutionContext;
		mutationPolicyHandlers = eaExecutionContext.mutationPolicyHandlers();
	}

	public boolean canHandle(final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicy);

		return mutationPolicyHandlers.stream()
				.anyMatch((sph) -> sph.canHandle(this, mutationPolicy));
	}

	public MutationPolicyHandler resolve(final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicy);

		return mutationPolicyHandlers.stream()
				.dropWhile((sph) -> sph.canHandle(this, mutationPolicy) == false)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Could not find suitable mutation policy handler for policy: " + mutationPolicy));

	}
}