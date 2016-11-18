/*
    Controls all interactions between non-graphical and graphical components for the Overworld

    TODO LIST - IN ORDER OF BLOCK PRIORITY
    Fix View scrolling in SW direction - Seems to have been self-fixed... Nov 17 2016
    Player movement independent of AI's movement
    Fix Settlement banners

    Design and implement Economy

    Design, implement and improve UI

    Improve design and implement improvements for Party AI's
    Fix coastline gen - (more info in Map class)

    Implement lake drawing
    Design and implement factions

 */

package overworld;

import javafx.scene.input.MouseButton;
import main.Control;
import main.Main;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import main.Path;

import java.awt.Point;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class OverworldController implements Runnable {

    private Main main;

    private Scene scene;
    private boolean controlsLocked = false;

    private long start; // for timing the creation of the Model and View

    private OverworldView view;
    private OverworldModel model;

    public OverworldController(Main main, int mapSize) { // new game
        this.main = main;
        start = System.currentTimeMillis();

        model = new OverworldModel(mapSize, true);
        System.out.println("Model init took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");

        view = new OverworldView(model.getMapSize(), main.screenWidth, main.screenHeight);

        model.createPlayer(getBaseSpeed(), "none");
        //model.genParties(getBaseSpeed());

        System.out.println("Overworld init took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");
    }

    public OverworldController(Main main, OverworldModel model) { // load game
        this.main = main;
        start = System.currentTimeMillis();

        this.model = model;
        view = new OverworldView(model.getMapSize(), main.screenWidth, main.screenHeight);
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

    public OverworldModel getModel() {
        return model;
    }

    public void setModelName(String name) {
        model.setModelName(name);
    }

    private void setScene() {

        /*
            Gets scene from the View and passes it to main to be displayed
         */

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
         */

        long start = System.currentTimeMillis();

        //TODO: TILE CHANGE DETECTION NEEDS FIXING AGAIN... PROBABLY TO DO WITH NEXT TWO LINES

        //System.out.println(view.centerTile.screenToLocal(view.redDot.localToScreen(3, 3)));

        double[] angles = calcAngles(view.getPlayerXOffset(), view.getPlayerYOffset());
        double leftAngle = angles[0];
        double rightAngle = angles[1];

        /*System.out.println("xOffset: " + tileXOffset);
        System.out.println("yOffset: " + tileYOffset);
        System.out.println("Left side angle " + leftAngle);
        System.out.println("Right side angle " + rightAngle);
        System.out.println();*/

        if (Math.abs(leftAngle) >= 22.5 || Math.abs(rightAngle) >= 22.5) { // new tile
            /*System.out.println("Moved tile");
            System.out.println();*/
            if (rightAngle >= 22.5) {
                model.setCurrPos(1, model.getCurrPos(1) - 1);
                view.addRow(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), true);
            }
            if (rightAngle <= -22.5) {
                model.setCurrPos(0, model.getCurrPos(0) + 1);
                view.addColumn(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), true);
            }
            if (leftAngle >= 22.5) {
                model.setCurrPos(0, model.getCurrPos(0) - 1);
                view.addColumn(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), false);
            }
            if (leftAngle <= -22.5) {
                model.setCurrPos(1, model.getCurrPos(1) + 1);
                view.addRow(model.getTiles(), model.getPlayer(), model.getParties(), model.getMapSize(), false);
            }
            setMouseEvents();

            System.out.println("Change detection took " + (System.currentTimeMillis() - start) + "ms");
        }
    }

    private void setInput(Scene scene) {

        /*
            Maps keyboard inputs to various functions, such as movement of screen
         */

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            Control key = main.getControl(event.getCode());

            if (key == Control.BACK && !view.paneStack.empty()) {
                view.removePane(view.paneStack.pop());
                if (view.paneStack.empty())
                    controlsLocked = false;
            }

            if (controlsLocked)
                return;

            model.getPlayer().setPath(null);

            //System.out.println("XPos: " + model.getCurrPos(0));
            //System.out.println("YPos: " + model.getCurrPos(1));
            //System.out.println("Tile size: " + model.getMapTileSize());

            // position on getTiles() needs work

            if (event.getCode() == KeyCode.P) {
                //model.getParty(0).nextMove(model.getBooleanMap(), model.getParties());
                Party p = model.getParty(0);
                model.getParty(0).nextMove(model.getBooleanMap(), model.getParties());
            }
            if (key == Control.T) { // TEST CODE
                System.out.print("Enter coords to move to: ");
                Scanner scanner = new Scanner(System.in);
                model.setCurrPos(0, scanner.nextInt());
                model.setCurrPos(1, scanner.nextInt());
                setScene();
                System.out.println("New coords are: " + model.getCurrPos(0) + ", " + model.getCurrPos(1));
            }

            if (key == Control.ALT) { // show borders
                for (int y = -OverworldView.zoom; y < OverworldView.zoom; y++) {
                    for (int x = -OverworldView.zoom; x < OverworldView.zoom; x++) {
                        if (view.imageViews[x + OverworldView.zoom][y + OverworldView.zoom][1] == null) {
                            //System.out.println(xPos + " " + yPos);
                            continue;
                        }
                        view.imageViews[x + OverworldView.zoom][y + OverworldView.zoom][1].setVisible(true);
                    }
                }
            }

            if (key == Control.ESC) { // for now save when hit escape
                //model.saveGame();
                try {
                    main.saveModel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (key == Control.LEFT || key == Control.RIGHT || key == Control.UP || key == Control.DOWN) {
                if (model.getCurrPos(0) <= 0 || model.getCurrPos(0) >= model.getMapSize() || model.getCurrPos(1) <= 0 || model.getCurrPos(1) >= model.getMapSize()) {
                    view.speedX.set(0);
                    view.speedY.set(0);
                    return;
                }

                if (key == Control.UP || key == Control.DOWN)
                    view.speedY.set(model.getPlayer().getSpeedY(key));
                if (key == Control.RIGHT || key == Control.LEFT)
                    view.speedX.set(-model.getPlayer().getSpeedX(key));

                detectTileChange();
            }
            event.consume();
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {

            Control key = main.getControl(event.getCode());

            //System.out.println("XPos: " + model.getCurrPos(0));
            ///System.out.println("YPos: " + model.getCurrPos(1));

            if (controlsLocked)
                return;

            if (key == Control.ALT) { // hide borders
                for (int y = -OverworldView.zoom; y < OverworldView.zoom; y++) {
                    for (int x = -OverworldView.zoom; x < OverworldView.zoom; x++) {
                        if (view.imageViews[x + OverworldView.zoom][y + OverworldView.zoom][1] == null)
                            continue;
                        view.imageViews[x + OverworldView.zoom][y + OverworldView.zoom][1].setVisible(false);
                    }
                }
            }

            if (key == Control.RIGHT || key == Control.LEFT)
                view.speedX.set(0);
            else if (key == Control.UP || key == Control.DOWN)
                view.speedY.set(0);

            if (key == Control.RIGHT || key == Control.LEFT || key == Control.UP || key == Control.DOWN) {
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
        for (int y = -OverworldView.zoom; y <= OverworldView.zoom; y++) {
            for (int x = -OverworldView.zoom; x <= OverworldView.zoom; x++) {
                final int fX = x;
                final int fY = y;
                final Party p = model.getPlayer();
                final int yPos = (p.getTileY() + y < 0 ? (p.getTileY() + y >= model.getMapSize() ? model.getMapSize() - 1 : 0) : p.getTileY() + y);
                final int xPos = (p.getTileX() + x < 0 ? (p.getTileX() + x >= model.getMapSize() ? model.getMapSize() - 1 : 0) : p.getTileX() + x);

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

                    if (event.getButton() == MouseButton.PRIMARY) {
                        System.out.println("Tile @ pos " + xPos + ", " + yPos);
                        if (model.getTiles()[xPos][yPos].type.equalsIgnoreCase("Settlement") || model.getTiles()[xPos][yPos].type.equalsIgnoreCase("InMap")) {
                            if (model.getTiles()[xPos][yPos].type.equalsIgnoreCase("Settlement")) // will change
                                view.showSettlementInfo(model.getTiles()[xPos][yPos].settlementTile);
                            else if (model.getTiles()[xPos][yPos].type.equalsIgnoreCase("InMap")) {
                                main.IMController.newLocation(new Point(xPos, yPos), model.getTiles()[xPos][yPos].inMapTile.inmapType.toLowerCase());
                                view.showInMapInfo(main.IMController.getName(new Point(xPos, yPos)),
                                        main.IMController.getDifficulty(new Point(xPos, yPos)));
                            }
                            controlsLocked = true;
                        }

                        view.manageCity.setOnAction(event1 -> {
                            // show city politics elements

                            view.removePane(view.paneStack.pop());
                            view.showCityManagement(model.getTiles()[xPos][yPos].settlementTile);
                        });

                        view.enterDungeon.setOnAction(event1 -> {
                            main.IMController.passControl(new Point(xPos, yPos));
                        });
                    } else if (event.getButton() == MouseButton.SECONDARY) { // move to tile
                        int areaStartX = p.getTileX() < xPos ? p.getTileX() - 10 > 0 ? p.getTileX() : 0 : xPos - 10 > 0 ? xPos : 0;
                        int areaStartY = p.getTileY() < yPos ? p.getTileY() - 10 > 0 ? p.getTileY() : 0 : yPos - 10 > 0 ? yPos : 0;
                        int areaDestX = p.getTileX() < xPos ? xPos + 10 < model.getMapSize() - 1 ? xPos : model.getMapSize() - 1 : p.getTileX() + 1 < model.getMapSize() - 1 ? p.getTileX() : model.getMapSize() - 1;
                        int areaDestY = p.getTileY() < yPos ? yPos + 10 < model.getMapSize() - 1 ? yPos : model.getMapSize() - 1 : p.getTileY() + 1 < model.getMapSize() - 1 ? p.getTileY() : model.getMapSize() - 1;
                        long start = System.currentTimeMillis();
                        model.getPlayer().setPath(new Path());
                        model.getPlayer().getPath().pathFind(
                                model.getBooleanMap(), // implement area search later, use general for now
                                new Point(model.getPlayer().getTileX(), model.getPlayer().getTileY()), new Point(xPos, yPos), true
                        );
                        model.getPlayer().setStart(model.getPlayer().getTileX(), model.getPlayer().getTileY());
                        model.getPlayer().setPixelStartPos((int) view.getPlayerXOffset(), (int) view.getPlayerYOffset());

                        System.out.println("Path to target tile created in " + (System.currentTimeMillis() - start) + "ms");
                    }
                });
            }
        }
    }
}