package persistence;

import model.GameState;
import model.Position;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static ui.Terminal.CANVAS_SIZE;
import static ui.Terminal.JSON_STORE;

public class JsonReaderTest {
    private JsonReader reader;
    private String source;

    @BeforeEach
    void setup() {
        reader = new JsonReader("./data/jsonreadertest.json");
        source = "./data/jsonreadertest.json";
    }

    @Test
    void constructorTest() {
        assertEquals("./data/jsonreadertest.json", reader.getSource());
    }

    @Test
    void readFileTest() throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(reader.getSource()), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        assertEquals(contentBuilder.toString(),reader.readFile(reader.getSource()));
    }

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            GameState game = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyGameWithChickenAt11() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyGame.json");
        try {
            GameState game = reader.read();
            assertEquals(0, game.getListOfGrass().size());
            assertEquals(0, game.getListOfRoads().size());
            assertEquals(0, game.getListOfTrees().size());
            assertEquals(0, game.getListOfCars().size());
            assertEquals(new Position(1,1), game.getChicken().getPosition());
            assertEquals(0, game.getScore());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralGameState() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralGame.json");
        try {
            GameState game = reader.read();
            assertEquals(1, game.getListOfGrass().size());
            assertEquals(1, game.getListOfRoads().size());
            assertEquals(1, game.getListOfTrees().size());
            assertEquals(1, game.getListOfCars().size());
            assertEquals(new Position(10,18), game.getChicken().getPosition());
            assertEquals(2, game.getScore());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

}
