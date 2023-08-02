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

import static ui.Terminal.CANVAS_SIZE;

public class CrossyApp extends JFrame {
    public static final int SCALE = 30;
    public static final int CANVAS_SIZE = 20;
    private static final int WIDTH = CANVAS_SIZE * SCALE;
    private static final int HEIGHT = CANVAS_SIZE * SCALE;
    private static final int INTERVAL = 1000;
    private GameState game;
    private CrossyRenderer renderer;
    public static final String JSON_STORE = "./data/gamestate.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private String input;
    private boolean resume;

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
        renderer = new CrossyRenderer(game);
        resume = true;
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
                    resume = false;
                    break;
                default:
                    input = "none";
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
                if (game.isChickenDead()) {
                    t.stop();
                    showGameOverWindow();
                } else if (!resume) {
                    t.stop();
                    showPauseWindow();
                } else {
                    game.tick(input);
                    repaint();
                }
                if (resume && !t.isRunning()) {
                    t.restart();
                }
            }
        });
        t.start();
    }

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

    // EFFECT: shows pause window
    private void showPauseWindow() {
        JFrame pauseFrame = new JFrame("Paused");
        pauseFrame.setSize(CANVAS_SIZE * SCALE / 2, CANVAS_SIZE * SCALE / 2);
        pauseFrame.setLocationRelativeTo(this);
        pauseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel pauseLabel = new JLabel("Game Paused, would you like to save before quitting?");
        JButton resumeButton = new JButton("Resume");
        JButton saveButton = new JButton("Save");
        JButton quitButton = new JButton("Quit");
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resume = true;
                pauseFrame.dispose();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGameState();
                System.exit(0);
            }
        });
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        pauseFrame.getContentPane().setLayout(new FlowLayout());
        pauseFrame.getContentPane().add(pauseLabel, BorderLayout.CENTER);
        pauseFrame.getContentPane().add(quitButton, BorderLayout.SOUTH);
        pauseFrame.getContentPane().add(saveButton, BorderLayout.SOUTH);
        pauseFrame.getContentPane().add(resumeButton, BorderLayout.SOUTH);
        pauseFrame.setVisible(true);
    }


    @Override
    // MODIFIES: graphics
    // EFFECTS:  clears screen and paints game onto graphics
    public void paint(Graphics graphics) {
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        renderer.draw(graphics);
    }
}
