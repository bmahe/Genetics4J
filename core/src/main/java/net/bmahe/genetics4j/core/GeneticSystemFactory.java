package net.bmahe.genetics4j.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public class GeneticSystemFactory {

	private ChromosomeCombinator findMatchingChromosomeCombinatorHandler(
			final GeneticSystemDescriptor geneticSystemDescriptor, final CombinationPolicy combinationPolicy,
			final ChromosomeSpec chromosome) {
		Validate.notNull(geneticSystemDescriptor);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		final List<ChromosomeCombinator> chromosomeCombinators = geneticSystemDescriptor.chromosomeCombinators();

		return chromosomeCombinators.stream()
				.dropWhile((cch) -> cch.canHandle(combinationPolicy, chromosome) == false)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Could not find suitable chromosome combination policy handler for policy " + combinationPolicy
								+ " and chromosome " + chromosome));

	}

	public GeneticSystem from(final GenotypeSpec genotypeSpec, final GeneticSystemDescriptor geneticSystemDescriptor) {
		Validate.notNull(genotypeSpec);
		Validate.notNull(geneticSystemDescriptor);

		final SelectionPolicyHandlerResolver selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver(
				geneticSystemDescriptor);

		final SelectionPolicyHandler parentSelectionPolicyHandler = selectionPolicyHandlerResolver
				.resolve(genotypeSpec.parentSelectionPolicy());
		final Selector parentSelector = parentSelectionPolicyHandler.resolve(geneticSystemDescriptor, genotypeSpec,
				selectionPolicyHandlerResolver, genotypeSpec.parentSelectionPolicy());

		final SelectionPolicyHandler survivorSelectionPolicyHandler = selectionPolicyHandlerResolver
				.resolve(genotypeSpec.survivorSelectionPolicy());
		final Selector survivorSelector = survivorSelectionPolicyHandler.resolve(geneticSystemDescriptor, genotypeSpec,
				selectionPolicyHandlerResolver, genotypeSpec.survivorSelectionPolicy());

		final MutationPolicyHandlerResolver mutationPolicyHandlerResolver = new MutationPolicyHandlerResolver(
				geneticSystemDescriptor);

		// TODO move to a chromosome combinator resolver
		final CombinationPolicy combinationPolicy = genotypeSpec.combinationPolicy();
		final List<ChromosomeCombinator> chromosomeCombinators = genotypeSpec.chromosomeSpecs()
				.stream()
				.map((chromosome) -> {
					return findMatchingChromosomeCombinatorHandler(geneticSystemDescriptor, combinationPolicy, chromosome);
				})
				.collect(Collectors.toList());

		final List<Mutator> mutators = new ArrayList<Mutator>();
		for (int i = 0; i < genotypeSpec.mutationPolicies()
				.size(); i++) {
			final MutationPolicy mutationPolicy = genotypeSpec.mutationPolicies()
					.get(i);

			final MutationPolicyHandler mutationPolicyHandler = mutationPolicyHandlerResolver.resolve(mutationPolicy);
			final Mutator mutator = mutationPolicyHandler.createMutator(geneticSystemDescriptor, genotypeSpec,
					mutationPolicyHandlerResolver, mutationPolicy);

			mutators.add(mutator);

		}

		final long populationSize = geneticSystemDescriptor.populationSize();

		return new GeneticSystem(genotypeSpec, populationSize, chromosomeCombinators, genotypeSpec.offspringRatio(),
				parentSelector, survivorSelector, mutators, geneticSystemDescriptor);
	}
}