package net.bmahe.genetics4j.core.replacement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.selection.SelectAllPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.replacement.Elitism;
import net.bmahe.genetics4j.core.spec.selection.RandomSelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.SelectAll;
import net.bmahe.genetics4j.core.termination.Terminations;

public class ElitismImplTest {
	final static public Logger logger = LogManager.getLogger(ElitismImplTest.class);

	private final EAConfiguration<Double> SIMPLE_MAXIMIZING_EA_CONFIGURATION = new EAConfiguration.Builder<Double>()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(RandomSelectionPolicy.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Double.MAX_VALUE * 10.0)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test(expected = NullPointerException.class)
	public void ctorNoElitismSpec() {
		final EAExecutionContext<Double> eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(100)
				.build();

		final SelectionPolicyHandlerResolver<Double> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final var selectAllPolicyHandler = new SelectAllPolicyHandler<Double>();
		final var allSelector = selectAllPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				SelectAll.build());

		final ElitismImpl<Double> elitismImpl = new ElitismImpl<>(null, allSelector, allSelector);
	}

	@Test(expected = NullPointerException.class)
	public void ctorNoOffspringSelector() {
		final EAExecutionContext<Double> eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(100)
				.build();

		final SelectionPolicyHandlerResolver<Double> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final var selectAllPolicyHandler = new SelectAllPolicyHandler<Double>();
		final var allSelector = selectAllPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				SelectAll.build());

		final var elitismSpec = Elitism.builder()
				.offspringSelectionPolicy(SelectAll.build())
				.survivorSelectionPolicy(SelectAll.build())
				.offspringRatio(0.5)
				.build();

		final ElitismImpl<Double> elitismImpl = new ElitismImpl<>(elitismSpec, null, allSelector);
	}

	@Test(expected = NullPointerException.class)
	public void ctorNoSurvivorSelector() {
		final EAExecutionContext<Double> eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(100)
				.build();

		final SelectionPolicyHandlerResolver<Double> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final var selectAllPolicyHandler = new SelectAllPolicyHandler<Double>();
		final var allSelector = selectAllPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				SelectAll.build());

		final var elitismSpec = Elitism.builder()
				.offspringSelectionPolicy(SelectAll.build())
				.survivorSelectionPolicy(SelectAll.build())
				.offspringRatio(0.5)
				.build();

		final ElitismImpl<Double> elitismImpl = new ElitismImpl<>(elitismSpec, allSelector, null);
	}

	@Test
	public void simple() {

		final var elitismSpec = Elitism.builder()
				.offspringSelectionPolicy(SelectAll.build())
				.survivorSelectionPolicy(SelectAll.build())
				.offspringRatio(0.6)
				.build();

		final EAExecutionContext<Double> eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(100)
				.build();
		final SelectionPolicyHandlerResolver<Double> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final var selectAllPolicyHandler = new SelectAllPolicyHandler<Double>();
		final var allSelector = selectAllPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				SelectAll.build());

		final int populationSize = 20;
		final List<Genotype> population = new ArrayList<Genotype>(populationSize);
		final List<Double> fitnessScore = new ArrayList<>(populationSize);

		final List<Genotype> offsprings = new ArrayList<Genotype>(populationSize);
		final List<Double> offspringsFitnessScore = new ArrayList<>(populationSize);

		for (int i = 0; i < populationSize; i++) {
			final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { i, i + 1, i + 2, i + 3 });
			final Genotype genotype = new Genotype(new Chromosome[] { intChromosome });

			if (i < populationSize / 2) {
				population.add(genotype);
				fitnessScore.add((double) i);
			} else {
				offsprings.add(genotype);
				offspringsFitnessScore.add((double) i);
			}
		}

		final ElitismImpl<Double> elitismImpl = new ElitismImpl<>(elitismSpec, allSelector, allSelector);

		final int selectionSize = 10;

		final Population<Double> selected = elitismImpl.select(SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionSize,
				population,
				fitnessScore,
				offsprings,
				offspringsFitnessScore);

		assertNotNull(selected);
		assertEquals(selectionSize, selected.size());

		logger.info("Selected: {}", selected);

		for (int i = 0; i < selectionSize * elitismSpec.offspringRatio(); i++) {
			assertEquals(offsprings.get(i), selected.getGenotype(i));
			assertEquals(offspringsFitnessScore.get(i), selected.getFitness(i));
		}

		final int offset = (int) (selectionSize * elitismSpec.offspringRatio());
		for (int i = 0; i < selectionSize * (1 - elitismSpec.offspringRatio()); i++) {
			assertEquals(population.get(i), selected.getGenotype(i + offset));
			assertEquals(fitnessScore.get(i), selected.getFitness(i + offset));
		}
	}
}