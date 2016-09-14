package overworld;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class Tile {

    boolean tresspassable;
    boolean accessible; // for entering cities
    String type;
    SettlementTile settlementTile;
    Pane banner;

    Tile(){
        tresspassable = false;
        accessible = false;
        type = "";
    }

    // for static tiles
    Tile(String tileType){
        this.type = tileType;
    }

    // for settlements
    Tile(String type, String subType, String branch, String name, int relationship){
        this.type = type;
        settlementTile = new SettlementTile(subType, branch, name, relationship);
    }

    // set tresspasaibility and accessibility based on tile type

}

class SettlementTile extends Tile {

    public String subType; // village, city...
    public String branch; // m for military or c for commercial
    public int relationship;
    public String faction;
    public boolean capitalSettlement;
    public String settlementName;
    public CityPolitics cityPolitics = new CityPolitics(); // read from SQL database

    SettlementTile(){
        accessible = true;
        tresspassable = false;
        settlementName = "";
        capitalSettlement = false;
    }

    SettlementTile(String subType, String branch, String name, int relationship){
        accessible = true;
        tresspassable = false;
        this.settlementName = name;
        this.subType = subType;
        this.branch = branch;
        this.relationship = relationship;
        this.capitalSettlement = false;
    }

    public void setCapitalSettlement(){
        capitalSettlement = true;
    }
}

class CityPolitics {
    float avgHappiness = 0;
    int maxNobles; // max amount of nobles that can be on the council at once
    double growth; // rate of growth of a city
    double capital;
    int population;
    int[] resources; // for the 5? types of resources

    List<String> nobles = new ArrayList<>();

    // TODO: list of nobles
    // TODO: COURT SYSTEM (so much work...) use SQLite for saving finds by kingdom, city, and name for fast lookup
    // TODO: Implement random and non-random events (scandals(levels?), assassination)

    CityPolitics(){
        avgHappiness = 0;
        maxNobles = 0;
        growth = 0d;
        capital = 0d;
        population = 0;
        resources = new int[5];
    }

    CityPolitics(double avgHappiness, int maxNobles, double growth, double capital, int population, int[] resources){

    }

    public void buyLot(){

    }

    public void buildOnLot(){

    }

    public void demolishOnLot(){

    }

    public void sellLot(){

    }

    public void assasination(/* Target char, Assassin char */){

    }
}

