package net.bmahe.genetics4j.moo.nsga2.impl;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.TournamentSelectionPolicyHandler;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.SelectionPolicyHandlerFactory;

public class TournamentNSGA2SelectionPolicyHandlerFactory<T extends Comparable<T>>
		implements SelectionPolicyHandlerFactory<T>
{

	@Override
	public SelectionPolicyHandler<T> apply(final AbstractEAExecutionContext<T> abstractEAExecutionContext) {
		Validate.notNull(abstractEAExecutionContext);

		final RandomGenerator randomGenerator = abstractEAExecutionContext.randomGenerator();
		return new TournamentSelectionPolicyHandler<T>(randomGenerator);
	}
}