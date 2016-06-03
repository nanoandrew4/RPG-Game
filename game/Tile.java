package game;

public class Tile {
    boolean tresspassable;
    boolean accesible; // for entering cities
    String type;

    Tile(){
        tresspassable = false;
        accesible = false;
        type = "";
    }

    Tile(String tileType, boolean tresspassable, boolean accesible){
        this.tresspassable = tresspassable;
        this.type = tileType;
        this.accesible = accesible;
    }
}
