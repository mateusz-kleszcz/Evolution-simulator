package agh.ics.oop.simulation;

import agh.ics.oop.classes.Animal;
import agh.ics.oop.classes.Genotype;
import agh.ics.oop.classes.Grass;
import agh.ics.oop.gui.StatisticsVisualization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;

public class Statistics {

    private int animalsNumber = 0;
    private int grassNumber = 0;
    private Genotype dominantGenotype;
    private double averageEnergy = 0.0;
    private double averageChildrenNumber = 0.0;
    private double averageLengthOfLife = 0.0;
    private int deadAnimals;
    private int sumOfDeadAnimalsLifeLengths;

    private final StatisticsVisualization statisticsVisualization;

    private File csvFile;
    private PrintWriter csvPrintWriterOut;

    public Statistics(StatisticsVisualization statisticsVisualization, String csvPathName) {
        this.statisticsVisualization = statisticsVisualization;
        this.csvFile = new File(csvPathName);
        try {
            this.csvPrintWriterOut = new PrintWriter(this.csvFile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void calculateAnimals(LinkedList<Animal> animals) {
        this.animalsNumber = animals.size();
    }

    private void calculateGrasses(int grassNumber) {
        this.grassNumber = grassNumber;
    }

    private void calculateMostFrequentGenotype(HashMap<Genotype, Integer> genotypes) {
        Genotype mostFrequent = null;
        if (genotypes.size() != 0) {
            int maxNumber = 0;
            for (Genotype genotypeKey : genotypes.keySet()) {
                int numberOfGenotypes = genotypes.get(genotypeKey);
                if (numberOfGenotypes > maxNumber) {
                    maxNumber = numberOfGenotypes;
                    mostFrequent = genotypeKey;
                }
            }
        }
        if (mostFrequent != null) {
            this.dominantGenotype = mostFrequent;
        }
    }

    private void calculateAverageEnergy(LinkedList<Animal> animals){
        double sum = 0;
        for (Animal animal : animals) {
            sum += animal.getEnergy();
        }
        double average = sum / animals.size();
        if (Double.isNaN(average)) average = 0.0;
        this.averageEnergy = average;
    }

    private void calculateAverageChildrenNumber(LinkedList<Animal> animals){
        double sum = 0;
        for (Animal animal : animals) {
            sum += animal.getChildrenNumber();
        }
        double average = sum / animals.size();
        if (Double.isNaN(average)) average = 0.0;
        this.averageChildrenNumber = average;
    }

    private void calculateAverageLengthOfLife(int deadAnimalsInCurrentDay, int sumOfDeadAnimalsLifeLengthsInCurrentDay){
        if (deadAnimalsInCurrentDay != 0) {
            this.deadAnimals += deadAnimalsInCurrentDay;
            this.sumOfDeadAnimalsLifeLengths += sumOfDeadAnimalsLifeLengthsInCurrentDay;
            double average = (double)this.sumOfDeadAnimalsLifeLengths / (double)this.deadAnimals;
            if (Double.isNaN(average)) average = 0.0;
            this.averageLengthOfLife = average;
        }
    }

    private void generateStatistics() {
        this.csvPrintWriterOut.println(this.animalsNumber + ";" + this.grassNumber + ";" + averageEnergy + ";" + averageChildrenNumber + ";" + averageLengthOfLife);
    }

    public void saveCsvFile() {
        csvPrintWriterOut.close();
    }

    private void updateVisualization(int day, Animal selectedAnimal) {
        this.statisticsVisualization.update(day, animalsNumber, grassNumber, dominantGenotype, averageEnergy, averageChildrenNumber, averageLengthOfLife, selectedAnimal);
    }

    public void generateOneDayStatistics(int day, LinkedList<Animal> animals, int grassNumber, HashMap<Genotype, Integer> genotypes, int deadAnimalsInCurrentDay, int sumOfDeadAnimalsLifeLengthsInCurrentDay, Animal selectedAnimal) {
        this.calculateAnimals(animals);
        this.calculateGrasses(grassNumber);
        this.calculateMostFrequentGenotype(genotypes);
        this.calculateAverageEnergy(animals);
        this.calculateAverageChildrenNumber(animals);
        this.calculateAverageLengthOfLife(deadAnimalsInCurrentDay, sumOfDeadAnimalsLifeLengthsInCurrentDay);
        this.generateStatistics();
        this.updateVisualization(day, selectedAnimal);
    }

}
