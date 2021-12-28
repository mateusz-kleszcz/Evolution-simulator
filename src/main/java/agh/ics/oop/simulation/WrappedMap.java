package agh.ics.oop.simulation;

import agh.ics.oop.classes.Vector2d;

public class WrappedMap extends AbstractWorldMap {

    public WrappedMap(
            int width,
            int height,
            double jungleRatio
    ) {
        super(width, height, jungleRatio);
    }

    // calculate 'wrapped' position - if newPosition coords are not in area, get to the other side of map
    public Vector2d getWrappedPosition(Vector2d position) {
        int newX = position.x;
        int newY = position.y;
        if (position.x < 0) {
            newX = width - 1;
        } else if (position.x >= width) {
            newX = 0;
        }
        if (position.y < 0) {
            newY = height - 1;
        } else if (position.y >= width) {
            newY = 0;
        }
        return new Vector2d(newX, newY);
    }

}
