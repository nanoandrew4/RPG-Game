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

    public double xOffset = 0;
    public double yOffset = 0;
    
    public int mapLoadArea;

    private Image forestTile;
    private Image villageTile;
    private Image mountainTile;
    private Image grassTile;
    
    OverworldView(){}

    public Scene displayOverworld(Tile[][] tiles, double screenWidth, double screenHeight, int zoom, double mapTileSize, int[] currPos, int mapSize){

        System.out.println("Reloading image array");

        long start = System.currentTimeMillis();
        
        mapLoadArea = 100;

        speedXVal = mapTileSize / 32;
        speedYVal = mapTileSize / 64;

        imageViews = new ImageView[mapLoadArea][mapLoadArea];
        Pane overworldLayout = new Pane();

        loadGraphics(mapTileSize, mapTileSize);

        for (int y = 0; y < imageViews.length; y++) {
            for (int x = 0; x < imageViews.length; x++) {

                int xPos = (currPos[0] + (x - (mapLoadArea / 2)) > 0 ? (currPos[0] + (x - (mapLoadArea / 2)) < mapSize ? currPos[0] + (x - (mapLoadArea / 2)) : mapSize - 1) : 0);
                int yPos = (currPos[1] + (y - (mapLoadArea / 2)) > 0 ? (currPos[1] + (y - (mapLoadArea / 2)) < mapSize ? currPos[1] + (y - (mapLoadArea / 2)) : mapSize - 1) : 0);

                if (tiles[xPos][yPos].type.equalsIgnoreCase("Village"))
                    imageViews[x][y] = new ImageView(villageTile);
                if (tiles[xPos][yPos].type.equalsIgnoreCase("ForestTest"))
                    imageViews[x][y] = new ImageView(forestTile);
                if (tiles[xPos][yPos].type.equalsIgnoreCase("Grass"))
                    imageViews[x][y] = new ImageView(grassTile);
                if (tiles[xPos][yPos].type.equalsIgnoreCase("Mountain"))
                    imageViews[x][y] = new ImageView(mountainTile);

                imageViews[x][y].relocate(0.5 * mapTileSize * ((x - (mapLoadArea / 2)) - (y - (mapLoadArea / 2))) + (screenWidth / 2) - (mapTileSize / 2), 0.25 * mapTileSize * ((x - (mapLoadArea / 2)) + (y - (mapLoadArea / 2))) + (screenHeight / 2) - (mapTileSize / 2));
                overworldLayout.getChildren().add(imageViews[x][y]);
            }
        }

        setMoveAnim();

        System.out.println("Reloading took " + (System.currentTimeMillis() - start) + "ms");

        return new Scene(overworldLayout, screenWidth, screenHeight);
    }

    private void loadGraphics(double width, double height){
        forestTile = new Image("/media/graphics/ForestTest.png", width, height, true, false);
        villageTile = new Image("/media/graphics/Village.png", width, height, true, false);
        mountainTile = new Image("/media/graphics/Mountain.png", width, height, true, false);
        grassTile = new Image("/media/graphics/Grass.png", width, height, true, false);
    }

    public void setMoveAnim(){

        for (int y = 0; y < imageViews.length; y++) {
            for (int x = 0; x < imageViews.length; x++) {

                final int fy = y;
                final int fx = x;

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
                            //System.out.println(xOffset);
                        }
                        lastUpdateTime.set(timestamp);
                    }
                }.start();
            }
        }
    }
}
