package agh.ics.oop.gui;

import agh.ics.oop.simulation.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class SettingsMenu {

    private final GridPane gridPane;
    private TextField widthInput;
    private TextField heightInput;
    private TextField animalNumberInput;
    private TextField animalEnergyInput;
    private TextField moveEnergyInput;
    private TextField eatingEnergyInput;
    private TextField jungleRatioInput;
    private CheckBox isMagicInput;

    SettingsMenu(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    // methods that creates inputs with labels and default values
    public TextField createTextInput(String labelText, String defaultFieldValue, int columnIndex) {
        HBox box = new HBox();
        Label label = new Label(labelText);
        TextField field = new TextField(defaultFieldValue);
        box.getChildren().addAll(label, field);
        gridPane.add(box, 0, columnIndex, 1,1);
        return field;
    }

    public CheckBox createCheckbox(String labelText, boolean defaultCheckboxValue, int columnIndex) {
        HBox box = new HBox();
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(defaultCheckboxValue);
        Label label = new Label(labelText);
        box.getChildren().addAll(label, checkBox);
        gridPane.add(box, 0, columnIndex, 1, 1);
        return checkBox;
    }

    // create settings menu
    public void createSettingsMenu() {
        widthInput = createTextInput("Map width", "20", 0);
        heightInput = createTextInput("Map height", "20", 1);
        animalNumberInput = createTextInput("Initial number of animals", "10", 2);
        animalEnergyInput = createTextInput("Initial animal energy", "100", 3);
        moveEnergyInput = createTextInput("Animal move energy", "1", 4);
        eatingEnergyInput = createTextInput("Energy after eating", "50", 5);
        jungleRatioInput = createTextInput("Proportion of width and height of jungle", "2", 6);
        isMagicInput = createCheckbox("Magic simulation ", false, 7);
        Button startButton = new Button("Start");
        startButton.setOnAction(e -> {
            gridPane.getChildren().clear();
            createSimulation(true, 0);
            createSimulation(false, 1);
        });
        gridPane.add(startButton, 0, 8, 1, 1);
    }

    // get values from inputs and create two simulations
    public void createSimulation(boolean isWrapped, int columnIndex) {
        int width = Integer.parseInt(widthInput.getText());
        int height = Integer.parseInt(heightInput.getText());
        int initialAnimalNumber = Integer.parseInt(animalNumberInput.getText());
        double initialAnimalEnergy = Double.parseDouble(animalEnergyInput.getText());
        int moveEnergy = Integer.parseInt(moveEnergyInput.getText());
        double eatingEnergy = Double.parseDouble(eatingEnergyInput.getText());
        double jungleRatio = Double.parseDouble(jungleRatioInput.getText());
        boolean isSimulationMagic = isMagicInput.isSelected();

        // create map used in simulation
        AbstractWorldMap map;
        if (isWrapped) {
            map = new BoundedMap(width, height, jungleRatio);
        } else {
            map = new WrappedMap(width, height, jungleRatio);
        }

        // grid used in map visualization
        GridPane mapGridPane = new GridPane();
        //statistic used in simulation
        GridPane statisticsPane = new GridPane();
        StatisticsVisualization statisticsVisualization = new StatisticsVisualization(statisticsPane);
        Statistics statistics = new Statistics(statisticsVisualization, isWrapped ? "wrapped.csv" : "bounded.csv");

        //engine as a new thread
        ThreadedSimulationEngine simulationEngine = new ThreadedSimulationEngine(
                map,
                initialAnimalNumber,
                initialAnimalEnergy,
                moveEnergy,
                eatingEnergy,
                isSimulationMagic,
                mapGridPane,
                statistics
        );

        Thread engineThread = new Thread(simulationEngine);
        engineThread.start();

        // create controls to pause thread and save csv
        HBox controlsBox = new HBox();
        Button pauseButton = new Button("Pause");
        Button saveToCsvButton = new Button("Save to csv");
        pauseButton.setOnAction(pauseEvent -> {
            if (simulationEngine.isThreadPaused()) {
                simulationEngine.resume();
                pauseButton.setText("Pause");
            } else {
                simulationEngine.pause();
                pauseButton.setText("Play");
            }

        });
        saveToCsvButton.setOnAction(selectEvent -> {
            if (simulationEngine.isThreadPaused()) {
                statistics.saveCsvFile();
            }
        });
        controlsBox.getChildren().addAll(pauseButton, saveToCsvButton);

        // add map visualization statistics and controls to view
        gridPane.add(mapGridPane, columnIndex, 0, 1, 1);
        gridPane.add(controlsBox, columnIndex, 1, 1, 1);
        gridPane.add(statisticsPane, columnIndex, 2, 1, 1);
    }

}
