package gravity;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

/**
 * The main class that displays the window in which stuff occurs. Uses JavaFX for display.
 * @author Kiran Tomlinson
 */
public class Gravity extends Application {

    //The width and height of the window.
    public static double WIDTH;
    public static double HEIGHT;

    //The system in which bodies are stored. This is essentially the simulation universe
    public static GravitySystem system;

    //A variable to store the timestamp of the last frame.
    static double prevT = 0;

    static boolean drawTrails = false;
    static boolean paused = false;

    /**
     * Gravity method.
     * @param args
     */
    public static void main(String[] args) {


        system = new GravitySystem();


        launch(args);
    }

    /**
     * JavaFX method that runs/displays the sim
     * @param stage
     */
    public void start(Stage stage) {

        // Get screen dimensions, and set window size appropriately
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        WIDTH = screenBounds.getWidth();
        HEIGHT = screenBounds.getHeight();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        stage.setResizable(false);

        // Create Scene
        stage.setTitle("Gravity");
        StackPane root = new StackPane();
        root.setId("root");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
        stage.setScene(scene);

        // Create canvas
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create instructions
        Text instructions = new Text("Space to pause\nD to toggle trails\nR to reset");
        instructions.setId("instructions");

        // Add items to the window
        root.getChildren().addAll(canvas, instructions);
        root.setAlignment(instructions, Pos.TOP_RIGHT);


        resetSystem();


        //The start time of the simulation
        final long startNanoTime = java.lang.System.nanoTime();


        //The simulation loop!
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {

                // Clear screen
                if (!drawTrails) {
                    gc.clearRect(0, 0, WIDTH, HEIGHT);
                }


                // The timestamp of the current frame in seconds, with t=0 at currentNanoTime
                double t = (currentNanoTime - startNanoTime) / 1_000_000_000.0;

                // List of merged objects
                ArrayList<Body> merged = new ArrayList<>();

                // Resolve collisions
                for (Body body : system.getBodies()) {
                    for (Body otherBody : system.getBodies()) {
                        if (body.equals(otherBody)) continue;

                        // Check if two bodies are colliding
                        if (body.isTouching(otherBody) && !merged.contains(body) && !merged.contains(otherBody)) {

                            // If they are, merge them. Keep the larger one
                            if (body.mass > otherBody.mass) {
                                body.collide(otherBody);
                                merged.add(otherBody);
                            } else {
                                otherBody.collide(body);
                                merged.add(body);
                                break;
                            }
                        }
                    }
                }

                // Remove merged bodies
                system.getBodies().removeAll(merged);

                //Loop over each body in the system, move it, and draw it
                for (Body body : system.getBodies()) {
                    body.step(0.1);
                    gc.setFill(body.color);
                    gc.fillOval(body.position.x - body.radius, body.position.y - body.radius, 2 * body.radius, 2 * body.radius);

                }

                prevT = t;

            }
        };

        timer.start();
        stage.show();

        // Event handler
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case SPACE:
                    togglePaused(timer);
                    break;
                case D:
                    drawTrails = !drawTrails;
                    break;
                case R:
                    resetSystem();
                    gc.clearRect(0, 0, WIDTH, HEIGHT);
                    break;
                default:
                    return;
            }
        });

    }

    /**
     * Pauses or resumes the simulation
     * @param timer
     */
    private void togglePaused(AnimationTimer timer) {
        if (paused) {
            paused = false;
            timer.start();
        } else {
            paused = true;
            timer.stop();
        }
    }


    /**
     * Resets the simulation to initial conditions
     */
    private void resetSystem() {
        system = new GravitySystem();

        // Random planets
        Random rand = new Random();
        for (int i = 0; i < 300; i++) {
            Body planet = new Body(5 + rand.nextInt(20), new Vector(rand.nextInt((int)WIDTH), rand.nextInt((int)HEIGHT)), Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
            planet.velocity = new Vector(rand.nextInt(50) - 25, rand.nextInt(50) - 25);
            system.addBody(planet);
        }
    }

}
