package net.bmahe.genetics4j.neat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;

public class FeedForwardNetworkTest {
	public static final Logger logger = LogManager.getLogger(FeedForwardNetworkTest.class);

	@Test
	public void simple() {

		//
		// 0 ----|
		// ______3 -----> 4 ----> 2
		// 1 ----|
		//
		//

		final var neatChromosome = new NeatChromosome(2,
				1,
				-10,
				10,
				List.of(Connection.of(0, 3, 0.2f, true, 0),
						Connection.of(3, 4, -0.2f, true, 0),
						Connection.of(1, 3, 1.5f, true, 0),
						Connection.of(4, 2, -4.1f, true, 0),
						Connection.of(0, 2, -1f, false, 0)));

		final List<List<Integer>> layers = NeatUtils.partitionLayersNodes(neatChromosome.getInputNodeIndices(),
				neatChromosome.getOutputNodeIndices(),
				neatChromosome.getConnections());

		logger.info("Chromosome: {}", neatChromosome);
		logger.info("layers: {}", layers);

		final var feedForwardNetwork = new FeedForwardNetwork(neatChromosome.getInputNodeIndices(),
				neatChromosome.getOutputNodeIndices(),
				neatChromosome.getConnections(),
				v -> (float) Math.tanh(v));

		final List<Pair<Map<Integer, Float>, Float>> tests = List.of(Pair.of(Map.of(0, 1f, 1, 1f), 0.640021595f),
				Pair.of(Map.of(0, 0f, 1, 0f), 0.f),
				Pair.of(Map.of(0, 1f, 1, 0f), 0.160367376f),
				Pair.of(Map.of(0, 0f, 1, 1f), 0.625639402f),
				Pair.of(Map.of(0, -5.6f, 1, 1f), 0.288472691f),
				Pair.of(Map.of(0, -5.6f, 1, -100.1f), -0.66917014f));

		for (final var entry : tests) {

			logger.info("Input {}", entry.getLeft());
			final Map<Integer, Float> outputValues = feedForwardNetwork.compute(entry.getLeft());
			logger.info("\tOutput Values: {}", outputValues);
			assertEquals(entry.getRight(), outputValues.get(2), 0.001f);
		}

	}
}