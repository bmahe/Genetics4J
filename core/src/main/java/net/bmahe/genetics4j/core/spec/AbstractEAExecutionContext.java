package net.bmahe.genetics4j.core.spec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.random.RandomGenerator;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorHandler;
import net.bmahe.genetics4j.core.combination.PickFirstParentHandler;
import net.bmahe.genetics4j.core.combination.erx.EdgeRecombinationCrossoverHandler;
import net.bmahe.genetics4j.core.combination.multicombinations.MultiCombinationsHandler;
import net.bmahe.genetics4j.core.combination.multipointarithmetic.MultiPointArithmeticCombinationHandler;
import net.bmahe.genetics4j.core.combination.multipointcrossover.MultiPointCrossoverCombinationHandler;
import net.bmahe.genetics4j.core.combination.ordercrossover.IntOrderCrossoverHandler;
import net.bmahe.genetics4j.core.combination.singlepointarithmetic.SinglePointArithmeticCombinationHandler;
import net.bmahe.genetics4j.core.combination.singlepointcrossover.SinglePointCrossoverHandler;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListener;
import net.bmahe.genetics4j.core.mutation.CreepMutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MultiMutationsPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.PartialMutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.RandomMutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.SwapMutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.creepmutation.DoubleChromosomeCreepMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.creepmutation.FloatChromosomeCreepMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.creepmutation.IntChromosomeCreepMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.randommutation.BitChromosomeRandomMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.randommutation.DoubleChromosomeRandomMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.randommutation.FloatChromosomeRandomMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.randommutation.IntChromosomeRandomMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.swapmutation.BitChromosomeSwapMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.swapmutation.DoubleChromosomeSwapMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.swapmutation.FloatChromosomeSwapMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.swapmutation.IntChromosomeSwapMutationHandler;
import net.bmahe.genetics4j.core.replacement.ElitismReplacementStrategyHandler;
import net.bmahe.genetics4j.core.replacement.GenerationalReplacementStrategyHandler;
import net.bmahe.genetics4j.core.replacement.ReplacementStrategyHandler;
import net.bmahe.genetics4j.core.selection.DoubleTournamentSelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.MultiSelectionsPolicyHandler;
import net.bmahe.genetics4j.core.selection.MultiTournamentsSelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.ProportionalTournamentSelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.RandomSelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectAllPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.TournamentSelectionPolicyHandler;

/**
 * Evolutionary Algorithm - Execution Context
 * <p>
 * This defines how the Evolutionary Algorithm will be executed.
 *
 * @param <T> Type of the fitness measurement
 */
public abstract class AbstractEAExecutionContext<T extends Comparable<T>> {
	public static final int DEFAULT_POPULATION_SIZE = 100;

	@Value.Default
	public List<ChromosomeCombinatorHandler> defaultChromosomeCombinatorHandlers() {
		return Arrays.asList(new MultiCombinationsHandler(randomGenerator()),
				new IntOrderCrossoverHandler(randomGenerator()),
				new MultiPointCrossoverCombinationHandler(randomGenerator()),
				new MultiPointArithmeticCombinationHandler(randomGenerator()),
				new SinglePointCrossoverHandler(randomGenerator()),
				new SinglePointArithmeticCombinationHandler(randomGenerator()),
				new EdgeRecombinationCrossoverHandler(randomGenerator()),
				new PickFirstParentHandler());
	}

	public abstract List<Function<AbstractEAExecutionContext<T>, ChromosomeCombinatorHandler>> chromosomeCombinatorHandlerFactories();

	@Value.Derived
	public List<ChromosomeCombinatorHandler> chromosomeCombinatorHandlers() {

		final List<ChromosomeCombinatorHandler> chromosomeCombinatorHandlers = new ArrayList<>();

		final List<ChromosomeCombinatorHandler> defaultChromosomeCombinatorHandlers = defaultChromosomeCombinatorHandlers();
		if (defaultChromosomeCombinatorHandlers.isEmpty() == false) {
			chromosomeCombinatorHandlers.addAll(defaultChromosomeCombinatorHandlers);
		}

		chromosomeCombinatorHandlerFactories().stream()
				.map(factory -> factory.apply(this))
				.forEach(cch -> chromosomeCombinatorHandlers.add(cch));

		return Collections.unmodifiableList(chromosomeCombinatorHandlers);
	}

	/////////////////////////////////////////

	@Value.Default
	public List<SelectionPolicyHandler<T>> defaultSelectionPolicyHandlers() {
		return Arrays.asList(new RandomSelectionPolicyHandler<T>(randomGenerator()),
				new TournamentSelectionPolicyHandler<T>(randomGenerator()),
				new DoubleTournamentSelectionPolicyHandler<T>(randomGenerator()),
				new ProportionalTournamentSelectionPolicyHandler<T>(randomGenerator()),
				new MultiTournamentsSelectionPolicyHandler<T>(randomGenerator()),
				new MultiSelectionsPolicyHandler<T>(),
				new SelectAllPolicyHandler<T>());
	}

	public abstract List<Function<AbstractEAExecutionContext<T>, SelectionPolicyHandler<T>>> selectionPolicyHandlerFactories();

	@Value.Derived
	public List<SelectionPolicyHandler<T>> selectionPolicyHandlers() {

		final List<SelectionPolicyHandler<T>> selectionPolicyHandlers = new ArrayList<>();

		final List<SelectionPolicyHandler<T>> defaultSelectionPolicyHandlers = defaultSelectionPolicyHandlers();
		if (defaultSelectionPolicyHandlers.isEmpty() == false) {
			selectionPolicyHandlers.addAll(defaultSelectionPolicyHandlers);
		}

		selectionPolicyHandlerFactories().stream()
				.map(factory -> factory.apply(this))
				.forEach(sph -> selectionPolicyHandlers.add(sph));

		return Collections.unmodifiableList(selectionPolicyHandlers);
	}

	/////////////////////////////////////////

	@Value.Default
	public List<MutationPolicyHandler> defaultMutationPolicyHandlers() {
		return Arrays.asList(new RandomMutationPolicyHandler(randomGenerator()),
				new SwapMutationPolicyHandler(randomGenerator()),
				new MultiMutationsPolicyHandler(randomGenerator()),
				new PartialMutationPolicyHandler(),
				new CreepMutationPolicyHandler(randomGenerator()));
	}

	public abstract List<Function<AbstractEAExecutionContext<T>, MutationPolicyHandler>> mutationPolicyHandlerFactories();

	@Value.Derived
	public List<MutationPolicyHandler> mutationPolicyHandlers() {

		final List<MutationPolicyHandler> mutationPolicyHandlers = new ArrayList<>();

		final List<MutationPolicyHandler> defaultMutationPolicyHandlers = defaultMutationPolicyHandlers();
		if (defaultMutationPolicyHandlers.isEmpty() == false) {
			mutationPolicyHandlers.addAll(defaultMutationPolicyHandlers);
		}

		mutationPolicyHandlerFactories().stream()
				.map(factory -> factory.apply(this))
				.forEach(mph -> mutationPolicyHandlers.add(mph));

		return Collections.unmodifiableList(mutationPolicyHandlers);
	}

	/////////////////////////////////////////

	@Value.Default
	public List<ChromosomeMutationHandler<? extends Chromosome>> defaultChromosomeMutationPolicyHandlers() {
		return Arrays.asList(new BitChromosomeRandomMutationHandler(randomGenerator()),
				new IntChromosomeRandomMutationHandler(randomGenerator()),
				new DoubleChromosomeRandomMutationHandler(randomGenerator()),
				new FloatChromosomeRandomMutationHandler(randomGenerator()),
				new BitChromosomeSwapMutationHandler(randomGenerator()),
				new IntChromosomeSwapMutationHandler(randomGenerator()),
				new DoubleChromosomeSwapMutationHandler(randomGenerator()),
				new FloatChromosomeSwapMutationHandler(randomGenerator()),
				new IntChromosomeCreepMutationHandler(randomGenerator()),
				new DoubleChromosomeCreepMutationHandler(randomGenerator()),
				new FloatChromosomeCreepMutationHandler(randomGenerator()));
	}

	public abstract List<Function<AbstractEAExecutionContext<T>, ChromosomeMutationHandler<? extends Chromosome>>> chromosomeMutationPolicyHandlerFactories();

	@Value.Derived
	public List<ChromosomeMutationHandler<? extends Chromosome>> chromosomeMutationPolicyHandlers() {

		final List<ChromosomeMutationHandler<? extends Chromosome>> chromosomeMutationPolicyHandlers = new ArrayList<>();

		final List<ChromosomeMutationHandler<? extends Chromosome>> defaultChromosomeMutationPolicyHandlers = defaultChromosomeMutationPolicyHandlers();
		if (defaultChromosomeMutationPolicyHandlers.isEmpty() == false) {
			chromosomeMutationPolicyHandlers.addAll(defaultChromosomeMutationPolicyHandlers);
		}

		chromosomeMutationPolicyHandlerFactories().stream()
				.map(factory -> factory.apply(this))
				.forEach(cmh -> chromosomeMutationPolicyHandlers.add(cmh));

		return Collections.unmodifiableList(chromosomeMutationPolicyHandlers);
	}

	/////////////////////////////////////////

	@Value.Default
	public List<ReplacementStrategyHandler<T>> defaultReplacementStrategyHandlers() {
		return List.of(new ElitismReplacementStrategyHandler<>(), new GenerationalReplacementStrategyHandler<>());
	}

	public abstract List<Function<AbstractEAExecutionContext<T>, ReplacementStrategyHandler<T>>> replacementStrategyHandlerFactories();

	@Value.Derived
	public List<ReplacementStrategyHandler<T>> replacementStrategyHandlers() {
		final List<ReplacementStrategyHandler<T>> replacementStrategyHandlers = new ArrayList<>();

		final List<ReplacementStrategyHandler<T>> defaultReplacementStrategyHandlers = defaultReplacementStrategyHandlers();
		if (defaultReplacementStrategyHandlers.isEmpty() == false) {
			replacementStrategyHandlers.addAll(defaultReplacementStrategyHandlers);
		}

		replacementStrategyHandlerFactories().stream()
				.map(factory -> factory.apply(this))
				.forEach(esh -> replacementStrategyHandlers.add(esh));

		return Collections.unmodifiableList(replacementStrategyHandlers);
	}

	/////////////////////////////////////////

	@Value.Default
	public RandomGenerator randomGenerator() {
		return RandomGenerator.getDefault();
	}

	@Value.Default
	public int populationSize() {
		return DEFAULT_POPULATION_SIZE;
	}

	@Value.Default
	public ChromosomeFactoryProvider chromosomeFactoryProvider() {
		return ImmutableChromosomeFactoryProvider.builder()
				.randomGenerator(randomGenerator())
				.build();
	}

	@Value.Default
	public List<EvolutionListener<T>> evolutionListeners() {
		return Collections.emptyList();
	}
}