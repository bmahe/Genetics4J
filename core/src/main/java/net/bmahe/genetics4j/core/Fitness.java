package net.bmahe.genetics4j.core;

@FunctionalInterface
public interface Fitness {

	Double compute(Genotype genotype);
}