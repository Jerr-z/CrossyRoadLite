package model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// overall state of the game, contains almost all info that goes on
public class GameState {

    public static final int CAMERA_SPD = 1;
    public static final int CHICKEN_SPD = 2;
    public static final int CAR_SPD_LW = 1;
    public static final int CAR_SPD_HI = 2;
    public static final int CAR_GEN_PROB = 8; // 1 out of cargenprob

    private HashSet<Position> listOfTrees = new HashSet<>();
    private HashSet<Position> listOfGrass = new HashSet<>();
    private HashSet<Road> listOfRoads = new HashSet<>();
    private HashSet<Car> listOfCars = new HashSet<>();
    private int score;
    private Chicken chicken;
    private int canvasSize;
    private String input;


    // EFFECTS: creates a GameState object with score of 0, canvasSize and initial level
    // REQUIRES: canvasSize > 3
    public GameState(int canvasSize) {
        score = 0;
        this.canvasSize = canvasSize;
        // generate an initial level of canvasSize x canvasSize
        initializeLevel();
        // places chicken in the middle third last row in the middle
        placeChicken();

        this.input = "none";
    }

    // MODIFIES: this
    // EFFECTS: updates various states of the game, main tick function
    // REQUIRES: input must be one of "up", "down", "left", "right" or "none"
    public void tick(String input) {
        this.input = input; // updates user input
        updateGameCamera(); // shifts everything from cam perspective
        removeBottomCars();
        removeBottomGrass();
        removeBottomRoad();
        removeBottomTrees(); // removes terrain that's phased out from memory
        generateTerrain(0); // generating another layer of terrain
        generateCars(); // roads proc car generation
        updateCars();// start moving car
        updateChicken(); // update chicken position

    }

    // MODIFIES: this
    // EFFECTS: randomly generates a map for the player
    public void initializeLevel() {
        // creates random environment for rows - 3 rows
        for (int i = 0; i < canvasSize - 3; i++) {
            generateTerrain(i);
        }

        // for the last 3 rows create just grass
        for (int i = canvasSize - 3; i < canvasSize; i++) {
            generateGrass(canvasSize, i, false);
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes a chicken for the player to control
    public void placeChicken() {
        // places the chicken in the 3rd last row, in the middle
        int midpoint = canvasSize / 2;
        //System.out.println(midpoint);
        chicken = new Chicken(new Position(midpoint, canvasSize - 4));
    }

    // MODIFIES: this
    // EFFECTS: Main level generation function to randomly choose between the 2 terrains,
    // generates a single strip of terrain
    // REQUIRES: 0 <= y <= canvasSize
    public void generateTerrain(int y) {
        int choice = ThreadLocalRandom.current().nextInt(0,4);
        if (choice == 0) {
            // grass
            generateGrass(canvasSize, y, true);
        } else {
            // road
            generateRoad(y);
        }
    }

    // MODIFIES: this
    // EFFECTS: Generates a strip of grass level with length x at row y, with tree on or off
    // REQUIRES: 0 <= x,y <= canvasSize
    public void generateGrass(int x, int y, boolean tree) {
        for (int i = 0; i < x; i++) {
            listOfGrass.add(new Position(i, y));
            if (tree) {
                int choice = ThreadLocalRandom.current().nextInt(0,5);
                if (choice == 0) {
                    generateTree(i,y);
                }
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a road object to listOfRoads with road at level y.
    // REQUIRES: 0 <= y < canvasSize
    public void generateRoad(int y) {
        Road road = new Road(y);
        if (!listOfRoads.contains(road)) {
            listOfRoads.add(road);
        }
    }

    // MODIFIES: this
    // EFFECTS: Generates a tree block on grass
    // REQUIRES: 0 <= both x and y < canvasSize
    public void generateTree(int x, int y) {
        listOfTrees.add(new Position(x,y));
    }

    // MODIFIES: this
    // EFFECTS: Randomly generate cars on all the roads
    public void generateCars() {
        // use random for a small chance to generate car per tick
        for (Road r: listOfRoads) {
            int choice = ThreadLocalRandom.current().nextInt(0,CAR_GEN_PROB);
            if (choice == 0) {
                listOfCars.add(r.generateCar(canvasSize));
            }
        }
    }

    // EFFECTS: checks if chicken hits a car or goes below the bottom bound
    public boolean isChickenDead() {
        // iterate through cars
        Iterator<Car> carIterator = listOfCars.iterator();
        int chickenX = chicken.getPosition().getX();
        int chickenY = chicken.getPosition().getY();

        // if on the bottom of screen
        if (chickenY >= canvasSize - 1) {
            return true;
        }
        for (Car c: listOfCars) {
            if (chicken.getPosition().equals(c.getPosition())) {
                return true;
            }
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: shifts every game object's position down and removes objects out of view
    public void updateGameCamera() {
        removeBottomTrees();
        removeBottomRoad();
        removeBottomGrass();
        removeBottomCars();
        chicken.updatePos(0, CAMERA_SPD);
        moveRoadDown();
        moveGrassDown();
        moveTreesDown();
        moveCarsDown();
    }

    // MODIFIES: this
    // EFFECTS: sub function for updateGameCamera, moves the road down
    public void moveRoadDown() {
        // move road down
        Iterator<Road> roadIterator = listOfRoads.iterator();
        while (roadIterator.hasNext()) {
            Road road = roadIterator.next();
            road.update(CAMERA_SPD);
        }
    }

    // MODIFIES: this
    // EFFECTS: sub function for updateGameCamera, moves the grass down
    public void moveGrassDown() {
        //move grass down
        Iterator<Position>  grassIterator = listOfGrass.iterator();
        while (grassIterator.hasNext()) {
            Position grass = grassIterator.next();
            grass.updatePosition(0, CAMERA_SPD);
        }
    }

    // MODIFIES: this
    // EFFECTS: sub function for updateGameCamera, moves the Trees down
    public void moveTreesDown() {
        // move trees down
        Iterator<Position> treeIterator = listOfTrees.iterator();
        while (treeIterator.hasNext()) {
            Position tree = treeIterator.next();
            tree.updatePosition(0, CAMERA_SPD);
        }
    }

    // MODIFIES: this
    // EFFECTS: sub function for updateGameCamera, moves the Cars down
    public void moveCarsDown() {
        // move cars down
        Iterator<Car> carIterator = listOfCars.iterator();
        while (carIterator.hasNext()) {
            Car car = carIterator.next();
            car.updatePosition(0, CAMERA_SPD);
        }
    }

    // MODIFIES: this
    // EFFECTS: update chicken position based on user input
    public void updateChicken() {
        HashSet<Position> nextPos = nextValidPosForChicken();
        Position stepRight = new Position(chicken.getPosition().getX() + CHICKEN_SPD, chicken.getPosition().getY());
        Position stepLeft = new Position(chicken.getPosition().getX() - CHICKEN_SPD, chicken.getPosition().getY());
        Position stepDown = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() + CHICKEN_SPD);
        Position stepUp = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() - CHICKEN_SPD);
        // update chicken position based on input
        if (input == "up" && nextPos.contains(stepUp)) {
            chicken.updatePos(0,-CHICKEN_SPD);
            updateScore(1);
        } else if (input == "down" && nextPos.contains(stepDown)) {
            chicken.updatePos(0,CHICKEN_SPD);
            updateScore(-1);
        } else if (input == "left" && nextPos.contains(stepLeft)) {
            chicken.updatePos(-CHICKEN_SPD,0);
        } else if (input == "right" && nextPos.contains(stepRight)) {
            chicken.updatePos(CHICKEN_SPD,0);
        }
        return;
    }

    // MODIFIES: this
    // EFFECTS: moves the cars in their direction with their speed
    public void updateCars() {
        removeCarsOutOfBounds();
        // iterate through listOfCars
        Iterator<Car> carIterator = listOfCars.iterator();
        while (carIterator.hasNext()) {
            Car car = carIterator.next();
            car.move();
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes cars that are out of left right bound
    public void removeCarsOutOfBounds() {
        Iterator<Car> carIterator = listOfCars.iterator();
        while (carIterator.hasNext()) {
            Car car = carIterator.next();
            Position carPos = car.getPosition();
            if (!carPos.withinBoundary(canvasSize, canvasSize)) {
                carIterator.remove();
            }
        }
    }


    // EFFECTS: removes the most bottom layer of grass if its about to be phased out
    // of view
    // MODIFIES: this
    public void removeBottomGrass() {
        listOfGrass.removeIf(grass -> grass.getY() == canvasSize - 1);
    }

    // EFFECTS: removes the most bottom layer of trees if its about to be phased out
    // of view
    // MODIFIES: this
    public void removeBottomTrees() {
        listOfTrees.removeIf(tree -> tree.getY() == canvasSize - 1);
    }

    // EFFECTS: removes the most bottom layer of cars if its about to be phased out
    // of view
    // MODIFIES: this
    public void removeBottomCars() {
        listOfCars.removeIf(car -> car.getPosition().getY() == canvasSize - 1);
    }

    // EFFECTS: removes bottom layer of roads if its about to be phased out
    // MODIFIES: this
    public void removeBottomRoad() {
        listOfRoads.removeIf(road -> road.getPosition() == canvasSize - 1);
    }


    // EFFECTS: returns the possible positions for a chicken to go to
    public HashSet<Position> nextValidPosForChicken() {
        HashSet<Position> nextPos = new HashSet<>();
        Position stepRight = new Position(chicken.getPosition().getX() + CHICKEN_SPD, chicken.getPosition().getY());
        Position stepLeft = new Position(chicken.getPosition().getX() - CHICKEN_SPD, chicken.getPosition().getY());
        Position stepDown = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() + CHICKEN_SPD);
        Position stepUp = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() - CHICKEN_SPD);

        if (!listOfTrees.contains(stepRight)
                && stepRight.withinBoundary(canvasSize - 1, canvasSize - 1)) {
            nextPos.add(stepRight);
        }
        if (!listOfTrees.contains(stepLeft)
                && stepLeft.withinBoundary(canvasSize - 1, canvasSize - 1)) {
            nextPos.add(stepLeft);
        }
        if (!listOfTrees.contains(stepDown)
                && stepDown.withinBoundary(canvasSize - 1, canvasSize - 1)) {
            nextPos.add(stepDown);
        }
        if (!listOfTrees.contains(stepUp)
                && stepUp.withinBoundary(canvasSize - 1, canvasSize - 1)) {
            nextPos.add(stepUp);
        }
        return nextPos;
    }

    // MODIFIES: this
    // EFFECTS: updates the score
    public void updateScore(int amount) {
        score += amount;
    }

    public int getScore() {
        return score;
    }

    public Chicken getChicken() {
        return chicken;
    }

    public HashSet<Position> getListOfGrass() {
        return listOfGrass;
    }

    public HashSet<Position> getListOfTrees() {
        return listOfTrees;
    }

    public HashSet<Car> getListOfCars() {
        return listOfCars;
    }

    public HashSet<Road> getListOfRoads() {
        return listOfRoads;
    }

    public String getInput() {
        return input;
    }

    public int getCanvasSize() {
        return canvasSize;
    }

    public void setInput(String input) {
        this.input = input;
    }

}
