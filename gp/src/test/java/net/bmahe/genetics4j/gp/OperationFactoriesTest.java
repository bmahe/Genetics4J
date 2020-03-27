package net.bmahe.genetics4j.gp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;

public class OperationFactoriesTest {

	@Test(expected = NullPointerException.class)
	public void ofOperationSupplierNullAccept() {
		OperationFactories.of(null, Double.class, (inputSpec) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofOperationSupplierNullReturned() {
		OperationFactories.of(new Class[] {}, null, (inputSpec) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofOperationSupplierNullFunction() {
		OperationFactories.of(new Class[] {}, Double.class, null);
	}

	@Test
	public void ofOperationSupplier() {

		final Operation<String> operation = ImmutableOperation.of("operation",
				Collections.emptyList(),
				Float.class,
				(String[] str, Object[] obj) -> str[0].toLowerCase());

		final Supplier<Operation> operationSupplier = () -> operation;

		final OperationFactory operationFactory = OperationFactories
				.ofOperationSupplier(new Class[] { Integer.class }, Double.class, operationSupplier);

		assertNotNull(operationFactory);
		assertEquals(1, operationFactory.acceptedTypes().length);
		assertEquals(Integer.class, operationFactory.acceptedTypes()[0]);
		assertEquals(Double.class, operationFactory.returnedType());
		assertEquals(operation, operationFactory.build(ImmutableInputSpec.of(Collections.singletonList(Double.class))));
	}

	@Test(expected = NullPointerException.class)
	public void ofFunctionNullAccept() {
		OperationFactories.of(null, Double.class, (inputSpec) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofFunctionNullReturned() {
		OperationFactories.of(new Class[] {}, null, (inputSpec) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofFunctionNullFunction() {
		OperationFactories.of(new Class[] {}, Double.class, null);
	}

	@Test
	public void ofFunction() {

		final Operation<String> operation = ImmutableOperation.of("operation",
				Collections.emptyList(),
				Float.class,
				(String[] str, Object[] obj) -> str[0].toLowerCase());

		final Function<InputSpec, Operation> operationBuilder = (inputSpec) -> operation;

		final OperationFactory operationFactory = OperationFactories
				.of(new Class[] { Integer.class }, Double.class, operationBuilder);

		assertNotNull(operationFactory);
		assertEquals(1, operationFactory.acceptedTypes().length);
		assertEquals(Integer.class, operationFactory.acceptedTypes()[0]);
		assertEquals(Double.class, operationFactory.returnedType());
		assertEquals(operation, operationFactory.build(ImmutableInputSpec.of(Collections.singletonList(Double.class))));
	}

	@Test(expected = NullPointerException.class)
	public void ofBiFunctionNullName() {
		OperationFactories.of(null, new Class[] {}, Double.class, (Object[] input, Object[] parameters) -> null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ofBiFunctionEmptyName() {
		OperationFactories.of("", new Class[] {}, Double.class, (Object[] input, Object[] parameters) -> null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ofBiFunctionBlankName() {
		OperationFactories.of("   ", new Class[] {}, Double.class, (Object[] input, Object[] parameters) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofBiFunctionNullAccept() {
		OperationFactories.of("test", null, Double.class, (Object[] input, Object[] parameters) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofBiFunctionNullReturned() {
		OperationFactories.of("test", new Class[] {}, null, (Object[] input, Object[] parameters) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofBiFunctionNullFunction() {
		OperationFactories.of("test", new Class[] {}, Double.class, null);
	}

	@Test
	public void ofBiFunction() {

		final BiFunction<Object[], Object[], Object> compute = (Object[] str, Object[] obj) -> str[0].toString();

		final OperationFactory operationFactory = OperationFactories
				.of("test", new Class[] { Integer.class }, Double.class, compute);

		assertNotNull(operationFactory);
		assertEquals(1, operationFactory.acceptedTypes().length);
		assertEquals(Integer.class, operationFactory.acceptedTypes()[0]);
		assertEquals(Double.class, operationFactory.returnedType());
	}

	@Test(expected = NullPointerException.class)
	public void ofTerminalNullName() {
		OperationFactories.ofTerminal(null, Double.class, () -> null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ofTerminalEmptyName() {
		OperationFactories.ofTerminal("", Double.class, () -> null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ofTerminalBlankName() {
		OperationFactories.ofTerminal("  ", Double.class, () -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofTerminalNullReturned() {
		OperationFactories.ofTerminal("test", null, () -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofTerminalNullFunction() {
		OperationFactories.ofTerminal("test", Double.class, null);
	}

	@Test
	public void ofTerminal() {

		final String value = "This is a test";
		final Supplier<String> compute = () -> value;

		final OperationFactory operationFactory = OperationFactories.ofTerminal("terminal", String.class, compute);

		assertNotNull(operationFactory);
		assertNotNull(operationFactory.acceptedTypes());
		assertEquals(0, operationFactory.acceptedTypes().length);
		assertEquals(String.class, operationFactory.returnedType());
		assertEquals(value,
				operationFactory.build(ImmutableInputSpec.of(Collections.singletonList(Double.class)))
						.apply(new Object[] {}, new Object[] {}));
	}

	@Test(expected = NullPointerException.class)
	public void ofUnaryNullName() {
		OperationFactories.ofUnary(null, Double.class, Double.class, (p) -> null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ofUnaryEmptyName() {
		OperationFactories.ofUnary("", Double.class, Double.class, (p) -> null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ofUnaryBlankName() {
		OperationFactories.ofUnary("  ", Double.class, Double.class, (p) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofUnaryNullAccepted() {
		OperationFactories.ofUnary("test", null, Double.class, (p) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofUnaryNullReturned() {
		OperationFactories.ofUnary("test", Double.class, null, (p) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofUnaryNullFunction() {
		OperationFactories.ofUnary("test", Double.class, Double.class, null);
	}

	@Test
	public void ofUnary() {

		final String value = "This is a test";
		final Function<String, Integer> compute = (p) -> p.length();

		final OperationFactory operationFactory = OperationFactories
				.ofUnary("terminal", String.class, Integer.class, compute);

		assertNotNull(operationFactory);
		assertNotNull(operationFactory.acceptedTypes());
		assertEquals(1, operationFactory.acceptedTypes().length);
		assertEquals(String.class, operationFactory.acceptedTypes()[0]);
		assertEquals(Integer.class, operationFactory.returnedType());
		assertEquals(value.length(),
				operationFactory.build(ImmutableInputSpec.of(Collections.singletonList(Double.class)))
						.apply(new Object[] {}, new Object[] { value }));
	}

	@Test(expected = NullPointerException.class)
	public void ofBinaryNullName() {
		OperationFactories.ofBinary(null, Double.class, Double.class, Double.class, (p, v) -> null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ofBinaryEmptyName() {
		OperationFactories.ofBinary("", Double.class, Double.class, Double.class, (p, v) -> null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ofBinaryBlankName() {
		OperationFactories.ofBinary("   ", Double.class, Double.class, Double.class, (p, v) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofBinaryNullAccepted1() {
		OperationFactories.ofBinary("test", null, Double.class, Double.class, (p, v) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofBinaryNullAccepted2() {
		OperationFactories.ofBinary("test", Double.class, null, Double.class, (p, v) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofBinaryNullReturned() {
		OperationFactories.ofBinary("test", Double.class, Double.class, null, (p, v) -> null);
	}

	@Test(expected = NullPointerException.class)
	public void ofBinaryNullFunction() {
		OperationFactories.ofBinary("test", Double.class, Double.class, Double.class, null);
	}

	@Test
	public void ofBinary() {

		final String str = "2";
		final int intValue = 3;

		final BiFunction<String, Integer, Long> compute = (p, v) -> (long) Integer.parseInt(p) + v;

		final OperationFactory operationFactory = OperationFactories
				.ofBinary("terminal", String.class, Integer.class, Long.class, compute);

		assertNotNull(operationFactory);
		assertNotNull(operationFactory.acceptedTypes());
		assertEquals(2, operationFactory.acceptedTypes().length);
		assertEquals(String.class, operationFactory.acceptedTypes()[0]);
		assertEquals(Integer.class, operationFactory.acceptedTypes()[1]);
		assertEquals(Long.class, operationFactory.returnedType());
		assertEquals((long) Integer.parseInt(str) + intValue,
				operationFactory.build(ImmutableInputSpec.of(Collections.singletonList(Double.class)))
						.apply(new Object[] {}, new Object[] { str, intValue }));
	}

}