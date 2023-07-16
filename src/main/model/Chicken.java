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

    public void updatePos(int dx, int dy) {
        position.updatePosition(dx, dy);
    }

    public boolean collidedWith(Position pos) {
        return (this.position.getX() == pos.getX())
                && (this.position.getY() == pos.getY());
    }



    public Position getPosition() {
        return position;
    }
}
