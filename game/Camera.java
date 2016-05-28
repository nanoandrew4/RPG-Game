package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class Camera {

    Pane overworldLayout;
    Values values = Main.values;
    Tile[][] map;

    Camera(Tile[][] map, int x, int y, int zoomLevel){
        overworldLayout = new Pane();
        this.map = map;

        ImageView image;

        if(values.screenWidth > values.screenHeight){
            values.mapTileSize = (int)(values.screenHeight / zoomLevel);
        }
        else {
            values.mapTileSize = (int)(values.screenWidth / zoomLevel);
        }

        // (x + c > 0 ? (x + c < values.mapSize ? x + c : values.mapSize -1) : 0), (y + b > 0 ? (y + b < values.mapSize ? y + b : values.mapSize -1) : 0)

        for(int c = -zoomLevel; c < zoomLevel; c++) {
            for (int b = -zoomLevel; b < zoomLevel; b++) {
                double offset = (Math.abs(c) % 2 == 1 && Math.abs(b) % 2 == 1) ? values.mapTileSize / 2 : 0;
                System.out.println(offset);
                image = new ImageView(new Image("/media/graphics/" + map[(x + c > 0 ? (x + c < values.mapSize ? x + c : values.mapSize -1) : 0)][(y + b > 0 ? (y + b < values.mapSize ? y + b : values.mapSize -1) : 0)].name + ".png", values.mapTileSize, values.mapTileSize, false, false));
                image.relocate(values.mapTileSize * (x + c) - offset, values.mapTileSize * (y + b) - (offset * 2));
                overworldLayout.getChildren().add(image);
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
