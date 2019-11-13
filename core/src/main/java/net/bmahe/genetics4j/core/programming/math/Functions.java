package net.bmahe.genetics4j.core.programming.math;

import net.bmahe.genetics4j.core.programming.OperationFactories;
import net.bmahe.genetics4j.core.programming.OperationFactory;

public final class Functions {

	public static final OperationFactory COS = OperationFactories.ofUnary("Cos", Double.class, Double.class, Math::cos);
	public static final OperationFactory SIN = OperationFactories.ofUnary("Sin", Double.class, Double.class, Math::sin);
	public static final OperationFactory EXP = OperationFactories.ofUnary("Exp", Double.class, Double.class, Math::exp);

	public static final OperationFactory ADD = OperationFactories
			.ofBinary("Add", Double.class, Double.class, Double.class, (a, b) -> a + b);

	public static final OperationFactory SUB = OperationFactories
			.ofBinary("Sub", Double.class, Double.class, Double.class, (a, b) -> a - b);

	public static final OperationFactory MUL = OperationFactories
			.ofBinary("Mul", Double.class, Double.class, Double.class, (a, b) -> a * b);

	public static final OperationFactory DIV = OperationFactories
			.ofBinary("Div", Double.class, Double.class, Double.class, (a, b) -> a / b);
}