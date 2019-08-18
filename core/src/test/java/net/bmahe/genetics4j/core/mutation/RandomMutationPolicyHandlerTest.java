package net.bmahe.genetics4j.core.mutation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.randommutation.IntChromosomeRandomMutationHandler;
import net.bmahe.genetics4j.core.spec.mutation.ImmutableRandomMutation;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public class RandomMutationPolicyHandlerTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new RandomMutationPolicyHandler(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleRequireMutation() {
		final RandomMutationPolicyHandler randomMutationPolicyHandler = new RandomMutationPolicyHandler(new Random());

		randomMutationPolicyHandler.canHandle(null);
	}

	@Test
	public void canHandle() {
		final RandomMutationPolicyHandler randomMutationPolicyHandler = new RandomMutationPolicyHandler(new Random());

		assertTrue(randomMutationPolicyHandler.canHandle(ImmutableRandomMutation.of(0.1)));
	}

	@Test
	public void mutate() {

		final RandomMutationPolicyHandler randomMutationPolicyHandler = new RandomMutationPolicyHandler(new Random());

		final MutationPolicy mutationPolicyNoMutation = ImmutableRandomMutation.of(0.0);
		final MutationPolicy mutationPolicyAlwaysMutation = ImmutableRandomMutation.of(1.0);

		final Genotype original = new Genotype(
				new Chromosome[] { new IntChromosome(5, 0, 10, new int[] { 1, 2, 3, 4, 5 }) });
		final List<ChromosomeMutationHandler<? extends Chromosome>> chromosomeMutationHandlers = Arrays
				.asList(new IntChromosomeRandomMutationHandler(new Random()));

		for (int i = 0; i < 20; i++) {
			final Genotype notMutated = randomMutationPolicyHandler.mutate(mutationPolicyNoMutation, original,
					chromosomeMutationHandlers);

			assertNotNull(notMutated);
			assertEquals(original, notMutated);

			final Genotype mutated = randomMutationPolicyHandler.mutate(mutationPolicyAlwaysMutation, original,
					chromosomeMutationHandlers);

			assertNotNull(mutated);
			assertNotEquals(original, mutated);
		}
	}
}