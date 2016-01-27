package gravity2d;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * The main class that displays the window in which stuff occurs. Uses JavaFX for display.
 * @author kiran
 */
public class Main extends Application {

    //The width and height of the window.
    public static final double W = 1500;
    public static final double H = 1000;

    //The system in which bodies are stored. This is essentially the simulation universe
    public static GravitySystem system;

    //A variable to store the timestamp of the last frame. Useful for variable dt based on calculation time.
    public static double prevT = 0;

    /**
     * Main method.
     * @param args
     */
    public static void main(String[] args) {

        system = new GravitySystem();


        /**
         * A fun, varied system
         */
        Body planet = new Body(30, new MyVector(750, 100), Color.BLUE);
        Body sun = new Body(1000, new MyVector(750, 500), Color.BLACK);
        Body otherPlanet = new Body(20, new MyVector(750, 700), Color.RED);
        Body eccentric = new Body(5, new MyVector(100, 500), Color.GRAY);


        planet.velocity = new MyVector(13, 0);
        otherPlanet.velocity = new MyVector(-25, 0);
        eccentric.velocity = new MyVector(3, -7);

        system.addBody(planet);
        system.addBody(sun);
//        system.addBody(otherPlanet);
//        system.addBody(eccentric);
//        sun.isFixed = true;

        /**
         * Simple harmonic oscillator. Note that the amplitude decreases due to the inherent error of RK4
         */
//        Body one = new Body(10000, new MyVector(600, 500), Color.BLACK);
//        Body two = new Body(10000, new MyVector(900, 500), Color.BLACK);
//        one.isFixed = true;
//        two.isFixed = true;
//
//        Body oscillator = new Body(50, new MyVector(750, 100), Color.BLUE);
//
//        system.addBody(one);
//        system.addBody(two);
//        system.addBody(oscillator);

        launch(args);
    }

    /**
     * JavaFX method that runs/displays the sim
     * @param stage
     */
    public void start(Stage stage) {

        //JavaFX stuff for window name, size, etc
        stage.setTitle("Gravity2D");
        Group root = new Group();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        Canvas canvas = new Canvas(W, H);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //The start time of the simulation
        final long startNanoTime = System.nanoTime();


        //The simulation loop!
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {

                //Uncomment this to only display current object positions
//              gc.clearRect(0, 0, W, H);

                // The timestamp of the current frame in seconds, with t=0 at currentNanoTime
                double t = (currentNanoTime - startNanoTime) / 1_000_000_000.0;

                //Loop over each body in the system, step its position forward by dt, and draw it.
                for (Body body : system.getBodies()) {

                    gc.setFill(body.color);
                    if (!body.isFixed) {
                        body.step(0.1);
                    }
                    gc.fillOval(body.position.x - body.radius, body.position.y - body.radius, 2 * body.radius, 2 * body.radius);
                }

                stage.setTitle("" + (int)system.getTotalEnergy());

                prevT = t;

            }
        };

        timer.start();

        stage.show();

    }

}
