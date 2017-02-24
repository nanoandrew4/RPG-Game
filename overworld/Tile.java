package overworld;

import java.util.Random;

class Tile implements java.io.Serializable {

    boolean tresspassable;
    boolean accessible; // for entering cities
    String type;
    SettlementTile settlementTile;
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
    Tile(String type, String subType, String name, int relationship) {
        this.type = type;
        this.accessible = true;
        this.tresspassable = true;
        settlementTile = new SettlementTile(subType, name, (byte)relationship);
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
    public byte relationship;
    //public String faction;
    public String settlementName;

    int population;
    int[] resources; // wood, stone, iron, gold
    float avgHappiness;

    SettlementTile(String subType, String name, byte relationship) {
        accessible = true;
        tresspassable = false;
        this.settlementName = name;
        this.subType = subType;
        this.relationship = relationship;
        resources = new int[4];

        Random rand = new Random();

        if (subType.equals("Hamlet")) {
            population = rand.nextInt(25) + 25;
            resources[0] = rand.nextInt(25) + 10; // wood
            resources[1] = rand.nextInt(5); // stone, though not required for upgrade, can exist
            // iron and gold are 0 for this tier
        } else if (subType.equals("Village")) {
            population = rand.nextInt(100) + 25;
            resources[0] = rand.nextInt(50) + 25;
            resources[1] = rand.nextInt(25) + 10;
            // iron and gold are 0 for this tier
        } else if (subType.equals("Town")) {
            population = rand.nextInt(250) + 50;
            resources[0] = rand.nextInt(150) + 50;
            resources[1] = rand.nextInt(50) + 25;
            resources[2] = rand.nextInt(25) + 10;
        } else if (subType.equals("City") || subType.equals("Castle")) {
            population = rand.nextInt(500) + 250;
            resources[0] = rand.nextInt(250) + 150;
            resources[1] = rand.nextInt(150) + 50;
            if (subType.equals("City")) {
                resources[2] = rand.nextInt(50) + 25;
                resources[3] = rand.nextInt(50) + 25;
            } else {
                resources[2] = rand.nextInt(150) + 25;
                resources[3] = rand.nextInt(25) + 10;
            }
        } else {
            population = rand.nextInt(1000) + 500;
            resources[0] = rand.nextInt(500) + 250;
            resources[1] = rand.nextInt(250) + 150;
            if (subType.equals("Metropolis")) {
                resources[2] = rand.nextInt(150) + 150;
                resources[3] = rand.nextInt(150) + 150;
            } else {
                resources[2] = rand.nextInt(250) + 150;
                resources[3] = rand.nextInt(50) + 25;
            }
        }
    }
}
           