package net.bmahe.genetics4j.gp.math;

import net.bmahe.genetics4j.gp.OperationFactories;
import net.bmahe.genetics4j.gp.OperationFactory;

public final class Functions {

	public static final String NAME_ADD = "Add";
	public static final String NAME_SUB = "Sub";
	public static final String NAME_MUL = "Mul";
	public static final String NAME_DIV = "Div";
	public static final String NAME_COS = "Cos";
	public static final String NAME_SIN = "Sin";
	public static final String NAME_EXP = "Exp";
	public static final String NAME_STR_TO_DOUBLE = "StrToDouble";

	public static final OperationFactory COS = OperationFactories
			.ofUnary(NAME_COS, Double.class, Double.class, Math::cos);
	public static final OperationFactory SIN = OperationFactories
			.ofUnary(NAME_SIN, Double.class, Double.class, Math::sin);
	public static final OperationFactory EXP = OperationFactories
			.ofUnary(NAME_EXP, Double.class, Double.class, Math::exp);

	public static final OperationFactory STR_TO_DOUBLE = OperationFactories
			.ofUnary(NAME_STR_TO_DOUBLE, String.class, Double.class, Double::parseDouble);

	public static final OperationFactory ADD = OperationFactories
			.ofBinary(NAME_ADD, Double.class, Double.class, Double.class, (a, b) -> a + b);

	public static final OperationFactory SUB = OperationFactories
			.ofBinary(NAME_SUB, Double.class, Double.class, Double.class, (a, b) -> a - b);

	public static final OperationFactory MUL = OperationFactories
			.ofBinary(NAME_MUL, Double.class, Double.class, Double.class, (a, b) -> a * b);

	public static final OperationFactory DIV = OperationFactories
			.ofBinary(NAME_DIV, Double.class, Double.class, Double.class, (a, b) -> a / b);
}