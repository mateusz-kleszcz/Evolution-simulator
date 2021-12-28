package agh.ics.oop.simulation;

import agh.ics.oop.classes.Vector2d;

public class BoundedMap extends AbstractWorldMap{

    public BoundedMap(
            int width,
            int height,
            double jungleRatio
    ) {
        super(width, height, jungleRatio);
    }

    // in bounded map, animal cannot walk beyond defined area, otherwise animal lose his move and still losing energy
    @Override
    public boolean canMoveTo(Vector2d position) {
        if (position.precedes(mapUpperRight) && position.follows(mapLowerLeft)) {
            return true;
        }
        return false;
    }

}
