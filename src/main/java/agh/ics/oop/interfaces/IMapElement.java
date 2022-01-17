package agh.ics.oop.interfaces;
// to że coś jest interfejsem, to słaby wyznacznik czegokolwiek; interfejsy lepiej trzymać tam, gdzie klasy, które je implementują
import agh.ics.oop.classes.Vector2d;

public interface IMapElement {

    Vector2d getPosition();

    String getImagePath();  // lepiej to przenieść do GUI

    int getImageRotation();

}
