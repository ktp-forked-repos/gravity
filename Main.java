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
 * The main class that displays the window in which stuff occurs.
 */
public class Main extends Application {

    /**
     * The width and height of the window.
     */
    public static final double W = 1500;
    public static final double H = 1000;
    public static GravitySystem system;

    public static double prevT = 0;

    public static double x, y;

    /**
     * Main method.
     * @param args
     */
    public static void main(String[] args) {
        system = new GravitySystem();

        Body planet = new Body(1, new MyVector(300, 900));
        Body sun = new Body(1, new MyVector(500, 900));

        Body third = new Body(1, new MyVector(800, 500));

        planet.velocity = new MyVector(10, 0);
        sun.velocity = new MyVector(10, -20);

        system.addBody(planet);
        system.addBody(sun);
        system.addBody(third);


        launch(args);
    }

    public void start(Stage stage) {
        stage.setTitle("Gravity2D");
        Group root = new Group();
        Scene scene = new Scene(root);

        stage.setScene(scene);

        Canvas canvas = new Canvas(W, H);
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        final long startNanoTime = System.nanoTime();


        AnimationTimer timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
//                gc.clearRect(0, 0, W, H);
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;

                gc.setStroke(Color.BLACK);

                for (Body body : system.getBodies()) {
                    body.step(t, t - prevT);
                    gc.fillOval(body.position.x, body.position.y, 5, 5);
                }

                prevT = t;

            }
        };

        timer.start();

        stage.show();

    }

}
