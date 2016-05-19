package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Tile {
    ImageView tileImage;
    boolean tresspassable;
    private Values values = Main.values;

    Tile(String tileType, boolean tresspassable){
        this.tresspassable = tresspassable;
        tileImage = new ImageView(new Image("/media/graphics/" + tileType + ".png", values.mapTileSize, values.mapTileSize, true, true));
    }
}
