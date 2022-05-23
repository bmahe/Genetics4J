package net.bmahe.genetics4j.gpu.spec.fitness.multistage;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.gpu.spec.fitness.cldata.StaticDataLoader;

@Value.Immutable
public interface MultiStageDescriptor {

	Map<String, StaticDataLoader> staticDataLoaders();

	List<StageDescriptor> stageDescriptors();

	@Value.Check
	default void check() {
		Validate.notNull(stageDescriptors());
		Validate.isTrue(stageDescriptors().size() > 0);
	}

	static ImmutableMultiStageDescriptor.Builder builder() {
		return ImmutableMultiStageDescriptor.builder();
	}
}