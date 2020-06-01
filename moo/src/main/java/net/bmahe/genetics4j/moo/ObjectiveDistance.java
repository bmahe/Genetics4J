package net.bmahe.genetics4j.moo;

/**
 * Provide a method to compute distances between to fitness scores along one
 * objective
 *
 * @param <T>
 */
@FunctionalInterface
public interface ObjectiveDistance<T> {

	/**
	 * Compute the distance between two fitness scores along one objective
	 * 
	 * @param a         First fitness score
	 * @param b         Second fitness score
	 * @param objective Objective along with the distance shall be computed
	 * @return distance
	 */
	double distance(T a, T b, int objective);
}