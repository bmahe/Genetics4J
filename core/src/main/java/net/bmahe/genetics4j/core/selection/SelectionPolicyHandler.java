package net.bmahe.genetics4j.core.selection;

import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public interface SelectionPolicyHandler {
	boolean canHandle(SelectionPolicy selectionPolicy);

	Selector resolve(GeneticSystemDescriptor geneticSystemDescriptor, GenotypeSpec genotypeSpec,
			SelectionPolicyHandlerResolver selectionPolicyHandlerResolver, SelectionPolicy selectionPolicy);
}