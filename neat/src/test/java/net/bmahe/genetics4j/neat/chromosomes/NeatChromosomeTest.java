package net.bmahe.genetics4j.neat.chromosomes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.neat.Connection;

public class NeatChromosomeTest {

	@Test
	public void noInput() {
		assertThrows(IllegalArgumentException.class, () -> new NeatChromosome(0, 1, -10, 10, List.of()));
	}

	@Test
	public void negativeInput() {
		assertThrows(IllegalArgumentException.class, () -> new NeatChromosome(-2, 1, -10, 10, List.of()));
	}

	@Test
	public void noOutput() {
		assertThrows(IllegalArgumentException.class, () -> new NeatChromosome(1, 0, -10, 10, List.of()));
	}

	@Test
	public void negativeOutput() {
		assertThrows(IllegalArgumentException.class, () -> new NeatChromosome(2, -1, -10, 10, List.of()));
	}

	@Test
	public void minWeightGreaterThanMaxWeight() {
		assertThrows(IllegalArgumentException.class, () -> new NeatChromosome(2, 1, 10, -10, List.of()));
	}

	@Test
	public void nullConnections() {
		assertThrows(NullPointerException.class, () -> new NeatChromosome(2, 1, -10, 10, null));
	}

	@Test
	public void simple() {

		for (int numInput = 1; numInput < 5; numInput++) {
			for (int numOutput = 1; numOutput < 5; numOutput++) {

				final List<Connection> connections = new ArrayList<>();
				for (int i = 0; i < numOutput; i++) {
					// innovation is out of order to ensure they do get ordered
					final var connection = Connection.of(0, i, i / 10, true, numOutput - i);
					connections.add(connection);
				}

				final NeatChromosome neatChromosome = new NeatChromosome(numInput,
						numOutput,
						-5 - numInput,
						10 + numOutput,
						connections);

				assertEquals(connections.size() + numInput + numOutput, neatChromosome.getNumAlleles());
				assertEquals(-5 - numInput, neatChromosome.getMinWeightValue(), 0.001);
				assertEquals(10 + numOutput, neatChromosome.getMaxWeightValue(), 0.001);

				assertEquals(numInput, neatChromosome.getNumInputs());
				final Set<Integer> inputNodeIndices = neatChromosome.getInputNodeIndices();
				assertEquals(numInput, inputNodeIndices.size());
				for (int i = 0; i < numInput; i++) {
					assertTrue(inputNodeIndices.contains(i));
				}

				assertEquals(numOutput, neatChromosome.getNumOutputs());
				final Set<Integer> outputNodeIndices = neatChromosome.getOutputNodeIndices();
				assertEquals(numOutput, outputNodeIndices.size());
				for (int i = 0; i < numOutput; i++) {
					assertTrue(outputNodeIndices.contains(numInput + i),
							"Output " + i + " not found in output indices: " + outputNodeIndices);
				}

				assertEquals(Set.copyOf(connections), Set.copyOf(neatChromosome.getConnections()));

				if (numInput > 1 && numOutput > 1) {
					for (int i = 1; i < numOutput; i++) {

						assertTrue(neatChromosome.getConnections()
								.get(i)
								.innovation() >= neatChromosome.getConnections()
										.get(i - 1)
										.innovation());
					}

					final NeatChromosome otherNeatChromosome = new NeatChromosome(numInput,
							numOutput,
							-5 - numInput,
							10 + numOutput,
							connections);

					assertEquals(neatChromosome, otherNeatChromosome);
					assertEquals(neatChromosome.hashCode(), otherNeatChromosome.hashCode());

					assertNotEquals(neatChromosome,
							new NeatChromosome(numInput - 1, numOutput, -5 - numInput, 10 + numOutput, connections));

					assertNotEquals(neatChromosome,
							new NeatChromosome(numInput, numOutput - 1, -5 - numInput, 10 + numOutput, connections));

					assertNotEquals(neatChromosome,
							new NeatChromosome(numInput, numOutput, -4 - numInput, 10 + numOutput, connections));

					assertNotEquals(neatChromosome,
							new NeatChromosome(numInput, numOutput, -5 - numInput, 8 + numOutput, connections));

					assertNotEquals(neatChromosome,
							new NeatChromosome(numInput, numOutput, -5 - numInput, 10 + numOutput, connections.subList(0, 1)));

				}
			}
		}
	}
}