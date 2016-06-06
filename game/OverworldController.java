package game;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class OverworldController extends Thread{

    Main main;
    Scene scene;
    
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

            System.out.println("Key pressed");
            System.out.println("XPos: " + model.currPos[0]);
            System.out.println("YPos: " + model.currPos[1]);

            KeyCode keyCode = event.getCode();
            // position on tiles needs work
            if(keyCode == KeyCode.A){
                if(model.currPos[0] > 0){
                    System.out.println(view.xOffset);
                    if(model.mapTileSize <= Math.abs(view.xOffset)) {
                        System.out.println("Next tile");
                        model.currPos[0]--;
                        model.currPos[1]++;
                        view.xOffset -= model.mapTileSize;
                        //getScene();
                    }
                    else
                    if(event.isShiftDown())
                        view.speedX.set(view.speedXVal * 2);
                    else
                        view.speedX.set(view.speedXVal);
                }
            }
            else if(keyCode == KeyCode.S){
                if(model.currPos[1] < model.mapSize){
                    if(model.mapTileSize <= Math.abs(view.yOffset)) {
                        System.out.println("Next tile");
                        model.currPos[1]++;
                        model.currPos[0]++;
                        view.yOffset -= model.mapTileSize;
                        //getScene();
                    }
                    else
                        if(event.isShiftDown())
                            view.speedY.set(-view.speedYVal * 2);
                        else
                            view.speedY.set(-view.speedYVal);
                }
            }
            else if(keyCode == KeyCode.W){
                if(model.currPos[1] > 0){
                    if(model.mapTileSize <= Math.abs(view.yOffset)) {
                        System.out.println("Next tile");
                        model.currPos[1]--;
                        model.currPos[0]--;
                        view.yOffset -= model.mapTileSize;
                        //getScene();
                    }
                    else
                        if(event.isShiftDown())
                            view.speedY.set(view.speedYVal * 2);
                        else
                            view.speedY.set(view.speedYVal);
                }
            }
            else if(keyCode == KeyCode.D){
                if(model.currPos[0] < model.mapSize){
                    if(model.mapTileSize <= Math.abs(view.xOffset)) {
                        System.out.println("Next tile");
                        model.currPos[0]++;
                        model.currPos[1]--;
                        view.xOffset -= model.mapTileSize;
                        //getScene();
                    }
                    else
                        if(event.isShiftDown())
                            view.speedX.set(-view.speedXVal * 2);
                        else
                            view.speedX.set(-view.speedXVal);
                }
            }

            event.consume();
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.A) {
                view.speedX.set(0);
            }
            if (event.getCode() == KeyCode.W || event.getCode() == KeyCode.S) {
                view.speedY.set(0);
            }
            if(event.getCode() == KeyCode.A){
                if(model.currPos[0] > 0){
                    System.out.println(view.xOffset);
                    if(model.mapTileSize <= Math.abs(view.xOffset)) {
                        System.out.println("Next tile");
                        model.currPos[0]--;
                        model.currPos[1]++;
                        view.xOffset -= model.mapTileSize;
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
                        view.yOffset -= model.mapTileSize;
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
                        view.yOffset -= model.mapTileSize;
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
                        view.xOffset -= model.mapTileSize;
                        //getScene();
                    }
                }
            }
        });
    }
}
