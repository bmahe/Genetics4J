package net.bmahe.genetics4j.gp.chromosomes.factory;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactory;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.Program;
import net.bmahe.genetics4j.gp.ProgramGenerator;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;

public class ProgramTreeChromosomeFactory implements ChromosomeFactory<TreeChromosome<Operation>> {

	private final ProgramGenerator programGenerator;

	public ProgramTreeChromosomeFactory(final ProgramGenerator _programGenerator) {
		Validate.notNull(_programGenerator);

		this.programGenerator = _programGenerator;
	}

	@Override
	public boolean canHandle(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);

		return chromosomeSpec instanceof ProgramTreeChromosomeSpec;
	}

	@Override
	public TreeChromosome<Operation> generate(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);

		final ProgramTreeChromosomeSpec ptcs = (ProgramTreeChromosomeSpec) chromosomeSpec;
		final Program program = ptcs.program();

		final TreeNode<Operation> generatedProgram = programGenerator.generate(program);
		return new TreeChromosome<Operation>(generatedProgram);
	}
}