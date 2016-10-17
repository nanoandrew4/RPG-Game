/*
    Super-controller, owns both overworld and inmap controllers.
 */

package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.util.HashMap;
import java.awt.Point;

import inmap.InMapController;
import overworld.OverworldController;

public class Main extends Application {
    //controllers
    public OverworldController overworldController;
    public InMapController IMController;
    public DBManager dbManager;
    //converts keycodes into control enums
    private HashMap<KeyCode,Control> keybindings;

    Stage stage;

    public int mapSize;
    public double screenWidth, screenHeight;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage){
        this.stage = stage;
        
        keybindings = new HashMap<>();
        keybindings.put(KeyCode.W,Control.UP);
        keybindings.put(KeyCode.A,Control.LEFT);
        keybindings.put(KeyCode.S,Control.DOWN);
        keybindings.put(KeyCode.D,Control.RIGHT);
        keybindings.put(KeyCode.UP,Control.UP);
        keybindings.put(KeyCode.LEFT,Control.LEFT);
        keybindings.put(KeyCode.DOWN,Control.DOWN);
        keybindings.put(KeyCode.RIGHT,Control.RIGHT);
        keybindings.put(KeyCode.TAB,Control.TAB);
        keybindings.put(KeyCode.C,Control.MENU);
        keybindings.put(KeyCode.Z,Control.SELECT);
        keybindings.put(KeyCode.X,Control.BACK);
        keybindings.put(KeyCode.ESCAPE, Control.ESC);
        //temporary keybindings
        keybindings.put(KeyCode.R,Control.R);
        keybindings.put(KeyCode.T,Control.T);
        keybindings.put(KeyCode.ALT, Control.ALT);
        
        stage.setTitle("Rising Legend");

        getScreenSize();

        Pane layout = new Pane();
        Button newGame = new Button("New Game");
        Button loadGame = new Button("Load Game");
        Button inMap = new Button("InMap Test");

        newGame.relocate(screenWidth / 2 - 25, screenHeight / 2 - 25);
        loadGame.relocate(screenWidth / 2 - 25, screenHeight / 2 + 25);
        inMap.relocate(screenWidth / 2 - 25, screenHeight / 2 + 75);
        layout.getChildren().add(newGame);
        layout.getChildren().add(loadGame);
        layout.getChildren().add(inMap);
        newGame.setOnAction(event -> {

            dbManager = new DBManager("test");
            
            layout.getChildren().remove(newGame);
            layout.getChildren().remove(loadGame);
            layout.getChildren().remove(inMap);

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
                startOverworldController(150, true);
            });
            small.setOnAction(event1 -> {
                startOverworldController(300, true);
            });
            medium.setOnAction(event1 -> {
                startOverworldController(500, true);
            });
            large.setOnAction(event1 -> {
                startOverworldController(750, true);
            });
            veryLarge.setOnAction(event1 -> {
                startOverworldController(1000, true);
            });
            
            System.out.println("New game being created...");
        });
        
        loadGame.setOnAction(event -> {
            System.out.println("Loading game...");
            startOverworldController(0, false); // mapSize has to be set to 1 for load to work
            startInMapController();
        });
        
        inMap.setOnAction(event -> {
            startInMapController();
            IMController.newLocation(new Point(0, 0), "cave");
            IMController.passControl(new Point(0, 0));
        });
        
        stage.setScene(new Scene(layout, screenWidth, screenHeight));
        stage.show();
    }

    //start overworld controller
    private void startOverworldController(int mapSize, boolean newGame) {
        this.mapSize = mapSize;
        // if controllers need to talk, initialize objects and run instead of instance of new class
        overworldController = new OverworldController(this, mapSize, newGame);
        Thread overworldThread = new Thread(overworldController);
        overworldThread.setDaemon(true);
        overworldThread.run();

        startInMapController();

        System.out.println("All threads started");
    }
    
    //start inmap controller
    private void startInMapController() {
        IMController = new InMapController(this);
        Thread inmapThread = new Thread(IMController);
        inmapThread.setDaemon(true);

        inmapThread.run();
    }

    public void setStage(Scene scene){
        stage.setScene(scene);
        stage.show();
    }

    private void getScreenSize() {
        screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    }
    
    //returns control value of keycode with keybindings hashmap
    public Control getControl(KeyCode k) {
        if(keybindings.get(k) == null)
            return Control.NULL;
        else
            return keybindings.get(k);
    }
}