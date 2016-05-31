package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class Camera {

    Pane overworldLayout;
    Values values = Main.values;
    Tile[][] map;

    Camera(Tile[][] map, int x, int y, int zoomLevel){
        overworldLayout = new Pane();
        this.map = map;

        ImageView imageView;
        Image image = null;

        if(values.screenWidth > values.screenHeight){
            values.mapTileSize = values.screenHeight / zoomLevel;
        }
        else {
            values.mapTileSize = values.screenWidth / zoomLevel;
        }

        values.loadGraphics(values.mapTileSize, values.mapTileSize);

        // (x + c > 0 ? (x + c < values.mapSize ? x + c : values.mapSize -1) : 0), (y + b > 0 ? (y + b < values.mapSize ? y + b : values.mapSize -1) : 0)

        long start  = System.currentTimeMillis();

        for(int b = -zoomLevel * 2; b < zoomLevel * 2; b++) {
            for (int c = -zoomLevel * 2; c < zoomLevel * 2; c++) {
                //System.out.println(offset);
                int xPos = (x + c > 0 ? (x + c < values.mapSize ? x + c : values.mapSize -1) : 0);
                int yPos = (y + b > 0 ? (y + b < values.mapSize ? y + b : values.mapSize -1) : 0);
                if(map[xPos][yPos].name.equalsIgnoreCase("Village"))
                    image = values.villageTile;
                if(map[xPos][yPos].name.equalsIgnoreCase("ForestTest"))
                    image = values.forestTile;
                if(map[xPos][yPos].name.equalsIgnoreCase("Grass"))
                    image = values.grassTile;
                if(map[xPos][yPos].name.equalsIgnoreCase("Mountain"))
                    image = values.mountainTile;

                imageView = new ImageView(image); // needs resizing, can't do with preloaded
                imageView.relocate(0.5 * values.mapTileSize * ((x + c) - (y + b)) + (values.screenWidth / 2) - (values.mapTileSize / 2) + values.xOffset, 0.25 * values.mapTileSize * ((x + c) + (y + b)) + (values.screenHeight / 2) - (values.mapTileSize / 2) + values.yOffset);
                //map[(x + c > 0 ? (x + c < values.mapSize ? x + c : values.mapSize -1) : 0)][(y + b > 0 ? (y + b < values.mapSize ? y + b : values.mapSize -1) : 0)].tileImage.relocate(0.5 * values.mapTileSize * ((x + c) - (y + b)) + (values.screenWidth / 2) - (values.mapTileSize / 2), 0.25 * values.mapTileSize * ((x + c) + (y + b)) + (values.screenHeight / 2) - (values.mapTileSize / 2));
                overworldLayout.getChildren().add(imageView);
            }
        }

        System.out.println("Loading/Reloading " + (double)(System.currentTimeMillis() - start) / 1000);
    }

    public void scrollCamera(KeyCode keyCode){
        //System.out.println("WASD pressed");
        // position on tiles needs work
        if(keyCode == KeyCode.A){
            if(values.currPos[0] > 0){
                values.currPos[0]--;
                values.xOffset += 20;
            }
            else
                return;
        }
        else if(keyCode == KeyCode.S){
            if(values.currPos[1] < values.mapSize){
                values.currPos[1]++;
                values.yOffset -= 20;
            }
            else
                return;
        }
        else if(keyCode == KeyCode.W){
            if(values.currPos[1] > 0){
                values.currPos[1]--;
                values.yOffset += 20;
            }
            else
                return;
        }
        else if(keyCode == KeyCode.D){
            if(values.currPos[0] < values.mapSize){
                values.currPos[0]++;
                values.xOffset -= 20;
            }
            else
                return;
        }
    }
}
