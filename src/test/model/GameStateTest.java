package model;

import static model.GameState.CAMERA_SPD;
import static model.GameState.CHICKEN_SPD;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.Iterator;

public class GameStateTest {
    GameState game;

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
    @Test
    void initializeLevelTest() {
        game.initializeLevel();
        assertTrue(0<=game.getListOfRoads().size()
                && game.getListOfRoads().size()<=15);
        assertTrue(3*15<=game.getListOfGrass().size()
                && game.getListOfGrass().size()<=15*15);
    }

    // place chicken
    @Test
    void placeChickenTest() {
        assertTrue(game.getChicken() != null);
        assertTrue(game.getChicken().getPosition().getX() == 15/2);
        assertEquals(11, game.getChicken().getPosition().getY());

        game = new GameState(10);
        assertTrue(game.getChicken() != null);
        assertTrue(game.getChicken().getPosition().getX() == 10/2);
        assertEquals(10-4, game.getChicken().getPosition().getY());
    }

    // tick
    @Test
    void tickTest() {
        for (int i = 0; i <50; i++) {
            game.tick("none");
        }
        // make sure we get some trees and some cars in frame
        assertTrue(game.getListOfCars().size() > 0);
        assertTrue(game.getListOfTrees().size() > 0);
        assertEquals(11+50, game.getChicken().getPosition().getY());
        // check size
        assertTrue(0<=game.getListOfRoads().size()
                && game.getListOfRoads().size()<=15);
        //System.out.println(game.getListOfGrass().size());
        assertTrue(0<=game.getListOfGrass().size()
                && game.getListOfGrass().size()<=15*15);
        assertEquals("none", game.getInput());
    }

    @Test
    void tickTest2() {
        for (int i = 0; i <50; i++) {
            game.tick("up");
        }
        // make sure we get some trees and some cars in frame
        assertTrue(game.getListOfCars().size() > 0);
        assertTrue(game.getListOfTrees().size() > 0);
        assertEquals(11 - 50 * CAMERA_SPD + 50 * CHICKEN_SPD, game.getChicken().getPosition().getY());
        // check size
        assertTrue(0<=game.getListOfRoads().size()
                && game.getListOfRoads().size()<=15);
        assertTrue(0<=game.getListOfGrass().size()
                && game.getListOfGrass().size()<=15*15);
        assertEquals("up", game.getInput());
    }

    @Test
    void tickTest3() {
        int chickenX = game.getChicken().getPosition().getX();
        game.tick("left");
        assertEquals("left", game.getInput());
        game.tick("right");
        assertEquals("right", game.getInput());
        assertEquals(chickenX, game.getChicken().getPosition().getX());
    }

    // generate terrain
    @Test
    void generateTerrainTest() {
        int prevTreeSize = game.getListOfTrees().size();
        for (int i = 0; i <= 50; i++) {
            game.generateTerrain(1);
        }
        Road dummy = new Road(1);
        assertTrue(game.getListOfRoads().contains(dummy));
        int currTreeSize = game.getListOfTrees().size();
        assertTrue(currTreeSize > prevTreeSize);
    }

    // generate grass
    @Test
    void generateGrassTest() {
        for (int i = 0; i < 200; i++) {
            game.generateGrass(1,1, true);
        }
        assertTrue(game.getListOfTrees().contains(new Position(0,1)));
        for (int i = 0; i < 200; i++) {
            game.generateGrass(14,14, false);
        }
        assertFalse(game.getListOfTrees().contains(new Position(14,14)));
    }

    @Test
    void generateGrassTest2() {
        int prevGrassSize = game.getListOfGrass().size();
        game.generateGrass(0,1, false);
        int currGrassSize = game.getListOfGrass().size();
        assertEquals(prevGrassSize, currGrassSize);
    }

    // generate road
    @Test
    void generateRoadTest() {
        game.generateRoad(0);
        Road dummy = new Road(0);
        assertTrue(game.getListOfRoads().contains(dummy));
    }

    @Test
    void generateRoadTest2() {
        game.generateRoad(14);
        Road dummy = new Road(14);
        assertTrue(game.getListOfRoads().contains(dummy));
    }

    @Test
    void generateRoadTest3() {
        game.generateRoad(5);
        Road dummy = new Road(5);
        assertTrue(game.getListOfRoads().contains(dummy));
    }

    // generate tree
    @Test
    void generateTreeTest() {
        // edge cases
        game.generateTree(0,0);
        assertTrue(game.getListOfTrees().contains(new Position(0,0)));
        game.generateTree(0,1);
        assertTrue(game.getListOfTrees().contains(new Position(0,1)));
        game.generateTree(1,0);
        assertTrue(game.getListOfTrees().contains(new Position(1,0)));
        game.generateTree(14,14);
        assertTrue(game.getListOfTrees().contains(new Position(14,14)));
        game.generateTree(0,14);
        assertTrue(game.getListOfTrees().contains(new Position(0,14)));
        game.generateTree(14,0);
        assertTrue(game.getListOfTrees().contains(new Position(14,0)));
        // in the middle
        game.generateTree(7,7);
        assertTrue(game.getListOfTrees().contains(new Position(7,7)));
    }

    // generate cars
    @Test
    void generateCarTest() {
        for (int i = 0; i<=50; i++) {
            game.generateCars();
        }
        assertTrue(game.getListOfCars().size()>0
                && game.getListOfCars().size()<=50*15);
    }

    // is chicken dead?
    @Test
    void isChickenDeadTest() {
        // hit a car
        for (int i = 0; i<=50; i++) {
            game.generateCars();
        }
        Car dummy = game.getListOfCars().iterator().next();
        int chickenX = game.getChicken().getPosition().getX();
        int chickenY = game.getChicken().getPosition().getY();
        int dummyX = dummy.getPosition().getX();
        int dummyY = dummy.getPosition().getY();
        dummy.updatePosition(chickenX-dummyX, chickenY-dummyY);
        assertTrue(game.isChickenDead());
    }

    @Test
    void isChickenDeadTest2() {
        // out of bounds
        game.updateGameCamera();
        game.updateGameCamera();
        game.updateGameCamera();
        game.updateGameCamera();
        game.updateGameCamera();
        assertTrue(game.isChickenDead());
    }

    // update game camera
    @Test
    void updateGameCameraTest() {
        // check size
        int prevTreeSize = game.getListOfTrees().size();
        int prevGrassSize = game.getListOfGrass().size();
        int prevCarSize = game.getListOfCars().size();
        int prevRoadSize = game.getListOfRoads().size();
        Position prevChickenPos = game.getChicken().getPosition();
        game.updateGameCamera();

        int treeSize = game.getListOfTrees().size();
        int grassSize = game.getListOfGrass().size();
        int carSize = game.getListOfCars().size();
        int RoadSize = game.getListOfRoads().size();
        boolean decreasedSize = (prevRoadSize > RoadSize)
                || (prevCarSize > carSize)
                || (prevGrassSize > grassSize)
                || (prevTreeSize > treeSize);
        assertTrue(decreasedSize);
        // check chicken pos
        prevChickenPos.updatePosition(0,CHICKEN_SPD);
        assertEquals(prevChickenPos, game.getChicken().getPosition());
    }

    // move road down
    @Test
    void moveRoadDownTest() {
        HashSet<Road> prevListOfRoad = game.getListOfRoads();
        HashSet<Integer> roadYs = new HashSet<>();
        for (Road r: prevListOfRoad) {
            roadYs.add(r.getPosition());
        }
        game.moveRoadDown();
        HashSet<Road> currListOfRoad = game.getListOfRoads();
        for (Road r: currListOfRoad) {
            assertTrue(roadYs.contains(r.getPosition() - CAMERA_SPD));
        }
    }
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
