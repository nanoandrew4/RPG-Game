package overworld;

import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

class Images {

    /*
        Class contains and loads all images to be used by view in individual imageviews
        TODO: WIDTH AND HEIGHT FOR BANNER ITEMS MUST BE CALCULATED BEFORE IMAGES CLASS IS CREATED
     */

    Image tileBorder;

    Image forestLight;
    Image forestHeavy;
    Image village;
    Image tower;
    Image mountain;
    Image grass;

    Image banner;
    Image attack;
    Image enter;
    Image diplomacy;

    Image waterAll;
    Image waterE;
    Image waterN;
    Image waterNE;
    Image waterNESE;
    Image waterNW;
    Image waterNWNE;
    Image waterNWSW;
    Image waterS;
    Image waterSE;
    Image waterSW;
    Image waterSWSE;
    Image waterW;

    Images(double width, double height) {
        tileBorder = new Image("/media/graphics/overworld/TileBorder.png", width, height, true, false);

        forestLight = new Image("/media/graphics/overworld/ForestLight.png", width, height, true, false);
        forestHeavy = new Image("/media/graphics/overworld/ForestHeavy.png", width, height, true, false);
        village = new Image("/media/graphics/overworld/Village.png", width, height, true, false);
        tower = new Image("/media/graphics/overworld/Tower.png", width, height, true, false);
        mountain = new Image("/media/graphics/overworld/Mountain.png", width, height, true, false);
        grass = new Image("/media/graphics/overworld/Grass.png", width, height, true, false);

        banner = new Image("/media/graphics/overworld/banner/banner.png", width / 4, height / 4, true, false);
        attack = new Image("/media/graphics/overworld/banner/attack.png", width / 5, height / 5, true, false);
        enter = new Image("/media/graphics/overworld/banner/enter.png", width / 5, height / 5, true, false);
        diplomacy = new Image("/media/graphics/overworld/banner/diplomacy.png", width / 5, height / 5, true, false);

        waterAll = new Image("/media/graphics/overworld/water/WaterAll.png", width, height, true, false);
        waterE = new Image("/media/graphics/overworld/water/WaterE.png", width, height, true, false);
        waterN = new Image("/media/graphics/overworld/water/WaterN.png", width, height, true, false);
        waterNE = new Image("/media/graphics/overworld/water/WaterNE.png", width, height, true, false);
        waterNESE = new Image("/media/graphics/overworld/water/WaterNESE.png", width, height, true, false);
        waterNW = new Image("/media/graphics/overworld/water/WaterNW.png", width, height, true, false);
        waterNWNE = new Image("/media/graphics/overworld/water/WaterNWNE.png", width, height, true, false);
        waterNWSW = new Image("/media/graphics/overworld/water/WaterNWSW.png", width, height, true, false);
        waterS = new Image("/media/graphics/overworld/water/WaterS.png", width, height, true, false);
        waterSE = new Image("/media/graphics/overworld/water/WaterSE.png", width, height, true, false);
        waterSW = new Image("/media/graphics/overworld/water/WaterSW.png", width, height, true, false);
        waterSWSE = new Image("/media/graphics/overworld/water/WaterSWSE.png", width, height, true, false);
        waterW = new Image("/media/graphics/overworld/water/WaterW.png", width, height, true, false);
    }
}

@SuppressWarnings("ALL")
class OverworldView {

    /*
        Displays all graphical elements of the overworld with data from the model provided by the controller

        TODO: CHANGE ALL STATIC ARBITRARY VALUES IN LOWER CLASSES TO WORK ON ALL DISPLAYS EQUALLY
     */

    Images images;

    /*
        Vars to be used for moving animation
     */

    double speedXVal, speedYVal;
    final DoubleProperty speedX = new SimpleDoubleProperty(), speedY = new SimpleDoubleProperty();
    private final LongProperty lastUpdateTime = new SimpleLongProperty();

    private Pane overworldLayout, infoBox;

    ImageView[][][] imageViews; // for controller to access and add click events
    ImageView centerTile;
    ImageView playerIV;
    HashMap<Party, ImageView> pIVHashMap; // when party is eliminated, delete from hashmap

    static float zoomMultiplier = 2.2f;
    static int zoom = (int) (6 * zoomMultiplier);

    double xOffset = 0, yOffset = 0; // total offset from initial tile

    private double screenWidth, screenHeight, padding = 15;
    static double mapTileSize;

    Button manageCity = new Button("Manage"), diplomacy = new Button("Diplomacy"), trade = new Button("Trade"); // for info window, so controller can access it - will be imagebutton
    Button enterDungeon = new Button("Enter");

    Stack<Pane> paneStack = new Stack<>(); // makes returning to previous window easier

    OverworldView(int mapSize, double screenWidth, double screenHeight) {
        imageViews = new ImageView[zoom * 2 + 1][zoom * 2 + 1][2];
        pIVHashMap = new HashMap<>();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        if (screenWidth > screenHeight) {
            mapTileSize = screenHeight / (zoom / zoomMultiplier);
        } else {
            mapTileSize = screenWidth / (zoom / zoomMultiplier);
        }
    }

    double getMapTileSize() {
        return mapTileSize;
    }

    double getPlayerXOffset() {
        return centerTile.screenToLocal(playerIV.localToScreen(3, 3)).getX() - getMapTileSize() / 2;
    }

    double getPlayerYOffset() {
        return -centerTile.screenToLocal(playerIV.localToScreen(3, 3)).getY() + getMapTileSize() / 4;
    }

    Scene initDisplay(Tile[][] tiles, Party player, ArrayList<Party> parties, double screenWidth, double screenHeight, double mapTileSize, int mapSize) {

        /*
            Loads the initial set of tiles for display based on current position
         */

        System.out.println("Loading images");

        images = new Images(mapTileSize + (mapTileSize / (zoom * 4)), mapTileSize + (mapTileSize / (zoom * 4)));

        long start = System.currentTimeMillis();

        speedXVal = 100 / mapTileSize;
        speedYVal = 50 / mapTileSize;

        overworldLayout = new Pane();
        for (int y = -zoom; y <= zoom; y++) {
            for (int x = -zoom; x <= zoom; x++) {
                int xPos = (player.getTileX() + x < 0 ? (player.getTileX() + x >= mapSize ? mapSize - 1 : 0) : player.getTileX() + x);
                int yPos = (player.getTileY() + y < 0 ? (player.getTileY() + y >= mapSize ? mapSize - 1 : 0) : player.getTileY() + y);
                drawTile(tiles, player, parties, (float) (mapTileSize / 2), x + zoom, y + zoom, xPos, yPos,
                        0.5 * mapTileSize * (x - y) + (screenWidth / 2) - mapTileSize / 2,
                        0.25 * mapTileSize * (x + y) + (screenHeight / 2) - mapTileSize * 3 / 4
                );
            }
        }

        System.out.println();

        centerTile = imageViews[zoom + 1][zoom + 1][0];

        drawEntities(player, parties);

        System.out.println("Center tile " + centerTile.getLayoutX() + ", " + centerTile.getLayoutY());
        System.out.println("Player pos " + playerIV.getLayoutX() + ", " + playerIV.getLayoutY());

        System.out.println("Init load took " + (System.currentTimeMillis() - start) + "ms");

        return new Scene(overworldLayout, screenWidth, screenHeight);
    }


    private void drawEntities(Party player, ArrayList<Party> parties) {

        /*
            Draws all entities within FOV of player
         */

        int currX = player.getTileX();
        int currY = player.getTileY();

        if (playerIV != null)
            overworldLayout.getChildren().remove(playerIV);
        else {
            playerIV = new ImageView(genPartyImage());
            playerIV.relocate(screenWidth / 2 - 3, screenHeight / 2 - 3);
        }
        overworldLayout.getChildren().add(playerIV);

        /*System.out.println("Player coords " + playerIV.getLayoutX() + ", " + playerIV.getLayoutY());
        System.out.println("LTopCorner coords 0, 0");
        System.out.println("LBottomCorner coords 0, " + screenHeight);
        System.out.println("RTopCorner coords " + screenWidth + ", 0");
        System.out.println("RTopCorner coords " + screenWidth + ", " + screenHeight);*/

        for (int x = 0; x < parties.size(); x++) {
            Party p = parties.get(x);
            ImageView iv = pIVHashMap.get(p);
            if (iv != null)
                overworldLayout.getChildren().remove(iv);
            if (Math.abs(p.getTileX() - currX) < zoom && Math.abs(p.getTileY() - currY) < zoom) {
                pIVHashMap.put(p, iv = new ImageView(genPartyImage()));
                if (iv == null) {
                    System.out.println("ImageView @ " + x + " is null");
                    System.out.println("Party @ coords " + p.getTileX() + ", " + p.getTileY());
                    continue;
                }
                iv.relocate(playerIV.getLayoutX() + (p.getTileX() - player.getTileX()) * (mapTileSize / 2) + p.getxOffset(), playerIV.getLayoutY() + ((p.getTileY() - player.getTileY()) * (mapTileSize / 4) + p.getyOffset()));
                System.out.println(iv.getLayoutX() + ", " + iv.getLayoutY());
                setMoveAnim(p, player);
                //setMoveAnim(pIVHashMap.get(p));
            }
            if (pIVHashMap.get(p) != null && Math.abs(p.getTileX() - currX) < zoom && Math.abs(p.getTileY() - currY) < zoom) {
                overworldLayout.getChildren().add(iv); // add again if in FOV
                System.out.println("Added entity to layout @ coords " + p.getTileX() + ", " + p.getTileY());
            } else if (pIVHashMap.get(p) != null && Math.abs(p.getTileX() - currX) > zoom && Math.abs(p.getTileY() - currY) > zoom) {
                pIVHashMap.remove(p); // removes imageview from party (reduces mem usage)
                System.out.println("Removed entity from layout @ coords " + p.getTileX() + ", " + p.getTileY());
            }
        }
    }

    private Image genPartyImage() {

        /*
            Generates an ImageView to represent a party
         */

        return new Image("/media/graphics/redDot.png", 6, 6, false, false);
    }

    private void genBanner(Tile tile, double parentX, double parentY, float width, float height) {

        /*
            Generates the banner graphical element to overlay over each settlement
         */

        tile.banner = new Pane();

        double boxHeight = height / 1.5;

        Rectangle box = new Rectangle((double) width, boxHeight, Paint.valueOf("white"));

        ImageView bannerL = new ImageView(images.banner);
        ImageView bannerR = new ImageView(images.banner);
        ImageView attack = new ImageView(images.attack);
        ImageView enter = new ImageView(images.enter);
        ImageView diplomacy = new ImageView(images.diplomacy);
        Text name = new Text(tile.settlementTile.settlementName);
        name.setFont(new Font(10));
        Text relationship = new Text((tile.settlementTile.relationship >= 0 ? "+" : "-") + tile.settlementTile.relationship);
        relationship.setFont(new Font(10));

        bannerL.relocate(0, boxHeight / 3);
        bannerR.relocate((double) width - (double) width / 3, boxHeight / 3);
        name.relocate((double) width / 2 - calcStringWidth(tile.settlementTile.settlementName) / 2, boxHeight / 3);
        relationship.relocate((double) width / 2 - calcStringWidth(" " + tile.settlementTile.relationship), 0);

        tile.banner.getChildren().addAll(box, bannerL, bannerR, name, relationship);
        tile.banner.setVisible(false);

        tile.banner.setLayoutX((parentX - (mapTileSize / 2)) - (width / 2));
        tile.banner.setLayoutY((parentY - (mapTileSize / 4)) - (height / 2));
    }

    private Image genTile(int xPos, int yPos, Tile[][] tiles) {

        /*
            Generates the imageview with the image from Image class based on the data from the model
         */

        String type = tiles[xPos][yPos].type;

        if (type.equalsIgnoreCase("Settlement")) {
            if (tiles[xPos][yPos].settlementTile.subType.equalsIgnoreCase("Village"))
                return images.village;
        } else if (type.equalsIgnoreCase("InMap")) {
            if (tiles[xPos][yPos].inMapTile.inmapType.equalsIgnoreCase("Tower"))
                return images.tower;
        } else if (type.equalsIgnoreCase("ForestLight"))
            return images.forestLight;
        else if (type.equalsIgnoreCase("ForestHeavy"))
            return images.forestHeavy;
        else if (type.equalsIgnoreCase("Grass"))
            return images.grass;
        else if (type.equalsIgnoreCase("Mountain"))
            return images.mountain;
        else if (type.equalsIgnoreCase("WaterAll"))
            return images.waterAll;
        else if (type.equalsIgnoreCase("WaterE"))
            return images.waterE;
        else if (type.equalsIgnoreCase("WaterN"))
            return images.waterN;
        else if (type.equalsIgnoreCase("WaterNE"))
            return images.waterNE;
        else if (type.equalsIgnoreCase("WaterNESE"))
            return images.waterNESE;
        else if (type.equalsIgnoreCase("WaterNW"))
            return images.waterNW;
        else if (type.equalsIgnoreCase("WaterNWNE"))
            return images.waterNWNE;
        else if (type.equalsIgnoreCase("WaterNWSW"))
            return images.waterNWSW;
        else if (type.equalsIgnoreCase("WaterS"))
            return images.waterS;
        else if (type.equalsIgnoreCase("WaterSE"))
            return images.waterSE;
        else if (type.equalsIgnoreCase("WaterSW"))
            return images.waterSW;
        else if (type.equalsIgnoreCase("WaterSWSE"))
            return images.waterSWSE;
        else if (type.equalsIgnoreCase("WaterW"))
            return images.waterW;

        return null;
    }

    private void drawTile(Tile[][] tiles, Party player, ArrayList<Party> parties, float bannerSize, int imgX, int imgY, int xPos, int yPos, double pixelX, double pixelY) {

        /*
            Draws Image Views and tiles to screen for init load only
         */

        // add tiles

        System.out.print(".");
        imageViews[imgX][imgY][0] = new ImageView(genTile(xPos, yPos, tiles));
        imageViews[imgX][imgY][0].setLayoutX(pixelX);
        imageViews[imgX][imgY][0].setLayoutY(pixelY);
        setMoveAnim(imageViews[imgX][imgY][0], player);
        overworldLayout.getChildren().add(imageViews[imgX][imgY][0]);

        // add settlement banners

        if (tiles[xPos][yPos].type.equalsIgnoreCase("Settlement")) {
            genBanner(tiles[xPos][yPos], imageViews[imgX][imgY][0].getLayoutX(), imageViews[imgX][imgY][0].getLayoutY(), bannerSize, bannerSize / 2);
            tiles[xPos][yPos].banner.setLayoutX(pixelX); // fix positioning so is in center of tile...
            tiles[xPos][yPos].banner.setLayoutY(pixelY);
            setMoveAnim(tiles[xPos][yPos].banner, player);
            overworldLayout.getChildren().add(tiles[xPos][yPos].banner);
        }

        // add tile borders

        imageViews[imgX][imgY][1] = new ImageView(images.tileBorder);
        imageViews[imgX][imgY][1].setLayoutX(pixelX);
        imageViews[imgX][imgY][1].setLayoutY(pixelY);
        imageViews[imgX][imgY][1].setVisible(false);
        setMoveAnim(imageViews[imgX][imgY][1], player);
        overworldLayout.getChildren().add(imageViews[imgX][imgY][1]);
    }

    void addRow(Tile[][] tiles, Party player, ArrayList<Party> parties, int mapSize, boolean top) {

        /*
            Adds a row when a tile change on the y axis is detected
         */

        long start = System.currentTimeMillis();

        if ((top && player.getTileY() + (zoom) + 1 < mapSize) || (!top && player.getTileY() + (zoom) - 1 > 0)) {
            for (int x = 0; x < zoom * 2 + 1; x++) {
                if (!top)
                    for (int y = 0; y < zoom * 2; y++) // move all images down
                        imageViews[x][y][0].setImage(imageViews[x][y + 1][0].getImage());
                else
                    for (int y = zoom * 2; y > 0; y--)
                        imageViews[x][y][0].setImage(imageViews[x][y - 1][0].getImage());

                int yPos = player.getTileY() + (!top ? -zoom : zoom);
                int xPos = (player.getTileX() + x < 0 ? (player.getTileX() + x >= mapSize ? mapSize - 1 : 0) : player.getTileX() + x);

                imageViews[x][!top ? 0 : zoom * 2 - 1][0].setImage(genTile(xPos, yPos, tiles));
            }
            moveImageViews(top ? mapTileSize / 2 : -mapTileSize / 2, top ? -mapTileSize / 4 : mapTileSize / 4);
            drawEntities(player, parties);
        }

        System.out.println("Adding row took: " + (System.currentTimeMillis() - start) + "ms");
    }

    void addColumn(Tile[][] tiles, Party player, ArrayList<Party> parties, int mapSize, boolean right) {

        /*
            Adds a column when a tile change on the x axis is detected
         */

        System.out.println("Adding column");
        if ((right && player.getTileY() + (zoom) + 1 < mapSize) || (!right && player.getTileY() + (zoom) - 1 > 0)) {
            for (int y = 0; y < zoom * 2 + 1; y++) {
                if (right)
                    for (int x = 0; x < zoom * 2; x++)
                        imageViews[x][y][0].setImage(imageViews[x + 1][y][0].getImage());
                else
                    for (int x = zoom * 2; x > 0; x--)
                        imageViews[x][y][0].setImage(imageViews[x - 1][y][0].getImage());

                int xPos = player.getTileX() + (right ? -zoom : zoom);
                int yPos = (player.getTileY() + y < 0 ? (player.getTileY() + y >= mapSize ? mapSize - 1 : 0) : player.getTileY() + y);

                imageViews[right ? 0 : zoom * 2 - 1][y][0].setImage(genTile(xPos, yPos, tiles));
            }
            moveImageViews(right ? mapTileSize / 2 : -mapTileSize / 2, right ? mapTileSize / 4 : -mapTileSize / 4);
            drawEntities(player, parties);
        }
    }

    private void setMoveAnim(ImageView imageView, Party player) {

        /*
            Sets the movement animation of a tile (imageview)
         */

        new AnimationTimer() {

            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    if (imageView == null) {
                        stop();
                        return;
                    }

                    if (player.getPath() == null) {
                        imageView.setLayoutX(imageView.getLayoutX() + speedX.get());
                        imageView.setLayoutY(imageView.getLayoutY() + speedY.get());
                    } else if ((player.getStart() != null) && (player.getPixelStartPos() != null)) {
                        if ((Math.abs(player.getTileX() - player.getStart().getX()) == 1 || Math.abs(player.getTileY() - player.getStart().getY()) == 1)
                                && (int) getPlayerXOffset() == (int) player.getPixelStartPos().getX() && (int) getPlayerYOffset() == (int) player.getPixelStartPos().getY()) {
                            player.setPixelStartPos((int) getPlayerXOffset(), (int) (getPlayerYOffset()));
                            player.setStart(player.getTileX(), player.getTileY());
                            player.setDir(player.getPath().next());
                            System.out.println("Changed dir aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                        } else {
                            imageView.setLayoutX(imageView.getLayoutX() + player.getSpeedX(player.getDir()));
                            imageView.setLayoutY(imageView.getLayoutY() + player.getSpeedY(player.getDir()));
                        }
                    }
                }
                lastUpdateTime.set(timestamp);
            }
        }.start();
    }

    private void setMoveAnim(Party p, Party player) {

        /*
            Sets movement animation for a party (imageview)
         */

        new AnimationTimer() {

            final double startX = pIVHashMap.get(p).getLayoutX();
            final double startY = pIVHashMap.get(p).getLayoutY();

            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    if (pIVHashMap.get(p) == null) {
                        stop();
                        return;
                    }
                    pIVHashMap.get(p).relocate((float) (playerIV.getLayoutX() + ((p.getTileX() - player.getTileX()) * (mapTileSize / 2) + (p.getTileY() - player.getTileY()) * (-mapTileSize / 2) + p.getxOffset())) - speedX.get(),
                            (float) (playerIV.getLayoutY() - ((p.getTileY() - player.getTileY()) * (-mapTileSize / 4) + (p.getTileX() - player.getTileX()) * (-mapTileSize / 4) + p.getyOffset())) - speedY.get());
                }
                lastUpdateTime.set(timestamp);
            }
        }.start();
    }

    private void setMoveAnim(Pane pane, Party player) {

        /*
            Sets the movement animation for a Pane object
         */

        new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    if (pane == null) { // if redundant delete if statement
                        stop();
                        return;
                    }
                    pane.setLayoutX(pane.getLayoutX() + speedX.get());
                    pane.setLayoutY(pane.getLayoutY() + speedY.get());
                }
                lastUpdateTime.set(timestamp);
            }
        }.start();
    }

    private void moveImageViews(double xOffset, double yOffset) {
        for (int y = 0; y <= zoom * 2; y++)
            for (int x = 0; x <= zoom * 2; x++) {
                imageViews[x][y][0].setLayoutX(imageViews[x][y][0].getLayoutX() + xOffset);
                imageViews[x][y][0].setLayoutY(imageViews[x][y][0].getLayoutY() + yOffset);
                imageViews[x][y][1].setLayoutX(imageViews[x][y][1].getLayoutX() + xOffset);
                imageViews[x][y][1].setLayoutY(imageViews[x][y][1].getLayoutY() + yOffset);
            }
    }

    void showSettlementInfo(SettlementTile tile) {

        /*
            Shows the information of a settlement tile. Click event handled by controller
         */

        infoBox = new Pane();

        paneStack.push(infoBox);

        double boxWidth = screenWidth / 3;
        double boxHeight = screenHeight / 5;

        Rectangle box = new Rectangle(boxWidth, boxHeight, Paint.valueOf("white"));
        box.relocate((screenWidth / 2) - (boxWidth / 2), screenHeight / 2 - (boxHeight / 2));

        infoBox.getChildren().addAll(
                box,
                createButton(manageCity, (screenWidth / 2) - boxWidth / 2 + padding, screenHeight / 2 + boxHeight / 2 - 50),
                //createButton(diplomacy, screenWidth / 2 - calcStringWidth("Diplomacy") / 2, screenHeight / 2 + boxHeight / 2 - 50, "Diplomacy"),
                //createButton(trade, screenWidth / 2 + boxWidth / 2 - padding - calcStringWidth("Trade"), screenHeight / 2 + boxHeight / 2 - 50, "Trade"),
                createText(screenWidth / 2 - calcStringWidth(tile.settlementName) / 2, screenHeight / 2 - (boxHeight / 2) + 20, tile.settlementName, 24),
                createText(screenWidth / 2 - calcStringWidth(tile.subType) / 2, screenHeight / 2 - (boxHeight / 2) + 40, tile.subType, 16),
                createText(screenWidth / 2 - boxWidth / 2 + padding, screenHeight / 2 - (boxHeight / 2) + 55, "Type: " + (tile.branch.equals("m") ? "Military" : "Commercial"), 12),
                createText(screenWidth / 2 - boxWidth / 2 + padding, screenHeight / 2 - (boxHeight / 2) + 70, "Relationship: " + tile.relationship, 12)
        );
        overworldLayout.getChildren().add(infoBox);
    }

    // TODO: FUNCTION THAT DETERMINES POSITION TO PLACE ELEMENT IN THE INFO BOXES

    void showInMapInfo(String name, String difficulty) {

        /*
            Shows the information of a settlement tile. Click event handled by controller
            TODO: SHOULD PASS THE INMAPTILE OBJECT?
         */

        infoBox = new Pane();

        paneStack.push(infoBox);

        double boxWidth = screenWidth / 3;
        double boxHeight = screenHeight / 5;

        Rectangle box = new Rectangle(boxWidth, boxHeight, Paint.valueOf("white"));
        box.relocate((screenWidth / 2) - (boxWidth / 2), screenHeight / 2 - (boxHeight / 2));

        infoBox.getChildren().addAll(
                box,
                createButton(enterDungeon, (screenWidth / 2) - (calcStringWidth(enterDungeon.getText())), (screenHeight / 2)),
                createText((screenWidth / 2) - calcStringWidth(name), (screenHeight / 2) - (boxHeight / 2) + 20, name, 24),
                createText((screenWidth / 2) - calcStringWidth(difficulty), (screenHeight / 2) - (boxHeight / 2) + 40, difficulty, 16)
        );
        overworldLayout.getChildren().add(infoBox);
    }

    private Text createText(double x, double y, String string, int font) { // font size

        /*
            Creates and returns a text object at coords x, y with text string and font font
         */

        Text text = new Text(x, y, string);
        text.setFont(new Font(font));

        return text;
    }

    private Button createButton(Button button, double x, double y) {

        /*
            Creates and returns a button object at coords x, y and with text string
         */

        button.setPadding(Insets.EMPTY);
        button.relocate(x, y);
        return button;
    }

    private double calcStringWidth(String string) {

        /*
            Calculates and returns the width of string (graphical element)
         */

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        java.awt.FontMetrics fm = img.getGraphics().getFontMetrics();
        return fm.stringWidth(string); // for variable font size
    }

    void removePane(Pane pane) {

        /*
            Removes a pane (window) from the GUI
         */

        overworldLayout.getChildren().remove(pane);
    }

    void showCityManagement(SettlementTile tile) {

        /*
            Creates and displays the city management window
         */

        Pane managmentBox = new Pane();

        paneStack.push(managmentBox);

        double boxWidth = screenWidth / 2.5;
        double boxHeight = screenHeight / 2.5;

        final double BOXLEFTBOUND = screenWidth / 2 - boxWidth / 2 + padding;
        final double BOXRIGHTBOUND = screenWidth / 2 + boxWidth / 2 - padding;
        final double BOXTOPBOUND = screenHeight / 2 - boxHeight / 2 + padding;
        final double BOXBOTTOMBOUND = screenHeight / 2 + boxHeight / 2 - padding;


        Rectangle box = new Rectangle(boxWidth, boxHeight, Paint.valueOf("white"));
        box.relocate((screenWidth / 2) - (boxWidth / 2), screenHeight / 2 - (boxHeight / 2));

        managmentBox.getChildren().addAll(
                box,
                createText(screenWidth / 2 - calcStringWidth("City Managment") / 2, BOXTOPBOUND + 10, "City Management", 20),

                createText(BOXLEFTBOUND, BOXTOPBOUND + 50, "Economy", 16),
                createText(BOXLEFTBOUND, BOXTOPBOUND + 70, "Capital: " + tile.cityPolitics.capital, 12),
                createText(BOXLEFTBOUND, BOXTOPBOUND + 90, "Growth: " + tile.cityPolitics.growth, 12),
                createText(BOXLEFTBOUND, BOXTOPBOUND + 110, "Population: " + tile.cityPolitics.population, 12),

                createText(screenWidth / 2 - calcStringWidth("Politics") / 2, BOXTOPBOUND + 50, "Politics", 16),
                createText(screenWidth / 2 - calcStringWidth("Average Happiness: ") / 2, BOXTOPBOUND + 70, "Average Happiness: " + tile.cityPolitics.avgHappiness, 12)
        );

        overworldLayout.getChildren().add(managmentBox);
    }
}
