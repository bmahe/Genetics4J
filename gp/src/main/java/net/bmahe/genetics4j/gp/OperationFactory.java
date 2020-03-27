package net.bmahe.genetics4j.gp;

@SuppressWarnings("rawtypes")
public interface OperationFactory {

	//TODO make a List<Class>
	Class[] acceptedTypes();

	Class returnedType();

	Operation build(final InputSpec inputSpec);
}