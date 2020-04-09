package net.bmahe.genetics4j.gp.program;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.gp.OperationFactory;

public class ProgramHelper {

	private final Random random;

	public ProgramHelper(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	public OperationFactory pickRandomFunction(final Program program) {
		Validate.notNull(program);
		Validate.isTrue(program.functions().size() > 0);

		final Set<OperationFactory> functions = program.functions();
		return functions.stream().skip(random.nextInt(functions.size())).findFirst().get();
	}

	public <T> OperationFactory pickRandomFunction(final Program program, final Class<T> requiredClass) {
		Validate.notNull(program);
		Validate.notNull(requiredClass);
		Validate.isTrue(program.functions().size() > 0);

		final Set<OperationFactory> functions = program.functions();
		@SuppressWarnings("unchecked")
		final List<OperationFactory> candidates = functions.stream()
				.filter((operationFactory) -> operationFactory.returnedType().isAssignableFrom(requiredClass))
				.collect(Collectors.toList());

		if (candidates == null || candidates.isEmpty()) {
			throw new IllegalArgumentException("Could not find a suitable function returning a " + requiredClass);
		}

		return candidates.get(random.nextInt(candidates.size()));
	}

	public <T> OperationFactory pickRandomTerminal(final Program program, final Class<T> requiredClass) {
		Validate.notNull(program);
		Validate.notNull(requiredClass);

		final Set<OperationFactory> terminals = program.terminal();
		@SuppressWarnings("unchecked")
		final List<OperationFactory> candidates = terminals.stream()
				.filter((operationFactory) -> operationFactory.returnedType().isAssignableFrom(requiredClass))
				.collect(Collectors.toList());

		if (candidates == null || candidates.isEmpty()) {
			throw new IllegalArgumentException("Could not find a suitable terminal returning a " + requiredClass);
		}

		return candidates.get(random.nextInt(candidates.size()));
	}

	public OperationFactory pickRandomTerminal(final Program program) {
		Validate.notNull(program);

		final Set<OperationFactory> terminals = program.terminal();
		final List<OperationFactory> candidates = terminals.stream().collect(Collectors.toList());

		if (candidates == null || candidates.isEmpty()) {
			throw new IllegalArgumentException("Could not find a suitable terminal ");
		}

		return candidates.get(random.nextInt(candidates.size()));
	}

}