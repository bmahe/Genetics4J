package net.bmahe.genetics4j.core.selection;

import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public interface SelectionPolicyHandler<T extends Comparable<T>> {
	
	boolean canHandle(SelectionPolicy selectionPolicy);

	Selector<T> resolve(GeneticSystemDescriptor<T> geneticSystemDescriptor, GenotypeSpec<T> genotypeSpec,
			SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver, SelectionPolicy selectionPolicy);
}