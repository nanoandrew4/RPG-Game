/* 
    Graphical view for InMap.
 */

package inmap;

import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;

class Images {
    //contains images
    Image stoneFloor;
    Image stoneWall;
    Image stoneTop;
    Image door;
    Image opendoor;
    Image stairs;
    
    Image hero;
    Image spider;
    Image bat;
    Image slug;
    Image goblin;
    
    Image health;
    
    Images(double width, double height) {
        stoneFloor = new Image("/media/graphics/inmap/StoneFloor.png", width, height, true, false);
        stoneWall = new Image("/media/graphics/inmap/StoneWall.png", width, height, true, false);
        stoneTop = new Image("/media/graphics/inmap/StoneTop.png", width, height, true, false);
        door = new Image("/media/graphics/inmap/door.png", width, height, true, false);
        opendoor = new Image("/media/graphics/inmap/opendoor.png", width, height, true, false);
        stairs = new Image("/media/graphics/inmap/stairs.png", width, height, true, false);

        
        hero = new Image("/media/graphics/inmap/trump.png", width, width, true, false);
        spider = new Image("/media/graphics/inmap/spooder.png", width, width, true, false);
        bat = new Image("/media/graphics/inmap/batman.png", width, width, true, false);
        slug = new Image("/media/graphics/inmap/slug.png", width, width, true, false);
        goblin = new Image("/media/graphics/inmap/harambe.png", width, width, true, false);
        
        health = new Image("/media/graphics/inmap/health.jpg", width, width/5, true, false);
    }
}

public class InMapView {
    //vars
    Images images;
    ImageView[][][] imageViews;
    Text floorT;
    public double screenWidth, screenHeight;
    private Pane inmapLayout;
    public final DoubleProperty speedX = new SimpleDoubleProperty();
    public final DoubleProperty speedY = new SimpleDoubleProperty();
    public double speedXVal;
    public double speedYVal;
    private final LongProperty lastUpdateTime = new SimpleLongProperty();
    
    //constructor
    InMapView(int floorWidth, int floorLength, double screenWidth, double screenHeight) {
        //0: tiles
        //1: items
        //2: characters
        //3: health
        //4: overlay
        //5: fog
        imageViews = new ImageView[floorWidth][floorLength][6];
        images = new Images(64, 96);
        inmapLayout = new Pane();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        speedXVal = 64;
        speedYVal = 64;
    }
    
    //initialize display
    public Scene initDisplay(Floor floor) {
        
        //tiles
        for(int x = 0; x < floor.sizeX; x++) {
            for(int y = 0; y < floor.sizeY; y++) {
                imageViews[x][y][0] = new ImageView(genTile(x, y, floor));
                
                imageViews[x][y][0].relocate(64*x, 64*y - 32);
                inmapLayout.getChildren().add(imageViews[x][y][0]);
            }
        }
        
        //items
        //nothing here yet
        
        //characters
        for(int x = 0; x < floor.sizeX; x++) {
            for(int y = 0; y < floor.sizeY; y++) {
                imageViews[x][y][2] = new ImageView(genChar(x, y, floor));

                imageViews[x][y][2].setFitWidth(64);
                imageViews[x][y][2].setFitHeight(64);

                imageViews[x][y][2].relocate(64*x, 64*y);
                inmapLayout.getChildren().add(imageViews[x][y][2]);
            }
        }
        
        //health
        for(int x = 0; x < floor.sizeX; x++) {
            for(int y = 0; y < floor.sizeY; y++) {
                imageViews[x][y][3] = new ImageView(images.health);
                imageViews[x][y][3].relocate(64*x, 64*y + 64);
                if(!floor.chars[x][y].exists)
                    imageViews[x][y][3].setVisible(false);
                
                inmapLayout.getChildren().add(imageViews[x][y][3]);
            }
        }
        
        //floor text
        floorT = new Text(40, 40, ("Floor " + floor.number));
        floorT.setFont(Font.font(null, FontWeight.BOLD, 48));
        floorT.setFill(Paint.valueOf("WHITE"));
        inmapLayout.getChildren().add(floorT);
        
        return new Scene(inmapLayout, screenWidth, screenHeight);
    }
    
    //update display
    public void updateDisplay(Floor floor) {
        
        //tiles
        for(int x = 0; x < floor.sizeX; x++) {
            for(int y = 0; y < floor.sizeY; y++) {
                imageViews[x][y][0].setImage(genTile(x, y, floor));
            }
        }
        
        //items
        //nothing here yet
        
        //characters
        for(int x = 0; x < floor.sizeX; x++) {
            for(int y = 0; y < floor.sizeY; y++) {
                imageViews[x][y][2].setImage(genChar(x, y, floor));
            }
        }
        
        //health
        for(int x = 0; x < floor.sizeX; x++) {
            for(int y = 0; y < floor.sizeY; y++) {
                if(floor.chars[x][y].exists) {
                    imageViews[x][y][3].setVisible(true);
                    imageViews[x][y][3].setFitWidth(64 * (double)floor.chars[x][y].currentHP / 
                            floor.chars[x][y].maxHP);
                }
                else
                    imageViews[x][y][3].setVisible(false);
            }
        }
        
        //floor text
        floorT.setText("Floor " + floor.number);
    }
    
    //choose image based on data
    private Image genTile(int x, int y, Floor floor) {
        String type = floor.tiles[x][y].name;
        
        if(type == null)
            return null;
        else if(type.equalsIgnoreCase("wall"))
            if(y < floor.sizeY - 1 && floor.tiles[x][y+1].name.equalsIgnoreCase("wall"))
                return images.stoneTop;
            else return images.stoneWall;
        else if(type.equalsIgnoreCase("door") && floor.tiles[x][y].isWall)
            return images.door;
        else if(type.equalsIgnoreCase("door"))
            return images.opendoor;
        else if(type.equalsIgnoreCase("stairs"))
            return images.stairs;
        else if(type.equalsIgnoreCase(""))
            return images.stoneFloor;
        else
            return null;
    }
    
    //choose character image based on model data
    private Image genChar(int x, int y, Floor floor) {
        String type = floor.chars[x][y].name;
        
        if(type == null)
            return null;
        if(type.equalsIgnoreCase("hero"))
            return images.hero;
        else if(type.equalsIgnoreCase("spider"))
            return images.spider;
        else if(type.equalsIgnoreCase("slug"))
            return images.slug;
        else if(type.equalsIgnoreCase("bat"))
            return images.bat;
        else if(type.equalsIgnoreCase("goblin"))
            return images.goblin;
        else return null;
    }
    
    //imageview animation
    private void setMoveAnim(ImageView imageView) {
        new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    if (imageView == null) { // if redundant delete if statement
                        stop();
                        return;
                    }
                    final double oldX = imageView.getTranslateX();
                    final double newX = oldX + speedX.get();
                    final double oldY = imageView.getTranslateY();
                    final double newY = oldY + speedY.get();
                    imageView.setTranslateX(newX);
                    imageView.setTranslateY(newY);
//                    xOffset = imageView.getTranslateX();
//                    yOffset = imageView.getTranslateY();
                }
                lastUpdateTime.set(timestamp);
            }
        }.start();
    }
    
    //pane animation
    private void setMoveAnim(Pane pane) {
        new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    if (pane == null) { // if redundant delete if statement
                        stop();
                        return;
                    }
                    final double oldX = pane.getTranslateX();
                    final double newX = oldX + speedX.get();
                    final double oldY = pane.getTranslateY();
                    final double newY = oldY + speedY.get();
                    pane.setTranslateX(newX);
                    pane.setTranslateY(newY);
                    pane.getTranslateX();
                    pane.getTranslateY();
                }
                lastUpdateTime.set(timestamp);
            }
        }.start();
    }
}

