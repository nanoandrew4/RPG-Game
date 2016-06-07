package game;

import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class OverworldView{

    public double speedXVal;
    public double speedYVal;
    public final DoubleProperty speedX = new SimpleDoubleProperty();
    public final DoubleProperty speedY = new SimpleDoubleProperty();
    private final LongProperty lastUpdateTime = new SimpleLongProperty();

    private ImageView[][] imageViews;
    private Pane overworldLayout;

    private int areaMultiplier = 2;

    public double xOffset = 0;
    public double yOffset = 0;

    private Image forestTile;
    private Image villageTile;
    private Image mountainTile;
    private Image grassTile;
    
    OverworldView(int mapSize){imageViews = new ImageView[mapSize][mapSize];}

    public Scene initDisplay(Tile[][] tiles, double screenWidth, double screenHeight, int zoom, double mapTileSize, int[] currPos, int mapSize){

        System.out.println("Reloading image array");

        long start = System.currentTimeMillis();

        speedXVal = mapTileSize / 16;
        speedYVal = mapTileSize / 32;

        overworldLayout = new Pane();

        loadGraphics(mapTileSize, mapTileSize);

        for (int y = -zoom * areaMultiplier * 2; y < zoom * areaMultiplier * 2; y++) {
            for (int x = -zoom * areaMultiplier; x < zoom * areaMultiplier; x++) {

                if(!(currPos[0] + x < 0 || currPos[1] + y < 0 || currPos[0] + x > mapSize || currPos[1] + y > mapSize)) {

                    int xPos = currPos[0] + x;
                    int yPos = currPos[1] + y;

                    genTile(xPos, yPos, tiles);

                    imageViews[xPos][yPos].relocate(0.5 * mapTileSize * (x - y) + (screenWidth) - (mapTileSize / 2), 0.25 * mapTileSize * (x + y)/* - (screenHeight / 2)*/ - (mapTileSize / 2));
                    overworldLayout.getChildren().add(imageViews[xPos][yPos]);

                    setMoveAnim(xPos, yPos);
                }
            }
        }

        System.out.println("Init load took " + (System.currentTimeMillis() - start) + "ms");

        return new Scene(overworldLayout, screenWidth, screenHeight);
    }

    private void genTile(int xPos, int yPos, Tile[][] tiles){
        if (tiles[xPos][yPos].type.equalsIgnoreCase("Village"))
            imageViews[xPos][yPos] = new ImageView(villageTile);
        if (tiles[xPos][yPos].type.equalsIgnoreCase("ForestTest"))
            imageViews[xPos][yPos] = new ImageView(forestTile);
        if (tiles[xPos][yPos].type.equalsIgnoreCase("Grass"))
            imageViews[xPos][yPos] = new ImageView(grassTile);
        if (tiles[xPos][yPos].type.equalsIgnoreCase("Mountain"))
            imageViews[xPos][yPos] = new ImageView(mountainTile);
    }

    public void addRow(Tile[][] tiles, double screenWidth, double screenHeight, int zoom, double mapTileSize, int[] currPos, int mapSize, boolean top){
        System.out.println("Adding row");
        if((top && currPos[1] + (zoom * areaMultiplier * 2) + 1 < mapSize) || (!top && currPos[1] + (zoom * areaMultiplier * 2) - 1 > 0)) {
            int y = top ? -zoom * areaMultiplier : zoom * areaMultiplier;
            int yPos = currPos[1] + y;
            for (int x = -zoom * areaMultiplier; x < zoom * areaMultiplier; x++) {
                if (!(currPos[0] + x < 0 || currPos[0] + x > mapSize)) {
                    int xPos = currPos[0] + x;
                    genTile(xPos, yPos, tiles);
                    //imageViews[xPos][yPos].relocate(top ? imageViews[xPos][yPos + 1].getTranslateX() + 100 : imageViews[xPos][yPos - 1].getTranslateX() - 100, top ? imageViews[xPos][yPos + 1].getTranslateY() + 50 : imageViews[xPos][yPos - 1].getTranslateY() - 50);
                    imageViews[xPos][yPos].relocate(0.5 * mapTileSize * (x - y) + (screenWidth) - (mapTileSize / 2), 0.25 * mapTileSize * (x + y)/* - (screenHeight / 2)*/ - (mapTileSize / 2));
                    overworldLayout.getChildren().add(imageViews[xPos][yPos]);
                    setMoveAnim(xPos, yPos);
                }
            }
            removeRow(!top, currPos, zoom, mapSize);
        }
    }

    public void addColumn(Tile[][] tiles, double screenWidth, double screenHeight, int zoom, double mapTileSize, int[] currPos, int mapSize, boolean right){
        System.out.println("Adding column");
        if((right && currPos[0] + (zoom * areaMultiplier) + 1 < mapSize) || (!right && currPos[0] + (zoom * areaMultiplier) - 1 > 0)) {
            int x = right ? zoom * areaMultiplier / 2 : -zoom * areaMultiplier / 2;
            int xPos = currPos[0] + x;
            for (int y = -zoom * areaMultiplier * 2; y < zoom * areaMultiplier * 2; y++) {
                if (!(currPos[1] + y < 0 || currPos[1] + y > mapSize)) {
                    int yPos = currPos[1] + y;
                    genTile(xPos, yPos, tiles);
                    //imageViews[xPos][yPos].relocate(right ? imageViews[xPos - 1][yPos].getTranslateX() + 100 : imageViews[xPos + 1][yPos].getTranslateX() - 100, right ? imageViews[xPos - 1][yPos].getTranslateY() + 50 : imageViews[xPos + 1][yPos].getTranslateY() - 50);
                    imageViews[xPos][yPos].relocate(0.5 * mapTileSize * (x - y) + (screenWidth) - (mapTileSize / 2), 0.25 * mapTileSize * (x + y)/* - (screenHeight / 2)*/ - (mapTileSize / 2));
                    overworldLayout.getChildren().add(imageViews[xPos][yPos]);
                    setMoveAnim(xPos, yPos);
                }
            }
        }
        removeColumn(!right, currPos, zoom, mapSize);
    }

    private void removeRow(boolean top, int[] currPos, int zoom, int mapSize){
        if((top && currPos[1] + (zoom * areaMultiplier) + 1 < mapSize) || (!top && currPos[1] + (zoom * areaMultiplier) - 1 > 0)) {
            int y = top ? -zoom * areaMultiplier : zoom * areaMultiplier;
            int yPos = currPos[1] + y;
            for (int x = -zoom * areaMultiplier; x < zoom * areaMultiplier; x++) {
                if (!(currPos[0] + x < 0 || currPos[0] + x > mapSize)) {
                    int xPos = currPos[0] + x;
                    overworldLayout.getChildren().remove(imageViews[xPos][yPos]);
                    imageViews[xPos][yPos] = null;
                }
            }
        }
    }

    private void removeColumn(boolean right, int[] currPos, int zoom, int mapSize){
        if((right && currPos[0] + (zoom * areaMultiplier) + 1 < mapSize) || (!right && currPos[0] + (zoom * areaMultiplier) - 1 > 0)) {
            int x = right ? zoom * areaMultiplier / 2 : -zoom * areaMultiplier / 2;
            int xPos = currPos[0] + x;
            for (int y = -zoom * areaMultiplier * 2; y < zoom * areaMultiplier * 2; y++) {
                if (!(currPos[1] + y < 0 || currPos[1] + y > mapSize)) {
                    int yPos = currPos[1] + y;
                    overworldLayout.getChildren().remove(imageViews[xPos][yPos]);
                    imageViews[xPos][yPos] = null;
                }
            }
        }
    }

    private void loadGraphics(double width, double height){
        forestTile = new Image("/media/graphics/ForestTest.png", width, height, true, false);
        villageTile = new Image("/media/graphics/Village.png", width, height, true, false);
        mountainTile = new Image("/media/graphics/Mountain.png", width, height, true, false);
        grassTile = new Image("/media/graphics/Grass.png", width, height, true, false);
    }

    private void setMoveAnim(int xPos, int yPos) {
        final int fx = xPos;
        final int fy = yPos;
        new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    if(imageViews[fx][fy] == null) {
                        stop();
                        return;
                    }
                    final double oldX = imageViews[fx][fy].getTranslateX();
                    final double newX = oldX + speedX.get();
                    final double oldY = imageViews[fx][fy].getTranslateY();
                    final double newY = oldY + speedY.get();
                    imageViews[fx][fy].setTranslateX(newX);
                    imageViews[fx][fy].setTranslateY(newY);
                    xOffset = imageViews[fx][fy].getTranslateX();
                    yOffset = imageViews[fx][fy].getTranslateY();
                }
                lastUpdateTime.set(timestamp);
            }
        }.start();
    }
}
