package net.bmahe.genetics4j.core.combination;

import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;

public class ChromosomeCombinatorResolver {

	final GeneticSystemDescriptor geneticSystemDescriptor;
	private List<ChromosomeCombinatorHandler> chromosomeCombinatorHandlers;

	public ChromosomeCombinatorResolver(final GeneticSystemDescriptor _geneticSystemDescriptor) {
		Validate.notNull(_geneticSystemDescriptor);

		this.geneticSystemDescriptor = _geneticSystemDescriptor;
		this.chromosomeCombinatorHandlers = geneticSystemDescriptor.chromosomeCombinatorHandlers();
	}

	public boolean canHandle(final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return chromosomeCombinatorHandlers.stream()
				.anyMatch((cch) -> cch.canHandle(this, combinationPolicy, chromosome));

	}

	public ChromosomeCombinator resolve(final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return chromosomeCombinatorHandlers.stream()
				.dropWhile((cch) -> cch.canHandle(this, combinationPolicy, chromosome) == false)
				.findFirst()
				.map((cch) -> cch.resolve(this, combinationPolicy, chromosome))
				.orElseThrow(() -> new IllegalStateException(
						"Could not find suitable chromosome combination policy handler for policy " + combinationPolicy
								+ " and chromosome " + chromosome));
	}
}