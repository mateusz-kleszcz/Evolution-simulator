package agh.ics.oop.gui;

import agh.ics.oop.classes.Animal;
import agh.ics.oop.classes.Genotype;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class StatisticsVisualization {

    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private final LineChart<Number, Number> numbersChart = new LineChart<Number, Number>(xAxis, yAxis);
    private final XYChart.Series<Number, Number> grassSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> animalSeries = new XYChart.Series<>();
    private final Label genotypeLabel = new Label("");
    private final Label averageEnergyLabel = new Label("");
    private final Label averageChildrenLabel = new Label("");
    private final Label averageLengthOfLifeLabel = new Label("");
    private final Label selectedAnimalGenotypeLabel = new Label("Selected genotype: ");
    private final Label selectedAnimalChildNumberLabel = new Label("Selected child number: ");
    private final Label selectedAnimalDescendantNumberLabel = new Label("Selected descendant number: ");
    private final Label selectedAnimalDeathLabel = new Label("Selected death: ");

    private final GridPane statisticGridPane;

    public StatisticsVisualization(GridPane gridPane) {
        this.statisticGridPane = gridPane;

        // create chart
        grassSeries.setName("Grass");
        animalSeries.setName("Animal");
        numbersChart.getData().add(grassSeries);
        numbersChart.getData().add(animalSeries);
        statisticGridPane.add(numbersChart, 0, 0, 1, 1);

        // create statistics labels
        statisticGridPane.add(genotypeLabel, 0, 1, 1, 1);
        statisticGridPane.add(averageEnergyLabel, 0, 2, 1, 1);
        statisticGridPane.add(averageChildrenLabel, 0, 3, 1, 1);
        statisticGridPane.add(averageLengthOfLifeLabel, 0, 4, 1, 1);
        statisticGridPane.add(selectedAnimalGenotypeLabel, 0, 5, 1, 1);
        statisticGridPane.add(selectedAnimalChildNumberLabel, 0, 6, 1, 1);
        statisticGridPane.add(selectedAnimalDescendantNumberLabel, 0, 7, 1, 1);
        statisticGridPane.add(selectedAnimalDeathLabel, 0, 8, 1, 1);
    }

    public void update(int day, int animalsNumber, int grassNumber, Genotype dominantGenotype, double averageEnergy, double averageChildrenNumber, double averageLengthOfLife, Animal selectedAnimal) {
        grassSeries.getData().add(new XYChart.Data(day, grassNumber));
        animalSeries.getData().add(new XYChart.Data(day, animalsNumber));
        genotypeLabel.setText("Dominant genotype: " + dominantGenotype);
        averageEnergyLabel.setText("Average animal energy: " + averageEnergy);
        averageChildrenLabel.setText("Average children number: " + averageChildrenNumber);
        averageLengthOfLifeLabel.setText("Average animal length of life: " + averageLengthOfLife);
        if (selectedAnimal != null) {
            selectedAnimalGenotypeLabel.setText("Selected genotype: " + selectedAnimal.getGenotype().toString());
            selectedAnimalChildNumberLabel.setText("Selected child number: " + selectedAnimal.getChildrenNumber());
            selectedAnimalDescendantNumberLabel.setText("Selected descendant number: " + selectedAnimal.getDescendantNumber());
            if (selectedAnimal.getDeathDate() == -1) {
                selectedAnimalDeathLabel.setText("Selected animal still alive");
            } else {
                selectedAnimalDeathLabel.setText("Selected death date: " + selectedAnimal.getDeathDate());
            }
        }
    }

}
