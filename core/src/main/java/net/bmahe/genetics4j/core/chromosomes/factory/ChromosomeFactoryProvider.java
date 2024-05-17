package net.bmahe.genetics4j.core.chromosomes.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.random.RandomGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;

@Value.Immutable
public abstract class ChromosomeFactoryProvider {
	final static public Logger logger = LogManager.getLogger(ChromosomeFactoryProvider.class);

	public abstract RandomGenerator randomGenerator();

	@Value.Default
	public List<ChromosomeFactory<? extends Chromosome>> defaultChromosomeFactories() {
		final var randomGenerator = randomGenerator();

		return Arrays.asList(new BitChromosomeFactory(randomGenerator),
				new IntChromosomeFactory(randomGenerator),
				new DoubleChromosomeFactory(randomGenerator),
				new FloatChromosomeFactory(randomGenerator));
	}

	public abstract List<Function<ChromosomeFactoryProvider, ChromosomeFactory<? extends Chromosome>>> chromosomeFactoriesGenerator();

	@Value.Derived
	public List<ChromosomeFactory<? extends Chromosome>> chromosomeFactories() {

		final List<ChromosomeFactory<? extends Chromosome>> chromosomeFactories = new ArrayList<>();

		final List<ChromosomeFactory<? extends Chromosome>> defaultChromosomeFactories = defaultChromosomeFactories();
		if (defaultChromosomeFactories.isEmpty() == false) {
			chromosomeFactories.addAll(defaultChromosomeFactories);
		}

		chromosomeFactoriesGenerator().stream()
				.map(generator -> generator.apply(this))
				.forEach(cf -> chromosomeFactories.add(cf));

		return chromosomeFactories;
	}

	public ChromosomeFactory<? extends Chromosome> provideChromosomeFactory(final ChromosomeSpec chromosomeSpec) {
		Objects.requireNonNull(chromosomeSpec);

		final List<ChromosomeFactory<? extends Chromosome>> chromosomeFactories = chromosomeFactories();

		logger.trace("Known chromosome factories: {}", chromosomeFactories);

		return chromosomeFactories.stream()
				.dropWhile((chromosomeFactory) -> chromosomeFactory.canHandle(chromosomeSpec) == false)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Could not find a chromosome factory for chromosome spec: " + chromosomeSpec));
	}

	public static ImmutableChromosomeFactoryProvider.Builder builder() {
		return ImmutableChromosomeFactoryProvider.builder();
	}
}