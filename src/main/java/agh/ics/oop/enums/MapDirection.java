package agh.ics.oop.enums;

import agh.ics.oop.classes.Vector2d;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum MapDirection {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    private static final List<MapDirection> values = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int size = values.size();
    private static final Random random = new Random();

    public int getIntValue() {
        return switch(this) {
            case NORTH -> 0;
            case NORTHEAST -> 1;
            case EAST -> 2;
            case SOUTHEAST -> 3;
            case SOUTH -> 4;
            case SOUTHWEST -> 5;
            case WEST -> 6;
            case NORTHWEST -> 7;
        };
    }

    public Vector2d toUnitVector() {
        return switch (this) {
            case NORTH -> new Vector2d(0, 1);
            case NORTHEAST -> new Vector2d(1, 1);
            case EAST -> new Vector2d(1, 0);
            case SOUTHEAST -> new Vector2d(1, -1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTHWEST -> new Vector2d(-1, -1);
            case WEST -> new Vector2d(-1, 0);
            case NORTHWEST -> new Vector2d(-1, 1);
        };
    }

    public MapDirection getRandomValue() {
        return values.get(random.nextInt(size));
    }

    public MapDirection addDirections(int addedValue) {
        return values.get((this.getIntValue() + addedValue) % size);
    }
}
