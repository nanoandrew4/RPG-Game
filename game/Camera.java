package game;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class Camera {

    Pane[] zoomLevels;
    Values values = Main.values;

    Camera(Tile[][] map, int x, int y){
        zoomLevels = new Pane[values.mapZoomMax];
        for(int a = 0; a < values.mapZoomMax; a++){
            for(int c = -a; c < a; c++) {
                for (int b = -a; b < a; b++) {
                    zoomLevels[a].getChildren().add(map[x + c][y + b].tileImage);
                }
            }
        }
    }

    public void scrollCamera(KeyCode keyCode){
        if(keyCode == KeyCode.A){

        }
        else if(keyCode == KeyCode.S){

        }
        else if(keyCode == KeyCode.W){

        }
        else if(keyCode == KeyCode.D){

        }
    }
    public void zoomCamera(){

    }
}
