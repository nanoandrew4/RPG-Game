package overworld;

import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
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
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.omg.CORBA.BAD_CONTEXT;

import java.awt.image.BufferedImage;
import java.time.Year;
import java.util.*;

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

        banner = new Image("/media/graphics/overworld/banner/banner.png", width / 1.2, height / 4, false, false);

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


class OverworldView {

    /*
        Displays all graphical elements of the overworld with data from the model provided by the controller
        177.69230508522173 - mapTileSize on desktop monitor
     */

    private static float zoomMultiplier = 2.2f;

    static int tilesAcrossScreen = 6; // tiles to be fit across screen in vertical or horizontal direction
    static int zoom = (int) (tilesAcrossScreen * zoomMultiplier);
    private double screenWidth, screenHeight;
    static double mapTileSize;

    private final LongProperty lastUpdateTime = new SimpleLongProperty();

    private Images images;
    private ImageView[][][] imageViews; // for controller to access and add click events
    private Pane[][] banners;

    private Stack<Pane> paneStack = new Stack<>(); // makes returning to previous window easier
    private Pane overworldLayout, infoBox;
    private ImageView centerTile, playerIV;

    private HashMap<Party, ImageView> pIVHashMap; // when party is eliminated, delete from hashmap

    OverworldView(double screenWidth, double screenHeight) {
        imageViews = new ImageView[zoom * 2 + 1][zoom * 2 + 1][2];
        banners = new Pane[zoom * 2 + 1][zoom * 2 + 1];
        pIVHashMap = new HashMap<>();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        if (screenWidth > screenHeight) {
            mapTileSize = screenHeight / tilesAcrossScreen;
        } else {
            mapTileSize = screenWidth / tilesAcrossScreen;
        }
    }

    // returns the size of the tile (width = height) in pixels
    double getMapTileSize() {
        return mapTileSize;
    }

    // returns coordinate of player omn tile on the x axis
    float getPlayerXOffset() {
        return (float) (centerTile.screenToLocal(playerIV.localToScreen(3, 3)).getX() - getMapTileSize() / 2);
    }

    // returns coordinate of player on tile on the y axis
    float getPlayerYOffset() {
        return (float) (-centerTile.screenToLocal(playerIV.localToScreen(3, 3)).getY() + getMapTileSize() * 3 / 4);
    }

    Scene initDisplay(Tile[][] tiles, Party player, ArrayList<Party> parties, double screenWidth, double screenHeight, double mapTileSize, int mapSize) {

        /*
            Loads the initial set of tiles for display based on current position
         */

        System.out.println("Loading images");

        //images = new Images(mapTileSize + (mapTileSize / (zoom * 4)), mapTileSize + (mapTileSize / (zoom * 4)));
        images = new Images(mapTileSize, mapTileSize);

        long start = System.currentTimeMillis();

        overworldLayout = new Pane();

        // draw all tiles
        for (int y = -zoom; y <= zoom; y++) {
            for (int x = -zoom; x <= zoom; x++) {
                int xPos = (player.getTileX() + x < 0 ? (player.getTileX() + x >= mapSize ? mapSize - 1 : 0) : player.getTileX() + x);
                int yPos = (player.getTileY() + y < 0 ? (player.getTileY() + y >= mapSize ? mapSize - 1 : 0) : player.getTileY() + y);
                drawTile(tiles, player, x + zoom, y + zoom, xPos, yPos,
                        0.5 * mapTileSize * (x - y) + (screenWidth / 2) - mapTileSize / 2,
                        0.25 * mapTileSize * (x + y) + (screenHeight / 2) - mapTileSize * 3 / 4
                );
            }
        }

        System.out.println();

        // set center image for controller to be able to set offset of player from center of tile
        centerTile = imageViews[zoom][zoom][0];

        imageViews[zoom][zoom][1].setVisible(true);

        // create and draw player image view
        playerIV = new ImageView(genPartyImage());
        playerIV.relocate(screenWidth / 2 - 3, screenHeight / 2 - 3);
        overworldLayout.getChildren().add(playerIV);

        // draw all entities that are within visible range
        drawEntities(player, parties);

        // draw banners for settlements (must be on top of players)
        drawBanners();

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

        /*System.out.println("Player coords " + playerIV.getLayoutX() + ", " + playerIV.getLayoutY());
        System.out.println("LTopCorner coords 0, 0");
        System.out.println("LBottomCorner coords 0, " + screenHeight);
        System.out.println("RTopCorner coords " + screenWidth + ", 0");
        System.out.println("RTopCorner coords " + screenWidth + ", " + screenHeight);*/

        for (Party p : parties) {
            ImageView iv;

            // if player is within FOV and no image view exists, create and draw, if goes out of FOV, remove
            if (pIVHashMap.get(p) == null && Math.abs(p.getTileX() - currX) < zoom && Math.abs(p.getTileY() - currY) < zoom) {
                pIVHashMap.put(p, iv = new ImageView(genPartyImage()));
                iv.relocate(playerIV.getLayoutX() + (p.getTileX() - player.getTileX()) * (mapTileSize / 2) + p.getxOffset(), playerIV.getLayoutY() + ((p.getTileY() - player.getTileY()) * (mapTileSize / 4) + p.getyOffset()));
                System.out.println(iv.getLayoutX() + ", " + iv.getLayoutY());
                setMoveAnim(p, player);
                //setMoveAnim(pIVHashMap.get(p));
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

    private void drawTile(Tile[][] tiles, Party player, int imgX, int imgY, int xPos, int yPos, double pixelX, double pixelY) {

        /*
            Draws Image Views and tiles to screen for init load only
         */

        // add tile

        imageViews[imgX][imgY][0] = new ImageView(genTile(xPos, yPos, tiles));
        imageViews[imgX][imgY][0].setLayoutX(pixelX);
        imageViews[imgX][imgY][0].setLayoutY(pixelY);
        overworldLayout.getChildren().add(imageViews[imgX][imgY][0]);
        setMoveAnim(imageViews[imgX][imgY][0], player);

        // add tile border

        imageViews[imgX][imgY][1] = new ImageView(images.tileBorder);
        imageViews[imgX][imgY][1].setLayoutX(pixelX);
        imageViews[imgX][imgY][1].setLayoutY(pixelY);
        imageViews[imgX][imgY][1].setVisible(false);
        overworldLayout.getChildren().add(imageViews[imgX][imgY][1]);

        setMoveAnim(imageViews[imgX][imgY][1], player);

        // add settlement banner

        if (tiles[xPos][yPos].type.equalsIgnoreCase("Settlement")) {
            genBanner(tiles[xPos][yPos], imgX, imgY, imageViews[imgX][imgY][0].getLayoutX(), imageViews[imgX][imgY][0].getLayoutY(),
                    (float)images.banner.getWidth(), (float)images.banner.getHeight());
            //tiles[xPos][yPos].banner.setLayoutX(pixelX); // fix positioning so is in center of tile...
            //tiles[xPos][yPos].banner.setLayoutY(pixelY);
            // added later (after players)
            setMoveAnim(banners[imgX][imgY], player);
        }
    }
    
    // rename pls
    void reDraw (double[] angles, Tile[][] tiles, Party player, ArrayList<Party> parties) {
        if (angles[0] > 22.5)
            addColumn(tiles, player, parties, false);
        if (angles[0] < -22.5)
            addRow(tiles, player, parties, false);
        if (angles[1] > 22.5)
            addRow(tiles, player, parties, true);
        if (angles[1] < -22.5)
            addColumn(tiles, player, parties, true);
        if (Math.abs(getPlayerXOffset()) > getMapTileSize() / 2) {
            if (getPlayerXOffset() > 0) {
                addColumn(tiles, player, parties, true);
                addRow(tiles, player, parties, true);
            } else {
                addColumn(tiles, player, parties, false);
                addRow(tiles, player, parties, false);
            }
        }
    }

    private void addRow(Tile[][] tiles, Party player, ArrayList<Party> parties, boolean top) {

        /*
            Adds a row when a tile change on the y axis is detected
         */

        if ((top && player.getTileY() + (zoom) + 1 < 1000) || (!top && player.getTileY() + (zoom) - 1 > 0)) {
            for (int x = 0; x < zoom * 2 + 1; x++) {
                if (!top)
                    for (int y = 0; y < zoom * 2; y++) // move all images down
                        imageViews[x][y][0].setImage(imageViews[x][y + 1][0].getImage());
                else
                    for (int y = zoom * 2; y > 0; y--)
                        imageViews[x][y][0].setImage(imageViews[x][y - 1][0].getImage());

                int yPos = player.getTileY() + (!top ? -zoom : zoom);
                int xPos = (player.getTileX() + x < 0 ? (player.getTileX() + x >= 1000 ? 1000 - 1 : 0) : player.getTileX() + x);

                imageViews[x][!top ? 0 : zoom * 2 - 1][0].setImage(genTile(xPos, yPos, tiles));
            }
            moveSceneElements(top ? mapTileSize / 2 : -mapTileSize / 2, top ? -mapTileSize / 4 : mapTileSize / 4);
            drawEntities(player, parties);
        }
    }

    private void addColumn(Tile[][] tiles, Party player, ArrayList<Party> parties, boolean right) {

        /*
            Adds a column when a tile change on the x axis is detected
         */

        //System.out.println("Adding column");
        if ((right && player.getTileY() + (zoom) + 1 < 1000) || (!right && player.getTileY() + (zoom) - 1 > 0)) {
            for (int y = 0; y < zoom * 2 + 1; y++) {
                if (right)
                    for (int x = 0; x < zoom * 2; x++)
                        imageViews[x][y][0].setImage(imageViews[x + 1][y][0].getImage());
                else
                    for (int x = zoom * 2; x > 0; x--)
                        imageViews[x][y][0].setImage(imageViews[x - 1][y][0].getImage());

                int xPos = player.getTileX() + (right ? -zoom : zoom);
                int yPos = (player.getTileY() + y < 0 ? (player.getTileY() + y >= 1000 ? 1000 - 1 : 0) : player.getTileY() + y);

                imageViews[right ? 0 : zoom * 2 - 1][y][0].setImage(genTile(xPos, yPos, tiles));
            }
            moveSceneElements(right ? mapTileSize / 2 : -mapTileSize / 2, right ? mapTileSize / 4 : -mapTileSize / 4);
            drawEntities(player, parties);
        }
    }

    ImageView getTileIV(int arrX, int arrY) {
        return imageViews[arrX][arrY][0];
    }

    void showTileBorders(boolean show) {
        for (int y = -OverworldView.zoom; y < OverworldView.zoom; y++) {
            for (int x = -OverworldView.zoom; x < OverworldView.zoom; x++) {
                if (imageViews[x + OverworldView.zoom][y + OverworldView.zoom][1] == null)
                    continue;
                imageViews[x + OverworldView.zoom][y + OverworldView.zoom][1].setVisible(show);
            }
        }
    }

    private void genBanner(Tile tile, int parentImgX, int parentImgY, double parentPixelX, double parentPixelY, float width, float height) {

        /*
            Generates the banner graphical element to overlay over each settlement
         */

        banners[parentImgX][parentImgY] = new Pane();

        ImageView banner = new ImageView(images.banner);

        Text name = new Text(tile.settlementTile.settlementName);
        name.setFont(Font.font("Luminari", FontWeight.NORMAL, 14));

        banner.relocate(0, height / 3);
        name.relocate((double) (width / 2) - (calcStringWidth(tile.settlementTile.settlementName) / 2), banner.getLayoutY() + height / 3);

        banners[parentImgX][parentImgY].getChildren().addAll(banner, name);
        banners[parentImgX][parentImgY].setVisible(false);

        banners[parentImgX][parentImgY].setLayoutX((parentPixelX + (mapTileSize / 2) - (width / 2)));
        banners[parentImgX][parentImgY].setLayoutY((parentPixelY + (mapTileSize / 2) + (height / 8)));
    }

    private void drawBanners() {
        for (int y = 0; y <= zoom * 2; y++) {
            for (int x = 0; x <= zoom * 2; x++) {
                if (banners[x][y] != null)
                    overworldLayout.getChildren().add(banners[x][y]);
            }
        }
    }

    Pane getBanner(int arrX, int arrY) {
        return banners[arrX][arrY];
    }

    void showBanner(int xPos, int yPos, boolean show) {
        banners[xPos][yPos].setVisible(show);
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

                    imageView.setLayoutX(imageView.getLayoutX() - player.getSpeedX());
                    imageView.setLayoutY(imageView.getLayoutY() + player.getSpeedY());
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

            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    if (pIVHashMap.get(p) == null) {
                        stop();
                        return;
                    }
                    pIVHashMap.get(p).relocate((float) (playerIV.getLayoutX() + ((p.getTileX() - player.getTileX()) * (mapTileSize / 2) + (p.getTileY() - player.getTileY()) * (-mapTileSize / 2) + p.getxOffset())) - player.getSpeedX(),
                            (float) (playerIV.getLayoutY() - ((p.getTileY() - player.getTileY()) * (-mapTileSize / 4) + (p.getTileX() - player.getTileX()) * (-mapTileSize / 4) + p.getyOffset())) - player.getSpeedY());
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
                    if (pane == null) {
                        stop();
                        return;
                    }
                    pane.setLayoutX(pane.getLayoutX() - player.getSpeedX());
                    pane.setLayoutY(pane.getLayoutY() + player.getSpeedY());
                }
                lastUpdateTime.set(timestamp);
            }
        }.start();
    }

    private void moveSceneElements(double xOffset, double yOffset) {

        // could also just cycle through elements in overworld layout

        for (int y = 0; y <= zoom * 2; y++)
            for (int x = 0; x <= zoom * 2; x++) {
                imageViews[x][y][0].setLayoutX(imageViews[x][y][0].getLayoutX() + xOffset);
                imageViews[x][y][0].setLayoutY(imageViews[x][y][0].getLayoutY() + yOffset);
                imageViews[x][y][1].setLayoutX(imageViews[x][y][1].getLayoutX() + xOffset);
                imageViews[x][y][1].setLayoutY(imageViews[x][y][1].getLayoutY() + yOffset);
                //banners[x][y].setLayoutX(banners[x][y].getLayoutX() + xOffset);
                //banners[x][y].setLayoutY(banners[x][y].getLayoutY() + yOffset);
            }
    }

    private double calcStringWidth(String string) {

        /*
            Calculates and returns the width of string (graphical element)
         */

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        java.awt.FontMetrics fm = img.getGraphics().getFontMetrics();
        return fm.stringWidth(string); // for variable font size
    }

    void addPane (Pane p) {

        /*
            Adds pane to GUI
         */

        overworldLayout.getChildren().add(p);
    }

    boolean removePane() {

        /*
            Removes a pane (window) from the GUI
         */

        if (!paneStack.empty()) {
            overworldLayout.getChildren().remove(paneStack.pop());
            return true;
        } else
            return false;
    }

    void showSettlementInfo(SettlementTile tile, int arrX, int arrY) {

        /*
            Shows the information of a settlement tile. Click event handled by controller
         */

        banners[arrX][arrY].setVisible(false);

        double limit = screenWidth / 1.2;
        double xIncrement = images.banner.getWidth();
        double yIncrement = images.banner.getHeight();

        // creates animation that makes the banner bigger to fit more info
        ScaleAnimation anim = new ScaleAnimation(xIncrement, yIncrement, images.banner,
                banners[arrX][arrY].getLayoutX(), banners[arrX][arrY].getLayoutY(), limit);

        // add animated imageView to scene, no frames from animation seem to be missed by adding after
        overworldLayout.getChildren().add(anim.getImageView());

        // necessary to calculate top left coordinates of image
        double finalWidth = images.banner.getWidth();
        double finalHeight = images.banner.getHeight();

        while (finalWidth < limit) {
            finalWidth += xIncrement;
            finalHeight += yIncrement;
        }

        double topLeftX = (screenWidth - finalWidth) / 2;
        double topLeftY = (screenHeight - finalHeight) / 2; // needs fixing

        System.out.println();

        infoBox = new Pane();

        paneStack.push(infoBox);

        infoBox.getChildren().addAll(
                anim.getImageView(),
                createText(topLeftY + (finalHeight / 5.5), tile.settlementName, Font.font("Luminari", FontWeight.BOLD, 24), screenWidth),
                createText(topLeftY + (finalHeight / 3.5), tile.subType, Font.font("Luminari", FontWeight.NORMAL, 18), screenWidth),
                createText(topLeftY + (finalHeight / 2.5),
                        "This settlement is a " + tile.subType.toLowerCase() +
                        " that currently has a populace count of " + tile.population + "\n and has " + tile.resources[0] + " wood, " +
                        tile.resources[1] + " stone, " + tile.resources[2] + " iron and " + tile.resources[3] + " gold in it's warehouse/s",
                        Font.font("Luminari", FontWeight.NORMAL, 16), screenWidth)
        );
        overworldLayout.getChildren().add(infoBox);
    }

    void showInMapInfo(String name, String difficulty) {

        /*
            Shows the information of a settlement tile. Click event handled by controller
         */

        String enterInfo = "Press enter to explore this dungeon";

        infoBox = new Pane();

        paneStack.push(infoBox);

        double boxWidth = screenWidth / 3;
        double boxHeight = screenHeight / 5;

        Rectangle box = new Rectangle(boxWidth, boxHeight, Paint.valueOf("white"));
        box.relocate((screenWidth / 2) - (boxWidth / 2), screenHeight / 2 - (boxHeight / 2));

        infoBox.getChildren().addAll(
                box,
                createText((screenWidth / 2) - calcStringWidth(name), (screenHeight / 2) - (boxHeight / 2) + 20, name, new Font(24)),
                createText((screenWidth / 2) - calcStringWidth(difficulty), (screenHeight / 2) - (boxHeight / 2) + 40, difficulty, new Font(16)),
                createText(screenWidth / 2 - calcStringWidth(enterInfo), screenHeight / 2 - (boxHeight / 2) + 80, enterInfo, new Font(12))
        );
        overworldLayout.getChildren().add(infoBox);
    }

    private Text createText(double x, double y, String string, Font font) { // font size

        /*
            Creates and returns a text object at coords x, y with text string and font font
         */

        Text text = new Text(x, y, string);
        text.setFont(font);

        return text;
    }

    private Text createText(double y, String string, Font font, double wrappingWidth) { // font size

        /*
            Creates and returns a text object centered on x axis, at y-axis coord y, with String string, Font font
         */

        Text text = new Text(0, y, string);
        text.setFont(font);
        text.setWrappingWidth(wrappingWidth);
        text.setTextAlignment(TextAlignment.CENTER);

        return text;
    }
}