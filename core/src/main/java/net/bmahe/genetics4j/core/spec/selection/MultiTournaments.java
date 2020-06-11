package net.bmahe.genetics4j.core.spec.selection;

import java.util.Arrays;
import java.util.List;

import org.immutables.value.Value;

@Value.Style(overshadowImplementation = true)
@Value.Immutable
public interface MultiTournaments<T extends Comparable<T>> extends SelectionPolicy {

	@Value.Parameter
	List<Tournament<T>> tournaments();

	static <U extends Comparable<U>> MultiTournaments<U> of(final List<Tournament<U>> tournaments) {
		return ImmutableMultiTournaments.of(tournaments);
	}

	@SafeVarargs
	static <U extends Comparable<U>> MultiTournaments<U> of(final Tournament<U>... tournaments) {
		return ImmutableMultiTournaments.of(Arrays.asList(tournaments));
	}
}