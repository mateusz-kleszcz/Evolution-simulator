package agh.ics.oop.gui;

import agh.ics.oop.classes.Animal;
import agh.ics.oop.classes.Vector2d;
import agh.ics.oop.interfaces.IMapElement;
import agh.ics.oop.simulation.AbstractWorldMap;
import agh.ics.oop.simulation.ThreadedSimulationEngine;
import javafx.geometry.HPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MapVisualization {
    private final GridPane gridMap;
    private final AbstractWorldMap map;
    private final ThreadedSimulationEngine engine;

    // cached resources, used to optimize loading images
    private Image animalStrongImage;
    private Image animalMediumImage;
    private Image animalWeakImage;
    private Image grassImage;

    private final HBox[][] cells;
    private final IMapElement[][] objects;
    private final ImageView[][] imageViews;

    public MapVisualization(GridPane gridPane,AbstractWorldMap map, ThreadedSimulationEngine engine) {

        this.gridMap = gridPane;

        // load all resources only once, when map visualization is created
        try {
            animalStrongImage = new Image(new FileInputStream("src/main/resources/animalStrong.png"));
            animalMediumImage = new Image(new FileInputStream("src/main/resources/animalMedium.png"));
            animalWeakImage = new Image(new FileInputStream("src/main/resources/animalWeak.png"));
            grassImage = new Image(new FileInputStream("src/main/resources/grass.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.map = map;
        this.engine = engine;

        this.cells = new HBox[map.getWidth()][map.getHeight()];
        this.objects = new IMapElement[map.getWidth()][map.getHeight()];
        this.imageViews = new ImageView[map.getWidth()][map.getHeight()];

        this.drawBackground();
    }

    public void drawBackground() {
        int width = map.getWidth();
        int height = map.getHeight();

        // add constraints and lines
        for (int i = 0; i < width; i++) {
            gridMap.getColumnConstraints().add(new ColumnConstraints(20));
        }
        for (int i = 0; i < height; i++) {
            gridMap.getRowConstraints().add(new RowConstraints(20));
        }

        // draw background (jungle is darker green)
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Vector2d position = new Vector2d(i, j);
                HBox cell = new HBox();
                if (map.isPositionJungle(position)) {
                    cell.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
                } else {
                    cell.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
                }
                gridMap.add(cell, i, height - j - 1, 1, 1);
                GridPane.setHalignment(cell, HPos.CENTER);
                this.cells[i][j] = cell;
                this.objects[i][j] = null;
                this.imageViews[i][j] = null;
            }
        }
    }

    public Image getImage(IMapElement element) {
        return switch (element.getImagePath()) {
            case "animalStrong" -> animalStrongImage;
            case "animalMedium" -> animalMediumImage;
            case "animalWeak" -> animalWeakImage;
            case "grass" -> grassImage;
            default -> null;
        };
    }

    public void updateVisualization() {
        int width = map.getWidth();
        int height = map.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Vector2d position = new Vector2d(i, j);
                HBox cell = this.cells[i][j];
                IMapElement previousObjectAtPosition = this.objects[i][j];
                IMapElement mapElement = (IMapElement) map.objectAt(position);
                // if on current position element changed, create new resource and load it
                if ((previousObjectAtPosition == null || !previousObjectAtPosition.equals(mapElement)) && mapElement != null) {
                    GUIElementBox mapElementBox = new GUIElementBox(this.getImage(mapElement), mapElement.getImageRotation());
                    cell.getChildren().clear();
                    cell.getChildren().add(mapElementBox.getVerticalBox());
                    this.objects[i][j] = mapElement;
                    this.imageViews[i][j] = mapElementBox.getImageView();
                    if (mapElement instanceof Animal) {
                        cell.setOnMouseClicked(e -> {
                            if (engine.isThreadPaused()) {
                                Animal animal = (Animal) mapElement;
                                engine.setSelectedAnimal(animal);
                            }
                        });
                    }
                }
                // if it is the same element check it is animal and if it is, check rotation
                else {
                    if (mapElement == null) {
                        cell.getChildren().clear();
                        this.objects[i][j] = null;
                        this.imageViews[i][j] = null;
                    }
                    if (mapElement instanceof Animal) {
                        this.imageViews[i][j].setRotate(45 * mapElement.getImageRotation());
                    }
                }
            }
        }

    }

}
