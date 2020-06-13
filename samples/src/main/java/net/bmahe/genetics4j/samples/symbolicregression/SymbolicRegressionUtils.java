package net.bmahe.genetics4j.samples.symbolicregression;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListener;
import net.bmahe.genetics4j.extras.evolutionlisteners.CSVEvolutionListener;
import net.bmahe.genetics4j.extras.evolutionlisteners.ColumnExtractor;
import net.bmahe.genetics4j.extras.evolutionlisteners.EvolutionStep;
import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.Terminals;
import net.bmahe.genetics4j.gp.program.ImmutableProgram;
import net.bmahe.genetics4j.gp.program.ImmutableProgram.Builder;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.utils.TreeNodeUtils;
import net.bmahe.genetics4j.moo.ParetoUtils;

public class SymbolicRegressionUtils {

	private SymbolicRegressionUtils() {
	}

	public static Program buildProgram(final Random random) {
		Validate.notNull(random);

		final Builder programBuilder = ImmutableProgram.builder();
		programBuilder.addFunctions(Functions.ADD, Functions.MUL, Functions.DIV, Functions.SUB, Functions.POW);
		programBuilder.addTerminal(Terminals.InputDouble(random), Terminals.CoefficientRounded(random, -10, 10));

		programBuilder.inputSpec(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		programBuilder.maxDepth(4);
		final Program program = programBuilder.build();

		return program;
	}

	public static double evaluate(final double x) {
		return (6.0 * x * x) - x + 8;
	}

	public static <T extends Comparable<T>> EvolutionListener<T> csvLogger(final String filename,
			final Function<EvolutionStep<T, List<Set<Integer>>>, Double> computeScore,
			final Function<EvolutionStep<T, List<Set<Integer>>>, Double> computeComplexity) {
		Validate.isTrue(StringUtils.isNotBlank(filename));
		Validate.notNull(computeScore);
		Validate.notNull(computeComplexity);

		return CSVEvolutionListener.<T, List<Set<Integer>>>of(filename,
				(generation, population, fitness, isDone) -> ParetoUtils.rankedPopulation(Comparator.reverseOrder(),
						fitness),
				List.of(ColumnExtractor.of("generation", evolutionStep -> evolutionStep.generation()),
						ColumnExtractor.of("score", evolutionStep -> computeScore.apply(evolutionStep)),
						ColumnExtractor.of("complexity", evolutionStep -> computeComplexity.apply(evolutionStep)),
						ColumnExtractor.of("rank", evolutionStep -> {

							final List<Set<Integer>> rankedPopulation = evolutionStep.context().get();
							Integer rank = null;
							for (int i = 0; i < 5 && i < rankedPopulation.size() && rank == null; i++) {

								if (rankedPopulation.get(i).contains(evolutionStep.individualIndex())) {
									rank = i;
								}
							}

							return rank != null ? rank : -1;
						}),
						ColumnExtractor.of("expression",
								evolutionStep -> TreeNodeUtils.toStringTreeNode(evolutionStep.individual(), 0)))

		);
	}
}