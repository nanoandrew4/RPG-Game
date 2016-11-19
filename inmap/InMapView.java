/* 
    Graphical view for InMap.
 */

package inmap;

//import javafx.animation.AnimationTimer;
//import javafx.beans.property.DoubleProperty;
//import javafx.beans.property.LongProperty;
//import javafx.beans.property.SimpleDoubleProperty;
//import javafx.beans.property.SimpleLongProperty;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
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
        stoneFloor = new Image("/media/graphics/inmap/StoneFloor.png", width, height, false, false);
        stoneWall = new Image("/media/graphics/inmap/StoneWall.png", width, height, false, false);
        stoneTop = new Image("/media/graphics/inmap/StoneTop.png", width, height, false, false);
        door = new Image("/media/graphics/inmap/door.png", width, height, false, false);
        opendoor = new Image("/media/graphics/inmap/opendoor.png", width, height, false, false);
        stairsup = new Image("/media/graphics/inmap/stairsup.png", width, height, false, false);
        stairsdown = new Image("/media/graphics/inmap/stairsdown.png", width, height, false, false);

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
    private final Pane inmapLayout, floorPane, UIPane,
            menuPane, menubgPane, invPane, invTextPane, invStatPane, 
            charPane, partyPane, notePane, opPane;
    double width, height;
    final double zoom;
    
    private Text rip; //rip text
    private final Text charT, levelT, goldT; //quickinfo text
    private final Text diffT, floorT, nameT, typeT; //location text
    private Text[] invText; //menu inv text
    private Text invName, invDes, invType; //item information
    private Text[] invStats; //item stat information
    private Text[] invButtons; //item manipulation
    private Rectangle[] invRButtons; //item button boxes
    private Text[] chText; //menu char text
    private Text chName, chTitle, chHP, chMP, chLVL, chEXP;
    private Text[][] parText; //menu party text
    
    private final Rectangle menuFocus;
    private final Rectangle menuCursor, tempCursor;
    
    //animation
//    private final LongProperty lastUpdateTime = new SimpleLongProperty();
//    public final DoubleProperty speedX = new SimpleDoubleProperty();
//    public final DoubleProperty speedY = new SimpleDoubleProperty();
//    public double speedXVal;
//    public double speedYVal;
    
    //constructor
    InMapView(double screenWidth, double screenHeight) {
        //0: tiles
        //1: items
        //2: characters
        //3: health
        //4: overlay
        //5: fog
        imageViews = new ImageView[24][16][6];
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        zoom = 12;
        setTileSize();
        images = new Images(width, height);
        
        inmapLayout = new Pane();
        floorPane = new Pane();
        UIPane = new Pane();
        menuPane = new Pane();
        menubgPane = new Pane();
        invPane = new Pane();
        invTextPane = new Pane();
        invStatPane = new Pane();
        charPane = new Pane();
        partyPane = new Pane();
        notePane = new Pane();
        opPane = new Pane();
//        speedXVal = 64;
//        speedYVal = 64;
        
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
        
        UIPane.getChildren().addAll(box, box2, box3, nameT, typeT, diffT, floorT, charT, levelT, goldT);
        
        //initialize menu
        Rectangle box4 = new Rectangle(screenWidth, screenHeight, Paint.valueOf("GREY"));
        box4.relocate(0, 0);
        box4.setOpacity(.5);
        
        menuFocus = new Rectangle(screenWidth/7, screenHeight/24, Paint.valueOf("WHITE"));
        menuFocus.relocate(0, screenHeight*3/16);
        menuFocus.setEffect(new BoxBlur(5, 5, 3));
        
        menuCursor = new Rectangle(screenWidth/8, screenHeight/40, Paint.valueOf("WHITE"));
        menuCursor.setEffect(new BoxBlur(3, 3, 3));
        menuCursor.setOpacity(0);
        
        tempCursor = new Rectangle(screenWidth/8, screenHeight/40, Paint.valueOf("WHITE"));
        tempCursor.setEffect(new BoxBlur(3, 3, 3));
        tempCursor.setOpacity(0);
        
        Text[] menuText = new Text[5];
        for(int i = 0; i < 5; i++) {
            menuText[i] = new Text(screenWidth*2/19+screenWidth*4*i/25, screenHeight*2/9, "");
            menuText[i].setFont(Font.font("Arial", FontWeight.BOLD, 28));
            menuText[i].setFill(Paint.valueOf("WHITE"));
            menuText[i].setWrappingWidth(screenWidth/6);
            menuText[i].setTextAlignment(TextAlignment.CENTER);
        }
        menuText[0].setText("INV");
        menuText[1].setText("CHAR");
        menuText[2].setText("PARTY");
        menuText[3].setText("NOTES");
        menuText[4].setText("OPTIONS");
        
        menubgPane.getChildren().addAll(box4, menuFocus, menuCursor, tempCursor);
        menubgPane.getChildren().addAll(menuText);
        
        menuPane.getChildren().add(menubgPane);
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
        width = Math.floor(width);
        height = Math.floor(height);
    }
    
    //initialize display
    public Scene initDisplay() {
        //floorPane
        for(int x = 0; x < 24; x++) {
            for(int y = 0; y < 16; y++) {
                //tiles
                imageViews[x][y][0] = new ImageView();
                imageViews[x][y][0].relocate(width*x - width*3/2, width*y - width*2);
                
                //characters
                imageViews[x][y][2] = new ImageView();
                imageViews[x][y][2].relocate(width*x - width*3/2, width*y - width*3/2);
                
                //health
                imageViews[x][y][3] = new ImageView(images.health);
                imageViews[x][y][3].relocate(width*x - width*3/2, width*y - width/2);
                imageViews[x][y][3].setVisible(false);
                
                //fog
                imageViews[x][y][5] = new ImageView(images.black);
                imageViews[x][y][5].relocate(width*x-width/2, width*y-width/2);
                imageViews[x][y][5].setOpacity(Math.sqrt(Math.pow(Math.abs(x-10),2)+Math.pow(Math.abs(y-6),2))/10);
            }
        }
        
        for(int x = 0; x < 24; x++)
            for(int y = 0; y < 16; y++)
                floorPane.getChildren().add(imageViews[x][y][0]);
        for(int x = 0; x < 24; x++)
            for(int y = 0; y < 16; y++)
                floorPane.getChildren().add(imageViews[x][y][2]);
        for(int x = 0; x < 24; x++)
            for(int y = 0; y < 16; y++)
                floorPane.getChildren().add(imageViews[x][y][3]);
        for(int x = 0; x < 24; x++)
            for(int y = 0; y < 16; y++)
                floorPane.getChildren().add(imageViews[x][y][5]);
        
        inmapLayout.getChildren().add(floorPane);
        
        //add all animations
//        for(int x = 0; x < 24; x++) {
//            for(int y = 0; y < 16; y++) {
//                setMoveAnim(imageViews[x][y][0]);
//                setMoveAnim(imageViews[x][y][2]);
//                setMoveAnim(imageViews[x][y][3]);
//            }
//        }
        
        //invPane
        Text temp = new Text(screenWidth/10+60, screenHeight/6+100, "H");
        temp.setFont(Font.font("Monaco", FontWeight.BOLD, 20));
        temp.setFill(Paint.valueOf("RED"));
        invText = new Text[64];
        for(int i = 0; i < 64; i++) {
            invText[i] = new Text(screenWidth/7+screenWidth/7*(int)(i/16), screenHeight*2/7+screenHeight/28*(i%16), "-");
            invText[i].setFill(Paint.valueOf("WHITE"));
            invText[i].setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        }
        invButtons = new Text[4];
        invRButtons = new Rectangle[4];
        for(int i = 0; i < 4; i++) {
            invButtons[i] = new Text(screenWidth*5/7, screenHeight*3/5+screenHeight/18*i, "");
            invButtons[i].setWrappingWidth(300);
            invButtons[i].setTextAlignment(TextAlignment.CENTER);
            invButtons[i].setFill(Paint.valueOf("WHITE"));
            invButtons[i].setFont(Font.font("Monaco", FontWeight.NORMAL, 24));
            
            invRButtons[i] = new Rectangle(screenWidth/6, screenHeight/20, Paint.valueOf("WHITE"));
            invRButtons[i].relocate(screenWidth*3/4, screenHeight*40/71+screenHeight/18*i);
            invRButtons[i].setOpacity(.2);
        }
        invButtons[0].setText("USE / EQUIP");
        invButtons[1].setText("MOVE");
        invButtons[2].setText("DISCARD");
        invButtons[3].setText("CANCEL");
        
        //invTextPane
        invName = new Text(screenWidth*5/7, screenHeight*2/7, "");
        invName.setWrappingWidth(300);
        invName.setTextAlignment(TextAlignment.CENTER);
        invName.setFill(Paint.valueOf("WHITE"));
        invName.setFont(Font.font(null, FontWeight.BOLD, 30));
        invDes = new Text(screenWidth*5/7, screenHeight*3/8, "There's nothing here.");
        invDes.setWrappingWidth(300);
        invDes.setFill(Paint.valueOf("WHITE"));
        invDes.setFont(Font.font(null, FontWeight.NORMAL, 18));
        
        invTextPane.getChildren().addAll(invName, invDes);
        
        //invStatPane
        invType = new Text(screenWidth*5/7, screenHeight*2/7, "");
        invType.setWrappingWidth(300);
        invType.setTextAlignment(TextAlignment.CENTER);
        invType.setFill(Paint.valueOf("WHITE"));
        invType.setFont(Font.font(null, FontWeight.NORMAL, 30));
        invStats = new Text[19];
        for(int i = 0; i < 19; i++) {
            invStats[i] = new Text(screenWidth*5/7+screenWidth/8*(i%2), 
                    screenHeight*2/7+screenHeight/40*Math.floor(i/2), "");
            invStats[i].setFill(Paint.valueOf("WHITE"));
            invStats[i].setFont(Font.font(null, FontWeight.NORMAL, 16));
        }
        
        invStatPane.getChildren().add(invType);
        invStatPane.getChildren().addAll(invStats);
        
        invPane.getChildren().addAll(invText);
        invPane.getChildren().addAll(invButtons);
        invPane.getChildren().addAll(invRButtons);
        invPane.getChildren().add(invTextPane);
        
        //charPane
        ImageView portrait = new ImageView(new Image("/media/graphics/inmap/portrait.jpg", 
                screenHeight/5, screenHeight/5, false, false));
        portrait.relocate(screenWidth/5, screenHeight/3);
        chName = new Text(screenWidth/5, screenHeight*12/20, "");
        chName.setFont(Font.font(null, FontWeight.NORMAL, 24));
        chName.setFill(Paint.valueOf("WHITE"));
        chTitle = new Text(screenWidth/5, screenHeight*13/20, "");
        chTitle.setFont(Font.font(null, FontWeight.NORMAL, 24));
        chTitle.setFill(Paint.valueOf("WHITE"));
        chHP = new Text(screenWidth/5, screenHeight*14/20, "");
        chHP.setFont(Font.font(null, FontWeight.NORMAL, 24));
        chHP.setFill(Paint.valueOf("WHITE"));
        chMP = new Text(screenWidth/5, screenHeight*15/20, "");
        chMP.setFont(Font.font(null, FontWeight.NORMAL, 24));
        chMP.setFill(Paint.valueOf("WHITE"));
        chLVL = new Text(screenWidth/5, screenHeight*16/20, "");
        chLVL.setFont(Font.font(null, FontWeight.NORMAL, 24));
        chLVL.setFill(Paint.valueOf("WHITE"));
        chEXP = new Text(screenWidth/5, screenHeight*17/20, "");
        chEXP.setFont(Font.font(null, FontWeight.NORMAL, 24));
        chEXP.setFill(Paint.valueOf("WHITE"));
        chText = new Text[14];
        charPane.getChildren().addAll(portrait, chName, chTitle, chHP, chMP, chLVL, chEXP);
//        charPane.getChildren().addAll(chText);
        
        //partyPane
        Text t2 = new Text(screenWidth/3, screenHeight/3+screenHeight/5, "you have no friends");
        t2.setFont(Font.font(null, FontWeight.NORMAL, 24));
        t2.setFill(Paint.valueOf("WHITE"));
        partyPane.getChildren().addAll(t2);
        
        //notePane
        Text t3 = new Text(screenWidth/3, screenHeight/3+screenHeight/5, "you are illiterate");
        t3.setFont(Font.font(null, FontWeight.NORMAL, 24));
        t3.setFill(Paint.valueOf("WHITE"));
        notePane.getChildren().addAll(t3);
        
        //opPane
        Text t4 = new Text(screenWidth/3, screenHeight/3+screenHeight/5, "you have no options in life");
        t4.setFont(Font.font(null, FontWeight.NORMAL, 24));
        t4.setFill(Paint.valueOf("WHITE"));
        opPane.getChildren().addAll(t4);

        //rip text
        rip = new Text(0, screenHeight/2, ("GAME OVER\nR TO RESTART"));
        rip.setWrappingWidth(screenWidth);
        rip.setTextAlignment(TextAlignment.CENTER);
        rip.setFont(Font.font(null, FontWeight.BOLD, 80));
        rip.setFill(Paint.valueOf("WHITE"));
        rip.setVisible(false);
        inmapLayout.getChildren().add(rip);
        
        inmapLayout.setBackground(new Background(new BackgroundFill(Paint.valueOf("BLACK"), null, null)));
        
        return new Scene(inmapLayout, screenWidth, screenHeight);
    }
    
    //update
    public void update(InMapViewData vd) {
        if(vd.focus.equals("floor")) {
            //remove menu window
            inmapLayout.getChildren().remove(menuPane);
            
            //quick info
            if(vd.qiVisible && !inmapLayout.getChildren().contains(UIPane)) {
                nameT.setText(vd.floor.location.name);
                typeT.setText(vd.floor.location.type);
                diffT.setText("Difficulty: " + vd.floor.location.difficulty);
                floorT.setText("Floor " + vd.floor.location.currentFloor);
                charT.setText(vd.party[0].name);
                levelT.setText("Level " + vd.party[0].LVL);
                goldT.setText("Gold: " + String.valueOf(vd.gold));
                inmapLayout.getChildren().add(UIPane);
            }
            else if(!vd.qiVisible)
                inmapLayout.getChildren().remove(UIPane);
            
            //tiles
            for(int x = vd.floor.party[0].x - 11; x < vd.floor.party[0].x + 13; x++) {
                for(int y = vd.floor.party[0].y - 7; y < vd.floor.party[0].y + 9; y++) {
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][0].setImage(genTile(x, y, vd.floor));
                }
            }

            //characters
            for(int x = vd.floor.party[0].x - 11; x < vd.floor.party[0].x + 13; x++) {
                for(int y = vd.floor.party[0].y - 7; y < vd.floor.party[0].y + 9; y++) {
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][2].setImage(genChar(x, y, vd.floor));
                }
            }

            //health
            for(int x = vd.floor.party[0].x - 11; x < vd.floor.party[0].x + 13; x++) {
                for(int y = vd.floor.party[0].y - 7; y < vd.floor.party[0].y + 9; y++) {
                    if(x >= 0 && x < vd.floor.sizeX && y >= 0 && y < vd.floor.sizeY && vd.floor.chars[x][y].exists) {
                        imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][3].setVisible(true);
                        imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][3].setFitWidth(64 * 
                                (double)vd.floor.chars[x][y].currentHP / vd.floor.chars[x][y].maxHP);
                    }
                    else
                        imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][3].setVisible(false);
                }
            }

            //floor text
            floorT.setText("Floor " + vd.floor.location.currentFloor);

            //rip
            if(!vd.floor.party[0].exists)
                rip.setVisible(true);
            else
                rip.setVisible(false);
        }
        else if(vd.focus.equals("menu")) {
            //add menu window
            if(!inmapLayout.getChildren().contains(menuPane))
                inmapLayout.getChildren().add(menuPane);
            
            //remove quick info box
            inmapLayout.getChildren().remove(UIPane);
            
            //toggle menu
            if(vd.menuToggle && invPane.getChildren().contains(invTextPane)) {
                invPane.getChildren().remove(invTextPane);
                invPane.getChildren().add(invStatPane);
            }
            else if(!vd.menuToggle && invPane.getChildren().contains(invStatPane)) {
                invPane.getChildren().remove(invStatPane);
                invPane.getChildren().add(invTextPane);
            }
            
            //change menus
            if(vd.menuP.y == -1) {
                menuFocus.setOpacity(.5);
                tempCursor.setOpacity(0);
                menuCursor.setOpacity(0);
                invName.setText("");
                invDes.setText("");
                changeMenu(vd);
            }
            else {
                menuFocus.setOpacity(0.25);
                switch(vd.menuWindow) {
                    case "inv":
                        for(int i = 0; i < 4; i++)
                            invRButtons[i].setOpacity(.2);
                        //temporary pointer
                        if(vd.tempP.x != -1) {
                            tempCursor.setOpacity(.3);
                            tempCursor.relocate(screenWidth*2/15+screenWidth/7*vd.tempP.x, 
                                    screenHeight*4/15+screenHeight/28*vd.tempP.y);
                            if(vd.inv[vd.tempP.x*16+vd.tempP.y].exists) {
                                invName.setText(vd.inv[vd.tempP.x*16+vd.tempP.y].displayName);
                                invDes.setText(vd.invDes);
                                updateInvStats(vd.inv[vd.tempP.x*16+vd.tempP.y]);
                            }
                            else {
                                invName.setText("");
                                invDes.setText("There's nothing here.");
                                updateInvStats(new Item());
                            }
                        }
                        //selection
                        else if(vd.selectP != -1) {
                            menuCursor.setOpacity(.2);
                            invRButtons[vd.selectP].setOpacity(.5);
                        }
                        //moving around menu
                        else {
                            refreshMenu(vd);
                            tempCursor.setOpacity(0);
                            menuCursor.setOpacity(.3);
                            menuCursor.relocate(screenWidth*2/15+screenWidth/7*vd.menuP.x, 
                                    screenHeight*4/15+screenHeight/28*vd.menuP.y);
                            if(vd.inv[vd.menuP.x*16+vd.menuP.y].exists) {
                                invName.setText(vd.inv[vd.menuP.x*16+vd.menuP.y].displayName);
                                invDes.setText(vd.invDes);
                                updateInvStats(vd.inv[vd.menuP.x*16+vd.menuP.y]);
                            }
                            else {
                                invName.setText("");
                                invDes.setText(vd.invDes);
                                updateInvStats(new Item());
                            }
                        }
                        break;
                    case "char":
                        break;
                    case "party":
                        break;
                    case "notes":
                        break;
                    case "options":
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
    //toggle menu
    public void toggleMenu(InMapViewData vd) {
        if(inmapLayout.getChildren().contains(menuPane)) {
            inmapLayout.getChildren().remove(menuPane);
        }
        else {
            changeMenu(vd);
            inmapLayout.getChildren().add(menuPane);
        }
    }
    
    //change pages in menu and move menuFocus
    public void changeMenu(InMapViewData vd) {
        //set menu focus
        if(vd.menuWindow.equals("inv")) 
            menuFocus.setX(screenWidth*3/25);
        else if(vd.menuWindow.equals("char")) 
            menuFocus.setX(screenWidth*3/25+screenWidth*4/25);
        else if(vd.menuWindow.equals("party")) 
            menuFocus.setX(screenWidth*3/25+screenWidth*8/25);
        else if(vd.menuWindow.equals("notes")) 
            menuFocus.setX(screenWidth*3/25+screenWidth*12/25);
        else if(vd.menuWindow.equals("options")) 
            menuFocus.setX(screenWidth*3/25+screenWidth*16/25);
        
        //remove all nodes except for menu background pane
        if(menuPane.getChildren().size() > 1)
            menuPane.getChildren().remove(1, menuPane.getChildren().size());
        
        //refresh menu items
        refreshMenu(vd);
        
        //add pane to menu
        if(vd.menuWindow.equals("inv"))
            menuPane.getChildren().add(invPane);
        else if(vd.menuWindow.equals("char"))
            menuPane.getChildren().add(charPane);
        else if(vd.menuWindow.equals("party"))
            menuPane.getChildren().add(partyPane);
        else if(vd.menuWindow.equals("notes"))
            menuPane.getChildren().add(notePane);
        else if(vd.menuWindow.equals("options"))
            menuPane.getChildren().add(opPane);
    }
    
    //menu data refresh
    public void refreshMenu(InMapViewData vd) {
        if(vd.menuWindow.equals("inv")) {
            for(int i = 0; i < 64; i++) {
                if(!vd.inv[i].exists)
                    invText[i].setText("-");
                else if(vd.inv[i].displayName.length() > 15)
                    invText[i].setText(vd.inv[i].displayName.substring(0, 12) + "...");
                else
                    invText[i].setText(vd.inv[i].displayName);
            }
            for(int i = 0; i < 4; i++)
                invRButtons[i].setOpacity(.2);
        }
        else if(vd.menuWindow.equals("char")) {
            chName.setText("Ronald Rump");
            chTitle.setText("Weapon of Mass Destruction");
            chHP.setText("HP " + vd.party[0].currentHP + "/" + vd.party[0].maxHP);
            chMP.setText("MP " + vd.party[0].currentMP + "/" + vd.party[0].maxMP);
            chLVL.setText("Level " + vd.party[0].LVL);
            chEXP.setText("To next: "+((int)(150*Math.sqrt(vd.party[0].LVL+10)-430)-vd.party[0].EXP)+" EXP");
        }
        else if(vd.menuWindow.equals("party")) {
            
        }
        else if(vd.menuWindow.equals("notes")) {
            
        }
        else if(vd.menuWindow.equals("options")) {
            
        }
    }
    
    //update inventory stat text
    private void updateInvStats(Item i) {
        invStats[0].setText("DMG: " + i.DMG);
        invStats[1].setText("HIT: " + i.HIT);
        invStats[2].setText("CRT: " + i.CRT);
        invStats[3].setText("PRC: " + i.PRC);
        invStats[4].setText("VIT: " + i.VIT);
        invStats[5].setText("INT: " + i.INT);
        invStats[6].setText("ACC: " + i.ACC);
        invStats[7].setText("STR: " + i.STR);
        invStats[8].setText("DEX: " + i.DEX);
        invStats[9].setText("WIS: " + i.WIS);
        invStats[10].setText("LUK: " + i.LUK);
        invStats[11].setText("DEF: " + i.DEF);
        invStats[12].setText("RES: " + i.RES);
        invStats[13].setText("EVA: " + i.EVA);
        invStats[14].setText("MAXHP: " + i.MHP);
        invStats[15].setText("HEALHP: " + i.CHP);
        invStats[16].setText("MAXMP: " + i.MMP);
        invStats[17].setText("HEALMP: " + i.CMP);
        invStats[18].setText("VAL: " + i.VAL);
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
        }
        return null;
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
//        
//        new AnimationTimer() {
//            double oldX = 0;
//            double oldY = 0;
//            boolean waiting = false;
//            
//            @Override
//            public void handle(long timestamp) {
//                
//                if (lastUpdateTime.get() > 0) {
//                    final double oldX = imageView.getTranslateX();
//                    final double newX = oldX + speedX.get();
//                    final double oldY = imageView.getTranslateY();
//                    final double newY = oldY + speedY.get();
//                    imageView.setTranslateX(newX);
//                    imageView.setTranslateY(newY);
//                }
//                lastUpdateTime.set(timestamp);
//            }
//        }.start();
//    }
}