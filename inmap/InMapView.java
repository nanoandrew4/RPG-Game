/* 
    Graphical view for InMap.
 */

package inmap;

import java.util.ArrayList;
import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
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
import javafx.util.Duration;

import main.TextType;

class InMapView {
    //vars
    ImageView[][][] imageViews;
    ImageView playerView;
    public double screenWidth, screenHeight;
    private final Scene scene;
    private final Pane inmapLayout, floorPane, overlayPane, UIPane,
            menuPane, menubgPane, invPane, invTextPane, invStatPane, 
            charPane, partyPane, notePane, opPane, talkPane, talkSelectPane,
            ripPane, tradePane, tradeTextPane, tradeStatPane;
    double width, height;
    final double zoom;
    String name;
    private boolean walkState;
    
    //quickinfo
    private Text qiChar, qiLevel, qiGold;
    private Text qiDiff, qiFloor, qiName, qiType;
    //permanent ui
    private ImageView uiPortrait, uiHealth, uiMana;
    private Rectangle uiHealthR, uiManaR;
    private Text uiHealthT, uiManaT;
    private Text[] uiLogT;
    private SimpleIntegerProperty uiLogSize;
    private FadeTransition[] uiLogFade;
    private ArrayList<String> log;
    //menu
    private Rectangle menuFocus;
    private Rectangle menuCursor, tempCursor;
    //inventory
    private Text[] invText; //item names
    private Text invName, invDes, invType, invGold;
    private Text[] invStats; //stats
    private Rectangle[] invRButtons; //button boxes
    private Text invSort; //sorting type
    //character
    private Text chName, chTitle, chHP, chMP, chLVL, chEXP; //main stats
    private Text[] chStats, chEqp; //stats and equipment
    //party
    private Text[][] parText;
    //options
    private Text[][] saveInfo;
    private ImageView[] saveImages; //save information
    private Rectangle[] opRButtons; //button boxes
    private Rectangle[] opRSaves; //save boxes
    //talking
    private TextType talkTransition;
    private Text talkT;
    private Rectangle talkSelectR;
    private Text talkYes, talkNo;
    //trading: similar to inventory
    private Text[] traText;
    private Text traName, traDes, traType, traGold;
    private Text[] traStats;
    private Text[] traButtons;
    private Rectangle[] traRButtons;
    private Rectangle traFocus, traCursor;
    private Text traSort;
    
    private Timeline tUp, tLeft, tDown, tRight;
    private Sprite cUp, cLeft, cDown, cRight, cUp2, cLeft2, cRight2, cDown2;
    
    //constructor
    InMapView(double screenWidth, double screenHeight, String name, int sprite, String portrait) {
        //0: tiles
        //1: shadow
        //2: items
        //3: characters
        //4: health
        //5: overlay
        //6: fog
        imageViews = new ImageView[24][16][7];
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.name = name;
        zoom = 12;
        setTileSize();
        Images.setHeroSprite(width, height, sprite, portrait);
        
        inmapLayout = new Pane();
        floorPane = new Pane();
        UIPane = new Pane();
        overlayPane = new Pane();
        menuPane = new Pane();
        menubgPane = new Pane();
        invPane = new Pane();
        invTextPane = new Pane();
        invStatPane = new Pane();
        charPane = new Pane();
        partyPane = new Pane();
        notePane = new Pane();
        opPane = new Pane();
        talkPane = new Pane();
        talkSelectPane = new Pane();
        ripPane = new Pane();
        tradePane = new Pane();
        tradeTextPane = new Pane();
        tradeStatPane = new Pane();
        
        //initialize UI
        initDisplay();
        
        initAnimation();
        
        scene = new Scene(inmapLayout, screenWidth, screenHeight);
    }

    //set tile size
    private void setTileSize() {
        if (screenWidth > screenHeight) {
            width = (screenHeight / zoom);
            height = (screenHeight / zoom) * 1.5;
        } 
        else {
            width = (screenWidth / zoom);
            height = (screenWidth / zoom) * 1.5;
        }
        width = Math.floor(width);
        height = Math.floor(height);
    }
    
    //initialize display
    public final void initDisplay() {
        //quickinfo boxes
        Rectangle qiInfoBox = new Rectangle(screenWidth/8, screenHeight/4, Paint.valueOf("WHITE"));
        qiInfoBox.setOpacity(.7);
        qiInfoBox.relocate(screenWidth*7/8, 0);
        
        qiName = new Text(screenWidth*7/8 + 20, 40, "");
        qiName.setTextAlignment(TextAlignment.CENTER);
        qiName.setWrappingWidth(screenWidth/8-40);
        qiName.setFont(Font.font(null, FontWeight.BOLD, 20));
        
        qiType = new Text(screenWidth*7/8 + 20, 90, "");
        qiType.setFont(Font.font(null, FontWeight.NORMAL, 18));
        
        qiDiff = new Text(screenWidth*7/8 + 20, 120, "");
        qiDiff.setFont(Font.font(null, FontWeight.NORMAL, 18));
        
        qiFloor = new Text(screenWidth*7/8 + 20, 150, "");
        qiFloor.setFont(Font.font(null, FontWeight.NORMAL, 18));

        Rectangle qiPartyBox = new Rectangle(screenWidth/14, screenHeight*3/5, Paint.valueOf("WHITE"));
        qiPartyBox.setOpacity(.7);
        qiPartyBox.relocate(0, screenHeight/8);
        
        Rectangle qiCharBox = new Rectangle(screenWidth/8, screenHeight/7, Paint.valueOf("WHITE"));
        qiCharBox.setOpacity(.7);
        qiCharBox.relocate(screenWidth/8, screenHeight*3/4);
        
        qiChar = new Text(screenWidth/8 + 10, screenHeight*3/4+screenHeight/28, "");
        qiChar.setFont(Font.font(null, FontWeight.NORMAL, screenHeight/48));
        
        qiLevel = new Text(screenWidth/8 + 10, screenHeight*3/4+screenHeight*2/28, "");
        qiLevel.setFont(Font.font(null, FontWeight.NORMAL, screenHeight/48));
        
        qiGold = new Text(screenWidth/8 + 10, screenHeight*3/4+screenHeight*3/28, "");
        qiGold.setFont(Font.font(null, FontWeight.NORMAL, screenHeight/48));
        
        overlayPane.getChildren().addAll(qiInfoBox, qiPartyBox, qiCharBox, qiName, 
                qiType, qiDiff, qiFloor, qiChar, qiLevel, qiGold);
        
        //ui
        uiHealth = new ImageView(Images.health);
        uiHealth.relocate(0, screenHeight*46/50);
        uiHealth.setFitHeight(screenHeight/50);
        uiMana = new ImageView(Images.mana);
        uiMana.relocate(0, screenHeight*47/50);
        uiMana.setFitHeight(screenHeight/50);
        
        uiHealthR = new Rectangle(0, screenHeight/50, Paint.valueOf("GREY"));
        uiHealthR.relocate(0, screenHeight*46/50);
        uiHealthR.setOpacity(.4);
        uiManaR = new Rectangle(0, screenHeight/50, Paint.valueOf("GREY"));
        uiManaR.relocate(0, screenHeight*47/50);
        uiManaR.setOpacity(.4);
        
        uiHealthT = new Text(10, screenHeight*47/50, "");
        uiHealthT.setFill(Paint.valueOf("WHITE"));
        uiHealthT.setFont(Font.font(null, FontWeight.NORMAL, screenHeight/50));
        uiManaT = new Text(10, screenHeight*48/50, "");
        uiManaT.setFill(Paint.valueOf("WHITE"));
        uiManaT.setFont(Font.font(null, FontWeight.NORMAL, screenHeight/50));
        
        uiPortrait = new ImageView(Images.portrait);
        uiPortrait.relocate(0, screenHeight-Images.portrait.getHeight()*1.5);
        
        uiLogT = new Text[30];
        for(int i = 0; i < 30; i++) {
            uiLogT[i] = new Text(screenWidth*3/4, screenHeight - (i+2)*20, "");
            uiLogT[i].setFill(Paint.valueOf("WHITE"));
            uiLogT[i].setFont(Font.font("Bradley Hand", FontWeight.NORMAL, 18));
            uiLogT[i].setOpacity((((double)30-i)/30));
        }
        
        uiLogSize = new SimpleIntegerProperty();
        uiLogSize.set(0);
        uiLogFade = new FadeTransition[30];
        for(int i = 0; i < 30; i++) {
            uiLogFade[i] = new FadeTransition(Duration.millis(10000), uiLogT[i]);
            uiLogFade[i].setToValue(0);
            uiLogFade[i].setOnFinished(e -> {
                if(log.size() > 0) {
                    log.remove(log.size()-1);
                    uiLogSize.set(uiLogSize.get() - 1);
                }
            });
        }
        
        UIPane.getChildren().addAll(uiPortrait, uiHealthR, uiHealth, 
                uiHealthT, uiManaR, uiMana, uiManaT);
        UIPane.getChildren().addAll(uiLogT);
        
        //menu
        Rectangle box4 = new Rectangle(screenWidth, screenHeight, Paint.valueOf("BLACK"));
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
            menuText[i].setFont(Font.font("Zapfino", FontWeight.BOLD, 20));
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
        
        //floorPane
        for(int x = 0; x < 24; x++) {
            for(int y = 0; y < 16; y++) {
                //tiles
                imageViews[x][y][0] = new ImageView();
                imageViews[x][y][0].relocate(width*x - width*3/2, width*y - width*2);
                imageViews[x][y][0].setFitHeight(height);
                imageViews[x][y][0].setFitWidth(width);
                
                //shadows
                imageViews[x][y][1] = new ImageView(Images.blackcircle);
                imageViews[x][y][1].relocate(width*x-width*3/2, width*y-width*7/6);
                imageViews[x][y][1].setFitHeight(width/2);
                imageViews[x][y][1].setFitWidth(width);
                imageViews[x][y][1].setEffect(new BoxBlur(5, 5, 3));
                imageViews[x][y][1].setOpacity(.3);
                
                //items
                imageViews[x][y][2] = new ImageView();
                imageViews[x][y][2].relocate(width*x-width*3/2, width*y-width*9/6);
                imageViews[x][y][2].setFitHeight(width);
                imageViews[x][y][2].setFitWidth(width);
                
                //characters
                imageViews[x][y][3] = new ImageView();
                imageViews[x][y][3].relocate(width*x - width*3/2, width*y - width*9/4);
                
                //health
                imageViews[x][y][4] = new ImageView(Images.health);
                imageViews[x][y][4].relocate(width*x - width*3/2, width*y - width/2);
                imageViews[x][y][4].setVisible(false);
                
                //overlay
                imageViews[x][y][5] = new ImageView();
                
                //fog
                imageViews[x][y][6] = new ImageView(Images.black);
                imageViews[x][y][6].relocate(width*x-width*3/2, width*y-width*3/2);
                imageViews[x][y][6].setOpacity(Math.sqrt(Math.pow(Math.abs(x-10),2)+Math.pow(Math.abs(y-6),2))/10);
            }
        }
        
        //add to pane
        for(int y = 0; y < 16; y++) {
            for(int x = 0; x < 24; x++)
                floorPane.getChildren().add(imageViews[x][y][0]);
            for(int x = 0; x < 24; x++)
                floorPane.getChildren().add(imageViews[x][y][1]);
            for(int x = 0; x < 24; x++)
                floorPane.getChildren().add(imageViews[x][y][2]);
            for(int x = 0; x < 24; x++)
                floorPane.getChildren().add(imageViews[x][y][3]);
        }

        for(int x = 0; x < 24; x++)
            for(int y = 0; y < 16; y++)
                floorPane.getChildren().add(imageViews[x][y][4]);
        for(int x = 0; x < 24; x++)
            for(int y = 0; y < 16; y++)
                floorPane.getChildren().add(imageViews[x][y][6]);
        
        playerView = imageViews[11][7][3];
        
        //invPane
        invText = new Text[64];
        for(int i = 0; i < 64; i++) {
            invText[i] = new Text(screenWidth/7+screenWidth/7*(int)(i/16), 
                    screenHeight*2/7+screenHeight/28*(i%16), "-");
            invText[i].setFill(Paint.valueOf("WHITE"));
            invText[i].setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        }
        Text[] invButtons = new Text[4];
        invRButtons = new Rectangle[4];
        for(int i = 0; i < 4; i++) {
            invButtons[i] = new Text(screenWidth*3/4, screenHeight*3/5+screenHeight/18*i, "");
            invButtons[i].setWrappingWidth(screenWidth/6);
            invButtons[i].setTextAlignment(TextAlignment.CENTER);
            invButtons[i].setFill(Paint.valueOf("WHITE"));
            invButtons[i].setFont(Font.font("Trattatello", FontWeight.NORMAL, 28));
            
            invRButtons[i] = new Rectangle(screenWidth/6, screenHeight/20, Paint.valueOf("WHITE"));
            invRButtons[i].relocate(screenWidth*3/4, screenHeight*40/71+screenHeight/18*i);
            invRButtons[i].setOpacity(.2);
            invRButtons[i].setArcHeight(50);
            invRButtons[i].setArcWidth(10);
        }
        invButtons[0].setText("USE / EQUIP");
        invButtons[1].setText("MOVE");
        invButtons[2].setText("DISCARD");
        invButtons[3].setText("CANCEL");
        
        //invTextPane
        invName = new Text(screenWidth*5/7, screenHeight*2/7, "");
        invName.setWrappingWidth(screenWidth*2/9);
        invName.setTextAlignment(TextAlignment.CENTER);
        invName.setFill(Paint.valueOf("WHITE"));
        invName.setFont(Font.font(null, FontWeight.BOLD, 30));
        invDes = new Text(screenWidth*5/7, screenHeight*3/8, "There's nothing here.");
        invDes.setWrappingWidth(300);
        invDes.setFill(Paint.valueOf("WHITE"));
        invDes.setFont(Font.font(null, FontWeight.NORMAL, 18));
        
        invTextPane.getChildren().addAll(invName, invDes);
        
        //invStatPane
        invType = new Text(screenWidth*5/7, screenHeight*8/31, "");
        invType.setWrappingWidth(screenWidth*2/9);
        invType.setTextAlignment(TextAlignment.CENTER);
        invType.setFill(Paint.valueOf("WHITE"));
        invType.setFont(Font.font(null, FontWeight.NORMAL, 16));
        invStats = new Text[19];
        for(int i = 0; i < 19; i++) {
            invStats[i] = new Text(screenWidth*5/7+screenWidth/8*(i%2), 
                    screenHeight*2/7+screenHeight/40*Math.floor(i/2), "");
            invStats[i].setFill(Paint.valueOf("WHITE"));
            invStats[i].setFont(Font.font(null, FontWeight.NORMAL, 16));
        }
        
        invSort = new Text(screenWidth*11/14, screenHeight*6/7, "");
        invSort.setFill(Paint.valueOf("WHITE"));
        invSort.setFont(Font.font("Luminari", FontWeight.NORMAL, 24));
        invGold = new Text(screenWidth*11/14, screenHeight*25/28, "");
        invGold.setFill(Paint.valueOf("WHITE"));
        invGold.setFont(Font.font("Luminari", FontWeight.NORMAL, 24));
        
        invStatPane.getChildren().add(invType);
        invStatPane.getChildren().addAll(invStats);
        
        invPane.getChildren().addAll(invText);
        invPane.getChildren().addAll(invButtons);
        invPane.getChildren().addAll(invRButtons);
        invPane.getChildren().addAll(invTextPane, invSort, invGold);
        
        //charPane
        ImageView portrait = new ImageView(Images.portrait);
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
        
        Text[] chEqpT = new Text[3];
        for(int i = 0; i < 3; i++) {
            chEqpT[i] = new Text(screenWidth/2, screenHeight/3 + screenHeight*i/7, "");
            chEqpT[i].setFont(Font.font("Bradley Hand", FontWeight.NORMAL, 32));
            chEqpT[i].setFill(Paint.valueOf("WHITE"));
        }
        chEqpT[0].setText("Weapon");
        chEqpT[1].setText("Armor");
        chEqpT[2].setText("Accessories");
        
        chEqp = new Text[5];
        for(int i = 0; i < 5; i++) {
            chEqp[i] = new Text(screenWidth/2, screenHeight*6/15 + screenHeight*i/7, "");
            chEqp[i].setFont(Font.font(null, FontWeight.NORMAL, 28));
            chEqp[i].setFill(Paint.valueOf("WHITE"));
        }
        chEqp[3].setY(screenHeight*6/15 + screenHeight*5/14);
        chEqp[4].setY(screenHeight*6/15 + screenHeight*3/7);
        
        chStats = new Text[14];
        for(int i = 0; i < 14; i++) {
            chStats[i] = new Text(screenWidth*3/4, screenHeight/3 + screenHeight*i/25, "");
            chStats[i].setFont(Font.font(null, FontWeight.NORMAL, 18));
            chStats[i].setFill(Paint.valueOf("WHITE"));
        }
        
        charPane.getChildren().addAll(chEqpT);
        charPane.getChildren().addAll(chStats);
        charPane.getChildren().addAll(chEqp);
        charPane.getChildren().addAll(portrait, chName, chTitle, chHP, chMP, chLVL, chEXP);
        
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
        Text[] opButtons = new Text[4];
        opRButtons = new Rectangle[4];
        for(int i = 0; i < 4; i++) {
            opButtons[i] = new Text(screenWidth*3/4, screenHeight*3/5+screenHeight/18*i, "");
            opButtons[i].setWrappingWidth(screenWidth/6);
            opButtons[i].setTextAlignment(TextAlignment.CENTER);
            opButtons[i].setFill(Paint.valueOf("WHITE"));
            opButtons[i].setFont(Font.font("Trattatello", FontWeight.NORMAL, 28));
            
            opRButtons[i] = new Rectangle(screenWidth/6, screenHeight/20, Paint.valueOf("WHITE"));
            opRButtons[i].relocate(screenWidth*3/4, screenHeight*40/71+screenHeight/18*i);
            opRButtons[i].setOpacity(.2);
            opRButtons[i].setArcHeight(50);
            opRButtons[i].setArcWidth(10);
        }
        opButtons[0].setText("SAVE GAME");
        opButtons[1].setText("LOAD GAME");
        opButtons[2].setText("OPTIONS");
        opButtons[3].setText("QUIT");
        
        opRSaves = new Rectangle[6];
        for(int i = 0; i < 6; i++) {
            opRSaves[i] = new Rectangle(screenWidth/4, screenHeight/5, Paint.valueOf("WHITE"));
            opRSaves[i].relocate(screenWidth/7 + i%2 * screenWidth*2/7, 
                    screenHeight/4 + Math.floor(i/2) * screenHeight*2/9);
            opRSaves[i].setOpacity(.2);
            opRSaves[i].setArcHeight(20);
            opRSaves[i].setArcWidth(20);
        }
        
        //save info
        saveInfo = new Text[6][2];
        saveImages = new ImageView[6];
        
        for (int i = 0; i < 6; i++) {
            saveInfo[i][0] = new Text(screenWidth/7 + i%2 * screenWidth * 2/7 + screenWidth/10,
                    screenHeight/4 + Math.floor(i/2) * screenHeight*2/9 + screenHeight/14, "");
            saveInfo[i][1] = new Text(screenWidth/7 + i%2 * screenWidth * 2/7 + screenWidth/10,
                    screenHeight/4 + Math.floor(i/2) * screenHeight*2/9 + screenHeight/18 + screenHeight/14, "");
            
            for (int y = 0; y < 2; y++) {
                saveInfo[i][y].setFont(Font.font("Trattatello", FontWeight.NORMAL, 24));
                saveInfo[i][y].setFill(Paint.valueOf("WHITE"));
            }
            
            opPane.getChildren().addAll(saveInfo[i]);
            
            saveImages[i] = new ImageView();
            saveImages[i].relocate(screenWidth/7 + i%2 * screenWidth * 2/7 + screenWidth/60,
                    screenHeight*7/32 + Math.floor(i/2) * screenHeight*2/9);
        }
        
        opPane.getChildren().addAll(opButtons);
        opPane.getChildren().addAll(opRButtons);
        opPane.getChildren().addAll(opRSaves);
        opPane.getChildren().addAll(saveImages);
        
        //talkPane
        Rectangle talkR = new Rectangle(screenWidth*7/16, screenHeight*11/40, Paint.valueOf("WHITE"));
        talkR.relocate(screenWidth*5/16, screenHeight*2/3);
        talkR.setOpacity(.7);
        talkR.setArcHeight(20);
        talkR.setArcWidth(20);
                
        talkT = new Text(screenWidth*21/64, screenHeight*67/96, "kek");
        talkT.setWrappingWidth(screenWidth*13/32);
        talkT.setFont(Font.font("Luminari", FontWeight.NORMAL, 18));
        talkT.setFill(Paint.valueOf("BLACK"));
        
        Rectangle talkSelectBox = new Rectangle(screenWidth*3/20, screenHeight*11/40, Paint.valueOf("WHITE"));
        talkSelectBox.relocate(screenWidth*4/5, screenHeight*2/3);
        talkSelectBox.setOpacity(.7);
        talkSelectBox.setArcHeight(20);
        talkSelectBox.setArcWidth(20);
        
        talkYes = new Text(screenWidth*4/5, screenHeight*2/3+screenHeight*11/40/3, "YES");
        talkYes.setWrappingWidth(screenWidth*3/20);
        talkYes.setTextAlignment(TextAlignment.CENTER);
        talkYes.setFont(Font.font("Luminari", FontWeight.NORMAL, 24));
        talkYes.setFill(Paint.valueOf("BLACK"));
        
        talkNo = new Text(screenWidth*4/5, screenHeight*2/3+screenHeight*11/40/3*2, "NO");
        talkNo.setWrappingWidth(screenWidth*3/20);
        talkNo.setTextAlignment(TextAlignment.CENTER);
        talkNo.setFont(Font.font("Luminari", FontWeight.NORMAL, 24));
        talkNo.setFill(Paint.valueOf("BLACK"));
        
        talkSelectR = new Rectangle(screenWidth/10, screenHeight/24, Paint.valueOf("WHITE"));
        talkSelectR.relocate(screenWidth*19/20, screenHeight*4/5);
        talkSelectR.setEffect(new BoxBlur(3, 3, 3));
        talkSelectR.setOpacity(.7);
        
        talkTransition = new TextType(talkT, "");
        
        talkSelectPane.getChildren().addAll(talkSelectBox, talkSelectR, talkYes, talkNo);
        talkPane.getChildren().addAll(talkR, talkT, talkSelectPane);
        
        //tradePane
        traText = new Text[64];
        for(int i = 0; i < 64; i++) {
            traText[i] = new Text(screenWidth/7+screenWidth/7*(int)(i/16), 
                    screenHeight*2/7+screenHeight/28*(i%16), "-");
            traText[i].setFill(Paint.valueOf("WHITE"));
            traText[i].setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        }
        traButtons = new Text[2];
        traRButtons = new Rectangle[2];
        for(int i = 0; i < 2; i++) {
            traButtons[i] = new Text(screenWidth*3/4, screenHeight*3/5+screenHeight/18*i, "");
            traButtons[i].setWrappingWidth(screenWidth/6);
            traButtons[i].setTextAlignment(TextAlignment.CENTER);
            traButtons[i].setFill(Paint.valueOf("WHITE"));
            traButtons[i].setFont(Font.font("Trattatello", FontWeight.NORMAL, 28));
            
            traRButtons[i] = new Rectangle(screenWidth/6, screenHeight/20, Paint.valueOf("WHITE"));
            traRButtons[i].relocate(screenWidth*3/4, screenHeight*40/71+screenHeight/18*i);
            traRButtons[i].setOpacity(.2);
            traRButtons[i].setArcHeight(50);
            traRButtons[i].setArcWidth(10);
        }
        traButtons[0].setText("BUY");
        traButtons[1].setText("CANCEL");
        
        traName = new Text(screenWidth*5/7, screenHeight*2/7, "");
        traName.setWrappingWidth(screenWidth*2/9);
        traName.setTextAlignment(TextAlignment.CENTER);
        traName.setFill(Paint.valueOf("WHITE"));
        traName.setFont(Font.font(null, FontWeight.BOLD, 30));
        traDes = new Text(screenWidth*5/7, screenHeight*3/8, "There's nothing here.");
        traDes.setWrappingWidth(300);
        traDes.setFill(Paint.valueOf("WHITE"));
        traDes.setFont(Font.font(null, FontWeight.NORMAL, 18));
        
        tradeTextPane.getChildren().addAll(traName, traDes);
        
        traType = new Text(screenWidth*5/7, screenHeight*8/31, "");
        traType.setWrappingWidth(screenWidth*2/9);
        traType.setTextAlignment(TextAlignment.CENTER);
        traType.setFill(Paint.valueOf("WHITE"));
        traType.setFont(Font.font(null, FontWeight.NORMAL, 16));
        traStats = new Text[19];
        for(int i = 0; i < 19; i++) {
            traStats[i] = new Text(screenWidth*5/7+screenWidth/8*(i%2), 
                    screenHeight*2/7+screenHeight/40*Math.floor(i/2), "");
            traStats[i].setFill(Paint.valueOf("WHITE"));
            traStats[i].setFont(Font.font(null, FontWeight.NORMAL, 16));
        }
        
        tradeStatPane.getChildren().add(traType);
        tradeStatPane.getChildren().addAll(traStats);
        
        Rectangle box5 = new Rectangle(screenWidth, screenHeight, Paint.valueOf("BLACK"));
        box5.relocate(0, 0);
        box5.setOpacity(.5);
        
        traFocus = new Rectangle(screenWidth/7, screenHeight/24, Paint.valueOf("WHITE"));
        traFocus.relocate(0, screenHeight*3/16);
        traFocus.setEffect(new BoxBlur(5, 5, 3));
        
        traCursor = new Rectangle(screenWidth/8, screenHeight/40, Paint.valueOf("WHITE"));
        traCursor.setEffect(new BoxBlur(3, 3, 3));
        traCursor.setOpacity(0);
        
        Text[] traSelectText = new Text[2];
        for(int i = 0; i < 2; i++) {
            traSelectText[i] = new Text(screenWidth*15/64+screenWidth*i/3, screenHeight*2/9, "");
            traSelectText[i].setFont(Font.font("Zapfino", FontWeight.BOLD, 20));
            traSelectText[i].setFill(Paint.valueOf("WHITE"));
            traSelectText[i].setWrappingWidth(screenWidth/6);
            traSelectText[i].setTextAlignment(TextAlignment.CENTER);
        }
        traSelectText[0].setText("BUY");
        traSelectText[1].setText("SELL");
        
        traSort = new Text(screenWidth*11/14, screenHeight*6/7, "");
        traSort.setFill(Paint.valueOf("WHITE"));
        traSort.setFont(Font.font("Luminari", FontWeight.NORMAL, 24));
        
        traGold = new Text(screenWidth*11/14, screenHeight*25/28, "");
        traGold.setFill(Paint.valueOf("WHITE"));
        traGold.setFont(Font.font("Luminari", FontWeight.NORMAL, 24));
                
        tradePane.getChildren().addAll(box5, traFocus, traCursor);
        
        tradePane.getChildren().addAll(traSelectText);
        tradePane.getChildren().addAll(traText);
        tradePane.getChildren().addAll(traButtons);
        tradePane.getChildren().addAll(traRButtons);
        
        tradePane.getChildren().addAll(tradeTextPane, traSort, traGold);

        //ripPane
        Text ripT = new Text(0, screenHeight/2, ("YOU ARE DEAD"));
        ripT.setWrappingWidth(screenWidth);
        ripT.setTextAlignment(TextAlignment.CENTER);
        ripT.setFont(Font.font("Luminari", FontWeight.BOLD, 80));
        ripT.setFill(Paint.valueOf("MAROON"));
        Rectangle ripR = new Rectangle(screenWidth, screenHeight, Paint.valueOf("BLACK"));
        ripR.setOpacity(.5);
        ripPane.getChildren().addAll(ripR, ripT);
        
        overlayPane.setOpacity(0);
        menuPane.setOpacity(0);
        ripPane.setOpacity(0);
        tradePane.setOpacity(0); 
        talkSelectPane.setOpacity(0);
        inmapLayout.getChildren().addAll(floorPane, UIPane, overlayPane, menuPane, talkPane, tradePane, ripPane);
        inmapLayout.setBackground(new Background(new BackgroundFill(Paint.valueOf("BLACK"), null, null)));
    }
    
    //initialize animation
    public final void initAnimation() {
        //movement animation creation
        tUp = new Timeline();
        tLeft = new Timeline();
        tDown = new Timeline();
        tRight = new Timeline();
        for(int x = 0; x < 24; x++) {
            for(int y = 0; y < 16; y++) {
                for(int i = 0; i < 7; i++) {
                    if(x != 11 || y != 7 || (i != 1 && i != 3 && i != 4)) {
                        tUp.getKeyFrames().addAll(
                                new KeyFrame(Duration.ZERO, 
                                new KeyValue(imageViews[x][y][i].translateYProperty(), 
                                        imageViews[x][y][i].getTranslateY()-width)),
                                new KeyFrame(Duration.millis(100),
                                new KeyValue(imageViews[x][y][i].translateYProperty(),
                                        imageViews[x][y][i].getTranslateY())));
                        tLeft.getKeyFrames().addAll(
                                new KeyFrame(Duration.ZERO, 
                                new KeyValue(imageViews[x][y][i].translateXProperty(), 
                                        imageViews[x][y][i].getTranslateX()-width)),
                                new KeyFrame(Duration.millis(100),
                                new KeyValue(imageViews[x][y][i].translateXProperty(),
                                        imageViews[x][y][i].getTranslateX())));
                        tDown.getKeyFrames().addAll(
                                new KeyFrame(Duration.ZERO, 
                                new KeyValue(imageViews[x][y][i].translateYProperty(), 
                                        imageViews[x][y][i].getTranslateY()+width)),
                                new KeyFrame(Duration.millis(100),
                                new KeyValue(imageViews[x][y][i].translateYProperty(),
                                        imageViews[x][y][i].getTranslateY())));
                        tRight.getKeyFrames().addAll(
                                new KeyFrame(Duration.ZERO, 
                                new KeyValue(imageViews[x][y][i].translateXProperty(), 
                                        imageViews[x][y][i].getTranslateX()+width)),
                                new KeyFrame(Duration.millis(100),
                                new KeyValue(imageViews[x][y][i].translateXProperty(),
                                        imageViews[x][y][i].getTranslateX())));
                    }
                }
            }
        }
        
        //sprite animation
        cDown = new Sprite(playerView, Duration.millis(100), 2, 0, 0, 64, 96, Images.playerAnimation);
        cLeft = new Sprite(playerView, Duration.millis(100), 2, 1, 0, 64, 96, Images.playerAnimation);
        cRight = new Sprite(playerView, Duration.millis(100), 2, 2, 0, 64, 96, Images.playerAnimation);
        cUp = new Sprite(playerView, Duration.millis(100), 2, 3, 0, 64, 96, Images.playerAnimation);
        cDown2 = new Sprite(playerView, Duration.millis(100), 2, 0, 2, 64, 96, Images.playerAnimation);
        cLeft2 = new Sprite(playerView, Duration.millis(100), 2, 1, 2, 64, 96, Images.playerAnimation);
        cRight2 = new Sprite(playerView, Duration.millis(100), 2, 2, 2, 64, 96, Images.playerAnimation);
        cUp2 = new Sprite(playerView, Duration.millis(100), 2, 3, 2, 64, 96, Images.playerAnimation);
    }
    
    //general update
    public void update(InMapViewData vd) {
        switch (vd.focus) {
            case "floor":
                updateFloor(vd);
                break;
            case "talk":
                updateTalk(vd);
                break;
            case "trade":
                updateTrade(vd);
                break;
            case "menu":
                updateMenu(vd);
                break;
            case "rip":
                //update overlay health and mana
                uiHealth.setVisible(false);
                uiHealthT.setText("0/" + vd.party[0].maxHP);
                uiMana.setFitWidth((double)vd.party[0].currentMP/vd.party[0].maxMP*uiManaR.getWidth());
                uiManaT.setText(vd.party[0].currentMP + "/" + vd.party[0].maxMP);
                //remove shadow, health, and player image
                imageViews[11][7][1].setVisible(false);
                imageViews[11][7][4].setVisible(false);
                playerView.setVisible(false);
                
                if(ripPane.getOpacity() != 1) {
                    FadeTransition ft = new FadeTransition(Duration.millis(500), ripPane);
                    ft.setFromValue(ripPane.getOpacity());
                    ft.setToValue(1);
                    ft.play();
                }
                break;
            default:
                System.out.println("Failed focus.");
                break;
        }
    }
    
    //update floor: tiles, characters, items, etc
    private void updateFloor(InMapViewData vd) {
        //remove menu window
        if(menuPane.getOpacity() == 1) {
            FadeTransition ft = new FadeTransition(Duration.millis(200), menuPane);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.play();
        }

        //remove talk window
        if(talkPane.getOpacity() != 0) {
            FadeTransition ft = new FadeTransition(Duration.millis(100), talkPane);
            ft.setFromValue(talkPane.getOpacity());
            ft.setToValue(0);
            ft.play();
            ft.setOnFinished(e -> {
                talkSelectPane.setOpacity(0);
            });
        }
        if(talkTransition.getStatus() == Status.RUNNING) {
            talkTransition.stop();
        }
        
        //remove trade window
        if(tradePane.getOpacity() != 0) {
            FadeTransition ft = new FadeTransition(Duration.millis(100), tradePane);
            ft.setFromValue(tradePane.getOpacity());
            ft.setToValue(0);
            ft.play();
        }

        //quick info
        if(vd.qiVisible) {
            if(overlayPane.getOpacity() == 0) {
                FadeTransition ft = new FadeTransition(Duration.millis(200), overlayPane);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();
            }
            qiName.setText(vd.floor.location.name);
            qiType.setText(vd.floor.location.type);
            qiDiff.setText("Difficulty: " + vd.floor.location.difficulty);
            qiFloor.setText("Floor " + vd.floor.location.currentFloor);
            qiChar.setText(vd.party[0].name);
            qiLevel.setText("Level " + vd.party[0].LVL);
            qiGold.setText("Gold: " + String.valueOf(vd.gold));
        }
        else if(!vd.qiVisible) {
            if(overlayPane.getOpacity() == 1) {
                FadeTransition ft = new FadeTransition(Duration.millis(200), overlayPane);
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.play();
            }
        }

        //overlay health and mana
        uiHealthR.setWidth(((double)-10000/(vd.party[0].maxHP+11000)+1)*screenWidth);
        uiHealth.setFitWidth((double)vd.party[0].currentHP/vd.party[0].maxHP*uiHealthR.getWidth());
        uiHealthT.setText(vd.party[0].currentHP + "/" + vd.party[0].maxHP);
        uiManaR.setWidth(((double)-10000/(vd.party[0].maxMP+11000)+1)*screenWidth);
        uiMana.setFitWidth((double)vd.party[0].currentMP/vd.party[0].maxMP*uiManaR.getWidth());
        uiManaT.setText(vd.party[0].currentMP + "/" + vd.party[0].maxMP);

        //tiles
        for(int x = vd.floor.party[0].x - 11; x < vd.floor.party[0].x + 13; x++) {
            for(int y = vd.floor.party[0].y - 7; y < vd.floor.party[0].y + 9; y++) {
                if(x >= 0 && x < vd.floor.sizeX && y >= 0 && y < vd.floor.sizeY && vd.floor.tiles[x][y].id != -1) {
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][0]
                            .setImage(Images.tiles[vd.floor.tiles[x][y].id]);
                }
                else if(vd.locationType.equals("city"))
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][0]
                            .setImage(Images.tiles[Tile.tiles.get("Grass").id]);
                else
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][0].setImage(null);
            }
        }

        //shadows
        for(int x = vd.floor.party[0].x - 11; x < vd.floor.party[0].x + 13; x++) {
            for(int y = vd.floor.party[0].y - 7; y < vd.floor.party[0].y + 9; y++) {
                if(x >= 0 && x < vd.floor.sizeX && y >= 0 && y < vd.floor.sizeY 
                        && (vd.floor.chars[x][y].exists || vd.floor.items[x][y].exists))
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][1].setVisible(true);
                else
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][1].setVisible(false);
            }
        }

        //items
        for(int x = vd.floor.party[0].x - 11; x < vd.floor.party[0].x + 13; x++) {
            for(int y = vd.floor.party[0].y - 7; y < vd.floor.party[0].y + 9; y++) {
                if(x >= 0 && x < vd.floor.sizeX && y >= 0 && y < vd.floor.sizeY && vd.floor.items[x][y].exists) {
                    switch(vd.floor.items[x][y].type) {
                        case WEAPON:
                            imageViews[x-vd.floor.party[0].x+11]
                                    [y-vd.floor.party[0].y+7][2].setImage(Images.weapon);
                            break;
                        case ARMOR:
                            imageViews[x-vd.floor.party[0].x+11]
                                    [y-vd.floor.party[0].y+7][2].setImage(Images.armor);
                            break;
                        case ACCESSORY:
                            imageViews[x-vd.floor.party[0].x+11]
                                    [y-vd.floor.party[0].y+7][2].setImage(Images.accessory);
                            break;
                        case MATERIAL:
                            imageViews[x-vd.floor.party[0].x+11]
                                    [y-vd.floor.party[0].y+7][2].setImage(Images.material);
                            break;
                        case CONSUMABLE:
                            imageViews[x-vd.floor.party[0].x+11]
                                    [y-vd.floor.party[0].y+7][2].setImage(Images.consumable);
                            break;
                    }
                }
                else {
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][2].setImage(null);
                }
            }
        }

        //characters
        for(int x = vd.floor.party[0].x - 11; x < vd.floor.party[0].x + 13; x++) {
            for(int y = vd.floor.party[0].y - 7; y < vd.floor.party[0].y + 9; y++) {
                if(x != 11 || y != 7) {
                    if(x >= 0 && x < vd.floor.sizeX && y >= 0 && y < vd.floor.sizeY && vd.floor.chars[x][y].exists) {
                        imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][3]
                                .setImage(Images.getSprite(vd.floor.chars[x][y].id));
                    }
                    else
                        imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][3].setImage(null);
                }
            }
        }

        //player
        switch(vd.facing) {
            case UP:
                playerView.setImage(Images.playerAnimation);
                playerView.setViewport(new Rectangle2D(0, 288, 64, 96));
                break;
            case LEFT:
                playerView.setImage(Images.playerAnimation);
                playerView.setViewport(new Rectangle2D(0, 96, 64, 96));
                break;
            case RIGHT:
                playerView.setImage(Images.playerAnimation);
                playerView.setViewport(new Rectangle2D(0, 192, 64, 96));
                break;
            case DOWN:
                playerView.setImage(Images.playerAnimation);
                playerView.setViewport(new Rectangle2D(0, 0, 64, 96));
                break;
        }

        //health
        for(int x = vd.floor.party[0].x - 11; x < vd.floor.party[0].x + 13; x++) {
            for(int y = vd.floor.party[0].y - 7; y < vd.floor.party[0].y + 9; y++) {
                if(x >= 0 && x < vd.floor.sizeX && y >= 0 && y < vd.floor.sizeY && vd.floor.chars[x][y].exists) {
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][4].setVisible(true);
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][4].setFitWidth(64 * 
                            (double)vd.floor.chars[x][y].currentHP / vd.floor.chars[x][y].maxHP);
                }
                else {
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][4].setVisible(false);
                }
            }
        }

        //fog
        for(int x = vd.floor.party[0].x - 11; x < vd.floor.party[0].x + 13; x++) {
            for(int y = vd.floor.party[0].y - 7; y < vd.floor.party[0].y + 9; y++) {
                if(x >= 0 && x < vd.floor.sizeX && y >= 0 && y < vd.floor.sizeY) {
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][6]
                            .setOpacity(1-(double)vd.floor.tiles[x][y].vis/64);
                }
                else if(vd.locationType.equals("city")) {
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][6].setOpacity(.05);
                }
                else {
                    imageViews[x-vd.floor.party[0].x+11][y-vd.floor.party[0].y+7][6].setOpacity(1);
                }
            }
        }

        //movement animation if not running
        if(!vd.running) {
            if(vd.returnCode >= 1000) {
                vd.returnCode = (int)Math.floor(vd.returnCode/1000);
            }

            if(vd.returnCode == 5) { //up
                if(floorPane.getChildren().indexOf(imageViews[11][7][3]) == 755) {
                    floorPane.getChildren().remove(755);
                    floorPane.getChildren().remove(731);
                    floorPane.getChildren().remove(707);
                    floorPane.getChildren().add(846, imageViews[11][7][1]);
                    floorPane.getChildren().add(847, imageViews[11][7][2]);
                    floorPane.getChildren().add(848, imageViews[11][7][3]);
                }
                tUp.play();
                tUp.setOnFinished(e -> {
                    floorPane.getChildren().remove(846, 849);
                    floorPane.getChildren().add(707, imageViews[11][7][1]);
                    floorPane.getChildren().add(731, imageViews[11][7][2]);
                    floorPane.getChildren().add(755, imageViews[11][7][3]);
                });

                if(walkState)
                    cUp.start();
                else
                    cUp2.start();

                walkState = !walkState;
            }
            else if(vd.returnCode == 6) { //left
                tLeft.play();

                if(walkState)
                    cLeft.start();
                else
                    cLeft2.start();

                walkState = !walkState;
            }
            else if(vd.returnCode == 7) { //down
                tDown.play();

                if(walkState)
                    cDown.start();
                else
                    cDown2.start();

                walkState = !walkState;
            }
            else if(vd.returnCode == 8) { //right
                tRight.play();

                if(walkState)
                    cRight.start();
                else
                    cRight2.start();

                walkState = !walkState;
            }
            //use up return code
            vd.returnCode = -1;
        }

        //new entry in log
        if(log.size() > uiLogSize.get()) {
            for(int i = log.size(); i > 0; i--) {
                uiLogT[i].setOpacity(uiLogT[i-1].getOpacity());
            }
            uiLogT[0].setOpacity(1);
            uiLogSize.set(uiLogSize.get() + 1);
            for(int i = 0; i < 30; i++) {
                if(i < log.size()) {
                    uiLogT[i].setText(log.get(i));
                    uiLogFade[i].setFromValue(uiLogT[i].getOpacity());
                    uiLogFade[i].setDuration(Duration.millis(uiLogT[i].getOpacity()*4000));
                    uiLogFade[i].playFromStart();
                }
                else {
                    uiLogT[i].setText("");
                }
            }
        }

        //floor text
        qiFloor.setText("Floor " + vd.floor.location.currentFloor);
        
    }
    
    //update talk box and selection
    private void updateTalk(InMapViewData vd) {
        //only update player
        switch(vd.facing) {
            case UP:
                playerView.setImage(Images.playerAnimation);
                playerView.setViewport(new Rectangle2D(0, 288, 64, 96));
                break;
            case LEFT:
                playerView.setImage(Images.playerAnimation);
                playerView.setViewport(new Rectangle2D(0, 96, 64, 96));
                break;
            case RIGHT:
                playerView.setImage(Images.playerAnimation);
                playerView.setViewport(new Rectangle2D(0, 192, 64, 96));
                break;
            case DOWN:
                playerView.setImage(Images.playerAnimation);
                playerView.setViewport(new Rectangle2D(0, 0, 64, 96));
                break;
        }
        
        //remove trade window
        if(tradePane.getOpacity() != 0) {
            FadeTransition ft = new FadeTransition(Duration.millis(100), tradePane);
            ft.setFromValue(tradePane.getOpacity());
            ft.setToValue(0);
            ft.play();
        }

        //open talk box
        if(talkPane.getOpacity() != 1) {
            FadeTransition ft = new FadeTransition(Duration.millis(100), talkPane);
            ft.setFromValue(talkPane.getOpacity());
            ft.setToValue(1);
            ft.play();
            talkTransition.setStrings(vd.talkText);
            talkTransition.playFromStart();
        }
        //selecting
        if(vd.talkSelect >= 0) {
            //open select box
            if(talkSelectPane.getOpacity() != 1) {
                FadeTransition ft = new FadeTransition(Duration.millis(200), talkSelectPane);
                ft.setFromValue(talkSelectPane.getOpacity());
                ft.setToValue(1);
                ft.play();
            }
            
            talkSelectR.relocate(screenWidth*4/5+screenHeight/25, screenHeight*2/3+screenHeight*11/40/3*(vd.talkSelect+1)-screenHeight/32);
        }
        //close select window
        else if(talkSelectPane.getOpacity() != 0) {
            FadeTransition ft = new FadeTransition(Duration.millis(200), talkSelectPane);
            ft.setFromValue(talkSelectPane.getOpacity());
            ft.setToValue(0);
            ft.play();
        }
        //finish typing
        if(vd.talkSelect == -2) {
            talkTransition.finish();
        }
        //update text
        else if(vd.talkSelect == -3) {
            talkTransition.setStrings(vd.talkText);
            talkTransition.playFromStart();
        }
        //advance to next lines
        if(vd.talkSelect < 0 && vd.talkIndex > talkTransition.getIndex()) {
            while(talkTransition.getIndex() < vd.talkIndex)
                talkTransition.next();
            talkTransition.playFromStart();
        }
    }
    
    //update trade window
    private void updateTrade(InMapViewData vd) {
        //fade in
        if(tradePane.getOpacity() != 1) {
            FadeTransition ft = new FadeTransition(Duration.millis(200), tradePane);
            ft.setFromValue(tradePane.getOpacity());
            ft.setToValue(1);
            ft.play();
        }

        //remove talk window
        if(talkPane.getOpacity() != 0) {
            FadeTransition ft = new FadeTransition(Duration.millis(100), talkPane);
            ft.setFromValue(talkPane.getOpacity());
            ft.setToValue(0);
            ft.play();
            ft.setOnFinished(e -> {
                talkSelectPane.setOpacity(0);
            });
        }
        if(talkTransition.getStatus() == Status.RUNNING) {
            talkTransition.stop();
        }

        //toggle menu
        if(vd.menuToggle && tradePane.getChildren().contains(tradeTextPane)) {
            tradePane.getChildren().remove(tradeTextPane);
            tradePane.getChildren().add(tradeStatPane);
        }
        else if(!vd.menuToggle && tradePane.getChildren().contains(tradeStatPane)) {
            tradePane.getChildren().remove(tradeStatPane);
            tradePane.getChildren().add(tradeTextPane);
        }
        
        //change menus
        if(vd.menuP.y == -1) {
            traFocus.setOpacity(.5);
            traCursor.setOpacity(0);
            traName.setText("");
            traType.setText("");
            traDes.setText("");
            if(vd.tradeState)
                traFocus.setX(screenWidth/4);
            else
                traFocus.setX(screenWidth*7/12);
            refreshTrade(vd);
        }
        else {
            traFocus.setOpacity(0.25);
            for(int i = 0; i < 2; i++)
                traRButtons[i].setOpacity(.2);
            //selection
            if(vd.selectP != -1) {
                traCursor.setOpacity(.2);
                traDes.setText(vd.invDes);
                traRButtons[vd.selectP].setOpacity(.5);
            }
            //moving around menu
            else {
                refreshTrade(vd);
                traCursor.setOpacity(.3);
                traCursor.relocate(screenWidth*2/15+screenWidth/7*vd.menuP.x, 
                        screenHeight*4/15+screenHeight/28*vd.menuP.y);
                if(vd.tradeState && vd.tradeInv[vd.menuP.x*16+vd.menuP.y].exists) {
                    traName.setText(vd.tradeInv[vd.menuP.x*16+vd.menuP.y].displayName);
                    traType.setText(vd.tradeInv[vd.menuP.x*16+vd.menuP.y].type.toString());
                    traDes.setText(vd.invDes);
                    updateTradeStats(vd.tradeInv[vd.menuP.x*16+vd.menuP.y]);
                }
                else if(!vd.tradeState && vd.inv[vd.menuP.x*16+vd.menuP.y].exists) {
                    traName.setText(vd.inv[vd.menuP.x*16+vd.menuP.y].displayName);
                    traType.setText(vd.inv[vd.menuP.x*16+vd.menuP.y].type.toString());
                    traDes.setText(vd.invDes);
                    updateTradeStats(vd.inv[vd.menuP.x*16+vd.menuP.y]);
                }
                else {
                    traName.setText("");
                    traType.setText("");
                    traDes.setText(vd.invDes);
                    updateTradeStats(new Item());
                }
            }
        }
    }
    
    //update menu pages
    private void updateMenu(InMapViewData vd) {
        //add menu window if not already added
        if(vd.floor != null && !inmapLayout.getChildren().contains(menuPane)) {
            inmapLayout.getChildren().add(menuPane);
        }
        //fade in
        if(menuPane.getOpacity() != 1) {
            FadeTransition ft = new FadeTransition(Duration.millis(200), menuPane);
            ft.setFromValue(menuPane.getOpacity());
            ft.setToValue(1);
            ft.play();
        }
        
        //close trade
        if(tradePane.getOpacity() != 0) {
            FadeTransition ft = new FadeTransition(Duration.millis(100), tradePane);
            ft.setFromValue(tradePane.getOpacity());
            ft.setToValue(0);
            ft.play();
        }

        //remove quick info boxes
        if(overlayPane.getOpacity() != 0) {
            FadeTransition ft = new FadeTransition(Duration.millis(200), overlayPane);
            ft.setFromValue(overlayPane.getOpacity());
            ft.setToValue(0);
            ft.play();
        }

        //remove talk window
        if(talkPane.getOpacity() != 0) {
            FadeTransition ft = new FadeTransition(Duration.millis(100), talkPane);
            ft.setFromValue(talkPane.getOpacity());
            ft.setToValue(0);
            ft.play();
            ft.setOnFinished(e -> {
                talkSelectPane.setOpacity(0);
            });
        }
        if(talkTransition.getStatus() == Status.RUNNING) {
            talkTransition.stop();
        }

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
            invType.setText("");
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
                            invType.setText(vd.inv[vd.tempP.x*16+vd.tempP.y].type.toString());
                            invDes.setText(vd.invDes);
                            updateInvStats(vd.inv[vd.tempP.x*16+vd.tempP.y]);
                        }
                        else {
                            invName.setText("");
                            invType.setText("");
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
                            invType.setText(vd.inv[vd.menuP.x*16+vd.menuP.y].type.toString());
                            invDes.setText(vd.invDes);
                            updateInvStats(vd.inv[vd.menuP.x*16+vd.menuP.y]);
                        }
                        else {
                            invName.setText("");
                            invType.setText("");
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
                    for(Rectangle r: opRButtons)
                        r.setOpacity(.2);
                    for(Rectangle r: opRSaves)
                        r.setOpacity(.2);
                    //selectP
                    if(vd.selectP != -1) {
                        opRSaves[vd.selectP].setOpacity(.5);
                    }
                    else if(vd.menuP.y != -1) {
                        opRButtons[vd.menuP.y].setOpacity(.5);
                    }
                    break;

                default:
                    break;
            }
        }
    }
    
    //toggle menu
    public void toggleMenu(InMapViewData vd) {
        //remove
        if(menuPane.getOpacity() == 1) {
            FadeTransition ft = new FadeTransition(Duration.millis(200), menuPane);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.play();
        }
        else if(menuPane.getOpacity() == 0) {
            changeMenu(vd);
            if(vd.floor != null && !inmapLayout.getChildren().contains(menuPane))
                inmapLayout.getChildren().add(menuPane);
            FadeTransition ft = new FadeTransition(Duration.millis(200), menuPane);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
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
                
                if(vd.invStacks[i] > 1)
                    invText[i].setText(invText[i].getText() + " x" + vd.invStacks[i]);
            }
            for(int i = 0; i < 4; i++)
                invRButtons[i].setOpacity(.2);
            
            if(vd.sortType == 0)
                invSort.setText("(F) Sorted by Name");
            else if(vd.sortType == 1)
                invSort.setText("(F) Sorted by Type");
            else if(vd.sortType == 2)
                invSort.setText("(F) Sorted by Value");
            else if(vd.sortType == 3)
                invSort.setText("(F) Sorted by Damage");
            else if(vd.sortType == 4)
                invSort.setText("(F) Sorted by Rarity");
            
            invGold.setText(String.valueOf(vd.gold) + " g");
        }
        else if(vd.menuWindow.equals("char")) {
            chName.setText("Ronald Rump");
            chTitle.setText("Weapon of Mass Destruction");
            chHP.setText("HP " + vd.party[0].currentHP + "/" + vd.party[0].maxHP);
            chMP.setText("MP " + vd.party[0].currentMP + "/" + vd.party[0].maxMP);
            chLVL.setText("Level " + vd.party[0].LVL);
            chEXP.setText("To next: "+((int)(150*Math.sqrt(vd.party[0].LVL+10)-430)-vd.party[0].EXP)+" EXP");
            chEqp[0].setText(vd.party[0].weapon.displayName);
            chEqp[1].setText(vd.party[0].armor.displayName);
            chEqp[2].setText(vd.party[0].acc1.displayName);
            chEqp[3].setText(vd.party[0].acc2.displayName);
            chEqp[4].setText(vd.party[0].acc3.displayName);
            chStats[0].setText("VIT: " + vd.party[0].VIT);
            chStats[1].setText("INT: " + vd.party[0].INT);
            chStats[2].setText("STR: " + vd.party[0].STR);
            chStats[3].setText("WIS: " + vd.party[0].WIS);
            chStats[4].setText("LUK: " + vd.party[0].LUK);
            chStats[5].setText("CHA: " + vd.party[0].CHA);
            chStats[6].setText("CRT: " + vd.party[0].CRT);
            chStats[7].setText("ACC: " + vd.party[0].ACC);
            chStats[8].setText("EVA: " + vd.party[0].EVA);
            chStats[9].setText("DEF: " + vd.party[0].DEF);
            chStats[10].setText("RES: " + vd.party[0].RES);
            chStats[11].setText("PRC: " + vd.party[0].PRC);
        }
        else if(vd.menuWindow.equals("party")) {
            
        }
        else if(vd.menuWindow.equals("notes")) {
            
        }
        else if(vd.menuWindow.equals("options")) {
            for(Rectangle r: opRButtons)
                r.setOpacity(.2);
            for(Rectangle r: opRSaves)
                r.setOpacity(.2);
            for(int i = 0; i < 6; i++) {
                saveInfo[i][0].setText(vd.saveInfo[i][0].getText());
                saveInfo[i][1].setText(vd.saveInfo[i][1].getText());
                saveImages[i].setImage(vd.saveImages[i].getImage());
            }
        }
    }
    
    //trade data refresh
    public void refreshTrade(InMapViewData vd) {
        if(vd.tradeState) {
            traButtons[0].setText("BUY");
            for(int i = 0; i < 64; i++) {
                if(!vd.tradeInv[i].exists)
                    traText[i].setText("-");
                else if(vd.tradeInv[i].displayName.length() > 15)
                    traText[i].setText(vd.tradeInv[i].displayName.substring(0, 12) + "...");
                else
                    traText[i].setText(vd.tradeInv[i].displayName);

                if(vd.tradeStacks[i] > 1)
                    traText[i].setText(vd.tradeInv[i].displayName + " x" + vd.tradeStacks[i]);
            }
        }
        else {
            traButtons[0].setText("SELL");
            for(int i = 0; i < 64; i++) {
                if(!vd.inv[i].exists)
                    traText[i].setText("-");
                else if(vd.inv[i].displayName.length() > 15)
                    traText[i].setText(vd.inv[i].displayName.substring(0, 12) + "...");
                else
                    traText[i].setText(vd.inv[i].displayName);

                if(vd.invStacks[i] > 1)
                    traText[i].setText(vd.inv[i].displayName + " x" + vd.invStacks[i]);
            }
        }
        for(int i = 0; i < 2; i++)
            traRButtons[i].setOpacity(.2);
            
        traGold.setText(String.valueOf(vd.gold) + " g");
            
        if(vd.sortType == 0)
            traSort.setText("(F) Sorted by Name");
        else if(vd.sortType == 1)
            traSort.setText("(F) Sorted by Type");
        else if(vd.sortType == 2)
            traSort.setText("(F) Sorted by Value");
        else if(vd.sortType == 3)
            traSort.setText("(F) Sorted by Damage");
        else if(vd.sortType == 4)
            traSort.setText("(F) Sorted by Rarity");
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
        invStats[8].setText("WIS: " + i.WIS);
        invStats[9].setText("LUK: " + i.LUK);
        invStats[10].setText("CHA: " + i.CHA);
        invStats[11].setText("DEF: " + i.DEF);
        invStats[12].setText("RES: " + i.RES);
        invStats[13].setText("EVA: " + i.EVA);
        invStats[14].setText("MAXHP: " + i.MHP);
        invStats[15].setText("HEALHP: " + i.CHP);
        invStats[16].setText("MAXMP: " + i.MMP);
        invStats[17].setText("HEALMP: " + i.CMP);
        invStats[18].setText("VAL: " + i.VAL);
    }
    
    //update trade inventory stat text
    private void updateTradeStats(Item i) {
        traStats[0].setText("DMG: " + i.DMG);
        traStats[1].setText("HIT: " + i.HIT);
        traStats[2].setText("CRT: " + i.CRT);
        traStats[3].setText("PRC: " + i.PRC);
        traStats[4].setText("VIT: " + i.VIT);
        traStats[5].setText("INT: " + i.INT);
        traStats[6].setText("ACC: " + i.ACC);
        traStats[7].setText("STR: " + i.STR);
        traStats[8].setText("WIS: " + i.WIS);
        traStats[9].setText("LUK: " + i.LUK);
        traStats[10].setText("CHA: " + i.CHA);
        traStats[11].setText("DEF: " + i.DEF);
        traStats[12].setText("RES: " + i.RES);
        traStats[13].setText("EVA: " + i.EVA);
        traStats[14].setText("MAXHP: " + i.MHP);
        traStats[15].setText("HEALHP: " + i.CHP);
        traStats[16].setText("MAXMP: " + i.MMP);
        traStats[17].setText("HEALMP: " + i.CMP);
        traStats[18].setText("VAL: " + i.VAL);
    }
    
    //return menuPane
    Pane getMenuPane() {
        return menuPane;
    }
    
    //return scene
    Scene getScene() {
        return scene;
    }
    
    //set log
    void setLog(ArrayList<String> log) {
        this.log = log;
    }
}