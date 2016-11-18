/*
    Super-controller, owns both overworld and inmap controllers.
 */

package main;

import inmap.InMapModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import inmap.InMapController;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import overworld.OverworldController;
import overworld.OverworldModel;

import javax.swing.*;

public class Main extends Application {
    //controllers
    public OverworldController overworldController;
    public InMapController IMController;
    public DBManager dbManager;
    //converts keycodes into control enums
    private HashMap<KeyCode, Control> keybindings;

    Stage stage;
    Scene scene;
    Pane pane;

    public int mapSize;
    private int select = 0;
    private File[] listOfFiles;
    public double screenWidth, screenHeight;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        keybindings = new HashMap<>();
        keybindings.put(KeyCode.W, Control.UP);
        keybindings.put(KeyCode.A, Control.LEFT);
        keybindings.put(KeyCode.S, Control.DOWN);
        keybindings.put(KeyCode.D, Control.RIGHT);
        keybindings.put(KeyCode.UP, Control.UP);
        keybindings.put(KeyCode.LEFT, Control.LEFT);
        keybindings.put(KeyCode.DOWN, Control.DOWN);
        keybindings.put(KeyCode.RIGHT, Control.RIGHT);
        keybindings.put(KeyCode.M, Control.MENU);
        keybindings.put(KeyCode.I, Control.OPENINV);
        keybindings.put(KeyCode.C, Control.OPENCHAR);
        keybindings.put(KeyCode.P, Control.OPENPARTY);
        keybindings.put(KeyCode.N, Control.OPENNOTES);
        keybindings.put(KeyCode.O, Control.OPENOPTIONS);
        keybindings.put(KeyCode.Z, Control.SELECT);
        keybindings.put(KeyCode.ENTER, Control.SELECT);
        keybindings.put(KeyCode.SPACE, Control.SELECT);
        keybindings.put(KeyCode.X, Control.BACK);
        keybindings.put(KeyCode.TAB, Control.TOGGLE);
        keybindings.put(KeyCode.F, Control.SWITCH);
        //temporary keybindings
        keybindings.put(KeyCode.ESCAPE, Control.ESC);
        keybindings.put(KeyCode.R, Control.R);
        keybindings.put(KeyCode.T, Control.T);
        keybindings.put(KeyCode.ALT, Control.ALT);

        stage.setTitle("Rising Legend");

        getScreenSize();

        pane = new Pane();

        scene = new Scene(pane, screenWidth, screenHeight);

        ImageView bg = new ImageView(new Image("/media/graphics/backgrounds/mainmenu.jpg",
                screenWidth, screenHeight, false, false));

        Text title = new Text(0, screenHeight * 2 / 5, "Rising Legend");
        title.setFont(Font.font("ARIAL", FontWeight.BOLD, 72));
        title.setFill(Paint.valueOf("SADDLEBROWN"));
        title.setWrappingWidth(screenWidth);
        title.setTextAlignment(TextAlignment.CENTER);

        Text newGame = new Text(0, screenHeight * 2 / 3, "New Game");
        newGame.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 32));
        newGame.setFill(Paint.valueOf("WHITE"));
        newGame.setWrappingWidth(screenWidth);
        newGame.setTextAlignment(TextAlignment.CENTER);

        Text loadGame = new Text(0, screenHeight * 3 / 4, "Load Game");
        loadGame.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 32));
        loadGame.setFill(Paint.valueOf("WHITE"));
        loadGame.setWrappingWidth(screenWidth);
        loadGame.setTextAlignment(TextAlignment.CENTER);

        Rectangle r = new Rectangle(screenWidth / 2, screenHeight / 15, Paint.valueOf("WHITE"));
        r.setEffect(new BoxBlur(3, 3, 3));
        r.setOpacity(.3);
        r.relocate(screenWidth / 4, screenHeight * 2 / 3 - screenHeight / 20);

        pane.getChildren().addAll(bg, title, newGame, loadGame, r);

        stage.setScene(scene);
        stage.show();

        select = 0;

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            Control c = getControl(event.getCode());

            if (listOfFiles == null) {
                switch (c) {
                    case UP:
                        select += (select >= 1 ? -1 : 1);
                        break;
                    case DOWN:
                        select -= (select <= 0 ? -1 : 1);
                        break;
                    case SELECT:
                        //new game: default very large
                        if (select == 0) {
                            startOverworldController(1000, null, null);
                        }
                        //load game
                        else if (select == 1) {
                            pane.getChildren().removeAll(title, newGame, loadGame);

                            File folder = new File("src/saves");
                            listOfFiles = folder.listFiles();

                            for (int i = 0; i < listOfFiles.length; i++) {
                                if (listOfFiles[i].isFile()) {
                                    Text b = new Text(0, screenHeight / 16 * (i + 1), listOfFiles[i].getName().split("\\.")[0]);
                                    b.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 32));
                                    b.setWrappingWidth(screenWidth);
                                    b.setTextAlignment(TextAlignment.CENTER);
                                    pane.getChildren().add(b);
                                }
                            }

                            select = 0;
                            r.relocate(screenWidth / 4, screenHeight / 16 * (select + 1) - screenHeight / 20);
                        }
                        return;
                    case TOGGLE:
                        startInMapController(null);
                        IMController.newLocation(new Point(0, 0), "cave");
                        IMController.passControl(new Point(0, 0));
                        break;
                    default:
                        break;
                }
                if (select == 0)
                    r.relocate(screenWidth / 4, screenHeight * 2 / 3 - screenHeight / 20);
                else if (select == 1)
                    r.relocate(screenWidth / 4, screenHeight * 3 / 4 - screenHeight / 20);
            } else {
                switch (c) {
                    case UP:
                        select -= (select <= 0 ? -(listOfFiles.length - 1) : 1);
                        break;
                    case DOWN:
                        select += (select >= (listOfFiles.length - 1) ? -(listOfFiles.length - 1) : 1);
                        break;
                    case SELECT:
                        try {
                            Object[] models = loadModel(listOfFiles[select].getName().split("\\.")[0]);
                            startOverworldController(-1, (OverworldModel) models[0], (InMapModel) models[1]);
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                r.relocate(screenWidth / 4, screenHeight / 16 * (select + 1) - screenHeight / 20);
            }

            event.consume();
        });

//        Pane layout = new Pane();
//        Button newGame = new Button("New Game");
//        Button loadGame = new Button("Load Game");
//        Button inMap = new Button("InMap Test");
//
//        newGame.relocate(screenWidth / 2 - 25, screenHeight / 2 - 25);
//        loadGame.relocate(screenWidth / 2 - 25, screenHeight / 2 + 25);
//        inMap.relocate(screenWidth / 2 - 25, screenHeight / 2 + 75);
//        layout.getChildren().add(newGame);
//        layout.getChildren().add(loadGame);
//        layout.getChildren().add(inMap);
//        newGame.setOnAction(event -> {
//
//            dbManager = new DBManager("test");
//            
//            layout.getChildren().removeAll(newGame, loadGame, inMap);
//
//            Button verySmall = new Button("Very Small");
//            Button small = new Button("Small");
//            Button medium = new Button("Medium");
//            Button large = new Button("Large");
//            Button veryLarge = new Button("Very Large");
//
//            verySmall.relocate(screenWidth / 2 - 25, screenHeight / 2 - 70);
//            small.relocate(screenWidth / 2 - 25, screenHeight / 2 - 35);
//            medium.relocate(screenWidth / 2 - 25, screenHeight / 2);
//            large.relocate(screenWidth / 2 - 25, screenHeight / 2 + 35);
//            veryLarge.relocate(screenWidth / 2 - 25, screenHeight / 2 + 70);
//
//            layout.getChildren().add(verySmall);
//            layout.getChildren().add(small);
//            layout.getChildren().add(medium);
//            layout.getChildren().add(large);
//            layout.getChildren().add(veryLarge);
//
//            verySmall.setOnAction(event1 -> {
//                startOverworldController(150, true, null);
//            });
//            small.setOnAction(event1 -> {
//                startOverworldController(300, true, null);
//            });
//            medium.setOnAction(event1 -> {
//                startOverworldController(500, true, null);
//            });
//            large.setOnAction(event1 -> {
//                startOverworldController(750, true, null);
//            });
//            veryLarge.setOnAction(event1 -> {
//                startOverworldController(1000, true, null);
//            });
//            
//            System.out.println("New game being created...");
//        });
//        
//        loadGame.setOnAction(event -> {
//
//            layout.getChildren().removeAll(newGame, loadGame, inMap);
//
//            File folder = new File("src/saves");
//            File[] listOfFiles = folder.listFiles();
//
//            for (int i = 0; i < listOfFiles.length; i++) {
//                if (listOfFiles[i].isFile()) {
//                    Button b = new Button(listOfFiles[i].getName().split("\\.")[0]);
//                    b.relocate(screenWidth / 2 - 50, screenHeight / 16 + i * 50);
//                    layout.getChildren().add(b);
//                    b.setOnAction(event1 -> {
//                        startOverworldController(0, false, b.getText());
//                    });
//                }
//            }
//
//            /*System.out.println("Loading game...");
//            startOverworldController(0, false); // mapSize has to be set to 1 for load to work
//            startInMapController();*/
//        });
//        
//        inMap.setOnAction(event -> {
//            startInMapController();
//            IMController.newLocation(new Point(0, 0), "cave");
//            IMController.passControl(new Point(0, 0));
//        });
//        
//        stage.setScene(new Scene(layout, screenWidth, screenHeight));
//        stage.show();
    }

    //start overworld controller
    private void startOverworldController(int mapSize, OverworldModel overworldModel, InMapModel inmapModel) {
        // if controllers need to talk, initialize objects and run instead of instance of new class
        if (overworldModel == null)
            overworldController = new OverworldController(this, mapSize);
        else
            overworldController = new OverworldController(this, overworldModel);

        Thread overworldThread = new Thread(overworldController);
        overworldThread.setDaemon(true);
        overworldThread.run();

        startInMapController(inmapModel);

        System.out.println("All threads started");
    }

    //start inmap controller
    private void startInMapController(InMapModel inMapModel) {
        if (inMapModel == null)
            IMController = new InMapController(this);
        else
            IMController = new InMapController(this, inMapModel);
        Thread inmapThread = new Thread(IMController);
        inmapThread.setDaemon(true);

        inmapThread.run();
    }

    public void saveModel() throws IOException {

        if (overworldController.getModelName() == null)
            overworldController.setModelName(JOptionPane.showInputDialog(this, "Enter name to save game as: "));

        System.out.println("Saving game...");
        long start = System.currentTimeMillis();
        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream("src/saves/" + overworldController.getModelName() + ".sav"));
        out.writeObject(keybindings);
        out.writeObject(overworldController.getModel());
        out.writeObject(IMController.getModel());
        out.close();
        System.out.println("Wrote successfully! Process took " + (System.currentTimeMillis() / 1000d - start / 1000d));
    }

    private Object[] loadModel(String saveName) throws IOException, ClassNotFoundException {
        FSTObjectInput in = new FSTObjectInput(new FileInputStream("src/saves/" + saveName + ".sav"));
        keybindings = (HashMap<KeyCode, Control>) in.readObject();
        Object[] models = {in.readObject(), in.readObject()};
        in.close();
        return models;
        // start other threads if necessary
    }

    public void setStage(Scene scene) {
        stage.setScene(scene);
        stage.show();
    }

    private void getScreenSize() {
        screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    }

    //returns control value of keycode with keybindings hashmap
    public Control getControl(KeyCode k) {
        if (keybindings.get(k) == null)
            return Control.NULL;
        else
            return keybindings.get(k);
    }
}