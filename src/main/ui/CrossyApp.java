package ui;

import model.GameState;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

// Reference from Lab6 SnakeApp
// https://github.students.cs.ubc.ca/CPSC210-2023S-T2/lab6_j4o9k

// A game app for crossy
public class CrossyApp extends JFrame {
    public static final int SCALE = 30;
    public static final int CANVAS_SIZE = 20;
    private static final int WIDTH = CANVAS_SIZE * SCALE;
    private static final int HEIGHT = CANVAS_SIZE * SCALE;
    private static final int INTERVAL = 1000;
    private GameState game;
    private CrossyRenderer renderer;
    public static final String JSON_STORE = "./data/gamestate.json";
    public static final String CHICKEN_IMG_PATH = "./data/chicken.png";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private String input;
    private boolean isPaused;
    private boolean gameOverShown;

    // EFFECTS: sets up a display window for the game
    public CrossyApp() {
        super("CROSSY");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setUndecorated(true);
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        game = new GameState(CANVAS_SIZE);
        promptLoadGame();
        renderer = new CrossyRenderer(game, this);
        isPaused = false;
        addKeyListener(new KeyHandler());
        centreOnScreen();
        addTimer();
        setVisible(true);
    }

    // EFFECTS: generates a popup window if there is a game save available
    // MODIFIES: this
    public void promptLoadGame() {
        if (isFilePresent(JSON_STORE)) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Do you wish to continue from save?",
                    "Game Save Detected",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                loadGameState();
                File f = new File(JSON_STORE);
                f.delete();
            }
        }
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

    // EFFECTS: loads save file to game
    // MODIFIES: this
    private void loadGameState() {
        try {
            game.clear(); // probably not needed but i would rather comment here than delete it :)
            game = jsonReader.read();
            System.out.println("Game loaded from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Read failed");
        }
    }



    // EFFECTS: Represents a key handler that responds to keyboard events
    private class KeyHandler extends KeyAdapter {
        @Override
        // MODIFIES: this
        // EFFECTS:  updates game in response to a keyboard event
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    input = "left";
                    break;
                case KeyEvent.VK_D:
                    input = "right";
                    break;
                case KeyEvent.VK_W:
                    input = "up";
                    break;
                case KeyEvent.VK_S:
                    input = "down";
                    break;
                case KeyEvent.VK_Q:
                    input = "quit";
                    isPaused = true;
                    break;
            }
        }

    }

    // MODIFIES: this
    // EFFECTS:  frame is centred on desktop
    private void centreOnScreen() {
        Dimension scrn = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((scrn.width - getWidth()) / 2, (scrn.height - getHeight()) / 2);
    }

    // EFFECTS: initializes a timer that updates game each
    //          INTERVAL milliseconds
    private void addTimer() {
        final Timer t = new Timer(INTERVAL, null);
        t.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (game.isChickenDead() && !gameOverShown) {
                    t.stop();
                    showGameOverWindow();
                    gameOverShown = true;
                } else if (gameOverShown) {
                    t.stop();
                } else if (isPaused) {
                    t.stop();
                    showPauseWindow();
                } else {
                    t.start();
                    game.tick(input);
                    input = "none";
                    repaint();
                }
            }
        });
        t.start();
    }

    // EFFECTS: shows a game over window
    // MODIFIES: this
    private void showGameOverWindow() {
        JFrame gameOverFrame = new JFrame("Game Over");
        gameOverFrame.setSize(CANVAS_SIZE * SCALE / 2, CANVAS_SIZE * SCALE / 2);
        gameOverFrame.setLocationRelativeTo(this);
        gameOverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel gameOverLabel = new JLabel("Game Over, you have a score of " + game.getScore());
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        gameOverFrame.getContentPane().setLayout(new BorderLayout());
        gameOverFrame.getContentPane().add(gameOverLabel, BorderLayout.CENTER);
        gameOverFrame.getContentPane().add(quitButton, BorderLayout.SOUTH);
        gameOverFrame.setVisible(true);
    }

    // EFFECTS: displays pause window
    private void showPauseWindow() {
        JFrame pauseFrame = createPauseFrame();
        JLabel pauseLabel = createPauseLabel();
        JButton resumeButton = createResumeButton(pauseFrame);
        JButton saveButton = createSaveButton();
        JButton quitButton = createQuitButton();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(resumeButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(quitButton);

        pauseFrame.add(pauseLabel, BorderLayout.CENTER);
        pauseFrame.add(buttonPanel, BorderLayout.SOUTH);
        pauseFrame.setVisible(true);
    }

    // EFFECTS: create a pause JFrame
    private JFrame createPauseFrame() {
        JFrame pauseFrame = new JFrame("Paused");
        pauseFrame.setSize(CANVAS_SIZE * SCALE / 2, CANVAS_SIZE * SCALE / 2);
        pauseFrame.setLocationRelativeTo(this);
        pauseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return pauseFrame;
    }

    // EFFECTS: create a string to display on the window
    private JLabel createPauseLabel() {
        JLabel pauseLabel = new JLabel("Game Paused");
        pauseLabel.setMaximumSize(new Dimension(CANVAS_SIZE * SCALE / 2, CANVAS_SIZE * SCALE / 2));
        return pauseLabel;
    }

    // EFFECTS: creates a resume button
    private JButton createResumeButton(JFrame pauseFrame) {
        JButton resumeButton = new JButton("Resume");
        resumeButton.addActionListener(e -> {
            isPaused = false;
            addTimer();
            pauseFrame.dispose();
        });
        return resumeButton;
    }

    // EFFECTS: creates a button to save game
    private JButton createSaveButton() {
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            saveGameState();
            System.exit(0);
        });
        return saveButton;
    }

    // EFFECTS: creates a button that quits game
    private JButton createQuitButton() {
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));
        return quitButton;
    }


    @Override
    // MODIFIES: graphics
    // EFFECTS:  clears screen and paints game onto graphics
    public void paint(Graphics graphics) {
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        renderer.draw(graphics);
    }
}
