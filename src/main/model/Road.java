package model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static model.GameState.CAR_SPD_HI;
import static model.GameState.CAR_SPD_LW;

// Represents a road on the map which can spawn cars, only has y coord
// has a direction which cars travel in and their speed
public class Road {
    private int direction; // 1: R -1: L
    private int carSpeed;
    private int ypos;

    //generating car positions with their speed (void?)
    // generate?
    // if yes add a car obj to loc with current y pos and x pos determined by dir
    // else do nothing

    // EFFECTS: constructs a road object with road speed , y position and direction
    public Road(int y) {
        this.ypos = y;
        this.direction = ThreadLocalRandom.current().nextInt(0,2) * 2 - 1;
        this.carSpeed = ThreadLocalRandom.current().nextInt(CAR_SPD_LW,CAR_SPD_HI);

    }

    // EFFECTS: updates the y position of the road by dy
    // MODIFIES: this
    public void update(int dy) {
        this.ypos += dy;
    }

    // EFFECTS: returns the y position of the object
    public int getPosition() {
        return this.ypos;
    }

    public Car generateCar(int canvasSize) {
        Position spawnPoint = null;
        if (direction == -1) {
            spawnPoint = new Position(canvasSize, ypos);
        } else if (direction == 1) {
            spawnPoint = new Position(0, ypos);
        }
        Car car = new Car(carSpeed, spawnPoint, direction);
        return car;
    }

    // EFFECTS: returns the direction which cars go on the road
    public int getDirection() {
        return this.direction;
    }

    // EFFECTS: returns how fast cars go on this road
    public int getCarSpeed() {
        return this.carSpeed;
    }
    // HUGE THANK YOU TO MAZENK FOR THIS PIECE OF CODE
    // https://github.students.cs.ubc.ca/CPSC210/Snake
    // Console-Lanterna/blob/main/src/main/java/com/mazenk/snake/model/Position.java

    // EFFECTS: returns whether 2 objects have equal value
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Road other = (Road) o;
        return this.ypos == other.ypos;
    }

    // HUGE THANK YOU TO MAZENK FOR THIS PIECE OF CODE
    // https://github.students.cs.ubc.ca/CPSC210/Snake
    // Console-Lanterna/blob/main/src/main/java/com/mazenk/snake/model/Position.java
    // EFFECTS: returns object hashcode
    @Override
    public int hashCode() {
        return Objects.hash(ypos);
    }

    //REQUIRES : direction = 1 or -1
    public void setDirection(int dir) {
        this.direction = dir;
    }
}
