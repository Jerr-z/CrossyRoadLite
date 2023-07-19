package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.Objects;

public class PositionTest {
    Position position;
    @BeforeEach
    void setup() {
        position = new Position(5,5);
    }
    // constructor
    @Test
    void constructorTest() {
        Position p1 = new Position(1,2);
        assertEquals(1, p1.getX());
        assertEquals(2, p1.getY());
    }

    // update position
    @Test
    void updatePositionTest() {
        position.updatePosition(0,0);
        assertEquals(5, position.getX());
        assertEquals(5, position.getY());
        position.updatePosition(1,0);
        assertEquals(6, position.getX());
        assertEquals(5, position.getY());
        position.updatePosition(0,1);
        assertEquals(6, position.getX());
        assertEquals(6, position.getY());
        position.updatePosition(-1,0);
        assertEquals(5, position.getX());
        assertEquals(6, position.getY());
        position.updatePosition(0,-1);
        assertEquals(5, position.getX());
        assertEquals(5, position.getY());
    }

    // within boundary
    @Test
    void withinBoundaryTest() {
        assertTrue(position.withinBoundary(5,5));
        assertTrue(position.withinBoundary(5,6));
        assertTrue(position.withinBoundary(6,5));
        assertTrue(position.withinBoundary(6,6));
        assertFalse(position.withinBoundary(4,5));
        assertFalse(position.withinBoundary(5,4));
        assertFalse(position.withinBoundary(4,4));
        assertTrue(position.withinBoundary(1000,1000));
        assertFalse(position.withinBoundary(1,1));
        position = new Position(0,0);
        assertTrue(position.withinBoundary(0,0));
    }

    // equals
    @Test
    void equalsTest() {
        assertTrue(position.equals(new Position(5,5)));
        assertTrue(position.equals(position));
        assertFalse(position.equals(null));
        assertFalse(position.equals(3));
    }

    // hashcode
    @Test
    void hashCodeTest() {
        assertEquals(Objects.hash(5,5),position.hashCode());
        position = new Position(7,8);
        assertEquals(Objects.hash(7,8),position.hashCode());
    }
}
