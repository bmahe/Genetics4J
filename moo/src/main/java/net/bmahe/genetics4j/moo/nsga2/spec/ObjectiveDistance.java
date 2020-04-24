package net.bmahe.genetics4j.moo.nsga2.spec;

@FunctionalInterface
public interface ObjectiveDistance<T> {

	double distance(T a, T b, int objective);
}