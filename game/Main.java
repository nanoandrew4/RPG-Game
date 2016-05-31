package game;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.*;

public class Main extends Application{
    public static Values values = new Values();

    public static OverworldMap overworldMap;

    private Pane layout = new Pane();
    // will have multiple screens for offscreen rendering and so on
    private Scene mainScene;
    private Scene overworldScene;

    public static void main(String[] args){
        launch(args);
    }

    private void getScreenSize(){
        values.screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        values.screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        //mainScene = new Scene(layout, values.screenWidth, values.screenHeight);
        mainScene = new Scene(layout, 800, 600);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("RPG Game");
        primaryStage.setFullScreen(true);

        getScreenSize();

        primaryStage.setScene(mainScene);
        primaryStage.show();

        Button playButton = new Button("Play");
        playButton.relocate(values.screenWidth / 2, values.screenHeight / 2);
        layout.getChildren().add(playButton);
        playButton.setOnAction(event -> {
            System.out.println("Loading overworld");
            overworldMap = new OverworldMap();
            loadOverworld(primaryStage);
            primaryStage.setFullScreen(true);
        });
    }

    public static void sleep(int time){
        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < time);
    }

    private void loadOverworld(Stage primaryStage){

        layout = overworldMap.getLayout(values.zoom); // max is max zoom /2
        overworldScene = new Scene(layout, values.screenWidth, values.screenHeight);

        // threading the movement might help with smoothing, by making bool check work without freeze

        //overworldScene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
        //    values.keyPressed = true;
        //});

        overworldScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode() == KeyCode.Z && values.zoom > 0){ // for zooming in
                //while (!values.keyPressed) {
                    values.zoom--;
                    System.out.println("Zoom in");
                    loadOverworld(primaryStage);
                //}
            }
            else if(event.getCode() == KeyCode.X && values.zoom < values.mapZoomMax){ // for zooming out
                values.zoom++;
                System.out.println("Zoom out");
                loadOverworld(primaryStage);
            }
            else if(event.getCode() == KeyCode.A || event.getCode() == KeyCode.S || event.getCode() == KeyCode.W || event.getCode() == KeyCode.D){
                //while(!values.keyPressed) {
                    overworldMap.camera.scrollCamera(event.getCode());
                    loadOverworld(primaryStage);
                //}
            }
            //values.keyPressed = false;
        });

        primaryStage.setScene(overworldScene);
    }
}
