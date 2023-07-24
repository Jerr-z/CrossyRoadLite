package persistence;

import model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static ui.Terminal.CANVAS_SIZE;

public class JsonWriterTest {

    @Test
    void testWriterInvalidFile() {
        try {
            GameState game = new GameState(CANVAS_SIZE);
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyGame() {
        try {
            GameState game = new GameState(CANVAS_SIZE);
            game.clear();
            game.setChicken(new Chicken(new Position(1,1)));
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyGame.json");
            writer.open();
            writer.write(game);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyGame.json");
            game = reader.read();
            assertEquals(0, game.getListOfCars().size());
            assertEquals(0, game.getListOfRoads().size());
            assertEquals(0, game.getListOfTrees().size());
            assertEquals(0, game.getListOfGrass().size());
            assertEquals(new Position(1,1), game.getChicken().getPosition());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralGame() {
        try {
            GameState game = new GameState(CANVAS_SIZE);
            game.clear();
            Chicken c = new Chicken(new Position(1,1));
            game.setChicken(c);
            game.getListOfCars().add(new Car(1,new Position(2,2),1));
            game.getListOfRoads().add(new Road(1));
            game.getListOfGrass().add(new Position(3,3));
            game.getListOfTrees().add(new Position(5,5));

            JsonWriter writer = new JsonWriter("./data/testWriterGeneralGame.json");
            writer.open();
            writer.write(game);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralGame.json");
            game = reader.read();
            assertEquals(c.getPosition(), game.getChicken().getPosition());
            assertEquals(1, game.getListOfGrass().size());
            assertEquals(1, game.getListOfRoads().size());
            assertEquals(1,game.getListOfTrees().size());
            assertEquals(1, game.getListOfCars().size());

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

}
