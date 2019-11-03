package net.bmahe.genetics4j.core.programming.math;

import net.bmahe.genetics4j.core.programming.Operation;
import net.bmahe.genetics4j.core.programming.OperationFactory;

public class AdditionFactory implements OperationFactory {

	private final static Class[] ACCEPTED_TYPES = new Class[] { Double.class, Double.class };

	@Override
	public Class[] acceptedTypes() {
		return ACCEPTED_TYPES;
	}

	@Override
	public Class returnedType() {
		return Double.class;
	}

	@Override
	public Operation build(Operation[] input) {
		return null;
	}

}
