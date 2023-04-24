package net.bmahe.genetics4j.neat.mutation.chromosome;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.mutation.SwitchStateMutation;

public class NeatChromosomeSwitchStateHandlerTest {

	@Test
	public void constructor() {
		assertThrows(NullPointerException.class, () -> new NeatChromosomeSwitchStateHandler(null));
	}

	@Test
	public void mutateConnection() {

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();
		final NeatChromosomeSwitchStateHandler neatChromosomeSwitchStateHandler = new NeatChromosomeSwitchStateHandler(
				randomGenerator);

		assertThrows(NullPointerException.class,
				() -> neatChromosomeSwitchStateHandler.mutateConnection(null, null, null, 0));

		final List<Connection> originalConnections = List.of(Connection.of(0, 5, 0.5f, true, 0),
				Connection.of(5, 6, -8.5f, false, 1),
				Connection.of(6, 7, 2.5f, true, 2),
				Connection.of(7, 8, 9.5f, false, 3),
				Connection.of(8, 3, 4.5f, true, 4));

		final NeatChromosome originalChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		for (int i = 0; i < originalConnections.size(); i++) {
			final Connection oldConnection = originalConnections.get(i);

			final List<Connection> mutatedConnections = neatChromosomeSwitchStateHandler
					.mutateConnection(SwitchStateMutation.of(0.2), originalChromosome, oldConnection, i);
			assertNotNull(mutatedConnections);
			assertEquals(1, mutatedConnections.size());

			final Connection mutatedConnection = mutatedConnections.get(0);
			assertEquals(oldConnection.fromNodeIndex(), mutatedConnection.fromNodeIndex());
			assertEquals(oldConnection.toNodeIndex(), mutatedConnection.toNodeIndex());
			assertEquals(oldConnection.innovation(), mutatedConnection.innovation());
			assertEquals(oldConnection.weight(), mutatedConnection.weight(), 0.0001);

			assertEquals(!oldConnection.isEnabled(), mutatedConnection.isEnabled());
		}
	}
}