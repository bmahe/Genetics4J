package net.bmahe.genetics4j.gp.spec.mutation;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.Program;

@Value.Immutable
public interface Rule {

	@Value.Parameter
	Predicate<TreeNode<Operation<?>>> predicate();

	@Value.Parameter
	BiFunction<Program, TreeNode<Operation<?>>, TreeNode<Operation<?>>> applicator();

	default boolean test(final TreeNode<Operation<?>> root) {
		Validate.notNull(root);

		return predicate().test(root);
	}

	default TreeNode<Operation<?>> apply(final Program program, final TreeNode<Operation<?>> root) {
		Validate.notNull(program);
		Validate.notNull(root);

		return applicator().apply(program, root);
	}
}