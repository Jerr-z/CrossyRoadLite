package persistence;

import model.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.stream.Stream;
import org.json.*;

import java.io.IOException;

import static java.lang.Math.abs;
import static ui.Terminal.CANVAS_SIZE;

// represents a reader that reads game state from json data stored in file
// Base code provided by Prof. Paul Carter
// Source: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo/blob/master/src/main/persistence/JsonReader.java
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: gets source file string
    public String getSource() {
        return source;
    }

    // EFFECTS: reads workroom from file and returns it;
    // throws IOException if an error occurs reading data from file
    public GameState read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseGameState(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    public String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses GameState from JSON object and returns it
    public GameState parseGameState(JSONObject jsonObject) {
        GameState game = new GameState(CANVAS_SIZE);
        game.clear();
        // add everything to game
        // add chicken
        addChicken(game, jsonObject);
        // add list of grass
        addListOfPosition(jsonObject, "listOfGrass", game.getListOfGrass());
        // add list of trees
        addListOfPosition(jsonObject, "listOfTrees", game.getListOfTrees());
        // add list of roads
        addListOfRoads(game, jsonObject);
        // add list of cars
        addListOfCars(game, jsonObject);
        // add score
        addScore(game, jsonObject);

        return game;
    }

    // MODIFIES: game
    // EFFECTS: parses chicken from JSON object and adds it to GameState
    public void addChicken(GameState game, JSONObject jsonObject) {
        JSONObject chickenInfo = jsonObject.getJSONObject("chicken");
        Position chickenPos = new Position(chickenInfo.getInt("x"), chickenInfo.getInt("y"));
        Chicken chicken = new Chicken(chickenPos);
        game.setChicken(chicken);
    }

    // MODIFIES: game
    // EFFECTS: parses position from JSON object and adds it to hashset
    public void addPositionToHashSet(JSONObject jsonObject, HashSet<Position> list) {
        // this is for grass and trees
        int x = jsonObject.getInt("x");
        int y = jsonObject.getInt("y");
        Position newPos = new Position(x,y);
        list.add(newPos);
    }

    // for both grass and trees

    // MODIFIES: game
    // EFFECTS: parses list of position from JSON object and adds it to a list
    public void addListOfPosition(JSONObject jsonObject, String key, HashSet<Position> list) {
        JSONArray jsonArray = jsonObject.getJSONArray(key);
        for (Object json: jsonArray) {
            JSONObject nextPos = (JSONObject) json;
            addPositionToHashSet(nextPos, list);
        }
    }


    // MODIFIES: game
    // EFFECTS: parses listOfCars from JSON object and adds it to GameState
    public void addListOfCars(GameState game, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("listOfCars");
        for (Object json: jsonArray) {
            JSONObject nextCar = (JSONObject) json;
            addCars(game, nextCar);
        }
    }

    // MODIFIES: game
    // EFFECTS: parses Car from JSON object and adds it to GameState
    public void addCars(GameState game, JSONObject jsonObject) {
        int speed = jsonObject.getInt("speed");
        int dir = speed / abs(speed);
        int x = jsonObject.getInt("x");
        int y = jsonObject.getInt("y");
        Car car = new Car(speed, new Position(x,y), dir);
        HashSet<Car> loc = game.getListOfCars();
        loc.add(car);
    }

    // MODIFIES: game
    // EFFECTS: parses listOfRoads from JSON object and adds it to GameState
    public void addListOfRoads(GameState game, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("listOfRoads");
        for (Object json: jsonArray) {
            JSONObject nextRoad = (JSONObject) json;
            addRoad(game, nextRoad);
        }
    }

    // MODIFIES: game
    // EFFECTS: parses listOfTrees from JSON object and adds it to GameState
    public void addRoad(GameState game, JSONObject jsonObject) {
        int direction = jsonObject.getInt("direction");
        int carSpeed = jsonObject.getInt("carSpeed");
        int y = jsonObject.getInt("y");
        Road r = new Road(y);
        r.setDirection(direction);
        r.setCarSpeed(carSpeed);
        HashSet<Road> lor = game.getListOfRoads();
        lor.add(r);
    }

    // EFFECTS: adds score into the game state
    public void addScore(GameState game, JSONObject jsonObject) {
        int score = jsonObject.getInt("score");
        game.updateScore(score);
    }


}
