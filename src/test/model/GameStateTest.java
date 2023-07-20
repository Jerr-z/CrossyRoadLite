package model;

import static model.GameState.CAMERA_SPD;
import static model.GameState.CHICKEN_SPD;
import static org.junit.jupiter.api.Assertions.*;
import static ui.Terminal.CANVAS_SIZE;

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
        assertTrue(0<game.getListOfRoads().size()
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
        assertTrue(game.getListOfTrees().size() >= 0);
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

    @Test
    void isChickenDeadTest3() {
        assertFalse(game.isChickenDead());
    }

    @Test
    void isChickenDeadTest4() {
        // chicken has the same pos as car
        Car testCar = new Car(1, game.getChicken().getPosition(),-1);
        game.getListOfCars().add(testCar);
        assertTrue(game.isChickenDead());
    }

    @Test
    void isChickenDeadTest5() {
        // listofcar has car but chicken not dead
        Position carPos = new Position(game.getChicken().getPosition().getX() + 1,
                game.getChicken().getPosition().getY()+1);
        Car testCar = new Car(1, carPos,-1);
        game.getListOfCars().clear();
        game.getListOfCars().add(testCar);
        assertFalse(game.isChickenDead());
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
    @Test
    void moveGrassDownTest() {
        HashSet<Position> prevListOfGrass = game.getListOfGrass();
        HashSet<Integer> copyList = new HashSet<>();
        for (Position p: prevListOfGrass) {
            copyList.add(p.getY());
        }
        game.moveGrassDown();
        for (Position p: prevListOfGrass){
            int py = p.getY() - CAMERA_SPD;
            assertTrue(copyList.contains(py));
        }
    }

    // move trees down
    @Test
    void moveTreesDownTest() {
        HashSet<Position> prevListOfTrees = game.getListOfTrees();
        HashSet<Integer> copyList = new HashSet<>();
        for (Position p: prevListOfTrees) {
            copyList.add(p.getY());
        }
        game.moveTreesDown();
        for (Position p: prevListOfTrees){
            int py = p.getY() - CAMERA_SPD;
            assertTrue(copyList.contains(py));
        }
    }
    // move cars down
    @Test
    void moveCarsDownTest() {
        HashSet<Car> prevListOfCars = game.getListOfCars();
        HashSet<Integer> copyList = new HashSet<>();
        for (Car c: prevListOfCars) {
            copyList.add(c.getPosition().getY());
        }
        game.moveCarsDown();
        for (Car c: prevListOfCars) {
            int py = c.getPosition().getY() - CAMERA_SPD;
            assertTrue(copyList.contains(py));
        }
    }

    // update chicken
    @Test
    void updateChickenTest() {
        Position p = game.getChicken().getPosition();
        Position pcopy = new Position(p.getX(),p.getY());
        game.tick("none");
        pcopy.updatePosition(0, CAMERA_SPD);
        assertTrue(pcopy.equals(p));
    }

    @Test
    void updateChickenTest2() {
        Position p = game.getChicken().getPosition();
        Position pcopy = new Position(p.getX(),p.getY());
        game.tick("right");
        pcopy.updatePosition(CHICKEN_SPD, CAMERA_SPD);
        assertTrue(pcopy.equals(p));
    }

    @Test
    void updateChickenTest3() {
        Position p = game.getChicken().getPosition();
        Position pcopy = new Position(p.getX(),p.getY());
        game.tick("left");
        pcopy.updatePosition(-CHICKEN_SPD, CAMERA_SPD);
        assertTrue(pcopy.equals(p));
    }

    @Test
    void updateChickenTest4() {
        Position p = game.getChicken().getPosition();
        int prevScore = game.getScore();
        Position pcopy = new Position(p.getX(),p.getY());
        game.tick("down");
        assertEquals(prevScore-1, game.getScore());
        pcopy.updatePosition(0, CAMERA_SPD+CHICKEN_SPD);
        assertTrue(pcopy.equals(p));
    }

    @Test
    void updateChickenTest5() {
        Position p = game.getChicken().getPosition();
        int prevScore = game.getScore();
        Position pcopy = new Position(p.getX(),p.getY());
        game.tick("up");
        pcopy.updatePosition(0, CAMERA_SPD-CHICKEN_SPD);
        assertTrue(pcopy.equals(p));
        assertEquals(prevScore + 1,game.getScore());
    }

    @Test
    void updateChickenTest6() {
        Position p = game.getChicken().getPosition();
        Position pcopy = new Position(p.getX(),p.getY());
        game.setInput("down");
        Chicken chicken = game.getChicken();
        Position stepDown = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() + CHICKEN_SPD);
        HashSet<Position> nextPos = game.getNextStep();
        nextPos.add(stepDown);
        game.updateChicken();
        pcopy.updatePosition(0, CHICKEN_SPD);
        assertTrue(pcopy.equals(p));
    }

    @Test
    void updateChickenTest9() {
        Position p = game.getChicken().getPosition();
        Position pcopy = new Position(p.getX(),p.getY());
        game.setInput("down");
        Chicken chicken = game.getChicken();
        Position stepDown = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() + CHICKEN_SPD);
        HashSet<Position> nextPos = game.getNextStep();
        Iterator<Position> nextPosIter = nextPos.iterator();
        while (nextPosIter.hasNext()) {
            Position pos = nextPosIter.next();
            if (pos.equals(stepDown)) {
                nextPosIter.remove();
            }
        }
        // nextPos.add(stepDown);
        game.updateChicken();
        //pcopy.updatePosition(0, CHICKEN_SPD);
        assertTrue(pcopy.equals(p));
    }

    @Test
    void updateChickenTest12() {
        Position p = game.getChicken().getPosition();
        Position pcopy = new Position(p.getX(),p.getY());
        game.setInput("up");
        Chicken chicken = game.getChicken();
        Position stepUp = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() - CHICKEN_SPD);
        HashSet<Position> nextPos = game.getNextStep();
        Iterator<Position> nextPosIter = nextPos.iterator();
        while (nextPosIter.hasNext()) {
            Position pos = nextPosIter.next();
            if (pos.equals(stepUp)) {
                nextPosIter.remove();
            }
        }
        // nextPos.add(stepDown);
        game.updateChicken();
        //pcopy.updatePosition(0, CHICKEN_SPD);
        assertTrue(pcopy.equals(p));
    }

    @Test
    void updateChickenTest13() {
        Position p = game.getChicken().getPosition();
        Position pcopy = new Position(p.getX(),p.getY());
        game.setInput("up");
        Chicken chicken = game.getChicken();
        Position stepUp = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() - CHICKEN_SPD);
        HashSet<Position> nextPos = game.getNextStep();
        nextPos.add(stepUp);
        game.updateChicken();
        pcopy.updatePosition(0, -CHICKEN_SPD);
        assertTrue(pcopy.equals(p));
    }

    @Test
    void updateChickenTest10() {
        Position p = game.getChicken().getPosition();
        Position pcopy = new Position(p.getX(),p.getY());
        game.setInput("left");
        Chicken chicken = game.getChicken();
        Position stepLeft = new Position(chicken.getPosition().getX() - CHICKEN_SPD, chicken.getPosition().getY());
        HashSet<Position> nextPos = game.getNextStep();
        Iterator<Position> nextPosIter = nextPos.iterator();
        while (nextPosIter.hasNext()) {
            Position pos = nextPosIter.next();
            if (pos.equals(stepLeft)) {
                nextPosIter.remove();
            }
        }
        // nextPos.add(stepDown);
        game.updateChicken();
        //pcopy.updatePosition(0, CHICKEN_SPD);
        assertTrue(pcopy.equals(p));
    }

    @Test
    void updateChickenTest11() {
        Position p = game.getChicken().getPosition();
        Position pcopy = new Position(p.getX(),p.getY());
        game.setInput("right");
        Chicken chicken = game.getChicken();
        Position stepRight = new Position(chicken.getPosition().getX() + CHICKEN_SPD, chicken.getPosition().getY());
        HashSet<Position> nextPos = game.getNextStep();
        Iterator<Position> nextPosIter = nextPos.iterator();
        while (nextPosIter.hasNext()) {
            Position pos = nextPosIter.next();
            if (pos.equals(stepRight)) {
                nextPosIter.remove();
            }
        }
        // nextPos.add(stepDown);
        game.updateChicken();
        //pcopy.updatePosition(0, CHICKEN_SPD);
        assertTrue(pcopy.equals(p));
    }

    @Test
    void updateChickenTest7() {
        Position p = game.getChicken().getPosition();
        Position pcopy = new Position(p.getX(),p.getY());
        game.setInput("left");
        Chicken chicken = game.getChicken();
        Position stepLeft = new Position(chicken.getPosition().getX() - CHICKEN_SPD, chicken.getPosition().getY());
        HashSet<Position> nextPos = game.getNextStep();
        nextPos.add(stepLeft);
        game.updateChicken();
        pcopy.updatePosition(-CHICKEN_SPD, 0);
        assertTrue(pcopy.equals(p));
    }

    @Test
    void updateChickenTest8() {
        Position p = game.getChicken().getPosition();
        Position pcopy = new Position(p.getX(),p.getY());
        game.setInput("right");
        Chicken chicken = game.getChicken();
        Position stepRight = new Position(chicken.getPosition().getX() + CHICKEN_SPD, chicken.getPosition().getY());
        HashSet<Position> nextPos = game.getNextStep();
        nextPos.add(stepRight);
        game.updateChicken();
        pcopy.updatePosition(CHICKEN_SPD, 0);
        assertTrue(pcopy.equals(p));
    }

    // update cars
    @Test
    void updateCarsTest() {
        game.updateCars();
        assertTrue(game.getListOfCars().size()<= game.getCanvasSize()*game.getCanvasSize());
    }

    // remove car out of bounds
    @Test
    void removeCarsOutOfBoundsTest() {
        HashSet<Car> almostGone = new HashSet<>();
        for (Car c: game.getListOfCars()) {
            if ((c.getSpeed() > 0
                    && c.getPosition().getX() + c.getSpeed()
                    >= game.getCanvasSize())
                    || c.getSpeed() < 0
            && c.getPosition().getX() + c.getSpeed() <= 0) {
                almostGone.add(c);
            }
        }
        game.updateCars();
        for (Car c: almostGone) {
            assertFalse(game.getListOfCars().contains(c));
        }
    }

    @Test
    void removeCarsOutOfBoundsTest2() {
        game.getListOfCars().clear();
        game.getListOfCars().add(new Car(1, new Position(17,17), -1));
        game.removeCarsOutOfBounds();
        assertEquals(0, game.getListOfCars().size());
    }

    // remove bottom terrain
    @Test
    void removeBottomTerrainTest() {
        for (int i = 0; i <= game.getCanvasSize()+1; i++) {
            game.updateGameCamera();
        }
        assertEquals(0, game.getListOfCars().size());
        assertEquals(0, game.getListOfTrees().size());
        assertEquals(0, game.getListOfGrass().size());
        assertEquals(0, game.getListOfRoads().size());
    }

    // next valid pos for chicken
    @Test
    void nextValidPosForChickenTest() {
        for (Position p: game.nextValidPosForChicken()) {
            assertTrue(p.withinBoundary(CANVAS_SIZE - 1, CANVAS_SIZE - 1));
        }
    }

    @Test
    void nextValidPosForChickenTest2() {
        Chicken chicken = game.getChicken();
        Position stepRight = new Position(chicken.getPosition().getX() + CHICKEN_SPD, chicken.getPosition().getY());
        Position stepLeft = new Position(chicken.getPosition().getX() - CHICKEN_SPD, chicken.getPosition().getY());
        Position stepDown = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() + CHICKEN_SPD);
        Position stepUp = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() - CHICKEN_SPD);
        HashSet<Position> lot = game.getListOfTrees();
        Iterator<Position> treeIter = lot.iterator();

        while (treeIter.hasNext()) {
            Position tree = treeIter.next();
            if (stepUp.equals(tree) || stepDown.equals(tree) || stepLeft.equals(tree) || stepRight.equals(tree)) {
                treeIter.remove();
            }
        }
        assertTrue(game.nextValidPosForChicken().contains(stepUp));
        assertTrue(game.nextValidPosForChicken().contains(stepDown));
        assertTrue(game.nextValidPosForChicken().contains(stepLeft));
        assertTrue(game.nextValidPosForChicken().contains(stepRight));
    }

    @Test
    void nextValidPosForChickenTest3() {
        Chicken chicken = game.getChicken();
        Position stepRight = new Position(chicken.getPosition().getX() + CHICKEN_SPD, chicken.getPosition().getY());
        Position stepLeft = new Position(chicken.getPosition().getX() - CHICKEN_SPD, chicken.getPosition().getY());
        Position stepDown = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() + CHICKEN_SPD);
        Position stepUp = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() - CHICKEN_SPD);
        HashSet<Position> lot = game.getListOfTrees();
        Iterator<Position> treeIter = lot.iterator();

        while (treeIter.hasNext()) {
            Position tree = treeIter.next();
            if (stepUp.equals(tree)) {
                treeIter.remove();
            }
        }
        assertTrue(game.nextValidPosForChicken().contains(stepUp));
    }

    @Test
    void nextValidPosForChickenTest4() {
        Chicken chicken = game.getChicken();
        Position stepRight = new Position(chicken.getPosition().getX() + CHICKEN_SPD, chicken.getPosition().getY());
        Position stepLeft = new Position(chicken.getPosition().getX() - CHICKEN_SPD, chicken.getPosition().getY());
        Position stepDown = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() + CHICKEN_SPD);
        Position stepUp = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() - CHICKEN_SPD);
        HashSet<Position> lot = game.getListOfTrees();
        Iterator<Position> treeIter = lot.iterator();

        while (treeIter.hasNext()) {
            Position tree = treeIter.next();
            if (stepDown.equals(tree)) {
                treeIter.remove();
            }
        }

        assertTrue(game.nextValidPosForChicken().contains(stepDown));
    }

    @Test
    void nextValidPosForChickenTest5() {
        Chicken chicken = game.getChicken();
        Position stepRight = new Position(chicken.getPosition().getX() + CHICKEN_SPD, chicken.getPosition().getY());
        Position stepLeft = new Position(chicken.getPosition().getX() - CHICKEN_SPD, chicken.getPosition().getY());
        Position stepDown = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() + CHICKEN_SPD);
        Position stepUp = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() - CHICKEN_SPD);
        HashSet<Position> lot = game.getListOfTrees();
        Iterator<Position> treeIter = lot.iterator();

        while (treeIter.hasNext()) {
            Position tree = treeIter.next();
            if (stepLeft.equals(tree)) {
                treeIter.remove();
            }
        }
        assertTrue(game.nextValidPosForChicken().contains(stepLeft));
    }

    @Test
    void nextValidPosForChickenTest6() {
        Chicken chicken = game.getChicken();
        Position stepRight = new Position(chicken.getPosition().getX() + CHICKEN_SPD, chicken.getPosition().getY());
        Position stepLeft = new Position(chicken.getPosition().getX() - CHICKEN_SPD, chicken.getPosition().getY());
        Position stepDown = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() + CHICKEN_SPD);
        Position stepUp = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() - CHICKEN_SPD);
        HashSet<Position> lot = game.getListOfTrees();
        Iterator<Position> treeIter = lot.iterator();

        while (treeIter.hasNext()) {
            Position tree = treeIter.next();
            if (stepRight.equals(tree)) {
                treeIter.remove();
            }
        }
        assertTrue(game.nextValidPosForChicken().contains(stepRight));
    }

    @Test
    void nextValidPosForChickenTest7() {
        Chicken chicken = game.getChicken();
        Position stepRight = new Position(chicken.getPosition().getX() + CHICKEN_SPD, chicken.getPosition().getY());
        Position stepLeft = new Position(chicken.getPosition().getX() - CHICKEN_SPD, chicken.getPosition().getY());
        Position stepDown = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() + CHICKEN_SPD);
        Position stepUp = new Position(chicken.getPosition().getX(), chicken.getPosition().getY() - CHICKEN_SPD);
        HashSet<Position> lot = game.getListOfTrees();
        lot.add(stepRight);
        lot.add(stepLeft);
        lot.add(stepUp);
        lot.add(stepDown);
        assertFalse(game.nextValidPosForChicken().contains(stepRight));
        assertFalse(game.nextValidPosForChicken().contains(stepLeft));
        assertFalse(game.nextValidPosForChicken().contains(stepUp));
        assertFalse(game.nextValidPosForChicken().contains(stepDown));
    }
    // update score
    @Test
    void updateScoreTest() {
        int prevScore = game.getScore();
        game.updateScore(0);
        assertEquals(prevScore, game.getScore());
        game.updateScore(1);
        assertEquals(prevScore + 1, game.getScore());
        game.updateScore(-1);
        assertEquals(prevScore, game.getScore());
    }
}
