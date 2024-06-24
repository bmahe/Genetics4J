package net.bmahe.genetics4j.core.selection;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class SelectionPolicyHandlerResolver<T extends Comparable<T>> {
	public final static Logger logger = LogManager.getLogger(SelectionPolicyHandlerResolver.class);

	private final AbstractEAExecutionContext<T> eaExecutionContext;

	private final List<SelectionPolicyHandler<T>> selectionPolicyHandlers;

	public SelectionPolicyHandlerResolver(final AbstractEAExecutionContext<T> _eaExecutionContext) {
		Objects.requireNonNull(_eaExecutionContext);

		this.eaExecutionContext = _eaExecutionContext;
		this.selectionPolicyHandlers = eaExecutionContext.selectionPolicyHandlers();
	}

	public SelectionPolicyHandler<T> resolve(final SelectionPolicy selectionPolicy) {
		Objects.requireNonNull(selectionPolicy);

		return selectionPolicyHandlers.stream()
				.dropWhile((sph) -> sph.canHandle(selectionPolicy) == false)
				.findFirst()
				.orElseThrow(() -> {

					if (logger.isDebugEnabled()) {
						logger.debug("Could not find suitable selection policy handler for policy {}", selectionPolicy);
						logger.debug("Currently known selectionPolicyHandlers: {}", selectionPolicyHandlers);
					}

					throw new IllegalStateException(
							"Could not find suitable selection policy handler for policy: " + selectionPolicy);
				});

	}
}