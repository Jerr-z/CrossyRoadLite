package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.Objects;

import static model.GameState.CAR_SPD_HI;
import static model.GameState.CAR_SPD_LW;


public class RoadTest {
    Road r;

    // setup
    @BeforeEach
    void setup() {
        r = new Road(1);
    }

    // constructor test
    @Test
    void constructorTest() {
        assertEquals(1, r.getPosition());
        assertTrue(r.getCarSpeed() >= CAR_SPD_LW && r.getCarSpeed() <= CAR_SPD_HI);
        assertTrue(r.getDirection() == -1 || r.getDirection() == 1);
    }

    // update
    @Test
    void updateTest() {
        r.update(1);
        assertEquals(2, r.getPosition());
        r.update(2);
        assertEquals(4, r.getPosition());
        r.update(-3);
        assertEquals(1, r.getPosition());
        r.update(0);
        assertEquals(1, r.getPosition());
    }

    // generate car
    @Test
    void generateCarTest() {
        for (int i = 0; i < 50; i++) {
            r = new Road(1);
            Car c = r.generateCar(15);
            if (r.getDirection() == -1) {
                assertEquals(15, c.getPosition().getX());
                assertEquals(r.getCarSpeed(), -c.getSpeed());
            } else if (r.getDirection() == 1) {
                assertEquals(0, c.getPosition().getX());
                assertEquals(r.getCarSpeed(), c.getSpeed());
            }
        }
    }

    // equals
    @Test
    void equalsTest() {
        Road otherRoad = new Road(1);
        assertTrue(r.equals(otherRoad));
        assertFalse(r.equals(null));
        assertFalse(r.equals("yoooo"));
        assertTrue(r.equals(r));
    }

    // hashcode
    @Test
    void hashCodeTest() {
        assertEquals(Objects.hash(r.getPosition()), r.hashCode());
    }
}
