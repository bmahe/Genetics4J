package net.bmahe.genetics4j.gp.mutation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.program.StdProgramGenerator;
import net.bmahe.genetics4j.gp.spec.mutation.ImmutableRule;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramApplyRules;

public class ProgramRulesApplicatorPolicyHandlerTest {

	@Test(expected = NullPointerException.class)
	public void canHandleNoMutationPolicyHandlerResolver() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

		final ProgramRulesApplicatorPolicyHandler programRulesApplicatorPolicyHandler = new ProgramRulesApplicatorPolicyHandler();

		final ProgramApplyRules mutationPolicy = ProgramApplyRules
				.of(List.of(ImmutableRule.of((n) -> false, (p, t) -> t)));
		programRulesApplicatorPolicyHandler.canHandle(null, mutationPolicy);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleNoMutationPolicy() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

		final ProgramRulesApplicatorPolicyHandler programRulesApplicatorPolicyHandler = new ProgramRulesApplicatorPolicyHandler();

		final MutationPolicyHandlerResolver mockMutationPolicyHandlerResolver = mock(
				MutationPolicyHandlerResolver.class);
		programRulesApplicatorPolicyHandler.canHandle(mockMutationPolicyHandlerResolver, null);
	}

	@Test
	public void canHandle() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

		final ProgramApplyRules mutationPolicy = ProgramApplyRules
				.of(List.of(ImmutableRule.of((n) -> false, (p, t) -> t)));
		final ProgramRulesApplicatorPolicyHandler programRulesApplicatorPolicyHandler = new ProgramRulesApplicatorPolicyHandler();

		final MutationPolicyHandlerResolver mockMutationPolicyHandlerResolver = mock(
				MutationPolicyHandlerResolver.class);

		assertTrue(programRulesApplicatorPolicyHandler.canHandle(mockMutationPolicyHandlerResolver, mutationPolicy));
		assertFalse(programRulesApplicatorPolicyHandler.canHandle(mockMutationPolicyHandlerResolver,
				SwapMutation.of(0.2, 10, true)));
	}

	@Test
	public void createMutator() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

		final ProgramApplyRules mutationPolicy = ProgramApplyRules
				.of(List.of(ImmutableRule.of((n) -> false, (p, t) -> t)));
		final ProgramRulesApplicatorPolicyHandler programRulesApplicatorPolicyHandler = new ProgramRulesApplicatorPolicyHandler();

		final MutationPolicyHandlerResolver mockMutationPolicyHandlerResolver = mock(
				MutationPolicyHandlerResolver.class);
		final EAExecutionContext mockEaExecutionContext = mock(EAExecutionContext.class);
		final EAConfiguration mockEaConfiguration = mock(EAConfiguration.class);

		final Mutator mutator = programRulesApplicatorPolicyHandler.createMutator(mockEaExecutionContext,
				mockEaConfiguration,
				mockMutationPolicyHandlerResolver,
				mutationPolicy);
		assertNotNull(mutator);
		assertTrue(mutator instanceof ProgramRulesApplicatorMutator);
	}
}