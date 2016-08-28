/*
    Main controller for game, controller of controllers,
 */

package game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {

    //ExecutorService threadPool = Executors.newFixedThreadPool(1); // change later, only need 1 for overworld now

    OverworldController overworldController;

    Stage stage;

    int mapSize;

    public double screenWidth, screenHeight;

    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage stage){
        this.stage = stage;
        stage.setTitle("Rising Legend");

        getScreenSize();

        Pane layout = new Pane();
        Button newGame = new Button("New Game");
        Button loadGame = new Button("Load Game");

        newGame.relocate(screenWidth / 2 - 25, screenHeight / 2 - 25);
        loadGame.relocate(screenWidth / 2 - 25, screenHeight / 2 + 25);
        layout.getChildren().add(newGame);
        layout.getChildren().add(loadGame);
        newGame.setOnAction(event -> {

            layout.getChildren().remove(newGame);
            layout.getChildren().remove(loadGame);

            Button verySmall = new Button("Very Small");
            Button small = new Button("Small");
            Button medium = new Button("Medium");
            Button large = new Button("Large");
            Button veryLarge = new Button("Very Large");

            verySmall.relocate(screenWidth / 2 - 25, screenHeight / 2 - 70);
            small.relocate(screenWidth / 2 - 25, screenHeight / 2 - 35);
            medium.relocate(screenWidth / 2 - 25, screenHeight / 2);
            large.relocate(screenWidth / 2 - 25, screenHeight / 2 + 35);
            veryLarge.relocate(screenWidth / 2 - 25, screenHeight / 2 + 70);

            layout.getChildren().add(verySmall);
            layout.getChildren().add(small);
            layout.getChildren().add(medium);
            layout.getChildren().add(large);
            layout.getChildren().add(veryLarge);

            verySmall.setOnAction(event1 -> {
                startOverworldController(150);
            });
            small.setOnAction(event1 -> {
                startOverworldController(300);
            });
            medium.setOnAction(event1 -> {
                startOverworldController(500);
            });
            large.setOnAction(event1 -> {
                startOverworldController(750);
            });
            veryLarge.setOnAction(event1 -> {
                startOverworldController(1000);
            });

            System.out.println("New game being created...");
        });
        loadGame.setOnAction(event -> {
            System.out.println("Loading game...");
        });

        stage.setScene(new Scene(layout, screenWidth, screenHeight));
        stage.show();
    }

    private void startOverworldController(int mapSize){
        this.mapSize = mapSize;
        // if controllers need to talk, initialize objects and run instead of instance of new class
        Thread overworldThread = new Thread(new OverworldController(this));
        overworldThread.setDaemon(true);
        overworldThread.run();

        System.out.println("All threads started");

        //threadPool.shutdown();
    }

    public void setStage(Scene scene){
        stage.setScene(scene);
    }

    private void getScreenSize() {
        screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    }
}
