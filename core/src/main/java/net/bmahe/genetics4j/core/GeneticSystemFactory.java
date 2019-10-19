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
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class GeneticSystemFactory {

	private SelectionPolicyHandler findMatchingSelectionPolicyHandler(
			final GeneticSystemDescriptor geneticSystemDescriptor, final SelectionPolicy selectionPolicy) {
		Validate.notNull(geneticSystemDescriptor);
		Validate.notNull(selectionPolicy);

		final List<SelectionPolicyHandler> selectionPolicyHandlers = geneticSystemDescriptor.selectionPolicyHandlers();

		return selectionPolicyHandlers.stream()
				.dropWhile((sph) -> sph.canHandle(selectionPolicy) == false)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Could not find suitable selection policy handler for policy: " + selectionPolicy));
	}

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

		// TODO move to a SelectionPolicyHandlerResolver
		final SelectionPolicyHandler parentSelectionPolicyHandler = findMatchingSelectionPolicyHandler(
				geneticSystemDescriptor, genotypeSpec.parentSelectionPolicy());

		final SelectionPolicyHandler survivorSelectionPolicyHandler = findMatchingSelectionPolicyHandler(
				geneticSystemDescriptor, genotypeSpec.survivorSelectionPolicy());

		// TODO move to a MutationPolicyHandlerResolver
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
				parentSelectionPolicyHandler, survivorSelectionPolicyHandler, mutators, geneticSystemDescriptor);
	}
}