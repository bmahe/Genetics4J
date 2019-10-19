package net.bmahe.genetics4j.core.mutation;

import net.bmahe.genetics4j.core.Genotype;

@FunctionalInterface
public interface Mutator {

	Genotype mutate(Genotype original);
}