package overworld;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

class Tile implements java.io.Serializable {

    boolean tresspassable;
    boolean accessible; // for entering cities
    String type;
    SettlementTile settlementTile;
    transient Pane banner;

    InMapTile inMapTile;

    Tile() {
        tresspassable = false;
        accessible = false;
        type = "";
    }

    // for static tiles
    Tile(String tileType) {
        this.type = tileType;
        if (tileType.contains("Water") || tileType.equals("Mountain")) {
            tresspassable = false;
            accessible = false;
        } else {
            tresspassable = true;
            accessible = false;
        }
    }

    // for dungeons
    Tile(String type, String subtype) {
        this.type = type;
        this.tresspassable = true;
        this.accessible = true;
        inMapTile = new InMapTile(subtype);
    }

    // for settlements
    Tile(String type, String subType, char branch, String name, int relationship) {
        this.type = type;
        this.accessible = true;
        this.tresspassable = true;
        settlementTile = new SettlementTile(subType, branch, name, (byte)relationship);
    }

    // set tresspasaibility and accessibility based on tile type

}

class InMapTile extends Tile implements java.io.Serializable {

    String inmapType;

    InMapTile(String inmapType) {
        this.inmapType = inmapType;
    }
}

class SettlementTile extends Tile implements java.io.Serializable {

    public String subType; // village, city...
    public char branch; // m for military or c for commercial
    public byte relationship;
    //public String faction;
    public String settlementName;

    float capital;
    int population;
    int[] resources;
    float avgHappiness;

    SettlementTile(String subType, char branch, String name, byte relationship) {
        accessible = true;
        tresspassable = false;
        this.settlementName = name;
        this.subType = subType;
        this.branch = branch;
        this.relationship = relationship;

        avgHappiness = 0;
        capital = 0;
        population = 0;
        resources = new int[5];
    }
}
