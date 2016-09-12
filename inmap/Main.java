/*
    Main controller for game, controller of controllers,
 */

package inmap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    //ExecutorService threadPool = Executors.newFixedThreadPool(1); // change later, only need 1 for overworld now

//    OverworldController overworldController;
    InMapController IMController;

    Stage stage;

//    int mapSize;

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
        //Button loadGame = new Button("Load Game");

        newGame.relocate(screenWidth / 2 - 25, screenHeight / 2 - 25);
        //loadGame.relocate(screenWidth / 2 - 25, screenHeight / 2 + 25);
        layout.getChildren().add(newGame);
        //layout.getChildren().add(loadGame);
        newGame.setOnAction(event -> {
            
            layout.getChildren().remove(newGame);
            
            System.out.println("New game being created...");
            
            IMController = new InMapController(this);
            
            IMController.run();
        });
//        loadGame.setOnAction(event -> {
//            System.out.println("Loading game...");
//            startOverworldController(0, false); // mapSize has to be set to 1 for load to work
//        });

        stage.setScene(new Scene(layout, screenWidth, screenHeight));
        stage.show();
    }

//    private void startOverworldController(int mapSize, boolean newGame){
//        this.mapSize = mapSize;
//        // if controllers need to talk, initialize objects and run instead of instance of new class
//        Thread overworldThread = new Thread(new OverworldController(this, newGame));
//        overworldThread.setDaemon(true);
//        overworldThread.run();
//
//        System.out.println("All threads started");
//
//        //threadPool.shutdown();
//    }

    public void setStage(Scene scene){
        stage.setScene(scene);
        stage.show();
    }

    private void getScreenSize() {
        screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    }
}
