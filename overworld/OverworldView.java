package overworld;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
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
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.omg.CORBA.BAD_CONTEXT;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Year;
import java.util.*;

class Images {

    /*
        Class contains and loads all images to be used by view in individual imageviews
        TODO: WIDTH AND HEIGHT FOR BANNER ITEMS MUST BE CALCULATED BEFORE IMAGES CLASS IS CREATED
     */

    static Image tileBorder;

    static Image forestLight;
    static Image forestHeavy;
    static Image village;
    static Image tower;
    static Image mountain;
    static Image grass;

    static Image banner;

    static Image minimapFrame;

    static Image waterAll;
    static Image waterE;
    static Image waterN;
    static Image waterNE;
    static Image waterNESE;
    static Image waterNW;
    static Image waterNWNE;
    static Image waterNWSW;
    static Image waterS;
    static Image waterSE;
    static Image waterSW;
    static Image waterSWSE;
    static Image waterW;

    Images(double width, double height, double screenWidth) {
        tileBorder = new Image("/media/graphics/overworld/TileBorder.png", width, height, true, false);

        forestLight = new Image("/media/graphics/overworld/ForestLight.png", width, height, true, false);
        forestHeavy = new Image("/media/graphics/overworld/ForestHeavy.png", width, height, true, false);
        village = new Image("/media/graphics/overworld/Village.png", width, height, true, false);
        tower = new Image("/media/graphics/overworld/Tower.png", width, height, true, false);
        mountain = new Image("/media/graphics/overworld/Mountain.png", width, height, true, false);
        grass = new Image("/media/graphics/overworld/Grass.png", width, height, true, false);

        banner = new Image("/media/graphics/overworld/banner/banner.png", width / 1.2, height / 4, false, false);

        minimapFrame = new Image("/media/graphics/overworld/minimap/minimapFrame.png", screenWidth / 8, screenWidth / 8, true, false);

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
        TODO: FONT SIZES DEPENDANT ON SCREEN SIZE
     */

    private static float zoomMultiplier = 2.2f;

    private static int tilesAcrossScreen = 6; // tiles to be fit across screen in vertical or horizontal direction
    static int zoom = (int) (tilesAcrossScreen * zoomMultiplier);
    private double screenWidth, screenHeight;
    static double mapTileSize;

    private final LongProperty lastUpdateTime = new SimpleLongProperty();

    private ImageView[][][] imageViews; // for controller to access and add click events
    private Pane banner;

    private Minimap minimap;

    private Stack<Node> nodeStack = new Stack<>(); // makes returning to previous window easier
    private Pane overworldLayout, infoBox;
    private ImageView centerTile, playerIV;

    private HashMap<Party, ImageView> pIVHashMap; // when party is eliminated, delete from hashmap

    OverworldView(double screenWidth, double screenHeight, ImageView playerIV) {
        imageViews = new ImageView[zoom * 2 + 1][zoom * 2 + 1][2];
        pIVHashMap = new HashMap<>();

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.playerIV = playerIV;
        if (playerIV != null) {
            playerIV.setFitWidth(48);
            playerIV.setFitHeight(72);
        }

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
        if (centerTile.screenToLocal(24, 72) == null)
            return 0;
        return (float) (centerTile.screenToLocal(playerIV.localToScreen(24, 72)).getX() - getMapTileSize() / 2);
    }

    // returns coordinate of player on tile on the y axis
    float getPlayerYOffset() {
        if (centerTile.screenToLocal(24, 72) == null)
            return 0;
        return (float) (-centerTile.screenToLocal(playerIV.localToScreen(24, 72)).getY() + getMapTileSize() * 3 / 4);
    }

    Stack<Node> getNodeStack() {
        return nodeStack;
    }

    Pane getMinimapPane() {
        return minimap.getMinimapPane();
    }

    ImageView getMinimapFrame() {
        return minimap.getMinimapFrame();
    }

    boolean isNodeStackEmpty() {
        return nodeStack.empty();
    }

    Scene initDisplay(Tile[][] tiles, Party player, ArrayList<Party> parties, double screenWidth, double screenHeight, double mapTileSize, int mapSize) {

        /*
            Loads the initial set of tiles for display based on current position
         */

        //images = new Images(mapTileSize + (mapTileSize / (zoom * 4)), mapTileSize + (mapTileSize / (zoom * 4)));
        new Images(mapTileSize, mapTileSize, screenWidth);

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
        if (playerIV == null)
            playerIV = genPartyImage();
        playerIV.relocate(screenWidth / 2 - 24, screenHeight / 2 - 72);
        playerIV.setMouseTransparent(true); // might change
        overworldLayout.getChildren().add(playerIV);

        // draw all entities that are within visible range
        drawEntities(player, parties);

        // draw minimap
        minimap = new Minimap(screenWidth, screenHeight, 10);
        overworldLayout.getChildren().add(minimap.getMinimapFrame());
        overworldLayout.getChildren().add(minimap.drawMiniMap(tiles, player));

        if (OverworldController.debug) {
            System.out.println("Center tile " + centerTile.getLayoutX() + ", " + centerTile.getLayoutY());
            System.out.println("Player pos " + playerIV.getLayoutX() + ", " + playerIV.getLayoutY());

            System.out.println("Init load took " + (System.currentTimeMillis() - start) + "ms");

        }
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
                pIVHashMap.put(p, iv = genPartyImage());
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

    private ImageView genPartyImage() {

        /*
            Generates an ImageView to represent a party
         */

        return new ImageView(new Image("/media/graphics/redDot.png", 6, 6, false, false));
    }

    private Image genTile(int xPos, int yPos, Tile[][] tiles) {

        /*
            Generates the imageview with the image from Image class based on the data from the model
         */

        String type = tiles[xPos][yPos].type;

        if (type.equalsIgnoreCase("Settlement")) {
            if (tiles[xPos][yPos].settlementTile.subType.equalsIgnoreCase("Village"))
                return Images.village;
            else
                return Images.village;
        } else if (type.equalsIgnoreCase("InMap")) {
            if (tiles[xPos][yPos].inMapTile.inmapType.equalsIgnoreCase("Tower"))
                return Images.tower;
        } else if (type.equalsIgnoreCase("ForestLight"))
            return Images.forestLight;
        else if (type.equalsIgnoreCase("ForestHeavy"))
            return Images.forestHeavy;
        else if (type.equalsIgnoreCase("Grass"))
            return Images.grass;
        else if (type.equalsIgnoreCase("Mountain"))
            return Images.mountain;
        else if (type.equalsIgnoreCase("WaterAll"))
            return Images.waterAll;
        else if (type.equalsIgnoreCase("WaterE"))
            return Images.waterE;
        else if (type.equalsIgnoreCase("WaterN"))
            return Images.waterN;
        else if (type.equalsIgnoreCase("WaterNE"))
            return Images.waterNE;
        else if (type.equalsIgnoreCase("WaterNESE"))
            return Images.waterNESE;
        else if (type.equalsIgnoreCase("WaterNW"))
            return Images.waterNW;
        else if (type.equalsIgnoreCase("WaterNWNE"))
            return Images.waterNWNE;
        else if (type.equalsIgnoreCase("WaterNWSW"))
            return Images.waterNWSW;
        else if (type.equalsIgnoreCase("WaterS"))
            return Images.waterS;
        else if (type.equalsIgnoreCase("WaterSE"))
            return Images.waterSE;
        else if (type.equalsIgnoreCase("WaterSW"))
            return Images.waterSW;
        else if (type.equalsIgnoreCase("WaterSWSE"))
            return Images.waterSWSE;
        else if (type.equalsIgnoreCase("WaterW"))
            return Images.waterW;

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

        imageViews[imgX][imgY][1] = new ImageView(Images.tileBorder);
        imageViews[imgX][imgY][1].setLayoutX(pixelX);
        imageViews[imgX][imgY][1].setLayoutY(pixelY);
        imageViews[imgX][imgY][1].setVisible(false);
        overworldLayout.getChildren().add(imageViews[imgX][imgY][1]);
        setMoveAnim(imageViews[imgX][imgY][1], player);
    }

    // rename pls
    void reDraw(Point2D angles, Tile[][] tiles, Party player, ArrayList<Party> parties) {
        if (angles.getX() > 30.0)
            addColumn(tiles, player, parties, false);
        if (angles.getX() < -30.0)
            addRow(tiles, player, parties, false);
        if (angles.getY() > 30.0)
            addRow(tiles, player, parties, true);
        if (angles.getY() < -30.0)
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

        minimap.drawMiniMap(tiles, player); // redraws the minimap
    }

    private void addRow(Tile[][] tiles, Party player, ArrayList<Party> parties, boolean top) {

        /*
            Adds a row when a tile change on the y axis is detected
         */

        for (int x = 0; x < zoom * 2 + 1; x++) {
            if (!top)
                for (int y = 0; y < zoom * 2; y++) // move all images down
                    imageViews[x][y][0].setImage(imageViews[x][y + 1][0].getImage());
            else
                for (int y = zoom * 2; y > 0; y--)
                    imageViews[x][y][0].setImage(imageViews[x][y - 1][0].getImage());

            int yPos = player.getTileY() + (top ? -zoom : zoom);
            int xPos = (player.getTileX() + x - zoom > 0 ? (player.getTileX() + x - zoom < 1000 ? player.getTileX() + x - zoom : 999) : 0);

            imageViews[x][top ? 0 : zoom * 2][0].setImage(genTile(xPos, yPos, tiles));
        }
        moveSceneElements(top ? mapTileSize / 2 : -mapTileSize / 2, top ? -mapTileSize / 4 : mapTileSize / 4);
        drawEntities(player, parties);
    }

    private void addColumn(Tile[][] tiles, Party player, ArrayList<Party> parties, boolean right) {

        /*
            Adds a column when a tile change on the x axis is detected
         */

        for (int y = 0; y < zoom * 2 + 1; y++) {
            if (right)
                for (int x = 0; x < zoom * 2; x++)
                    imageViews[x][y][0].setImage(imageViews[x + 1][y][0].getImage());
            else
                for (int x = zoom * 2; x > 0; x--)
                    imageViews[x][y][0].setImage(imageViews[x - 1][y][0].getImage());

            int xPos = player.getTileX() + (!right ? -zoom: zoom);
            int yPos = (player.getTileY() + y - zoom > 0 ? (player.getTileY() + y - zoom < 1000 ? player.getTileY() + y - zoom : 999) : 0);

            imageViews[!right ? 0 : zoom * 2][y][0].setImage(genTile(xPos, yPos, tiles));
        }
        moveSceneElements(right ? mapTileSize / 2 : -mapTileSize / 2, right ? mapTileSize / 4 : -mapTileSize / 4);
        drawEntities(player, parties);
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

    void genBanner(Tile tile, Party player, int arrX, int arrY) {

        /*
            Generates the banner graphical element to overlay over each settlement
         */

        double width = Images.banner.getWidth();
        double height = Images.banner.getHeight();

        banner = new Pane();
        banner.setMouseTransparent(true);

        ImageView bannerIV = new ImageView(Images.banner);

        Text name = new Text(tile.settlementTile.settlementName);
        name.setFont(Font.font("Luminari", FontWeight.NORMAL, (screenWidth / 1680) * 14));

        bannerIV.relocate(0, height / 3);
        name.relocate((width / 2) - name.getBoundsInLocal().getWidth() / 2, bannerIV.getLayoutY() + height / 3);

        banner.getChildren().addAll(bannerIV, name);
        banner.setVisible(true);

        banner.setLayoutX((imageViews[arrX][arrY][0].getLayoutX() + (mapTileSize / 2) - (width / 2)));
        banner.setLayoutY((imageViews[arrX][arrY][1].getLayoutY() + (mapTileSize / 2) + (height / 8)));

        setMoveAnim(banner, player);
    }

    Pane getBanner() {
        return banner;
    }

    private void setMoveAnim(ImageView imageView, Party player) {

        /*
            Sets the movement animation of a tile (imageview)
         */

        new AnimationTimer() {

            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    if (imageView == null || !OverworldController.running) {
                        stop();
                        return;
                    }

                    if (OverworldController.hasControl) {
//                        if (player.detectTileChange(OverworldView.mapTileSize, true)) {
//                            OverworldController.reload = true;
//                        }
                        player.setxOffset(getPlayerXOffset());
                        player.setyOffset(getPlayerYOffset());
                        imageView.setLayoutX(imageView.getLayoutX() - player.getSpeedX());
                        imageView.setLayoutY(imageView.getLayoutY() + player.getSpeedY());
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

            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    if (pIVHashMap.get(p) == null || !OverworldController.running) {
                        stop();
                        return;
                    }
                    if (OverworldController.hasControl) {
                        pIVHashMap.get(p).relocate((float) (playerIV.getLayoutX() + ((p.getTileX() - player.getTileX()) * (mapTileSize / 2) + (p.getTileY() - player.getTileY()) * (-mapTileSize / 2) + p.getxOffset())) - player.getSpeedX(),
                                (float) (playerIV.getLayoutY() - ((p.getTileY() - player.getTileY()) * (-mapTileSize / 4) + (p.getTileX() - player.getTileX()) * (-mapTileSize / 4) + p.getyOffset())) - player.getSpeedY());
                    }
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
                    if (pane == null || !OverworldController.running) {
                        stop();
                        return;
                    }

                    if (OverworldController.hasControl) {
                        pane.setLayoutX(pane.getLayoutX() - player.getSpeedX());
                        pane.setLayoutY(pane.getLayoutY() + player.getSpeedY());
                    }
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

    void addPane(Pane p, boolean addToStack) {

        /*
            Adds pane to GUI
         */

        if (addToStack)
            nodeStack.add(p);
        overworldLayout.getChildren().add(p);
    }

    boolean removePane() {

        /*
            Removes a pane (window) from the stack and view
         */

        if (!nodeStack.empty()) {
            overworldLayout.getChildren().remove(nodeStack.pop());
            return true;
        } else
            return false;
    }

    void showSettlementInfo(SettlementTile tile, int arrX, int arrY) {

        /*
            Shows the information of a settlement tile. Click event handled by controller
            Uses ScaleTransition from javafx.animation to enlarge the banner to fit the text
         */

        float scaleFactor = 8f;

        ImageView banner = new ImageView(Images.banner);
        banner.setLayoutX(imageViews[arrX][arrY][0].getLayoutX() + (mapTileSize / 2) - (Images.banner.getWidth() / 2));
        banner.setLayoutY(imageViews[arrX][arrY][0].getLayoutY() + (mapTileSize / 2) + (Images.banner.getHeight() / 8));
        overworldLayout.getChildren().add(banner);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), banner);
        scaleTransition.setToX(6);
        scaleTransition.setToY(6);
        scaleTransition.setInterpolator(Interpolator.EASE_IN);

        scaleTransition.play();

        infoBox = new Pane();

        scaleTransition.setOnFinished(event -> {

            if (nodeStack.size() > 0)
                overworldLayout.getChildren().remove(nodeStack.pop());

            nodeStack.push(infoBox);

            ImageView newBanner = new ImageView(new Image("/media/graphics/overworld/banner/banner.png", banner.getImage().getWidth() * scaleFactor, banner.getImage().getHeight() * scaleFactor, false, false));
            newBanner.setLayoutX(screenWidth / 2 - newBanner.getImage().getWidth() / 2);
            newBanner.setLayoutY(screenHeight / 2 - newBanner.getImage().getHeight() / 2);

            infoBox.getChildren().addAll(
                    newBanner,
                    createText(newBanner.getLayoutY() + newBanner.getImage().getHeight() / 4.5, tile.settlementName, Font.font("Luminari", FontWeight.BOLD, (screenWidth / 1680) * 24), screenWidth),
                    createText(newBanner.getLayoutY() + newBanner.getImage().getHeight() / 3, tile.subType, Font.font("Luminari", FontWeight.NORMAL, (screenWidth / 1680) * 18), screenWidth),
                    createText(newBanner.getLayoutY() + newBanner.getImage().getHeight() / 2,
                            "This settlement is a " + tile.subType.toLowerCase() +
                                    " that currently has a populace count of " + tile.population + "\n and has " + tile.resources[0] + " wood, " +
                                    tile.resources[1] + " stone, " + tile.resources[2] + " iron and " + tile.resources[3] + " gold in it's warehouse/s",
                            Font.font("Luminari", FontWeight.NORMAL, ((screenWidth / 1680) * 16)), screenWidth)
            );

            overworldLayout.getChildren().add(infoBox);

            overworldLayout.getChildren().remove(scaleTransition.getNode());
        });
    }

    void showInMapInfo(String name, String difficulty) {

        /*
            Shows the information of a settlement tile. Click event handled by controller
         */

        String enterInfo = "Press enter to explore this dungeon";

        infoBox = new Pane();

        nodeStack.push(infoBox);

        double boxWidth = screenWidth / 3;
        double boxHeight = screenHeight / 5;

        Rectangle box = new Rectangle(boxWidth, boxHeight, Paint.valueOf("white"));
        box.relocate((screenWidth / 2) - (boxWidth / 2), screenHeight / 2 - (boxHeight / 2));

        infoBox.getChildren().addAll(
                box,
                createText((screenWidth / 2), (screenHeight / 2) - (boxHeight / 2) + 20, name, new Font(24)),
                createText((screenWidth / 2), (screenHeight / 2) - (boxHeight / 2) + 40, difficulty, new Font(16)),
                createText(screenWidth / 2, screenHeight / 2 - (boxHeight / 2) + 80, enterInfo, new Font(12))
        );
        overworldLayout.getChildren().add(infoBox);
    }

    private Text createText(double x, double y, String string, Font font) { // font size

        /*
            Creates and returns a text object at coords x, y with text string and font font
         */

        Text text = new Text(string);
        text.setFont(font);
        text.relocate(x - text.getBoundsInLocal().getWidth() / 2, y);

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

class Minimap {

    /*
        Contains all data related to Overworld Minimap

        Is accessed by view to add minimapPane and minimapFrame

        Can be hidden or shown through HIDEMENU key (default M) (default visible)

        Minimap color legend:
        Red - Settlement
        Purple - Dungeon
        Lime green - Grass
        Green - Light forest
        Dark green - Heavy forest
        Blue - Water (All water tiles)
     */

    private Pane minimapPane;
    private ImageView minimapFrame;

    private Rectangle[][] minimap;

    private int radius;

    Minimap(double screenWidth, double screenHeight, int radius) {
        this.radius = radius;

        minimapPane = new Pane();
        minimapPane.relocate(screenWidth - Images.minimapFrame.getWidth() / 1.25, screenHeight / 16);
        minimapPane.getTransforms().add(new Rotate(45, Rotate.Z_AXIS));
        minimap = new Rectangle[radius * 2 + 1][radius * 2 + 1];

        minimapFrame = new ImageView(Images.minimapFrame);
        minimapFrame.getTransforms().add(new Rotate(45, Rotate.Z_AXIS));
        minimapFrame.relocate(minimapPane.getLayoutX(), minimapPane.getLayoutY() - (1020 / Images.minimapFrame.getWidth()) / 2);
    }

    Pane getMinimapPane() {
        return minimapPane;
    }

    public ImageView getMinimapFrame() {
        return minimapFrame;
    }

    Pane drawMiniMap(Tile[][] tiles, Party player) {

        long start = System.currentTimeMillis();

        for (int y = 0; y < radius * 2 + 1; y++) {
            // yPos in tile array
            int yPos = player.getTileY() > 0 ? (player.getTileY() < 1000 ? player.getTileY() - radius + y : 999) : 0;
            for (int x = 0; x < radius * 2 + 1; x++) {
                // xPos in tile array
                int xPos = player.getTileX() > 0 ? (player.getTileX() < 1000 ? player.getTileX() - radius + x : 999) : 0;
                String tile = tiles[xPos][yPos].type;
                double size = (Images.minimapFrame.getWidth() - (1020 / Images.minimapFrame.getWidth()) / 2) / (radius * 2 + 1);
                if (x == radius && y == radius)
                    minimap[x][y] = new Rectangle(size, size, Paint.valueOf("white"));
                else if (tile.equals("Grass"))
                    minimap[x][y] = new Rectangle(size, size, Paint.valueOf("2cce38"));
                else if (tile.equals("ForestLight"))
                    minimap[x][y] = new Rectangle(size, size, Paint.valueOf("0c8d15"));
                else if (tile.equals("ForestHeavy"))
                    minimap[x][y] = new Rectangle(size, size, Paint.valueOf("125d17"));
                else if (tile.equals("Mountain"))
                    minimap[x][y] = new Rectangle(size, size, Paint.valueOf("a5a5a5"));
                else if (tile.contains("Water"))
                    minimap[x][y] = new Rectangle(size, size, Paint.valueOf("0b50b7"));
                else if (tile.equals("Settlement"))
                    minimap[x][y] = new Rectangle(size, size, Paint.valueOf("red"));
                else if (tile.equals("InMap"))
                    minimap[x][y] = new Rectangle(size, size, Paint.valueOf("violet"));
                minimap[x][y].relocate(x * size, y * size);
                minimapPane.getChildren().add(minimap[x][y]);
            }
        }

        System.out.println("Minimap init took " + (System.currentTimeMillis() - start) + "ms");

        return minimapPane;
    }
}