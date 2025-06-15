package net.bmahe.genetics4j.gp;

/**
 * Factory interface for creating operations in genetic programming.
 * 
 * <p>An OperationFactory provides a way to create {@link Operation} instances with type information
 * and context-dependent parameters. Factories are particularly useful when operations need to be
 * configured based on input specifications or when operations require runtime information.
 * 
 * <p>The factory pattern allows for:
 * <ul>
 * <li>Dynamic operation creation based on input specifications</li>
 * <li>Type-safe operation construction in strongly-typed GP</li>
 * <li>Parameterized operations that adapt to problem context</li>
 * <li>Lazy evaluation of expensive operation setup</li>
 * </ul>
 * 
 * <p>Common use cases include:
 * <ul>
 * <li>Creating input operations that depend on input variable count</li>
 * <li>Building coefficient operations with domain-specific ranges</li>
 * <li>Constructing type-specific mathematical operations</li>
 * <li>Generating random constants within specified bounds</li>
 * </ul>
 * 
 * @see Operation
 * @see InputSpec
 * @see OperationFactories
 */
@SuppressWarnings("rawtypes")
public interface OperationFactory {

	/**
	 * Returns the types that operations created by this factory accept as arguments.
	 * 
	 * <p>This defines the type signature for the operation's arguments and is used
	 * for type checking in strongly-typed genetic programming.
	 * 
	 * @return an array of classes representing the accepted argument types
	 */
	//TODO make a List<Class>
	Class[] acceptedTypes();

	/**
	 * Returns the type that operations created by this factory return.
	 * 
	 * @return the return type of operations created by this factory
	 */
	Class returnedType();

	/**
	 * Creates a new operation instance using the provided input specification.
	 * 
	 * <p>The input specification provides context about the problem domain,
	 * such as the number and types of input variables, which can be used
	 * to customize the created operation.
	 * 
	 * @param inputSpec the input specification providing context for operation creation
	 * @return a new operation instance configured according to the input specification
	 */
	Operation build(final InputSpec inputSpec);
}