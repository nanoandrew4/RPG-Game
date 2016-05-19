package game;

import javafx.application.Application;
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
        //primaryStage.setFullScreen(true);

        getScreenSize();

        primaryStage.setScene(mainScene);
        primaryStage.show();

        Button playButton = new Button("Play");
        playButton.relocate(values.screenWidth / 2, values.screenHeight / 2);
        layout.getChildren().add(playButton);
        playButton.setOnAction(event -> {
            System.out.println("Loading overworld");
            loadOverworld(primaryStage);
            //primaryStage.setFullScreen(true);
        });
    }

    private void loadOverworld(Stage stage){
        overworldMap = new OverworldMap();
        layout = overworldMap.getLayout(10);
        overworldScene = new Scene(layout, values.screenWidth, values.screenHeight);
        overworldScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode() == KeyCode.Z){ // for zooming

            }
            else if(event.getCode() == KeyCode.X){ // for zooming

            }
        });
        stage.setScene(overworldScene);
    }
}
