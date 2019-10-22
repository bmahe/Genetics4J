package net.bmahe.genetics4j.core.mutation;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MultiMutations;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public class MultiMutationsPolicyHandler implements MutationPolicyHandler {

	private final Random random;

	public MultiMutationsPolicyHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
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
	public Mutator createMutator(final GeneticSystemDescriptor geneticSystemDescriptor, final GenotypeSpec genotypeSpec,
			final MutationPolicyHandlerResolver mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(geneticSystemDescriptor);
		Validate.notNull(genotypeSpec);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(MultiMutations.class, mutationPolicy);

		final MultiMutations multiMutations = (MultiMutations) mutationPolicy;

		final List<Mutator> mutators = multiMutations.mutationPolicies()
				.stream()
				.map((mp) -> {

					final MutationPolicyHandler mutationPolicyHandler = mutationPolicyHandlerResolver.resolve(mp);

					return mutationPolicyHandler.createMutator(geneticSystemDescriptor, genotypeSpec,
							mutationPolicyHandlerResolver, mp);
				})
				.collect(Collectors.toList());

		return new Mutator() {

			@Override
			public Genotype mutate(final Genotype original) {
				Validate.notNull(original);

				final int selectedMutatorIndex = random.nextInt(mutators.size());

				return mutators.get(selectedMutatorIndex)
						.mutate(original);
			}
		};
	}
}