package net.bmahe.genetics4j.gp.spec.mutation;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.Program;

@Value.Immutable
public interface Rule {

	@Value.Parameter
	Predicate<TreeNode<Operation<?>>> predicate();

	@Value.Parameter
	BiFunction<Program, TreeNode<Operation<?>>, TreeNode<Operation<?>>> simplify();
}