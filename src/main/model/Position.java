package model;

import org.json.JSONObject;

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
    // EFFECTS: changes the current position by shifting dx and dy units.
    public void updatePosition(int dx, int dy) {
        this.xpos += dx;
        this.ypos += dy;
    }

    // REQUIRES: x, y >= 0
    // EFFECTS: checks if current position is within the specified boundary
    public boolean withinBoundary(int x, int y) {
        // inclusive
        return xpos <= x && ypos <= y;
    }


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


    // EFFECTS: overrides the hashCode method for object comparison
    @Override
    public int hashCode() {
        return Objects.hash(xpos, ypos);
    }

    // EFFECTS: parses current object to json format
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("x", xpos);
        jsonObject.put("y", ypos);
        return  jsonObject;
    }
}
