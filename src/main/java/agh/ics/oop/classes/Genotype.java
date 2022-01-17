package agh.ics.oop.classes;

import java.util.Arrays;
import java.util.Random;

public class Genotype {

    private static final int genotypeSize = 32;
    private static final int genotypeMinValue = 0;
    private static final int genotypeMaxValue = 7;
    private static final Random randomObject = new Random();    // randomObject = losowy obiekt

    private final int[] genes;

    public Genotype() {
        this.genes = randomObject.ints(genotypeSize, genotypeMinValue, genotypeMaxValue + 1).toArray();
        Arrays.sort(this.genes);
    }

    public Genotype(int[] genes) {
        this.genes = genes; // brak kontroli poprawności
    }

    public int[] getGenes() {
        return this.genes;  // zwraca Pan na zewnątrz obiekt modyfikowalny
    }

    public int getRandomGene() {
        int randomIndex = randomObject.nextInt(genotypeSize);
        return this.genes[randomIndex];
    }

    public String toString() {
        return Arrays.toString(this.genes);
    }

}
