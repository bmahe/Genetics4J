package net.bmahe.genetics4j.core.combination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.util.MultiIntCounter;

/**
 * TODO TEST THE SHIT OUT OF ME
 * 
 * @author bruno
 *
 */
public class AllCasesGenotypeCombinator implements GenotypeCombinator {

	@Override
	public List<Genotype> combine(final AbstractEAConfiguration eaConfiguration, final List<List<Chromosome>> chromosomes) {
		Validate.notNull(eaConfiguration);
		Validate.notNull(chromosomes);
		Validate.isTrue(eaConfiguration.chromosomeSpecs().size() == chromosomes.size());

		boolean hasChild = true;
		for (int i = 0; i < chromosomes.size() && hasChild; i++) {
			final List<Chromosome> list = chromosomes.get(i);

			if (list.isEmpty()) {
				hasChild = false;
			}
		}
		if (hasChild == false) {
			return Collections.emptyList();
		}

		final int[] maxIndices = chromosomes.stream().mapToInt(List::size).toArray();

		final MultiIntCounter multiIntCounter = new MultiIntCounter(maxIndices);

		boolean done = false;
		final List<Genotype> combined = new ArrayList<>();
		while (done == false) {

			final List<Chromosome> currentGenotype = new ArrayList<>();

			for (int i = 0; i < chromosomes.size(); i++) {
				final int index = multiIntCounter.getIndex(i);
				currentGenotype.add(chromosomes.get(i).get(index));
			}
			combined.add(new Genotype(currentGenotype));

			multiIntCounter.next();
			if (multiIntCounter.hasNext() == false) {
				done = true;
			}
		}

		return combined;
	}
}