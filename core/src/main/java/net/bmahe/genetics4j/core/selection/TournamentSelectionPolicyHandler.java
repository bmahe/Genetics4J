package net.bmahe.genetics4j.core.selection;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.Tournament;

public class TournamentSelectionPolicyHandler<T extends Comparable<T>> implements SelectionPolicyHandler<T> {
	public static final Logger logger = LogManager.getLogger(TournamentSelectionPolicyHandler.class);

	private final RandomGenerator randomGenerator;

	public TournamentSelectionPolicyHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);

		return selectionPolicy instanceof Tournament;
	}

	@Override
	public Selector<T> resolve(final AbstractEAExecutionContext<T> eaExecutionContext,
			final AbstractEAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(Tournament.class, selectionPolicy);

		return new TournamentSelector<>(selectionPolicy, randomGenerator);
	}
}