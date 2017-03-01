/*
    Controls all interactions between non-graphical and graphical components for the Overworld

    TODO LIST - IN ORDER OF BLOCK PRIORITY
    Let player move away from non-tresspassable tile
    Settlements generate in mountains

    Design and implement Economy

    Design, implement and improve UI

    Improve design and implement improvements for Party AI's
    Player movement independent of AI's movement

    Implement lake drawing
    Design and implement factions
 */

package overworld;

import javafx.application.Platform;
import javafx.event.*;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.Control;
import main.Main;

import java.awt.*;

public class OverworldController extends Thread {

    private Main main;
    private Scene scene;

    static boolean debug = true;

    static long start; // for timing the creation of the Model and View

    private OverworldView view;
    private OverworldModel model;
    private PlayerMove playerMove;
    private Thread playerMoveThread;

    private EventHandler<MouseEvent>[][][] eventHandlers;

    public static boolean hasControl;
    public static boolean running = true;
    private boolean autopilot;

    private int clickReturnCode;
    private Point dungeonPoint;

    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private long autopilotStart;

    // constructor used when creating a new game, initializes new model
    public OverworldController(Main main) {
        this.main = main;
        start = System.currentTimeMillis();

        model = new OverworldModel();
        if (debug)
            System.out.println("Model init took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");

        view = new OverworldView(main.screenWidth, main.screenHeight, main.getPlayerSprite());

        model.createPlayer(getBaseSpeed(), "none");
        //model.genParties(getBaseSpeed());

        if (debug)
            System.out.println("Overworld init took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");
    }

    // constructor for loading game, sets model from save file to model variable
    public OverworldController(Main main, OverworldModel model) { // load game
        this.main = main;
        start = System.currentTimeMillis();

        this.model = model;
        view = new OverworldView(main.screenWidth, main.screenHeight, main.getPlayerSprite());

        if (debug)
            System.out.println("Overworld init took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");

        //model.startPartyAI();
    }

    @Override
    public void run() {
        start = System.currentTimeMillis();

        model.setMenuOpen(false);
        model.setControlsLocked(false);

        if (debug)
            System.out.println("Overworld thread started");
        setScene();

        // double backwards virtual keypress to prevent game from being frozen when the model is saved with an open menu
        try {
            Robot r = new Robot();
            r.keyPress(java.awt.event.KeyEvent.VK_X);
            r.keyRelease(java.awt.event.KeyEvent.VK_X);
            r.keyPress(java.awt.event.KeyEvent.VK_X);
            r.keyRelease(java.awt.event.KeyEvent.VK_X);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        running = true;
        hasControl = true;
    }

    //pass control back
    public void passControl() {
        hasControl = false;
        main.setStage(scene);
        model.setControlsLocked(true);
    }

    public OverworldModel getModel() {
        return model;
    }

    public long getStart() {
        return start;
    }

    private void setScene() {

        /*
            Gets scene from the View and passes it to main to be displayed
         */

        // create scene for main to display
        scene = view.initDisplay(model.getTiles(), model.getPlayer(), model.getParties(), main.screenWidth, main.screenHeight,
                view.getMapTileSize(), model.getMapSize());
        Platform.runLater(() -> main.setStage(scene)); // to update UI from non-javafx thread
        eventHandlers = new EventHandler[OverworldView.zoom * 2 + 1][OverworldView.zoom * 2 + 1][4];
        setInput(scene);
        setMouseEvents();
        if (debug)
            System.out.println("Scene creation took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");
    }

    static private float getBaseSpeed() {
        return (float) (200 / OverworldView.mapTileSize);
    }

    private void setInput(Scene scene) {

        /*
            Maps keyboard inputs to various functions, such as movement of screen
         */

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            Control key;

            int returnCode = model.process(key = main.getControl(event.getCode()), false);

            if (key == Control.R) {
                System.out.println(view.getNodeStack().size());
            }

            // Sets movement key booleans to false if they are released, to track which are pressed
            if (key == Control.UP)
                upPressed = true;
            else if (key == Control.DOWN)
                downPressed = true;
            else if (key == Control.LEFT)
                leftPressed = true;
            else if (key == Control.RIGHT)
                rightPressed = true;

            if (autopilot && System.currentTimeMillis() - autopilotStart > 1000 && key == Control.SELECT)
                autopilot = false;

//            if (key == Control.SELECT) {
//                autopilot = true;
//                autopilotStart = System.currentTimeMillis();
//            }

            if (autopilot) {
                try {
                    Robot r = new Robot();

                    if (upPressed)
                        r.keyRelease(java.awt.event.KeyEvent.VK_W);
                    if (downPressed)
                        r.keyRelease(java.awt.event.KeyEvent.VK_S);
                    if (leftPressed)
                        r.keyRelease(java.awt.event.KeyEvent.VK_A);
                    if (rightPressed)
                        r.keyRelease(java.awt.event.KeyEvent.VK_D);

                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }

            if (returnCode == -1) {
                if (!view.removePane() || view.isNodeStackEmpty())
                    model.setControlsLocked(false);
                // lock controls
            } else if (returnCode == 1 || model.getMenuOpen()) {
                // open menu or close menu
                if (!model.getMenuOpen()) {
                    model.setMenuOpen(true);
                    model.setControlsLocked(true);
                    view.addPane(main.getMenuPane(), true);
                }
                if (running && model.getMenuOpen() && main.processMenuInput(main.getControl(event.getCode()))) {
                    model.setMenuOpen(false);
                    model.setControlsLocked(false);
                    view.removePane();
                }
            } else if (returnCode == 2 && clickReturnCode == 12) {
                // enter dungeon
                hasControl = false;
                main.passControl(dungeonPoint);
            } else if (returnCode == 3)
                view.showTileBorders(true);
            else if (returnCode == 4) {
                // update view, tile change occured
                Point2D angles = model.getAngles();
                view.reDraw(angles, model.getTiles(), model.getPlayer(), model.getParties());
                setMouseEvents();
            }
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {

            Control key;

            int returnCode = model.process(key = main.getControl(event.getCode()), true);

//            System.out.println(autopilot);
//            System.out.println(key);
//            System.out.println();

            /*
                Sets movement key booleans to false if they are released, to track which are pressed
                The reason for these booleans is that only one key press can be stored at a time, so if W is pressed
                and D is subsequently pressed, and D is then released, tho W is still pressed the KEY_PRESSED event won't run
                Essentially needed for tile change detection only, since it runs only when the process function is called
                in the KEY_PRESSED or KEY_RELEASED events
             */

            if (!autopilot) {
                if (key == Control.UP)
                    upPressed = false;
                else if (key == Control.DOWN)
                    downPressed = false;
                else if (key == Control.LEFT)
                    leftPressed = false;
                else if (key == Control.RIGHT)
                    rightPressed = false;
            }

            //System.out.println("XPos: " + model.getCurrPos(0));
            //System.out.println("YPos: " + model.getCurrPos(1));

            /* if a movement key is released a "virtual" keypress is made by the Robot class to ensure that the
                KEY_PRESSED event for keys that are still pressed run
            */
            if ((upPressed || downPressed || leftPressed || rightPressed) && Control.isMovementKey(key) && !autopilot) {
                try {
                    Robot r = new Robot();
                    if (upPressed)
                        r.keyPress(java.awt.event.KeyEvent.VK_W);
                    else if (downPressed)
                        r.keyPress(java.awt.event.KeyEvent.VK_S);
                    else if (leftPressed)
                        r.keyPress(java.awt.event.KeyEvent.VK_A);
                    else if (rightPressed)
                        r.keyPress(java.awt.event.KeyEvent.VK_D);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }

            // prevents weird bugs from occurring, like pressing a null key (K for example) while releasing which causes
            // the player to keep moving even if all keys are released
            if (!autopilot) {
                try {
                    Robot r = new Robot();

                    if (!upPressed)
                        r.keyRelease(java.awt.event.KeyEvent.VK_W);
                    if (!downPressed)
                        r.keyRelease(java.awt.event.KeyEvent.VK_S);
                    if (!leftPressed)
                        r.keyRelease(java.awt.event.KeyEvent.VK_A);
                    if (!rightPressed)
                        r.keyRelease(java.awt.event.KeyEvent.VK_D);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }

            if (autopilot) {
                try {
                    Robot r = new Robot();

                    if (upPressed) {
                        //r.keyRelease(java.awt.event.KeyEvent.VK_W);
                        r.keyPress(java.awt.event.KeyEvent.VK_W);
                    }
                    if (downPressed) {
                        //r.keyRelease(java.awt.event.KeyEvent.VK_S);
                        r.keyPress(java.awt.event.KeyEvent.VK_S);
                    }
                    if (leftPressed) {
                        //r.keyRelease(java.awt.event.KeyEvent.VK_A);
                        r.keyPress(java.awt.event.KeyEvent.VK_A);
                    }
                    if (rightPressed) {
                        //r.keyRelease(java.awt.event.KeyEvent.VK_D);
                        r.keyPress(java.awt.event.KeyEvent.VK_D);
                    }
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }

            if (returnCode == 0)
                return;
            else if (returnCode == 3)
                view.showTileBorders(false);
            else if (returnCode == 4) {
                Point2D angles = model.getAngles();
                view.reDraw(angles, model.getTiles(), model.getPlayer(), model.getParties());
                setMouseEvents();
            }
        });
    }

    void setMouseEvents() {

        /*
            Maps click events to functions
         */

        for (int y = 0; y < OverworldView.zoom * 2 + 1; y++) {
            for (int x = 0; x < OverworldView.zoom * 2 + 1; x++) {

                if (eventHandlers[x][y][0] != null) {
                    view.getTileIV(x, y).removeEventHandler(MouseEvent.MOUSE_ENTERED, eventHandlers[x][y][0]);
                    view.getTileIV(x, y).removeEventHandler(MouseEvent.MOUSE_EXITED, eventHandlers[x][y][1]);
                    view.getTileIV(x, y).removeEventHandler(MouseEvent.MOUSE_CLICKED, eventHandlers[x][y][2]);
                }
                if (eventHandlers[x][y][3] != null)
                    view.getTileIV(x, y).removeEventHandler(MouseEvent.MOUSE_CLICKED, eventHandlers[x][y][3]);

                final Party p = model.getPlayer();
                final int yPos = (p.getTileY() + y - OverworldView.zoom < 0 ? (p.getTileY() + y - OverworldView.zoom >= model.getMapSize() ? model.getMapSize() - 1 : 0) : p.getTileY() + y - OverworldView.zoom);
                final int xPos = (p.getTileX() + x - OverworldView.zoom < 0 ? (p.getTileX() + x - OverworldView.zoom >= model.getMapSize() ? model.getMapSize() - 1 : 0) : p.getTileX() + x - OverworldView.zoom);
                final int arrX = x, arrY = y;

                // mouse hovering events to show or hide settlement banners
                if (model.getTiles()[xPos][yPos].settlementTile != null) {
                    view.getTileIV(arrX, arrY).addEventHandler(MouseEvent.MOUSE_ENTERED, eventHandlers[x][y][0] = event -> {
                        // if mouse is on a settlement tile, show banner for settlement
                        if (view.isNodeStackEmpty()) {
                            view.genBanner(model.getTiles()[xPos][yPos], model.getPlayer(), arrX, arrY);
                            view.addPane(view.getBanner(), true);
                        }
                    });
                    view.getTileIV(arrX, arrY).addEventHandler(MouseEvent.MOUSE_EXITED, eventHandlers[x][y][1] = event -> {
                        // if mouse leaves settlement tile, hide banner
                        if (!view.isNodeStackEmpty() && view.getNodeStack().size() == 1) // only remove top pane on stack if that pane is the banner
                            view.removePane();
                    });
                    // mouse click event for opening menu with settlement info
                    view.getTileIV(arrX, arrY).addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandlers[arrX][arrY][2] = event -> {
                        if (event.getButton() == MouseButton.PRIMARY && model.getPlayer().getTileX() - xPos == 0 && model.getPlayer().getTileY() - yPos == 0)
                            view.showSettlementInfo(model.getTiles()[xPos][yPos].settlementTile, arrX, arrY);
                    });
                }

                view.getTileIV(arrX, arrY).addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandlers[x][y][3] = event -> {

                    // shows tile information when tile is clicked and handles all buttons in windows opened

                    clickReturnCode = model.process(event, xPos, yPos);
                    if (clickReturnCode == 11) {
                        view.showSettlementInfo(model.getTiles()[xPos][yPos].settlementTile, arrX, arrY);
                    } else if (clickReturnCode == 12) {
                        dungeonPoint = new Point(xPos, yPos);
                        main.newLocation(dungeonPoint, model.getTiles()[xPos][yPos].inMapTile.inmapType.toLowerCase());
                        view.showInMapInfo(main.getLocationName(dungeonPoint), main.getDifficulty(dungeonPoint));
                    } else if (clickReturnCode == 21) {
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