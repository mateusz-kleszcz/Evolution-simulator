package agh.ics.oop.gui;

import agh.ics.oop.classes.Animal;
import agh.ics.oop.classes.Grass;
import agh.ics.oop.classes.Vector2d;
import agh.ics.oop.interfaces.IMapElement;
import agh.ics.oop.simulation.AbstractWorldMap;
import agh.ics.oop.simulation.ThreadedSimulationEngine;
import javafx.geometry.HPos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class MapVisualization {
    private final GridPane gridMap = new GridPane();

    public MapVisualization(AbstractWorldMap map, ThreadedSimulationEngine engine) {
        int width = map.getWidth();
        int height = map.getHeight();

        // add constraints and lines
        for (int i = 0; i < width; i++) {
            gridMap.getColumnConstraints().add(new ColumnConstraints(20));
        }
        for (int i = 0; i < height; i++) {
            gridMap.getRowConstraints().add(new RowConstraints(20));
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Vector2d position = new Vector2d(i, j);
                IMapElement mapElement = (IMapElement) map.objectAt(position);
                HBox cell = new HBox();
                if (map.isPositionJungle(position)) {
                    cell.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
                } else {
                    cell.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
                }
                if (mapElement instanceof Grass) {
                    GUIElementBox mapElementBox = new GUIElementBox(mapElement);
                    cell.getChildren().add(mapElementBox.getVerticalBox());
                }
                if (mapElement instanceof Animal) {
                    GUIElementBox mapElementBox = new GUIElementBox(mapElement);
                    cell.getChildren().add(mapElementBox.getVerticalBox());
                    cell.setOnMouseClicked(e -> {
                        Animal animal = (Animal) mapElement;
                        engine.setSelectedAnimal(animal);
                    });
                }
                gridMap.add(cell, i, height - j - 1, 1, 1);
                gridMap.setHalignment(cell, HPos.CENTER);
            }
        }

    }

    public GridPane getGridMap() {
        return gridMap;
    }
}
