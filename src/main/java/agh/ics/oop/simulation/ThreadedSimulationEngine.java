package agh.ics.oop.simulation;

import agh.ics.oop.classes.Animal;
import agh.ics.oop.classes.Genotype;
import agh.ics.oop.classes.Vector2d;
import agh.ics.oop.gui.MapVisualization;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;

import java.util.*;

public class ThreadedSimulationEngine implements Runnable{

    // variable to randomized actions
    private static final Random random = new Random();

    // variables to manage thread
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();

    // initial properties of engine
    private final AbstractWorldMap map;
    private final double initialAnimalEnergy;
    private final int moveEnergy;
    private final double eatingEnergy;
    private final boolean isSimulationMagic;
    private short magicRemains; // magic can happend only 3 times

    // list of animals
    private final LinkedList<Animal> animals = new LinkedList<Animal>();
    // hash map of genotypes used to find the most frequent one
    private final HashMap<Genotype, Integer> genotypes = new LinkedHashMap<Genotype, Integer>();

    // statistics and map
    private final MapVisualization mapVisualization;
    private final Statistics statistics;

    // info about day
    private int day = 0;
    private int deadAnimalsInCurrentDay = 0;
    private int sumOfDeadAnimalsLifeLengthsInCurrentDay = 0;

    // selected animal
    private Animal selectedAnimal = null;

    public ThreadedSimulationEngine(
            AbstractWorldMap worldMap,
            int initialAnimalNumber,
            double initialAnimalEnergy,
            int moveEnergy,
            double eatingEnergy,
            boolean isSimulationMagic,
            GridPane mapGridPane,
            Statistics statistics
    ) {
        this.map = worldMap;
        this.initialAnimalEnergy = initialAnimalEnergy;
        this.moveEnergy = moveEnergy;
        this.eatingEnergy = eatingEnergy;
        this.mapVisualization = new MapVisualization(mapGridPane, this.map, this);
        this.statistics = statistics;
        this.isSimulationMagic = isSimulationMagic;
        this.magicRemains = 3;
        // create animals and add it to the map and the engine's list of all animals
        for (int i = 0; i < initialAnimalNumber; i++) {
            Animal animal = new Animal(this.map, this.getRandomNonOccupiedPosition(), this.initialAnimalEnergy);
            spawnAnimal(animal);
        }
        this.map.createPossibleSpawnGrassLocations();
    }

    public void spawnAnimal(Animal animal) {
        animals.add(animal);
        Genotype genotype = animal.getGenotype();
        int numberOfSameGenotypes = genotypes.get(genotype) == null ? 1 : genotypes.get(genotype) + 1;
        genotypes.put(animal.getGenotype(), numberOfSameGenotypes);
    }

    public Vector2d getRandomNonOccupiedPosition() {
        int width = this.map.getWidth();
        int height = this.map.getHeight();
        // find place that is not occupied yet
        Vector2d position = new Vector2d(random.nextInt(width - 1), random.nextInt(height - 1));
        while (this.map.isOccupied(position)) {
            position = new Vector2d(random.nextInt(width - 1), random.nextInt(height - 1));
        }
        return position;
    }

    public void setSelectedAnimal(Animal animal) {
        this.selectedAnimal = animal;
    }

    // move all animals and check is any animal die
    public void moveAnimalsAndCheckDeaths() {
        // using iterator to avoid looping through array and deleting at the same time errors
        Iterator<Animal> animalIterator = this.animals.iterator();
        while (animalIterator.hasNext()) {
            Animal animal = animalIterator.next();
            animal.move(moveEnergy);
            if (animal.getEnergy() < 0) {
                this.sumOfDeadAnimalsLifeLengthsInCurrentDay += animal.getLifeLength();
                this.deadAnimalsInCurrentDay++;
                animal.setDeathDate(this.day);
                animal.animalDie(animal);
                animalIterator.remove();
            }
        }
    }

    public void eatGrassAndCopulate() {
        Map<Vector2d, ArrayList<Animal>> positionsWithAnimals = this.map.getAnimals();
        // iterate through all positions with animals
        positionsWithAnimals.forEach((position, animals) -> {
            ArrayList<Animal> animalsWithLargestEnergy = new ArrayList<>();
            ArrayList<Animal> animalsWithSecondLargestEnergy = new ArrayList<>();
            // find animals with the largest energy on one field
            double largestEnergy = 0;
            double secondLargestEnergy = 0;
            for (Animal animal : animals) {
                double animalEnergy = animal.getEnergy();
                if (animalEnergy > largestEnergy) {
                    secondLargestEnergy = largestEnergy;
                    largestEnergy = animal.getEnergy();
                } else if (animalEnergy > secondLargestEnergy) {
                    secondLargestEnergy = animalEnergy;
                }
            }
            for (Animal animal : animals) {
                double animalEnergy = animal.getEnergy();
                if (animalEnergy == largestEnergy) {
                    animalsWithLargestEnergy.add(animal);
                }
                if (animalEnergy == secondLargestEnergy) {
                    animalsWithSecondLargestEnergy.add(animal);
                }
            }
            // if on the position is grass we delete it from dictionary and add energy for all strongest animal
            if (this.map.eatGrass(position)) {
                for (Animal animal : animalsWithLargestEnergy) {
                    animal.eat(eatingEnergy / animalsWithLargestEnergy.size());
                }
            }
            // if there is two animals, get strongest and create new one
            // also minimal energy to copulate is half of initial
            if (largestEnergy >= this.initialAnimalEnergy / 2 || secondLargestEnergy >= this.initialAnimalEnergy / 2) {
                if (animalsWithLargestEnergy.size() >= 2) {
                    Animal firstAnimal = animalsWithLargestEnergy.remove(random.nextInt(animalsWithLargestEnergy.size()));
                    Animal secondAnimal = animalsWithLargestEnergy.get(random.nextInt(animalsWithLargestEnergy.size()));
                    Animal child = firstAnimal.copulate(secondAnimal);
                    spawnAnimal(child);
                } else if (animalsWithLargestEnergy.size() == 1 && animalsWithSecondLargestEnergy.size() >= 1) {
                    Animal firstAnimal = animalsWithLargestEnergy.get(random.nextInt(animalsWithLargestEnergy.size()));
                    Animal secondAnimal = animalsWithSecondLargestEnergy.get(random.nextInt(animalsWithSecondLargestEnergy.size()));
                    Animal child = firstAnimal.copulate(secondAnimal);
                    spawnAnimal(child);
                }
            }
        });
    }

    public void useMagic() {
        for (int i = 4; i >= 0; i--) {
            Animal animal = this.animals.get(i);
            Animal animalCopy = new Animal(this.map, this.getRandomNonOccupiedPosition(), this.initialAnimalEnergy, animal.getGenotype());
            this.spawnAnimal(animalCopy);
        }
    }

    @Override
    public void run() {
        while (animals.size() != 0) {
            // manage thread
            synchronized (pauseLock) {
                if (paused) {
                    try {
                        synchronized (pauseLock) {
                            pauseLock.wait();
                        }
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
            }
            // call all actions for day
            this.day++;
            this.moveAnimalsAndCheckDeaths();
            this.eatGrassAndCopulate();
            this.map.spawnGrass();
            if (this.isSimulationMagic) {
                if (this.animals.size() == 5 && this.magicRemains > 0) {
                    this.useMagic();
                    this.magicRemains--;
                }
            }
            Platform.runLater(() -> {
                this.mapVisualization.updateVisualization();
                this.statistics.generateOneDayStatistics(day, animals, this.map.getGrassNumber(), genotypes, deadAnimalsInCurrentDay, sumOfDeadAnimalsLifeLengthsInCurrentDay, this.selectedAnimal);
            });
            // delay between operations
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    // methods to manage thread waiting
    public boolean isThreadPaused() {
        return this.paused;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        synchronized(pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

}
