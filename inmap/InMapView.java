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
    Image stairsup;
    Image stairsdown;
    
    Image hero;
    Image spider;
    Image bat;
    Image slug;
    Image goblin;
    
    Image health;
    
    Image black;
    
    Images(double width, double height) {
        stoneFloor = new Image("/media/graphics/inmap/StoneFloor.png", width, height, true, false);
        stoneWall = new Image("/media/graphics/inmap/StoneWall.png", width, height, true, false);
        stoneTop = new Image("/media/graphics/inmap/StoneTop.png", width, height, true, false);
        door = new Image("/media/graphics/inmap/door.png", width, height, true, false);
        opendoor = new Image("/media/graphics/inmap/opendoor.png", width, height, true, false);
        stairsup = new Image("/media/graphics/inmap/stairsup.png", width, height, true, false);
        stairsdown = new Image("/media/graphics/inmap/stairsdown.png", width, height, true, false);

        
        hero = new Image("/media/graphics/inmap/trump.png", width, width, true, false);
        spider = new Image("/media/graphics/inmap/spooder.png", width, width, true, false);
        bat = new Image("/media/graphics/inmap/batman.png", width, width, true, false);
        slug = new Image("/media/graphics/inmap/slug.png", width, width, true, false);
        goblin = new Image("/media/graphics/inmap/harambe.png", width, width, true, false);
        
        health = new Image("/media/graphics/inmap/health.jpg", width, width/5, true, false);
        
        black = new Image("/media/graphics/inmap/black.jpg", width, height, true, false);
    }
}

public class InMapView {
    //vars
    Images images;
    ImageView[][][] imageViews;
    Text floorT, levelT, rip;
    public double screenWidth, screenHeight;
    private Pane inmapLayout;
    public final DoubleProperty speedX = new SimpleDoubleProperty();
    public final DoubleProperty speedY = new SimpleDoubleProperty();
    public double speedXVal;
    public double speedYVal;
    private final LongProperty lastUpdateTime = new SimpleLongProperty();
    double width, height;
    final double zoom;
    
    //constructor
    InMapView(double screenWidth, double screenHeight) {
        //0: tiles
        //1: items
        //2: characters
        //3: health
        //4: overlay
        //5: fog
        //setTileSize();
        imageViews = new ImageView[22][14][6];
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        zoom = 12;
        setTileSize();
        images = new Images(width, height);
        inmapLayout = new Pane();
        speedXVal = 64;
        speedYVal = 64;
    }

    //set tile size
    private void setTileSize() {
        if (screenWidth > screenHeight) {
            width = (screenHeight / zoom);
            height = (screenHeight / zoom) * 1.5;
        } else {
            width = (screenWidth / zoom);
            height = (screenWidth / zoom) * 1.5;
        }
    }
    
    //initialize display
    public Scene initDisplay(Floor floor) {
        
        //tiles
        for(int x = floor.party[0].x - 10; x < floor.party[0].x + 12; x++) {
            for(int y = floor.party[0].y - 6; y < floor.party[0].y + 8; y++) {
                imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][0] = new ImageView(genTile(x, y, floor));
                
                imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][0].relocate(64*(x-floor.party[0].x+10) - 32, 64*(y-floor.party[0].y+6) - 64);
                inmapLayout.getChildren().add(imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][0]);
            }
        }
        
        //characters
        for(int x = floor.party[0].x - 10; x < floor.party[0].x + 12; x++) {
            for(int y = floor.party[0].y - 6; y < floor.party[0].y + 8; y++) {
                imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][2] = new ImageView(genChar(x, y, floor));

                imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][2].setFitWidth(64);
                imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][2].setFitHeight(64);

                imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][2].relocate(64*(x-floor.party[0].x+10) - 32, 64*(y-floor.party[0].y+6) - 32);
                inmapLayout.getChildren().add(imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][2]);
            }
        }
        
        //health
        for(int x = floor.party[0].x - 10; x < floor.party[0].x + 12; x++) {
            for(int y = floor.party[0].y - 6; y < floor.party[0].y + 8; y++) {
                imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][3] = new ImageView(images.health);
                imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][3].relocate(64*(x-floor.party[0].x+10) - 32, 64*(y-floor.party[0].y+6) + 32);
                if(x < 0 || x >= floor.sizeX || y < 0 || y >= floor.sizeY || !floor.chars[x][y].exists)
                    imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][3].setVisible(false);
                
                inmapLayout.getChildren().add(imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][3]);
            }
        }
        
        //floor text
        floorT = new Text(40, 40, ("Floor " + floor.location.currentFloor));
        floorT.setFont(Font.font(null, FontWeight.BOLD, 48));
        floorT.setFill(Paint.valueOf("WHITE"));
        inmapLayout.getChildren().add(floorT);
        
        //level text
        levelT = new Text(40, 60, ("Level " + floor.party[0].LVL));
        levelT.setFont(Font.font(null, FontWeight.NORMAL, 24));
        levelT.setFill(Paint.valueOf("WHITE"));
        inmapLayout.getChildren().add(levelT);
        
        rip = new Text(360, 340, ("  GAME OVER\nR TO RESTART"));
        rip.setFont(Font.font(null, FontWeight.BOLD, 80));
        rip.setFill(Paint.valueOf("WHITE"));
        rip.setVisible(false);
        inmapLayout.getChildren().add(rip);
        
        return new Scene(inmapLayout, screenWidth, screenHeight);
    }
    
    //update display
    public void updateDisplay(Floor floor) {
        //tiles
        for(int x = floor.party[0].x - 10; x < floor.party[0].x + 12; x++) {
            for(int y = floor.party[0].y - 6; y < floor.party[0].y + 8; y++) {
                imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][0].setImage(genTile(x, y, floor));
            }
        }
        
        //characters
        for(int x = floor.party[0].x - 10; x < floor.party[0].x + 12; x++) {
            for(int y = floor.party[0].y - 6; y < floor.party[0].y + 8; y++) {
                imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][2].setImage(genChar(x, y, floor));
            }
        }
        
        //health
        for(int x = floor.party[0].x - 10; x < floor.party[0].x + 12; x++) {
            for(int y = floor.party[0].y - 6; y < floor.party[0].y + 8; y++) {
                if(x >= 0 && x < floor.sizeX && y >= 0 && y < floor.sizeY && floor.chars[x][y].exists) {
                    imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][3].setVisible(true);
                    imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][3].setFitWidth(64 * 
                            (double)floor.chars[x][y].currentHP / floor.chars[x][y].maxHP);
                }
                else
                    imageViews[x-floor.party[0].x+10][y-floor.party[0].y+6][3].setVisible(false);
            }
        }
        
        //floor text
        floorT.setText("Floor " + floor.location.currentFloor);
        
        //level text
        levelT.setText("Level " + floor.party[0].LVL);
        
        //rip
        if(!floor.party[0].exists)
            rip.setVisible(true);
        else
            rip.setVisible(false);
    }
    
    //choose image based on data
    private Image genTile(int x, int y, Floor floor) {
        if(x >= 0 && x < floor.sizeX && y >= 0 && y < floor.sizeY) {
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
            else if(type.equalsIgnoreCase("stairsup"))
                return images.stairsup;
            else if(type.equalsIgnoreCase("stairsdown"))
                return images.stairsdown;
            else if(type.equalsIgnoreCase(""))
                return images.stoneFloor;
            else
                return images.black;
        }
        else return images.black;
    }
    
    //choose character image based on model data
    private Image genChar(int x, int y, Floor floor) {
        if(x >= 0 && x < floor.sizeX && y >= 0 && y < floor.sizeY) {
            String type = floor.chars[x][y].name;

            if(!floor.chars[x][y].exists)
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
        else return null;
    }
}

