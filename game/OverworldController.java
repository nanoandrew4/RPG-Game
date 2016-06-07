/*
    Controls all interactions between non-graphical and graphical components for the Overworld
 */

package game;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class OverworldController extends Thread{

    private Main main;
    private Scene scene;
    
    private OverworldView view;
    private OverworldModel model;

    OverworldController(Main main){
        this.main = main;
        model = new OverworldModel();
        view = new OverworldView(model.mapSize);

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
        scene = view.initDisplay(model.tiles,main.screenWidth, main.screenHeight, model.zoom, model.mapTileSize, model.currPos, model.mapSize);
        setInput(scene);
        main.setStage(scene);
    }

    private void setInput(Scene scene){

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            System.out.println("XPos: " + model.currPos[0]);
            System.out.println("YPos: " + model.currPos[1]);
            //System.out.println("Xoffset: " + view.xOffset);
            //System.out.println("Yoffset: " + view.yOffset);

            KeyCode keyCode = event.getCode();
            // position on tiles needs work

            if(keyCode == KeyCode.A || keyCode == KeyCode.D || keyCode == KeyCode.W || keyCode == KeyCode.S){
                if(model.currPos[0] <= 0 || model.currPos[0] >= model.mapSize || model.currPos[1] <= 0 || model.currPos[1] >= model.mapSize)
                    return;

                double leftAngle = Math.toDegrees(Math.asin(((model.mapTileSize / 2) - view.xOffset) / ((model.mapTileSize / 4) + view.yOffset)));
                double rightAngle = Math.toDegrees(Math.asin(((model.mapTileSize / 2) + view.xOffset) / ((model.mapTileSize / 4) + view.yOffset)));

                System.out.println("Left side angle " + leftAngle);
                System.out.println("Right side angle " + rightAngle);

                if(leftAngle >= 45 || rightAngle >= 45){ // new tile
                    System.out.println("Moved tile");
                    view.xOffset -= view.xOffset >= model.mapTileSize ? model.mapTileSize : view.xOffset;
                    view.yOffset -= view.yOffset >= model.mapTileSize / 2 ? model.mapTileSize / 2 : view.yOffset;
                }
            }

            if(keyCode == KeyCode.A){
                view.speedX.set(view.speedXVal);
                view.speedY.set(view.speedYVal);
                if(model.currPos[0] > 0){
                    System.out.println(view.xOffset);
                    if(model.mapTileSize <= Math.abs(view.xOffset)) {
                        System.out.println("Next tile");
                        model.currPos[0]--;
                        //model.currPos[1]++;
                        //view.xOffset -= model.mapTileSize;
                        view.addColumn(model.tiles, main.screenWidth, main.screenHeight, model.zoom, model.mapTileSize, model.currPos, model.mapSize, false);
                    }
                }
            }
            else if(keyCode == KeyCode.S){
                view.speedX.set(view.speedXVal);
                view.speedY.set(-view.speedYVal);
                if(model.currPos[1] < model.mapSize){
                    if(model.mapTileSize <= Math.abs(view.yOffset)) {
                        System.out.println("Next tile");
                        model.currPos[1]++;
                        //model.currPos[0]++;
                        //view.yOffset -= model.mapTileSize;
                        view.addRow(model.tiles, main.screenWidth, main.screenHeight, model.zoom, model.mapTileSize, model.currPos, model.mapSize, false);
                    }
                }
            }
            else if(keyCode == KeyCode.W){
                view.speedX.set(-view.speedXVal);
                view.speedY.set(view.speedYVal);
                if(model.currPos[1] > 0){
                    if(model.mapTileSize <= Math.abs(view.yOffset)) {
                        System.out.println("Next tile");
                        model.currPos[1]--;
                        //model.currPos[0]--;
                        //view.yOffset -= model.mapTileSize;
                        view.addRow(model.tiles, main.screenWidth, main.screenHeight, model.zoom, model.mapTileSize, model.currPos, model.mapSize, true);

                    }
                }
            }
            else if(keyCode == KeyCode.D){
                view.speedX.set(-view.speedXVal);
                view.speedY.set(-view.speedYVal);
                if(model.currPos[0] < model.mapSize){
                    if(model.mapTileSize <= Math.abs(view.xOffset)) {
                        System.out.println("Next tile");
                        model.currPos[0]++;
                        //model.currPos[1]--;
                        //view.xOffset -= model.mapTileSize;
                        view.addColumn(model.tiles, main.screenWidth, main.screenHeight, model.zoom, model.mapTileSize, model.currPos, model.mapSize, true);
                    }
                }
            }

            event.consume();
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.A || event.getCode() == KeyCode.W || event.getCode() == KeyCode.S) {
                view.speedX.set(0);
                view.speedY.set(0);
            }
            if(event.getCode() == KeyCode.A){
                if(model.currPos[0] > 0){
                    System.out.println(view.xOffset);
                    if(model.mapTileSize <= Math.abs(view.xOffset)) {
                        System.out.println("Next tile");
                        model.currPos[0]--;
                        model.currPos[1]++;
                        //view.xOffset -= model.mapTileSize;
                        //getScene();
                    }
                }
            }
            else if(event.getCode() == KeyCode.S){
                if(model.currPos[1] < model.mapSize){
                    if(model.mapTileSize <= Math.abs(view.yOffset)) {
                        System.out.println("Next tile");
                        model.currPos[1]++;
                        model.currPos[0]++;
                        //view.yOffset -= model.mapTileSize;
                        //getScene();
                    }
                }
            }
            else if(event.getCode() == KeyCode.W){
                if(model.currPos[1] > 0){
                    if(model.mapTileSize <= Math.abs(view.yOffset)) {
                        System.out.println("Next tile");
                        model.currPos[1]--;
                        model.currPos[0]--;
                        //view.yOffset -= model.mapTileSize;
                        //getScene();
                    }
                }
            }
            else if(event.getCode() == KeyCode.D){
                if(model.currPos[0] < model.mapSize){
                    if(model.mapTileSize <= Math.abs(view.xOffset)) {
                        System.out.println("Next tile");
                        model.currPos[0]++;
                        model.currPos[1]--;
                        //view.xOffset -= model.mapTileSize;
                        //getScene();
                    }
                }
            }
        });
    }
}
