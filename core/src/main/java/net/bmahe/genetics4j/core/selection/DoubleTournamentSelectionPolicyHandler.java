package net.bmahe.genetics4j.core.selection;

import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.DoubleTournament;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class DoubleTournamentSelectionPolicyHandler<T extends Comparable<T>> implements SelectionPolicyHandler<T> {
	private final Random random;

	public DoubleTournamentSelectionPolicyHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);

		return selectionPolicy instanceof DoubleTournament;
	}

	@Override
	public Selector<T> resolve(final EAExecutionContext<T> eaExecutionContext, final EAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(DoubleTournament.class, selectionPolicy);

		return new DoubleTournamentSelector<T>(selectionPolicy, random);
	}
}