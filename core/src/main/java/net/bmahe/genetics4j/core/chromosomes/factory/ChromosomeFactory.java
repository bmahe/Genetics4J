package net.bmahe.genetics4j.core.chromosomes.factory;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;

public interface ChromosomeFactory<T extends Chromosome> {

	boolean canHandle(ChromosomeSpec chromosomeSpec);

	T generate(final ChromosomeSpec chromosomeSpec);
}