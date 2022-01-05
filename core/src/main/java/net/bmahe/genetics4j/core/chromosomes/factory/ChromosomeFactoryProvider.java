package net.bmahe.genetics4j.core.chromosomes.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;

@Value.Immutable
public abstract class ChromosomeFactoryProvider {

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
		Validate.notNull(chromosomeSpec);

		final List<ChromosomeFactory<? extends Chromosome>> chromosomeFactories = chromosomeFactories();

		return chromosomeFactories.stream()
				.dropWhile((chromosomeFactory) -> chromosomeFactory.canHandle(chromosomeSpec) == false)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Could not find a chromosome factory for chromosome spec: " + chromosomeSpec));
	}
}