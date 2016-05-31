package game;

public class Tile {
    boolean tresspassable;
    boolean accesible; // for entering cities
    String name;

    private Values values = Main.values;

    Tile(String tileType, boolean tresspassable, boolean accesible){
        this.tresspassable = tresspassable;
        this.name = tileType;
        this.accesible = accesible;
    }
}
