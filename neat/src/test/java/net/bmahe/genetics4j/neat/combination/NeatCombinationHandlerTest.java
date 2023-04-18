package net.bmahe.genetics4j.neat.combination;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorResolver;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.combination.NeatCombination;

public class NeatCombinationHandlerTest {

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new NeatCombinationHandler<>(null));
	}

	@Test
	public void canTest() {
		final RandomGenerator random = RandomGenerator.getDefault();
		final NeatCombinationHandler<Integer> neatCombinationHandler = new NeatCombinationHandler<>(random);

		final var eaExecutionContextBuilder = EAExecutionContexts.<Integer>standard();

		final var eaExecutionContext = eaExecutionContextBuilder.build();
		final var chromosomeCombinatorResolver = new ChromosomeCombinatorResolver<Integer>(eaExecutionContext);

		/**
		 * Validate that invalid input throws errors
		 */
		assertThrows(NullPointerException.class, () -> neatCombinationHandler.canHandle(null, null, null));
		assertThrows(NullPointerException.class,
				() -> neatCombinationHandler.canHandle(chromosomeCombinatorResolver, null, null));
		assertThrows(NullPointerException.class,
				() -> neatCombinationHandler.canHandle(chromosomeCombinatorResolver, SinglePointCrossover.build(), null));

		/**
		 * Valid input but not matching for the combination policy or the chromosome
		 * spec
		 */
		assertFalse(neatCombinationHandler
				.canHandle(chromosomeCombinatorResolver, SinglePointCrossover.build(), IntChromosomeSpec.of(10, -5, 5)));
		assertFalse(neatCombinationHandler.canHandle(chromosomeCombinatorResolver,
				SinglePointCrossover.build(),
				NeatChromosomeSpec.of(3, 5, -1.0f, 3.0f)));
		assertFalse(neatCombinationHandler
				.canHandle(chromosomeCombinatorResolver, NeatCombination.build(), DoubleChromosomeSpec.of(5, 0, 10)));

		/**
		 * Example of valid input that it can handle
		 */
		assertTrue(neatCombinationHandler.canHandle(chromosomeCombinatorResolver,
				NeatCombination.build(),
				NeatChromosomeSpec.of(3, 5, -1.0f, 3.0f)));
	}

	@Test
	public void resolve() {

		final RandomGenerator random = RandomGenerator.getDefault();
		final NeatCombinationHandler<Integer> neatCombinationHandler = new NeatCombinationHandler<>(random);

		final var eaExecutionContextBuilder = EAExecutionContexts.<Integer>standard();

		final var eaExecutionContext = eaExecutionContextBuilder.build();
		final var chromosomeCombinatorResolver = new ChromosomeCombinatorResolver<Integer>(eaExecutionContext);

		assertThrows(NullPointerException.class, () -> neatCombinationHandler.resolve(null, null, null));
		assertThrows(NullPointerException.class,
				() -> neatCombinationHandler.resolve(chromosomeCombinatorResolver, null, null));
		assertThrows(NullPointerException.class,
				() -> neatCombinationHandler.resolve(chromosomeCombinatorResolver, NeatCombination.build(), null));
		assertThrows(NullPointerException.class,
				() -> neatCombinationHandler
						.resolve(chromosomeCombinatorResolver, null, DoubleChromosomeSpec.of(5, 0, 10)));
		assertThrows(IllegalArgumentException.class,
				() -> neatCombinationHandler
						.resolve(chromosomeCombinatorResolver, NeatCombination.build(), DoubleChromosomeSpec.of(5, 0, 10)));
		assertThrows(IllegalArgumentException.class,
				() -> neatCombinationHandler.resolve(chromosomeCombinatorResolver,
						SinglePointCrossover.build(),
						NeatChromosomeSpec.of(3, 5, -1.0f, 3.0f)));

		final ChromosomeCombinator<Integer> chromosomeCombinator = neatCombinationHandler
				.resolve(chromosomeCombinatorResolver, NeatCombination.build(), NeatChromosomeSpec.of(3, 5, -1.0f, 3.0f));
		assertNotNull(chromosomeCombinator);
		assertTrue(chromosomeCombinator instanceof NeatChromosomeCombinator);
	}
}