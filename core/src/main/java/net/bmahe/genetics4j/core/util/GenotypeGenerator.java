package net.bmahe.genetics4j.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactory;
import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;

public class GenotypeGenerator<T extends Comparable<T>> {
	final static public Logger logger = LogManager.getLogger(GenotypeGenerator.class);

	private final ChromosomeFactoryProvider chromosomeFactoryProvider;
	private final AbstractEAConfiguration<T> eaConfiguration;

	private final Optional<Supplier<Genotype>> populationGenerator;

	public GenotypeGenerator(final ChromosomeFactoryProvider _chromosomeFactoryProvider,
			final AbstractEAConfiguration<T> _eaConfiguration) {
		Objects.requireNonNull(_chromosomeFactoryProvider);
		Objects.requireNonNull(_eaConfiguration);

		this.chromosomeFactoryProvider = _chromosomeFactoryProvider;
		this.eaConfiguration = _eaConfiguration;

		this.populationGenerator = eaConfiguration.genotypeGenerator();
	}

	public List<Genotype> generateGenotypes(final int numPopulation) {
		Validate.isTrue(numPopulation > 0);
		logger.info("Generating {} individuals", numPopulation);

		final List<Genotype> genotypes = new ArrayList<>();

		// Override
		if (populationGenerator.isPresent()) {
			final Supplier<Genotype> populationSupplier = populationGenerator.get();

			for (int i = 0; i < numPopulation; i++) {
				genotypes.add(populationSupplier.get());
			}

		} else {

			final int numChromosomes = eaConfiguration.numChromosomes();
			final ChromosomeFactory<? extends Chromosome>[] chromosomeFactories = new ChromosomeFactory<?>[numChromosomes];
			for (int i = 0; i < numChromosomes; i++) {
				chromosomeFactories[i] = chromosomeFactoryProvider
						.provideChromosomeFactory(eaConfiguration.getChromosomeSpec(i));
			}

			for (int i = 0; i < numPopulation; i++) {

				final Chromosome[] chromosomes = new Chromosome[numChromosomes];
				for (int j = 0; j < numChromosomes; j++) {
					chromosomes[j] = chromosomeFactories[j].generate(eaConfiguration.getChromosomeSpec(j));
				}

				genotypes.add(new Genotype(chromosomes));
			}
		}
		return genotypes;
	}
}