package net.bmahe.genetics4j.neat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Individual;

/**
 * Represents a species in the NEAT (NeuroEvolution of Augmenting Topologies) algorithm.
 * 
 * <p>A Species groups together genetically similar individuals in the population, enabling
 * fitness sharing and diversity preservation in NEAT evolution. Species are formed based on
 * genetic compatibility distance, allowing individuals with similar network topologies to
 * compete within their own niche rather than with the entire population.
 * 
 * <p>Key characteristics:
 * <ul>
 * <li><strong>Genetic similarity</strong>: Members share similar network topologies and connection patterns</li>
 * <li><strong>Fitness sharing</strong>: Members compete primarily within their species for reproductive opportunities</li>
 * <li><strong>Diversity preservation</strong>: Protects innovative topologies from being eliminated by established forms</li>
 * <li><strong>Dynamic membership</strong>: Species composition changes as individuals evolve and compatibility shifts</li>
 * </ul>
 * 
 * <p>NEAT speciation process:
 * <ol>
 * <li><strong>Compatibility measurement</strong>: Calculate genetic distance between individuals</li>
 * <li><strong>Species assignment</strong>: Assign individuals to species based on distance thresholds</li>
 * <li><strong>Representative selection</strong>: Choose species representatives for compatibility testing</li>
 * <li><strong>Fitness sharing</strong>: Adjust individual fitness based on species membership size</li>
 * <li><strong>Reproduction allocation</strong>: Allocate offspring based on species average fitness</li>
 * </ol>
 * 
 * <p>Species lifecycle management:
 * <ul>
 * <li><strong>Formation</strong>: New species created when individuals exceed compatibility threshold</li>
 * <li><strong>Growth</strong>: Species gain members as similar individuals are assigned</li>
 * <li><strong>Stagnation</strong>: Species may stagnate if they fail to improve over generations</li>
 * <li><strong>Extinction</strong>: Species die out when they have no members or persistently poor performance</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create new species with founding ancestors
 * List<Individual<Double>> founders = List.of(individual1, individual2);
 * Species<Double> species = new Species<>(42, founders);
 * 
 * // Add members during population assignment
 * species.addMember(similarIndividual1);
 * species.addMember(similarIndividual2);
 * species.addAllMembers(batchOfSimilarIndividuals);
 * 
 * // Access species information
 * int speciesId = species.getId();
 * int memberCount = species.getNumMembers();
 * List<Individual<Double>> allMembers = species.getMembers();
 * 
 * // Species-based fitness sharing
 * for (Individual<Double> member : species.getMembers()) {
 *     double sharedFitness = member.fitness() / species.getNumMembers();
 *     // Use shared fitness for selection
 * }
 * }</pre>
 * 
 * <p>Ancestor tracking:
 * <ul>
 * <li><strong>Species representatives</strong>: Ancestors serve as compatibility test references</li>
 * <li><strong>Historical continuity</strong>: Maintains connection to previous generations</li>
 * <li><strong>Stability</strong>: Prevents species boundaries from shifting too rapidly</li>
 * <li><strong>Representative selection</strong>: Best performers may become ancestors for next generation</li>
 * </ul>
 * 
 * <p>Fitness sharing mechanism:
 * <ul>
 * <li><strong>Within-species competition</strong>: Members primarily compete with each other</li>
 * <li><strong>Diversity protection</strong>: Prevents single topology from dominating population</li>
 * <li><strong>Innovation preservation</strong>: Allows new topologies time to optimize</li>
 * <li><strong>Niche exploitation</strong>: Different species can specialize for different aspects of the problem</li>
 * </ul>
 * 
 * <p>Integration with NEAT selection:
 * <ul>
 * <li><strong>Speciation</strong>: Used by NeatSelectionPolicyHandler for population organization</li>
 * <li><strong>Compatibility testing</strong>: Ancestors used as reference points for species assignment</li>
 * <li><strong>Reproduction allocation</strong>: Species size influences offspring distribution</li>
 * <li><strong>Population dynamics</strong>: Species creation, growth, and extinction drive population diversity</li>
 * </ul>
 * 
 * @param <T> the fitness value type (typically Double)
 * @see NeatSelectionPolicyHandler
 * @see SpeciesIdGenerator
 * @see NeatUtils#computeCompatibilityDistance
 * @see Individual
 */
public class Species<T extends Comparable<T>> {

	private final int id;
	private final List<Individual<T>> ancestors = new ArrayList<>();
	private final List<Individual<T>> members = new ArrayList<>();

	/**
	 * Constructs a new species with the specified ID and founding ancestors.
	 * 
	 * <p>The ancestors serve as reference points for compatibility testing and represent
	 * the genetic heritage of the species. New individuals are tested against these
	 * ancestors to determine species membership.
	 * 
	 * @param _id unique identifier for this species
	 * @param _ancestors founding individuals that define the species genetic signature
	 * @throws IllegalArgumentException if ancestors is null
	 */
	public Species(final int _id, final List<Individual<T>> _ancestors) {
		Validate.notNull(_ancestors);

		this.id = _id;
		ancestors.addAll(_ancestors);
	}

	/**
	 * Adds an individual as an ancestor of this species.
	 * 
	 * <p>Ancestors serve as reference points for compatibility testing in subsequent
	 * generations. Typically, the best performers from a species may be promoted
	 * to ancestors to maintain species continuity.
	 * 
	 * @param individual the individual to add as an ancestor
	 * @throws IllegalArgumentException if individual is null
	 */
	public void addAncestor(final Individual<T> individual) {
		Validate.notNull(individual);

		ancestors.add(individual);
	}

	/**
	 * Adds an individual as a member of this species.
	 * 
	 * <p>Members are the current generation individuals that have been assigned to
	 * this species based on genetic compatibility. They participate in fitness
	 * sharing and species-based selection.
	 * 
	 * @param individual the individual to add as a member
	 * @throws IllegalArgumentException if individual is null
	 */
	public void addMember(final Individual<T> individual) {
		Validate.notNull(individual);
		members.add(individual);
	}

	/**
	 * Adds multiple individuals as members of this species.
	 * 
	 * <p>This is a convenience method for bulk assignment of compatible individuals
	 * to the species. All individuals in the collection will participate in
	 * fitness sharing within this species.
	 * 
	 * @param individuals collection of individuals to add as members
	 * @throws IllegalArgumentException if individuals is null
	 */
	public void addAllMembers(final Collection<Individual<T>> individuals) {
		Validate.notNull(individuals);

		members.addAll(individuals);
	}

	/**
	 * Returns the number of ancestors in this species.
	 * 
	 * <p>Ancestors serve as reference points for compatibility testing and represent
	 * the species genetic heritage from previous generations.
	 * 
	 * @return the number of ancestors
	 */
	public int getNumAncestors() {
		return ancestors.size();
	}

	/**
	 * Returns the number of current members in this species.
	 * 
	 * <p>The member count is used for fitness sharing calculations and reproduction
	 * allocation. Larger species will have their members' fitness values adjusted
	 * downward to prevent single species from dominating the population.
	 * 
	 * @return the number of current members
	 */
	public int getNumMembers() {
		return members.size();
	}

	/**
	 * Returns the unique identifier for this species.
	 * 
	 * <p>Species IDs are typically assigned by a SpeciesIdGenerator and remain
	 * constant throughout the species lifecycle.
	 * 
	 * @return the species unique identifier
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the list of ancestors for this species.
	 * 
	 * <p>Ancestors are reference individuals used for compatibility testing
	 * when assigning new individuals to species. The returned list is mutable
	 * and modifications will affect the species behavior.
	 * 
	 * @return mutable list of ancestor individuals
	 */
	public List<Individual<T>> getAncestors() {
		return ancestors;
	}

	/**
	 * Returns the list of current members in this species.
	 * 
	 * <p>Members are the current generation individuals that participate in
	 * fitness sharing and species-based selection. The returned list is mutable
	 * and modifications will affect species membership.
	 * 
	 * @return mutable list of member individuals
	 */
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