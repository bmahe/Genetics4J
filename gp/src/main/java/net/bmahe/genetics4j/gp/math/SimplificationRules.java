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

	final public static Rule ADD_INPUT_TO_SAME_INPUT = ImmutableRule.of((t) -> {
		boolean result = isOperation(t, Functions.NAME_ADD) && hasChildOperation(t, 0, Terminals.TYPE_INPUT)
				&& hasChildOperation(t, 1, Terminals.TYPE_INPUT);

		if (result == false) {
			return false;
		}

		final InputOperation<?> firstInput = getChildAs(t, 0, InputOperation.class);
		final InputOperation<?> secondInput = getChildAs(t, 1, InputOperation.class);

		return firstInput.index() == secondInput.index();
	}, (program, t) -> {

		final InputSpec inputSpec = program.inputSpec();

		final TreeNode<Operation<?>> multBaseTreeNode = new TreeNode<Operation<?>>(Functions.MUL.build(inputSpec));

		final OperationFactory coefficientFactory = OperationFactories
				.ofCoefficient(Terminals.TYPE_COEFFICIENT, Double.class, 2.0d);
		final TreeNode<Operation<?>> timesTwoTreeNode = new TreeNode<Operation<?>>(coefficientFactory.build(inputSpec));
		multBaseTreeNode.addChild(timesTwoTreeNode);

		final InputOperation<?> firstInput = (InputOperation<Double>) t.getChild(0)
				.getData();
		final TreeNode<Operation<?>> firstInputTreeNode = new TreeNode<Operation<?>>(firstInput);
		multBaseTreeNode.addChild(firstInputTreeNode);

		return multBaseTreeNode;
	});

	final public static Rule MULTIPLY_INPUT_WITH_SAME_INPUT = ImmutableRule.of((t) -> {

		if (isOperation(t, Functions.NAME_MUL) == false) {
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

		final TreeNode<Operation<?>> expBaseTreeNode = new TreeNode<Operation<?>>(Functions.EXP.build(inputSpec));

		final InputOperation<?> firstInput = (InputOperation<Double>) t.getChild(0)
				.getData();
		final TreeNode<Operation<?>> firstInputTreeNode = new TreeNode<Operation<?>>(firstInput);
		expBaseTreeNode.addChild(firstInputTreeNode);

		final OperationFactory coefficientFactory = OperationFactories
				.ofCoefficient(Terminals.TYPE_COEFFICIENT, Double.class, 2.0d);
		final TreeNode<Operation<?>> twoTreeNode = new TreeNode<Operation<?>>(coefficientFactory.build(inputSpec));
		expBaseTreeNode.addChild(twoTreeNode);

		return expBaseTreeNode;
	});

	final public static Rule MULTIPLY_INPUT_WITH_EXP_SAME_INPUT_COEFF = ImmutableRule.of((t) -> {
		// ex: MULT( EXP( INPUT[0], 3), INPUT[0])
		// ==> EXP( INPUT[0], 4)

		if (isOperation(t, Functions.NAME_MUL) == false) {
			return false;
		}
		if (hasChildOperation(t, 0, Functions.NAME_EXP) == false) {
			return false;
		}
		if (hasChildOperation(t, 1, Terminals.TYPE_INPUT) == false) {
			return false;
		}

		final TreeNode<Operation<?>> expTreeNode = t.getChild(0);
		if (hasChildOperation(expTreeNode, 0, Terminals.TYPE_INPUT) == false) {
			return false;
		}
		if (hasChildOperation(expTreeNode, 1, Terminals.TYPE_COEFFICIENT) == false) {
			return false;
		}

		final InputOperation<?> expInput = getChildAs(expTreeNode, 0, InputOperation.class);
		final InputOperation<?> secondInput = getChildAs(t, 1, InputOperation.class);

		return expInput.index() == secondInput.index();
	}, (program, t) -> {

		final InputSpec inputSpec = program.inputSpec();
		final TreeNode<Operation<?>> originalExpTreeNode = t.getChild(0);
		final CoefficientOperation<Double> originalCoefficientExp = getChildAs(originalExpTreeNode,
				1,
				CoefficientOperation.class);

		final TreeNode<Operation<?>> expBaseTreeNode = new TreeNode<Operation<?>>(Functions.EXP.build(inputSpec));

		final InputOperation<?> firstInput = (InputOperation<Double>) t.getChild(0)
				.getData();
		final TreeNode<Operation<?>> firstInputTreeNode = new TreeNode<Operation<?>>(firstInput);
		expBaseTreeNode.addChild(firstInputTreeNode);

		final OperationFactory coefficientFactory = OperationFactories
				.ofCoefficient(Terminals.TYPE_COEFFICIENT, Double.class, originalCoefficientExp.value() + 1.0d);
		final TreeNode<Operation<?>> newCoeffTreeNode = new TreeNode<Operation<?>>(coefficientFactory.build(inputSpec));
		expBaseTreeNode.addChild(newCoeffTreeNode);

		return expBaseTreeNode;
	});

	final public static List<Rule> SIMPLIFY_RULES = Arrays.asList(ADD_TWO_COEFFCIENTS,
			MUL_TWO_COEFFICIENTS,
			SUB_TWO_COEFFICIENTS,
			SUB_INPUT_FROM_SAME_INPUT,
			SUB_ZERO_FROM_INPUT,
			DIV_TWO_COEFFICIENT_FINITE,
			ADD_INPUT_TO_SAME_INPUT,
			MULTIPLY_INPUT_WITH_SAME_INPUT);

}