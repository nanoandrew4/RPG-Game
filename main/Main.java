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
    //important vars
    public int mapSize;
    public double screenWidth, screenHeight;
    //view vars
    private Stage stage;
    private Scene scene;
    private Pane pane, mainPane, loadPane;
    private Rectangle selectR;
    private int select = 0;
    private String menuState = "main";
    private Rectangle[] saveR;
    private Text[][] saveInfo;
    private ImageView[] saveImages;
    private File[] listOfFiles;

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

        //creating panes
        pane = new Pane();
        mainPane = new Pane();
        loadPane = new Pane();

        scene = new Scene(pane, screenWidth, screenHeight);
        
        ImageView bg = new ImageView(new Image("/media/graphics/backgrounds/mainmenu.jpg",
                screenWidth, screenHeight, false, false));

        selectR = new Rectangle(screenWidth / 2, screenHeight / 15, Paint.valueOf("WHITE"));
        selectR.setEffect(new BoxBlur(10, 10, 3));
        selectR.setOpacity(.3);
        selectR.relocate(screenWidth/4, screenHeight*2/3-screenHeight/20);

        //mainPane
        Text title = new Text(0, screenHeight * 2 / 5, "Rising Legend");
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, 72));
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

        mainPane.getChildren().addAll(title, newGame, loadGame);

        //loadPane
        saveInfo = new Text[6][2];
        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 2; y++) {
                saveInfo[x][y] = new Text(screenWidth/8 + (x%2) * screenWidth * 2/5 + screenWidth/10,
                        screenHeight/10 + Math.floor(x/2) * screenHeight/4 + y * screenHeight/18 + screenHeight/14, 
                        "Save File Nonexistent");
                saveInfo[x][y].setFont(Font.font("Times New Roman", FontWeight.NORMAL, 24));
                saveInfo[x][y].setFill(Paint.valueOf("BLACK"));
            }
            loadPane.getChildren().addAll(saveInfo[x]);
        }
        
        saveImages = new ImageView[6];
        for (int i = 0; i < 6; i++) {
            saveImages[i] = new ImageView(new Image("/media/graphics/inmap/trump.png",
                    64, 64, false, false));
            saveImages[i].relocate(screenWidth/8 + i%2 * screenWidth * 2/5 + screenWidth/30, 
                        screenHeight/10 + Math.floor(i/2) * screenHeight/4 + screenHeight/20);
        }
        
        /* load saves here using loadSave(SaveFile s) */
//        File folder = new File("src/saves");
//        listOfFiles = folder.listFiles();
//
//        for (int i = 0; i < listOfFiles.length; i++) {
//            if (listOfFiles[i].isFile()) {
//                Text b = new Text(0, screenHeight*(1+i)/16, listOfFiles[i].getName().split("\\.")[0]);
//                b.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 32));
//                b.setWrappingWidth(screenWidth);
//                b.setTextAlignment(TextAlignment.CENTER);
//                loadPane.getChildren().add(b);
//            }
//        }
        
        saveR = new Rectangle[6];
        for (int i = 0; i < 6; i++) {
            saveR[i] = new Rectangle(screenWidth/3, screenHeight/5, Paint.valueOf("WHITE"));
            saveR[i].relocate(screenWidth/8 + i%2 * screenWidth * 2/5, 
                        screenHeight/10 + Math.floor(i/2) * screenHeight/4);
            saveR[i].setOpacity(.2);
        }
        
        loadPane.getChildren().addAll(saveR);
        loadPane.getChildren().addAll(saveImages);
        
        //start
        pane.getChildren().addAll(bg, mainPane, selectR);
        
        stage.setScene(scene);
        stage.show();

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (getControl(event.getCode())) {
                case DOWN:
                    if (menuState.equals("main")) {
                        select++;
                        if (select > 1) {
                            select = 0;
                        }
                    }
                    else if (menuState.equals("load")) {
                        select += 2;
                        if (select > 5)
                            select -= 6;
                    }
                    break;
                    
                case UP:
                    if (menuState.equals("main")) {
                        select--;
                        if (select < 0)
                            select = 1;
                    }
                    else if (menuState.equals("load")) {
                        select -= 2;
                        if (select < 0)
                            select += 6;
                    }
                    break;
                    
                case RIGHT:
                    if (menuState.equals("load")) {
                        select += select % 2 == 0 ? 1 : -1;
                    }
                    break;
                    
                case LEFT:
                    if (menuState.equals("load")) {
                        select += select % 2 == 0 ? 1 : -1;
                    }
                    break;
                    
                case SELECT:
                    if(menuState.equals("main")) {
                        //new game: default very large
                        if (select == 0) {
                            startOverworldController(1000, null, null);
                        }
                        //load game
                        else if (select == 1) {
                            select = 0;
                            menuState = "load";
                        }
                    }
                    else if(menuState.equals("load")) {
                        try {
                            Object[] models = loadModel(listOfFiles[select].getName().split("\\.")[0]);
                            startOverworldController(-1, (OverworldModel) models[0], (InMapModel) models[1]);
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    
                case BACK:
                    if(menuState.equals("load"))
                        menuState = "main";
                    select = 0;
                    break;
                    
                case TOGGLE:
                    if (menuState.equals("main")) {
                        startInMapController(null);
                        IMController.newLocation(new Point(-1, -1), "cave");
                        IMController.passControl(new Point(-1, -1));
                    }
                    break;
                    
                default:
                    break;
            }
            
            updateView();

            event.consume();
        });
    }
    
    //update menu view
    private void updateView() {
        switch (menuState) {
            case "main":
                if(pane.getChildren().remove(loadPane)) {
                    selectR.setWidth(screenWidth / 2);
                    selectR.setHeight(screenHeight / 15);
                    pane.getChildren().add(mainPane);
                }
                selectR.relocate(screenWidth/4, screenHeight*(8+select)/12-screenHeight/20);
                break;
                
            case "load":
                if(pane.getChildren().remove(mainPane)) {
                    selectR.setWidth(screenWidth / 3);
                    selectR.setHeight(screenHeight / 5);
                    pane.getChildren().add(loadPane);
                }
                selectR.relocate(screenWidth/8+select%2*screenWidth*2/5, 
                        screenHeight/10+Math.floor(select/2)*screenHeight/4);
                break;
        }
    }
    
    //load a save file
    private void loadSave(SaveFile s) {
        saveInfo[s.slot][0].setText(s.name + " " + s.level);
        saveInfo[s.slot][1].setText("Playtime: " + s.playtime + " units of time");
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
//        if (overworldController.getModelName() == null)
//            overworldController.setModelName(JOptionPane.showInputDialog(this, "Enter name to save game as: "));
        overworldController.setModelName("asdf" + (int)(Math.random()*26));

        System.out.println("Saving game...");
        long start = System.currentTimeMillis();
        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream("src/saves/" 
                + overworldController.getModelName() + ".sav"));
        out.writeObject(keybindings);
        out.writeObject(overworldController.getModel());
        out.writeObject(IMController.getModel());
        out.close();
        System.out.println("Wrote successfully! Process took "+(System.currentTimeMillis()/1000d-start/1000d));
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