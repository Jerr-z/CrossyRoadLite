package ui;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.WebSocket;
import java.util.HashSet;
import java.util.Iterator;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.util.*;


public class Terminal {
    public static final int FPS = 1;
    public static final int CANVAS_SIZE = 20;
    private GameState game;
    private Screen screen;
    private WindowBasedTextGUI endGui;
    private static final String JSON_STORE = "./data/gamestate.json";
    private Scanner input;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // THANK YOU TO MAZENK's SNAKE GAME FOR REFERENCE

    // MODIFIES: this
    // EFFECTS: begins the game by starting up a screen
    public void start() throws IOException, InterruptedException {
        screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        TerminalSize terminalSize = screen.getTerminalSize();
        game = new GameState(CANVAS_SIZE);

        if (isFilePresent(JSON_STORE)) {
            promptRead();
        }

        startTick();
    }

    // EFFECTS: checks if file is present
    private boolean isFilePresent(String filePathString) {
        File f = new File(filePathString);
        return f.isFile();
    }

    // EFFECTS: saves gamestate to file
    private void saveGameState() {
        try {
            jsonWriter.open();
            jsonWriter.write(game);
            jsonWriter.close();
            System.out.println("Game saved to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Writing to file failed");
        }
    }

    private void loadGameState() {
        try {
            game.clear(); // probably not needed but i would rather comment here than delete it :)
            game = jsonReader.read();
            System.out.println("Game loaded from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Read failed");
        }
    }


    // MODIFIES: this
    // EFFECTS: starts the "ticking" mechanic
    public void startTick() throws IOException, InterruptedException {
        while (!game.isChickenDead() || endGui.getActiveWindow() != null
                || !game.getInput().equals("quit")) {
            tick();
            Thread.sleep(1000L / FPS);
        }
        System.exit(0);
    }

    // MODIFIES: this
    // EFFECTS: processes everything that happens within a single frame
    private void tick() throws IOException {
        game.tick(userInput());
        //screen.setCursorPosition(new TerminalPosition(0,0));
        render();
        screen.refresh();
        screen.setCursorPosition(new TerminalPosition(screen.getTerminalSize().getColumns() - 1, 0));
    }

    // EFFECTS: returns a user input
    private String userInput() throws IOException {
        KeyStroke stroke = screen.pollInput();
        if (stroke == null) {
            return "none";
        }
        char input = stroke.getCharacter();
        //System.out.println(input);
        switch (input) {
            case 'a':
                return "left";
            case 's':
                return "down";
            case 'd':
                return "right";
            case 'w':
                return "up";
            case 'q':
                return "quit";
            default:
                return "none";
        }

    }


    // MODIFIES: this
    // EFFECTS: main render method for a single frame
    private void render() throws IOException {
        if (game.isChickenDead()) {
            if (endGui == null) {
                renderEndScreen();
            }
            System.exit(0);
            return;
        }
        if (game.getInput().equals("quit")) {
            if (endGui == null) {
                int result = promptSave();
                switch (result) {
                    case 1:
                        // save stuff
                        saveGameState();
                        System.exit(0);
                    case 0:
                        endGui = null;
                        break;
                    case -1:
                        System.exit(0);
                }
            }
        }
        drawGame();
    }

    // EFFECTS: draws game objects
    private void drawGame() {
        drawGrass();
        drawTrees();
        drawRoads();
        drawCars();
        drawChicken();
        drawScore();
    }

    // MODIFIES: this
    // EFFECTS: render a window after the game ends
    private void renderEndScreen() {
        endGui = new MultiWindowTextGUI(screen);
        new MessageDialogBuilder()
                .setTitle("Game Over")
                .setText("You finished with a score of " + game.getScore())
                .addButton(MessageDialogButton.Close)
                .build()
                .showDialog(endGui);
    }

    // MODIFIES: this
    // EFFECTS: prompts a window to ask whether user wants to save the game and quit, keep going or just quit
    // return 1 if save and quit, 0 if resumed, -1 if quit without save
    private int promptSave() {
        endGui = new MultiWindowTextGUI(screen);

        MessageDialogButton result = new MessageDialogBuilder().setTitle("Pause")
                .setText("Do you wish to save game progress before quitting?")
                .addButton(MessageDialogButton.Yes)
                .addButton(MessageDialogButton.No)
                .addButton(MessageDialogButton.Cancel)
                .build()
                .showDialog(endGui);
        switch (result) {
            case Yes:
                return 1;
            case No:
                return -1;
            case Cancel:
                return 0;
        }
        return 0;
    }

    // EFFECTS: prompts whether user wants to read existing game state from file
    private void promptRead() {
        MessageDialogButton result = new MessageDialogBuilder()
                .setTitle("Save File Detected")
                .setText("Do you wish to continue from save?")
                .addButton(MessageDialogButton.Yes)
                .addButton(MessageDialogButton.No)
                .build()
                .showDialog(new MultiWindowTextGUI(screen));

        if (result == MessageDialogButton.Yes) {
            loadGameState();
            File f = new File(JSON_STORE);
            f.delete();
        }
    }

    // MODIFIES: screen
    // EFFECTS: renders the score on screen
    private void drawScore() {
        TextGraphics text = screen.newTextGraphics();;
        text.setForegroundColor(TextColor.ANSI.WHITE);
        text.putString(1, 0, "CURRENT SCORE: ");

        text = screen.newTextGraphics();
        text.setForegroundColor(TextColor.ANSI.WHITE);
        text.putString(15, 0, Integer.toString(game.getScore()));
    }

    // MODIFIES: screen
    // EFFECTS: draws chicken on screen
    private void drawChicken() {
        Chicken chicken = game.getChicken();
        drawAt(chicken.getPosition(), TextColor.ANSI.BLUE, (char) 254);
    }

    // MODIFIES: screen
    // EFFECTS: draws grass on screen
    private void drawGrass() {
        HashSet<Position> listOfGrass = game.getListOfGrass();
        Iterator<Position> grassIterator = listOfGrass.iterator();
        while (grassIterator.hasNext()) {
            Position grass = grassIterator.next();
            drawAt(grass, TextColor.ANSI.GREEN, (char) 254);
        }
    }

    // MODIFIES: this
    // EFFECTS: draws tree on screen
    private void drawTrees() {
        HashSet<Position> listOfTrees = game.getListOfTrees();
        Iterator<Position> treeIterator = listOfTrees.iterator();
        while (treeIterator.hasNext()) {
            Position tree = treeIterator.next();
            drawAt(tree, TextColor.ANSI.CYAN, (char) 254);
        }
    }

    // MODIFIES: screen
    // EFFECTS: draw cars on screen
    private void drawCars() {
        HashSet<Car> listOfCars = game.getListOfCars();
        Iterator<Car> carIterator = listOfCars.iterator();
        while (carIterator.hasNext()) {
            Car car = carIterator.next();
            Position carPos = car.getPosition();
            drawAt(carPos, TextColor.ANSI.RED, (char) 254);
        }
    }

    // MODIFIES: screen
    // EFFECTS: draw roads on screen
    private void drawRoads() {
        HashSet<Road> listOfRoad = game.getListOfRoads();
        Iterator<Road> roadIterator = listOfRoad.iterator();
        while (roadIterator.hasNext()) {
            Road road = roadIterator.next();
            int roadPos = road.getPosition();
            for (int i = 0; i < CANVAS_SIZE; i++) {
                drawAt(new Position(i, roadPos), TextColor.ANSI.WHITE, (char) 254);
            }
        }
    }

    // MODIFIES: screen
    // EFFECTS: main drawing function that draws a character at a position on screen
    private void drawAt(Position pos, TextColor color, char c) {
        TextGraphics text = screen.newTextGraphics();
        text.setForegroundColor(color);
        text.putString(pos.getX(), pos.getY(), String.valueOf(c));
    }

}
