package net.bmahe.genetics4j.gpu.spec.fitness.cldata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_mem;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;

public class DataLoaders {
	final static public Logger logger = LogManager.getLogger(DataLoaders.class);

	private DataLoaders() {
	}

	public static DataLoader ofFloatSupplier(final DataSupplier<float[]> floatSupplier) {
		return (openCLExecutionContext, generation, genotypes) -> {
			final var clContext = openCLExecutionContext.clContext();

			final float[] data = floatSupplier.get(openCLExecutionContext, generation, genotypes);
			logger.trace("Allocating memory on GPU for float[] of {} elements", data.length);

			final Pointer dataPtr = Pointer.to(data);
			final cl_mem inputMem = CL.clCreateBuffer(clContext,
					CL.CL_MEM_COPY_HOST_PTR | CL.CL_MEM_READ_ONLY,
					Sizeof.cl_float * data.length,
					dataPtr,
					null);

			return CLData.of(inputMem, Sizeof.cl_float, data.length);
		};
	}

	public static DataLoader ofIntSupplier(final DataSupplier<int[]> intSupplier) {
		return (openCLExecutionContext, generation, genotypes) -> {
			final var clContext = openCLExecutionContext.clContext();

			final int[] data = intSupplier.get(openCLExecutionContext, generation, genotypes);
			logger.trace("Allocating memory on GPU for int[] of {} elements", data.length);

			final Pointer dataPtr = Pointer.to(data);
			final cl_mem inputMem = CL.clCreateBuffer(clContext,
					CL.CL_MEM_COPY_HOST_PTR | CL.CL_MEM_READ_ONLY,
					Sizeof.cl_int * data.length,
					dataPtr,
					null);

			return CLData.of(inputMem, Sizeof.cl_int, data.length);
		};
	}

	public static DataLoader ofGenerationAndPopulationSize(final boolean readOnly) {
		long bufferFlags = CL.CL_MEM_COPY_HOST_PTR;

		if (readOnly) {
			bufferFlags |= CL.CL_MEM_READ_ONLY;
		}
		final long fBufferFlags = bufferFlags;

		return (openCLExecutionContext, generation, genotypes) -> {
			final var clContext = openCLExecutionContext.clContext();

			int[] data = new int[2];
			data[0] = (int) generation;
			data[1] = genotypes.size();

			final Pointer dataPtr = Pointer.to(data);
			final cl_mem inputMem = CL.clCreateBuffer(clContext, fBufferFlags, Sizeof.cl_int * data.length, dataPtr, null);

			return CLData.of(inputMem, Sizeof.cl_int, data.length);
		};
	}

	public static DataLoader ofGenerationAndPopulationSize() {
		return ofGenerationAndPopulationSize(true);
	}

	public static DataLoader ofLinearizeFloatChromosome(final int chromosomeIndex, final boolean readOnly) {

		long bufferFlags = CL.CL_MEM_COPY_HOST_PTR;

		if (readOnly) {
			bufferFlags |= CL.CL_MEM_READ_ONLY;
		}
		final long fBufferFlags = bufferFlags;

		return (openCLExecutionContext, generation, genotypes) -> {
			final var clContext = openCLExecutionContext.clContext();

			final var firstchromosome = genotypes.get(0)
					.getChromosome(chromosomeIndex, FloatChromosome.class);
			final int chromosomeSize = firstchromosome.getSize();

			final float[] data = new float[genotypes.size() * chromosomeSize];

			for (int i = 0; i < genotypes.size(); i++) {
				final Genotype genotype = genotypes.get(i);
				final var chromosome = genotype.getChromosome(chromosomeIndex, FloatChromosome.class);

				for (int j = 0; j < chromosome.getNumAlleles(); j++) {
					data[i * chromosomeSize + j] = (float) chromosome.getAllele(j);
				}

			}

			final Pointer inputPtr = Pointer.to(data);
			final cl_mem inputMem = CL
					.clCreateBuffer(clContext, fBufferFlags, Sizeof.cl_float * data.length, inputPtr, null);

			return CLData.of(inputMem, Sizeof.cl_float, data.length);
		};
	}

	public static DataLoader ofLinearizeFloatChromosome(final int chromosomeIndex) {
		return ofLinearizeFloatChromosome(chromosomeIndex, true);
	}

	public static DataLoader ofLinearizeDoubleChromosome(final int chromosomeIndex, final boolean readOnly) {

		long bufferFlags = CL.CL_MEM_COPY_HOST_PTR;

		if (readOnly) {
			bufferFlags |= CL.CL_MEM_READ_ONLY;
		}
		final long fBufferFlags = bufferFlags;

		return (openCLExecutionContext, generation, genotypes) -> {
			final var clContext = openCLExecutionContext.clContext();

			final var firstchromosome = genotypes.get(0)
					.getChromosome(chromosomeIndex, DoubleChromosome.class);
			final int chromosomeSize = firstchromosome.getSize();

			final double[] data = new double[genotypes.size() * chromosomeSize];

			for (int i = 0; i < genotypes.size(); i++) {
				final Genotype genotype = genotypes.get(i);
				final var chromosome = genotype.getChromosome(0, DoubleChromosome.class);

				for (int j = 0; j < chromosome.getNumAlleles(); j++) {
					data[i * chromosomeSize + j] = chromosome.getAllele(j);
				}

			}

			final Pointer dataPtr = Pointer.to(data);
			final cl_mem dataMem = CL
					.clCreateBuffer(clContext, fBufferFlags, Sizeof.cl_double * data.length, dataPtr, null);

			return CLData.of(dataMem, Sizeof.cl_double, data.length);
		};
	}

	public static DataLoader ofLinearizeDoubleChromosome(final int chromosomeIndex) {
		return ofLinearizeDoubleChromosome(chromosomeIndex, true);
	}
}