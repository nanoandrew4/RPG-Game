/*
    Controls all interactions between non-graphical and graphical components for the Overworld

    TODO LIST - IN ORDER OF BLOCK PRIORITY
    Fix View scrolling in SW direction - Seems to have been self-fixed... Nov 17 2016
    Fix mouse events not being set properly - Dec 7 2016
    Player movement processed by the model completely - Nov 28 2016
    Player moves when tile is clicked on - Partial Nov 30 2016
    Player movement independent of AI's movement
    Fix Settlement banners
    Can't move on to non tresspasable tiles - Nov 30 2016 - Needs fixing
    Fix world gen pls... Coastline gen, outOfBounds

    Design and implement Economy

    Design, implement and improve UI

    Improve design and implement improvements for Party AI's
    Improve tile change detection

    Implement lake drawing
    Design and implement factions
 */

package overworld;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.Control;
import main.Main;

import java.awt.*;
import java.util.Scanner;

public class OverworldController implements Runnable {

    private Main main;

    private Scene scene;
    private boolean controlsLocked = false;
    private boolean menuOpen = false;

    private long start; // for timing the creation of the Model and View

    private OverworldView view;
    private OverworldModel model;
    private PlayerMove playerMove;
    private Thread playerMoveThread;

    private Control horizontalDir = Control.NULL;
    private Control verticalDir = Control.NULL;

    // constructor used when creating a new game
    public OverworldController(Main main) {
        this.main = main;
        start = System.currentTimeMillis();

        model = new OverworldModel();
        System.out.println("Model init took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");

        view = new OverworldView(main.screenWidth, main.screenHeight);

        model.createPlayer(getBaseSpeed(), "none");
        //model.genParties(getBaseSpeed());

        System.out.println("Overworld init took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");
    }

    // constructor for loading game, sets model from save file to model variable
    public OverworldController(Main main, OverworldModel model) { // load game
        this.main = main;
        start = System.currentTimeMillis();

        this.model = model;
        view = new OverworldView(main.screenWidth, main.screenHeight);
        model.createPlayer(getBaseSpeed(), "none");

        System.out.println("Overworld init took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");

        model.startPartyAI();
    }

    @Override
    public void run() {
        start = System.currentTimeMillis();
        System.out.println("Overworld thread started");
        setScene();
    }

    //pass control back
    public void passControl() {
        main.setStage(scene);
        controlsLocked = false;
    }

    public String getModelName() {
        return model.getModelName();
    }

    public void setModelName(String name) {
        model.setModelName(name);
    }

    public OverworldModel getModel() {
        return model;
    }

    private void setScene() {

        /*
            Gets scene from the View and passes it to main to be displayed
         */

        // create scene for main to display
        scene = view.initDisplay(model.getTiles(), model.getPlayer(), model.getParties(), main.screenWidth, main.screenHeight,
                view.getMapTileSize(), model.getMapSize());
        setMouseEvents();
        setInput(scene);
        Platform.runLater(() -> main.setStage(scene)); // to update UI from non-javafx thread
        System.out.println("Scene creation took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");
    }

    private float getBaseSpeed() {
        return (float) (100 / view.getMapTileSize());
    }

    private void setInput(Scene scene) {

        /*
            Maps keyboard inputs to various functions, such as movement of screen
         */

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            model.getPlayer().setxOffset(view.getPlayerXOffset());
            model.getPlayer().setyOffset(view.getPlayerYOffset());

            Control key = main.getControl(event.getCode());

            // remove one menu from scene
            if (key == Control.BACK && !view.paneStack.empty()) {
                view.removePane(view.paneStack.pop());
                if (view.paneStack.empty())
                    controlsLocked = false;
            }

            // if looking at a menu, lock controls
            if (controlsLocked)
                return;

            // open menu
            if (key == Control.BACK || key == Control.MENU || key == Control.OPENCHAR || key == Control.OPENINV || key == Control.OPENNOTES || key == Control.OPENOPTIONS || key == Control.OPENPARTY) {
                if (!menuOpen)
                    view.setMenuPane(main.getMenuPane());
                menuOpen = true;
            }

            // close menu
            if (menuOpen) {
                if (main.processMenuInput(key)) {
                    menuOpen = false;
                    view.removePane(main.getMenuPane());
                }
                return;
            }

            // when player touches key, stops automatic moving through map (moving by clicking on a tile)
            model.getPlayer().setPath(null);

            //System.out.println("XPos: " + model.getCurrPos(0));
            //System.out.println("YPos: " + model.getCurrPos(1));
            //System.out.println("Tile size: " + model.getMapTileSize());

            // position on getTiles() needs work

            // test code for AI movement
            if (event.getCode() == KeyCode.P) {
                //model.getParty(0).nextMove(model.getBooleanMap(), model.getParties());
                model.getParty(0).nextMove(model.getTiles(), model.getBooleanMap(), model.getParties());
            }

            // teleporting, testing only
            if (key == Control.T) { // TEST CODE
                System.out.print("Enter coords to move to: ");
                Scanner scanner = new Scanner(System.in);
                model.setCurrPos(0, scanner.nextInt());
                model.setCurrPos(1, scanner.nextInt());
                setScene();
                System.out.println("New coords are: " + model.getCurrPos(0) + ", " + model.getCurrPos(1));
            }

            // shows tile borders
            if (key == Control.ALT) {
                for (int y = -OverworldView.zoom; y < OverworldView.zoom; y++) {
                    for (int x = -OverworldView.zoom; x < OverworldView.zoom; x++) {
                        if (view.imageViews[x + OverworldView.zoom][y + OverworldView.zoom][1] == null)
                            continue;
                        view.imageViews[x + OverworldView.zoom][y + OverworldView.zoom][1].setVisible(true);
                    }
                }
            }

            // movement on map processing
            if (key == Control.LEFT || key == Control.RIGHT || key == Control.UP || key == Control.DOWN) {
                //System.out.println(key);

                if (key == Control.UP || key == Control.DOWN)
                    verticalDir = key;
                else
                    horizontalDir = key;

                model.getPlayer().setSpeedY(model.getPlayer().getSpeedY(verticalDir, model.getTiles()));
                model.getPlayer().setSpeedX(model.getPlayer().getSpeedX(horizontalDir, model.getTiles()));

                // detect if player has moved tiles, and if so, add and remove rows appropriately
                if (model.getPlayer().detectTileChange(view.getMapTileSize(), true)) {
                    double angles[] = model.getPlayer().calcAngles(model.getPlayer().getxOffset(), model.getPlayer().getyOffset(), view.getMapTileSize());
                    if (angles[0] > 22.5)
                        Platform.runLater(() -> view.addColumn(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), false));
                    if (angles[0] < -22.5)
                        Platform.runLater(() -> view.addRow(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), false));
                    if (angles[1] > 22.5)
                        Platform.runLater(() -> view.addRow(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), true));
                    if (angles[1] < -22.5)
                        Platform.runLater(() -> view.addColumn(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), true));
                    if (Math.abs(view.getPlayerXOffset()) > view.getMapTileSize() / 2) {
                        if (view.getPlayerXOffset() > 0) {
                            Platform.runLater(() -> view.addColumn(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), true));
                            Platform.runLater(() -> view.addRow(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), true));
                        } else {
                            Platform.runLater(() -> view.addColumn(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), false));
                            Platform.runLater(() -> view.addRow(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), false));
                        }
                    }

                    setMouseEvents();
                }
            }
            event.consume();
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {

            model.getPlayer().setxOffset(view.getPlayerXOffset());
            model.getPlayer().setyOffset(view.getPlayerYOffset());

            Control key = main.getControl(event.getCode());

            //System.out.println("XPos: " + model.getCurrPos(0));
            //System.out.println("YPos: " + model.getCurrPos(1));

            if (controlsLocked)
                return;

            // hide borders
            if (key == Control.ALT) {
                for (int y = -OverworldView.zoom; y < OverworldView.zoom; y++) {
                    for (int x = -OverworldView.zoom; x < OverworldView.zoom; x++) {
                        if (view.imageViews[x + OverworldView.zoom][y + OverworldView.zoom][1] == null)
                            continue;
                        view.imageViews[x + OverworldView.zoom][y + OverworldView.zoom][1].setVisible(false);
                    }
                }
            }

            // process keyboard release of movement keys
            if (key == Control.RIGHT || key == Control.LEFT || key == Control.UP || key == Control.DOWN) {
                if (model.getCurrPos(0) <= 0 || model.getCurrPos(0) >= model.getMapSize() || model.getCurrPos(1) <= 0 || model.getCurrPos(1) >= model.getMapSize())
                    return;

                // set speed to 0 to stop movement
                if (key == Control.RIGHT || key == Control.LEFT) {
                    model.getPlayer().setSpeedX(0);
                    horizontalDir = Control.NULL;
                } else {
                    model.getPlayer().setSpeedY(0);
                    verticalDir = Control.NULL;
                }

                if (model.getPlayer().detectTileChange(view.getMapTileSize(), true)) {
                    double angles[] = model.getPlayer().calcAngles(model.getPlayer().getxOffset(), model.getPlayer().getyOffset(), view.getMapTileSize());
                    if (angles[0] > 22.5)
                        Platform.runLater(() -> view.addColumn(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), false));
                    if (angles[0] < -22.5)
                        Platform.runLater(() -> view.addRow(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), false));
                    if (angles[1] > 22.5)
                        Platform.runLater(() -> view.addRow(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), true));
                    if (angles[1] < -22.5)
                        Platform.runLater(() -> view.addColumn(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), true));
                    if (Math.abs(view.getPlayerXOffset()) > view.getMapTileSize() / 2) {
                        if (view.getPlayerXOffset() > 0) {
                            Platform.runLater(() -> view.addColumn(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), true));
                            Platform.runLater(() -> view.addRow(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), true));
                        } else {
                            Platform.runLater(() -> view.addColumn(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), false));
                            Platform.runLater(() -> view.addRow(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), false));
                        }
                    }

                    setMouseEvents();
                }

            }
            event.consume();
        });
    }

    void setMouseEvents() {

        /*
            Maps click events to functions
         */

        //System.out.println("Setting click events");
        for (int y = -OverworldView.zoom; y <= OverworldView.zoom; y++) {
            for (int x = -OverworldView.zoom; x <= OverworldView.zoom; x++) {
                final Party p = model.getPlayer();
                final int yPos = (p.getTileY() + y < 0 ? (p.getTileY() + y >= model.getMapSize() ? model.getMapSize() - 1 : 0) : p.getTileY() + y);
                final int xPos = (p.getTileX() + x < 0 ? (p.getTileX() + x >= model.getMapSize() ? model.getMapSize() - 1 : 0) : p.getTileX() + x);

                // mouse hovering events to show or hide settlement banners
                if (model.getTiles()[xPos][yPos].settlementTile != null) {
                    view.imageViews[x + OverworldView.zoom][y + OverworldView.zoom][0].addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                        // if mouse is on a settlement tile, show banner for settlement
                        model.getTiles()[xPos][yPos].banner.setVisible(true);
                    });
                    view.imageViews[x + OverworldView.zoom][y + OverworldView.zoom][0].addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                        // if mouse leaves settlement tile, hide banner
                        model.getTiles()[xPos][yPos].banner.setVisible(false);
                    });
                    model.getTiles()[xPos][yPos].banner.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                        // if mouse is on banner, show banner
                        model.getTiles()[xPos][yPos].banner.setVisible(true);
                    });
                    model.getTiles()[xPos][yPos].banner.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                        // if mouse leaves banner, hide banner
                        model.getTiles()[xPos][yPos].banner.setVisible(false);
                    });
                }

                view.imageViews[x + OverworldView.zoom][y + OverworldView.zoom][0].addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

                    // shows tile information when tile is clicked and handles all buttons in windows opened

                    if (controlsLocked)
                        return;

                    System.out.println("Tile type: " + model.getTiles()[xPos][yPos].type);

                    if (event.getButton() == MouseButton.PRIMARY) {
                        // processing mouse events for left click
                        System.out.println("Tile @ pos " + xPos + ", " + yPos);
                        if (model.getTiles()[xPos][yPos].type.equalsIgnoreCase("Settlement") || model.getTiles()[xPos][yPos].type.equalsIgnoreCase("InMap")) {
                            // display additional menus if dungeon or settlement
                            if (model.getTiles()[xPos][yPos].type.equalsIgnoreCase("Settlement")) // will change
                                view.showSettlementInfo(model.getTiles()[xPos][yPos].settlementTile);
                            else if (model.getTiles()[xPos][yPos].type.equalsIgnoreCase("InMap")) {
                                main.newLocation(new Point(xPos, yPos), model.getTiles()[xPos][yPos].inMapTile.inmapType.toLowerCase());
                                view.showInMapInfo(main.getLocationName(new Point(xPos, yPos)),
                                        main.getDifficulty(new Point(xPos, yPos)));
                            }
                            // lock controls to prevent moving on world map while scrolling menu
                            controlsLocked = true;
                        }

                        view.manageCity.setOnAction(event1 -> {
                            // show city politics elements

                            view.removePane(view.paneStack.pop());
                            view.showCityManagement(model.getTiles()[xPos][yPos].settlementTile);
                        });

                        view.enterDungeon.setOnAction(event1 -> main.passControl(new Point(xPos, yPos)));
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        boolean startThread = playerMoveThread == null;
                        // processing mouse event for right click, moves player by itself to target tile
                        model.getPlayer().detectTileChange(OverworldView.mapTileSize, true);
                        if (startThread) {
                            playerMove = new PlayerMove(this, model, view);
                            playerMove.setPos(xPos, yPos);
                            playerMoveThread = new Thread(playerMove);
                            playerMoveThread.start();
                        } else {
                            playerMove.setPos(xPos, yPos);
                            playerMoveThread.run();
                        }
                    }
                });
            }
        }
    }
}