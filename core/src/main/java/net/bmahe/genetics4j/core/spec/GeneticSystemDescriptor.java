package net.bmahe.genetics4j.core.spec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorHandler;
import net.bmahe.genetics4j.core.combination.PickFirstParentHandler;
import net.bmahe.genetics4j.core.combination.erx.EdgeRecombinationCrossoverHandler;
import net.bmahe.genetics4j.core.combination.multicombinations.MultiCombinationsHandler;
import net.bmahe.genetics4j.core.combination.multipointcrossover.MultiPointCrossoverCombinationHandler;
import net.bmahe.genetics4j.core.combination.ordercrossover.IntOrderCrossoverHandler;
import net.bmahe.genetics4j.core.combination.singlepointcrossover.SinglePointCrossoverHandler;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListener;
import net.bmahe.genetics4j.core.mutation.MultiMutationsPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.PartialMutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.RandomMutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.SwapMutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.randommutation.BitChromosomeRandomMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.randommutation.IntChromosomeRandomMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.swapmutation.BitChromosomeSwapMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.swapmutation.IntChromosomeSwapMutationHandler;
import net.bmahe.genetics4j.core.selection.MultiSelectionsPolicyHandler;
import net.bmahe.genetics4j.core.selection.RandomSelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.TournamentSelectionPolicyHandler;

@Value.Immutable
public abstract class GeneticSystemDescriptor<T extends Comparable<T>> {
	public static final long DEFAULT_POPULATION_SIZE = 100;

	@Value.Default
	public List<ChromosomeCombinatorHandler> defaultChromosomeCombinatorHandlers() {
		return Arrays.asList(new MultiCombinationsHandler(random()),
				new IntOrderCrossoverHandler(random()),
				new MultiPointCrossoverCombinationHandler(random()),
				new SinglePointCrossoverHandler(random()),
				new EdgeRecombinationCrossoverHandler(random()),
				new PickFirstParentHandler());
	}

	public abstract List<Function<GeneticSystemDescriptor<T>, ChromosomeCombinatorHandler>>
			chromosomeCombinatorHandlerFactories();

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
		return Arrays.asList(new RandomSelectionPolicyHandler<T>(random()),
				new TournamentSelectionPolicyHandler<T>(random()),
				new MultiSelectionsPolicyHandler<T>());
	}

	public abstract List<Function<GeneticSystemDescriptor<T>, SelectionPolicyHandler<T>>>
			selectionPolicyHandlerFactories();

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
		return Arrays.asList(new RandomMutationPolicyHandler(random()),
				new SwapMutationPolicyHandler(random()),
				new MultiMutationsPolicyHandler(random()),
				new PartialMutationPolicyHandler());
	}

	public abstract List<Function<GeneticSystemDescriptor<T>, MutationPolicyHandler>> mutationPolicyHandlerFactories();

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
		return Arrays.asList(new BitChromosomeRandomMutationHandler(random()),
				new IntChromosomeRandomMutationHandler(random()),
				new BitChromosomeSwapMutationHandler(random()),
				new IntChromosomeSwapMutationHandler(random()));
	}

	public abstract List<Function<GeneticSystemDescriptor<T>, ChromosomeMutationHandler<? extends Chromosome>>>
			chromosomeMutationPolicyHandlerFactories();

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
	public Random random() {
		return new Random();
	}

	@Value.Default
	public long populationSize() {
		return DEFAULT_POPULATION_SIZE;
	}

	/**
	 * XXX TODO review how to specify the execution system. This might need some
	 * abstraction as to accomodate different execution systems
	 * 
	 * @return
	 */
	@Value.Default
	public int numberOfPartitions() {
		return Runtime.getRuntime().availableProcessors();
	}

	@Value.Default
	public ChromosomeFactoryProvider chromosomeFactoryProvider() {
		return ImmutableChromosomeFactoryProvider.builder().random(random()).build();
	}

	@Value.Default
	public List<EvolutionListener<T>> evolutionListeners() {
		return Collections.emptyList();
	}

	public static <U extends Comparable<U>> ImmutableGeneticSystemDescriptor.Builder<U> builder() {
		return ImmutableGeneticSystemDescriptor.builder();
	}
}