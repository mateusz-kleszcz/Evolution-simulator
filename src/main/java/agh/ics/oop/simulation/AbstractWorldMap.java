package agh.ics.oop.simulation;

import agh.ics.oop.classes.Animal;
import agh.ics.oop.classes.Grass;
import agh.ics.oop.classes.Vector2d;
import agh.ics.oop.interfaces.IPositionChangeObserver;
import agh.ics.oop.interfaces.IWorldMap;

import java.util.*;

public class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {

    protected final Random random = new Random();

    protected final Map<Vector2d, ArrayList<Animal>> animals = new LinkedHashMap<Vector2d, ArrayList<Animal>>();
    protected final Map<Vector2d, Grass> grasses = new LinkedHashMap<Vector2d, Grass>();

    protected final int width;
    protected final int height;
    protected final Vector2d jungleLowerLeft;
    protected final Vector2d jungleUpperRight;
    protected final Vector2d mapLowerLeft;
    protected final Vector2d mapUpperRight;

    // possible positions where grass can grow
    protected final Map<Vector2d, Boolean> possibleGrassPositionsStep = new LinkedHashMap<Vector2d, Boolean>();
    protected final Map<Vector2d, Boolean> possibleGrassPositionsJungle = new LinkedHashMap<Vector2d, Boolean>();

    public AbstractWorldMap (
            int width,
            int height,
            double jungleRatio
    ) {
        this.width = width;
        this.height = height;
        // create vertexes that limiting the map
        this.mapLowerLeft = new Vector2d(0, 0);
        this.mapUpperRight = new Vector2d(this.width - 1, this.height - 1);
        // calculate vertex of jungle
        double lowLeft = (jungleRatio - 1) / (2 * jungleRatio);
        double uppRight = (jungleRatio + 1) / (2 * jungleRatio);
        this.jungleLowerLeft = new Vector2d((int)(lowLeft * width), (int)(lowLeft * height));
        this.jungleUpperRight = new Vector2d((int)(uppRight * width), (int)(uppRight * height));
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Map<Vector2d, ArrayList<Animal>> getAnimals() {
        return this.animals;
    }

    public int getGrassNumber() {
        return this.grasses.size();
    }

    public void addAnimalAtPosition (Animal animal, Vector2d animalPosition) {
        // get animals on this position
        ArrayList<Animal> animalsAtPosition = this.animals.get(animalPosition);
        // if it is first animal in the position we need to create array list
        if (animalsAtPosition == null) {
            animalsAtPosition = new ArrayList<>();
        }
        // add animals to this array
        animalsAtPosition.add(animal);
        this.animals.put(animalPosition, animalsAtPosition);
        // mark field, grass cannot grow on this position
        this.getPossibleGrass(animalPosition).remove(animalPosition);
    }

    public void removeAnimalFromPosition (Animal animal, Vector2d animalPosition) {
        // get all animals on this position
        ArrayList<Animal> animalsAtPosition = this.animals.get(animalPosition);
        // find animal and remove it
        animalsAtPosition.removeIf(a -> a.equals(animal));
        // if that was last animal on this field, mark that grass can grow and mark animal position field as null
        if (animalsAtPosition.size() == 0) {
            this.getPossibleGrass(animalPosition).put(animalPosition, true);
            this.animals.remove(animalPosition);
        }
    }

    // check is given position is jungle or step, returns jungle or step possible grass positions
    public boolean isPositionJungle(Vector2d position) {
        if (position.precedes(this.jungleUpperRight) && position.follows(this.jungleLowerLeft)) {
            return true;
        } else {
            return false;
        }
    }

    public Map<Vector2d, Boolean> getPossibleGrass(Vector2d position) {
        if (this.isPositionJungle(position)) {
            return this.possibleGrassPositionsJungle;
        } else {
            return this.possibleGrassPositionsStep;
        }
    };

    public void createPossibleSpawnGrassLocations() {
        // check all possible coordinates
        for (int i = 0; i <= this.width; i++) {
            for (int j = 0; j <= height; j++) {
                // find is animal on this location
                Vector2d position = new Vector2d(i, j);
                if (!this.isOccupied(position)) {
                    // check in which area is position and add possibility to list
                    this.getPossibleGrass(position).put(position, true);
                }
            }
        }
    }

    // spawn one grass in empty field in jungle and step
    public void spawnGrass() {
        // get possible positions
        List<Vector2d> possibleFieldsStep = new ArrayList<Vector2d>(this.possibleGrassPositionsStep.keySet());
        List<Vector2d> possibleFieldsJungle = new ArrayList<Vector2d>(this.possibleGrassPositionsJungle.keySet());
        // if there is a place when we can spawn grass
        if (possibleFieldsStep.size() != 0) {
            // select random position, create grass and add to grass dictionary
            Grass newStepGrass = new Grass(possibleFieldsStep.get(random.nextInt(possibleFieldsStep.size())));
            grasses.put(newStepGrass.getPosition(), newStepGrass);
            // remove fields from dictionary of possible spawn positions
            this.possibleGrassPositionsStep.remove(newStepGrass.getPosition());
        }
        // do same things for jungle grass
        if (possibleFieldsJungle.size() != 0) {
            Grass newJungleGrass = new Grass(possibleFieldsJungle.get(random.nextInt(possibleFieldsJungle.size())));
            grasses.put(newJungleGrass.getPosition(), newJungleGrass);
            this.possibleGrassPositionsStep.remove(newJungleGrass.getPosition());
        }
    }

    // if the field isn't occupied yet, we can place animal here
    public boolean place(Animal animal) {
        Vector2d animalPosition = animal.getPosition();
        this.addAnimalAtPosition(animal, animalPosition);
        // add animal observer
        animal.addObserver(this);
        return true;
    }

    // we can move animal to place (only to keep IWorldMap)
    public boolean canMoveTo(Vector2d position) {
        return true;
    }

    // check is any object appearing on position (animal or grass)
    public boolean isOccupied(Vector2d position) {
        return this.objectAt(position) != null;
    }

    // check is animal exists on given position than check is grass exists, otherwise return null
    public Object objectAt(Vector2d position) {
        ArrayList<Animal> animalsAtPosition = animals.get(position);
        if (animalsAtPosition == null) {
            Grass grass = grasses.get(position);
            return grass;
        } else {
            return animalsAtPosition.get(0);
        }
    }

    // update details in map when animal's position changed
    public void positionChanged(Animal movedAnimal, Vector2d oldPosition, Vector2d newPosition) {
        // update animal position in animals dictionary
        this.removeAnimalFromPosition(movedAnimal, oldPosition);
        this.addAnimalAtPosition(movedAnimal, newPosition);
    }

    // remove dead animals from dictionary
    public void animalDie(Animal animal) {
        Vector2d animalPosition = animal.getPosition();
        if (this.animals.containsKey(animalPosition)) {
            this.removeAnimalFromPosition(animal, animalPosition);
        }
    }

    // update grass that animals eat in this day
    public boolean eatGrass (Vector2d position) {
        if (!this.grasses.containsKey(position)) {
            return false;
        } else {
            this.grasses.remove(position);
            return true;
        }
    }

    @Override
    public String toString() {
        MapVisualizer visualizer = new MapVisualizer(this);
        return visualizer.draw(new Vector2d(0, 0), new Vector2d(this.width, this.height));
    }

}
