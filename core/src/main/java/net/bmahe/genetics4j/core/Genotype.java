package net.bmahe.genetics4j.core;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;

public class Genotype {

	private final Chromosome[] chromosomes;

	public Genotype(final Chromosome... _chromosomes) {
		Validate.notNull(_chromosomes);

		this.chromosomes = _chromosomes;
	}

	public Genotype(final Collection<Chromosome> _chromosomes) {
		Validate.notNull(_chromosomes);
		Validate.isTrue(_chromosomes.size() > 0);

		final Chromosome[] chromosomesArray = _chromosomes.toArray(new Chromosome[_chromosomes.size()]);

		this.chromosomes = chromosomesArray;
	}

	public int getSize() {
		return chromosomes.length;
	}

	public Chromosome[] getChromosomes() {
		return chromosomes;
	}

	public Chromosome getChromosome(final int index) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < chromosomes.length);

		return chromosomes[index];
	}

	public <T extends Chromosome> T getChromosome(final int index, Class<T> clazz) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < chromosomes.length);
		Validate.notNull(clazz);

		return clazz.cast(chromosomes[index]);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(chromosomes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Genotype other = (Genotype) obj;
		if (!Arrays.equals(chromosomes, other.chromosomes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Genotype [chromosomes=" + Arrays.toString(chromosomes) + "]";
	}
}