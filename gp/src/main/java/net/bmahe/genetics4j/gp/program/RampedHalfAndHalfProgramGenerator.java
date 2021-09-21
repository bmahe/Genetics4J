package net.bmahe.genetics4j.gp.program;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.Operation;

public class RampedHalfAndHalfProgramGenerator implements ProgramGenerator {

	private final RandomGenerator randomGenerator;
	private final ProgramHelper programHelper;

	private final ProgramGenerator programGenerator;

	public RampedHalfAndHalfProgramGenerator(final RandomGenerator _randomGenerator, final ProgramHelper _programHelper) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_programHelper);

		this.randomGenerator = _randomGenerator;
		this.programHelper = _programHelper;

		final GrowProgramGenerator growProgramGenerator = new GrowProgramGenerator(programHelper);
		final FullProgramGenerator fullProgramGenerator = new FullProgramGenerator(programHelper);

		this.programGenerator = new MultiProgramGenerator(randomGenerator, List.of(fullProgramGenerator, growProgramGenerator));
	}

	@Override
	public TreeNode<Operation<?>> generate(final Program program) {
		Validate.notNull(program);

		return programGenerator.generate(program);
	}

	@Override
	public <T> TreeNode<Operation<T>> generate(final Program program, final int maxDepth) {
		Validate.notNull(program);
		Validate.isTrue(maxDepth > 0);

		final int newMaxDepth = 1 + randomGenerator.nextInt(maxDepth);
		return programGenerator.generate(program, newMaxDepth);
	}

	@Override
	public <T, U> TreeNode<Operation<T>> generate(final Program program, final int maxDepth, final Class<U> rootType) {
		Validate.notNull(program);
		Validate.notNull(rootType);
		Validate.isTrue(maxDepth > 0);

		final int newMaxDepth = 1 + randomGenerator.nextInt(maxDepth);
		return programGenerator.generate(program, newMaxDepth, rootType);
	}

}