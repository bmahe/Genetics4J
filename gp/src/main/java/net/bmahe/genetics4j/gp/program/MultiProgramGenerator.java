package net.bmahe.genetics4j.gp.program;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.Operation;

public class MultiProgramGenerator implements ProgramGenerator {

	final private List<ProgramGenerator> programGenerators = new ArrayList<ProgramGenerator>();

	final private RandomGenerator randomGenerator;

	public MultiProgramGenerator(final RandomGenerator _randomGenerator,
			final Collection<ProgramGenerator> _programGenerators) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_programGenerators);
		Validate.isTrue(_programGenerators.size() > 0);

		this.randomGenerator = _randomGenerator;
		this.programGenerators.addAll(_programGenerators);
	}

	private ProgramGenerator pickProgramGenerator() {
		final int programGeneratorIndex = randomGenerator.nextInt(programGenerators.size());

		return programGenerators.get(programGeneratorIndex);
	}

	@Override
	public TreeNode<Operation<?>> generate(final Program program) {
		Validate.notNull(program);

		final ProgramGenerator programGenerator = pickProgramGenerator();
		return programGenerator.generate(program);
	}

	@Override
	public <T> TreeNode<Operation<T>> generate(final Program program, final int maxDepth) {
		Validate.notNull(program);
		Validate.isTrue(maxDepth > 0);

		final ProgramGenerator programGenerator = pickProgramGenerator();
		return programGenerator.generate(program, maxDepth);
	}

	@Override
	public <T, U> TreeNode<Operation<T>> generate(final Program program, final int maxDepth, final Class<U> rootType) {
		Validate.notNull(program);
		Validate.isTrue(maxDepth > 0);
		Validate.notNull(program);

		final ProgramGenerator programGenerator = pickProgramGenerator();
		return programGenerator.generate(program, maxDepth, rootType);
	}
}