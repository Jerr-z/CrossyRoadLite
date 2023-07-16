package ui;

import java.io.Console;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import model.*;


public class Terminal {
    public static final int FPS = 1;
    public static final int CANVAS_SIZE = 15;
    private GameState game;
    private Screen screen;
    private WindowBasedTextGUI endGui;

    // THANK YOU TO MAZENK's SNAKE GAME FOR REFERENCE

    public void start() throws IOException, InterruptedException {
        screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();

        TerminalSize terminalSize = screen.getTerminalSize();

        game = new GameState(15);
        startTick();
    }

    public void startTick() throws IOException, InterruptedException {
        while (!game.isChickenDead() || endGui.getActiveWindow() != null) {
            tick();
            Thread.sleep(1000L / FPS);
        }
        System.exit(0);
    }

    private void tick() throws IOException {
        game.tick(userInput());
        screen.setCursorPosition(new TerminalPosition(0,0));
        render();
        screen.refresh();
        screen.setCursorPosition(new TerminalPosition(screen.getTerminalSize().getColumns() - 1, 0));
    }


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
            default:
                return "none";
        }

    }

    private void render() {
        if (game.isChickenDead()) {
            if (endGui == null) {
                renderEndScreen();
            }
            return;
        }
        drawGrass();
        drawTrees();
        drawRoads();
        drawCars();
        drawChicken();
        drawScore();
    }

    private void renderEndScreen() {
        endGui = new MultiWindowTextGUI(screen);

        new MessageDialogBuilder()
                .setTitle("Game Over")
                .setText("You finished with a score of" + game.getScore())
                .addButton(MessageDialogButton.Close)
                .build()
                .showDialog(endGui);
    }

    private void drawScore() {
        TextGraphics text = screen.newTextGraphics();;
        text.setForegroundColor(TextColor.ANSI.WHITE);
        text.putString(1, 0, "CURRENT SCORE: ");

        text = screen.newTextGraphics();
        text.setForegroundColor(TextColor.ANSI.WHITE);
        text.putString(15, 0, Integer.toString(game.getScore()));
    }

    private void drawChicken() {
        Chicken chicken = game.getChicken();
        drawAt(chicken.getPosition(), TextColor.ANSI.BLUE, '█');
    }

    private void drawGrass() {
        HashSet<Position> listOfGrass = game.getListOfGrass();
        Iterator<Position> grassIterator = listOfGrass.iterator();
        while (grassIterator.hasNext()) {
            Position grass = grassIterator.next();
            drawAt(grass, TextColor.ANSI.GREEN, '█');
        }
    }

    private void drawTrees() {
        HashSet<Position> listOfTrees = game.getListOfTrees();
        Iterator<Position> treeIterator = listOfTrees.iterator();
        while (treeIterator.hasNext()) {
            Position tree = treeIterator.next();
            drawAt(tree, new TextColor.RGB(0,200,0), '█');
        }
    }

    private void drawCars() {
        HashSet<Car> listOfCars = game.getListOfCars();
        Iterator<Car> carIterator = listOfCars.iterator();
        while (carIterator.hasNext()) {
            Car car = carIterator.next();
            Position carPos = car.getPosition();
            drawAt(carPos, TextColor.ANSI.RED, '█');
        }
    }

    private void drawRoads() {
        HashSet<Road> listOfRoad = game.getListOfRoads();
        Iterator<Road> roadIterator = listOfRoad.iterator();
        while (roadIterator.hasNext()) {
            Road road = roadIterator.next();
            int roadPos = road.getPosition();
            for (int i = 0; i < CANVAS_SIZE; i++) {
                drawAt(new Position(i, roadPos), TextColor.ANSI.WHITE, '█');
            }
        }
    }

    private void drawAt(Position pos, TextColor color, char c) {
        TextGraphics text = screen.newTextGraphics();
        text.setForegroundColor(color);
        text.putString(pos.getX(), pos.getY(), String.valueOf(c));
    }

}
