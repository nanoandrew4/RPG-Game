/*
    Controls all interactions between non-graphical and graphical components for the Overworld

    TODO LIST - IN ORDER OF BLOCK PRIORITY
    Fix View scrolling in SW direction - Seems to have been self-fixed... Nov 17 2016
    Fix mouse events not being set properly - Dec 7 2016
    Player movement processed by the model completely - Nov 28 2016
    Player moves when tile is clicked on - Partial Nov 30 2016
    Fix Settlement banners
    Fix tile change detection
    Fixed major bug in view regarding tile size
    Can't move on to non tresspasable tiles - Nov 30 2016 - Needs fixing - Depends on detectTileChange() being fixed to work
    Fix world gen pls... Coastline gen, outOfBounds

    Design and implement Economy

    Design, implement and improve UI

    Improve design and implement improvements for Party AI's
    Player movement independent of AI's movement

    Implement lake drawing
    Design and implement factions
 */

package overworld;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.Main;

import java.awt.*;

public class OverworldController implements Runnable {

    private Main main;

    private Scene scene;

    private long start; // for timing the creation of the Model and View

    private OverworldView view;
    private OverworldModel model;
    private PlayerMove playerMove;
    private Thread playerMoveThread;

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
        model.setControlsLocked(true);
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
        Platform.runLater(() -> main.setStage(scene)); // to update UI from non-javafx thread
        setInput(scene);
        setMouseEvents();
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

            int returnCode = model.process(main.getControl(event.getCode()), false);

            if (returnCode == -1) {
                if (!view.removePane())
                    model.setControlsLocked(false);
                // lock controls
            } else if (returnCode == 1 || model.getMenuOpen()) {
                if (!model.getMenuOpen()) {
                    model.setMenuOpen(true);
                    model.setControlsLocked(true);
                    view.addPane(main.getMenuPane(), true);
                }
                if (model.getMenuOpen() && main.processMenuInput(main.getControl(event.getCode()))) {
                    model.setMenuOpen(false);
                    model.setControlsLocked(false);
                    view.removePane();
                }
            } else if (returnCode == 2)
                view.showTileBorders(true);
            else if (returnCode == 3) {
                view.reDraw(model.getAngles(), model.getTiles(), model.getPlayer(), model.getParties());
                setMouseEvents();
            }

            event.consume();
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {

            model.getPlayer().setxOffset(view.getPlayerXOffset());
            model.getPlayer().setyOffset(view.getPlayerYOffset());

            int returnCode = model.process(main.getControl(event.getCode()), true);

            //System.out.println("XPos: " + model.getCurrPos(0));
            //System.out.println("YPos: " + model.getCurrPos(1));

            if (returnCode == 0)
                return;
            else if (returnCode == 2)
                view.showTileBorders(false);
            else if (returnCode == 3) {
                view.reDraw(model.getAngles(), model.getTiles(), model.getPlayer(), model.getParties());
                setMouseEvents();
            }

            event.consume();
        });
    }

    void setMouseEvents() {

        /*
            Maps click events to functions
         */

        //System.out.println("Setting click events");
        for (int y = 0; y <= OverworldView.zoom * 2; y++) {
            for (int x = 0; x <= OverworldView.zoom * 2; x++) {
                final Party p = model.getPlayer();
                final int yPos = (p.getTileY() + y - OverworldView.zoom < 0 ? (p.getTileY() + y - OverworldView.zoom >= model.getMapSize() ? model.getMapSize() - 1 : 0) : p.getTileY() + y - OverworldView.zoom);
                final int xPos = (p.getTileX() + x - OverworldView.zoom < 0 ? (p.getTileX() + x - OverworldView.zoom >= model.getMapSize() ? model.getMapSize() - 1 : 0) : p.getTileX() + x - OverworldView.zoom);
                final int arrX = x, arrY = y;

                // mouse hovering events to show or hide settlement banners
                if (model.getTiles()[xPos][yPos].settlementTile != null ) {
                    view.getTileIV(arrX, arrY).addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                        // if mouse is on a settlement tile, show banner for settlement
                        view.showBanner(arrX, arrY, true);
                    });
                    view.getTileIV(arrX, arrY).addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                        // if mouse leaves settlement tile, hide banner
                        view.showBanner(arrX, arrY, false);
                    });
                    view.getBanner(arrX, arrY).addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                        // if mouse is on banner, show banner
                        view.showBanner(arrX, arrY, true);
                    });
                    view.getBanner(arrX, arrY).addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                        // if mouse leaves banner, hide banner
                        view.showBanner(arrX, arrY, false);
                    });
                    view.getBanner(arrX, arrY).addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                        if (event.getButton() == MouseButton.PRIMARY && model.getPlayer().getTileX() - xPos == 0 && model.getPlayer().getTileY() - yPos == 0)
                            view.showSettlementInfo(model.getTiles()[xPos][yPos].settlementTile, arrX, arrY);
                    });
                }

                view.getTileIV(arrX, arrY).addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

                    // shows tile information when tile is clicked and handles all buttons in windows opened

                    int returnCode = model.process(event, xPos, yPos);
                    if (returnCode == 11) {
                        view.showSettlementInfo(model.getTiles()[xPos][yPos].settlementTile, arrX, arrY);
                    } else if (returnCode == 12) {
                        main.newLocation(new Point(xPos, yPos), model.getTiles()[xPos][yPos].inMapTile.inmapType.toLowerCase());
                        view.showInMapInfo(main.getLocationName(new Point(xPos, yPos)), main.getDifficulty(new Point(xPos, yPos)));
                    } else if (returnCode == 21) {
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