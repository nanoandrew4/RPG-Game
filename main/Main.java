/*
    Super-controller, owns both overworld and inmap controllers.
 */

package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.effect.BoxBlur;
import javafx.geometry.Pos;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.swing.*;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import inmap.InMapModel;
import inmap.Character;
import inmap.InMapController;
import overworld.OverworldController;
import overworld.OverworldModel;

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
    private Pane pane, mainPane, loadPane, sizePane, racePane, 
            statPane, charPane, namePane, sumPane, waitPane; //all panes
    private Rectangle selectR; //selection box
    private int select = 0;
    private String menuState = "main";
    //loadPane vars
    private Text[][] saveInfo;
    private ImageView[] saveImages;
    private File[] listOfFiles;
    //racePane vars
    private Text[] raceT;
    private Text raceDes, raceStats;
    //statPane vars
    private Text[] statRolls;
    private int VIT, INT, STR, WIS, LUK, CHA;
    //charPane vars
    private String[] spritePaths, portraitPaths;
    private Image[] sprites, portraits;
    private ImageView sprite, portrait;
    private int spriteSelect, portraitSelect;
    //namePane vars
    private TextField nameField;
    //sumPane vars
    private ImageView sumSpr, sumPor;
    private Text sumStats, sumChar, sumEqp;
    
    //saved vars for new game
    private int ngSize, ngVIT, ngINT, ngSTR, ngWIS, ngLUK, ngCHA;
    private String ngRace, ngName, ngSprite, ngPortrait;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        //create keybindings
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
        sizePane = new Pane();
        racePane = new Pane();
        statPane = new Pane();
        charPane = new Pane();
        namePane = new Pane();
        sumPane = new Pane();
        waitPane = new Pane();

        scene = new Scene(pane, screenWidth, screenHeight);
        
        ImageView bg = new ImageView(new Image("/media/graphics/mainmenu/mainmenu.jpg",
                screenWidth, screenHeight, false, false));

        selectR = new Rectangle(screenWidth / 2, screenHeight / 15, Paint.valueOf("WHITE"));
        selectR.setEffect(new BoxBlur(10, 10, 3));
        selectR.setOpacity(.5);
        selectR.relocate(screenWidth/4, screenHeight*2/3-screenHeight/20);

        //mainPane
        Text title = new Text(0, screenHeight * 2 / 5, "RISING LEGEND");
        title.setFont(Font.font("Bradley Hand", FontWeight.BOLD, 104));
        title.setFill(Paint.valueOf("SADDLEBROWN"));
        title.setWrappingWidth(screenWidth);
        title.setTextAlignment(TextAlignment.CENTER);
        
        Text[] mainT = new Text[3];
        for(int i = 0; i < 3; i++) {
            mainT[i] = new Text(0, screenHeight*2/3 + i*screenHeight/12, "");
            mainT[i].setFont(Font.font("Trattatello", FontWeight.NORMAL, 36));
            mainT[i].setFill(Paint.valueOf("BLACK"));
            mainT[i].setWrappingWidth(screenWidth);
            mainT[i].setTextAlignment(TextAlignment.CENTER);
        }

        mainT[0].setText("New Game");
        mainT[1].setText("Load Game");
        mainT[2].setText("Exit");

        mainPane.getChildren().add(title);
        mainPane.getChildren().addAll(mainT);

        //loadPane

        File folder = new File("src/saves");
        listOfFiles = folder.listFiles();

        saveInfo = new Text[6][2];
        saveImages = new ImageView[6];

        for (int x = 0; x < 6; x++) { // load files
            loadSaveInfo((SaveFile) getSaveFile(x));
        }

        if (saveInfo[0][0] == null)
            System.out.println("welp");

        for (int x = 0; x < 6; x++) {
            if (saveInfo[x][0] == null || saveInfo[x][1] == null) {
                saveInfo[x][0] = new Text(screenWidth / 8 + (x % 2) * screenWidth * 2 / 5 + screenWidth / 10,
                        screenHeight / 10 + Math.floor(x / 2) * screenHeight / 4 + screenHeight / 14,
                        "Save File Nonexistent");
                saveInfo[x][1] = new Text(screenWidth / 8 + (x % 2) * screenWidth * 2 / 5 + screenWidth / 10,
                        screenHeight / 10 + Math.floor(x / 2) * screenHeight / 4 + screenHeight / 18 + screenHeight / 14, "");
            }
            for (int y = 0; y < 2; y++) {
                saveInfo[x][y].setFont(Font.font("Trattatello", FontWeight.NORMAL, 24));
                saveInfo[x][y].setFill(Paint.valueOf("BLACK"));
            }
            loadPane.getChildren().addAll(saveInfo[x]);
        }

        for (int i = 0; i < 6; i++) {
            if (saveImages[i] == null) {
                saveImages[i] = new ImageView(new Image("/media/graphics/inmap/trump.png",
                        screenHeight / 8, screenHeight / 8, false, false));
                saveImages[i].relocate(screenWidth / 8 + i % 2 * screenWidth * 2 / 5 + screenWidth / 60,
                        screenHeight / 10 + Math.floor(i / 2) * screenHeight / 4 + screenHeight / 40);
            }
        }
        
        Rectangle[] saveR = new Rectangle[6];
        for (int i = 0; i < 6; i++) {
            saveR[i] = new Rectangle(screenWidth/3, screenHeight/5, Paint.valueOf("WHITE"));
            saveR[i].relocate(screenWidth/8 + i%2 * screenWidth * 2/5, 
                        screenHeight/10 + Math.floor(i/2) * screenHeight/4);
            saveR[i].setOpacity(.2);
        }
        
        loadPane.getChildren().addAll(saveR);
        loadPane.getChildren().addAll(saveImages);
        
        //sizePane
        Text[] sizeT = new Text[5];
        for (int i = 0; i < 5; i++) {
            sizeT[i] = new Text(0, screenHeight/2 + i*screenHeight/12, "");
            sizeT[i].setWrappingWidth(screenWidth);
            sizeT[i].setTextAlignment(TextAlignment.CENTER);
            sizeT[i].setFont(Font.font("Trattatello", FontWeight.LIGHT, 32));
            sizeT[i].setFill(Paint.valueOf("BLACK"));
        }
        sizeT[0].setText("Miniscule");
        sizeT[1].setText("Small");
        sizeT[2].setText("Moderate");
        sizeT[3].setText("Large");
        sizeT[4].setText("Massive");
        
        Text sizeTitle = new Text(0, screenHeight/3, "Select a world size");
        sizeTitle.setWrappingWidth(screenWidth);
        sizeTitle.setTextAlignment(TextAlignment.CENTER);
        sizeTitle.setFont(Font.font("Bradley Hand", FontWeight.BOLD, 48));
        sizeTitle.setFill(Paint.valueOf("SADDLEBROWN"));
        
        sizePane.getChildren().add(sizeTitle);
        sizePane.getChildren().addAll(sizeT);
        
        //racePane
        raceT = new Text[8];
        for (int i = 0; i < 8; i++) {
            raceT[i] = new Text(0, screenHeight/4 + i*screenHeight/12, "");
            raceT[i].setWrappingWidth(screenWidth/2);
            raceT[i].setTextAlignment(TextAlignment.CENTER);
            raceT[i].setFont(Font.font("Trattatello", FontWeight.LIGHT, 32));
            raceT[i].setFill(Paint.valueOf("BLACK"));
        }
        raceT[0].setText("Human");
        raceT[1].setText("Elf");
        raceT[2].setText("Dwarf");
        raceT[3].setText("Orc");
        raceT[4].setText("Fairy");
        raceT[5].setText("Half-Beast");
        raceT[6].setText("Mutant");
        raceT[7].setText("Skeleton");
        
        Text raceTitle = new Text(0, screenHeight/6, "Select your race");
        raceTitle.setWrappingWidth(screenWidth);
        raceTitle.setTextAlignment(TextAlignment.CENTER);
        raceTitle.setFont(Font.font("Bradley Hand", FontWeight.BOLD, 48));
        raceTitle.setFill(Paint.valueOf("SADDLEBROWN"));
        
        raceDes = new Text(screenWidth/2, screenHeight/4, "");
        raceDes.setWrappingWidth(screenWidth*2/5);
        raceDes.setTextAlignment(TextAlignment.CENTER);
        raceDes.setFont(Font.font("Bradley Hand", FontWeight.LIGHT, 28));
        raceDes.setFill(Paint.valueOf("BLACK"));
        raceStats = new Text(screenWidth/2, screenHeight*3/4, "");
        raceStats.setFont(Font.font("Monaco", FontWeight.NORMAL, 24));
        raceStats.setFill(Paint.valueOf("BLACK"));
        
        racePane.getChildren().addAll(raceTitle, raceDes, raceStats);
        racePane.getChildren().addAll(raceT);
        
        //statPane
        statRolls = new Text[6];
        for (int i = 0; i < 6; i++) {
            statRolls[i] = new Text(screenWidth/2, screenHeight/3 + i*screenHeight/12, "");
            statRolls[i].setFont(Font.font("Trattatello", FontWeight.LIGHT, 32));
            statRolls[i].setFill(Paint.valueOf("BLACK"));
        }
        
        Text statTitle = new Text(0, screenHeight/6, "Select your stats");
        statTitle.setWrappingWidth(screenWidth);
        statTitle.setTextAlignment(TextAlignment.CENTER);
        statTitle.setFont(Font.font("Bradley Hand", FontWeight.BOLD, 48));
        statTitle.setFill(Paint.valueOf("SADDLEBROWN"));
        
        Text statRoll = new Text(0, screenHeight/2, "Reroll");
        statRoll.setWrappingWidth(screenWidth/2);
        statRoll.setTextAlignment(TextAlignment.CENTER);
        statRoll.setFont(Font.font("Trattatello", FontWeight.BOLD, 48));
        statRoll.setFill(Paint.valueOf("BLACK"));
        
        Text statGo = new Text(0, screenHeight*2/3, "Continue");
        statGo.setWrappingWidth(screenWidth/2);
        statGo.setTextAlignment(TextAlignment.CENTER);
        statGo.setFont(Font.font("Trattatello", FontWeight.BOLD, 48));
        statGo.setFill(Paint.valueOf("BLACK"));
        
        VIT = (int)(Math.random()*20+1);
        INT = (int)(Math.random()*20+1);
        STR = (int)(Math.random()*20+1);
        WIS = (int)(Math.random()*20+1);
        LUK = (int)(Math.random()*20+1);
        CHA = (int)(Math.random()*20+1);
        
        statPane.getChildren().addAll(statRolls);
        statPane.getChildren().addAll(statTitle, statRoll, statGo);
        
        //charPane
        spritePaths = new String[5];
        spritePaths[0] = "/media/graphics/inmap/trump.png";
        spritePaths[1] = "/media/graphics/inmap/adelf.png";
        spritePaths[2] = "/media/graphics/inmap/longcat.png";
        spritePaths[3] = "/media/graphics/inmap/skelebro.png";
        spritePaths[4] = "/media/graphics/inmap/mote.png";
        sprites = new Image[spritePaths.length];
        for (int i = 0; i < spritePaths.length; i++) {
            sprites[i] = new Image(spritePaths[i], screenHeight/4, screenHeight/4, false, false);
        }
        
        portraitPaths = new String[3];
        portraitPaths[0] = "/media/graphics/inmap/portrait.jpg";
        portraitPaths[1] = "/media/graphics/inmap/clinton.jpg";
        portraitPaths[2] = "/media/graphics/inmap/harambe.png";
        portraits = new Image[3];
        for (int i = 0; i < portraitPaths.length; i++) {
            portraits[i] = new Image(portraitPaths[i], screenHeight/4, screenHeight/4, false, false);
        }
        
        sprite = new ImageView(sprites[0]);
        sprite.relocate(screenWidth/4, screenHeight/3);
        portrait = new ImageView(portraits[0]);
        portrait.relocate(screenWidth*3/4-screenHeight/4, screenHeight/3);
        
        spriteSelect = 0;
        portraitSelect = 0;
        
        Text charTitle = new Text(0, screenHeight/6, "Select your character.");
        charTitle.setWrappingWidth(screenWidth);
        charTitle.setTextAlignment(TextAlignment.CENTER);
        charTitle.setFont(Font.font("Bradley Hand", FontWeight.BOLD, 48));
        charTitle.setFill(Paint.valueOf("SADDLEBROWN"));
        
        ImageView[] charTri = new ImageView[4];
        for (int i = 0; i < 4; i++) {
            charTri[i] = new ImageView(new Image("/media/graphics/mainmenu/triangle.png", 
                    screenWidth/12, screenHeight/12, true, false));
            charTri[i].relocate(screenWidth, screenWidth);
        }
        
        charPane.getChildren().addAll(charTri);
        charPane.getChildren().addAll(charTitle, sprite, portrait);
        
        //namePane
        Text nameTitle = new Text(0, screenHeight/6, "Enter your name.");
        nameTitle.setWrappingWidth(screenWidth);
        nameTitle.setTextAlignment(TextAlignment.CENTER);
        nameTitle.setFont(Font.font("Bradley Hand", FontWeight.BOLD, 48));
        nameTitle.setFill(Paint.valueOf("SADDLEBROWN"));
        
        Text nameT = new Text(0, screenHeight*3/4, 
                "Press ENTER to continue.\nPress ESCAPE to go back.");
        nameT.setWrappingWidth(screenWidth);
        nameT.setTextAlignment(TextAlignment.CENTER);
        nameT.setFont(Font.font("Bradley Hand", FontWeight.BOLD, 36));
        nameT.setFill(Paint.valueOf("BLACK"));
        
        nameField = new TextField();
        nameField.setAlignment(Pos.CENTER);
        nameField.setBorder(Border.EMPTY);
        nameField.setBackground(Background.EMPTY);
        nameField.setPrefSize(screenWidth/3, screenHeight/18);
        nameField.setFont(Font.font("Trattatello", FontWeight.BOLD, 36));
        nameField.relocate(screenWidth/3, screenHeight/2-25);
        
        namePane.getChildren().addAll(nameTitle, nameT, nameField);
        
        //sumPane
        Text sumTitle = new Text(0, screenHeight/6, "Summary");
        sumTitle.setWrappingWidth(screenWidth);
        sumTitle.setTextAlignment(TextAlignment.CENTER);
        sumTitle.setFont(Font.font("Bradley Hand", FontWeight.BOLD, 48));
        sumTitle.setFill(Paint.valueOf("SADDLEBROWN"));
        
        Text sumGo = new Text(screenWidth/2, screenHeight*6/7, "Press ENTER to start your journey.");
        sumGo.setWrappingWidth(screenWidth/2);
        sumGo.setTextAlignment(TextAlignment.CENTER);
        sumGo.setFont(Font.font("Trattatello", FontWeight.BOLD, 36));
        sumGo.setFill(Paint.valueOf("BLACK"));

        sumSpr = new ImageView();
        sumSpr.relocate(screenWidth/4 - screenHeight*9/32, screenHeight/4);
        sumPor = new ImageView();
        sumPor.relocate(screenWidth/4 + screenHeight/32, screenHeight/4);
        
        sumChar = new Text(screenWidth/8, screenHeight*2/3, "");
        sumChar.setWrappingWidth(screenWidth/4);
        sumChar.setTextAlignment(TextAlignment.CENTER);
        sumChar.setFont(Font.font("Trattatello", FontWeight.NORMAL, 48));
        sumChar.setFill(Paint.valueOf("BLACK"));
        
        sumStats = new Text(screenWidth*3/8, screenHeight/4, "");
        sumStats.setWrappingWidth(screenWidth/4);
        sumStats.setTextAlignment(TextAlignment.CENTER);
        sumStats.setFont(Font.font("Trattatello", FontWeight.NORMAL, 36));
        sumStats.setFill(Paint.valueOf("BLACK"));
        
        sumEqp = new Text(screenWidth*5/8, screenHeight/4, "");
        sumEqp.setWrappingWidth(screenWidth/4);
        sumEqp.setTextAlignment(TextAlignment.CENTER);
        sumEqp.setFont(Font.font("Trattatello", FontWeight.NORMAL, 36));
        sumEqp.setFill(Paint.valueOf("BLACK"));
        
        sumPane.getChildren().addAll(sumTitle, sumGo, sumSpr, sumPor, sumChar, sumStats, sumEqp);
        
        //waitPane
        Text waitTitle = new Text(0, screenHeight/2, "Loading...");
        waitTitle.setWrappingWidth(screenWidth);
        waitTitle.setTextAlignment(TextAlignment.CENTER);
        waitTitle.setFont(Font.font("Bradley Hand", FontWeight.BOLD, 48));
        waitTitle.setFill(Paint.valueOf("SADDLEBROWN"));
        
        waitPane.getChildren().add(waitTitle);
        
        //start
        pane.getChildren().addAll(bg, mainPane, selectR);
        
        stage.setScene(scene);
        stage.show();

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            Control c = getControl(event.getCode());
            
            //special cases for name
            if (menuState.equals("name")) {
                if (event.getCode() == KeyCode.ENTER)
                    c = Control.SELECT;
                else if (event.getCode() == KeyCode.ESCAPE)
                    c = Control.BACK;
                else if (event.getCode() == KeyCode.BACK_SPACE)
                    nameField.deletePreviousChar();
                else
                    c = Control.NULL;
            }
            
            switch (c) {
                case DOWN:
                    if (menuState.equals("main")) {
                        select++;
                        if (select > 2)
                            select = 0;
                    }
                    else if (menuState.equals("load")) {
                        select += 2;
                        if (select > 5)
                            select -= 6;
                    }
                    else if (menuState.equals("size")) {
                        select++;
                        if (select > 4)
                            select = 0;
                    }
                    else if (menuState.equals("race")) {
                        select++;
                        if (select > 7)
                            select = 0;
                    }
                    else if (menuState.equals("stat")) {
                        select++;
                        if (select > 1)
                            select = 0;
                    }
                    else if (menuState.equals("char")) {
                        if (select == 0) {
                            spriteSelect++;
                            if (spriteSelect >= sprites.length)
                                spriteSelect = 0;
                        }
                        else if (select == 1) {
                            portraitSelect++;
                            if (portraitSelect >= portraits.length)
                                portraitSelect = 0;
                        }
                    }
                    break;
                    
                case UP:
                    if (menuState.equals("main")) {
                        select--;
                        if (select < 0)
                            select = 2;
                    }
                    else if (menuState.equals("load")) {
                        select -= 2;
                        if (select < 0)
                            select += 6;
                    }
                    else if (menuState.equals("size")) {
                        select--;
                        if (select < 0)
                            select = 4;
                    }
                    else if (menuState.equals("race")) {
                        select--;
                        if (select < 0)
                            select = 7;
                    }
                    else if (menuState.equals("stat")) {
                        select--;
                        if (select < 0)
                            select = 1;
                    }
                    else if (menuState.equals("char")) {
                        if (select == 0) {
                            spriteSelect--;
                            if (spriteSelect < 0)
                                spriteSelect = sprites.length - 1;
                        }
                        else if (select == 1) {
                            portraitSelect--;
                            if (portraitSelect < 0)
                                portraitSelect = portraits.length - 1;
                        }
                    }
                    break;
                    
                case RIGHT:
                    if (menuState.equals("load")) {
                        select += select % 2 == 0 ? 1 : -1;
                    }
                    else if (menuState.equals("char")) {
                        select++;
                        if (select > 1)
                            select = 0;
                    }
                    break;
                    
                case LEFT:
                    if (menuState.equals("load")) {
                        select += select % 2 == 0 ? 1 : -1;
                    }
                    else if (menuState.equals("char")) {
                        select--;
                        if (select < 0)
                            select = 1;
                    }
                    break;
                    
                case SELECT:
                    if (menuState.equals("main")) {
                        //new game: default very large
                        if (select == 0)
                            menuState = "size";
                        //load game
                        else if (select == 1)
                            menuState = "load";
                        //exit game
                        else if (select == 2)
                            System.exit(0);
                    }
                    else if (menuState.equals("load")) {
                        if (!saveInfo[select][1].getText().equals("")) {
                            System.out.println(select);
                            loadGame(select);
                        }
                    }
                    else if (menuState.equals("size")) {
                        ngSize = select;
                        menuState = "race";
                    }
                    else if (menuState.equals("race")) {
                        ngRace = raceT[select].getText();
                        menuState = "stat";
                    }
                    else if (menuState.equals("stat")) {
                        if (select == 0) {
                            VIT = (int)(Math.random()*20+1);
                            INT = (int)(Math.random()*20+1);
                            STR = (int)(Math.random()*20+1);
                            WIS = (int)(Math.random()*20+1);
                            LUK = (int)(Math.random()*20+1);
                            CHA = (int)(Math.random()*20+1);
                        }
                        else if (select == 1) {
                            ngVIT = VIT;
                            ngINT = INT;
                            ngSTR = STR;
                            ngWIS = WIS;
                            ngLUK = LUK;
                            ngCHA = CHA;
                            menuState = "char";
                        }
                    }
                    else if (menuState.equals("char")) {
                        ngSprite = spritePaths[spriteSelect];
                        ngPortrait = portraitPaths[portraitSelect];
                        menuState = "name";
                    }
                    else if (menuState.equals("name")) {
                        ngName = nameField.getText();
                        //random name
                        if (ngName.equals("")) {
                            ngName = Character.randomName();
                        }
                        menuState = "sum";
                    }
                    else if (menuState.equals("sum")) {
                        pane.getChildren().remove(sumPane);
                        pane.getChildren().add(waitPane);
                        startNewGame(ngSize, ngVIT, ngINT, ngSTR, ngWIS, ngLUK, 
                                ngCHA, ngRace, ngName, ngSprite, ngPortrait);
                    }
                    select = 0;
                    break;
                    
                case BACK:
                    if (!menuState.equals("main"))
                        select = 0;
                    if (menuState.equals("load"))
                        menuState = "main";
                    else if (menuState.equals("size"))
                        menuState = "main";
                    else if (menuState.equals("race"))
                        menuState = "size";
                    else if (menuState.equals("stat"))
                        menuState = "race";
                    else if (menuState.equals("char"))
                        menuState = "stat";
                    else if (menuState.equals("name"))
                        menuState = "char";
                    else if (menuState.equals("sum"))
                        menuState = "name";
                    break;
                    
                case TOGGLE:
                    if (menuState.equals("main")) {
                        startInMapController(null);
                        IMController.newLocation(new Point(-1, -1), "cave");
                        IMController.passControl(new Point(-1, -1));
                    }
                    else if (menuState.equals("name")) {
                        menuState = "sum";
                    }
                    break;
                    
                case ESC:
                    if (menuState.equals("main")) {
                        startOverworldController(1000, null);
                        startInMapController(null);
                    }
                    
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
            case "load":
                if(pane.getChildren().remove(mainPane)) {
                    selectR.setWidth(screenWidth / 3);
                    selectR.setHeight(screenHeight / 5);
                    pane.getChildren().add(loadPane);
                }
                selectR.relocate(screenWidth/8+select%2*screenWidth*2/5, 
                        screenHeight/10+Math.floor(select/2)*screenHeight/4);
                break;
                
            case "main":
                if(pane.getChildren().removeAll(loadPane, sizePane)) {
                    selectR.setWidth(screenWidth / 2);
                    selectR.setHeight(screenHeight / 15);
                    pane.getChildren().add(mainPane);
                }
                selectR.relocate(screenWidth/4, screenHeight*(8+select)/12-screenHeight/20);
                break;
                
            case "size":
                if(pane.getChildren().removeAll(mainPane, racePane)) {
                    selectR.setWidth(screenWidth / 2);
                    selectR.setHeight(screenHeight / 15);
                    pane.getChildren().add(sizePane);
                }
                selectR.relocate(screenWidth/4, screenHeight*5/11 + select*screenHeight/12);
                break;
                
            case "race":
                if(pane.getChildren().removeAll(sizePane, statPane)) {
                    selectR.setWidth(screenWidth / 4);
                    selectR.setHeight(screenHeight / 15);
                    pane.getChildren().add(racePane);
                }
                switch(select) {
                    case 0: //human
                        raceDes.setText("Humans, though not the strongest of all races,"
                                + " have established a significant presence through their"
                                + " ingenuity and perseverance. While not inherently"
                                + " strong in any aspect, they have the ability to"
                                + " adapt to many situations and have high potential.");
                        raceStats.setText("VIT: Good        INT: Moderate\n"
                                        + "STR: Good        WIS: Good\n"
                                        + "LUK: Moderate    CHA: Good");
                        break;
                    case 1: //elf
                        raceDes.setText("Elves are commonly known to stay in thier own"
                                + " communities, often refusing contact with other races."
                                + " Their longevitude allowed for a long history of study"
                                + " in magic, in which they have a high aptitude.");
                        raceStats.setText("VIT: Good        INT: Great\n"
                                        + "STR: Okay        WIS: Great\n"
                                        + "LUK: Okay        CHA: Bad");
                        break;
                    case 2: //dwarf
                        raceDes.setText("Dwarves, though short, have sturdy builds and"
                                + " have withstood many hardships throughout their lives.");
                        raceStats.setText("VIT: Great       INT: Okay\n"
                                        + "STR: Good        WIS: Okay\n"
                                        + "LUK: Good        CHA: Moderate");
                        break;
                    case 3: //orc
                        raceDes.setText("Orcs are nomadic and move from place to place,"
                                + " doing whatever they can to survive, meaning they often"
                                + " steal from others and pillage settlements.");
                        raceStats.setText("VIT: Great       INT: Okay\n"
                                        + "STR: Great       WIS: Okay\n"
                                        + "LUK: Moderate    CHA: Bad");
                        break;
                    case 4: //fairy
                        raceDes.setText("Fairies are generally frail due to their small"
                                + " stature. However, their strong relationship with"
                                + " nature allows them to harness a significant amount"
                                + " of magical energy.");
                        raceStats.setText("VIT: Okay        INT: Great\n"
                                        + "STR: Bad         WIS: Great\n"
                                        + "LUK: Good        CHA: Good");
                        break;
                    case 5: //half-beast
                        raceDes.setText("Half-beasts are offspring of a humanoid and "
                                + "a beast, creating some sort of human with the "
                                + "traits of a monster.");
                        raceStats.setText("VIT: Great       INT: Moderate\n"
                                        + "STR: Good        WIS: Good\n"
                                        + "LUK: Good        CHA: Bad");
                        break;
                    case 6: //mutant
                        raceDes.setText("Some failed experiment of a mad scientist.");
                        raceStats.setText("VIT: Good        INT: Good\n"
                                        + "STR: Great       WIS: Moderate\n"
                                        + "LUK: Okay        CHA: Moderate");
                        break;
                    case 7: //skeleton
                        raceDes.setText("Because Skeletons are entirely made up of"
                                + " bones, they aren't really good at anything. Spooky.");
                        raceStats.setText("VIT: Bad         INT: Bad\n"
                                        + "STR: Bad         WIS: Bad\n"
                                        + "LUK: Bad         CHA: Bad");
                        break;
                }
                selectR.relocate(screenWidth/8, screenHeight/5 + select*screenHeight/12);
                break;
                
            case "stat":
                if (pane.getChildren().removeAll(racePane, charPane)) {
                    selectR.setWidth(screenWidth / 4);
                    selectR.setHeight(screenHeight / 8);
                    pane.getChildren().add(statPane);
                }
                statRolls[0].setText("VIT: " + VIT);
                statRolls[1].setText("INT: " + INT);
                statRolls[2].setText("STR: " + STR);
                statRolls[3].setText("WIS: " + WIS);
                statRolls[4].setText("LUK: " + LUK);
                statRolls[5].setText("CHA: " + CHA);
                selectR.relocate(screenWidth/8, screenHeight*2/(4-select)-screenHeight/12);
                break;
                
            case "char":
                if (pane.getChildren().removeAll(statPane, namePane)) {
                    selectR.setWidth(screenHeight / 3);
                    selectR.setHeight(screenHeight / 3);
                    pane.getChildren().add(charPane);
                }
                sprite.setImage(sprites[spriteSelect]);
                portrait.setImage(portraits[portraitSelect]);
                if (select == 0)
                    selectR.relocate(screenWidth/4-screenHeight/24, screenHeight*7/24);
                else if (select == 1)
                    selectR.relocate(screenWidth*3/4-screenHeight*7/24, screenHeight*7/24);
                break;
                
            case "name":
                if (pane.getChildren().removeAll(charPane, sumPane)) {
                    selectR.setWidth(screenWidth / 3);
                    selectR.setHeight(screenHeight / 8);
                    pane.getChildren().add(namePane);
                }
                selectR.relocate(screenWidth/3, screenHeight*7/15);
                break;
                
            case "sum":
                if (pane.getChildren().remove(namePane)) {
                    selectR.setWidth(0);
                    pane.getChildren().add(sumPane);
                    sumSpr.setImage(sprite.getImage());
                    sumPor.setImage(portrait.getImage());
                    sumChar.setText(ngName + "\nLVL 1 " + ngRace);
                    sumStats.setText("VIT: " + ngVIT + "\nINT: " + ngINT + "\nSTR: " + ngSTR 
                            + "\nWIS: " + ngWIS + "\nLUK: " + ngLUK + "\nCHA: "+ ngCHA);
                    sumEqp.setText("Starting Weapon:\nSharp Stick\nStarting Armor:\nRags"
                            + "\nStarting Item:\nLucky Coin");
                }
                break;
        }
    }
    
    //start new game
    private void startNewGame(int size, int VIT, int INT, int STR, int WIS, int LUK, 
            int CHA, String race, String name, String sprite, String portrait) {
        switch(size) {
            case 0: startOverworldController(100, null); break;
            case 1: startOverworldController(250, null); break;
            case 2: startOverworldController(500, null); break;
            case 3: startOverworldController(750, null); break;
            case 4: startOverworldController(1000, null); break;
        }
        startInMapController(VIT, INT, STR, WIS, LUK, CHA, race, name, sprite, portrait);

        System.out.println("All threads started");
    }
    
    //load a game
    private void loadGame(int slot) {
        try {
            System.out.println(listOfFiles[slot].getName().split("\\.")[0]);
            Object[] models = loadModel(listOfFiles[slot].getName().split("\\.")[0]);
            startOverworldController(-1, (OverworldModel) models[0]);
            startInMapController((InMapModel) models[1]);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //start overworld controller
    private void startOverworldController(int mapSize, OverworldModel overworldModel) {
        //if controllers need to talk, initialize objects and run instead of instance of new class
        if (overworldModel == null)
            overworldController = new OverworldController(this, mapSize);
        else
            overworldController = new OverworldController(this, overworldModel);

        Thread overworldThread = new Thread(overworldController);
        overworldThread.setDaemon(true);
        overworldThread.run();
    }
    
    //start inmap controller for new game
    private void startInMapController(int VIT, int INT, int STR, int WIS, int LUK, 
            int CHA, String race, String name, String sprite, String portrait) {
        IMController = new InMapController(this, VIT, INT, STR, WIS, LUK, 
                CHA, race, name, sprite, portrait);
        Thread inmapThread = new Thread(IMController);
        inmapThread.setDaemon(true);

        inmapThread.run();
    }

    //start inmap controller with loaded model
    private void startInMapController(InMapModel inMapModel) {
        if (inMapModel == null)
            IMController = new InMapController(this);
        else 
            IMController = new InMapController(this, inMapModel);
        Thread inmapThread = new Thread(IMController);
        inmapThread.setDaemon(true);

        inmapThread.run();
    }

    public void saveModel(int slot) throws IOException {
//        if (overworldController.getModelName() == null)
//            overworldController.setModelName(JOptionPane.showInputDialog(this, "Enter name to save game as: "));
        overworldController.setModelName("save" + slot);

        System.out.println("Saving game...");
        long start = System.currentTimeMillis();
        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream("src/saves/" 
                + overworldController.getModelName() + ".sav"));
        out.writeObject(new SaveFile("src/saves/" + overworldController.getModelName() + ".sav", IMController.getModel().getParty()[0].getName(), "/media/graphics/inmap/trump.png",
                IMController.getModel().getParty()[0].getLVL(), (double)(System.currentTimeMillis() - overworldController.getModel().getStartTime()) / 3600000d, slot));
        out.writeObject(keybindings);
        out.writeObject(overworldController.getModel());
        out.writeObject(IMController.getModel());
        out.close();
        System.out.println("Wrote successfully! Process took " +
                (System.currentTimeMillis() / 1000d - start / 1000d));
    }

    private Object[] loadModel(String slot) throws IOException, ClassNotFoundException {
        FSTObjectInput in = new FSTObjectInput(new FileInputStream("src/saves/" + slot + ".sav"));
        in.readObject(); // skips savefile
        keybindings = (HashMap<KeyCode, Control>) in.readObject();
        Object[] models = {in.readObject(), in.readObject()};
        in.close();
        return models;
        // start other threads if necessary
    }

    //load save file information
    private void loadSaveInfo(SaveFile s) {
        if (s == null)
            return;
        saveImages[s.slot] = new ImageView(new Image(s.sprite, screenHeight/8, screenHeight/8, false, false));
        saveImages[s.slot].relocate(screenWidth / 8 + s.slot % 2 * screenWidth * 2 / 5 + screenWidth / 60,
                screenHeight / 10 + Math.floor(s.slot / 2) * screenHeight / 4 + screenHeight / 40);
        saveInfo[s.slot][0] = new Text(screenWidth / 8 + (s.slot% 2) * screenWidth * 2 / 5 + screenWidth / 10,
                screenHeight / 10 + Math.floor(s.slot / 2) * screenHeight / 4 + screenHeight / 14,
                s.name + " " + s.level);
        saveInfo[s.slot][1] = new Text(screenWidth / 8 + (s.slot % 2) * screenWidth * 2 / 5 + screenWidth / 10,
                screenHeight / 10 + Math.floor(s.slot / 2) * screenHeight / 4 + screenHeight / 18 + screenHeight / 14,
                "Playtime: " + String.format("%.2f", s.playtime) + " hours");
    }

    public Object getSaveFile(int slot) {
        //File f = new File(Paths.get("/saves/save" + slot + ".sav"));
        FSTObjectInput in;
        try {
            in = new FSTObjectInput(new FileInputStream("src/saves/save" + slot + ".sav"));
            Object o = in.readObject();
            in.close();
            return o;
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            return null;
        }
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