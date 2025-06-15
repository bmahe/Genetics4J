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

/**
 * Utility class for generating initial populations of genotypes in evolutionary algorithms.
 * 
 * <p>GenotypeGenerator provides the infrastructure for creating random initial populations
 * based on chromosome specifications defined in the EA configuration. It supports both
 * automatic generation using chromosome factories and custom generation through user-provided
 * suppliers, enabling flexible population initialization strategies.
 * 
 * <p>The generator operates in two modes:
 * <ul>
 * <li><strong>Automatic generation</strong>: Uses registered chromosome factories to create random individuals</li>
 * <li><strong>Custom generation</strong>: Uses a user-provided supplier for specialized initialization</li>
 * </ul>
 * 
 * <p>Key responsibilities include:
 * <ul>
 * <li><strong>Population initialization</strong>: Creating diverse initial populations for algorithm startup</li>
 * <li><strong>Factory coordination</strong>: Managing chromosome factories for different chromosome types</li>
 * <li><strong>Configuration compliance</strong>: Ensuring generated genotypes match EA configuration specifications</li>
 * <li><strong>Logging and monitoring</strong>: Providing visibility into population generation process</li>
 * </ul>
 * 
 * <p>Automatic generation workflow:
 * <ol>
 * <li>Resolve appropriate chromosome factories for each chromosome specification</li>
 * <li>Generate random chromosomes using the resolved factories</li>
 * <li>Assemble chromosomes into complete genotypes</li>
 * <li>Repeat until the required population size is reached</li>
 * </ol>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Standard automatic generation
 * ChromosomeFactoryProvider factoryProvider = new ChromosomeFactoryProvider();
 * EAConfiguration<Double> config = createConfiguration();
 * GenotypeGenerator<Double> generator = new GenotypeGenerator<>(factoryProvider, config);
 * 
 * List<Genotype> initialPopulation = generator.generateGenotypes(100);
 * 
 * // With custom generation logic
 * EAConfiguration<Double> configWithCustomGen = EAConfigurationBuilder.<Double>builder()
 *     .chromosomeSpecs(chromosomeSpec)
 *     .genotypeGenerator(() -> createSpecializedGenotype())
 *     .build();
 * 
 * GenotypeGenerator<Double> customGenerator = new GenotypeGenerator<>(factoryProvider, configWithCustomGen);
 * List<Genotype> customPopulation = customGenerator.generateGenotypes(50);
 * }</pre>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Factory reuse</strong>: Resolves chromosome factories once per generation session</li>
 * <li><strong>Memory efficiency</strong>: Generates individuals on-demand without intermediate storage</li>
 * <li><strong>Logging overhead</strong>: Uses efficient logging for population generation tracking</li>
 * <li><strong>Validation</strong>: Performs minimal validation to maintain generation performance</li>
 * </ul>
 * 
 * @param <T> the type of fitness values in the evolutionary algorithm
 * @see Genotype
 * @see ChromosomeFactory
 * @see ChromosomeFactoryProvider
 * @see net.bmahe.genetics4j.core.spec.AbstractEAConfiguration
 */
public class GenotypeGenerator<T extends Comparable<T>> {
	final static public Logger logger = LogManager.getLogger(GenotypeGenerator.class);

	private final ChromosomeFactoryProvider chromosomeFactoryProvider;
	private final AbstractEAConfiguration<T> eaConfiguration;

	private final Optional<Supplier<Genotype>> populationGenerator;

	/**
	 * Constructs a new genotype generator with the specified factory provider and EA configuration.
	 * 
	 * <p>The generator will use the provided chromosome factory provider to resolve appropriate
	 * factories for each chromosome type specified in the configuration. If the configuration
	 * includes a custom genotype generator, it will be used instead of automatic generation.
	 * 
	 * @param _chromosomeFactoryProvider the provider for chromosome factories
	 * @param _eaConfiguration the EA configuration containing chromosome specifications
	 * @throws NullPointerException if any parameter is null
	 */
	public GenotypeGenerator(final ChromosomeFactoryProvider _chromosomeFactoryProvider,
			final AbstractEAConfiguration<T> _eaConfiguration) {
		Objects.requireNonNull(_chromosomeFactoryProvider);
		Objects.requireNonNull(_eaConfiguration);

		this.chromosomeFactoryProvider = _chromosomeFactoryProvider;
		this.eaConfiguration = _eaConfiguration;

		this.populationGenerator = eaConfiguration.genotypeGenerator();
	}

	/**
	 * Generates a specified number of random genotypes for initial population creation.
	 * 
	 * <p>This method creates a diverse initial population by either using a custom genotype
	 * generator (if specified in the configuration) or by automatically generating individuals
	 * using chromosome factories. Each generated genotype conforms to the chromosome
	 * specifications defined in the EA configuration.
	 * 
	 * <p>Generation process:
	 * <ol>
	 * <li>Check if custom genotype generator is available</li>
	 * <li>If custom generator exists, use it to create all individuals</li>
	 * <li>Otherwise, resolve chromosome factories and generate chromosomes automatically</li>
	 * <li>Assemble chromosomes into complete genotypes</li>
	 * </ol>
	 * 
	 * @param numPopulation the number of genotypes to generate (must be positive)
	 * @return a list of randomly generated genotypes ready for evolution
	 * @throws IllegalArgumentException if numPopulation is not positive
	 * @throws RuntimeException if chromosome factory resolution or generation fails
	 */
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