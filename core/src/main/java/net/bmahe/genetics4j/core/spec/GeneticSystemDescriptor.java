package net.bmahe.genetics4j.core.spec;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.EvolutionListener;
import net.bmahe.genetics4j.core.SimpleEvolutionListener;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorHandler;
import net.bmahe.genetics4j.core.combination.multicombinations.MultiCombinationsHandler;
import net.bmahe.genetics4j.core.combination.multipointcrossover.MultiPointCrossoverCombinationHandler;
import net.bmahe.genetics4j.core.combination.ordercrossover.IntOrderCrossoverHandler;
import net.bmahe.genetics4j.core.combination.singlepointcrossover.SinglePointCrossoverHandler;
import net.bmahe.genetics4j.core.mutation.MultiMutationsPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.RandomMutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.SwapMutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.randommutation.BitChromosomeRandomMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.randommutation.IntChromosomeRandomMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.swapmutation.BitChromosomeSwapMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.swapmutation.IntChromosomeSwapMutationHandler;
import net.bmahe.genetics4j.core.selection.MultiSelectionsPolicyHandler;
import net.bmahe.genetics4j.core.selection.RandomSelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.RouletteWheelSelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.TournamentSelectionPolicyHandler;

@Value.Immutable
public abstract class GeneticSystemDescriptor {

	public static final long DEFAULT_POPULATION_SIZE = 100;

	@Value.Default
	public List<ChromosomeCombinatorHandler> chromosomeCombinatorHandlers() {
		return Arrays.asList(new MultiCombinationsHandler(random()), new IntOrderCrossoverHandler(random()),
				new MultiPointCrossoverCombinationHandler(random()), new SinglePointCrossoverHandler(random()));
	}

	@Value.Default
	public List<SelectionPolicyHandler> selectionPolicyHandlers() {
		return Arrays.asList(new RandomSelectionPolicyHandler(random()),
				new RouletteWheelSelectionPolicyHandler(random()), new TournamentSelectionPolicyHandler(random()),
				new MultiSelectionsPolicyHandler(random()));
	}

	@Value.Default
	public List<MutationPolicyHandler> mutationPolicyHandlers() {
		return Arrays.asList(new RandomMutationPolicyHandler(random()), new SwapMutationPolicyHandler(random()),
				new MultiMutationsPolicyHandler(random()));
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

	@Value.Default
	public ChromosomeFactoryProvider chromosomeFactoryProvider() {
		return ImmutableChromosomeFactoryProvider.builder()
				.random(random())
				.build();
	}

	@Value.Default
	public List<EvolutionListener> evolutionListeners() {
		return Arrays.asList(new SimpleEvolutionListener());
	}
}