package agh.ics.oop.gui;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class GUIElementBox {

    private final VBox verticalBox = new VBox();
    private final ImageView imageView;

    public GUIElementBox (Image elementImage, int rotation) {
        imageView = new ImageView(elementImage);
        imageView.setRotate(45 * rotation);
        imageView.setFitWidth(20);
        imageView.setFitHeight(12);
        verticalBox.getChildren().add(imageView);
        verticalBox.setAlignment(Pos.CENTER);
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public VBox getVerticalBox() {
        return this.verticalBox;
    }

}
