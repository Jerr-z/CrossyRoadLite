package model;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.*;

public class CarTest {
    private Position carPos;
    private int speed;
    private int dir;
    private Car car;

    @BeforeEach
    void setup() {
        carPos = new Position(1,1);
        speed = 1;
        dir = 1;
        car = new Car(speed, carPos, dir);
    }

    @Test
    void constructorTest() {
        // dir = 1
        // dir = -1
        car = new Car(speed, carPos, dir);
        assertEquals(1, car.getSpeed());
        assertEquals(new Position(1,1), car.getPosition());
        dir = -1;
        car = new Car(speed, carPos, dir);
        assertEquals(-1, car.getSpeed());
        assertEquals(new Position(1,1), car.getPosition());

        // change speed to see multiplying behavior
        speed = 2;
        dir = 1;
        car = new Car(speed, carPos, dir);
        assertEquals(2, car.getSpeed());
        assertEquals(new Position(1,1), car.getPosition());
        dir = -1;
        car = new Car(speed, carPos, dir);
        assertEquals(-2, car.getSpeed());
        assertEquals(new Position(1,1), car.getPosition());
    }

    @Test
    void moveTest() {
        // initial test
        car.move();
        assertEquals(new Position(2,1), car.getPosition());
        // can it move twice?
        car.move();
        assertEquals(new Position(3,1), car.getPosition());
        // what about opposite direction
        car = new Car(1, new Position(5,1), -1);
        car.move();
        assertEquals(new Position(4,1), car.getPosition());
        car.move();
        assertEquals(new Position(3,1), car.getPosition());
    }

    @Test
    void moveTest2() {
        // what about different speed
        car = new Car(2,new Position(1,1),1);
        car.move();
        assertEquals(new Position(3,1), car.getPosition());
    }

    @Test
    void updatePositionTest() {
        // up
        car.updatePosition(0,-1);
        assertEquals(new Position(1,0), car.getPosition());
    }

    @Test
    void updatePositionTest2() {
        // down
        car.updatePosition(0,1);
        assertEquals(new Position(1,2), car.getPosition());
    }

    @Test
    void updatePositionTest3() {
        // left
        car.updatePosition(-1,0);
        assertEquals(new Position(0,1), car.getPosition());
    }

    @Test
    void updatePositionTest4() {
        // right
        car.updatePosition(1,0);
        assertEquals(new Position(2,1), car.getPosition());
    }

    @Test
    void updatePositionTest5() {
        // left
        car.updatePosition(2,3);
        assertEquals(new Position(3,4), car.getPosition());
    }

    @Test
    void updatePositionTest6() {
        // neutral
        car.updatePosition(0,0);
        assertEquals(new Position(1,1), car.getPosition());
    }

    @Test
    void toJsonTest() {
        JSONObject jsonObject = car.getPosition().toJson();
        jsonObject.put("speed", car.getSpeed());
        assertEquals(jsonObject.toString(), car.toJson().toString());

    }


}
