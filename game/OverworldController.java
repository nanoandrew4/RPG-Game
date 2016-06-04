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
        view = new OverworldView();

        getScene();
    }

    private void getScene(){
        if (main.screenWidth > main.screenHeight) {
            model.mapTileSize = main.screenHeight / model.zoom;
            model.scrollOffset = model.mapTileSize / 8;
        } else {
            model.mapTileSize = main.screenWidth / model.zoom;
            model.scrollOffset = model.mapTileSize / 8;
        }

        scene = view.displayOverworld(model.mapArr, model.mapSize, model.mapTileSize, main.screenWidth, main.screenHeight, model.xOffset, model.yOffset, model.zoom, model.currPos);
        setInput(scene);
        main.setStage(scene);
    }

    private void setInput(Scene scene){

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode keyCode = event.getCode();
            if(keyCode == KeyCode.Z && model.zoom > 0){ // for zooming in
                model.zoom--;
                System.out.println("Zoom in");
            }
            else if(keyCode == KeyCode.X && model.zoom < model.mapZoomMax){ // for zooming out
                model.zoom++;
                System.out.println("Zoom out");
            }
            // position on tiles needs work
            if(keyCode == KeyCode.A){
                if(model.currPos[0] > 0){
                    if(model.mapTileSize - Math.abs(model.xOffset) <= 0) {
                        System.out.println("Next tile");
                        model.currPos[0]--;
                        model.currPos[1]++;
                        model.xOffset = 0;
                    }
                    else
                        model.xOffset += model.scrollOffset;
                }
                else
                    return;
            }
            else if(keyCode == KeyCode.S){
                if(model.currPos[1] < model.mapSize){
                    if(model.mapTileSize - Math.abs(model.yOffset * 2) <= 0) {
                        System.out.println("Next tile");
                        model.currPos[1]++;
                        model.currPos[0]++;
                        model.yOffset = 0;
                    }
                    else
                        model.yOffset -= model.scrollOffset / 2;
                }
                else
                    return;
            }
            else if(keyCode == KeyCode.W){
                if(model.currPos[1] > 0){
                    if(model.mapTileSize - Math.abs(model.yOffset * 2) <= 0) {
                        System.out.println("Next tile");
                        model.currPos[1]--;
                        model.currPos[0]--;
                        model.yOffset = 0;
                    }
                    else
                        model.yOffset += model.scrollOffset / 2;
                }
                else
                    return;
            }
            else if(keyCode == KeyCode.D){
                if(model.currPos[0] < model.mapSize){
                    if(model.mapTileSize - Math.abs(model.xOffset) <= 0) {
                        System.out.println("Next tile");
                        model.currPos[0]++;
                        model.currPos[1]--;
                        model.xOffset = 0;
                    }
                    else
                        model.xOffset -= model.scrollOffset;
                }
                else
                    return;
            }

            event.consume();
            getScene();
        });
    }
}
