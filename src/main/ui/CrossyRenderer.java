package ui;

import model.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import javax.imageio.ImageIO;

import static ui.CrossyApp.CHICKEN_IMG_PATH;
import static ui.CrossyApp.SCALE;
import static ui.Terminal.CANVAS_SIZE;

// Inspriation taken from lab 6 swing components
// Represents the renderer for crossy GUI
public class CrossyRenderer {
    private static final Color GRASS_COLOUR = new Color(0,255,0);
    private static final Color TREE_COLOUR = new Color(1,90,32);
    private static final Color ROAD_COLOUR = new Color(125,125,125);
    private static final Color CHICKEN_COLOUR = new Color(255,255,255);
    private static final Color CAR_COLOR = new Color(255,0,0);
    private GameState game;
    private CrossyApp crossyApp;
    private BufferedImage image;

    // EFFECTS: constructs a renderer with the current game session
    public CrossyRenderer(GameState game, CrossyApp app) {
        this.game = game;
        this.crossyApp = app;
    }

    // MODIFIES: graphics
    // EFFECTS: draws GameState onto graphics
    void draw(Graphics graphics) {
        drawGrass(graphics);
        drawTrees(graphics);
        drawRoads(graphics);
        drawCars(graphics);
        drawChicken(graphics);
        drawScore(graphics);

    }

    // MODIFIES: graphics
    // EFFECTS: draws grass onto graphics
    void drawGrass(Graphics graphics) {
        HashSet<Position> listOfGrass = game.getListOfGrass();
        graphics.setColor(GRASS_COLOUR);
        for (Position p: listOfGrass) {
            graphics.fillRect(getScreenXCoord(p), getScreenYCoord(p), SCALE, SCALE);
        }
    }

    // MODIFIES: graphics
    // EFFECTS: draws trees onto graphics
    void drawTrees(Graphics graphics) {
        HashSet<Position> listOfTrees = game.getListOfTrees();
        graphics.setColor(TREE_COLOUR);
        for (Position p: listOfTrees) {
            graphics.fillRect(getScreenXCoord(p), getScreenYCoord(p), SCALE, SCALE);
        }
    }

    // MODIFIES: graphics
    // EFFECTS: draws GameState onto graphics
    void drawRoads(Graphics graphics) {
        HashSet<Road> listOfRoads = game.getListOfRoads();
        graphics.setColor(ROAD_COLOUR);
        for (Road r: listOfRoads) {
            graphics.fillRect(0, r.getPosition() * SCALE, SCALE * CANVAS_SIZE, SCALE);
        }
    }

    // MODIFIES: graphics
    // EFFECTS: draws cars onto graphics
    void drawCars(Graphics graphics) {
        HashSet<Car> listOfCars = game.getListOfCars();
        graphics.setColor(CAR_COLOR);
        for (Car c: listOfCars) {
            Position carPos = c.getPosition();
            graphics.fillRect(getScreenXCoord(carPos), getScreenYCoord(carPos), SCALE, SCALE);
        }
    }

    // MODIFIES: graphics
    // EFFECTS: draws chicken onto graphics
    void drawChicken(Graphics graphics) {
        Chicken chicken = game.getChicken();
        graphics.setColor(CHICKEN_COLOUR);
        if (image == null) {
            try {
                image = ImageIO.read(new File(CHICKEN_IMG_PATH));
            } catch (Exception e) {
                System.out.println("chicken not found");
            }
        }
        if (image != null) {
            graphics.drawImage(image, getScreenXCoord(chicken.getPosition()),
                    getScreenYCoord(chicken.getPosition()), SCALE, SCALE, crossyApp);
        } else {
            graphics.fillOval(getScreenXCoord(chicken.getPosition()),
                    getScreenYCoord(chicken.getPosition()), SCALE, SCALE);
        }
    }

    // MODIFIES: graphics
    // EFFECTS: displays score onto graphics
    void drawScore(Graphics graphics) {
        Font font = new Font("Arial", Font.BOLD, 20);
        graphics.setFont(font);
        graphics.setColor(Color.white);
        graphics.drawString("SCORE: " + game.getScore(), 20,20);
    }

    // EFFECTS: translates game board position to actual screen coordinates
    public int getScreenXCoord(Position p) {
        return p.getX() * SCALE;
    }

    // EFFECTS: translates game board position to actual screen coordinates
    public int getScreenYCoord(Position p) {
        return p.getY() * SCALE;
    }
}
