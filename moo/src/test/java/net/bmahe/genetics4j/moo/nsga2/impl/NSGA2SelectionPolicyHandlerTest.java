package net.bmahe.genetics4j.moo.nsga2.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.selection.RandomSelection;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.moo.nsga2.spec.ImmutableNSGA2Selection;
import net.bmahe.genetics4j.moo.nsga2.spec.ImmutableNSGA2Selection.Builder;
import net.bmahe.genetics4j.moo.nsga2.spec.NSGA2Selection;

public class NSGA2SelectionPolicyHandlerTest {

	private final EAConfiguration<Integer> SIMPLE_MAXIMIZING_EA_CONFIGURATION = new EAConfiguration.Builder<Integer>()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(RandomSelection.build())
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
		assertFalse(selectionPolicyHandler.canHandle(RandomSelection.build()));
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

		final net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder<Integer> eaExecutionContextBuilder = EAExecutionContexts
				.standard();
		eaExecutionContextBuilder.populationSize(100);

		final EAExecutionContext<Integer> eaExecutionContext = eaExecutionContextBuilder.build();
		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final Selector<Integer> selector = selectionPolicyHandler
				.resolve(null, SIMPLE_MAXIMIZING_EA_CONFIGURATION, selectionPolicyHandlerResolver, nsga2Selection);
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

		final net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder<Integer> eaExecutionContextBuilder = EAExecutionContexts
				.standard();
		eaExecutionContextBuilder.populationSize(100);
		final EAExecutionContext<Integer> eaExecutionContext = eaExecutionContextBuilder.build();
		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final Selector<Integer> selector = selectionPolicyHandler
				.resolve(eaExecutionContext, null, selectionPolicyHandlerResolver, nsga2Selection);
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

		final net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder<Integer> eaExecutionContextBuilder = EAExecutionContexts
				.standard();
		eaExecutionContextBuilder.populationSize(100);
		final EAExecutionContext<Integer> eaExecutionContext = eaExecutionContextBuilder.build();

		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final Selector<Integer> selector = selectionPolicyHandler
				.resolve(eaExecutionContext, SIMPLE_MAXIMIZING_EA_CONFIGURATION, null, nsga2Selection);
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

		final net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder<Integer> eaExecutionContextBuilder = EAExecutionContexts
				.standard();
		eaExecutionContextBuilder.populationSize(100);
		final EAExecutionContext<Integer> eaExecutionContext = eaExecutionContextBuilder.build();

		final Selector<Integer> selector = selectionPolicyHandler
				.resolve(eaExecutionContext, SIMPLE_MAXIMIZING_EA_CONFIGURATION, null, nsga2Selection);
	}

	@Test(expected = IllegalArgumentException.class)
	public void resolveWrongSelectionSpec() {
		final NSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new NSGA2SelectionPolicyHandler<>();

		final net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder<Integer> eaExecutionContextBuilder = EAExecutionContexts
				.standard();
		eaExecutionContextBuilder.populationSize(100);
		final EAExecutionContext<Integer> eaExecutionContext = eaExecutionContextBuilder.build();

		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final Selector<Integer> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				RandomSelection.build());
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

		final net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder<Integer> eaExecutionContextBuilder = EAExecutionContexts
				.standard();
		eaExecutionContextBuilder.populationSize(100);
		final EAExecutionContext<Integer> eaExecutionContext = eaExecutionContextBuilder.build();

		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final Selector<Integer> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				nsga2Selection);

		assertNotNull(selector);
		assertTrue(selector instanceof NSGA2Selector<?>);
	}
}