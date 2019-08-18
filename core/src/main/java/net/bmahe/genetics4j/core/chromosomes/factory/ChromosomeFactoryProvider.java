package net.bmahe.genetics4j.core.chromosomes.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;

@Value.Immutable
public abstract class ChromosomeFactoryProvider {

	public abstract Random random();

	@Value.Default
	public List<ChromosomeFactory<? extends Chromosome>> chromosomeFactories() {
		return Arrays.asList(new BitChromosomeFactory(random()), new IntChromosomeFactory(random()));
	}

	public ChromosomeFactory<? extends Chromosome> provideChromosomeFactory(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);

		final List<ChromosomeFactory<? extends Chromosome>> chromosomeFactories = chromosomeFactories();

		return chromosomeFactories.stream()
				.dropWhile((chromosomeFactory) -> chromosomeFactory.canHandle(chromosomeSpec) == false).findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Could not find a chromosome factory for chromosome spec: " + chromosomeSpec));
	}
}