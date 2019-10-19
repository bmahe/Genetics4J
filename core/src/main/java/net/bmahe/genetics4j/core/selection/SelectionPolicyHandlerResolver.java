package net.bmahe.genetics4j.core.selection;

import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class SelectionPolicyHandlerResolver {
	private final GeneticSystemDescriptor geneticSystemDescriptor;

	private final List<SelectionPolicyHandler> selectionPolicyHandlers;

	public SelectionPolicyHandlerResolver(final GeneticSystemDescriptor _geneticSystemDescriptor) {
		Validate.notNull(_geneticSystemDescriptor);

		this.geneticSystemDescriptor = _geneticSystemDescriptor;
		this.selectionPolicyHandlers = geneticSystemDescriptor.selectionPolicyHandlers();
	}

	public SelectionPolicyHandler resolve(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);

		return selectionPolicyHandlers.stream()
				.dropWhile((sph) -> sph.canHandle(selectionPolicy) == false)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Could not find suitable selection policy handler for policy: " + selectionPolicy));

	}
}