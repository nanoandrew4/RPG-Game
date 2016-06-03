package game;

public class settlementTile extends Tile {

    public char subType; // m for military or c for commercial

    settlementTile(){
        accesible = true;
        tresspassable = false;
    }

    settlementTile(String type, char subType){
        accesible = true;
        tresspassable = false;
        this.type = type;
        this.subType = subType;
    }
}
