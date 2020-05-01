package net.bmahe.genetics4j.moo;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder;
import net.bmahe.genetics4j.moo.nsga2.impl.NSGA2SelectionPolicyHandler;
import net.bmahe.genetics4j.moo.nsga2.impl.TournamentNSGA2SelectionPolicyHandler;

public class MOOEAExecutionContexts {

	private MOOEAExecutionContexts() {
	}

	public static <T extends Comparable<T>> Builder<T> enrichWithMOO(final Builder<T> builder) {
		Validate.notNull(builder);

		builder.addSelectionPolicyHandlerFactories((gsd) -> new NSGA2SelectionPolicyHandler<T>(),
				gsd -> new TournamentNSGA2SelectionPolicyHandler<T>(gsd.random()));
		return builder;
	}

}