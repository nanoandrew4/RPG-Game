package game;

public class settlementTile extends Tile {

    public char subType; // m for military or c for commercial
    public settlementBanner settlementBanner;
    public String faction;

    settlementTile(){
        accesible = true;
        tresspassable = false;
    }

    settlementTile(String type, char subType, int relationship){
        accesible = true;
        tresspassable = false;
        this.type = type;
        this.subType = subType;
        settlementBanner = new settlementBanner(type, faction, relationship, 0d, 0d, 0d);
    }
}
