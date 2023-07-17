package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.HashSet;

public class GameStateTest {
    private HashSet<Position> listOfTrees = new HashSet<>();
    private HashSet<Position> listOfGrass = new HashSet<>();
    private HashSet<Road> listOfRoads = new HashSet<>();
    private HashSet<Car> listOfCars = new HashSet<>();
    private int score;
    private Chicken chicken;
    private int canvasSize;
    private String input;
    private GameState game;
    @BeforeEach
    void setup() {
        game = new GameState(15);
    }

    // constructor test
    @Test
    void constructorTest() {
        assertEquals(0, game.getScore());
        assertEquals("none", game.getInput());
        assertFalse(game.getChicken() == null);
        assertTrue(game.getListOfGrass().size() > 0);
        assertEquals(15, game.getCanvasSize());
    }

    @Test
    void constructorTest2() {
        game = new GameState(10);
        assertEquals(0, game.getScore());
        assertEquals("none", game.getInput());
        assertFalse(game.getChicken() == null);
        assertTrue(game.getListOfGrass().size() > 0);
        assertEquals(10, game.getCanvasSize());
    }
    // initialize level
    // place chicken

    // tick


    // generate terrain
    // generate grass
    // generate road
    // generate tree
    // generate cars
    // is chicken dead?
    // update game camera
    // move road down
    // move grass down
    // move trees down
    // move cars down
    // update chicken
    // update cars
    // remove car out of bounds
    // remove bottom terrain
    // next valid pos for chicken
    // update score
}
