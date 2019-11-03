package net.bmahe.genetics4j.core.programming.math;

import net.bmahe.genetics4j.core.programming.OperationFactories;
import net.bmahe.genetics4j.core.programming.OperationFactory;

public final class Functions {

	public static OperationFactory ADD = OperationFactories
			.ofBiFunction("ADD", Double.class, Double.class, Double.class, (Double i0, Double i1) -> i0 + i1);

	public static OperationFactory SUB = OperationFactories
			.ofBiFunction("SUB", Double.class, Double.class, Double.class, (Double i0, Double i1) -> i0 - i1);

	public static OperationFactory MUL = OperationFactories
			.ofBiFunction("MUL", Double.class, Double.class, Double.class, (Double i0, Double i1) -> i0 * i1);

	public static OperationFactory DIV = OperationFactories
			.ofBiFunction("DIV", Double.class, Double.class, Double.class, (Double i0, Double i1) -> i0 / i1);

	public static OperationFactory COS = OperationFactories
			.ofFunction("COS", Double.class, Double.class, (Double i0) -> Math.cos(i0));

	public static OperationFactory SIN = OperationFactories
			.ofFunction("SIN", Double.class, Double.class, (Double i0) -> Math.sin(i0));
}