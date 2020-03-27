package net.bmahe.genetics4j.gp.math;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.InputSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.OperationFactories;
import net.bmahe.genetics4j.gp.OperationFactory;
import net.bmahe.genetics4j.gp.spec.mutation.ImmutableRule;
import net.bmahe.genetics4j.gp.spec.mutation.Rule;

public class SimplificationRules {
	protected static boolean isOperation(final TreeNode<Operation<?>> node, final String name) {
		Validate.notNull(node);
		Validate.notBlank(name);
		return name.equals(node.getData()
				.getName());
	}

	protected static boolean hasChildOperation(final TreeNode<Operation<?>> node, final int childIndex,
			final String name) {
		Validate.notNull(node);
		Validate.isTrue(childIndex >= 0);
		Validate.notBlank(name);

		if (node.getChildren()
				.size() <= childIndex) {
			return false;
		}

		final TreeNode<Operation<?>> child = node.getChild(childIndex);
		return name.equals(child.getData()
				.getName());
	}

	protected static <T> T getChildAs(final TreeNode<Operation<?>> node, final int childIndex, final Class<T> clazz) {
		final TreeNode<Operation<?>> child = node.getChild(childIndex);
		final Operation<?> operation = child.getData();
		return (T) operation;
	}

	final public static Rule ADD_TWO_COEFFCIENTS = ImmutableRule.of((t) -> isOperation(t, Functions.NAME_ADD)
			&& hasChildOperation(t, 0, Terminals.TYPE_COEFFICIENT) && hasChildOperation(t, 1, Terminals.TYPE_COEFFICIENT),
			(program, t) -> {

				final InputSpec inputSpec = program.inputSpec();

				final CoefficientOperation<Double> firstCoefficient = getChildAs(t, 0, CoefficientOperation.class);
				final Double firstValue = firstCoefficient.value();

				final CoefficientOperation<Double> secondCoefficient = getChildAs(t, 1, CoefficientOperation.class);
				final Double secondValue = secondCoefficient.value();

				final OperationFactory coefficientFactory = OperationFactories
						.ofCoefficient(Terminals.TYPE_COEFFICIENT, Double.class, firstValue + secondValue);

				final Operation newOperation = coefficientFactory.build(inputSpec);

				return new TreeNode<Operation<?>>(newOperation);
			});

	final public static Rule MUL_TWO_COEFFICIENTS = ImmutableRule.of((t) -> isOperation(t, Functions.NAME_MUL)
			&& hasChildOperation(t, 0, Terminals.TYPE_COEFFICIENT) && hasChildOperation(t, 1, Terminals.TYPE_COEFFICIENT),
			(program, t) -> {

				final InputSpec inputSpec = program.inputSpec();

				final CoefficientOperation<Double> firstCoefficient = getChildAs(t, 0, CoefficientOperation.class);
				final Double firstValue = firstCoefficient.value();

				final CoefficientOperation<Double> secondCoefficient = getChildAs(t, 1, CoefficientOperation.class);
				final Double secondValue = secondCoefficient.value();

				final OperationFactory coefficientFactory = OperationFactories
						.ofCoefficient(Terminals.TYPE_COEFFICIENT, Double.class, firstValue * secondValue);

				final Operation newOperation = coefficientFactory.build(inputSpec);

				return new TreeNode<Operation<?>>(newOperation);
			});

	final public static Rule SUB_TWO_COEFFICIENTS = ImmutableRule.of((t) -> isOperation(t, Functions.NAME_SUB)
			&& hasChildOperation(t, 0, Terminals.TYPE_COEFFICIENT) && hasChildOperation(t, 1, Terminals.TYPE_COEFFICIENT),
			(program, t) -> {

				final InputSpec inputSpec = program.inputSpec();

				final CoefficientOperation<Double> firstCoefficient = getChildAs(t, 0, CoefficientOperation.class);
				final Double firstValue = firstCoefficient.value();

				final CoefficientOperation<Double> secondCoefficient = getChildAs(t, 1, CoefficientOperation.class);
				final Double secondValue = secondCoefficient.value();

				final OperationFactory coefficientFactory = OperationFactories
						.ofCoefficient(Terminals.TYPE_COEFFICIENT, Double.class, firstValue - secondValue);

				final Operation newOperation = coefficientFactory.build(inputSpec);

				return new TreeNode<Operation<?>>(newOperation);
			});

	final public static Rule SUB_INPUT_FROM_SAME_INPUT = ImmutableRule.of((t) -> {
		if (isOperation(t, Functions.NAME_SUB) == false) {
			return false;
		}

		if (hasChildOperation(t, 0, Terminals.TYPE_INPUT) == false) {
			return false;
		}

		if (hasChildOperation(t, 1, Terminals.TYPE_INPUT) == false) {
			return false;
		}

		final InputOperation<?> firstInput = (InputOperation<Double>) t.getChild(0)
				.getData();

		final InputOperation<?> secondInput = (InputOperation<Double>) t.getChild(1)
				.getData();

		return firstInput.index() == secondInput.index();
	}, (program, t) -> {

		final InputSpec inputSpec = program.inputSpec();

		final OperationFactory coefficientFactory = OperationFactories
				.ofCoefficient(Terminals.TYPE_COEFFICIENT, Double.class, 0.0d);

		final Operation newOperation = coefficientFactory.build(inputSpec);

		return new TreeNode<Operation<?>>(newOperation);
	});

	final public static Rule SUB_ZERO_FROM_INPUT = ImmutableRule.of((t) -> {
		if (isOperation(t, Functions.NAME_SUB) == false) {
			return false;
		}

		if (hasChildOperation(t, 0, Terminals.TYPE_INPUT) == false) {
			return false;
		}

		if (hasChildOperation(t, 1, Terminals.TYPE_INPUT) == false) {
			return false;
		}

		if (hasChildOperation(t, 0, Terminals.TYPE_COEFFICIENT) == false) {
			return false;
		}

		if (hasChildOperation(t, 1, Terminals.TYPE_COEFFICIENT) == false) {
			return false;
		}

		final CoefficientOperation<Double> firstCoefficient = (CoefficientOperation<Double>) t.getChild(0)
				.getData();

		return firstCoefficient.value() < 0.0001 && firstCoefficient.value() > -0.0001;
	}, (program, t) -> {

		final InputSpec inputSpec = program.inputSpec();

		final OperationFactory coefficientFactory = OperationFactories
				.ofCoefficient(Terminals.TYPE_COEFFICIENT, Double.class, 0.0d);

		final Operation newOperation = coefficientFactory.build(inputSpec);

		return new TreeNode<Operation<?>>(newOperation);
	});

	final public static Rule DIV_TWO_COEFFICIENT_FINITE = ImmutableRule.of((t) -> {
		if (isOperation(t, Functions.NAME_DIV) == false) {
			return false;
		}

		if (hasChildOperation(t, 0, Terminals.TYPE_COEFFICIENT) == false) {
			return false;
		}

		if (hasChildOperation(t, 1, Terminals.TYPE_COEFFICIENT) == false) {
			return false;
		}

		final CoefficientOperation<Double> firstCoefficient = (CoefficientOperation<Double>) t.getChild(0)
				.getData();

		final CoefficientOperation<Double> secondCoefficient = (CoefficientOperation<Double>) t.getChild(1)
				.getData();

		return Double.isFinite(firstCoefficient.value() / secondCoefficient.value());
	}, (program, t) -> {

		final InputSpec inputSpec = program.inputSpec();

		final CoefficientOperation<Double> firstCoefficient = getChildAs(t, 0, CoefficientOperation.class);
		final Double firstValue = firstCoefficient.value();

		final CoefficientOperation<Double> secondCoefficient = getChildAs(t, 1, CoefficientOperation.class);
		final Double secondValue = secondCoefficient.value();

		final OperationFactory coefficientFactory = OperationFactories
				.ofCoefficient(Terminals.TYPE_COEFFICIENT, Double.class, firstValue / secondValue);

		final Operation newOperation = coefficientFactory.build(inputSpec);

		return new TreeNode<Operation<?>>(newOperation);
	});

	final public static List<Rule> SIMPLIFY_RULES = Arrays.asList(ADD_TWO_COEFFCIENTS,
			MUL_TWO_COEFFICIENTS,
			SUB_TWO_COEFFICIENTS,
			SUB_INPUT_FROM_SAME_INPUT,
			SUB_ZERO_FROM_INPUT,
			DIV_TWO_COEFFICIENT_FINITE);

}