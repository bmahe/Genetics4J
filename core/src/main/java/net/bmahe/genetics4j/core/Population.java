package net.bmahe.genetics4j.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;

public class Population<T extends Comparable<T>> implements Iterable<Individual<T>> {

	private List<Genotype> genotypes;
	private List<T> fitnesses;

	public Population() {
		this.genotypes = new ArrayList<>();
		this.fitnesses = new ArrayList<>();
	}

	public Population(final List<Genotype> _genotype, final List<T> _fitnesses) {
		Validate.notNull(_genotype);
		Validate.notNull(_fitnesses);
		Validate.isTrue(_genotype.size() == _fitnesses.size(),
				"Size of genotype (%d) does not match size of fitnesses (%d)",
				_genotype.size(),
				_fitnesses.size());

		this.genotypes = new ArrayList<Genotype>(_genotype);
		this.fitnesses = new ArrayList<>(_fitnesses);
	}

	public void add(final Genotype genotype, final T fitness) {
		Validate.notNull(genotype);
		Validate.notNull(fitness);

		genotypes.add(genotype);
		fitnesses.add(fitness);
	}

	public void add(final Individual<T> individual) {
		Validate.notNull(individual);

		genotypes.add(individual.genotype());
		fitnesses.add(individual.fitness());
	}

	public void addAll(final Population<T> population) {
		Validate.notNull(population);

		this.genotypes.addAll(population.getAllGenotypes());
		this.fitnesses.addAll(population.getAllFitnesses());
	}

	@Override
	public Iterator<Individual<T>> iterator() {
		return new PopulationIterator<>(this);
	}

	public Genotype getGenotype(final int index) {
		Validate.inclusiveBetween(0, genotypes.size() - 1, index);

		return genotypes.get(index);
	}

	public T getFitness(final int index) {
		Validate.inclusiveBetween(0, fitnesses.size() - 1, index);

		return fitnesses.get(index);
	}

	public Individual<T> getIndividual(final int index) {
		return Individual.of(getGenotype(index), getFitness(index));
	}

	public List<Genotype> getAllGenotypes() {
		return genotypes;
	}

	public List<T> getAllFitnesses() {
		return fitnesses;
	}

	public int size() {
		return genotypes.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fitnesses == null) ? 0 : fitnesses.hashCode());
		result = prime * result + ((genotypes == null) ? 0 : genotypes.hashCode());
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

		@SuppressWarnings("rawtypes")
		Population other = (Population) obj;
		if (fitnesses == null) {
			if (other.fitnesses != null)
				return false;
		} else if (!fitnesses.equals(other.fitnesses))
			return false;
		if (genotypes == null) {
			if (other.genotypes != null)
				return false;
		} else if (!genotypes.equals(other.genotypes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Population [genotypes=" + genotypes + ", fitnesses=" + fitnesses + "]";
	}

	public static <U extends Comparable<U>> Population<U> of(final List<Genotype> _genotype, final List<U> _fitnesses) {
		return new Population<U>(_genotype, _fitnesses);
	}

	public static <U extends Comparable<U>> Population<U> of(final List<Individual<U>> individuals) {
		Validate.notNull(individuals);

		final List<Genotype> genotypes = individuals.stream()
				.map(Individual::genotype)
				.toList();

		final List<U> fitnesses = individuals.stream()
				.map(Individual::fitness)
				.toList();

		return new Population<U>(genotypes, fitnesses);
	}

	public static <U extends Comparable<U>> Population<U> empty() {
		return new Population<U>(List.of(), List.of());
	}

}