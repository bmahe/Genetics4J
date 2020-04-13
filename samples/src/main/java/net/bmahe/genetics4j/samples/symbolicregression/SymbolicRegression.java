package net.bmahe.genetics4j.samples.symbolicregression;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.EvolutionListener;
import net.bmahe.genetics4j.core.GeneticSystem;
import net.bmahe.genetics4j.core.GeneticSystemFactory;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.SimpleEvolutionListener;
import net.bmahe.genetics4j.core.Terminations;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;
import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.chromosomes.factory.ProgramTreeChromosomeFactory;
import net.bmahe.genetics4j.gp.combination.ProgramRandomCombineHandler;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.SimplificationRules;
import net.bmahe.genetics4j.gp.math.Terminals;
import net.bmahe.genetics4j.gp.mutation.ProgramRandomMutatePolicyHandler;
import net.bmahe.genetics4j.gp.mutation.ProgramRandomPrunePolicyHandler;
import net.bmahe.genetics4j.gp.mutation.ProgramRulesApplicatorPolicyHandler;
import net.bmahe.genetics4j.gp.program.FullProgramGenerator;
import net.bmahe.genetics4j.gp.program.GrowProgramGenerator;
import net.bmahe.genetics4j.gp.program.ImmutableProgram;
import net.bmahe.genetics4j.gp.program.ImmutableProgram.Builder;
import net.bmahe.genetics4j.gp.program.MultiProgramGenerator;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.program.ProgramGenerator;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;
import net.bmahe.genetics4j.gp.spec.combination.ProgramRandomCombine;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramApplyRules;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomMutate;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomPrune;
import net.bmahe.genetics4j.gp.utils.TreeNodeUtils;

public class SymbolicRegression {
	final static public Logger logger = LogManager.getLogger(SymbolicRegression.class);

	public Object execute(final TreeChromosome<Operation> treeChromosome, final Object[] input) {
		Validate.notNull(treeChromosome);

		final TreeNode<Operation> root = treeChromosome.getRoot();

		return execute(root, input);
	}

	public Object execute(final TreeNode<Operation> node, final Object[] input) {
		Validate.notNull(node);

		final Operation operation = node.getData();
		final List<TreeNode<Operation>> children = node.getChildren();

		final Object[] parameters = children != null ? children.stream().map(child -> execute(child, input)).toArray()
				: new Object[] {};

		return operation.apply(input, parameters);
	}

	public void run() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final ProgramGenerator fullProgramGenerator = new FullProgramGenerator(programHelper);
		final GrowProgramGenerator growProgramGenerator = new GrowProgramGenerator(programHelper);
		final ProgramGenerator programGenerator = new MultiProgramGenerator(random,
				List.of(fullProgramGenerator, growProgramGenerator));

		final Builder programBuilder = ImmutableProgram.builder();
		programBuilder.addFunctions(Functions.ADD, Functions.MUL, Functions.DIV, Functions.SUB, Functions.COS,
				Functions.SIN, Functions.EXP);
		programBuilder.addTerminal(Terminals.InputDouble(random), Terminals.PI, Terminals.E,
				Terminals.Coefficient(random, -50, 100), Terminals.CoefficientRounded(random, -25, 25));

		programBuilder.inputSpec(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		programBuilder.maxDepth(4);
		final Program program = programBuilder.build();

		net.bmahe.genetics4j.core.spec.GenotypeSpec.Builder genotypeSpecBuilder = new GenotypeSpec.Builder();
		genotypeSpecBuilder.chromosomeSpecs(ProgramTreeChromosomeSpec.of(program))
				.parentSelectionPolicy(TournamentSelection.build(3))
				.survivorSelectionPolicy(TournamentSelection.build(3))
				.offspringRatio(0.90d)
				.combinationPolicy(ProgramRandomCombine.build())
				.mutationPolicies(ProgramRandomMutate.of(0.10), ProgramRandomPrune.of(0.12),
						ProgramApplyRules.of(SimplificationRules.SIMPLIFY_RULES))
				.optimization(Optimization.MINIMIZE)
				.termination(Terminations.ofMaxGeneration(100))
				.fitness((genoType) -> {
					final TreeChromosome<Operation> chromosome = (TreeChromosome<Operation>) genoType.getChromosome(0);
					final Double[][] inputs = new Double[100][1];
					for (int i = 0; i < 100; i++) {
						inputs[i][0] = (i - 50) * 1.2;
					}

					double mse = 0;
					for (final Double[] input : inputs) {

						final double x = input[0];
						final double expected = (6.0 * x * x) - x;// + Math.cos(x - 1) * 12;
						final Object result = execute(chromosome, input);

						if (Double.isFinite(expected)) {
							if (result instanceof Double) {
								final Double resultDouble = (Double) result;
								mse += Double.isFinite(resultDouble)
										? (expected - resultDouble) * (expected - resultDouble)
										: 1_000_000_000;
							} else {
								logger.error("NOT A DOUBLE: {}", result);
								mse += 1000;
							}
						}
					}
					return Double.isFinite(mse) ? Math.sqrt(mse) + 1.5 * chromosome.getSize() : Double.MAX_VALUE;
				});
		final GenotypeSpec genotypeSpec = genotypeSpecBuilder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		geneticSystemDescriptorBuilder.populationSize(5000);
		geneticSystemDescriptorBuilder
				.addMutationPolicyHandlers(new ProgramRandomPrunePolicyHandler(random, programHelper));
		geneticSystemDescriptorBuilder
				.addMutationPolicyHandlers(new ProgramRandomMutatePolicyHandler(random, programGenerator));
		geneticSystemDescriptorBuilder.addMutationPolicyHandlers(new ProgramRulesApplicatorPolicyHandler());

		geneticSystemDescriptorBuilder.addChromosomeCombinatorHandlers(new ProgramRandomCombineHandler(random));

		net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider.Builder chromosomeFactoryProviderBuilder = ImmutableChromosomeFactoryProvider
				.builder();
		chromosomeFactoryProviderBuilder.random(random);
		chromosomeFactoryProviderBuilder.addChromosomeFactories(new ProgramTreeChromosomeFactory(programGenerator));
		geneticSystemDescriptorBuilder.chromosomeFactoryProvider(chromosomeFactoryProviderBuilder.build());
		geneticSystemDescriptorBuilder.numberOfPartitions(Math.max(1, Runtime.getRuntime().availableProcessors() - 1));

		geneticSystemDescriptorBuilder.addEvolutionListeners(new EvolutionListener() {

			@Override
			public void onEvolution(long generation, Genotype[] population, double[] fitness) {
				logger.info("Generation {}", generation);

				final List<Integer> topGenotypes = IntStream.range(0, population.length)
						.boxed()
						.sorted((a, b) -> Double.compare(fitness[a], fitness[b]))
						.limit(5)
						.collect(Collectors.toList());

				for (final Integer topCandidateIndex : topGenotypes) {
					final Genotype genotype = population[topCandidateIndex];
					final TreeChromosome<Operation<?>> chromosome = (TreeChromosome<Operation<?>>) genotype
							.getChromosome(0);
					final TreeNode<Operation<?>> root = chromosome.getRoot();

					logger.info("\t{}\t{}", fitness[topCandidateIndex], TreeNodeUtils.toStringTreeNode(root));
				}
			}
		}, new SimpleEvolutionListener());

		final GeneticSystemDescriptor geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();
		final GeneticSystemFactory geneticSystemFactory = new GeneticSystemFactory();
		final GeneticSystem geneticSystem = geneticSystemFactory.from(genotypeSpec, geneticSystemDescriptor);

		final EvolutionResult evolutionResult = geneticSystem.evolve();
		final Genotype bestGenotype = evolutionResult.bestGenotype();
		final TreeChromosome<Operation<?>> bestChromosome = (TreeChromosome<Operation<?>>) bestGenotype
				.getChromosome(0);
		logger.info("Best genotype: {}", bestChromosome.getRoot());
		logger.info("Best genotype - pretty print: {}", TreeNodeUtils.toStringTreeNode(bestChromosome.getRoot()));
	}

	public static int main(String[] args) {

		final SymbolicRegression symbolicRegression = new SymbolicRegression();
		symbolicRegression.run();

		return 0;
	}
}