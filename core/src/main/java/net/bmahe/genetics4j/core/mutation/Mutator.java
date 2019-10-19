package net.bmahe.genetics4j.core.mutation;

import net.bmahe.genetics4j.core.Genotype;

public interface Mutator {

	Genotype mutate(Genotype original);
}