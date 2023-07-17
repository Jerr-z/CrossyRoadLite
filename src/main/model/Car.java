package model;

// Represents the car that can crush the chicken
public class Car {
    private int speed;
    private Position position;

    // EFFECTS: constructs a car object with speed, position and dir
    // REQUIRES: dir must be -1 or 1, speed > 0
    public Car(int spd, Position position, int dir) {
        this.speed = spd * dir;
        this.position = position;
    }

    // MODIFIES: this.position
    // EFFECTS: shifts car position to the left or right by dy units
    public void move() {
        this.position.updatePosition(speed, 0);
    }

    // MODIFIES: this.position
    // EFFECTS: shifts car position by dx dy units
    public void updatePosition(int dx, int dy) {
        this.position.updatePosition(dx, dy);
    }

    // EFFECTS: obtains the position of the car
    public Position getPosition() {
        return position;
    }

    // EFFECTS: obtains the speed of the car
    public int getSpeed() {
        return this.speed;
    }

}
