package game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {

    ExecutorService threadPool = Executors.newFixedThreadPool(1); // change later, only need 1 for overworld now

    Stage stage; // might crash

    public double screenWidth, screenHeight;

    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage stage){
        this.stage = stage;
        stage.setTitle("Rising Legend");

        getScreenSize();

        Pane layout = new Pane();
        Button playButton = new Button("Play");

        playButton.relocate(screenWidth / 2, screenHeight / 2);
        layout.getChildren().add(playButton);
        playButton.setOnAction(event -> {
            System.out.println("Initializing UI");
            startControllers();
        });

        stage.setScene(new Scene(layout, screenWidth, screenHeight));
        stage.show();
    }

    private void startControllers(){
        // if controllers need to talk, initialize objects and run instead of instance of new class
        threadPool.execute(new OverworldController(this));

        threadPool.shutdown();
    }

    public void setStage(Scene scene){
        stage.setScene(scene);
    }

    public void sleep(long time){
        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < time);
    }

    private void getScreenSize() {
        screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    }
}
