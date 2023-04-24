package net.bmahe.genetics4j.neat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder;
import net.bmahe.genetics4j.neat.chromosomes.factory.NeatConnectedChromosomeFactory;

public class NeatEAExecutionContextsTest {

	private <T extends Number & Comparable<T>> void validate(AbstractEAExecutionContext<T> eaExecutionContext) {
		assertNotNull(eaExecutionContext);

		assertTrue(eaExecutionContext.chromosomeFactoryProvider()
				.chromosomeFactories()
				.stream()
				.anyMatch(chromosomeFactory -> chromosomeFactory instanceof NeatConnectedChromosomeFactory));
	}

	@Test
	public void standard() {
		final Builder<Integer> standardBuilder = NeatEAExecutionContexts.<Integer>standard();
		EAExecutionContext<Integer> eaExecutionContext = standardBuilder.build();

		validate(eaExecutionContext);
	}

	@Test
	public void enrichWithNeat() {
		final Builder<Integer> builder = EAExecutionContexts.<Integer>forScalarFitness();
		final Builder<Integer> neatBuilder = NeatEAExecutionContexts.enrichWithNeat(builder);

		final EAExecutionContext<Integer> eaExecutionContext = neatBuilder.build();

		validate(eaExecutionContext);
	}
}