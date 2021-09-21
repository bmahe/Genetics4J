package net.bmahe.genetics4j.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;

public class GenotypeTest {

	@Test
	public void nullChromosomeArrayCtor() {
		assertThrows(NullPointerException.class, () -> new Genotype((Chromosome[]) null));
	}

	@Test
	public void nullChromosomeCollectionCtor() {
		assertThrows(NullPointerException.class, () -> new Genotype((Collection<Chromosome>) null));
	}

	@Test
	public void emptyChromosomeCollectionCtor() {
		assertThrows(IllegalArgumentException.class, () -> new Genotype(Collections.emptyList()));
	}

	@Test
	public void simple() {

		final BitChromosome bitChromosome = new BitChromosome(3, BitSet.valueOf(new long[] { 123456, 456 }));
		final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { 0, 1, 2, 3 });

		final Genotype genotypeA = new Genotype(bitChromosome, intChromosome);
		final Genotype genotypeB = new Genotype(bitChromosome, intChromosome);
		final Genotype genotypeC = new Genotype(intChromosome, bitChromosome);
		final Genotype genotypeD = new Genotype(intChromosome);

		assertEquals(2, genotypeA.getSize());
		assertEquals(2, genotypeB.getSize());
		assertEquals(2, genotypeC.getSize());
		assertEquals(1, genotypeD.getSize());

		assertEquals(genotypeA, genotypeB);
		assertNotEquals(genotypeA, genotypeC);
		assertNotEquals(genotypeA, genotypeD);

		assertEquals(bitChromosome, genotypeA.getChromosome(0));
		assertEquals(intChromosome, genotypeA.getChromosome(1));
	}
}