package gravity2d;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    public static final double W = 1000;
    public static final double H = 1000;

    public static double x, y;


    public static void main(String[] args) {
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

        x = 0;
        y = 0;

        AnimationTimer timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                gc.clearRect(0, 0, W, H);
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;

                x = 490 + 300 * Math.cos(t);
                y = 490 + 300 * Math.sin(t);

                gc.setStroke(Color.BLACK);
                gc.fillOval(x, y, 20, 20);
                gc.fillOval(450, 450, 100, 100);

            }
        };

        timer.start();

        stage.show();

    }

}
