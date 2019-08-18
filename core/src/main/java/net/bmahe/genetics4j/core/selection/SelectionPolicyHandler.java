package net.bmahe.genetics4j.core.selection;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public interface SelectionPolicyHandler {
	boolean canHandle(SelectionPolicy selectionPolicy);

	List<Genotype> select(SelectionPolicy selectionPolicy, int numParent, Genotype[] population, double[] fitnessScore);
}