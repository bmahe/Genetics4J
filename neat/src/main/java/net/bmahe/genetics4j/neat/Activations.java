package net.bmahe.genetics4j.neat;

import java.util.function.Function;

/**
 * Utility class providing common activation functions for NEAT (NeuroEvolution of Augmenting Topologies) neural networks.
 * 
 * <p>Activations contains a collection of mathematical functions commonly used as activation functions
 * in neural networks. These functions transform the weighted sum of inputs at each node into the node's
 * output value, introducing non-linearity that enables neural networks to learn complex patterns.
 * 
 * <p>Available activation functions:
 * <ul>
 * <li><strong>Linear</strong>: Simple linear transformation with slope and bias parameters</li>
 * <li><strong>Sigmoid</strong>: Logistic function providing smooth transition between 0 and 1</li>
 * <li><strong>Hyperbolic tangent</strong>: Smooth function providing output between -1 and 1</li>
 * <li><strong>Identity</strong>: Pass-through function for linear networks</li>
 * <li><strong>NEAT paper</strong>: Sigmoid variant with specific parameters from original NEAT research</li>
 * </ul>
 * 
 * <p>Function variations:
 * <ul>
 * <li><strong>Float versions</strong>: Optimized for float precision networks</li>
 * <li><strong>Double versions</strong>: Higher precision for sensitive applications</li>
 * <li><strong>Parameterized versions</strong>: Customizable function parameters</li>
 * <li><strong>Pre-configured versions</strong>: Common parameter combinations</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Use standard sigmoid activation
 * FeedForwardNetwork network = new FeedForwardNetwork(
 *     inputNodes, outputNodes, connections, Activations::sigmoid
 * );
 * 
 * // Custom sigmoid with different steepness
 * Function<Double, Double> customSigmoid = Activations.sigmoid(2.0);
 * FeedForwardNetwork steeperNetwork = new FeedForwardNetwork(
 *     inputNodes, outputNodes, connections, customSigmoid
 * );
 * 
 * // Hyperbolic tangent for outputs in [-1, 1] range
 * FeedForwardNetwork tanhNetwork = new FeedForwardNetwork(
 *     inputNodes, outputNodes, connections, Activations::tanh
 * );
 * 
 * // Linear activation for regression problems
 * FeedForwardNetwork linearNetwork = new FeedForwardNetwork(
 *     inputNodes, outputNodes, connections, Activations::identity
 * );
 * 
 * // Float versions for memory efficiency
 * Function<Float, Float> floatSigmoid = Activations.sigmoidFloat;
 * }</pre>
 * 
 * <p>Activation function characteristics:
 * <ul>
 * <li><strong>Sigmoid</strong>: Smooth, bounded [0,1], good for binary classification</li>
 * <li><strong>Tanh</strong>: Smooth, bounded [-1,1], zero-centered, often preferred over sigmoid</li>
 * <li><strong>Linear</strong>: Unbounded, preserves gradients, suitable for regression</li>
 * <li><strong>Identity</strong>: No transformation, useful for pass-through connections</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Float vs Double</strong>: Float versions use less memory and may be faster</li>
 * <li><strong>Function references</strong>: Pre-defined functions avoid object creation</li>
 * <li><strong>Mathematical operations</strong>: Optimized implementations for common cases</li>
 * <li><strong>Branch prediction</strong>: Simple functions improve CPU branch prediction</li>
 * </ul>
 * 
 * <p>Integration with NEAT evolution:
 * <ul>
 * <li><strong>Network evaluation</strong>: Applied to hidden and output nodes during forward propagation</li>
 * <li><strong>Fitness computation</strong>: Affects network behavior and resulting fitness</li>
 * <li><strong>Gradient flow</strong>: Function choice impacts learning and evolution dynamics</li>
 * <li><strong>Problem matching</strong>: Different problems benefit from different activation functions</li>
 * </ul>
 * 
 * @see FeedForwardNetwork
 * @see NeatChromosome
 * @see Function
 */
public class Activations {

	private Activations() {
	}

	/**
	 * Creates a linear activation function with specified slope and bias for float values.
	 * 
	 * <p>The linear function computes: f(x) = a * x + b
	 * 
	 * @param a the slope parameter
	 * @param b the bias parameter
	 * @return a linear activation function
	 */
	public static Function<Float, Float> linearFloat(final float a, final float b) {
		return (x) -> a * x + b;
	}

	/**
	 * Creates a linear activation function with specified slope and bias for double values.
	 * 
	 * <p>The linear function computes: f(x) = a * x + b
	 * 
	 * @param a the slope parameter
	 * @param b the bias parameter
	 * @return a linear activation function
	 */
	public static Function<Double, Double> linear(final double a, final double b) {
		return (x) -> a * x + b;
	}

	/**
	 * Creates a sigmoid activation function with specified steepness for float values.
	 * 
	 * <p>The sigmoid function computes: f(x) = 1 / (1 + exp(-a * x))
	 * <p>Output range: (0, 1)
	 * 
	 * @param a the steepness parameter (higher values create steeper transitions)
	 * @return a sigmoid activation function
	 */
	public static Function<Float, Float> sigmoidFloat(final float a) {
		return (x) -> 1.0f / (1.0f + (float) Math.exp(-a * x));
	}

	/**
	 * Creates a sigmoid activation function with specified steepness for double values.
	 * 
	 * <p>The sigmoid function computes: f(x) = 1 / (1 + exp(-a * x))
	 * <p>Output range: (0, 1)
	 * 
	 * @param a the steepness parameter (higher values create steeper transitions)
	 * @return a sigmoid activation function
	 */
	public static Function<Double, Double> sigmoid(final double a) {
		return (x) -> 1.0d / (1.0d + Math.exp(-a * x));
	}

	/** Standard sigmoid activation function for float values (steepness = 1.0). */
	public static Function<Float, Float> sigmoidFloat = sigmoidFloat(1.0f);
	
	/** Standard sigmoid activation function for double values (steepness = 1.0). */
	public static Function<Double, Double> sigmoid = sigmoid(1.0d);

	/** Identity activation function for float values (f(x) = x). */
	public static Function<Float, Float> identityFloat = linearFloat(1.0f, 0.0f);
	
	/** Identity activation function for double values (f(x) = x). */
	public static Function<Double, Double> identity = linear(1.0d, 0.0d);

	/** Hyperbolic tangent activation function for float values. Output range: (-1, 1). */
	public static Function<Float, Float> tanhFloat = (x) -> (float) Math.tanh(x);
	
	/** Hyperbolic tangent activation function for double values. Output range: (-1, 1). */
	public static Function<Double, Double> tanh = (x) -> Math.tanh(x);

	/** Sigmoid activation function with steepness 4.9 as used in the original NEAT paper (float version). */
	public static Function<Float, Float> neatPaperFloat = sigmoidFloat(4.9f);
	
	/** Sigmoid activation function with steepness 4.9 as used in the original NEAT paper (double version). */
	public static Function<Double, Double> neatPaper = sigmoid(4.9f);
}