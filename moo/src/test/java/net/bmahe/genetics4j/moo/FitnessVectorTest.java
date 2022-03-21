package net.bmahe.genetics4j.moo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

public class FitnessVectorTest {

	@Test
	public void ctorNoArg() {
		assertThrows(NullPointerException.class, () -> new FitnessVector<Double>(null, null));
	}

	@Test
	public void ctorNoComparatorArg() {
		assertThrows(NullPointerException.class, () -> new FitnessVector<Double>(List.of(2.0d), null));
	}

	@Test
	public void ctorEmptyComparatorArg() {
		assertThrows(IllegalArgumentException.class,
				() -> new FitnessVector<Double>(List.of(2.0d), Collections.emptyList()));
	}

	@Test
	public void ctorEmptyVectorArg() {
		assertThrows(IllegalArgumentException.class,
				() -> new FitnessVector<Double>(Collections.emptyList(), List.of(Comparator.<Double>naturalOrder())));
	}

	@Test
	public void ctorNotSameSizeComparatorArg() {
		assertThrows(IllegalArgumentException.class,
				() -> new FitnessVector<Double>(List.of(2.0d),
						List.of(Comparator.<Double>naturalOrder(), Comparator.<Double>reverseOrder())));
	}

	@Test
	public void ctorNoArgArr() {
		assertThrows(NullPointerException.class, () -> new FitnessVector<Double>((Double) null));
	}

	@Test
	public void ctorEmptyArg() {
		assertThrows(IllegalArgumentException.class, () -> new FitnessVector<Double>(Collections.emptyList()));
	}

	@Test
	public void negativeGetIndex() {
		final FitnessVector<Integer> fv1 = new FitnessVector<>(1, 2, 3, 4);

		assertThrows(IllegalArgumentException.class, () -> fv1.get(-10));
	}

	@Test
	public void outOfBoundGetIndex() {
		final FitnessVector<Integer> fv1 = new FitnessVector<>(1, 2, 3, 4);

		assertThrows(IllegalArgumentException.class, () -> fv1.get(10));
	}

	@Test
	public void negativeComparatorGetIndex() {
		final Comparator<Integer> naturalOrder = Comparator.naturalOrder();

		final FitnessVector<Integer> fv1 = new FitnessVector<>(List.of(1, 2, 3, 4),
				List.of(naturalOrder, naturalOrder, naturalOrder, naturalOrder));

		assertThrows(IllegalArgumentException.class, () -> fv1.getComparator(-10));
	}

	@Test
	public void outOfBoundComparatorGetIndex() {
		final Comparator<Integer> naturalOrder = Comparator.naturalOrder();

		final FitnessVector<Integer> fv1 = new FitnessVector<>(List.of(1, 2, 3, 4),
				List.of(naturalOrder, naturalOrder, naturalOrder, naturalOrder));

		assertThrows(IllegalArgumentException.class, () -> fv1.getComparator(10));
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

		assertEquals(1,
				fv1.get(0)
						.intValue());
		assertEquals(2,
				fv1.get(1)
						.intValue());
		assertEquals(3,
				fv1.get(2)
						.intValue());
		assertEquals(4,
				fv1.get(3)
						.intValue());

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
		assertEquals(-1,
				new FitnessVector<>(4f, 5.0201883f, 0.51985145f)
						.compareTo(new FitnessVector<>(4f, 5.0217524f, 0.54367197f)));

		assertEquals(1,
				new FitnessVector<>(List.of(1, 2),
						List.of(Comparator.<Integer>reverseOrder(), Comparator.<Integer>reverseOrder()))
								.compareTo(new FitnessVector<>(3, 2)));
	}

	@Test
	public void dominanceCheckDifferentDimensions() {
		assertThrows(IllegalArgumentException.class,
				() -> new FitnessVector<>(1, 2).compareTo(new FitnessVector<>(0, 2, 3)));
	}
}