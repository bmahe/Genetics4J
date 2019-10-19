package net.bmahe.genetics4j.core.spec.selection;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

@Value.Immutable
public abstract class MultiSelections implements SelectionPolicy {

	@Value.Parameter
	public abstract List<SelectionPolicy> selectionPolicies();

	@Value.Check
	protected void check() {
		Validate.notNull(selectionPolicies());
	}

	public static MultiSelections of(final List<SelectionPolicy> selectionPolicies) {
		return ImmutableMultiSelections.of(selectionPolicies);
	}

	public static MultiSelections of(final SelectionPolicy... selectionPolicies) {
		return ImmutableMultiSelections.of(Arrays.asList(selectionPolicies));
	}
}