package game;

import javafx.scene.image.ImageView;

public class settlementTile extends Tile {

    public char subType; // m for military or c for commercial
    public settlementBanner settlementBanner;
    public String faction;
    public cityPolitics cityPolitics = new cityPolitics();

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

class settlementBanner {

    ImageView banner, attack, enter, diplomacy;
    private int relationship;
    private String name;
    private String faction;
    private double parentSize, parentX, parentY;

    settlementBanner(String name, String faction, int relationship, double parentSize, double parentX, double parentY){
        this.name = name;
        this.faction = faction;
        this.parentSize = parentSize;
        updateData(relationship, parentX, parentY);

        banner = new ImageView();
        attack = new ImageView(); // on click listeners will be in controller
        enter = new ImageView();
        diplomacy = new ImageView();
    }

    public void updateData(int relationship,double parentX, double parentY){
        this.relationship = relationship;
        this.parentX = parentX;
        this.parentY = parentY;
    }
}

class cityPolitics {
    float avgHappiness = 0;
    int maxNobles; // max amount of nobles that can be on the council at once
    // TODO: list of nobles
    // TODO: COURT SYSTEM (so much work...) use SQLite for saving finds by kingdom, city, and name for fast lookup
    // TODO: Implement random and non-random events (scandals, assassination)

    cityPolitics(){

    }

    public void buyLot(){

    }

    public void buildOnLot(){

    }

    public void demolishOnLot(){

    }

    public void sellLot(){

    }
}
