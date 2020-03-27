package net.bmahe.genetics4j.gp.spec.mutation;

import java.util.List;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

@Value.Immutable
public interface ProgramApplyRules extends MutationPolicy {

	@Value.Parameter
	List<Rule> rules();
	
	public static ProgramApplyRules of(final List<Rule> rules) {
		return ImmutableProgramApplyRules.of(rules);
	}
}