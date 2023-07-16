package net.bmahe.genetics4j.neat.combination.parentcompare;

import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;

public record ChosenOtherChromosome(NeatChromosome chosen, NeatChromosome other) {
}