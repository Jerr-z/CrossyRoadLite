package model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Chicken {
    private Position position;


    // EFFECTS: constructs a chicken
    // REQUIRES: position to be within the frame
    public Chicken(Position position) {
        this.position = position;
    }

    // MODIFIES: this
    // EFFECTS: shifts chicken's position by dx and dy units
    public void updatePos(int dx, int dy) {
        position.updatePosition(dx, dy);
    }

    public Position getPosition() {
        return position;
    }

    // EFFECTS: converts current object to json format
    public JSONObject toJson() {
        return position.toJson();
    }
}
