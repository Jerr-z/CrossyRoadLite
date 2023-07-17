package model;

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

    // EFFECTS: detects collision with other game objects
    // REQUIRES: position to be in game bound
    public boolean collidedWith(Position pos) {
        // not super useful atm...
        return (this.position.getX() == pos.getX())
                && (this.position.getY() == pos.getY());
    }



    public Position getPosition() {
        return position;
    }
}
