package agh.ics.oop.classes;

import agh.ics.oop.enums.MapDirection;
import agh.ics.oop.interfaces.IMapElement;
import agh.ics.oop.interfaces.IPositionChangeObserver;
import agh.ics.oop.interfaces.IWorldMap;
import agh.ics.oop.simulation.WrappedMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Animal implements IPositionChangeObserver, IMapElement {

    private MapDirection direction = MapDirection.NORTH;
    private Vector2d position;
    private final double maxEnergy;
    private int lifeLength = 0;
    private double energy;
    private final IWorldMap map;
    private Genotype genotype;
    private int childrenNumber;
    private int descendantNumber;
    private int deathDate = -1;
    private final ArrayList<IPositionChangeObserver> observersList = new ArrayList<>();

    public Animal(IWorldMap map, Vector2d initialPosition, double initialEnergy) {
        this.map = map;
        this.position = initialPosition;
        this.maxEnergy = initialEnergy;
        this.energy = initialEnergy;
        this.direction = this.direction.getRandomValue();
        this.map.place(this);
        this.genotype = new Genotype();
        this.childrenNumber = 0;
    }

    public Animal(IWorldMap map, Vector2d initialPosition, double initialEnergy, Genotype genotype) {
        this(map, initialPosition, initialEnergy);
        this.genotype = genotype;
    }

    // getters and setters
    public Vector2d getPosition() {
        return this.position;
    }

    public double getEnergy() {
        return this.energy;
    }

    public void setEnergy(double newEnergy) {
        this.energy = newEnergy;
    }

    public Genotype getGenotype() {
        return this.genotype;
    }

    public int getChildrenNumber() {
        return this.childrenNumber;
    }

    public int getDescendantNumber() {
        return this.descendantNumber;
    }

    public int getDeathDate() {
        return this.deathDate;
    }

    public void setDeathDate(int deathDate) {
        this.deathDate = deathDate;
    }

    public int getLifeLength() {
        return this.lifeLength;
    }

    public void addChild () {
        this.childrenNumber += 1;
    }

    // move animal if it is possible
    public void move(int moveEnergy) {
        this.lifeLength++;
        Vector2d oldPosition = this.position;
        // get gene that decide what animal will do in current day
        int moveGene = this.genotype.getRandomGene();
        Vector2d newPosition;
        switch (moveGene) {
            case 0 -> newPosition = this.position.add(this.direction.toUnitVector());
            case 4 -> newPosition = this.position.add(this.direction.toUnitVector().opposite());
            default -> {
                this.direction = this.direction.addDirections(moveGene);
                newPosition = this.position;
            }
        }
        // lose energy
        this.energy -= moveEnergy;
        // if map is wrapped, calculate is animal getting to the other side
        if (this.map instanceof WrappedMap) {
            newPosition = ((WrappedMap) this.map).getWrappedPosition(newPosition);
        }

        // if animal wants to move (gene 0 or 4) and it is possible, move it
        if (this.map.canMoveTo(newPosition) && !newPosition.equals(oldPosition)) {
            this.position = newPosition;
            // inform observers about position changed
            this.positionChanged(this, oldPosition, newPosition);
        }
    }

    // increase energy after eating
    public void eat(double eatingEnergy) {
        this.energy += eatingEnergy;
    }

    // create new Animal and place it to map
    public Animal copulate(Animal animal) {
        // we can assume that first animal is stronger (in engine stronger animal call method)
        double firstEnergy = this.getEnergy();
        double secondEnergy = animal.getEnergy();
        int[] firstGenes = this.getGenotype().getGenes();
        int[] secondGenes = animal.getGenotype().getGenes();
        boolean isLeftSide = Math.random() < 0.5;
        int breakpoint = (int)((firstEnergy / (firstEnergy + secondEnergy)) * 32);
        int[] newAnimalGenes = new int[32];
        if (isLeftSide) {
            System.arraycopy(firstGenes, 0, newAnimalGenes, 0, breakpoint);
            System.arraycopy(secondGenes, breakpoint, newAnimalGenes, breakpoint, 32 - breakpoint);
        } else {
            System.arraycopy(secondGenes, 0, newAnimalGenes, 0, 32 - breakpoint);
            System.arraycopy(firstGenes, 32 - breakpoint, newAnimalGenes, 32 - breakpoint, breakpoint);
        }
        Arrays.sort(newAnimalGenes);
        Animal child = new Animal(map, animal.getPosition(), this.energy * 0.25 + animal.getEnergy() * 0.25, new Genotype(newAnimalGenes));
        this.energy *= 0.75;
        this.addChild();
        animal.addChild();
        animal.setEnergy(animal.getEnergy() * 0.75);
        return child;
    }

    // observer methods
    public void addObserver(IPositionChangeObserver observer) {
        observersList.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer) {
        observersList.remove(observer);
    }

    public void positionChanged(Animal movedAnimal, Vector2d oldPosition, Vector2d newPosition) {
        for (IPositionChangeObserver observer : observersList) {
            observer.positionChanged(movedAnimal, oldPosition, newPosition);
        }
    }

    public void animalDie(Animal animal) {
        for (IPositionChangeObserver observer : observersList) {
            observer.animalDie(this);
        }
    }

    @Override
    public String toString() {
        return this.direction.toString();
    }

    public String getImagePath() {
        if (this.energy / this.maxEnergy > 0.5) {
            return "animalStrong";
        } else if (this.energy / this.maxEnergy > 0.2) {
            return "animalMedium";
        } else {
            return "animalWeak";
        }
    }

    public int getImageRotation() {
        return this.direction.getIntValue();
    }

}
