package net.bmahe.genetics4j.gp;

import java.util.List;
import java.util.function.BiFunction;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Represents an operation (function or terminal) in genetic programming.
 * 
 * <p>An operation defines a computational unit that can be used as a node in genetic programming trees.
 * Operations can be either functions (with arguments) or terminals (without arguments). Each operation
 * has a defined signature including the types it accepts as input and the type it returns.
 * 
 * <p>Operations are the building blocks of genetic programming expressions and define:
 * <ul>
 * <li>The computation to perform</li>
 * <li>Type constraints for strongly-typed GP</li>
 * <li>Arity (number of arguments)</li>
 * <li>Whether it's a terminal or function</li>
 * </ul>
 * 
 * <p>Common operation types include:
 * <ul>
 * <li><strong>Mathematical functions</strong>: +, -, *, /, sin, cos, exp</li>
 * <li><strong>Logical functions</strong>: AND, OR, NOT, IF-THEN-ELSE</li>
 * <li><strong>Terminals</strong>: constants, variables, input values</li>
 * <li><strong>Domain-specific</strong>: problem-specific functions and operators</li>
 * </ul>
 * 
 * @param <T> the base type used for computation in this operation
 * @see OperationFactory
 * @see net.bmahe.genetics4j.gp.program.Program
 */
@Value.Immutable
public abstract class Operation<T> {

	/**
	 * Returns the name of this operation.
	 * 
	 * @return the operation name, used for identification and display
	 */
	@Parameter
	public abstract String getName();

	/**
	 * Returns the list of types that this operation accepts as arguments.
	 * 
	 * <p>For strongly-typed genetic programming, this defines the type constraints
	 * for each argument position. The list size determines the operation's arity.
	 * 
	 * @return the list of accepted argument types, empty for terminals
	 */
	@SuppressWarnings("rawtypes")
	@Parameter
	public abstract List<Class> acceptedTypes();

	/**
	 * Returns the type that this operation returns.
	 * 
	 * @return the return type of this operation
	 */
	@SuppressWarnings("rawtypes")
	@Parameter
	public abstract Class returnedType();

	/**
	 * Returns the computation function for this operation.
	 * 
	 * @return a function that takes input arguments and parameters and returns the computed result
	 */
	@Parameter
	@Value.Auxiliary
	public abstract BiFunction<T[], Object[], Object> compute();

	/**
	 * Returns a human-readable name for this operation.
	 * 
	 * <p>By default, this returns the same value as {@link #getName()}, but can be
	 * overridden to provide more descriptive names for display purposes.
	 * 
	 * @return the pretty name for display purposes
	 */
	@Value.Default
	public String getPrettyName() {
		return getName();
	}

	/**
	 * Applies this operation to the given input and parameters.
	 * 
	 * @param input the input arguments to the operation
	 * @param parameters additional parameters for the operation
	 * @return the result of applying this operation
	 */
	public Object apply(final T[] input, final Object[] parameters) {
		final BiFunction<T[], Object[], Object> function = compute();
		return function.apply(input, parameters);
	}

	/**
	 * Returns the arity (number of arguments) of this operation.
	 * 
	 * @return the number of arguments this operation accepts
	 */
	public int getArity() {
		return acceptedTypes().size();
	}

	/**
	 * Checks if this operation is a terminal (has no arguments).
	 * 
	 * @return {@code true} if this operation takes no arguments, {@code false} otherwise
	 */
	public boolean isTerminal() {
		return getArity() == 0;
	}
}