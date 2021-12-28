package agh.ics.oop.interfaces;

import agh.ics.oop.classes.Animal;
import agh.ics.oop.classes.Vector2d;

public interface IPositionChangeObserver {

    // delete animal with old position from dictionary and add animal with new position
    void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition);

    // inform observers about death of animal
    void animalDie(Animal animal);

}
