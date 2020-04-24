package net.bmahe.genetics4j.moo.nsga2.impl;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.moo.nsga2.spec.NSGA2Selection;

public class NSGA2SelectionPolicyHandler<T extends Comparable<T>> implements SelectionPolicyHandler<T> {

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		return selectionPolicy instanceof NSGA2Selection<?>;
	}

	@Override
	public Selector<T> resolve(final GeneticSystemDescriptor<T> geneticSystemDescriptor,
			final GenotypeSpec<T> genotypeSpec, final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final SelectionPolicy selectionPolicy) {
		Validate.notNull(geneticSystemDescriptor);
		Validate.notNull(genotypeSpec);
		Validate.notNull(selectionPolicyHandlerResolver);
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(NSGA2Selection.class, selectionPolicy);

		final NSGA2Selection<T> nsga2Spec = (NSGA2Selection<T>) selectionPolicy;

		return new NSGA2Selector<T>(nsga2Spec);
	}
}