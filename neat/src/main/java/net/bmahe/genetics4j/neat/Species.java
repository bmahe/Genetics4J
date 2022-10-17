package net.bmahe.genetics4j.neat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Individual;

public class Species<T extends Comparable<T>> {

	private final int id;
	private final List<Individual<T>> ancestors = new ArrayList<>();
	private final List<Individual<T>> members = new ArrayList<>();

	public Species(final int _id, final List<Individual<T>> _ancestors) {
		Validate.notNull(_ancestors);

		this.id = _id;
		ancestors.addAll(_ancestors);
	}

	public void addAncestor(final Individual<T> individual) {
		Validate.notNull(individual);

		ancestors.add(individual);
	}

	public void addMember(final Individual<T> individual) {
		Validate.notNull(individual);
		members.add(individual);
	}

	public void addAllMembers(final Collection<Individual<T>> individuals) {
		Validate.notNull(individuals);

		members.addAll(individuals);
	}

	public int getNumAncestors() {
		return ancestors.size();
	}

	public int getNumMembers() {
		return members.size();
	}

	public int getId() {
		return id;
	}

	public List<Individual<T>> getAncestors() {
		return ancestors;
	}

	public List<Individual<T>> getMembers() {
		return members;
	}

	@Override
	public int hashCode() {
		return Objects.hash(ancestors, id, members);
	}

	// Should it be id only?
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Species other = (Species) obj;
		return Objects.equals(ancestors, other.ancestors) && id == other.id && Objects.equals(members, other.members);
	}

	@Override
	public String toString() {
		return "Species [id=" + id + ", ancestors=" + ancestors + ", members=" + members + "]";
	}
}