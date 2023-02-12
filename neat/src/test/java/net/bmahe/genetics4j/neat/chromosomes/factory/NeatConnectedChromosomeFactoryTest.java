package net.bmahe.genetics4j.neat.chromosomes.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.InnovationManager;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;

public class NeatConnectedChromosomeFactoryTest {

	private final RandomGenerator randomGenerator = RandomGenerator.getDefault();
	private final InnovationManager innovationManager = new InnovationManager();

	@Test
	public void validateConstructor() {
		assertThrows(NullPointerException.class, () -> new NeatConnectedChromosomeFactory(null, innovationManager));
		assertThrows(NullPointerException.class, () -> new NeatConnectedChromosomeFactory(randomGenerator, null));

		final var neatConnectedChromosomeFactory = new NeatConnectedChromosomeFactory(randomGenerator, innovationManager);
	}

	@Test
	public void invalidCanHandle() {

		final var neatConnectedChromosomeFactory = new NeatConnectedChromosomeFactory(randomGenerator, innovationManager);

		assertThrows(NullPointerException.class, () -> neatConnectedChromosomeFactory.canHandle(null));
		assertFalse(neatConnectedChromosomeFactory.canHandle(DoubleChromosomeSpec.of(10, 0, 10)));
	}

	@Test
	public void canHandle() {
		final var neatConnectedChromosomeFactory = new NeatConnectedChromosomeFactory(randomGenerator, innovationManager);

		final var neatChromosomeSpec = NeatChromosomeSpec.of(3, 4, -10, 10);
		assertTrue(neatConnectedChromosomeFactory.canHandle(neatChromosomeSpec));
	}

	@Test
	public void invalidGenerate() {
		final var neatConnectedChromosomeFactory = new NeatConnectedChromosomeFactory(randomGenerator, innovationManager);

		assertThrows(NullPointerException.class, () -> neatConnectedChromosomeFactory.generate(null));
		assertThrows(IllegalArgumentException.class,
				() -> neatConnectedChromosomeFactory.generate(DoubleChromosomeSpec.of(10, 0, 10)));
	}

	@Test
	public void generate() {
		final RandomGenerator spyRandomGenerator = spy(randomGenerator);

		final var neatConnectedChromosomeFactory = new NeatConnectedChromosomeFactory(spyRandomGenerator,
				innovationManager);

		final int numInputs = 3;
		final int numOutputs = 5;
		final var neatChromosomeSpec = NeatChromosomeSpec.of(numInputs, numOutputs, -10, 10);

		final NeatChromosome neatChromosome = neatConnectedChromosomeFactory.generate(neatChromosomeSpec);
		assertNotNull(neatChromosome);

		assertEquals(neatChromosomeSpec.numInputs(), neatChromosome.getNumInputs());
		assertEquals(neatChromosomeSpec.numOutputs(), neatChromosome.getNumOutputs());
		assertEquals(neatChromosomeSpec.minWeightValue(), neatChromosome.getMinWeightValue(), 0.00001);
		assertEquals(neatChromosomeSpec.maxWeightValue(), neatChromosome.getMaxWeightValue(), 0.00001);

		assertNotNull(neatChromosome.getConnections());
		assertEquals(numInputs * numOutputs,
				neatChromosome.getConnections()
						.size());

		verify(spyRandomGenerator, times(numInputs * numOutputs)).nextFloat(anyFloat(), anyFloat());

		final var connections = neatChromosome.getConnections();
		for (final Connection connection : connections) {
			assertTrue(connection.isEnabled());
			assertTrue(connection.weight() >= neatChromosomeSpec.minWeightValue());
			assertTrue(connection.weight() <= neatChromosomeSpec.maxWeightValue());
			assertEquals(innovationManager.computeNewId(connection.fromNodeIndex(), connection.toNodeIndex()),
					connection.innovation());
		}
	}
}