package model;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
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
            }
            if (r.getDirection() == 1) {
                assertEquals(0, c.getPosition().getX());
                assertEquals(r.getCarSpeed(), c.getSpeed());
            }
        }
    }

    @Test
    void generateCar1DirectionTest() {
        r.setDirection(2);
        Car c = r.generateCar(15);
        assertEquals(null, c.getPosition());
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

    @Test
    void setPositionTest() {
        r.setPosition(2);
        assertEquals(2, r.getPosition());
    }

    @Test
    void setCarSpeedTest() {
        r.setCarSpeed(5);
        assertEquals(5, r.getCarSpeed());
    }

    @Test
    void toJsonTest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("direction", r.getDirection());
        jsonObject.put("carSpeed", r.getCarSpeed());
        jsonObject.put("y", r.getPosition());
        assertEquals(jsonObject.toString(), r.toJson().toString());
    }
}
