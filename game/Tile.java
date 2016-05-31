package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Tile {
    boolean tresspassable;
    boolean accesible; // for entering cities
    String name;
    //ImageView tileImage;

    private Values values = Main.values;

    Tile(String tileType, boolean tresspassable, boolean accesible){
        this.tresspassable = tresspassable;
        this.name = tileType;
        this.accesible = accesible;
        //tileImage = new ImageView(new Image("/media/graphics/" + tileType + ".png"));
    }
}
