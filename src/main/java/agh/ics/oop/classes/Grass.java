package agh.ics.oop.classes;

import agh.ics.oop.interfaces.IMapElement;

public class Grass implements IMapElement {
    private final Vector2d position;

    public Grass(Vector2d position) {
        this.position = position;
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public String toString() {
        return "*";
    }

    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }

    public String getImagePath() {
        return "grass";
    }

    public int getImageRotation() {
        return 0;
    }
}
