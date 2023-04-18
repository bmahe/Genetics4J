package net.bmahe.genetics4j.neat.combination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.combination.NeatCombination;

public class NeatChromosomeCombinatorTest {

	@Test
	public void constructorNeedsRandom() {
		assertThrows(NullPointerException.class, () -> new NeatChromosomeCombinator<>(null, NeatCombination.build()));
	}

	@Test
	public void constructorNeedsCombinationPolicy() {
		assertThrows(NullPointerException.class,
				() -> new NeatChromosomeCombinator<>(RandomGenerator.getDefault(), null));
	}

	private void shouldReEnable(final boolean expectEnable) {
		final RandomGenerator randomGenerator = RandomGenerator.getDefault();

		final NeatCombination neatCombination = NeatCombination.builder()
				.reenableGeneInheritanceThresold(expectEnable ? 1.0d : 0.0d)
				.build();

		final NeatChromosomeCombinator<Integer> neatChromosomeCombinator = new NeatChromosomeCombinator<>(randomGenerator,
				neatCombination);

		final Connection connectionDisabled = Connection.of(0, 10, 0.2f, false, 0);
		final Connection connectionEnabled = Connection.of(0, 10, 0.2f, true, 0);

		assertThrows(NullPointerException.class, () -> neatChromosomeCombinator.shouldReEnable(null, null));
		assertThrows(NullPointerException.class, () -> neatChromosomeCombinator.shouldReEnable(connectionDisabled, null));
		assertThrows(NullPointerException.class, () -> neatChromosomeCombinator.shouldReEnable(null, connectionDisabled));

		assertTrue(neatChromosomeCombinator.shouldReEnable(connectionDisabled, connectionDisabled) == false);
		assertTrue(neatChromosomeCombinator.shouldReEnable(connectionDisabled, connectionEnabled) == expectEnable);
		assertTrue(neatChromosomeCombinator.shouldReEnable(connectionEnabled, connectionDisabled) == false);
		assertTrue(neatChromosomeCombinator.shouldReEnable(connectionEnabled, connectionEnabled) == false);
	}

	@Test
	public void shouldNeverReEnable() {

		shouldReEnable(false);
	}

	@Test
	public void shouldAlwaysReEnable() {

		shouldReEnable(true);
	}

	public void combine(final boolean pickBest, final boolean reEnableGene) {

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();

		final NeatCombination neatCombination = NeatCombination.builder()
				.inheritanceThresold(pickBest ? 1.0d : 0.0d)
				.reenableGeneInheritanceThresold(reEnableGene ? 1.0d : 0.0d)
				.build();

		final NeatChromosomeCombinator<Integer> neatChromosomeCombinator = new NeatChromosomeCombinator<>(randomGenerator,
				neatCombination);

		final var neatChromosomeA = new NeatChromosome(3,
				3,
				-10,
				10,
				List.of(Connection.of(0, 1, 0, false, 0),
						Connection.of(0, 2, 0, false, 1),
						Connection.of(0, 3, 0, false, 2),
						Connection.of(0, 4, 0, false, 3)));

		/**
		 * Start after and has excess gene
		 */
		final var neatChromosomeB = new NeatChromosome(3,
				3,
				-10,
				10,
				List.of(Connection.of(1, 2, 0, true, 1),
						Connection.of(1, 3, 0, true, 2),
						Connection.of(1, 4, 0, true, 3),
						Connection.of(1, 5, 0, true, 4)));

		final AbstractEAConfiguration<Integer> eaConfiguration = mock(AbstractEAConfiguration.class);
		when(eaConfiguration.fitnessComparator()).thenReturn((a, b) -> -1);

		final List<Chromosome> combined = neatChromosomeCombinator
				.combine(eaConfiguration, neatChromosomeA, 0, neatChromosomeB, 0);

		assertNotNull(combined);
		assertTrue(combined.size() == 1);
		final NeatChromosome combinedNeat = (NeatChromosome) combined.get(0);

		final List<Connection> baseExpected = pickBest ? neatChromosomeB.getConnections()
				: neatChromosomeA.getConnections();

		final List<Connection> expected;
		if (pickBest) {
			expected = baseExpected.stream()
					.map(connection -> (Connection) Connection.builder()
							.from(connection)
							.isEnabled(reEnableGene ? true : connection.isEnabled())
							.build())
					.toList();

		} else {
			expected = baseExpected.subList(1, baseExpected.size())
					.stream()
					.map(connection -> (Connection) Connection.builder()
							.from(connection)
							.isEnabled(reEnableGene ? true : connection.isEnabled())
							.build())
					.collect(Collectors.toCollection(() -> new ArrayList<>()));
			expected.add(neatChromosomeB.getConnections()
					.get(neatChromosomeB.getConnections()
							.size() - 1));
		}
		assertEquals(expected, combinedNeat.getConnections());

		// Let's reverse A and B
		final List<Chromosome> combined2 = neatChromosomeCombinator
				.combine(eaConfiguration, neatChromosomeB, 0, neatChromosomeA, 0);

		assertNotNull(combined2);
		assertTrue(combined2.size() == 1);
		final NeatChromosome combinedNeat2 = (NeatChromosome) combined2.get(0);

		final List<Connection> baseExpected2 = pickBest ? neatChromosomeA.getConnections()
				: neatChromosomeB.getConnections();

		/**
		 * Note: The first connection is an excess and does not match with the other
		 * chromosome. So it will never re-enable
		 */
		final List<Connection> expected2;

		if (pickBest) {
			expected2 = baseExpected2.stream()
					.map(connection -> (Connection) Connection.builder()
							.from(connection)
							.isEnabled(reEnableGene && connection.innovation() > 0 ? true : connection.isEnabled())
							.build())
					.toList();
		} else {
			expected2 = baseExpected2.subList(0, baseExpected.size() - 1)
					.stream()
					.map(connection -> (Connection) Connection.builder()
							.from(connection)
							.isEnabled(reEnableGene && connection.innovation() > 0 ? true : connection.isEnabled())
							.build())
					.collect(Collectors.toCollection(() -> new ArrayList<>()));
			expected2.add(0,
					neatChromosomeA.getConnections()
							.get(0));
		}
		assertEquals(expected2, combinedNeat2.getConnections());
	}

	@Test
	public void combinePickWorstNoReEnable() {
		combine(false, false);
	}

	@Test
	public void combinePickBestNoReEnable() {
		combine(true, false);
	}

	@Test
	public void combinePickWorstReEnable() {
		combine(false, true);
	}

	@Test
	public void combinePickBestReEnable() {
		combine(true, true);
	}
}