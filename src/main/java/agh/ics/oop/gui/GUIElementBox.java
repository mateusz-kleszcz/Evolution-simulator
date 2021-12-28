package agh.ics.oop.gui;

import agh.ics.oop.interfaces.IMapElement;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GUIElementBox {

    private final VBox verticalBox = new VBox();

    public GUIElementBox (IMapElement element) {
        try {
            Image elementImage = new Image(new FileInputStream("src/main/resources/" + element.getImagePath() + ".png"));
            ImageView imageView = new ImageView(elementImage);
            imageView.setRotate(45 * element.getImageRotation());
            imageView.setFitWidth(20);
            imageView.setFitHeight(12);
            verticalBox.getChildren().add(imageView);
            verticalBox.setAlignment(Pos.CENTER);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public VBox getVerticalBox() {
        return this.verticalBox;
    }

}
