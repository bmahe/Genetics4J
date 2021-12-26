package net.bmahe.genetics4j.moo.nsga2.impl;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.moo.nsga2.spec.TournamentNSGA2Selection;

public class TournamentNSGA2SelectionPolicyHandler<T extends Comparable<T>> implements SelectionPolicyHandler<T> {

	private final RandomGenerator randomGenerator;

	public TournamentNSGA2SelectionPolicyHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		return selectionPolicy instanceof TournamentNSGA2Selection<?>;
	}

	@Override
	public Selector<T> resolve(final EAExecutionContext<T> EASystemDescriptor, final AbstractEAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final SelectionPolicy selectionPolicy) {
		Validate.notNull(EASystemDescriptor);
		Validate.notNull(eaConfiguration);
		Validate.notNull(selectionPolicyHandlerResolver);
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(TournamentNSGA2Selection.class, selectionPolicy);

		final TournamentNSGA2Selection<T> tournamentNsga2Spec = (TournamentNSGA2Selection<T>) selectionPolicy;

		return new TournamentNSGA2Selector<T>(randomGenerator, tournamentNsga2Spec);
	}
}