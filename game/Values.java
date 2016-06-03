package game;

public class Values {
    public double screenWidth, screenHeight, mapTileSize, scrollOffset;
    public int mapSize = 1000, mapZoomMax = 15 /* in each direction */, zoom = 8;
    public int[] currPos = new int[2];
    public double xOffset = 0, yOffset = 0; // from init pos, to calculate if you have moved to a different tile

    // init related variables

    public boolean startGame = false;
    public boolean initComplete = false;

    Values(){}
}
