package game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Camera extends Application {

    Values values = Controller.values;

    Tile[][] map;

    private int overworldZoom = 8; // will store zoom on different stages for switching between them
    private int dungeonZoom;
    private int cityZoom;
    private int zoom = overworldZoom;

    private Image forestTile;
    private Image villageTile;
    private Image mountainTile;
    private Image grassTile;

    @Override
    public void start(Stage stage){
        getScreenSize();

        loadMainScene(stage);
    }

    private void getScreenSize() {
        values.screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        values.screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    }

    private void loadMainScene(Stage stage){
        Pane layout = new Pane();
        Button playButton = new Button("Play");

        playButton.relocate(values.screenWidth / 2, values.screenHeight / 2);
        layout.getChildren().add(playButton);
        playButton.setOnAction(event -> {
            System.out.println("Initializing UI");
            initGame(stage);
        });

        stage.setScene(new Scene(layout, values.screenWidth, values.screenHeight));
        stage.show();
    }

    private void initGame(Stage stage){
        //values.startGame = true;
        //Controller.sleep(1000);
        //while (!values.initComplete);

        displayOverworld(stage);
    }

    private void displayOverworld(Stage stage){

        System.out.println("Displaying overworld");

        Pane overworldLayout = new Pane();
        Scene overworldScene = null;

        map = OverworldMap.getMapArr();

        long start = System.currentTimeMillis();

        ImageView imageView;
        Image image = null;

        if (values.screenWidth > values.screenHeight) {
            values.mapTileSize = values.screenHeight / zoom;
            values.scrollOffset = values.mapTileSize / 8;
        } else {
            values.mapTileSize = values.screenWidth / zoom;
            values.scrollOffset = values.mapTileSize / 8;
        }

        loadGraphics(values.mapTileSize, values.mapTileSize);

        System.out.println("Xoffset is: " + values.xOffset);
        System.out.println("Yoffset is: " + values.yOffset);

        // (x + c > 0 ? (x + c < values.mapSize ? x + c : values.mapSize -1) : 0), (y + b > 0 ? (y + b < values.mapSize ? y + b : values.mapSize -1) : 0)

        for (int b = -zoom * 2; b < zoom * 2; b++) {
            for (int c = -zoom * 2; c < zoom * 2; c++) {
                //System.out.println(offset);
                int xPos = (values.currPos[0] + c > 0 ? (values.currPos[0] + c < values.mapSize ? values.currPos[0] + c : values.mapSize - 1) : 0);
                int yPos = (values.currPos[1] + b > 0 ? (values.currPos[1] + b < values.mapSize ? values.currPos[1] + b : values.mapSize - 1) : 0);
                if (map[xPos][yPos].type.equalsIgnoreCase("Village"))
                    image = villageTile;
                if (map[xPos][yPos].type.equalsIgnoreCase("ForestTest"))
                    image = forestTile;
                if (map[xPos][yPos].type.equalsIgnoreCase("Grass"))
                    image = grassTile;
                if (map[xPos][yPos].type.equalsIgnoreCase("Mountain"))
                    image = mountainTile;

                imageView = new ImageView(image);
                imageView.relocate(0.5 * values.mapTileSize * ((c) - (b)) + (values.screenWidth / 2) - (values.mapTileSize / 2) + values.xOffset, 0.25 * values.mapTileSize * ((c) + (b)) + (values.screenHeight / 2) - (values.mapTileSize / 2) + values.yOffset);
                //map[(x + c > 0 ? (x + c < values.mapSize ? x + c : values.mapSize -1) : 0)][(y + b > 0 ? (y + b < values.mapSize ? y + b : values.mapSize -1) : 0)].tileImage.relocate(0.5 * values.mapTileSize * ((x + c) - (y + b)) + (values.screenWidth / 2) - (values.mapTileSize / 2), 0.25 * values.mapTileSize * ((x + c) + (y + b)) + (values.screenHeight / 2) - (values.mapTileSize / 2));
                overworldLayout.getChildren().add(imageView);
            }
        }

        overworldScene = new Scene(overworldLayout, values.screenWidth, values.screenHeight);
        setInput(stage, overworldScene);
        stage.setScene(overworldScene);

        System.out.println("Frame drawn in " + (System.currentTimeMillis() - start) + "ms");

        if(System.currentTimeMillis() - start < 33){
            System.out.println("Sleeping");
            Controller.sleep(33 - (System.currentTimeMillis() - start));
        }

    }

    private void setInput(Stage stage, Scene scene){

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode keyCode = event.getCode();
            if(keyCode == KeyCode.Z && values.zoom > 0){ // for zooming in
                values.zoom--;
                System.out.println("Zoom in");
            }
            else if(keyCode == KeyCode.X && values.zoom < values.mapZoomMax){ // for zooming out
                values.zoom++;
                System.out.println("Zoom out");
            }
            // position on tiles needs work
            if(keyCode == KeyCode.A){
                if(values.currPos[0] > 0){
                    if(values.mapTileSize - Math.abs(values.xOffset) <= 0) {
                        System.out.println("Next tile");
                        values.currPos[0]--;
                        values.currPos[1]++;
                        values.xOffset = 0;
                    }
                    else
                        values.xOffset += values.scrollOffset;
                }
                else
                    return;
            }
            else if(keyCode == KeyCode.S){
                if(values.currPos[1] < values.mapSize){
                    if(values.mapTileSize - Math.abs(values.yOffset * 2) <= 0) {
                        System.out.println("Next tile");
                        values.currPos[1]++;
                        values.currPos[0]++;
                        values.yOffset = 0;
                    }
                    else
                        values.yOffset -= values.scrollOffset / 2;
                }
                else
                    return;
            }
            else if(keyCode == KeyCode.W){
                if(values.currPos[1] > 0){
                    if(values.mapTileSize - Math.abs(values.yOffset * 2) <= 0) {
                        System.out.println("Next tile");
                        values.currPos[1]--;
                        values.currPos[0]--;
                        values.yOffset = 0;
                    }
                    else
                        values.yOffset += values.scrollOffset / 2;
                }
                else
                    return;
            }
            else if(keyCode == KeyCode.D){
                if(values.currPos[0] < values.mapSize){
                    if(values.mapTileSize - Math.abs(values.xOffset) <= 0) {
                        System.out.println("Next tile");
                        values.currPos[0]++;
                        values.currPos[1]--;
                        values.xOffset = 0;
                    }
                    else
                        values.xOffset -= values.scrollOffset;
                }
                else
                    return;
            }
            displayOverworld(stage);
        });
    }

    private void loadGraphics(double width, double height){
        forestTile = new Image("/media/graphics/ForestTest.png", width, height, true, false);
        villageTile = new Image("/media/graphics/Village.png", width, height, true, false);
        mountainTile = new Image("/media/graphics/Mountain.png", width, height, true, false);
        grassTile = new Image("/media/graphics/Grass.png", width, height, true, false);
    }
}
