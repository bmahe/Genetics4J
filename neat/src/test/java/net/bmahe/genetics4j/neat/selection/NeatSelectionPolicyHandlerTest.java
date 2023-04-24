package net.bmahe.genetics4j.neat.selection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.selection.TournamentSelector;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.neat.SpeciesIdGenerator;
import net.bmahe.genetics4j.neat.spec.selection.NeatSelection;

public class NeatSelectionPolicyHandlerTest {

	@Test
	public void constructor() {
		final RandomGenerator randomGenerator = RandomGenerator.getDefault();
		final SpeciesIdGenerator speciesIdGenerator = new SpeciesIdGenerator();

		assertThrows(NullPointerException.class, () -> new NeatSelectionPolicyHandler<>(null, null));
		assertThrows(NullPointerException.class, () -> new NeatSelectionPolicyHandler<>(randomGenerator, null));
		assertDoesNotThrow(() -> new NeatSelectionPolicyHandler<>(randomGenerator, speciesIdGenerator));
	}

	@Test
	public void canHandle() {
		final RandomGenerator randomGenerator = RandomGenerator.getDefault();
		final SpeciesIdGenerator speciesIdGenerator = new SpeciesIdGenerator();

		final NeatSelectionPolicyHandler<Integer> neatSelectionPolicyHandler = new NeatSelectionPolicyHandler<>(
				randomGenerator,
				speciesIdGenerator);

		assertThrows(NullPointerException.class, () -> neatSelectionPolicyHandler.canHandle(null));
		assertFalse(neatSelectionPolicyHandler.canHandle(Tournament.of(3)));
		assertTrue(neatSelectionPolicyHandler.canHandle(NeatSelection.ofDefault()));
	}

	@Test
	public void resolve() {

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();
		final SpeciesIdGenerator speciesIdGenerator = new SpeciesIdGenerator();

		final NeatSelectionPolicyHandler<Integer> neatSelectionPolicyHandler = new NeatSelectionPolicyHandler<>(
				randomGenerator,
				speciesIdGenerator);

		final AbstractEAExecutionContext<Integer> abstractEAExecutionContext = mock(AbstractEAExecutionContext.class);
		final AbstractEAConfiguration<Integer> abstractEAConfiguration = mock(AbstractEAConfiguration.class);

		final SelectionPolicyHandlerResolver<Integer> selectionPolicyHandlerResolver = mock(
				SelectionPolicyHandlerResolver.class);
		final SelectionPolicyHandler<Integer> selectionPolicyHandler = mock(SelectionPolicyHandler.class);

		when(selectionPolicyHandlerResolver.resolve(any())).thenReturn(selectionPolicyHandler);
		when(selectionPolicyHandler.resolve(any(), any(), any(), any())).thenReturn(mock(TournamentSelector.class));

		assertThrows(NullPointerException.class, () -> neatSelectionPolicyHandler.resolve(null, null, null, null));
		assertThrows(NullPointerException.class,
				() -> neatSelectionPolicyHandler.resolve(abstractEAExecutionContext, null, null, null));
		assertThrows(NullPointerException.class,
				() -> neatSelectionPolicyHandler.resolve(abstractEAExecutionContext, abstractEAConfiguration, null, null));
		assertThrows(NullPointerException.class,
				() -> neatSelectionPolicyHandler
						.resolve(abstractEAExecutionContext, abstractEAConfiguration, selectionPolicyHandlerResolver, null));
		assertThrows(IllegalArgumentException.class,
				() -> neatSelectionPolicyHandler.resolve(abstractEAExecutionContext,
						abstractEAConfiguration,
						selectionPolicyHandlerResolver,
						Tournament.of(3)));

		final Selector<Integer> selector = neatSelectionPolicyHandler.resolve(abstractEAExecutionContext,
				abstractEAConfiguration,
				selectionPolicyHandlerResolver,
				NeatSelection.ofDefault());
		assertNotNull(selector);

	}
}