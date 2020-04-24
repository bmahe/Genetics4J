package net.bmahe.genetics4j.moo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

public class FitnessVectorTest {

	@Test(expected = NullPointerException.class)
	public void ctorNoArg() {
		new FitnessVector<Double>(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void ctorNoComparatorArg() {
		new FitnessVector<Double>(List.of(2.0d), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorEmptyComparatorArg() {
		new FitnessVector<Double>(List.of(2.0d), Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorEmptyVectorArg() {
		new FitnessVector<Double>(Collections.emptyList(), List.of(Comparator.<Double>naturalOrder()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorNotSameSizeComparatorArg() {
		new FitnessVector<Double>(List.of(2.0d),
				List.of(Comparator.<Double>naturalOrder(), Comparator.<Double>reverseOrder()));
	}

	@Test(expected = NullPointerException.class)
	public void ctorNoArgArr() {
		new FitnessVector<Double>((Double) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorEmptyArg() {
		new FitnessVector<Double>(Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void negativeGetIndex() {
		final FitnessVector<Integer> fv1 = new FitnessVector<>(1, 2, 3, 4);

		fv1.get(-10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void outOfBoundGetIndex() {
		final FitnessVector<Integer> fv1 = new FitnessVector<>(1, 2, 3, 4);

		fv1.get(10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void negativeComparatorGetIndex() {
		final Comparator<Integer> naturalOrder = Comparator.naturalOrder();

		final FitnessVector<Integer> fv1 = new FitnessVector<>(List.of(1, 2, 3, 4),
				List.of(naturalOrder, naturalOrder, naturalOrder, naturalOrder));

		fv1.getComparator(-10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void outOfBoundComparatorGetIndex() {
		final Comparator<Integer> naturalOrder = Comparator.naturalOrder();

		final FitnessVector<Integer> fv1 = new FitnessVector<>(List.of(1, 2, 3, 4),
				List.of(naturalOrder, naturalOrder, naturalOrder, naturalOrder));

		fv1.getComparator(10);
	}

	@Test
	public void comparatorGetIndex() {
		final Comparator<Integer> naturalOrder = Comparator.naturalOrder();

		final FitnessVector<Integer> fv1 = new FitnessVector<>(List.of(1, 2, 3, 4),
				List.of(naturalOrder, naturalOrder, naturalOrder, naturalOrder));

		assertEquals(naturalOrder, fv1.getComparator(0));
		assertEquals(naturalOrder, fv1.getComparator(1));
		assertEquals(naturalOrder, fv1.getComparator(2));
		assertEquals(naturalOrder, fv1.getComparator(3));
	}

	@Test
	public void simpleChecks() {
		final FitnessVector<Integer> fv1 = new FitnessVector<>(1, 2, 3, 4);
		assertEquals(4, fv1.dimensions());

		assertEquals(1, fv1.get(0).intValue());
		assertEquals(2, fv1.get(1).intValue());
		assertEquals(3, fv1.get(2).intValue());
		assertEquals(4, fv1.get(3).intValue());

		final FitnessVector<Integer> fv2 = new FitnessVector<>(1, 2, 3, 4);
		assertEquals(0, fv1.compareTo(fv2));
		assertEquals(fv1, fv2);
		assertNotEquals(fv1, null);
		assertNotEquals(fv1, new FitnessVector<>(1, 2, 3, 4, 5));
		assertNotEquals(fv1, new FitnessVector<>(1, 2, 3, 5));
	}

	@Test
	public void dominanceChecks() {
		assertEquals(1, new FitnessVector<>(1, 2).compareTo(new FitnessVector<>(0, 2)));
		assertEquals(0, new FitnessVector<>(1, 2).compareTo(new FitnessVector<>(1, 2)));
		assertEquals(0, new FitnessVector<>(1, 2).compareTo(new FitnessVector<>(0, 3)));
		assertEquals(-1, new FitnessVector<>(1, 2).compareTo(new FitnessVector<>(2, 2)));
		assertEquals(-1, new FitnessVector<>(1, 2).compareTo(new FitnessVector<>(3, 2)));

		assertEquals(1,
				new FitnessVector<>(List.of(1, 2),
						List.of(Comparator.<Integer>reverseOrder(), Comparator.<Integer>reverseOrder()))
								.compareTo(new FitnessVector<>(3, 2)));

	}

	@Test(expected = IllegalArgumentException.class)
	public void dominanceCheckDifferentDimensions() {
		new FitnessVector<>(1, 2).compareTo(new FitnessVector<>(0, 2, 3));
	}
}