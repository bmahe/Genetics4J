package net.bmahe.genetics4j.gp.spec;

import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder;
import net.bmahe.genetics4j.gp.chromosomes.factory.ProgramTreeChromosomeFactory;
import net.bmahe.genetics4j.gp.combination.ProgramRandomCombineHandler;
import net.bmahe.genetics4j.gp.mutation.ProgramRandomMutatePolicyHandler;
import net.bmahe.genetics4j.gp.mutation.ProgramRandomPrunePolicyHandler;
import net.bmahe.genetics4j.gp.mutation.ProgramRulesApplicatorPolicyHandler;
import net.bmahe.genetics4j.gp.program.ProgramGenerator;
import net.bmahe.genetics4j.gp.program.ProgramHelper;

public class GPGeneticSystemDescriptors {

	private GPGeneticSystemDescriptors() {
	}

	public static <T extends Comparable<T>> Builder<T> forGP(final Random random, final ProgramHelper programHelper,
			final ProgramGenerator programGenerator) {
		Validate.notNull(random);
		Validate.notNull(programHelper);
		Validate.notNull(programGenerator);

		final Builder<T> builder = ImmutableGeneticSystemDescriptor.<T>builder();
		builder.random(random);

		builder.addMutationPolicyHandlerFactories(
				gsd -> new ProgramRandomPrunePolicyHandler(gsd.random(), programHelper),
				gsd -> new ProgramRandomMutatePolicyHandler(gsd.random(), programGenerator),
				gsd -> new ProgramRulesApplicatorPolicyHandler());

		builder.addChromosomeCombinatorHandlerFactories(gsd -> new ProgramRandomCombineHandler(gsd.random()));

		final net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider.Builder chromosomeFactoryProviderBuilder = ImmutableChromosomeFactoryProvider
				.builder();
		chromosomeFactoryProviderBuilder.random(random);
		chromosomeFactoryProviderBuilder.addDefaultChromosomeFactories(new ProgramTreeChromosomeFactory(programGenerator));
		builder.chromosomeFactoryProvider(chromosomeFactoryProviderBuilder.build());

		return builder;
	}

}