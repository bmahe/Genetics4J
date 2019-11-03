package net.bmahe.genetics4j.core.programming;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import net.bmahe.genetics4j.core.programming.ImmutableProgram.Builder;
import net.bmahe.genetics4j.core.programming.math.Functions;
import net.bmahe.genetics4j.core.programming.math.Terminals;

public class SimpleGPTest {

	private OperationFactory pickRandomFunction(final Random random, Program program) {

		final Set<OperationFactory> functions = program.functions();
		return functions.stream()
				.skip(random.nextInt(functions.size()))
				.findFirst()
				.get();
	}

	@SuppressWarnings("unchecked")
	private OperationFactory pickRandomFunction(final Random random, final Program program, final Class requiredClass) {

		final Set<OperationFactory> functions = program.functions();
		final List<OperationFactory> candidates = functions.stream()
				.filter((operationFactory) -> operationFactory.returnedType()
						.isAssignableFrom(requiredClass))
				.collect(Collectors.toList());

		if (candidates == null || candidates.isEmpty()) {
			throw new IllegalArgumentException("Could not find a suitable function returning a " + requiredClass);
		}

		return candidates.get(random.nextInt(candidates.size()));
	}

	@SuppressWarnings("unchecked")
	private OperationFactory pickRandomTerminal(final Random random, final Program program, final Class requiredClass) {

		final Set<OperationFactory> terminals = program.terminal();
		final List<OperationFactory> candidates = terminals.stream()
				.filter((operationFactory) -> operationFactory.returnedType()
						.isAssignableFrom(requiredClass))
				.collect(Collectors.toList());

		if (candidates == null || candidates.isEmpty()) {
			throw new IllegalArgumentException("Could not find a suitable terminal returning a " + requiredClass);
		}

		return candidates.get(random.nextInt(candidates.size()));
	}

	@SuppressWarnings("unchecked")
	private OperationFactory pickRandomTerminal(final Random random, final Program program) {

		final Set<OperationFactory> terminals = program.terminal();
		final List<OperationFactory> candidates = terminals.stream()
				.collect(Collectors.toList());

		if (candidates == null || candidates.isEmpty()) {
			throw new IllegalArgumentException("Could not find a suitable terminal ");
		}

		return candidates.get(random.nextInt(candidates.size()));
	}

	private Operation generate(final Random random, final Program program, Class acceptedType, int maxHeight,
			int height) {

		OperationFactory currentNode = height < maxHeight && random.nextDouble() < 0.5
				? pickRandomFunction(random, program, acceptedType)
				: pickRandomTerminal(random, program, acceptedType);

		Class[] acceptedTypes = currentNode.acceptedTypes();

		final Operation[] input = new Operation[acceptedTypes.length];

		for (int i = 0; i < acceptedTypes.length; i++) {
			final Class childAcceptedType = acceptedTypes[i];
			Operation operation = generate(random, program, childAcceptedType, maxHeight, height + 1);
			input[i] = operation;

		}

		return currentNode.build(input);
	}

	private Operation generate(final Random random, final Program program, int maxHeight) {

		OperationFactory currentNode = random.nextDouble() < 0.98 ? pickRandomFunction(random, program)
				: pickRandomTerminal(random, program);

		Class[] acceptedTypes = currentNode.acceptedTypes();

		final Operation[] input = new Operation[acceptedTypes.length];

		for (int i = 0; i < acceptedTypes.length; i++) {
			final Class acceptedType = acceptedTypes[i];
			Operation operation = generate(random, program, acceptedType, maxHeight, 1);
			input[i] = operation;

		}

		return currentNode.build(input);
	}

	@Test
	public void simple() {
		final Random random = new Random();
		final Builder programBuilder = ImmutableProgram.builder();
		programBuilder
				.addFunctions(Functions.ADD, Functions.MUL, Functions.DIV, Functions.SUB, Functions.COS, Functions.SIN);
		programBuilder.addTerminal(Terminals.PI, Terminals.E, Terminals.Coefficient(random, 0, 100));

		programBuilder.inputSpec(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));

		final ImmutableProgram program = programBuilder.build();

		final Operation operation = generate(random, program, 3);
		System.out.println(operation);
	}

}