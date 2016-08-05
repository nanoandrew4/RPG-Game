/*
    Controls all interactions between non-graphical and graphical components for the Overworld
 */

package game;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class OverworldController implements Runnable{

    private Main main;
    private Scene scene;
    private boolean newTile;
    private float zoomMultiplier = 2.5f;
    
    private OverworldView view;
    private OverworldModel model;

    OverworldController(Main main){
        this.main = main;
        model = new OverworldModel(main.mapSize);
        view = new OverworldView(model.mapSize);
    }

    @Override
    public void run(){
        System.out.println("Overworld thread started");
        setTileSize(); // which runs get scene
    }

    private void setTileSize(){
        if (main.screenWidth > main.screenHeight) {
            model.mapTileSize = main.screenHeight / model.zoom;
        } else {
            model.mapTileSize = main.screenWidth / model.zoom;
        }
        getScene();
    }

    private void getScene(){
        scene = view.initDisplay(model.tiles,main.screenWidth, main.screenHeight, (int)(model.zoom * zoomMultiplier), model.mapTileSize, model.currPos, model.mapSize);
        setClickEvents();
        setInput(scene);
        Platform.runLater(() -> main.setStage(scene)); // to update UI from non-javafx thread
    }

    private double[] calcAngles(double yOffset, double xOffset){
        double[] tmpArr = new double[2]; // stores langle, rangle
        while (xOffset > model.mapTileSize / 2)
            xOffset -= model.mapTileSize;
        while (yOffset > model.mapTileSize / 4)
            yOffset -= model.mapTileSize / 2;
        while (xOffset < -model.mapTileSize / 2)
            xOffset += model.mapTileSize;
        while (yOffset < -model.mapTileSize / 4)
            yOffset += model.mapTileSize / 2;

        tmpArr[0] = Math.toDegrees(Math.atan(yOffset / (xOffset + (model.mapTileSize / 2)))); // left
        tmpArr[1] = Math.toDegrees(Math.atan(yOffset / ((model.mapTileSize / 2) - xOffset))); // right

        System.out.println("xOffset: " + xOffset);
        System.out.println("yOffset: " + yOffset);
        System.out.println(String.valueOf(newTile));
        //System.out.println();
        return tmpArr;
    }

    private void detectTileChange(){ //
        // mapTileSize / 2 is the max the actual offset from the centre of any tile, horizontally (vertically divide by 4)
        double xOffset = -view.xOffset; // offset from centre of nearest tile, negative to make right positive
        double yOffset = view.yOffset; // offset from centre of nearest tile

        // do something with newTile to offset the offsets

        double[] angles = calcAngles(yOffset, xOffset);
        double leftAngle = angles[0];
        double rightAngle = angles[1];

        if (newTile){
            newTile = false;
            if (rightAngle >= 22.5){
                xOffset -= model.mapTileSize / 2;
                yOffset -= model.mapTileSize / 4;
            }
            else if (rightAngle <= -22.5){
                xOffset -= model.mapTileSize / 2;
                yOffset += model.mapTileSize / 4;
            }
            else if (leftAngle >= 22.5){
                xOffset += model.mapTileSize / 2;
                yOffset -= model.mapTileSize / 4;
            }
            else if (leftAngle <= -22.5){
                xOffset += model.mapTileSize / 2;
                yOffset += model.mapTileSize / 4;
            }
            angles = calcAngles(yOffset, xOffset);
            rightAngle = angles[0];
            leftAngle = angles[1];
        }

        System.out.println("Left side angle " + leftAngle);
        System.out.println("Right side angle " + rightAngle);

        if ((leftAngle >= 22.5 || rightAngle >= 22.5 || leftAngle <= -22.5 || rightAngle <= -22.5) && !newTile){ // new tile
            System.out.println("Moved tile");
            if (rightAngle >= 22.5) {
                model.currPos[1]++;
                //view.addRow(model.tiles, main.screenWidth, main.screenHeight, (int)(model.zoom * zoomMultiplier), model.mapTileSize, model.currPos, model.mapSize, false);
            }
            else if (rightAngle <= -22.5) {
                model.currPos[0]++;
                //view.addColumn(model.tiles, main.screenWidth, main.screenHeight, (int)(model.zoom * zoomMultiplier), model.mapTileSize, model.currPos, model.mapSize, false);
            }
            else if (leftAngle >= 22.5) {
                model.currPos[0]--;
                //view.addColumn(model.tiles, main.screenWidth, main.screenHeight, (int)(model.zoom * zoomMultiplier), model.mapTileSize, model.currPos, model.mapSize, false);
            }
            else if (leftAngle <= -22.5) {
                model.currPos[1]--;
                //view.addRow(model.tiles, main.screenWidth, main.screenHeight, (int)(model.zoom * zoomMultiplier), model.mapTileSize, model.currPos, model.mapSize, false);
            }
            if (!(xOffset < -model.mapTileSize / 2 || yOffset < -model.mapTileSize / 4 || xOffset > model.mapTileSize / 2 || yOffset > model.mapTileSize / 4))
                newTile = true; // means moved tile so that even tho the offsets are < mapTileSize / 2 it will start calculating from the next tile
            setClickEvents();
        }
        System.out.println();
    }

    private void setInput(Scene scene){

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            //System.out.println("XPos: " + model.currPos[0]);
            //System.out.println("YPos: " + model.currPos[1]);
            //System.out.println("Curr tile x-offset: " + view.xOffset);
            //System.out.println("Curr tile y-offset: " + view.yOffset);
            //System.out.println("Tile size: " + model.mapTileSize);

            // position on tiles needs work

            if(event.getCode() == KeyCode.A || event.getCode() == KeyCode.D || event.getCode() == KeyCode.W || event.getCode() == KeyCode.S) {
                if (model.currPos[0] <= 0 || model.currPos[0] >= model.mapSize || model.currPos[1] <= 0 || model.currPos[1] >= model.mapSize) {
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
            if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.A || event.getCode() == KeyCode.W || event.getCode() == KeyCode.S) {
                view.speedX.set(0);
                view.speedY.set(0);
            }

            if(event.getCode() == KeyCode.A || event.getCode() == KeyCode.D || event.getCode() == KeyCode.W || event.getCode() == KeyCode.S){
                if(model.currPos[0] <= 0 || model.currPos[0] >= model.mapSize || model.currPos[1] <= 0 || model.currPos[1] >= model.mapSize)
                    return;

                detectTileChange();
            }
            event.consume();
        });
    }

    private void setClickEvents(){
        System.out.println("Setting click events");
        for(int y = model.currPos[1] - (int)(model.zoom * zoomMultiplier); y < model.currPos[1] + (int)(model.zoom * zoomMultiplier); y++){
            for(int x = model.currPos[0] - (int)(model.zoom * zoomMultiplier); x < model.currPos[0] + (int)(model.zoom * zoomMultiplier); x++){
                final int finalX = x;
                final int finalY = y;
                view.imageViews[x][y].addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    int x = finalX;
                    int y = finalY;
                    @Override
                    public void handle(MouseEvent event) {
                        System.out.println("Tile @ pos " + x + ", " + y);
                    }
                });
            }
        }
    }
}
