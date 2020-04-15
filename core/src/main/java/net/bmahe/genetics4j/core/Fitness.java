package net.bmahe.genetics4j.core;

@FunctionalInterface
public interface Fitness<T extends Comparable<T>> {

	T compute(Genotype genotype);
}