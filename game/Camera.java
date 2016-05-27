package game;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class Camera {

    Pane[] zoomLevels;
    Pane overworldLayout;
    Values values = Main.values;
    Tile[][] map;

    Camera(Tile[][] map, int x, int y, int zoomLevel){
        /*
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
        */
        overworldLayout = new Pane();
        this.map = map;

        for(int c = -zoomLevel; c < zoomLevel; c++) {
            for (int b = -zoomLevel; b < zoomLevel; b++) {
                map[(x + c > 0 ? (x < values.mapSize ? x : values.mapSize -1) : 0)][(y + b > 0 ? (y < values.mapSize ? y : values.mapSize -1) : 0)].tileImage.setFitWidth((values.screenWidth > values.screenHeight ? values.screenHeight / (zoomLevel + 1): values.screenWidth / (zoomLevel + 1)));
                map[(x + c > 0 ? (x < values.mapSize ? x : values.mapSize -1) : 0)][(y + b > 0 ? (y < values.mapSize ? y : values.mapSize -1) : 0)].tileImage.setFitHeight((values.screenWidth > values.screenHeight ? values.screenHeight / (zoomLevel + 1) : values.screenWidth / (zoomLevel + 1)));
                overworldLayout.getChildren().add(map[x + c][y + b].tileImage);
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
    public Pane zoomCamera(int zoomLevel, int x, int y){
        for(int c = -zoomLevel; c < zoomLevel; c++) {
            for (int b = -zoomLevel; b < zoomLevel; b++) {
                map[(x + c > 0 ? (x < values.mapSize ? x : values.mapSize -1) : 0)][(y + b > 0 ? (y < values.mapSize ? y : values.mapSize -1) : 0)].tileImage.setFitWidth((values.screenWidth > values.screenHeight ? values.screenHeight / (zoomLevel + 1): values.screenWidth / (zoomLevel + 1)));
                map[(x + c > 0 ? (x < values.mapSize ? x : values.mapSize -1) : 0)][(y + b > 0 ? (y < values.mapSize ? y : values.mapSize -1) : 0)].tileImage.setFitHeight((values.screenWidth > values.screenHeight ? values.screenHeight / (zoomLevel + 1) : values.screenWidth / (zoomLevel + 1)));
                overworldLayout.getChildren().add(map[x + c][y + b].tileImage);
            }
        }

        return overworldLayout;
    }
    */
}
