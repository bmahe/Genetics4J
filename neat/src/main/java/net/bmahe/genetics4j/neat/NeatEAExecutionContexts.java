package net.bmahe.genetics4j.neat;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder;
import net.bmahe.genetics4j.neat.chromosomes.factory.NeatConnectedChromosomeFactory;
import net.bmahe.genetics4j.neat.combination.NeatCombinationHandler;
import net.bmahe.genetics4j.neat.mutation.AddConnectionPolicyHandler;
import net.bmahe.genetics4j.neat.mutation.AddNodePolicyHandler;
import net.bmahe.genetics4j.neat.mutation.DeleteConnectionPolicyHandler;
import net.bmahe.genetics4j.neat.mutation.DeleteNodePolicyHandler;
import net.bmahe.genetics4j.neat.mutation.NeatConnectionWeightPolicyHandler;
import net.bmahe.genetics4j.neat.mutation.NeatSwitchStatePolicyHandler;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeAddConnection;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeAddNodeMutationHandler;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeConnectionWeightMutationHandler;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeCreepMutationHandler;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeDeleteConnection;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeDeleteNodeMutationHandler;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeRandomMutationHandler;
import net.bmahe.genetics4j.neat.mutation.chromosome.NeatChromosomeSwitchStateHandler;
import net.bmahe.genetics4j.neat.selection.NeatSelectionPolicyHandler;

public class NeatEAExecutionContexts {

	private NeatEAExecutionContexts() {
	}

	public static <T extends Number & Comparable<T>> Builder<T> enrichWithNeat(final Builder<T> builder) {
		Validate.notNull(builder);

		final var innovationManager = new InnovationManager();
		final var speciesIdGenerator = new SpeciesIdGenerator();

		final var cfp = ImmutableChromosomeFactoryProvider.builder()
				.randomGenerator(RandomGenerator.getDefault())
				.addChromosomeFactoriesGenerator(
						cdp -> new NeatConnectedChromosomeFactory(cdp.randomGenerator(), innovationManager))
				.build();

		return enrichWithNeat(builder, innovationManager, speciesIdGenerator, cfp);
	}

	public static <T extends Number & Comparable<T>> Builder<T> enrichWithNeat(final Builder<T> builder,
			final InnovationManager innovationManager, final SpeciesIdGenerator speciesIdGenerator,
			final ChromosomeFactoryProvider chromosomeFactoryProvider) {
		Validate.notNull(builder);
		Validate.notNull(innovationManager);
		Validate.notNull(speciesIdGenerator);
		Validate.notNull(chromosomeFactoryProvider);

		builder.chromosomeFactoryProvider(chromosomeFactoryProvider)
				.addSelectionPolicyHandlerFactories(
						ec -> new NeatSelectionPolicyHandler<>(ec.randomGenerator(), speciesIdGenerator))
				.addMutationPolicyHandlerFactories(ec -> new NeatSwitchStatePolicyHandler<>(ec.randomGenerator()),
						ec -> new AddNodePolicyHandler<>(ec.randomGenerator()),
						ec -> new DeleteNodePolicyHandler<>(ec.randomGenerator()),
						ec -> new AddConnectionPolicyHandler<>(ec.randomGenerator()),
						ec -> new DeleteConnectionPolicyHandler<>(ec.randomGenerator()),
						ec -> new NeatConnectionWeightPolicyHandler<>(ec.randomGenerator()))
				.addChromosomeCombinatorHandlerFactories(ec -> new NeatCombinationHandler<>(ec.randomGenerator()))
				.addChromosomeMutationPolicyHandlerFactories(
						ec -> new NeatChromosomeSwitchStateHandler(ec.randomGenerator()),
						ec -> new NeatChromosomeCreepMutationHandler(ec.randomGenerator()),
						ec -> new NeatChromosomeRandomMutationHandler(ec.randomGenerator()),
						ec -> new NeatChromosomeAddNodeMutationHandler(ec.randomGenerator(), innovationManager),
						ec -> new NeatChromosomeDeleteNodeMutationHandler(ec.randomGenerator()),
						ec -> new NeatChromosomeAddConnection(ec.randomGenerator(), innovationManager),
						ec -> new NeatChromosomeDeleteConnection(ec.randomGenerator()),
						ec -> new NeatChromosomeConnectionWeightMutationHandler(ec.randomGenerator()));

		return builder;
	}

	public static <T extends Number & Comparable<T>> Builder<T> standard() {

		final var scalarEAExecutionContext = EAExecutionContexts.<T>forScalarFitness();
		return enrichWithNeat(scalarEAExecutionContext);
	}
}