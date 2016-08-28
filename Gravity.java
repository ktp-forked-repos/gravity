package gravity;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * The main class that displays the window in which stuff occurs. Uses JavaFX for display.
 * @author Kiran Tomlinson
 */
public class Gravity extends Application {

    //The width and height of the window.
    public static double WIDTH;
    public static double HEIGHT;

    //The system in which bodies are stored. This is essentially the simulation universe
    public static System system;

    //A variable to store the timestamp of the last frame.
    public static double prevT = 0;

    /**
     * Gravity method.
     * @param args
     */
    public static void main(String[] args) {


        system = new System();


        /**
         * Sample system
         */
        Body planet = new Body(30, new Vector(950, 100), Color.BLUE);
        Body sun = new Body(1000, new Vector(950, 500), Color.BLACK);
        Body otherPlanet = new Body(20, new Vector(950, 700), Color.RED);
        Body eccentric = new Body(5, new Vector(300, 500), Color.GRAY);


        planet.velocity = new Vector(13, 0);
        otherPlanet.velocity = new Vector(-25, 0);
        eccentric.velocity = new Vector(3, -7);

        system.addBody(planet);
        system.addBody(sun);
        system.addBody(otherPlanet);
        system.addBody(eccentric);


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

        //JavaFX stuff for window name, size, etc
        stage.setTitle("Gravity");
        Group root = new Group();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //The start time of the simulation
        final long startNanoTime = java.lang.System.nanoTime();


        //The simulation loop!
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {

                //Uncomment this to only display current object positions
                gc.clearRect(0, 0, WIDTH, HEIGHT);

                // The timestamp of the current frame in seconds, with t=0 at currentNanoTime
                double t = (currentNanoTime - startNanoTime) / 1_000_000_000.0;

                //Loop over each body in the system, step its position forward by dt, and draw it.
                for (Body body : system.getBodies()) {

                    body.step(0.5);

                    gc.setFill(body.color);
                    gc.fillOval(body.position.x - body.radius, body.position.y - body.radius, 2 * body.radius, 2 * body.radius);
                }
                prevT = t;

            }
        };

        timer.start();

        stage.show();

    }

}
