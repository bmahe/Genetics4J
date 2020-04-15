package net.bmahe.genetics4j.core.spec;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.EvolutionListener;
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
	public List<ChromosomeCombinatorHandler> chromosomeCombinatorHandlers() {
		return Arrays.asList(new MultiCombinationsHandler(random()), new IntOrderCrossoverHandler(random()),
				new MultiPointCrossoverCombinationHandler(random()), new SinglePointCrossoverHandler(random()),
				new EdgeRecombinationCrossoverHandler(random()), new PickFirstParentHandler());
	}

	@Value.Default
	public List<SelectionPolicyHandler<T>> selectionPolicyHandlers() {
		// new RouletteWheelSelectionPolicyHandler<T>(random()),
		return Arrays.asList(new RandomSelectionPolicyHandler<T>(random()),
				new TournamentSelectionPolicyHandler<T>(random()), new MultiSelectionsPolicyHandler<T>());
	}

	@Value.Default
	public List<MutationPolicyHandler> mutationPolicyHandlers() {
		return Arrays.asList(new RandomMutationPolicyHandler(random()), new SwapMutationPolicyHandler(random()),
				new MultiMutationsPolicyHandler(random()), new PartialMutationPolicyHandler());
	}

	@Value.Default
	public List<ChromosomeMutationHandler<? extends Chromosome>> chromosomeMutationPolicyHandlers() {
		return Arrays.asList(new BitChromosomeRandomMutationHandler(random()),
				new IntChromosomeRandomMutationHandler(random()), new BitChromosomeSwapMutationHandler(random()),
				new IntChromosomeSwapMutationHandler(random()));
	}

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
}