package net.bmahe.genetics4j.neat;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

@Value.Immutable
public interface Connection {

	@Value.Parameter
	int fromNodeIndex();

	@Value.Parameter
	int toNodeIndex();

	@Value.Parameter
	float weight();

	@Value.Parameter
	boolean isEnabled();

	@Value.Parameter
	int innovation();

	@Value.Check
	default void check() {
		Validate.isTrue(fromNodeIndex() >= 0);
		Validate.isTrue(toNodeIndex() >= 0);
		Validate.isTrue(innovation() >= 0);
	}

	static class Builder extends ImmutableConnection.Builder {
	}

	static Builder builder() {
		return new Builder();
	}

	static Connection of(final int from, final int to, final float weight, final boolean isEnabled,
			final int innovation) {
		return ImmutableConnection.of(from, to, weight, isEnabled, innovation);
	}

	static Connection copyOf(Connection original) {
		return ImmutableConnection.copyOf(original);
	}
}