package net.bmahe.genetics4j.neat.selection;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.neat.SpeciesIdGenerator;
import net.bmahe.genetics4j.neat.spec.selection.NeatSelection;

public class NeatSelectionPolicyHandler<T extends Number & Comparable<T>> implements SelectionPolicyHandler<T> {
	public static final Logger logger = LogManager.getLogger(NeatSelectionPolicyHandler.class);

	private final RandomGenerator randomGenerator;
	private final SpeciesIdGenerator speciesIdGenerator;

	public NeatSelectionPolicyHandler(final RandomGenerator _randomGenerator,
			final SpeciesIdGenerator _speciesIdGenerator) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_speciesIdGenerator);

		this.randomGenerator = _randomGenerator;
		this.speciesIdGenerator = _speciesIdGenerator;
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);

		return selectionPolicy instanceof NeatSelection;
	}

	@Override
	public Selector<T> resolve(final AbstractEAExecutionContext<T> eaExecutionContext,
			final AbstractEAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(NeatSelection.class, selectionPolicy);

		final NeatSelection<T> neatSelection = (NeatSelection<T>) selectionPolicy;

		final SelectionPolicy speciesSelection = neatSelection.speciesSelection();
		final SelectionPolicyHandler<T> speciesSelectionPolicyHandler = selectionPolicyHandlerResolver
				.resolve(speciesSelection);
		final Selector<T> speciesSelector = speciesSelectionPolicyHandler
				.resolve(eaExecutionContext, eaConfiguration, selectionPolicyHandlerResolver, speciesSelection);

		return new NeatSelectorImpl<>(randomGenerator, neatSelection, speciesIdGenerator, speciesSelector);
	}
}