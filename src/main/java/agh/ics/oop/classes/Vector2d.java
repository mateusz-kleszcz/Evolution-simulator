package agh.ics.oop.classes;

import java.util.Objects;

public class Vector2d {
    // coordinates of vecor
    public final int x;
    public final int y;

    // constructor
    public Vector2d (int x, int y) {
        this.x = x;
        this.y = y;
    }

    // returns coordinates as string (x,y)
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    // returns is both coordinates of vector are lesser than coordinates of different vector
    public boolean precedes(Vector2d other) {
        return this.x <= other.x && this.y <= other.y;
    }

    // returns is both coordinates of vector are greater than coordinates of different vector
    public boolean follows(Vector2d other) {
        return this.x >= other.x && this.y >= other.y;
    }

    // returns new vector with coordinates of the greatest values given vectors
    public Vector2d upperRight(Vector2d other) {
        return new Vector2d(Math.max(this.x, other.x), Math.max(this.y, other.y));
    }

    // returns new vector with coordinates of the least values of given vectors
    public Vector2d lowerLeft(Vector2d other) {
        return new Vector2d(Math.min(this.x, other.x), Math.min(this.y, other.y));
    }

    // returns new vector with coordinates that are sum of coordinates of given vectors
    public Vector2d add(Vector2d other) {
        return new Vector2d(this.x + other.x, this.y + other.y);
    }

    // returns new vector with coordinates that are difference between coordinates of given vectors
    public Vector2d subtract(Vector2d other) {
        return new Vector2d(this.x - other.x, this.y - other.y);
    }

    // check is vectors have equals coordinates
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Vector2d))
            return false;
        Vector2d that = (Vector2d) other;
        return this.x == that.x && this.y == that.y;
    }

    // because equals is override, we need to override hashCode function
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    // returns new vector with opposite coordinates of given vector
    public Vector2d opposite() {
        return new Vector2d(-this.x, -this.y);
    }
}
