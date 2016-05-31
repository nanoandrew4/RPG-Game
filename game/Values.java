package game;

import javafx.scene.image.Image;

public class Values {
    public double screenWidth, screenHeight, mapTileSize;
    public int mapSize = 1000, mapZoomMax = 15 /* in each direction */, zoom = mapZoomMax / 2, initxPos, inityPos;
    public int[] currPos = new int[2];
    public int xOffset = 0, yOffset = 0; // from init pos, to calculate if you have moved to a different tile
    boolean keyPressed;

    Image forestTile;
    Image villageTile;
    Image mountainTile;
    Image grassTile;

    Values(){}

    public void loadGraphics(double width, double height){
        forestTile = new Image("/media/graphics/ForestTest.png", width, height, true, false);
        villageTile = new Image("/media/graphics/Village.png", width, height, true, false);
        mountainTile = new Image("/media/graphics/Mountain.png", width, height, true, false);
        grassTile = new Image("/media/graphics/Grass.png", width, height, true, false);
    }
}
