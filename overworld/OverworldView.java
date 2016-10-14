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
import main.Main;

import java.awt.image.BufferedImage;
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

    private Pane overworldLayout;
    private Pane infoBox;

    ImageView[][][] imageViews; // for controller to access and add click events
    ImageView centerTile; // for determining tile offset in pixels
    ImageView redDot;

    float zoomMultiplier = 2.2f;
    int zoom = (int) (6 * zoomMultiplier);

    double xOffset = 0, yOffset = 0; // total offset from initial tile

    private double screenWidth, screenHeight, padding = 15, mapTileSize;

    Button manageCity = new Button("Manage"), diplomacy = new Button("Diplomacy"), trade = new Button("Trade"); // for info window, so controller can access it - will be imagebutton
    Button enterDungeon = new Button("Enter");

    Stack<Pane> paneStack = new Stack<>(); // makes returning to previous window easier

    OverworldView(int mapSize, double screenWidth, double screenHeight) {
        imageViews = new ImageView[mapSize][mapSize][2];
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        if (screenWidth > screenHeight) {
            mapTileSize = screenHeight / (zoom / zoomMultiplier);
        } else {
            mapTileSize = screenWidth / (zoom / zoomMultiplier);
        }
    }

    Double getMapTileSize() {
        return mapTileSize;
    }

    Scene initDisplay(Tile[][] tiles, double screenWidth, double screenHeight, double mapTileSize, int[] currPos, int[] startPos, int mapSize) {

        /*
            Loads the initial set of tiles for display based on current position
         */

        System.out.println("Loading images");

        // might make the tile detection faulty after moving a lot
        images = new Images(mapTileSize + 2, mapTileSize + 2);

        long start = System.currentTimeMillis();

        speedXVal = 100 / mapTileSize;
        speedYVal = 50 / mapTileSize;

        overworldLayout = new Pane();

        for (int y = -zoom; y < zoom; y++) {
            for (int x = -zoom; x < zoom; x++) {
                int xPos = (currPos[0] + x < 0 ? (currPos[0] + x >= mapSize ? mapSize - 1 : 0) : currPos[0] + x);
                int yPos = (currPos[1] + y < 0 ? (currPos[1] + y >= mapSize ? mapSize - 1 : 0) : currPos[1] + y);

                //System.out.println((xPos - startPos[0]));
                //System.out.println((yPos - startPos[1]));

                drawTile(tiles, (float) (mapTileSize / 2), xPos, yPos,
                        0.5 * mapTileSize * ((xPos - startPos[0]) - (yPos - startPos[1])) + (screenWidth / 2),
                        0.25 * mapTileSize * ((xPos - startPos[0]) + (yPos - startPos[1])) + (screenHeight / 2)
                );
            }
        }

        centerTile = imageViews[currPos[0]][currPos[1]][0];

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

    private void genBanner(Tile tile, double parentX, double parentY, float width, float height, double mapTileSize) {

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

        tile.banner.setLayoutX((parentX - (mapTileSize / 2)) - (width / 2));
        tile.banner.setLayoutY((parentY - (mapTileSize / 4)) - (height / 2));
    }

    private ImageView genTile(int xPos, int yPos, Tile[][] tiles) {

        /*
            Generates the imageview with the image from Image class based on the data from the model
         */

        String type = tiles[xPos][yPos].type;

        if (type.equalsIgnoreCase("Settlement")) {
            if (tiles[xPos][yPos].settlementTile.subType.equalsIgnoreCase("Village"))
                return new ImageView(images.village);
        } else if (type.equalsIgnoreCase("InMap")) {
            if (tiles[xPos][yPos].inMapTile.inmapType.equalsIgnoreCase("Tower"))
                return new ImageView(images.tower);
        } else if (type.equalsIgnoreCase("ForestLight"))
            return new ImageView(images.forestLight);
        else if (type.equalsIgnoreCase("ForestHeavy"))
            return new ImageView(images.forestHeavy);
        else if (type.equalsIgnoreCase("Grass"))
            return new ImageView(images.grass);
        else if (type.equalsIgnoreCase("Mountain"))
            return new ImageView(images.mountain);
        else if (type.equalsIgnoreCase("WaterAll"))
            return new ImageView(images.waterAll);
        else if (type.equalsIgnoreCase("WaterE"))
            return new ImageView(images.waterE);
        else if (type.equalsIgnoreCase("WaterN"))
            return new ImageView(images.waterN);
        else if (type.equalsIgnoreCase("WaterNE"))
            return new ImageView(images.waterNE);
        else if (type.equalsIgnoreCase("WaterNESE"))
            return new ImageView(images.waterNESE);
        else if (type.equalsIgnoreCase("WaterNW"))
            return new ImageView(images.waterNW);
        else if (type.equalsIgnoreCase("WaterNWNE"))
            return new ImageView(images.waterNWNE);
        else if (type.equalsIgnoreCase("WaterNWSW"))
            return new ImageView(images.waterNWSW);
        else if (type.equalsIgnoreCase("WaterS"))
            return new ImageView(images.waterS);
        else if (type.equalsIgnoreCase("WaterSE"))
            return new ImageView(images.waterSE);
        else if (type.equalsIgnoreCase("WaterSW"))
            return new ImageView(images.waterSW);
        else if (type.equalsIgnoreCase("WaterSWSE"))
            return new ImageView(images.waterSWSE);
        else if (type.equalsIgnoreCase("WaterW"))
            return new ImageView(images.waterW);

        return null;
    }

    private void drawTile(Tile[][] tiles, float bannerSize, int xPos, int yPos, double pixelX, double pixelY) {

        // add tiles

        System.out.print(".");
        imageViews[xPos][yPos][0] = genTile(xPos, yPos, tiles);
        imageViews[xPos][yPos][0].setLayoutX(pixelX);
        imageViews[xPos][yPos][0].setLayoutY(pixelY);
        setMoveAnim(imageViews[xPos][yPos][0]);
        if (xOffset == 0d && yOffset == 0d)
            overworldLayout.getChildren().add(imageViews[xPos][yPos][0]);
        else
            overworldLayout.getChildren().add(0, imageViews[xPos][yPos][0]);

        // add settlement banners

        if (tiles[xPos][yPos].type.equalsIgnoreCase("Settlement")) {
            genBanner(tiles[xPos][yPos], imageViews[xPos][yPos][0].getLayoutX(), imageViews[xPos][yPos][0].getLayoutY(), bannerSize, bannerSize / 2, mapTileSize);
            tiles[xPos][yPos].banner.setLayoutX(pixelX); // fix positioning so is in center of tile...
            tiles[xPos][yPos].banner.setLayoutY(pixelY);
            setMoveAnim(tiles[xPos][yPos].banner);
            overworldLayout.getChildren().add(tiles[xPos][yPos].banner);
        }

        // add tile borders

        imageViews[xPos][yPos][1] = new ImageView(images.tileBorder);
        imageViews[xPos][yPos][1].setLayoutX(pixelX);
        imageViews[xPos][yPos][1].setLayoutY(pixelY);
        imageViews[xPos][yPos][1].setVisible(false);
        setMoveAnim(imageViews[xPos][yPos][1]);
        if (xOffset == 0d && yOffset == 0d)
            overworldLayout.getChildren().add(imageViews[xPos][yPos][1]);
        else
            overworldLayout.getChildren().add(0, imageViews[xPos][yPos][1]);
    }

    void addRow(Tile[][] tiles, double screenWidth, double screenHeight, double mapTileSize, int[] currPos, int[] startPos, int mapSize, boolean top) {

        /*
            Adds a row when a tile change on the y axis is detected
            TODO: WIP
         */

        double currXOffset = xOffset;
        double currYOffset = yOffset;

        System.out.println("Adding row");
        if ((top && currPos[1] + (zoom) + 1 < mapSize) || (!top && currPos[1] + (zoom) - 1 > 0)) {
            for (int x = -zoom; x < zoom; x++) {
                int yPos = currPos[1] + (top ? -zoom : zoom);
                int xPos = (currPos[0] + x < 0 ? (currPos[0] + x >= mapSize ? mapSize - 1 : 0) : currPos[0] + x);

                //System.out.println((xPos - startPos[0]));
                //System.out.println((yPos - startPos[1]));

                // Draw new tile
                /*drawTile(tiles, (float) (mapTileSize / 2), xPos, yPos,
                        0.5 * mapTileSize * ((xPos - startPos[0]) - (yPos - startPos[1])) + (screenWidth / 2) + xOffset,
                        0.25 * mapTileSize * ((xPos - startPos[0]) + (yPos - startPos[1])) + (screenHeight / 2) - yOffset
                );*/


                if (imageViews[xPos][yPos + (top ? 1 : -1)][0] == null) {
                    System.out.println("Null at: " + xPos + ", " + yPos);
                    return;
                }

                drawTile(tiles, (float) (mapTileSize / 2), xPos, yPos,
                        imageViews[xPos][yPos + (top ? 1 : -1)][0].getLayoutX(),
                        -imageViews[xPos][yPos + (top ? 1 : -1)][0].getLayoutY()
                );
/*
                System.out.println(imageViews[xPos][yPos][0].getLayoutX());
                System.out.println(imageViews[xPos][yPos][0].getLayoutY());
                System.out.println(imageViews[xPos][yPos + (top ? 1 : -1)][0].getLayoutX());
                System.out.println(imageViews[xPos][yPos + (top ? 1 : -1)][0].getLayoutY());
                System.out.println();
*/
                // Remove tiles - confirmed working

                yPos = currPos[1] - (top ? -zoom : zoom);
                overworldLayout.getChildren().remove(imageViews[xPos][yPos][0]);
                overworldLayout.getChildren().remove(imageViews[xPos][yPos][1]);
                // remove banners
                imageViews[xPos][yPos][0] = null;
            }
            System.out.println("Currpos: " + currPos[0] + ", " + currPos[1]);
            System.out.println("First tile x-pos: " + imageViews[currPos[0] - zoom][currPos[1] - zoom][0].getLayoutX());
            System.out.println("First tile y-pos: " + imageViews[currPos[0] - zoom][currPos[1] - zoom][0].getLayoutY());
            System.out.println();
            centerTile = imageViews[currPos[0]][currPos[1]][0];
            drawPlayers();
        }
    }

    void addColumn(Tile[][] tiles, double screenWidth, double screenHeight, double mapTileSize, int[] currPos, int[] startPos, int mapSize, boolean right) {

        /*
            Adds a column when a tile change on the x axis is detected
         */

        double currXOffset = xOffset;
        double currYOffset = yOffset;

        System.out.println("Adding column");
        if ((right && currPos[1] + (zoom) + 1 < mapSize) || (!right && currPos[1] + (zoom) - 1 > 0)) {
            for (int y = -zoom; y < zoom; y++) {
                int xPos = currPos[0] + (right ? -zoom : zoom);
                int yPos = (currPos[1] + y < 0 ? (currPos[0] + y >= mapSize ? mapSize - 1 : 0) : currPos[0] + y);

                //System.out.println((xPos - startPos[0]));
                //System.out.println((yPos - startPos[1]));

                // Draw new tile
                /*drawTile(tiles, (float) (mapTileSize / 2), xPos, yPos,
                        0.5 * mapTileSize * ((xPos - startPos[0]) - (yPos - startPos[1])) + (screenWidth / 2) + xOffset,
                        0.25 * mapTileSize * ((xPos - startPos[0]) + (yPos - startPos[1])) + (screenHeight / 2) - yOffset
                );*/


                if (imageViews[xPos][yPos + (right ? 1 : -1)][0] == null) {
                    System.out.println("Null at: " + xPos + ", " + yPos);
                    return;
                }

                drawTile(tiles, (float) (mapTileSize / 2), xPos, yPos,
                        imageViews[xPos + (right ? 1 : -1)][yPos][0].getLayoutX(),
                        -imageViews[xPos + (right ? 1 : -1)][yPos][0].getLayoutY()
                );
/*
                System.out.println(imageViews[xPos][yPos][0].getLayoutX());
                System.out.println(imageViews[xPos][yPos][0].getLayoutY());
                System.out.println(imageViews[xPos][yPos + (right ? 1 : -1)][0].getLayoutX());
                System.out.println(imageViews[xPos][yPos + (right ? 1 : -1)][0].getLayoutY());
                System.out.println();
*/
                // Remove tiles

                yPos = currPos[1] - (right ? -zoom : zoom);
                overworldLayout.getChildren().remove(imageViews[xPos][yPos][0]);
                overworldLayout.getChildren().remove(imageViews[xPos][yPos][1]);
                // remove banners
                imageViews[xPos][yPos][0] = null;
            }
            System.out.println("Currpos: " + currPos[0] + ", " + currPos[1]);
            System.out.println();
            centerTile = imageViews[currPos[0]][currPos[1]][0];
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
                    final double oldX = pane.getLayoutX();
                    final double newX = oldX + speedX.get();
                    final double oldY = pane.getLayoutY();
                    final double newY = oldY + speedY.get();
                    pane.setLayoutX(newX);
                    pane.setLayoutY(newY);
                }
                lastUpdateTime.set(timestamp);
            }
        }.start();
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
