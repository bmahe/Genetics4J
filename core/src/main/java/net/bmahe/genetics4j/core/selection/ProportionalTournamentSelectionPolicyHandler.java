package net.bmahe.genetics4j.core.selection;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.ProportionalTournament;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class ProportionalTournamentSelectionPolicyHandler<T extends Comparable<T>>
		implements SelectionPolicyHandler<T> {
	private final RandomGenerator randomGenerator;

	public ProportionalTournamentSelectionPolicyHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);

		return selectionPolicy instanceof ProportionalTournament;
	}

	@Override
	public Selector<T> resolve(final EAExecutionContext<T> eaExecutionContext, final EAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final SelectionPolicy selectionPolicy) {

		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(ProportionalTournament.class, selectionPolicy);

		return new ProportionalTournamentSelector<T>(selectionPolicy, randomGenerator);
	}
}