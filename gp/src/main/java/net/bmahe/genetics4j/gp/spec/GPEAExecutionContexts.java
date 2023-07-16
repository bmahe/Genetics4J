package net.bmahe.genetics4j.gp.spec;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext;
import net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder;
import net.bmahe.genetics4j.gp.chromosomes.factory.ProgramTreeChromosomeFactory;
import net.bmahe.genetics4j.gp.combination.ProgramRandomCombineHandler;
import net.bmahe.genetics4j.gp.mutation.NodeReplacementPolicyHandler;
import net.bmahe.genetics4j.gp.mutation.ProgramRandomMutatePolicyHandler;
import net.bmahe.genetics4j.gp.mutation.ProgramRandomPrunePolicyHandler;
import net.bmahe.genetics4j.gp.mutation.ProgramRulesApplicatorPolicyHandler;
import net.bmahe.genetics4j.gp.mutation.TrimTreePolicyHandler;
import net.bmahe.genetics4j.gp.program.ProgramGenerator;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.program.RampedHalfAndHalfProgramGenerator;

/**
 * Defines multiple factory and helper methods to create and manage
 * EAExecutionContexts appropriate for Genetic Programming
 */
public class GPEAExecutionContexts {

	private GPEAExecutionContexts() {
	}

	/**
	 * Create a new EAExecutionContext pre-configured to support Genetic
	 * Programming.
	 * <p>
	 * It adds support for some operators to select, mutate and combine programs.
	 * 
	 * @param <T>              Type of the fitness measurement
	 * @param randomGenerator  Random Generator
	 * @param programHelper    Instance of ProgramHelper
	 * @param programGenerator Instance of a program generator which will be used to
	 *                         generate individuals
	 * @return A new instance of a EAExecutionContext
	 */
	public static <T extends Comparable<T>> Builder<T> forGP(final RandomGenerator randomGenerator,
			final ProgramHelper programHelper, final ProgramGenerator programGenerator) {
		Validate.notNull(randomGenerator);
		Validate.notNull(programHelper);
		Validate.notNull(programGenerator);

		final var builder = ImmutableEAExecutionContext.<T>builder();
		builder.randomGenerator(randomGenerator);

		builder.addMutationPolicyHandlerFactories(
				gsd -> new ProgramRandomPrunePolicyHandler<>(gsd.randomGenerator(), programHelper),
				gsd -> new ProgramRandomMutatePolicyHandler<>(gsd.randomGenerator(), programGenerator),
				gsd -> new NodeReplacementPolicyHandler<>(randomGenerator, programHelper),
				gsd -> new ProgramRulesApplicatorPolicyHandler<>(),
				gsd -> new TrimTreePolicyHandler<>(randomGenerator, programGenerator));

		builder.addChromosomeCombinatorHandlerFactories(gsd -> new ProgramRandomCombineHandler<T>(gsd.randomGenerator()));

		final var chromosomeFactoryProviderBuilder = ImmutableChromosomeFactoryProvider.builder();
		chromosomeFactoryProviderBuilder.randomGenerator(randomGenerator);
		chromosomeFactoryProviderBuilder
				.addChromosomeFactoriesGenerator(cdp -> new ProgramTreeChromosomeFactory(programGenerator));
		builder.chromosomeFactoryProvider(chromosomeFactoryProviderBuilder.build());

		return builder;
	}

	/**
	 * Create a new EAExecutionContext pre-configured to support Genetic
	 * Programming.
	 * <p>
	 * It adds support for some operators to select, mutate and combine programs. It
	 * also configure a default program generation based on ramped hald and half.
	 * 
	 * @param <T>             Type of the fitness measurement
	 * @param randomGenerator Random Generator
	 * @return A new instance of a EAExecutionContext
	 */
	public static <T extends Comparable<T>> Builder<T> forGP(final RandomGenerator randomGenerator) {
		Validate.notNull(randomGenerator);

		final var programHelper = new ProgramHelper(randomGenerator);
		final var programGenerator = new RampedHalfAndHalfProgramGenerator(randomGenerator, programHelper);

		return forGP(randomGenerator, programHelper, programGenerator);
	}
}