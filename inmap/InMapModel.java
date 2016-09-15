/*
    InMap Model.
    Contains important character data.
 */

package inmap;

public class InMapModel {
    private final Location[] maps;
    private int currentMap;
    private Character[] party;
    
    //default constructor
    InMapModel() {
        party = new Character[1];
        party[0] = new Character(1, 10, 10, 90, 10, 10, 10, 10, "Hero", "Human", "NA");
        currentMap = 0;
        maps = new Location[1];
        maps[0] = new Location("tower", 4, party);
        maps[currentMap].getCurrentFloor().passControl(Direction.Up);
    }
    
    //process input
    void process(Direction direction) {
        maps[currentMap].process(direction);
    }
    
    //reset location
    void reset() {
        party[0].currentHP = party[0].maxHP;
        party[0].exists = true;
        maps[currentMap] = new Location("tower", 4, party);
        maps[currentMap].getCurrentFloor().passControl(Direction.Up);
    }
    
    //return current map
    Location getCurrentLocation() {
        return maps[currentMap];
    }
}