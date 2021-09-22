package net.bmahe.genetics4j.moo.nsga2.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.Random;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.selection.RandomSelection;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.moo.nsga2.spec.ImmutableTournamentNSGA2Selection.Builder;
import net.bmahe.genetics4j.moo.nsga2.spec.TournamentNSGA2Selection;

public class TournamentNSGA2SelectionPolicyHandlerTest {
	private final EAConfiguration<Integer> SIMPLE_MAXIMIZING_EA_CONFIGURATION = new EAConfiguration.Builder<Integer>()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(RandomSelection.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Integer.MAX_VALUE * 10)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test
	public void ctorNullArg() {
		assertThrows(NullPointerException.class, () -> new TournamentNSGA2SelectionPolicyHandler<>(null));
	}

	@Test
	public void canHandleNullArg() {
		final Random random = new Random();
		final TournamentNSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<>(
				random);
		assertThrows(NullPointerException.class, () -> selectionPolicyHandler.canHandle(null));
	}

	@Test
	public void canHandle() {
		final Random random = new Random();
		final TournamentNSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<>(
				random);

		final Builder<Integer> builder = TournamentNSGA2Selection.builder();
		builder.numCandidates(3);
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final TournamentNSGA2Selection<Integer> nsga2Selection = builder.build();

		assertTrue(selectionPolicyHandler.canHandle(nsga2Selection));
		assertFalse(selectionPolicyHandler.canHandle(RandomSelection.build()));
	}

	@Test
	public void resolveNoGSD() {
		final Random random = new Random();

		final TournamentNSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<>(
				random);

		final Builder<Integer> builder = TournamentNSGA2Selection.builder();
		builder.numCandidates(3);
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final TournamentNSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder<Integer> eaExecutionContextBuilder = EAExecutionContexts
				.standard();
		eaExecutionContextBuilder.populationSize(100);
		final EAExecutionContext<Integer> eaExecutionContext = eaExecutionContextBuilder.build();

		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		assertThrows(NullPointerException.class,
				() -> selectionPolicyHandler
						.resolve(null, SIMPLE_MAXIMIZING_EA_CONFIGURATION, selectionPolicyHandlerResolver, nsga2Selection));
	}

	@Test
	public void resolveNoSpec() {
		final Random random = new Random();

		final TournamentNSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<>(
				random);

		final Builder<Integer> builder = TournamentNSGA2Selection.builder();
		builder.numCandidates(3);
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final TournamentNSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder<Integer> eaExecutionContextBuilder = EAExecutionContexts
				.standard();
		eaExecutionContextBuilder.populationSize(100);
		final EAExecutionContext<Integer> eaExecutionContext = eaExecutionContextBuilder.build();

		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		assertThrows(NullPointerException.class,
				() -> selectionPolicyHandler
						.resolve(eaExecutionContext, null, selectionPolicyHandlerResolver, nsga2Selection));
	}

	@Test
	public void resolveNoSHR() {
		final Random random = new Random();

		final TournamentNSGA2SelectionPolicyHandler<Integer> selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<>(
				random);

		final Builder<Integer> builder = TournamentNSGA2Selection.builder();
		builder.numCandidates(3);
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final TournamentNSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder<Integer> eaExecutionContextBuilder = EAExecutionContexts
				.standard();
		eaExecutionContextBuilder.populationSize(100);
		final EAExecutionContext<Integer> eaExecutionContext = eaExecutionContextBuilder.build();

		assertThrows(NullPointerException.class,
				() -> selectionPolicyHandler
						.resolve(eaExecutionContext, SIMPLE_MAXIMIZING_EA_CONFIGURATION, null, nsga2Selection));
	}

	@Test
	public void resolveNoSelectionSpec() {
		final Random random = new Random();

		final var selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<Integer>(random);

		final Builder<Integer> builder = TournamentNSGA2Selection.builder();
		builder.numCandidates(3);
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final TournamentNSGA2Selection<Integer> nsga2Selection = builder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder<Integer> eaExecutionContextBuilder = EAExecutionContexts
				.standard();
		eaExecutionContextBuilder.populationSize(100);
		final EAExecutionContext<Integer> eaExecutionContext = eaExecutionContextBuilder.build();

		assertThrows(NullPointerException.class,
				() -> selectionPolicyHandler
						.resolve(eaExecutionContext, SIMPLE_MAXIMIZING_EA_CONFIGURATION, null, nsga2Selection));
	}

	@Test
	public void resolveWrongSelectionSpec() {
		final Random random = new Random();

		final var selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<Integer>(random);

		final var eaExecutionContextBuilder = EAExecutionContexts.<Integer>standard();
		eaExecutionContextBuilder.populationSize(100);
		final EAExecutionContext<Integer> eaExecutionContext = eaExecutionContextBuilder.build();

		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		assertThrows(IllegalArgumentException.class,
				() -> selectionPolicyHandler.resolve(eaExecutionContext,
						SIMPLE_MAXIMIZING_EA_CONFIGURATION,
						selectionPolicyHandlerResolver,
						RandomSelection.build()));
	}

	@Test
	public void resolve() {
		final Random random = new Random();

		final var selectionPolicyHandler = new TournamentNSGA2SelectionPolicyHandler<Integer>(random);

		final Builder<Integer> builder = TournamentNSGA2Selection.builder();
		builder.numCandidates(3);
		builder.distance((a, b, m) -> 1);
		builder.dominance(Comparator.naturalOrder());
		builder.numberObjectives(2);
		builder.objectiveComparator((m) -> Comparator.naturalOrder());
		final TournamentNSGA2Selection<Integer> nsga2Selection = builder.build();

		final var eaExecutionContextBuilder = EAExecutionContexts.<Integer>standard();
		eaExecutionContextBuilder.populationSize(100);
		final EAExecutionContext<Integer> eaExecutionContext = eaExecutionContextBuilder.build();

		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final Selector<Integer> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				nsga2Selection);

		assertNotNull(selector);
		assertTrue(selector instanceof TournamentNSGA2Selector<?>);
	}
}