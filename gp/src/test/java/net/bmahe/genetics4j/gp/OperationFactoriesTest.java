package net.bmahe.genetics4j.gp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

public class OperationFactoriesTest {

	@Test
	public void ofOperationSupplierNullAccept() {
		assertThrows(NullPointerException.class, () -> OperationFactories.of(null, Double.class, (inputSpec) -> null));
	}

	@Test
	public void ofOperationSupplierNullReturned() {
		assertThrows(NullPointerException.class, () -> OperationFactories.of(new Class[] {}, null, (inputSpec) -> null));
	}

	@Test
	public void ofOperationSupplierNullFunction() {
		assertThrows(NullPointerException.class, () -> OperationFactories.of(new Class[] {}, Double.class, null));
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

	@Test
	public void ofFunctionNullAccept() {
		assertThrows(NullPointerException.class, () -> OperationFactories.of(null, Double.class, (inputSpec) -> null));
	}

	@Test
	public void ofFunctionNullReturned() {
		assertThrows(NullPointerException.class, () -> OperationFactories.of(new Class[] {}, null, (inputSpec) -> null));
	}

	@Test
	public void ofFunctionNullFunction() {
		assertThrows(NullPointerException.class, () -> OperationFactories.of(new Class[] {}, Double.class, null));
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

	@Test
	public void ofBiFunctionNullName() {
		assertThrows(NullPointerException.class,
				() -> OperationFactories
						.of(null, new Class[] {}, Double.class, (Object[] input, Object[] parameters) -> null));
	}

	@Test
	public void ofBiFunctionEmptyName() {
		assertThrows(IllegalArgumentException.class,
				() -> OperationFactories
						.of("", new Class[] {}, Double.class, (Object[] input, Object[] parameters) -> null));
	}

	@Test
	public void ofBiFunctionBlankName() {
		assertThrows(IllegalArgumentException.class,
				() -> OperationFactories
						.of("   ", new Class[] {}, Double.class, (Object[] input, Object[] parameters) -> null));
	}

	@Test
	public void ofBiFunctionNullAccept() {
		assertThrows(NullPointerException.class,
				() -> OperationFactories.of("test", null, Double.class, (Object[] input, Object[] parameters) -> null));
	}

	@Test
	public void ofBiFunctionNullReturned() {
		assertThrows(NullPointerException.class,
				() -> OperationFactories.of("test", new Class[] {}, null, (Object[] input, Object[] parameters) -> null));
	}

	@Test
	public void ofBiFunctionNullFunction() {
		assertThrows(NullPointerException.class, () -> OperationFactories.of("test", new Class[] {}, Double.class, null));
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

	@Test
	public void ofTerminalNullName() {
		assertThrows(NullPointerException.class, () -> OperationFactories.ofTerminal(null, Double.class, () -> null));
	}

	@Test
	public void ofTerminalEmptyName() {
		assertThrows(IllegalArgumentException.class, () -> OperationFactories.ofTerminal("", Double.class, () -> null));
	}

	@Test
	public void ofTerminalBlankName() {
		assertThrows(IllegalArgumentException.class, () -> OperationFactories.ofTerminal("  ", Double.class, () -> null));
	}

	@Test
	public void ofTerminalNullReturned() {
		assertThrows(NullPointerException.class, () -> OperationFactories.ofTerminal("test", null, () -> null));
	}

	@Test
	public void ofTerminalNullFunction() {
		assertThrows(NullPointerException.class, () -> OperationFactories.ofTerminal("test", Double.class, null));
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

	@Test
	public void ofUnaryNullName() {
		assertThrows(NullPointerException.class,
				() -> OperationFactories.ofUnary(null, Double.class, Double.class, (p) -> null));
	}

	@Test
	public void ofUnaryEmptyName() {
		assertThrows(IllegalArgumentException.class,
				() -> OperationFactories.ofUnary("", Double.class, Double.class, (p) -> null));
	}

	@Test
	public void ofUnaryBlankName() {
		assertThrows(IllegalArgumentException.class,
				() -> OperationFactories.ofUnary("  ", Double.class, Double.class, (p) -> null));
	}

	@Test
	public void ofUnaryNullAccepted() {
		assertThrows(NullPointerException.class,
				() -> OperationFactories.ofUnary("test", null, Double.class, (p) -> null));
	}

	@Test
	public void ofUnaryNullReturned() {
		assertThrows(NullPointerException.class,
				() -> OperationFactories.ofUnary("test", Double.class, null, (p) -> null));
	}

	@Test
	public void ofUnaryNullFunction() {
		assertThrows(NullPointerException.class,
				() -> OperationFactories.ofUnary("test", Double.class, Double.class, null));
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

	@Test
	public void ofBinaryNullName() {
		assertThrows(NullPointerException.class,
				() -> OperationFactories.ofBinary(null, Double.class, Double.class, Double.class, (p, v) -> null));
	}

	@Test
	public void ofBinaryEmptyName() {
		assertThrows(IllegalArgumentException.class,
				() -> OperationFactories.ofBinary("", Double.class, Double.class, Double.class, (p, v) -> null));
	}

	@Test
	public void ofBinaryBlankName() {
		assertThrows(IllegalArgumentException.class,
				() -> OperationFactories.ofBinary("   ", Double.class, Double.class, Double.class, (p, v) -> null));
	}

	@Test
	public void ofBinaryNullAccepted1() {
		assertThrows(NullPointerException.class,
				() -> OperationFactories.ofBinary("test", null, Double.class, Double.class, (p, v) -> null));
	}

	@Test
	public void ofBinaryNullAccepted2() {
		assertThrows(NullPointerException.class,
				() -> OperationFactories.ofBinary("test", Double.class, null, Double.class, (p, v) -> null));
	}

	@Test
	public void ofBinaryNullReturned() {
		assertThrows(NullPointerException.class,
				() -> OperationFactories.ofBinary("test", Double.class, Double.class, null, (p, v) -> null));
	}

	@Test
	public void ofBinaryNullFunction() {
		assertThrows(NullPointerException.class,
				() -> OperationFactories.ofBinary("test", Double.class, Double.class, Double.class, null));
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