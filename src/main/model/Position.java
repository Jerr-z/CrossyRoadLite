package model;

import java.util.*;

// represents a position in 2d space
public class Position {
    private int xpos;
    private int ypos;

    // EFFECTS: creates object that represents a position in game
    // REQUIRES: x, y >=0
    public Position(int x, int y) {
        this.xpos = x;
        this.ypos = y;
    }

    // EFFECTS: gets the x position of the object
    public int getX() {
        return this.xpos;
    }

    // EFFECTS: gets the y position of the object
    public int getY() {
        return this.ypos;
    }

    // MODIFIES: this
    // REQUIRES: x>=0
    // EFFECTS: changes the x value
    public void setX(int x) {
        this.xpos = x;
    }

    // MODIFIES: this
    // REQUIRES: y>=0
    // EFFECTS: changes the y value
    public void setY(int y) {
        this.ypos = y;
    }

    // MODIFIES: this
    // EFFECTS: changes the current position by shifting dx and dy units.
    public void updatePosition(int dx, int dy) {
        this.xpos += dx;
        this.ypos += dy;
    }

    // EFFECTS: checks if current position is within the specified boundary
    public boolean withinBoundary(int x, int y) {
        // inclusive
        return xpos >= 0 && ypos >= 0 && xpos <= x && ypos <= y;
    }

    // HUGE THANK YOU TO MAZENK FOR THIS PIECE OF CODE
    // https://github.students.cs.ubc.ca/CPSC210/Snake
    // Console-Lanterna/blob/main/src/main/java/com/mazenk/snake/model/Position.java

    // EFFECTS: overrides the equals method for object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Position other = (Position) o;
        return xpos == other.xpos && ypos == other.ypos;
    }

    // HUGE THANK YOU TO MAZENK FOR THIS PIECE OF CODE
    // https://github.students.cs.ubc.ca/CPSC210/Sn
    // akeConsole-Lanterna/blob/main/src/main/java/com/mazenk/snake/model/Position.java

    // EFFECTS: overrides the hashCode method for object comparison
    @Override
    public int hashCode() {
        return Objects.hash(xpos, ypos);
    }
}
