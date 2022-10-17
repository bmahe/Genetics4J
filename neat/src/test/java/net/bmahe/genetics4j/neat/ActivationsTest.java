package net.bmahe.genetics4j.neat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ActivationsTest {

	public final static double EPSILON = 0.0001d;

	@Test
	public void verifyNotNull() {

		assertNotNull(Activations.identity);
		assertNotNull(Activations.identityFloat);

		assertNotNull(Activations.neatPaper);
		assertNotNull(Activations.neatPaperFloat);

		assertNotNull(Activations.tanh);
		assertNotNull(Activations.tanhFloat);

		assertNotNull(Activations.sigmoid);
		assertNotNull(Activations.sigmoidFloat);

		for (int i = -100; i < 222; i++) {
			final double iDouble = (double) (i / 100.0d) * (i % 2 == 0 ? 12345.0d : 1.0d);
			final float iFloat = (float) iDouble;

			assertNotNull(Activations.sigmoid(i));
			assertNotNull(Activations.sigmoidFloat(iFloat));

			assertNotNull(Activations.linear(i, i * 2));
			assertNotNull(Activations.linearFloat(iFloat, iFloat * 2));

		}
	}

	@Test
	public void verifyIdentity() {

		for (int i = -100; i < 222; i++) {
			final double iDouble = (double) (i / 100.0d) * (i % 2 == 0 ? 12345.0d : 1.0d);
			final float iFloat = (float) iDouble;

			assertEquals(iDouble, Activations.identity.apply(iDouble), EPSILON);
			assertEquals(iFloat, Activations.identityFloat.apply(iFloat), EPSILON);
		}
	}

	@Test
	public void verifyTanh() {

		for (int i = -100; i < 222; i++) {
			final double iDouble = (double) (i / 100.0d) * (i % 2 == 0 ? 12345.0d : 1.0d);
			final float iFloat = (float) iDouble;

			final double expected = Math.tanh(iDouble);
			final float expectedFloat = (float) Math.tanh(iFloat);

			assertEquals(expected, Activations.tanh.apply(iDouble), EPSILON);
			assertEquals(expectedFloat, Activations.tanhFloat.apply(iFloat), EPSILON);
		}
	}

	@Test
	public void verifyNeatSigmoid() {

		for (int i = -100; i < 222; i++) {
			final double iDouble = (double) (i / 100.0d) * (i % 2 == 0 ? 12345.0d : 1.0d);
			final float iFloat = (float) iDouble;

			final double expected = 1.0d / (1.0d + Math.exp(-4.9 * iDouble));
			final float expectedFloat = 1.0f / (1.0f + (float) Math.exp(-4.9 * iFloat));

			assertEquals(expected, Activations.neatPaper.apply(iDouble), EPSILON);
			assertEquals(expectedFloat, Activations.neatPaperFloat.apply(iFloat), EPSILON);
		}
	}

	@Test
	public void verifyLinear() {

		for (int i = -100; i < 222; i++) {
			final double iDouble = (double) (i / 100.0d) * (i % 2 == 0 ? 12345.0d : 1.0d);
			final float iFloat = (float) iDouble;

			final double a = i % 2 == 0 ? 123d : -2d;
			final float aFloat = (float) a;

			final double b = i % 2 == 0 ? -50.02d : 0.004d;
			final float bFloat = (float) b;

			final double expected = iDouble * a + b;
			final double expectedFloat = iFloat * aFloat + bFloat;
			assertEquals(expected,
					Activations.linear(a, b)
							.apply(iDouble),
					EPSILON);
			assertEquals(expectedFloat,
					Activations.linearFloat(aFloat, bFloat)
							.apply(iFloat),
					EPSILON);
		}
	}
}