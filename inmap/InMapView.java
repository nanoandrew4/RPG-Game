/* 
    Graphical view for InMap.
 */

package inmap;

import java.awt.Point;
import javafx.event.EventType;

//import javafx.animation.AnimationTimer;
//import javafx.beans.property.DoubleProperty;
//import javafx.beans.property.LongProperty;
//import javafx.beans.property.SimpleDoubleProperty;
//import javafx.beans.property.SimpleLongProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;

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
    Image adelf;
    Image bat;
    Image bell;
    Image chest;
    Image chick;
    Image chicken;
    Image fishman;
    Image flan;
    Image ghost;
    Image kingslime;
    Image longcat;
    Image manta;
    Image mote;
    Image skelebro;
    Image snail;
    Image spookyslime;
    Image spookyslug;
    
//    Image spider;
//    Image slug;
//    Image goblin;
//    Image boss;
    
    Image npc1;
    Image npc2;
    
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

        npc1 = new Image("/media/graphics/inmap/elonaSin.png", width, width, true, false);
        npc2 = new Image("/media/graphics/inmap/elonaGilbert.png", width, width, true, false);
//        spider = new Image("/media/graphics/inmap/spooder.png", width, width, true, false);
//        bat = new Image("/media/graphics/inmap/batman.png", width, width, true, false);
//        slug = new Image("/media/graphics/inmap/slug.png", width, width, true, false);
//        goblin = new Image("/media/graphics/inmap/harambe.png", width, width, true, false);
//        boss = new Image("/media/graphics/inmap/clinton.jpg", width, width, true, false);
//        hero = new Image("/media/graphics/inmap/knight.png", width, width, true, false);
        hero = new Image("/media/graphics/inmap/trump.png", width, width, true, false);
        adelf = new Image("/media/graphics/inmap/adelf.png", width, width, true, false);
        bat = new Image("/media/graphics/inmap/bat.png", width, width, true, false);
        bell = new Image("/media/graphics/inmap/bell.png", width, width, true, false);
        chest = new Image("/media/graphics/inmap/chest.png", width, width, true, false);
        chick = new Image("/media/graphics/inmap/chick.png", width, width, true, false);
        chicken = new Image("/media/graphics/inmap/chicken.png", width, width, true, false);
        fishman = new Image("/media/graphics/inmap/fishman.png", width, width, true, false);
        flan = new Image("/media/graphics/inmap/flan.png", width, width, true, false);
        ghost = new Image("/media/graphics/inmap/ghost.png", width, width, true, false);
        kingslime = new Image("/media/graphics/inmap/kingslime.png", width, width, true, false);
        longcat = new Image("/media/graphics/inmap/longcat.png", width, width, true, false);
        manta = new Image("/media/graphics/inmap/manta.png", width, width, true, false);
        mote = new Image("/media/graphics/inmap/mote.png", width, width, true, false);
        skelebro = new Image("/media/graphics/inmap/skelebro.png", width, width, true, false);
        snail = new Image("/media/graphics/inmap/snail.png", width, width, true, false);
        spookyslime = new Image("/media/graphics/inmap/spookyslime.png", width, width, true, false);
        spookyslug = new Image("/media/graphics/inmap/spookyslug.png", width, width, true, false);
        
        health = new Image("/media/graphics/inmap/health.jpg", width, width/5, true, false);
        
        black = new Image("/media/graphics/inmap/black.jpg", width, height, true, false);
    }
}

class InMapView {
    //vars
    Images images;
    ImageView[][][] imageViews;
    public double screenWidth, screenHeight;
    private Pane inmapLayout, menuBox, UIBox;
    public double speedXVal;
    public double speedYVal;
    double width, height;
    final double zoom;
    
    private Text rip; //rip text
    private Text charT, levelT, goldT; //quickinfo text
    private Text diffT, floorT, nameT, typeT; //location text
    
    private Rectangle menuFocus;
    private Circle menuCursor;
    
    //animation
//    private final LongProperty lastUpdateTime = new SimpleLongProperty();
//    public final DoubleProperty speedX = new SimpleDoubleProperty();
//    public final DoubleProperty speedY = new SimpleDoubleProperty();
//    public double xOffset;
//    public double yOffset;
    
    //constructor
    InMapView(double screenWidth, double screenHeight) {
        //0: tiles
        //1: items
        //2: characters
        //3: health
        //4: overlay
        //5: fog
        //setTileSize();
        imageViews = new ImageView[24][16][6];
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        zoom = 12;
        setTileSize();
        images = new Images(width, height);
        inmapLayout = new Pane();
        UIBox = new Pane();
        menuBox = new Pane();
        speedXVal = 16;
        speedYVal = 16;
        
        //initialize UI
        Rectangle box = new Rectangle(screenWidth/5, screenHeight*1/3, Paint.valueOf("WHITE"));
        box.setOpacity(.7);
        box.relocate(screenWidth*4/5, 0); //top right
        
        nameT = new Text(screenWidth*4/5+20, 40, "");
        nameT.setFont(Font.font(null, FontWeight.BOLD, 20));
        
        typeT = new Text(screenWidth*4/5+20, 90, "");
        typeT.setFont(Font.font(null, FontWeight.NORMAL, 18));
        
        diffT = new Text(screenWidth*4/5+20, 140, "");
        diffT.setFont(Font.font(null, FontWeight.NORMAL, 18));
        
        floorT = new Text(screenWidth*4/5+20, 190, "");
        floorT.setFont(Font.font(null, FontWeight.NORMAL, 18));

        Rectangle box2 = new Rectangle(screenWidth/14, screenHeight*3/5, Paint.valueOf("WHITE"));
        box2.setOpacity(.7);
        box2.relocate(0, screenHeight/8); //top left
        
        Rectangle box3 = new Rectangle(screenWidth/4, screenHeight/4, Paint.valueOf("WHITE"));
        box3.setOpacity(.7);
        box3.relocate(0, screenHeight*3/4); //bottom left
        
        charT = new Text(30, screenHeight*3/4+40, "");
        charT.setFont(Font.font(null, FontWeight.NORMAL, 24));
        
        levelT = new Text(30, screenHeight*3/4+90, "");
        levelT.setFont(Font.font(null, FontWeight.NORMAL, 24));
        goldT = new Text(30, screenHeight*3/4+140, "");
        goldT.setFont(Font.font(null, FontWeight.NORMAL, 24));
        
        UIBox.getChildren().addAll(box, box2, box3, nameT, typeT, diffT, floorT, charT, levelT, goldT);
        
        //initialize menu
        Rectangle box4 = new Rectangle(screenWidth*4/5, screenHeight*2/3, Paint.valueOf("WHITE"));
        box4.relocate(screenWidth/10, screenHeight/6);
        
        menuFocus = new Rectangle(screenWidth*4/25, screenHeight/10, Paint.valueOf("PINK"));
        menuFocus.relocate(screenWidth/10, screenHeight/6);
        
        menuCursor = new Circle(screenWidth/8, screenHeight/6+40, 3, Paint.valueOf("RED"));
        menuCursor.setOpacity(0);
        
        Text[] menuText = new Text[5];
        for(int i = 0; i < 5; i++) {
            menuText[i] = new Text(screenWidth/8+screenWidth*4*i/25, screenHeight/6+40, "");
            menuText[i].setFont(Font.font("Arial", FontWeight.BOLD, 28));
        }
        menuText[0].setText("  INV");
        menuText[1].setText(" CHAR");
        menuText[2].setText(" PARTY");
        menuText[3].setText(" NOTES");
        menuText[4].setText("OPTIONS");
        
        menuBox.getChildren().addAll(box4, menuFocus, menuCursor);
        menuBox.getChildren().addAll(menuText);
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
        for(int x = floor.party[0].x - 11; x < floor.party[0].x + 13; x++) {
            for(int y = floor.party[0].y - 7; y < floor.party[0].y + 9; y++) {
                imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][0] = new ImageView(genTile(x, y, floor));
                
                imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][0].relocate(64*(x-floor.party[0].x+10) - 32, 64*(y-floor.party[0].y+6) - 64);
                inmapLayout.getChildren().add(imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][0]);
            }
        }
        
        //characters
        for(int x = floor.party[0].x - 11; x < floor.party[0].x + 13; x++) {
            for(int y = floor.party[0].y - 7; y < floor.party[0].y + 9; y++) {
                imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][2] = new ImageView(genChar(x, y, floor));

                imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][2].setFitWidth(64);
                imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][2].setFitHeight(64);

                imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][2].relocate(64*(x-floor.party[0].x+10) - 32, 64*(y-floor.party[0].y+6) - 32);
                inmapLayout.getChildren().add(imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][2]);
            }
        }
        
        //health
        for(int x = floor.party[0].x - 11; x < floor.party[0].x + 13; x++) {
            for(int y = floor.party[0].y - 7; y < floor.party[0].y + 9; y++) {
                imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][3] = new ImageView(images.health);
                imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][3].relocate(64*(x-floor.party[0].x+10) - 32, 64*(y-floor.party[0].y+6) + 32);
                if(x < 0 || x >= floor.sizeX || y < 0 || y >= floor.sizeY || !floor.chars[x][y].exists)
                    imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][3].setVisible(false);
                
                inmapLayout.getChildren().add(imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][3]);
            }
        }
        
        //fog
        for(int x = 0; x < 24; x++) {
            for(int y = 0; y < 16; y++) {
                imageViews[x][y][5] = new ImageView(images.black);
                imageViews[x][y][5].relocate(64*x-32, 64*y-32);
                imageViews[x][y][5].setOpacity(Math.sqrt(Math.pow(Math.abs(x-10),2)+Math.pow(Math.abs(y-6),2))/10);
                
                inmapLayout.getChildren().add(imageViews[x][y][5]);
            }
        }
        
        //add all animations
//        for(int x = floor.party[0].x - 11; x < floor.party[0].x + 13; x++) {
//            for(int y = floor.party[0].y - 7; y < floor.party[0].y + 9; y++) {
//                setMoveAnim(imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][0]);
//                setMoveAnim(imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][2]);
//                setMoveAnim(imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][3]);
//            }
//        }
        
        rip = new Text(360, 340, ("  GAME OVER\nR TO RESTART"));
        rip.setFont(Font.font(null, FontWeight.BOLD, 80));
        rip.setFill(Paint.valueOf("WHITE"));
        rip.setVisible(false);
        inmapLayout.getChildren().add(rip);
        
        return new Scene(inmapLayout, screenWidth, screenHeight);
    }
    
    //update
    public void update(String focus, Floor floor, Point menuP, String menuWindow, 
            Character[] party, Item[] inv, int gold, boolean qiVisible) {
        if(focus.equals("floor")) {
            //remove menu window
            if(inmapLayout.getChildren().contains(menuBox))
                inmapLayout.getChildren().remove(menuBox);
            
            //show quick info ui
            if(qiVisible && !inmapLayout.getChildren().contains(UIBox)) {
                nameT.setText(floor.location.name);
                typeT.setText(floor.location.type);
                diffT.setText("Difficulty: " + floor.location.difficulty);
                floorT.setText("Floor " + floor.location.currentFloor);
                charT.setText(party[0].name);
                levelT.setText("Level " + party[0].LVL);
                goldT.setText("Gold: " + String.valueOf(gold));
                inmapLayout.getChildren().add(UIBox);
            }
            else if(!qiVisible)
                inmapLayout.getChildren().remove(UIBox);
            
            //tiles
            for(int x = floor.party[0].x - 11; x < floor.party[0].x + 13; x++) {
                for(int y = floor.party[0].y - 7; y < floor.party[0].y + 9; y++) {
                    imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][0].setImage(genTile(x, y, floor));
                }
            }

            //characters
            for(int x = floor.party[0].x - 11; x < floor.party[0].x + 13; x++) {
                for(int y = floor.party[0].y - 7; y < floor.party[0].y + 9; y++) {
                    imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][2].setImage(genChar(x, y, floor));
                }
            }

            //health
            for(int x = floor.party[0].x - 11; x < floor.party[0].x + 13; x++) {
                for(int y = floor.party[0].y - 7; y < floor.party[0].y + 9; y++) {
                    if(x >= 0 && x < floor.sizeX && y >= 0 && y < floor.sizeY && floor.chars[x][y].exists) {
                        imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][3].setVisible(true);
                        imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][3].setFitWidth(64 * 
                                (double)floor.chars[x][y].currentHP / floor.chars[x][y].maxHP);
                    }
                    else
                        imageViews[x-floor.party[0].x+11][y-floor.party[0].y+7][3].setVisible(false);
                }
            }

            //floor text
            floorT.setText("Floor " + floor.location.currentFloor);

            //rip
            if(!floor.party[0].exists)
                rip.setVisible(true);
            else
                rip.setVisible(false);
        }
        else if(focus.equals("menu")) {
            //add menu window
            if(!inmapLayout.getChildren().contains(menuBox))
                inmapLayout.getChildren().add(menuBox);
            
            //remove quick info box
            inmapLayout.getChildren().remove(UIBox);
            
            if(menuP.x == -1) {
                toggleMenu("", party, inv, gold);
            }
            if(menuP.y == -1) {
                menuFocus.setOpacity(1);
                menuCursor.setOpacity(0);
                changeMenu(menuWindow, party, inv, gold);
            }
            else {
                menuFocus.setOpacity(0);
                menuCursor.setOpacity(1);
                switch(menuWindow) {
                    case "inv":
                        menuCursor.relocate(screenWidth/10+50+180*menuP.x, screenHeight/6+90+26*menuP.y);
                        break;
                    case "char":
                        break;
                    case "party":
                        break;
                    case "notes":
                        break;
                    case "options":
                        break;
                }
            }
        }
    }
    
    //toggle menu
    public void toggleMenu(String menuWindow, Character[] party, Item[] inv, int gold) {
        if(inmapLayout.getChildren().contains(menuBox)) {
            inmapLayout.getChildren().remove(menuBox);
        }
        else {
            changeMenu(menuWindow, party, inv, gold);
            inmapLayout.getChildren().add(menuBox);
        }
    }
    
    //change pages in menu and move rectangle thing
    public void changeMenu(String menuWindow, Character[] party, Item[] inv, int gold) {
        if(menuWindow.equals("inv")) menuFocus.setLayoutX(screenWidth/10);
        else if(menuWindow.equals("char")) menuFocus.setLayoutX(screenWidth/10+screenWidth*4/25);
        else if(menuWindow.equals("party")) menuFocus.setLayoutX(screenWidth/10+screenWidth*8/25);
        else if(menuWindow.equals("notes")) menuFocus.setLayoutX(screenWidth/10+screenWidth*12/25);
        else if(menuWindow.equals("options")) menuFocus.setLayoutX(screenWidth/10+screenWidth*16/25);
        
        //remove all nodes except for background
        menuBox.getChildren().remove(8, menuBox.getChildren().toArray().length);
        
        //add objects depending on window
        if(menuWindow.equals("inv")) {
            Text[] invText = new Text[64];
            
            for(int i = 0; i < 64; i++) {
                invText[i] = new Text(screenWidth/10+60+180*(int)(i/16), screenHeight/6+100+26*(i%16), inv[i].name);
                if(!inv[i].exists)
                    invText[i].setText("-");
                if(inv[i].name.length() > 15)
                    invText[i].setText(inv[i].name.substring(0, 12) + "...");
                
                invText[i].setFill(Paint.valueOf("BLUE"));
                invText[i].setFont(Font.font("Monaco", FontWeight.NORMAL, 14));
                menuBox.getChildren().addAll(invText[i]);
            }
        }
        else if(menuWindow.equals("char")) {
            Text t = new Text(screenWidth/3, screenHeight/3+screenHeight/5, "your skills: none");
            t.setFont(Font.font(null, FontWeight.NORMAL, 24));
            
            menuBox.getChildren().addAll(t);
        }
        else if(menuWindow.equals("party")) {
            Text t = new Text(screenWidth/3, screenHeight/3+screenHeight/5, "you have no friends");
            t.setFont(Font.font(null, FontWeight.NORMAL, 24));
            
            menuBox.getChildren().addAll(t);
        }
        else if(menuWindow.equals("notes")) {
            Text t = new Text(screenWidth/3, screenHeight/3+screenHeight/5, "you are illiterate");
            t.setFont(Font.font(null, FontWeight.NORMAL, 24));
            
            menuBox.getChildren().addAll(t);
        }
        else if(menuWindow.equals("options")) {
            Text t = new Text(screenWidth/3, screenHeight/3+screenHeight/5, "you have no options in life");
            t.setFont(Font.font(null, FontWeight.NORMAL, 24));
            
            menuBox.getChildren().addAll(t);
        }
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
            else if(type.equalsIgnoreCase("bat"))
                return images.bat;
            else if(type.equalsIgnoreCase("adelf"))
                return images.adelf;
            else if(type.equalsIgnoreCase("bell"))
                return images.bell;
            else if(type.equalsIgnoreCase("chest"))
                return images.chest;
            else if(type.equalsIgnoreCase("chick"))
                return images.chick;
            else if(type.equalsIgnoreCase("chicken"))
                return images.chicken;
            else if(type.equalsIgnoreCase("fishman"))
                return images.fishman;
            else if(type.equalsIgnoreCase("flan"))
                return images.flan;
            else if(type.equalsIgnoreCase("ghost"))
                return images.ghost;
            else if(type.equalsIgnoreCase("kingslime"))
                return images.kingslime;
            else if(type.equalsIgnoreCase("longcat"))
                return images.longcat;
            else if(type.equalsIgnoreCase("manta"))
                return images.manta;
            else if(type.equalsIgnoreCase("mote"))
                return images.mote;
            else if(type.equalsIgnoreCase("skelebro"))
                return images.skelebro;
            else if(type.equalsIgnoreCase("snail"))
                return images.snail;
            else if(type.equalsIgnoreCase("spookyslime"))
                return images.spookyslime;
            else if(type.equalsIgnoreCase("spookyslug"))
                return images.spookyslug;
            
//            else if(type.equalsIgnoreCase("spider"))
//                return images.spider;
//            else if(type.equalsIgnoreCase("slug"))
//                return images.slug;
//            else if(type.equalsIgnoreCase("goblin"))
//                return images.goblin;
//            else if(type.equalsIgnoreCase("clinton"))
//                return images.boss;
            else if(type.equalsIgnoreCase("npc"))
                return images.npc1;
            else return null;
        }
        else return null;
    }
    
    //animates imageview movement
//    private void setMoveAnim(ImageView imageView) {
//        new AnimationTimer() {
//            @Override
//            public void handle(long timestamp) {
//                if (lastUpdateTime.get() > 0) {
//                    final double oldX = imageView.getTranslateX();
//                    final double newX = oldX + speedX.get();
//                    final double oldY = imageView.getTranslateY();
//                    final double newY = oldY + speedY.get();
//                    imageView.setTranslateX(newX);
//                    imageView.setTranslateY(newY);
//                    xOffset = imageView.getTranslateX();
//                    yOffset = imageView.getTranslateY();
//                }
//                lastUpdateTime.set(timestamp);
//            }
//        }.start();
//    }
}

