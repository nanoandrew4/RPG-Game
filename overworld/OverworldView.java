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
import java.util.Stack;

class Images {

    /*
        Class contains and loads all images to be used by view in individual imageviews
     */

    Image tileBorder;

    Image forestLight;
    Image forestHeavy;
    Image village;
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
        TODO: FIX TILE POSITIONING SYSTEM, MUST BE FLAWLESS FOR ANY PROGRESS TO BE MADE (PROBLEM IS IN RELOCATE CALL)
     */

    Images images;

    /*
        Vars to be used for moving animation
     */

    double speedXVal;
    double speedYVal;
    final DoubleProperty speedX = new SimpleDoubleProperty();
    final DoubleProperty speedY = new SimpleDoubleProperty();
    private final LongProperty lastUpdateTime = new SimpleLongProperty();

    ImageView[][][] imageViews; // for controller to access and add click events

    double xOffset = 0; // total offset from initial tile
    double yOffset = 0; // total offset from initial tile

    private double screenWidth, screenHeight, padding = 15;

    Button close = new Button("Close"), manageCity = new Button("Manage"), diplomacy = new Button("Diplomacy"), trade = new Button("Trade"); // for info window, so controller can access it - will be imagebutton
    private Pane overworldLayout;
    private Pane infoBox;

    Stack<Pane> paneStack = new Stack<>(); // makes returning to previous window easier
    // if view controlled code for closing windows you could close one by one instead of all at once... since only one close button is global

    private ImageView redDot;

    OverworldView(int mapSize, double screenWidth, double screenHeight) {
        imageViews = new ImageView[mapSize][mapSize][2];
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    Scene initDisplay(Tile[][] tiles, double screenWidth, double screenHeight, int zoom, double mapTileSize, int[] currPos, int[] startPos, int mapSize) {

        /*
            Loads the initial set of tiles for display based on current position
         */

        System.out.println("Loading images");

        images = new Images(mapTileSize, mapTileSize);

        long start = System.currentTimeMillis();

        speedXVal = mapTileSize / 128;
        speedYVal = mapTileSize / 256;

        overworldLayout = new Pane();

        for (int y = -zoom; y < zoom; y++) {
            for (int x = -zoom; x < zoom; x++) {
                int xPos = (currPos[0] + x < 0 ? (currPos[0] + x >= mapSize ? mapSize - 1 : 0) : currPos[0] + x);
                int yPos = (currPos[1] + y < 0 ? (currPos[1] + y >= mapSize ? mapSize - 1 : 0) : currPos[1] + y);

                System.out.println((xPos - startPos[0]));
                System.out.println((yPos - startPos[1]));

                drawTile(tiles, (float)(mapTileSize / 2), xPos, yPos,
                        0.5 * mapTileSize * ((xPos - startPos[0]) - (yPos - startPos[1])) + (screenWidth / 2),
                        0.25 * mapTileSize * ((xPos - startPos[0]) + (yPos - startPos[1])) + (screenHeight / 2),
                        0,
                        0
                );
            }
        }

        drawPlayers();

        System.out.println("Init load took " + (System.currentTimeMillis() - start) + "ms");

        return new Scene(overworldLayout, screenWidth, screenHeight);
    }

    private void drawPlayers() {
        if (redDot != null)
            overworldLayout.getChildren().remove(redDot);
        redDot = new ImageView(new Image("/media/graphics/redDot.png", 6, 6, false, false));
        redDot.relocate((screenWidth / 2) - 3, (screenHeight / 2) - 3);
        overworldLayout.getChildren().add(redDot);
    }

    private void drawTile(Tile[][] tiles, float bannerSize, int xPos, int yPos, double pixelX, double pixelY, double currXOffset, double currYOffset) {
        if(xOffset != 0.0 || yOffset != 0.0) {
            overworldLayout.getChildren().remove(imageViews[xPos][yPos][0]);
            overworldLayout.getChildren().remove(imageViews[xPos][yPos][1]);
            if(tiles[xPos][yPos].type.equalsIgnoreCase("Settlement"))
                overworldLayout.getChildren().remove(tiles[xPos][yPos].banner);
        }

        // add tiles

        if(imageViews[xPos][yPos][0] == null) {
            System.out.print(".");
            imageViews[xPos][yPos][0] = genTile(xPos, yPos, tiles);
            imageViews[xPos][yPos][0].setLayoutX(pixelX);
            imageViews[xPos][yPos][0].setLayoutY(pixelY);
            setMoveAnim(imageViews[xPos][yPos][0]);
        }
        overworldLayout.getChildren().add(imageViews[xPos][yPos][0]);

        // add settlement banners

        if (tiles[xPos][yPos].type.equalsIgnoreCase("Settlement")) {
            if(tiles[xPos][yPos].banner == null) {
                genBanner(bannerSize, bannerSize / 2, tiles[xPos][yPos]);
                tiles[xPos][yPos].banner.relocate(pixelX, pixelY);
                tiles[xPos][yPos].banner.setTranslateX(currXOffset);
                tiles[xPos][yPos].banner.setTranslateY(currYOffset);
                setMoveAnim(tiles[xPos][yPos].banner);
            }
            overworldLayout.getChildren().add(tiles[xPos][yPos].banner);
        }

        // add tile borders

        if(imageViews[xPos][yPos][1] == null) {
            imageViews[xPos][yPos][1] = new ImageView(images.tileBorder);
            imageViews[xPos][yPos][1].relocate(pixelX, pixelY);
            imageViews[xPos][yPos][1].setTranslateX(currXOffset);
            imageViews[xPos][yPos][1].setTranslateY(currYOffset);
            imageViews[xPos][yPos][1].setVisible(false);
            setMoveAnim(imageViews[xPos][yPos][1]);
        }

        overworldLayout.getChildren().add(imageViews[xPos][yPos][1]);

        System.out.println("PixelX: " + pixelX);
        System.out.println("PixelY: " + pixelY);
        System.out.println("Y: " + imageViews[xPos][yPos][0].getLayoutY());
        //System.out.println("CurrXOffset: " + currXOffset);
        //System.out.println("CurrYOffset: " + currYOffset);
        System.out.println();
    }

    private void genBanner(float width, float height, Tile tile) {

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
        Text relationship = new Text((tile.settlementTile.relationship >= 0 ? "" : "-") + tile.settlementTile.relationship);
        relationship.setFont(new Font(10));

        bannerL.relocate(0, boxHeight / 3);
        bannerR.relocate((double) width - (double) width / 3, boxHeight / 3);
        attack.relocate((double) width / 8, boxHeight * 2 / 3);
        enter.relocate(((double) width / 8) + ((double) width / 4), boxHeight * 2 / 3);
        diplomacy.relocate(((double) width / 8) + ((double) width / 2), boxHeight * 2 / 3);
        name.relocate((double) width / 2 - calcStringWidth(tile.settlementTile.settlementName) / 2, boxHeight / 3);
        relationship.relocate((double) width / 2 - calcStringWidth(" " + tile.settlementTile.relationship), 0);

        tile.banner.getChildren().addAll(box, bannerL, bannerR, attack, enter, diplomacy, name, relationship);
        tile.banner.setVisible(false);

        tile.banner.setTranslateX(xOffset);
        tile.banner.setTranslateY(yOffset);
    }

    private ImageView genTile(int xPos, int yPos, Tile[][] tiles) {

        /*
            Generates the imageview with the image from Image class based on the data from the model
         */

        ImageView img = new ImageView();
        String type = tiles[xPos][yPos].type;

        if (type.equalsIgnoreCase("Settlement")) {
            if (tiles[xPos][yPos].settlementTile.subType.equalsIgnoreCase("Village"))
                img = new ImageView(images.village);
        } else if (type.equalsIgnoreCase("ForestLight"))
            img = new ImageView(images.forestLight);
        else if (type.equalsIgnoreCase("ForestHeavy"))
            img = new ImageView(images.forestHeavy);
        else if (type.equalsIgnoreCase("Grass"))
            img = new ImageView(images.grass);
        else if (type.equalsIgnoreCase("Mountain"))
            img = new ImageView(images.mountain);
        else if (type.equalsIgnoreCase("WaterAll"))
            img = new ImageView(images.waterAll);
        else if (type.equalsIgnoreCase("WaterE"))
            img = new ImageView(images.waterE);
        else if (type.equalsIgnoreCase("WaterN"))
            img = new ImageView(images.waterN);
        else if (type.equalsIgnoreCase("WaterNE"))
            img = new ImageView(images.waterNE);
        else if (type.equalsIgnoreCase("WaterNESE"))
            img = new ImageView(images.waterNESE);
        else if (type.equalsIgnoreCase("WaterNW"))
            img = new ImageView(images.waterNW);
        else if (type.equalsIgnoreCase("WaterNWNE"))
            img = new ImageView(images.waterNWNE);
        else if (type.equalsIgnoreCase("WaterNWSW"))
            img = new ImageView(images.waterNWSW);
        else if (type.equalsIgnoreCase("WaterS"))
            img = new ImageView(images.waterS);
        else if (type.equalsIgnoreCase("WaterSE"))
            img = new ImageView(images.waterSE);
        else if (type.equalsIgnoreCase("WaterSW"))
            img = new ImageView(images.waterSW);
        else if (type.equalsIgnoreCase("WaterSWSE"))
            img = new ImageView(images.waterSWSE);
        else if (type.equalsIgnoreCase("WaterW"))
            img = new ImageView(images.waterW);

        return img;
    }

    void addRow(Tile[][] tiles, double screenWidth, double screenHeight, double tileXOffset, double tileYOffset, int zoom, double mapTileSize, int[] currPos, int[] startPos, int mapSize, boolean top) {

        /*
            Adds a row when a tile change on the y axis is detected
            TODO: WIP
            TODO: DO ALL TILES NEED TO BE REDRAWN?
         */

        double currXOffset = xOffset;
        double currYOffset = yOffset;

        System.out.println("Adding row");
        if ((top && currPos[1] + (zoom) + 1 < mapSize) || (!top && currPos[1] + (zoom) - 1 > 0)) {
            for(int x = -zoom; x < zoom; x++) {
                int yPos = currPos[1] + (top ? -zoom : zoom);
                int xPos = (currPos[0] + x < 0 ? (currPos[0] + x >= mapSize ? mapSize - 1 : 0) : currPos[0] + x);

                System.out.println((xPos - startPos[0]));
                System.out.println((yPos - startPos[1]));

                // Draw new tile
                drawTile(tiles, (float) (mapTileSize / 2), xPos, yPos,
                        0.5 * mapTileSize * ((xPos - startPos[0]) - (yPos - startPos[1])) + (screenWidth / 2),
                        0.25 * mapTileSize * ((xPos - startPos[0]) + (yPos - startPos[1])) + (screenHeight / 2),
                        currXOffset,
                        currYOffset
                );

                // Remove tiles

                yPos = currPos[1] - (top ? -zoom : zoom);
                overworldLayout.getChildren().remove(imageViews[currPos[0] + x][yPos][0]);
                overworldLayout.getChildren().remove(imageViews[currPos[0] + x][yPos][1]);
                // remove banners
                imageViews[xPos][yPos][0] = null;
            }
            drawPlayers();
        }
    }

    void addColumn(Tile[][] tiles, double screenWidth, double screenHeight, int zoom, double mapTileSize, int[] currPos, int mapSize, boolean right) {

        /*
            Adds a column when a tile change on the x axis is detected
            TODO: WIP
         */

        System.out.println("Adding column");
        if ((right && currPos[0] + (zoom) + 1 < mapSize) || (!right && currPos[0] + (zoom) - 1 > 0)) {
            int x = right ? -zoom : zoom;
            for (int y = -zoom; y < zoom; y++) {
                /*
                drawTile(tiles, (float)(mapTileSize / 2), currPos[0] + x, currPos[1] + y,
                        0.5 * mapTileSize * (x - y) + (screenWidth / 2) - (xOffset),
                        0.25 * mapTileSize * (x + y) + (screenHeight / 2) - (yOffset + mapTileSize / 2)
                );
*/
                // Remove tile

                int xPos = currPos[0] - x;
                overworldLayout.getChildren().remove(imageViews[xPos][currPos[1] + y][0]);
                imageViews[xPos][currPos[1] + y][0] = null;
            }
            drawPlayers();
        }
    }

    private void setMoveAnim(ImageView imageView) {

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
                    final double oldX = imageView.getLayoutX();
                    final double newX = oldX + speedX.get();
                    final double oldY = imageView.getLayoutY();
                    final double newY = oldY + speedY.get();
                    imageView.setLayoutX(newX);
                    imageView.setLayoutY(newY);
                }
                lastUpdateTime.set(timestamp);
            }
        }.start();
    }

    private void setMoveAnim(Pane pane) {

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

    void showTileInfo(Tile tile) {

        /*
            Shows the information of the clicked tile. Click event handled by controller
         */

        // TODO: BUTTON RELOCATING NEEDS IMPROVING

        infoBox = new Pane();

        paneStack.push(infoBox);

        double boxWidth = screenWidth / 3;
        double boxHeight = screenHeight / 5;

        Rectangle box = new Rectangle(boxWidth, boxHeight, Paint.valueOf("white"));
        box.relocate((screenWidth / 2) - (boxWidth / 2), screenHeight / 2 - (boxHeight / 2));

        Text tileName = new Text(tile.type);
        tileName.setFont(Font.font(14)); // make font depend on screen size
        tileName.relocate((screenWidth / 2) - calcStringWidth(tile.type) / 2, screenHeight / 2 - (screenHeight / 10) + 20);

        close = new Button("Close");
        close.setPadding(Insets.EMPTY);
        close.relocate((screenWidth / 2) - calcStringWidth("Close") / 2, screenHeight / 2 + (screenHeight / 10) - 30);

        infoBox.getChildren().addAll(box, tileName, close);
        overworldLayout.getChildren().add(infoBox);
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
                createButton(close, (screenWidth / 2) - calcStringWidth("Close") / 2, screenHeight / 2 + (boxHeight / 2) - 30),
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
                createButton(close, screenWidth / 2 - calcStringWidth("Close") / 2, BOXBOTTOMBOUND - 30),
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
