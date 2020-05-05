package net.bmahe.genetics4j.core.mutation;

import java.util.List;

import org.junit.Test;

import net.bmahe.genetics4j.core.EASystem;
import net.bmahe.genetics4j.core.EASystemFactory;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAConfiguration.Builder;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.MultiMutations;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;
import net.bmahe.genetics4j.core.spec.selection.RandomSelectionPolicy;

public class SupersimpleTest {

	@Test
	public void simple() {

		final Builder<Double> eaConfigurationBuilder = new EAConfiguration.Builder<Double>();
		eaConfigurationBuilder.chromosomeSpecs(BitChromosomeSpec.of(5), IntChromosomeSpec.of(6, 10, 100))
				.fitness((genotype) -> 1.0)
				.termination((long generation, List<Genotype> population, List<Double> fitness) -> true)
				.parentSelectionPolicy(RandomSelectionPolicy.build())
				.combinationPolicy(SinglePointCrossover.build())
				.addMutationPolicies(MultiMutations.of(RandomMutation.of(0.15), SwapMutation.of(0.05, 2, true)));

		final EAConfiguration<Double> eaConfiguration = eaConfigurationBuilder.build();

		final EAExecutionContext<Double> eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(100)
				.build();

		final EASystem<Double> EASystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);
	}
}