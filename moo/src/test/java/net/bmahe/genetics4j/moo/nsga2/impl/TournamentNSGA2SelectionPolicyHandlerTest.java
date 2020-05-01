package net.bmahe.genetics4j.moo.nsga2.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptors;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.selection.RandomSelectionPolicy;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.moo.nsga2.spec.ImmutableTournamentNSGA2Selection;
import net.bmahe.genetics4j.moo.nsga2.spec.ImmutableTournamentNSGA2Selection.Builder;
import net.bmahe.genetics4j.moo.nsga2.spec.TournamentNSGA2Selection;

public class TournamentNSGA2SelectionPolicyHandlerTest {
	private final GenotypeSpec<Integer> SIMPLE_MAXIMIZING_GENOTYPE_SPEC = new GenotypeSpec.Builder<Integer>()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(RandomSelectionPolicy.build())
			.survivorSelectionPolicy(RandomSelectionPolicy.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Integer.MAX_VALUE * 10)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test(expected = NullPointerException.class)
	public void ctorNullArg() {
		new TournamentNSGA2SelectionPolicyHandler<>(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleNullArg() {
		final Random random = new Random();
		final TournamentNSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<>(
				random);
		selectionPolicyHandler.canHandle(null);
	}

	@Test
	public void canHandle() {
		final Random random = new Random();
		final TournamentNSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<>(
				random);

		final Builder<Integer> builder = ImmutableTournamentNSGA2Selection.builder();
		builder.numCandidates(3);
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final TournamentNSGA2Selection<Integer> nsga2Selection = builder.build();

		assertTrue(selectionPolicyHandler.canHandle(nsga2Selection));
		assertFalse(selectionPolicyHandler.canHandle(RandomSelectionPolicy.build()));
	}

	@Test(expected = NullPointerException.class)
	public void resolveNoGSD() {
		final Random random = new Random();

		final TournamentNSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<>(
				random);

		final Builder<Integer> builder = ImmutableTournamentNSGA2Selection.builder();
		builder.numCandidates(3);
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final TournamentNSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<Integer> geneticSystemDescriptorBuilder = GeneticSystemDescriptors
				.standard();
		geneticSystemDescriptorBuilder.populationSize(100);
		final GeneticSystemDescriptor<Integer> geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				geneticSystemDescriptor);

		selectionPolicyHandler
				.resolve(null, SIMPLE_MAXIMIZING_GENOTYPE_SPEC, selectionPolicyHandlerResolver, nsga2Selection);
	}

	@Test(expected = NullPointerException.class)
	public void resolveNoSpec() {
		final Random random = new Random();

		final TournamentNSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<>(
				random);

		final Builder<Integer> builder = ImmutableTournamentNSGA2Selection.builder();
		builder.numCandidates(3);
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final TournamentNSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<Integer> geneticSystemDescriptorBuilder = GeneticSystemDescriptors
				.standard();
		geneticSystemDescriptorBuilder.populationSize(100);
		final GeneticSystemDescriptor<Integer> geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				geneticSystemDescriptor);

		selectionPolicyHandler.resolve(geneticSystemDescriptor, null, selectionPolicyHandlerResolver, nsga2Selection);
	}

	@Test(expected = NullPointerException.class)
	public void resolveNoSHR() {
		final Random random = new Random();

		final TournamentNSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<>(
				random);

		final Builder<Integer> builder = ImmutableTournamentNSGA2Selection.builder();
		builder.numCandidates(3);
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final TournamentNSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<Integer> geneticSystemDescriptorBuilder = GeneticSystemDescriptors
				.standard();
		geneticSystemDescriptorBuilder.populationSize(100);
		final GeneticSystemDescriptor<Integer> geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		selectionPolicyHandler.resolve(geneticSystemDescriptor, SIMPLE_MAXIMIZING_GENOTYPE_SPEC, null, nsga2Selection);
	}

	@Test(expected = NullPointerException.class)
	public void resolveNoSelectionSpec() {
		final Random random = new Random();

		final TournamentNSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<>(
				random);

		final Builder<Integer> builder = ImmutableTournamentNSGA2Selection.builder();
		builder.numCandidates(3);
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final TournamentNSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<Integer> geneticSystemDescriptorBuilder = GeneticSystemDescriptors
				.standard();
		geneticSystemDescriptorBuilder.populationSize(100);
		final GeneticSystemDescriptor<Integer> geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		selectionPolicyHandler.resolve(geneticSystemDescriptor, SIMPLE_MAXIMIZING_GENOTYPE_SPEC, null, nsga2Selection);
	}

	@Test(expected = IllegalArgumentException.class)
	public void resolveWrongSelectionSpec() {
		final Random random = new Random();

		final TournamentNSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<>(
				random);

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<Integer> geneticSystemDescriptorBuilder = GeneticSystemDescriptors
				.standard();
		geneticSystemDescriptorBuilder.populationSize(100);
		final GeneticSystemDescriptor<Integer> geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				geneticSystemDescriptor);

		selectionPolicyHandler.resolve(geneticSystemDescriptor,
				SIMPLE_MAXIMIZING_GENOTYPE_SPEC,
				selectionPolicyHandlerResolver,
				RandomSelectionPolicy.build());
	}

	@Test
	public void resolve() {
		final Random random = new Random();

		final TournamentNSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<>(
				random);

		final Builder<Integer> builder = ImmutableTournamentNSGA2Selection.builder();
		builder.numCandidates(3);
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final TournamentNSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<Integer> geneticSystemDescriptorBuilder = GeneticSystemDescriptors
				.standard();
		geneticSystemDescriptorBuilder.populationSize(100);
		final GeneticSystemDescriptor<Integer> geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				geneticSystemDescriptor);

		final Selector<Integer> selector = selectionPolicyHandler.resolve(geneticSystemDescriptor,
				SIMPLE_MAXIMIZING_GENOTYPE_SPEC,
				selectionPolicyHandlerResolver,
				nsga2Selection);

		assertNotNull(selector);
		assertTrue(selector instanceof TournamentNSGA2Selector<?>);
	}
}