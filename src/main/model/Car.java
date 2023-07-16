package model;

public class Car {
    private int speed;
    private Position position;

    // EFFECTS: constructs a car object with speed, position and dir
    public Car(int spd, Position position, int dir) {
        this.speed = spd * dir;
        this.position = position;
    }

    public void move() {
        this.position.updatePosition(speed, 0);
    }

    public void updatePosition(int dx, int dy) {
        this.position.updatePosition(dx, dy);
    }

    public Position getPosition() {
        return position;
    }

    public int getSpeed() {
        return this.speed;
    }
}
