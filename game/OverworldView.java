package game;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class OverworldView{
    
    private Image forestTile;
    private Image villageTile;
    private Image mountainTile;
    private Image grassTile;
    
    OverworldView(){}

    public Scene displayOverworld(Tile[][] map, int mapSize, double mapTileSize, double screenWidth, double screenHeight, double xOffset, double yOffset, int zoom, int[] currPos){

        System.out.println("Displaying overworld");

        Pane overworldLayout = new Pane();
        
        long start = System.currentTimeMillis();

        ImageView imageView;
        Image image = null;

        loadGraphics(mapTileSize, mapTileSize); // should not always redraw

        System.out.println("Xoffset is: " + xOffset);
        System.out.println("Yoffset is: " + yOffset);

        // (x + c > 0 ? (x + c < mapSize ? x + c : mapSize -1) : 0), (y + b > 0 ? (y + b < mapSize ? y + b : mapSize -1) : 0)

        for (int b = -zoom * 2; b < zoom * 2; b++) {
            for (int c = -zoom * 2; c < zoom * 2; c++) {
                //System.out.println(offset);
                int xPos = (currPos[0] + c > 0 ? (currPos[0] + c < mapSize ? currPos[0] + c : mapSize - 1) : 0);
                int yPos = (currPos[1] + b > 0 ? (currPos[1] + b < mapSize ? currPos[1] + b : mapSize - 1) : 0);
                if (map[xPos][yPos].type.equalsIgnoreCase("Village"))
                    image = villageTile;
                if (map[xPos][yPos].type.equalsIgnoreCase("ForestTest"))
                    image = forestTile;
                if (map[xPos][yPos].type.equalsIgnoreCase("Grass"))
                    image = grassTile;
                if (map[xPos][yPos].type.equalsIgnoreCase("Mountain"))
                    image = mountainTile;

                imageView = new ImageView(image);
                imageView.relocate(0.5 * mapTileSize * ((c) - (b)) + (screenWidth / 2) - (mapTileSize / 2) + xOffset, 0.25 * mapTileSize * ((c) + (b)) + (screenHeight / 2) - (mapTileSize / 2) + yOffset);
                //map[(x + c > 0 ? (x + c < mapSize ? x + c : mapSize -1) : 0)][(y + b > 0 ? (y + b < mapSize ? y + b : mapSize -1) : 0)].tileImage.relocate(0.5 * mapTileSize * ((x + c) - (y + b)) + (screenWidth / 2) - (mapTileSize / 2), 0.25 * mapTileSize * ((x + c) + (y + b)) + (screenHeight / 2) - (mapTileSize / 2));
                overworldLayout.getChildren().add(imageView);
            }
        }

        System.out.println("Frame drawn in " + (System.currentTimeMillis() - start) + "ms");

        if(System.currentTimeMillis() - start < 33){
            System.out.println("Sleeping");
            //Main.sleep(33 - (System.currentTimeMillis() - start));
        }

        return new Scene(overworldLayout, screenWidth, screenHeight);
    }

    private void loadGraphics(double width, double height){
        forestTile = new Image("/media/graphics/ForestTest.png", width, height, true, false);
        villageTile = new Image("/media/graphics/Village.png", width, height, true, false);
        mountainTile = new Image("/media/graphics/Mountain.png", width, height, true, false);
        grassTile = new Image("/media/graphics/Grass.png", width, height, true, false);
    }
}
