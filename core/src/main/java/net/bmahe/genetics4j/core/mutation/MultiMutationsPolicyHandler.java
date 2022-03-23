package net.bmahe.genetics4j.core.mutation;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.MultiMutations;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public class MultiMutationsPolicyHandler implements MutationPolicyHandler {

	private final RandomGenerator randomGenerator;

	public MultiMutationsPolicyHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);

		if (mutationPolicy instanceof MultiMutations == false) {
			return false;
		}

		final MultiMutations multiMutations = (MultiMutations) mutationPolicy;

		return multiMutations.mutationPolicies()
				.stream()
				.allMatch((mp) -> mutationPolicyHandlerResolver.canHandle(mp));
	}

	@Override
	public Mutator createMutator(final AbstractEAExecutionContext eaExecutionContext,
			final AbstractEAConfiguration eaConfiguration,
			final MutationPolicyHandlerResolver mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(MultiMutations.class, mutationPolicy);

		final MultiMutations multiMutations = (MultiMutations) mutationPolicy;

		final List<Mutator> mutators = multiMutations.mutationPolicies()
				.stream()
				.map((mp) -> {

					final MutationPolicyHandler mutationPolicyHandler = mutationPolicyHandlerResolver.resolve(mp);

					return mutationPolicyHandler
							.createMutator(eaExecutionContext, eaConfiguration, mutationPolicyHandlerResolver, mp);
				})
				.collect(Collectors.toList());

		return new Mutator() {

			@Override
			public Genotype mutate(final Genotype original) {
				Validate.notNull(original);

				final int selectedMutatorIndex = randomGenerator.nextInt(mutators.size());

				return mutators.get(selectedMutatorIndex)
						.mutate(original);
			}
		};
	}
}