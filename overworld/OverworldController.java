/*
    Controls all interactions between non-graphical and graphical components for the Overworld

    TODO: IMPROVE MVC MODEL ESPECIALLY CONTROLLER PART
    TODO: CLEAN UP AND ORGANIZE CODE NEATLY
 */

package game;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Scanner;

public class OverworldController implements Runnable {

    private Main main;
    private Scene scene;
    private boolean newTile;
    private float zoomMultiplier = 2.5f;
    private boolean controlsLocked = false;

    long start; // for timing the creation of the Model and View

    private OverworldView view;
    private OverworldModel model;

    OverworldController(Main main, boolean newGame) {
        start = System.currentTimeMillis();

        this.main = main;
        model = new OverworldModel(main.mapSize, newGame);

        System.out.println("Model init took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");

        view = new OverworldView(model.getMapSize(), main.screenWidth, main.screenHeight);

        System.out.println("Overworld init took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");
    }

    @Override
    public void run() {
        start = System.currentTimeMillis();
        System.out.println("Overworld thread started");
        setTileSize(); // which runs get scene
    }

    private void setTileSize() {
        if (main.screenWidth > main.screenHeight) {
            model.setMapTileSize(main.screenHeight / model.getZoom());
        } else {
            model.setMapTileSize(main.screenWidth / model.getZoom());
        }
        getScene();
    }

    private void getScene() {

        /*
            Gets scene from the View and passes it to main to be displayed
         */

        scene = view.initDisplay(model.getTiles(), main.screenWidth, main.screenHeight, (int) (model.getZoom() * zoomMultiplier), model.getMapTileSize(), model.getCurrPos(), model.getMapSize());
        setMouseEvents();
        setInput(scene);
        Platform.runLater(() -> main.setStage(scene)); // to update UI from non-javafx thread
        System.out.println("Scene creation took: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");
    }

    private double[] calcAngles(double yOffset, double xOffset) {

        /*
            Calculates and returns angles from current position on tile to leftmost and rightmost point
         */

        double[] tmpArr = new double[2]; // stores langle, rangle
        while (xOffset > model.getMapTileSize() / 2)
            xOffset -= model.getMapTileSize();
        while (yOffset > model.getMapTileSize() / 4)
            yOffset -= model.getMapTileSize() / 2;
        while (xOffset < -model.getMapTileSize() / 2)
            xOffset += model.getMapTileSize();
        while (yOffset < -model.getMapTileSize() / 4)
            yOffset += model.getMapTileSize() / 2;

        tmpArr[0] = Math.toDegrees(Math.atan(yOffset / (xOffset + (model.getMapTileSize() / 2)))); // left
        tmpArr[1] = Math.toDegrees(Math.atan(yOffset / ((model.getMapTileSize() / 2) - xOffset))); // right

        System.out.println("xOffset: " + xOffset);
        System.out.println("yOffset: " + yOffset);
        System.out.println(String.valueOf(newTile));
        //System.out.println();
        return tmpArr;
    }

    private void detectTileChange() {

        /*
            Algorithm to detect whether there has been a change in tile (user moved off tile on to adjacent one)
            TODO: WIP
         */

        // getMapTileSize() / 2 is the max the actual offset from the centre of any tile, horizontally (vertically divide by 4)
        double xOffset = -view.xOffset; // offset from centre of nearest tile, negative to make right positive
        double yOffset = view.yOffset; // offset from centre of nearest tile

        // do something with newTile to offset the offsets

        double[] angles = calcAngles(yOffset, xOffset);
        double leftAngle = angles[0];
        double rightAngle = angles[1];

        if (newTile) {
            newTile = false;
            if (rightAngle >= 22.5) {
                xOffset -= model.getMapTileSize() / 2;
                yOffset -= model.getMapTileSize() / 4;
            } else if (rightAngle <= -22.5) {
                xOffset -= model.getMapTileSize() / 2;
                yOffset += model.getMapTileSize() / 4;
            } else if (leftAngle >= 22.5) {
                xOffset += model.getMapTileSize() / 2;
                yOffset -= model.getMapTileSize() / 4;
            } else if (leftAngle <= -22.5) {
                xOffset += model.getMapTileSize() / 2;
                yOffset += model.getMapTileSize() / 4;
            }
            angles = calcAngles(yOffset, xOffset);
            rightAngle = angles[0];
            leftAngle = angles[1];
        }

        System.out.println("Left side angle " + leftAngle);
        System.out.println("Right side angle " + rightAngle);

        if ((leftAngle >= 22.5 || rightAngle >= 22.5 || leftAngle <= -22.5 || rightAngle <= -22.5) && !newTile) { // new tile
            System.out.println("Moved tile");
            if (rightAngle >= 22.5) {
                model.setCurrPos(1, 1);
                //view.addRow(model.getTiles(), main.screenWidth, main.screenHeight, (int)(model.getZoom() * zoomMultiplier), model.getMapTileSize(), model.currPos, model.getMapSize(), false);
            } else if (rightAngle <= -22.5) {
                model.setCurrPos(0, 1);
                //view.addColumn(model.getTiles(), main.screenWidth, main.screenHeight, (int)(model.getZoom() * zoomMultiplier), model.getMapTileSize(), model.currPos, model.getMapSize(), false);
            } else if (leftAngle >= 22.5) {
                model.setCurrPos(0, -1);
                //view.addColumn(model.getTiles(), main.screenWidth, main.screenHeight, (int)(model.getZoom() * zoomMultiplier), model.getMapTileSize(), model.currPos, model.getMapSize(), false);
            } else if (leftAngle <= -22.5) {
                model.setCurrPos(1, -1);
                //view.addRow(model.getTiles(), main.screenWidth, main.screenHeight, (int)(model.getZoom() * zoomMultiplier), model.getMapTileSize(), model.currPos, model.getMapSize(), false);
            }
            if (!(xOffset < -model.getMapTileSize() / 2 || yOffset < -model.getMapTileSize() / 4 || xOffset > model.getMapTileSize() / 2 || yOffset > model.getMapTileSize() / 4))
                newTile = true; // means moved tile so that even tho the offsets are < getMapTileSize() / 2 it will start calculating from the next tile
            setMouseEvents();
        }
        System.out.println();
    }

    private void setInput(Scene scene) {

        /*
            Maps keyboard inputs to various functions, such as movement of screen
         */

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            //System.out.println("XPos: " + model.getCurrPos(0));
            //System.out.println("YPos: " + model.getCurrPos(1));
            //System.out.println("Curr tile x-offset: " + view.xOffset);
            //System.out.println("Curr tile y-offset: " + view.yOffset);
            //System.out.println("Tile size: " + model.getMapTileSize());

            // position on getTiles() needs work

            if (controlsLocked)
                return;

            if(event.getCode() == KeyCode.T){ // TEST CODE
                System.out.print("Enter coords to move to: ");
                Scanner scanner = new Scanner(System.in);
                model.setCurrPos(0, scanner.nextInt() - model.getCurrPos(0));
                model.setCurrPos(1, scanner.nextInt() - model.getCurrPos(1));
                setTileSize();
                System.out.println("New coords are: " + model.getCurrPos(0) + ", " + model.getCurrPos(1));
            }

            if (event.getCode() == KeyCode.ALT) { // show borders
                for (int y = (int) (-model.getZoom() * zoomMultiplier); y < model.getZoom() * zoomMultiplier; y++) {
                    for (int x = (int) (-model.getZoom() * zoomMultiplier); x < model.getZoom() * zoomMultiplier; x++) {
                        int xPos = model.getCurrPos(0) + x;
                        int yPos = model.getCurrPos(1) + y;
                        view.imageViews[xPos][yPos][1].setVisible(true);
                    }
                }
            }

            if(event.getCode() == KeyCode.ESCAPE){ // for now save when hit escape
                model.saveGame();
            }

            if (event.getCode() == KeyCode.M) {
                main.setStage(view.displayMap(model.getTiles(), model.getMapSize(), main.screenWidth, main.screenHeight));
            }

            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.D || event.getCode() == KeyCode.W || event.getCode() == KeyCode.S) {
                if (model.getCurrPos(0) <= 0 || model.getCurrPos(0) >= model.getMapSize() || model.getCurrPos(1) <= 0 || model.getCurrPos(1) >= model.getMapSize()) {
                    view.speedX.set(0);
                    view.speedY.set(0);
                    return;
                }

                if (event.getCode() == KeyCode.A)
                    view.speedX.set(view.speedXVal);
                if (event.getCode() == KeyCode.W)
                    view.speedY.set(view.speedYVal);
                if (event.getCode() == KeyCode.D)
                    view.speedX.set(-view.speedXVal);
                if (event.getCode() == KeyCode.S)
                    view.speedY.set(-view.speedYVal);

                detectTileChange();
            }
            event.consume();
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {

            if (controlsLocked)
                return;

            if (event.getCode() == KeyCode.ALT) { // hide borders
                for (int y = (int) (-model.getZoom() * zoomMultiplier); y < model.getZoom() * zoomMultiplier; y++) {
                    for (int x = (int) (-model.getZoom() * zoomMultiplier); x < model.getZoom() * zoomMultiplier; x++) {
                        int xPos = model.getCurrPos(0) + x;
                        int yPos = model.getCurrPos(1) + y;
                        view.imageViews[xPos][yPos][1].setVisible(false);
                    }
                }
            }

            if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.A || event.getCode() == KeyCode.W || event.getCode() == KeyCode.S) {
                view.speedX.set(0);
                view.speedY.set(0);
            }

            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.D || event.getCode() == KeyCode.W || event.getCode() == KeyCode.S) {
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
        for (int y = model.getCurrPos(1) - (int) (model.getZoom() * zoomMultiplier); y < model.getCurrPos(1) + (int) (model.getZoom() * zoomMultiplier); y++) {
            for (int x = model.getCurrPos(0) - (int) (model.getZoom() * zoomMultiplier); x < model.getCurrPos(0) + (int) (model.getZoom() * zoomMultiplier); x++) {
                final int finalX = x;
                final int finalY = y;
                if(x < 0 || y < 0 || x >= model.getMapSize() || y >= model.getMapSize())
                    return;
                if (model.getTiles()[x][y].settlementTile != null) {
                    model.getTiles()[x][y].banner.getChildren().get(3).addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            System.out.println("Attack");
                        }
                    });
                    model.getTiles()[x][y].banner.getChildren().get(4).addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            System.out.println("Enter");
                        }
                    });
                    model.getTiles()[x][y].banner.getChildren().get(5).addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            System.out.println("Diplomacy");
                        }
                    });
                    view.imageViews[x][y][0].addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            // if mouse is on a settlement tile, show banner for settlement
                            model.getTiles()[finalX][finalY].banner.setVisible(true);
                        }
                    });
                    view.imageViews[x][y][0].addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            // if mouse leaves settlement tile, hide banner
                            model.getTiles()[finalX][finalY].banner.setVisible(false);
                        }
                    });
                    model.getTiles()[finalX][finalY].banner.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            // if mouse is on banner, show banner
                            model.getTiles()[finalX][finalY].banner.setVisible(true);
                        }
                    });
                    model.getTiles()[finalX][finalY].banner.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            // if mouse leaves banner, hide banner
                            model.getTiles()[finalX][finalY].banner.setVisible(false);
                        }
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
                        controlsLocked = true;
                        if (model.getTiles()[x][y].type.equalsIgnoreCase("Settlement")) // will change
                            view.showSettlementInfo(model.getTiles()[x][y].settlementTile);
                        else
                            view.showTileInfo(model.getTiles()[x][y]);
                        view.close.setOnAction(event1 -> {
                            view.removePane(view.paneStack.pop());
                            if (view.paneStack.empty())
                                controlsLocked = false;
                        });
                        view.manageCity.setOnAction(event1 -> {
                            // show city politics elements

                            view.removePane(view.paneStack.pop());
                            view.showCityManagement(model.getTiles()[x][y].settlementTile);
                        });
                        view.diplomacy.setOnAction(event1 -> {

                        });
                        view.trade.setOnAction(event1 -> {

                        });
                    }
                });
            }
        }
    }
}
