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

        ImageView imageView;
        Image image = null;

        if(values.screenWidth > values.screenHeight){
            values.mapTileSize = (int)(values.screenHeight / zoomLevel);
        }
        else {
            values.mapTileSize = (int)(values.screenWidth / zoomLevel);
        }

        // (x + c > 0 ? (x + c < values.mapSize ? x + c : values.mapSize -1) : 0), (y + b > 0 ? (y + b < values.mapSize ? y + b : values.mapSize -1) : 0)

        long start  = System.currentTimeMillis();

        for(int b = -zoomLevel; b < zoomLevel; b++) {
            for (int c = -zoomLevel; c < zoomLevel; c++) {
                //System.out.println(offset);
                int xPos = (x + c > 0 ? (x + c < values.mapSize ? x + c : values.mapSize -1) : 0);
                int yPos = (y + b > 0 ? (y + b < values.mapSize ? y + b : values.mapSize -1) : 0);
                if(map[xPos][yPos].name.equalsIgnoreCase("Village"))
                    image = values.villageTile;
                if(map[xPos][yPos].name.equalsIgnoreCase("ForestTest"))
                    image = values.forestTile;

                imageView = new ImageView(image); // needs resizing, can't do with preloaded
                imageView.relocate(0.5 * values.mapTileSize * ((x + c) - (y + b)) + (values.screenWidth / 2) - (values.mapTileSize / 2), 0.25 * values.mapTileSize * ((x + c) + (y + b)) + (values.screenHeight / 2) - (values.mapTileSize / 2));
                //map[(x + c > 0 ? (x + c < values.mapSize ? x + c : values.mapSize -1) : 0)][(y + b > 0 ? (y + b < values.mapSize ? y + b : values.mapSize -1) : 0)].tileImage.relocate(0.5 * values.mapTileSize * ((x + c) - (y + b)) + (values.screenWidth / 2) - (values.mapTileSize / 2), 0.25 * values.mapTileSize * ((x + c) + (y + b)) + (values.screenHeight / 2) - (values.mapTileSize / 2));
                overworldLayout.getChildren().add(imageView);
            }
        }

        System.out.println("Instantiating all images took " + (double)(System.currentTimeMillis() - start) / 1000);
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
