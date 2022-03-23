package net.bmahe.genetics4j.core.selection;

import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class SelectionPolicyHandlerResolver<T extends Comparable<T>> {
	private final AbstractEAExecutionContext<T> eaExecutionContext;

	private final List<SelectionPolicyHandler<T>> selectionPolicyHandlers;

	public SelectionPolicyHandlerResolver(final AbstractEAExecutionContext<T> _eaExecutionContext) {
		Validate.notNull(_eaExecutionContext);

		this.eaExecutionContext = _eaExecutionContext;
		this.selectionPolicyHandlers = eaExecutionContext.selectionPolicyHandlers();
	}

	public SelectionPolicyHandler<T> resolve(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);

		return selectionPolicyHandlers.stream()
				.dropWhile((sph) -> sph.canHandle(selectionPolicy) == false)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Could not find suitable selection policy handler for policy: " + selectionPolicy));

	}
}