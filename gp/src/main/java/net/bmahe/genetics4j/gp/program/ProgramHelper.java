package net.bmahe.genetics4j.gp.program;

import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.gp.OperationFactory;

public class ProgramHelper {

	private final RandomGenerator randomGenerator;

	public ProgramHelper(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	public OperationFactory pickRandomFunction(final Program program) {
		Validate.notNull(program);
		Validate.isTrue(program.functions().size() > 0);

		final Set<OperationFactory> functions = program.functions();
		return functions.stream().skip(randomGenerator.nextInt(functions.size())).findFirst().get();
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

		return candidates.get(randomGenerator.nextInt(candidates.size()));
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

		return candidates.get(randomGenerator.nextInt(candidates.size()));
	}

	public OperationFactory pickRandomTerminal(final Program program) {
		Validate.notNull(program);

		final Set<OperationFactory> terminals = program.terminal();
		final List<OperationFactory> candidates = terminals.stream().collect(Collectors.toList());

		return candidates.get(randomGenerator.nextInt(candidates.size()));
	}

	public OperationFactory pickRandomFunctionOrTerminal(final Program program) {
		Validate.notNull(program);

		final Set<OperationFactory> terminals = program.terminal();
		final Set<OperationFactory> functions = program.functions();
		final int totalNumberCandidates = terminals.size() + functions.size();

		final Stream<OperationFactory> candidates = Stream.concat(terminals.stream(), functions.stream());
		final int chosenCandidate = randomGenerator.nextInt(totalNumberCandidates);

		return candidates.skip(chosenCandidate).findFirst().get();
	}

	public <T> OperationFactory pickRandomFunctionOrTerminal(final Program program, final Class<T> requiredClass) {
		Validate.notNull(program);
		Validate.notNull(requiredClass);

		final Set<OperationFactory> terminals = program.terminal();
		final Set<OperationFactory> functions = program.functions();

		final Stream<OperationFactory> candidates = Stream.concat(terminals.stream(), functions.stream());

		@SuppressWarnings("unchecked")
		final List<OperationFactory> filteredCandidates = candidates
				.filter((operationFactory) -> operationFactory.returnedType().isAssignableFrom(requiredClass))
				.collect(Collectors.toList());

		final int chosenCandidate = randomGenerator.nextInt(filteredCandidates.size());

		return filteredCandidates.get(chosenCandidate);
	}

}