package net.bmahe.genetics4j.core.replacement;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.replacement.DeleteNLast;

public class DeleteNLastImpl<T extends Comparable<T>> implements ReplacementStrategyImplementor<T> {

	private final DeleteNLast deleteNLastSpec;
	private final Selector<T> offspringSelector;

	public DeleteNLastImpl(final DeleteNLast _deleteNLastSpec, final Selector<T> _offspringSelector) {
		Validate.notNull(_deleteNLastSpec);
		Validate.notNull(_offspringSelector);

		this.deleteNLastSpec = _deleteNLastSpec;
		this.offspringSelector = _offspringSelector;
	}

	@Override
	public Population<T> select(final EAConfiguration<T> eaConfiguration, final int numIndividuals,
			final List<Genotype> population, final List<T> populationScores, final List<Genotype> offsprings,
			final List<T> offspringScores) {
		Validate.notNull(eaConfiguration);
		Validate.isTrue(numIndividuals > 0);
		Validate.notNull(population);
		Validate.notNull(populationScores);
		Validate.isTrue(population.size() == populationScores.size());
		Validate.notNull(offsprings);
		Validate.notNull(offspringScores);
		Validate.isTrue(offsprings.size() == offspringScores.size());

		switch (eaConfiguration.optimization()) {
			case MAXIMZE:
			case MINIMIZE:
				break;
			default:
				throw new IllegalArgumentException("Unsupported optimization " + eaConfiguration.optimization());
		}

		final Comparator<T> populationComparator = Optimization.MAXIMZE.equals(eaConfiguration.optimization())
				? Comparator.naturalOrder()
				: Comparator.reverseOrder();

		final Population<T> selected = new Population<>();

		final int weakestN = (int) (numIndividuals * deleteNLastSpec.weakRatio());

		IntStream.range(0, populationScores.size())
				.boxed()
				.sorted((a, b) -> populationComparator.compare(populationScores.get(a), populationScores.get(b)))
				.skip(weakestN)
				.forEach(index -> {
					selected.add(population.get(index), populationScores.get(index));
				});

		final Population<T> selectedOffspring = offspringSelector
				.select(eaConfiguration, weakestN, offsprings, offspringScores);
		selected.addAll(selectedOffspring);

		return selected;
	}
}