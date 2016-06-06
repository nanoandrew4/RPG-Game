package game;

import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class OverworldView{

    public double speedXVal;
    public double speedYVal;
    public final DoubleProperty speedX = new SimpleDoubleProperty();
    public final DoubleProperty speedY = new SimpleDoubleProperty();
    public final LongProperty lastUpdateTime = new SimpleLongProperty();

    private ImageView[][] imageViews;

    private int initAreaMultiplier = 4;

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

        Pane overworldLayout = new Pane();

        loadGraphics(mapTileSize, mapTileSize);

        for (int y = -zoom * initAreaMultiplier; y < zoom * initAreaMultiplier; y++) {
            for (int x = -zoom * initAreaMultiplier; x < zoom * initAreaMultiplier; x++) {

                if(!(currPos[0] + x < 0 || currPos[1] + y < 0 || currPos[0] + x > mapSize || currPos[1] + y > mapSize)) {

                    int xPos = currPos[0] + x;
                    int yPos = currPos[1] + y;

                    if (tiles[xPos][yPos].type.equalsIgnoreCase("Village"))
                        imageViews[xPos][yPos] = new ImageView(villageTile);
                    if (tiles[xPos][yPos].type.equalsIgnoreCase("ForestTest"))
                        imageViews[xPos][yPos] = new ImageView(forestTile);
                    if (tiles[xPos][yPos].type.equalsIgnoreCase("Grass"))
                        imageViews[xPos][yPos] = new ImageView(grassTile);
                    if (tiles[xPos][yPos].type.equalsIgnoreCase("Mountain"))
                        imageViews[xPos][yPos] = new ImageView(mountainTile);

                    imageViews[xPos][yPos].relocate(0.5 * mapTileSize * (x - y) + (screenWidth / 2) - (mapTileSize / 2), 0.25 * mapTileSize * (x + y) + (screenHeight / 2) - (mapTileSize / 2));
                    overworldLayout.getChildren().add(imageViews[xPos][yPos]);

                    setMoveAnim(xPos, yPos);
                }
            }
        }

        System.out.println("Reloading took " + (System.currentTimeMillis() - start) + "ms");

        return new Scene(overworldLayout, screenWidth, screenHeight);
    }

    private void loadGraphics(double width, double height){
        forestTile = new Image("/media/graphics/ForestTest.png", width, height, true, false);
        villageTile = new Image("/media/graphics/Village.png", width, height, true, false);
        mountainTile = new Image("/media/graphics/Mountain.png", width, height, true, false);
        grassTile = new Image("/media/graphics/Grass.png", width, height, true, false);
    }

    public void setMoveAnim(int xPos, int yPos) {
        final int fx = xPos;
        final int fy = yPos;
        new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
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
