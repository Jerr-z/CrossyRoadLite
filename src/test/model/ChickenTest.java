package model;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class ChickenTest {
    private Chicken chicken;
    private Position chickenPos;

    @BeforeEach
    void setup() {
        chickenPos = new Position(1,1);
        chicken = new Chicken(chickenPos);
    }

    @Test
    void constructorTest() {
        chickenPos = new Position(1,2);
        chicken = new Chicken(chickenPos);
        assertEquals(new Position(1,2), chicken.getPosition());
        chickenPos = new Position(3,4);
        chicken = new Chicken(chickenPos);
        assertEquals(new Position(3,4), chicken.getPosition());
    }

    @Test
    void updatePositionTest() {
        // up
        chicken.updatePos(0,-1);
        assertEquals(new Position(1,0), chicken.getPosition());
    }

    @Test
    void updatePositionTest2() {
        // down
        chicken.updatePos(0,1);
        assertEquals(new Position(1,2), chicken.getPosition());
    }

    @Test
    void updatePositionTest3() {
        // left
        chicken.updatePos(-1,0);
        assertEquals(new Position(0,1), chicken.getPosition());
    }

    @Test
    void updatePositionTest4() {
        // right
        chicken.updatePos(1,0);
        assertEquals(new Position(2,1), chicken.getPosition());
    }

    @Test
    void updatePositionTest5() {
        // left
        chicken.updatePos(2,3);
        assertEquals(new Position(3,4), chicken.getPosition());
    }

    @Test
    void updatePositionTest6() {
        // neutral
        chicken.updatePos(0,0);
        assertEquals(new Position(1,1), chicken.getPosition());
    }
}