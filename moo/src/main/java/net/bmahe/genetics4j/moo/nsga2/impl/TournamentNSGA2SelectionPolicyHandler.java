package net.bmahe.genetics4j.moo.nsga2.impl;

import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.moo.nsga2.spec.TournamentNSGA2Selection;

public class TournamentNSGA2SelectionPolicyHandler<T extends Comparable<T>> implements SelectionPolicyHandler<T> {

	private final Random random;

	public TournamentNSGA2SelectionPolicyHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		return selectionPolicy instanceof TournamentNSGA2Selection<?>;
	}

	@Override
	public Selector<T> resolve(final GeneticSystemDescriptor<T> geneticSystemDescriptor,
			final GenotypeSpec<T> genotypeSpec, final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final SelectionPolicy selectionPolicy) {
		Validate.notNull(geneticSystemDescriptor);
		Validate.notNull(genotypeSpec);
		Validate.notNull(selectionPolicyHandlerResolver);
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(TournamentNSGA2Selection.class, selectionPolicy);

		final TournamentNSGA2Selection<T> tournamentNsga2Spec = (TournamentNSGA2Selection<T>) selectionPolicy;

		return new TournamentNSGA2Selector<T>(random, tournamentNsga2Spec);
	}
}