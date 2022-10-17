package net.bmahe.genetics4j.neat;

import java.util.function.Function;

public class Activations {

	private Activations() {
	}

	public static Function<Float, Float> linearFloat(final float a, final float b) {
		return (x) -> a * x + b;
	}

	public static Function<Double, Double> linear(final double a, final double b) {
		return (x) -> a * x + b;
	}

	public static Function<Float, Float> sigmoidFloat(final float a) {
		return (x) -> 1.0f / (1.0f + (float) Math.exp(-a * x));
	}

	public static Function<Double, Double> sigmoid(final double a) {
		return (x) -> 1.0d / (1.0d + Math.exp(-a * x));
	}

	public static Function<Float, Float> sigmoidFloat = sigmoidFloat(1.0f);
	public static Function<Double, Double> sigmoid = sigmoid(1.0d);

	public static Function<Float, Float> identityFloat = linearFloat(1.0f, 0.0f);
	public static Function<Double, Double> identity = linear(1.0d, 0.0d);

	public static Function<Float, Float> tanhFloat = (x) -> (float) Math.tanh(x);
	public static Function<Double, Double> tanh = (x) -> Math.tanh(x);

	public static Function<Float, Float> neatPaperFloat = sigmoidFloat(4.9f);
	public static Function<Double, Double> neatPaper = sigmoid(4.9f);
}