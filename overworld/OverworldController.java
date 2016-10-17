/*
    Controls all interactions between non-graphical and graphical components for the Overworld

    TODO: IMPROVE MVC MODEL ESPECIALLY CONTROLLER PART
    TODO: CLEAN UP AND ORGANIZE CODE NEATLY
 */

package overworld;

import main.Control;
import main.Main;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import java.awt.Point;

import java.util.Scanner;

public class OverworldController implements Runnable {

    Main main;
    
    private Scene scene;
    private boolean controlsLocked = false;

    private long start; // for timing the creation of the Model and View

    private OverworldView view;
    private OverworldModel model;

    double prevRAngle = 0, prevLAngle = 0;

    public OverworldController(Main main, int mapSize, boolean newGame) {
        this.main = main;
        start = System.currentTimeMillis();
        model = new OverworldModel(mapSize, newGame, main.dbManager);

        System.out.println("Model init took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");

        view = new OverworldView(model.getMapSize(), main.screenWidth, main.screenHeight);

        System.out.println("Overworld init took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");
    }

    @Override
    public void run() {
        start = System.currentTimeMillis();
        System.out.println("Overworld thread started");
        setScene();
    }
    
    //temporary method to pass control
    public void passControl() {
        main.setStage(scene);
        controlsLocked = false;
    }

    private void setScene() {

        /*
            Gets scene from the View and passes it to main to be displayed
         */

        scene = view.initDisplay(model.getTiles(), main.screenWidth, main.screenHeight,
                view.getMapTileSize(), model.getCurrPos(), model.getStartPos(), model.getMapSize());
        setMouseEvents();
        setInput(scene);
        Platform.runLater(() -> main.setStage(scene)); // to update UI from non-javafx thread
        System.out.println("Scene creation took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");
    }

    private double[] calcAngles(double xOffset, double yOffset) {

        /*
            Calculates and returns angles from current position on tile to leftmost and rightmost point
         */

        double[] angles = new double[2]; // stores langle, rangle

        angles[0] = Math.toDegrees(Math.atan(yOffset / (xOffset + (view.getMapTileSize() / 2)))); // left
        angles[1] = Math.toDegrees(Math.atan(yOffset / ((view.getMapTileSize() / 2) - xOffset))); // right

        return angles;
    }

    private void detectTileChange() {

        /*
            Algorithm to detect whether there has been a change in tile (user moved off tile on to adjacent one)
            TODO: WIP
         */

        double tileXOffset = view.centerTile.screenToLocal(view.redDot.localToScreen(3, 3)).getX();
        double tileYOffset = -view.centerTile.screenToLocal(view.redDot.localToScreen(3, 3)).getY();

        //System.out.println(view.centerTile.screenToLocal(view.redDot.localToScreen(3, 3)));

        double[] angles = calcAngles(tileXOffset, tileYOffset);
        double leftAngle = angles[0];
        double rightAngle = angles[1];

        /*System.out.println("xOffset: " + tileXOffset);
        System.out.println("yOffset: " + tileYOffset);
        System.out.println("Left side angle " + leftAngle);
        System.out.println("Right side angle " + rightAngle);
        System.out.println();*/

        if (Math.abs(leftAngle) >= 22.5 || Math.abs(rightAngle) >= 22.5) { // new tile
            System.out.println("Moved tile");
            System.out.println();
            if (rightAngle >= 22.5 && prevLAngle >= -22.5) {
                view.xOffset -= view.getMapTileSize() / 2;
                view.yOffset -= view.getMapTileSize() / 4;
                model.setCurrPos(1, model.getCurrPos(1) - 1);
                view.addRow(model.getTiles(), main.screenWidth, main.screenHeight,
                       view.getMapTileSize(), model.getCurrPos(), model.getStartPos(), model.getMapSize(), true);
                prevRAngle = rightAngle;
            }
            if (rightAngle <= -22.5 && prevLAngle <= 22.5) {
                view.xOffset -= view.getMapTileSize() / 2;
                view.yOffset += view.getMapTileSize() / 4;
                model.setCurrPos(0, model.getCurrPos(0) + 1);
                //view.addColumn(model.getTiles(), main.screenWidth, main.screenHeight, (int)(model.getZoom() * zoomMultiplier), view.getMapTileSize(), model.getCurrPos(), model.getMapSize(), true);
                prevRAngle = rightAngle;
            }
            if (leftAngle >= 22.5 && prevRAngle >= -22.5) {
                view.xOffset += view.getMapTileSize() / 2;
                view.yOffset -= view.getMapTileSize() / 4;
                model.setCurrPos(0, model.getCurrPos(0) - 1);
                //view.addColumn(model.getTiles(), main.screenWidth, main.screenHeight, (int)(model.getZoom() * zoomMultiplier), view.getMapTileSize(), model.getCurrPos(), model.getMapSize(), false);
                prevLAngle = leftAngle;
            }
            if (leftAngle <= -22.5 && prevRAngle <= 22.5) {
                view.xOffset += view.getMapTileSize() / 2;
                view.yOffset += view.getMapTileSize() / 4;
                model.setCurrPos(1, model.getCurrPos(1) + 1);
                view.addRow(model.getTiles(), main.screenWidth, main.screenHeight,
                        view.getMapTileSize(), model.getCurrPos(), model.getStartPos(), model.getMapSize(), false);
                prevLAngle = leftAngle;
            }
            //setMouseEvents();
        }
    }

    private void setInput(Scene scene) {

        /*
            Maps keyboard inputs to various functions, such as movement of screen
         */

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            KeyCode k = event.getCode();

            if (main.getControl(k) == Control.BACK && !view.paneStack.empty()) {
                view.removePane(view.paneStack.pop());
                if(view.paneStack.empty())
                    controlsLocked = false;
            }

            if (controlsLocked)
                return;

            //System.out.println("XPos: " + model.getCurrPos(0));
            //System.out.println("YPos: " + model.getCurrPos(1));
            //System.out.println("Tile size: " + model.getMapTileSize());

            // position on getTiles() needs work

            if (main.getControl(k) == Control.T) { // TEST CODE
                System.out.print("Enter coords to move to: ");
                Scanner scanner = new Scanner(System.in);
                model.setCurrPos(0, scanner.nextInt());
                model.setCurrPos(1, scanner.nextInt());
                setScene();
                System.out.println("New coords are: " + model.getCurrPos(0) + ", " + model.getCurrPos(1));
            }

            if (main.getControl(k) == Control.ALT) { // show borders
                for (int y = -view.zoom; y < view.zoom; y++) {
                    for (int x = -view.zoom; x < view.zoom; x++) {
                        int xPos = (model.getCurrPos(0) + x < 0 ? (model.getCurrPos(0) + x >= model.getMapSize() ? model.getMapSize() - 1 : 0) : model.getCurrPos(0) + x);
                        int yPos = (model.getCurrPos(1) + y < 0 ? (model.getCurrPos(1) + y >= model.getMapSize() ? model.getMapSize() - 1 : 0) : model.getCurrPos(1) + y);
                        if (view.imageViews[xPos][yPos][1] == null) {
                            //System.out.println(xPos + " " + yPos);
                            continue;
                        }
                        view.imageViews[xPos][yPos][1].setVisible(true);
                    }
                }
            }

            if (main.getControl(k) == Control.ESC) { // for now save when hit escape
                model.saveGame();
            }

            if (main.getControl(k) == Control.LEFT || main.getControl(k) == Control.RIGHT || main.getControl(k) == Control.UP || main.getControl(k) == Control.DOWN) {
                if (model.getCurrPos(0) <= 0 || model.getCurrPos(0) >= model.getMapSize() || model.getCurrPos(1) <= 0 || model.getCurrPos(1) >= model.getMapSize()) {
                    view.speedX.set(0);
                    view.speedY.set(0);
                    return;
                }

                if (main.getControl(k) == Control.LEFT)
                    view.speedX.set(view.speedXVal);
                if (main.getControl(k) == Control.UP)
                    view.speedY.set(view.speedYVal);
                if (main.getControl(k) == Control.RIGHT)
                    view.speedX.set(-view.speedXVal);
                if (main.getControl(k) == Control.DOWN)
                    view.speedY.set(-view.speedYVal);

                detectTileChange();
            }
            event.consume();
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {

            KeyCode k = event.getCode();

            //System.out.println("XPos: " + model.getCurrPos(0));
            //System.out.println("YPos: " + model.getCurrPos(1));

            if (controlsLocked)
                return;

            if (main.getControl(k) == Control.ALT) { // hide borders
                for (int y = -view.zoom; y < view.zoom; y++) {
                    for (int x = -view.zoom; x < view.zoom; x++) {
                        int xPos = (model.getCurrPos(0) + x < 0 ? (model.getCurrPos(0) + x >= model.getMapSize() ? model.getMapSize() - 1 : 0) : model.getCurrPos(0) + x);
                        int yPos = (model.getCurrPos(1) + y < 0 ? (model.getCurrPos(1) + y >= model.getMapSize() ? model.getMapSize() - 1 : 0) : model.getCurrPos(1) + y);
                        if (view.imageViews[xPos][yPos][1] == null)
                            continue;
                        view.imageViews[xPos][yPos][1].setVisible(false);
                    }
                }
            }

            if (main.getControl(k) == Control.RIGHT || main.getControl(k) == Control.LEFT)
                view.speedX.set(0);
            else if (main.getControl(k) == Control.UP || main.getControl(k) == Control.DOWN)
                view.speedY.set(0);

            if (main.getControl(k) == Control.RIGHT || main.getControl(k) == Control.LEFT || main.getControl(k) == Control.UP || main.getControl(k) == Control.DOWN) {
                if (model.getCurrPos(0) <= 0 || model.getCurrPos(0) >= model.getMapSize() || model.getCurrPos(1) <= 0 || model.getCurrPos(1) >= model.getMapSize())
                    return;

                detectTileChange();
            }
            event.consume();
        });
    }

    private void setMouseEvents() {

        /*
            Maps click events to functions
         */

        System.out.println("Setting click events");
        for (int y = model.getCurrPos(1) - view.zoom; y < model.getCurrPos(1) + view.zoom; y++) {
            for (int x = model.getCurrPos(0) - view.zoom; x < model.getCurrPos(0) + view.zoom; x++) {
                final int finalX = x;
                final int finalY = y;
                if (x < 0 || y < 0 || x >= model.getMapSize() || y >= model.getMapSize())
                    return;
                if (model.getTiles()[x][y].settlementTile != null) {
                    model.getTiles()[x][y].banner.getChildren().get(3).addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                        System.out.println("Attack");
                    });
                    model.getTiles()[x][y].banner.getChildren().get(4).addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                        System.out.println("Enter");
                    });
                    model.getTiles()[x][y].banner.getChildren().get(5).addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                        System.out.println("Diplomacy");
                    });
                    view.imageViews[x][y][0].addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                        // if mouse is on a settlement tile, show banner for settlement
                        model.getTiles()[finalX][finalY].banner.setVisible(true);
                    });
                    view.imageViews[x][y][0].addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                        // if mouse leaves settlement tile, hide banner
                        model.getTiles()[finalX][finalY].banner.setVisible(false);
                    });
                    model.getTiles()[finalX][finalY].banner.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                        // if mouse is on banner, show banner
                        model.getTiles()[finalX][finalY].banner.setVisible(true);
                    });
                    model.getTiles()[finalX][finalY].banner.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                        // if mouse leaves banner, hide banner
                        model.getTiles()[finalX][finalY].banner.setVisible(false);
                    });
                }

                view.imageViews[x][y][0].addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    int x = finalX;
                    int y = finalY;

                    @Override
                    public void handle(MouseEvent event) {

                        // shows tile information when tile is clicked and handles all buttons in windows opened

                        if (controlsLocked)
                            return;

                        //System.out.println("Tile @ pos " + x + ", " + y);
                        if (model.getTiles()[x][y].type.equalsIgnoreCase("Settlement") || model.getTiles()[x][y].type.equalsIgnoreCase("InMap")) {
                            if (model.getTiles()[x][y].type.equalsIgnoreCase("Settlement")) // will change
                                view.showSettlementInfo(model.getTiles()[x][y].settlementTile);
                            else if (model.getTiles()[x][y].type.equalsIgnoreCase("InMap")) {
                                main.IMController.newLocation(new Point(model.getCurrPos(0), model.getCurrPos(1)), model.getTiles()[x][y].inMapTile.inmapType.toLowerCase());
                                view.showInMapInfo(main.IMController.getName(new Point(model.getCurrPos(0), model.getCurrPos(1))),
                                        main.IMController.getDifficulty(new Point(model.getCurrPos(0), model.getCurrPos(1))));
                            }
                            controlsLocked = true;
                        }

                        view.manageCity.setOnAction(event1 -> {
                            // show city politics elements

                            view.removePane(view.paneStack.pop());
                            view.showCityManagement(model.getTiles()[x][y].settlementTile);
                        });
                        view.diplomacy.setOnAction(event1 -> {

                        });
                        view.trade.setOnAction(event1 -> {

                        });
                        view.enterDungeon.setOnAction(event1 -> {
                            main.IMController.passControl(new Point(model.getCurrPos(0), model.getCurrPos(1)));
                        });
                    }
                });
            }
        }
    }
}
