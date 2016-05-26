package game;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class Camera {

    Pane[] zoomLevels;
    Values values = Main.values;

    Camera(Tile[][] map, int x, int y){
        zoomLevels = new Pane[values.mapZoomMax];
        for(int a = 0; a < values.mapZoomMax; a++){
            zoomLevels[a] = new Pane();
            for(int c = -a; c < a; c++) {
                for (int b = -a; b < a; b++) {
                    map[(x + c > 0 ? (x < values.mapSize ? x : values.mapSize -1) : 0)][(y + b > 0 ? (y < values.mapSize ? y : values.mapSize -1) : 0)].tileImage.setFitWidth((values.screenWidth > values.screenHeight ? values.screenHeight / (a + 1): values.screenWidth / (a + 1)));
                    map[(x + c > 0 ? (x < values.mapSize ? x : values.mapSize -1) : 0)][(y + b > 0 ? (y < values.mapSize ? y : values.mapSize -1) : 0)].tileImage.setFitHeight((values.screenWidth > values.screenHeight ? values.screenHeight / (a + 1) : values.screenWidth / (a + 1)));
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
    /*
    public Pane zoomCamera(boolean zoomIn, int zoomLevel){
        if(zoomIn && zoomLevel > 0){
            return zoomLevels[zoomLevel -1];
        }
        else{
            if(zoomLevel < values.mapZoomMax /2)
                return zoomLevels[zoomLevel +1];
        }
        return zoomLevels[zoomLevel]; // if neither, return current
    }
    */
}
