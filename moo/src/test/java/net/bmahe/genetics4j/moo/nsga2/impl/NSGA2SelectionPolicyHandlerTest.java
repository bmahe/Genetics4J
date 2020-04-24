package net.bmahe.genetics4j.moo.nsga2.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

import net.bmahe.genetics4j.core.Terminations;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.selection.RandomSelectionPolicy;
import net.bmahe.genetics4j.moo.nsga2.spec.ImmutableNSGA2Selection;
import net.bmahe.genetics4j.moo.nsga2.spec.ImmutableNSGA2Selection.Builder;
import net.bmahe.genetics4j.moo.nsga2.spec.NSGA2Selection;

public class NSGA2SelectionPolicyHandlerTest {

	private final GenotypeSpec<Integer> SIMPLE_MAXIMIZING_GENOTYPE_SPEC = new GenotypeSpec.Builder<Integer>()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(RandomSelectionPolicy.build())
			.survivorSelectionPolicy(RandomSelectionPolicy.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Integer.MAX_VALUE * 10)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test(expected = NullPointerException.class)
	public void canHandleNullArg() {
		final NSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new NSGA2SelectionPolicyHandler<>();
		selectionPolicyHandler.canHandle(null);
	}

	@Test
	public void canHandle() {
		final NSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new NSGA2SelectionPolicyHandler<>();

		final Builder<Integer> builder = ImmutableNSGA2Selection.builder();
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final NSGA2Selection<Integer> nsga2Selection = builder.build();

		assertTrue(selectionPolicyHandler.canHandle(nsga2Selection));
		assertFalse(selectionPolicyHandler.canHandle(RandomSelectionPolicy.build()));
	}

	@Test(expected = NullPointerException.class)
	public void resolveNoGSD() {
		final NSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new NSGA2SelectionPolicyHandler<>();

		final Builder<Integer> builder = ImmutableNSGA2Selection.builder();
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final NSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<Integer> geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		geneticSystemDescriptorBuilder.populationSize(100);

		final ImmutableGeneticSystemDescriptor<Integer> geneticSystemDescriptor = geneticSystemDescriptorBuilder
				.build();
		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				geneticSystemDescriptor);

		final Selector<Integer> selector = selectionPolicyHandler
				.resolve(null, SIMPLE_MAXIMIZING_GENOTYPE_SPEC, selectionPolicyHandlerResolver, nsga2Selection);
	}

	@Test(expected = NullPointerException.class)
	public void resolveNoSpec() {
		final NSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new NSGA2SelectionPolicyHandler<>();

		final Builder<Integer> builder = ImmutableNSGA2Selection.builder();
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final NSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<Integer> geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		geneticSystemDescriptorBuilder.populationSize(100);

		final ImmutableGeneticSystemDescriptor<Integer> geneticSystemDescriptor = geneticSystemDescriptorBuilder
				.build();
		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				geneticSystemDescriptor);

		final Selector<Integer> selector = selectionPolicyHandler
				.resolve(geneticSystemDescriptor, null, selectionPolicyHandlerResolver, nsga2Selection);
	}

	@Test(expected = NullPointerException.class)
	public void resolveNoSHR() {
		final NSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new NSGA2SelectionPolicyHandler<>();

		final Builder<Integer> builder = ImmutableNSGA2Selection.builder();
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final NSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<Integer> geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		geneticSystemDescriptorBuilder.populationSize(100);

		final ImmutableGeneticSystemDescriptor<Integer> geneticSystemDescriptor = geneticSystemDescriptorBuilder
				.build();
		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				geneticSystemDescriptor);

		final Selector<Integer> selector = selectionPolicyHandler
				.resolve(geneticSystemDescriptor, SIMPLE_MAXIMIZING_GENOTYPE_SPEC, null, nsga2Selection);
	}

	@Test(expected = NullPointerException.class)
	public void resolveNoSelectionSpec() {
		final NSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new NSGA2SelectionPolicyHandler<>();

		final Builder<Integer> builder = ImmutableNSGA2Selection.builder();
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final NSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<Integer> geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		geneticSystemDescriptorBuilder.populationSize(100);

		final ImmutableGeneticSystemDescriptor<Integer> geneticSystemDescriptor = geneticSystemDescriptorBuilder
				.build();

		final Selector<Integer> selector = selectionPolicyHandler
				.resolve(geneticSystemDescriptor, SIMPLE_MAXIMIZING_GENOTYPE_SPEC, null, nsga2Selection);
	}

	@Test(expected = IllegalArgumentException.class)
	public void resolveWrongSelectionSpec() {
		final NSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new NSGA2SelectionPolicyHandler<>();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<Integer> geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		geneticSystemDescriptorBuilder.populationSize(100);

		final ImmutableGeneticSystemDescriptor<Integer> geneticSystemDescriptor = geneticSystemDescriptorBuilder
				.build();
		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				geneticSystemDescriptor);

		final Selector<Integer> selector = selectionPolicyHandler.resolve(geneticSystemDescriptor,
				SIMPLE_MAXIMIZING_GENOTYPE_SPEC,
				selectionPolicyHandlerResolver,
				RandomSelectionPolicy.build());
	}

	@Test
	public void resolve() {
		final NSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new NSGA2SelectionPolicyHandler<>();

		final Builder<Integer> builder = ImmutableNSGA2Selection.builder();
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final NSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<Integer> geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		geneticSystemDescriptorBuilder.populationSize(100);

		final ImmutableGeneticSystemDescriptor<Integer> geneticSystemDescriptor = geneticSystemDescriptorBuilder
				.build();
		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				geneticSystemDescriptor);

		final Selector<Integer> selector = selectionPolicyHandler.resolve(geneticSystemDescriptor,
				SIMPLE_MAXIMIZING_GENOTYPE_SPEC,
				selectionPolicyHandlerResolver,
				nsga2Selection);

		assertNotNull(selector);
		assertTrue(selector instanceof NSGA2Selector<?>);
	}
}