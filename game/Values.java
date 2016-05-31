package game;

import javafx.scene.image.Image;

public class Values {
    public double screenWidth, screenHeight, mapTileSize;
    public int mapSize = 100, mapZoomMax = 8 /* in each direction */, initxPos, inityPos;

    Image forestTile;
    Image villageTile;

    Values(){}

    public void loadGraphics(){
        forestTile = new Image("/media/graphics/ForestTest.png");
        villageTile = new Image("/media/graphics/Village.png");
    }
}
