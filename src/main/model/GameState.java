package model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// overall state of the game
public class GameState {

    public static final int CAMERA_SPD = 1;
    public static final int CHICKEN_SPD = 1;
    public static final int CAR_SPD_LW = 1;
    public static final int CAR_SPD_HI = 3;
    public static final int CAR_GEN_PROB = 4; // 1 out of cargenprob

    private HashSet<Position> listOfTrees = new HashSet<>();
    private HashSet<Position> listOfGrass = new HashSet<>();
    private HashSet<Road> listOfRoads = new HashSet<>();
    private HashSet<Car> listOfCars = new HashSet<>();
    private int score;
    private Chicken chicken;
    private int canvasSize;
    private String input;


    // Constructor
    public GameState(int canvasSize) {
        score = 0;
        // generate an initial level of canvasSize x canvasSize
        initializeLevel();
        // places chicken in the middle third last row in the middle
        placeChicken();

        this.canvasSize = canvasSize;
        this.input = "none";
    }

    public void tick(String input) {
        this.input = input; // updates user input
        updateGameCamera(); // shifts everything from cam perspective
        removeBottomTerrain(); // removes terrain that's phased out from memory
        generateTerrain(0); // generating another layer of terrain
        generateCars(); // roads proc car generation
        updateCars();// start moving car
        updateChicken(); // update chicken position

    }

    // REQUIRES: canvasSize > 3
    public void initializeLevel() {
        // creates random environment for rows - 3 rows
        for (int i = 0; i < canvasSize - 3; i++) {
            generateTerrain(i);
        }

        // for the last 3 rows create just grass
        for (int i = canvasSize - 1; i < canvasSize + 2; i++) {
            generateGrass(canvasSize, i, false);
        }
    }

    public void placeChicken() {
        // places the chicken in the 3rd last row, in the middle
        int midpoint = canvasSize / 2;
        chicken = new Chicken(new Position(midpoint, canvasSize - 4));
    }


    // Main generation function to randomly choose between the 2 terrains
    // Generates a single strip of terrain
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

    // Generate grass level with length x at row y, with tree on or off
    public void generateGrass(int x, int y, boolean tree) {
        for (int i = 0; i < x; i++) {
            listOfGrass.add(new Position(i, y));
            if (tree) {
                int choice = ThreadLocalRandom.current().nextInt(0,2);
                if (choice == 0) {
                    generateTree(x,y);
                }
            }
        }
    }

    // Generate road level
    public void generateRoad(int y) {
        Road road = new Road(y);
        if (!listOfRoads.contains(road)) {
            listOfRoads.add(road);
        }
    }

    // Generate tree on grass with random probability
    public void generateTree(int x, int y) {
        listOfTrees.add(new Position(x,y));
    }

    // randomly generate cars on all the roads
    public void generateCars() {
        // use random for a small chance to generate car per tick
        for (Road r: listOfRoads) {
            int choice = ThreadLocalRandom.current().nextInt(0,CAR_GEN_PROB);
            if (choice == 0) {
                r.generateCar(canvasSize);
            }
        }
    }

    // check chicken death
    public boolean isChickenDead() {
        // iterate through cars
        Iterator<Car> carIterator = listOfCars.iterator();
        int chickenX = chicken.getPosition().getX();
        int chickenY = chicken.getPosition().getY();

        // if on the bottom of screen
        if (chickenY == canvasSize - 1) {
            return true;
        }
        while (carIterator.hasNext()) {
            Car car = carIterator.next();
            int carX = car.getPosition().getX();
            int carY = car.getPosition().getY();
            int carSpd = car.getSpeed();
            List<Integer> carHitBox = IntStream.rangeClosed(carX - carSpd, carX)
                    .boxed().collect(Collectors.toList());
            // if car passed thru chicken after tick
            if (chickenY == carY && carHitBox.contains(chickenX)) {
                return true;
            }
        }
        return false;
    }

    // update camera (shift everything down)
    public void updateGameCamera() {
        removeBottomTerrain();
        chicken.updatePos(0, CAMERA_SPD);
        moveRoadDown();
        moveGrassDown();
        moveTreesDown();
        moveCarsDown();

    }

    // sub function for updateGameCamera, moves the road down
    public void moveRoadDown() {
        // move road down
        Iterator<Road> roadIterator = listOfRoads.iterator();
        while (roadIterator.hasNext()) {
            Road road = roadIterator.next();
            road.update(CAMERA_SPD);
        }
    }

    // sub function for updateGameCamera, moves the grass down
    public void moveGrassDown() {
        //move grass down
        Iterator<Position>  grassIterator = listOfGrass.iterator();
        while (grassIterator.hasNext()) {
            Position grass = grassIterator.next();
            grass.updatePosition(0, CAMERA_SPD);
        }
    }

    // sub function for updateGameCamera, moves the Trees down
    public void moveTreesDown() {
        // move trees down
        Iterator<Position> treeIterator = listOfTrees.iterator();
        while (treeIterator.hasNext()) {
            Position tree = treeIterator.next();
            tree.updatePosition(0, CAMERA_SPD);
        }
    }

    // sub function for updateGameCamera, moves the Cars down
    public void moveCarsDown() {
        // move cars down
        Iterator<Car> carIterator = listOfCars.iterator();
        while (carIterator.hasNext()) {
            Car car = carIterator.next();
            car.updatePosition(0, CAMERA_SPD);
        }
    }

    // update chicken position based on user input
    public void updateChicken() {
        HashSet<Position> nextPos = nextValidPosForChicken();
        Position stepRight = new Position(chicken.getPosition().getX() + 1, chicken.getPosition().getY());
        Position stepLeft = new Position(chicken.getPosition().getX() - 1, chicken.getPosition().getY());
        Position stepDown = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() + 1);
        Position stepUp = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() - 1);
        // update chicken position based on input
        if (input == "up" && nextPos.contains(stepUp)) {
            chicken.updatePos(0,-1);
            updateScore(1);
        } else if (input == "down" && nextPos.contains(stepDown)) {
            chicken.updatePos(0,1);
            updateScore(-1);
        } else if (input == "left" && nextPos.contains(stepLeft)) {
            chicken.updatePos(-1,0);
        } else if (input == "right" && nextPos.contains(stepRight)) {
            chicken.updatePos(1,0);
        }
        return;
    }

    // moves the cars in their direction with their speed
    public void updateCars() {
        removeCarsOutOfBounds();
        // iterate through listOfCars
        Iterator<Car> carIterator = listOfCars.iterator();
        while (carIterator.hasNext()) {
            Car car = carIterator.next();
            car.move();
        }
    }

    public void removeCarsOutOfBounds() {
        // TODO
    }


    public void removeBottomTerrain() {
        Iterator<Road> roadIterator = listOfRoads.iterator();
        Iterator<Position> treeIterator = listOfTrees.iterator();
        Iterator<Car> carIterator = listOfCars.iterator();

        while (roadIterator.hasNext()) {
            Road road = roadIterator.next();
            if (road.getPosition() == canvasSize - 1) {
                roadIterator.remove();
            }
        }

        while (treeIterator.hasNext()) {
            Position tree = treeIterator.next();
            if (tree.getY() == canvasSize - 1) {
                treeIterator.remove();
            }
        }

        while (carIterator.hasNext()) {
            Car car = carIterator.next();
            if (car.getPosition().getY() == canvasSize - 1) {
                carIterator.remove();
            }
        }

    }

    public HashSet<Position> nextValidPosForChicken() {
        HashSet<Position> nextPos = new HashSet<>();
        Position stepRight = new Position(chicken.getPosition().getX() + 1, chicken.getPosition().getY());
        Position stepLeft = new Position(chicken.getPosition().getX() - 1, chicken.getPosition().getY());
        Position stepDown = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() + 1);
        Position stepUp = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() - 1);

        if (!listOfTrees.contains(stepRight)
                && stepRight.withinBoundary(canvasSize, canvasSize)) {
            nextPos.add(stepRight);
        }
        if (!listOfTrees.contains(stepLeft)
                && stepRight.withinBoundary(canvasSize, canvasSize)) {
            nextPos.add(stepLeft);
        }
        if (!listOfTrees.contains(stepDown)
                && stepRight.withinBoundary(canvasSize, canvasSize)) {
            nextPos.add(stepDown);
        }
        if (!listOfTrees.contains(stepUp)
                && stepRight.withinBoundary(canvasSize, canvasSize)) {
            nextPos.add(stepUp);
        }
        return nextPos;
    }

    // update score
    public void updateScore(int delta) {
        score += delta;
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
        return this.input;
    }

}
