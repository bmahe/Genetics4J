package net.bmahe.genetics4j.neat.combination.parentcompare;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.neat.spec.combination.parentcompare.ParentComparisonPolicy;

public class ParentComparisonHandlerLocator {

	private final ServiceLoader<ParentComparisonHandler> parentComparisonHandlerServices;

	public ParentComparisonHandlerLocator() {

		parentComparisonHandlerServices = ServiceLoader.load(ParentComparisonHandler.class);
	}

	public Optional<ParentComparisonHandler> find(final ParentComparisonPolicy parentComparisonPolicy) {
		Validate.notNull(parentComparisonPolicy);

		final Optional<ParentComparisonHandler> matchedParentComparisonHandler = parentComparisonHandlerServices.stream()
				.map(Provider::get)
				.filter(parentComparisonHandler -> parentComparisonHandler.canHandle(parentComparisonPolicy))
				.findFirst();

		return matchedParentComparisonHandler;
	}
}