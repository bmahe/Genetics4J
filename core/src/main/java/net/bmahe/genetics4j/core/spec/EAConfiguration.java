package net.bmahe.genetics4j.core.spec;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Fitness;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.combination.AllCasesGenotypeCombinator;
import net.bmahe.genetics4j.core.combination.GenotypeCombinator;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.replacement.Elitism;
import net.bmahe.genetics4j.core.spec.replacement.ReplacementStrategy;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;
import net.bmahe.genetics4j.core.termination.Termination;

/**
 * Evolutionary Algorithm Configuration.
 * <p>
 * This describe the set of strategies to use. They describe the genotype, the
 * different policies for selection, combination as well as mutation, and other
 * relevant parameters such as how to determine the fitness of a given
 * individual.
 * 
 * @param <T> Type of the fitness measurement
 */
@Value.Immutable
public abstract class EAConfiguration<T extends Comparable<T>> {
	/**
	 * Default offspring ratio
	 */
	public static final double DEFAULT_OFFSPRING_RATIO = 1.0;

	/**
	 * Default optimization strategy
	 */
	public static final Optimization DEFAULT_OPTIMIZATION = Optimization.MAXIMZE;

	/**
	 * Genotype of the population
	 * 
	 * @return
	 */
	public abstract List<ChromosomeSpec> chromosomeSpecs();

	/**
	 * Defines the policy to select the parents. The selected parents will be used
	 * for generating the new offsprings
	 * 
	 * @return
	 */
	public abstract SelectionPolicy parentSelectionPolicy();

	/**
	 * Defines the policy to generate new offsprings from two parents
	 * 
	 * @return
	 */
	public abstract CombinationPolicy combinationPolicy();

	/**
	 * Defines what mutations to be performed on the offsprings
	 * 
	 * @return
	 */
	public abstract List<MutationPolicy> mutationPolicies();

	/**
	 * Defines the replacement strategy
	 * <p>
	 * The replacement strategy is what will determine the next population based on
	 * the generated and mutated offsprings along with the current population
	 * <p>
	 * If not specified, the default replacement strategy will be to use Elitism
	 * with tournament selection of 3 individuals for both offsprings and survivors.
	 * The default offspring ratio is {@link Elitism#DEFAULT_OFFSPRING_RATIO}
	 * 
	 * @return
	 */
	@Value.Default
	public ReplacementStrategy replacementStrategy() {
		final var replacementStrategyBuilder = Elitism.builder();

		replacementStrategyBuilder.offspringRatio(Elitism.DEFAULT_OFFSPRING_RATIO)
				.offspringSelectionPolicy(TournamentSelection.of(3))
				.survivorSelectionPolicy(TournamentSelection.of(3));

		return replacementStrategyBuilder.build();
	}

	/**
	 * Defines how should individuals' fitness be assessed
	 * 
	 * @return
	 */
	public abstract Fitness<T> fitness();

	/**
	 * Post-processing of a population after it got evaluated
	 * <p>
	 * This gives the opportunity to filter out, repair or rescore individuals
	 * 
	 * @return Population to be used by the remaining evolution process
	 */
	public abstract Optional<Function<Population<T>, Population<T>>> postEvaluationProcessor();

	/**
	 * Defines termination condition
	 * 
	 * @return
	 */
	public abstract Termination<T> termination();

	/**
	 * Defines how to generate individuals
	 * <p>
	 * If not specified, the system will rely on the chromosome factories
	 * 
	 * @return
	 */
	public abstract Optional<Supplier<Genotype>> genotypeGenerator();

	/**
	 * Defines how to combine the offspring chromosomes generated
	 * <p>
	 * Combination of individuals is done on a per chromosome basis. This means some
	 * parents may generate a different number of children for each chromosome. This
	 * method will therefore define how to take all these generated chromosomes and
	 * combine them into offspring individuals
	 * <p>
	 * The current default implementation is to generate as many individual as there
	 * are combinations of generated chromosomes
	 * 
	 * @return
	 */
	@Value.Default
	public GenotypeCombinator genotypeCombinator() {
		return new AllCasesGenotypeCombinator();
	}

	/**
	 * Defines how many children will be generated at each iteration. Value must be
	 * between 0 and 1 (inclusive)
	 * 
	 * @return
	 */
	@Value.Default
	public double offspringGeneratedRatio() {
		return DEFAULT_OFFSPRING_RATIO;
	}

	/**
	 * Defines the optimization goal, whether we want to maximize the fitness or
	 * minimize it
	 * 
	 * @return
	 */
	@Value.Default
	public Optimization optimization() {
		return DEFAULT_OPTIMIZATION;
	}

	/**
	 * Validates the configuration
	 */
	@Value.Check
	protected void check() {
		Validate.isTrue(chromosomeSpecs().size() > 0, "No chromosomes were specified");
		Validate.inclusiveBetween(0.0d, 1.0d, offspringGeneratedRatio());
	}

	/**
	 * Returns a specific chromosome spec from the genotype definition
	 * 
	 * @param index
	 * @return
	 */
	public ChromosomeSpec getChromosomeSpec(final int index) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < chromosomeSpecs().size());

		return chromosomeSpecs().get(index);
	}

	/**
	 * Returns the currently number of chromosomes defined in the genotype
	 * 
	 * @return
	 */
	public int numChromosomes() {
		return chromosomeSpecs().size();
	}

	public static class Builder<T extends Comparable<T>> extends ImmutableEAConfiguration.Builder<T> {

		public final EAConfiguration.Builder<T> chromosomeSpecs(final ChromosomeSpec... elements) {
			return this.chromosomeSpecs(Arrays.asList(elements));
		}

		public final EAConfiguration.Builder<T> mutationPolicies(final MutationPolicy... elements) {
			return this.mutationPolicies(Arrays.asList(elements));
		}
	}
}