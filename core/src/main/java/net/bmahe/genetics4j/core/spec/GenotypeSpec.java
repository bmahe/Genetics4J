package net.bmahe.genetics4j.core.spec;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Fitness;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.combination.AllCasesGenotypeCombinator;
import net.bmahe.genetics4j.core.combination.GenotypeCombinator;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.core.termination.Termination;

@Value.Immutable
public abstract class GenotypeSpec<T extends Comparable<T>> {
	public static final double DEFAULT_OFFSPRING_RATIO = 0.7;
	public static final Optimization DEFAULT_OPTIMIZATION = Optimization.MAXIMZE;

	public abstract List<ChromosomeSpec> chromosomeSpecs();

	public abstract SelectionPolicy parentSelectionPolicy();

	public abstract SelectionPolicy survivorSelectionPolicy();

	public abstract CombinationPolicy combinationPolicy();

	public abstract List<MutationPolicy> mutationPolicies();

	public abstract Fitness<T> fitness();

	public abstract Termination<T> termination();

	public abstract Optional<Supplier<Genotype>> populationGenerator();

	@Value.Default
	public GenotypeCombinator genotypeCombinator() {
		return new AllCasesGenotypeCombinator();
	}

	@Value.Default
	public double offspringRatio() {
		return DEFAULT_OFFSPRING_RATIO;
	}

	@Value.Default
	public Optimization optimization() {
		return DEFAULT_OPTIMIZATION;
	}

	@Value.Check
	protected void check() {
		Validate.isTrue(chromosomeSpecs().size() > 0, "No chromosomes were specified");
		Validate.inclusiveBetween(0.0d, 1.0d, offspringRatio());
	}

	public ChromosomeSpec getChromosomeSpec(final int index) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < chromosomeSpecs().size());

		return chromosomeSpecs().get(index);
	}

	public int numChromosomes() {
		return chromosomeSpecs().size();
	}

	public static class Builder<T extends Comparable<T>> extends ImmutableGenotypeSpec.Builder<T> {

		public final GenotypeSpec.Builder<T> chromosomeSpecs(final ChromosomeSpec... elements) {
			return this.chromosomeSpecs(Arrays.asList(elements));
		}

		public final GenotypeSpec.Builder<T> mutationPolicies(final MutationPolicy... elements) {
			return this.mutationPolicies(Arrays.asList(elements));
		}
	}
}