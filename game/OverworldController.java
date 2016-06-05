package game;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Material;

public class OverworldController extends Thread{

    Main main;
    Scene[] scenes;
    
    private OverworldView view;
    private OverworldModel model;

    OverworldController(Main main){
        this.main = main;
        scenes = new Scene[5]; // 0 is current, 1 is top, 2 is right, 3 is down, 4 is left (clockwise with 0 in middle)
        model = new OverworldModel();
        view = new OverworldView();

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
        scenes[0] = view.displayOverworld(model.tiles,main.screenWidth, main.screenHeight, model.zoom, model.mapTileSize, model.currPos, model.mapSize);
        setInput(scenes[0]);
        main.setStage(scenes[0]);
        prepareScenes();
    }

    public void prepareScenes(){
        int tilesOffset = view.mapLoadArea; // for determining how many tiles above the next scene will be

        //scenes[1] =
    }

    private void setInput(Scene scene){

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            System.out.println("Key pressed");
            System.out.println("XPos: " + model.currPos[0]);
            System.out.println("YPos: " + model.currPos[1]);

            KeyCode keyCode = event.getCode();
            if(keyCode == KeyCode.Z && model.zoom > 0){ // for zooming in
                model.zoom--;
                setTileSize();
                System.out.println("Zoom in");
            }
            else if(keyCode == KeyCode.X && model.zoom < model.mapZoomMax){ // for zooming out
                model.zoom++;
                setTileSize();
                System.out.println("Zoom out");
            }
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
