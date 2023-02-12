package net.bmahe.genetics4j.neat.chromosomes.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;

public class NeatEmptyChromosomeFactoryTest {

	@Test
	public void invalidCanHandle() {
		final var neatEmptyChromosomeFactory = new NeatEmptyChromosomeFactory();

		assertThrows(NullPointerException.class, () -> neatEmptyChromosomeFactory.canHandle(null));
		assertFalse(neatEmptyChromosomeFactory.canHandle(DoubleChromosomeSpec.of(10, 0, 10)));
	}

	@Test
	public void canHandle() {
		final var neatEmptyChromosomeFactory = new NeatEmptyChromosomeFactory();

		final var neatChromosomeSpec = NeatChromosomeSpec.of(3, 4, -10, 10);
		assertTrue(neatEmptyChromosomeFactory.canHandle(neatChromosomeSpec));
	}

	@Test
	public void invalidGenerate() {
		final var neatEmptyChromosomeFactory = new NeatEmptyChromosomeFactory();

		assertThrows(NullPointerException.class, () -> neatEmptyChromosomeFactory.generate(null));
		assertThrows(IllegalArgumentException.class,
				() -> neatEmptyChromosomeFactory.generate(DoubleChromosomeSpec.of(10, 0, 10)));
	}

	@Test
	public void generate() {
		final var neatEmptyChromosomeFactory = new NeatEmptyChromosomeFactory();
		final var neatChromosomeSpec = NeatChromosomeSpec.of(3, 4, -10, 10);

		final NeatChromosome neatChromosome = neatEmptyChromosomeFactory.generate(neatChromosomeSpec);
		assertNotNull(neatChromosome);

		assertNotNull(neatChromosome.getConnections());
		assertEquals(0,
				neatChromosome.getConnections()
						.size());

		assertEquals(neatChromosomeSpec.numInputs(), neatChromosome.getNumInputs());
		assertEquals(neatChromosomeSpec.numOutputs(), neatChromosome.getNumOutputs());
		assertEquals(neatChromosomeSpec.minWeightValue(), neatChromosome.getMinWeightValue(), 0.00001);
		assertEquals(neatChromosomeSpec.maxWeightValue(), neatChromosome.getMaxWeightValue(), 0.00001);
	}
}