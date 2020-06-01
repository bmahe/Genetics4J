package net.bmahe.genetics4j.gp.spec.mutation;

import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

/**
 * Ensure no tree will have a greater depth than allowed
 * <p>
 * By default it will use the depth as specified in the Program definition, but
 * that can be overriden
 *
 */
@Value.Immutable
public interface TrimTree extends MutationPolicy {

	/**
	 * Override the max depth to enforce
	 * 
	 * @return
	 */
	@Value.Parameter
	@Value.Default
	public default Optional<Integer> maxDepth() {
		return Optional.empty();
	}

	@Value.Check
	default void check() {
		maxDepth().ifPresent(maxDepth -> Validate.isTrue(maxDepth > 0));
	}

	/**
	 * Build a TrimTree enforcing a specific max depth
	 * 
	 * @param maxDepth
	 * @return
	 */
	public static TrimTree of(final int maxDepth) {
		return ImmutableTrimTree.of(Optional.of(maxDepth));
	}

	/**
	 * Build a TrimTree using the default max depth as specified in the Program
	 * 
	 * @return
	 */
	public static TrimTree build() {
		return ImmutableTrimTree.builder().build();
	}
}