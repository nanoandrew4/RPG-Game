/*
    InMap Model.
    Contains important character data.
 */

package inmap;

public class InMapModel {
    private final Location[] maps;
    private int currentMap;
    private Character[] party;
    private Item[] inv;
    private int gold;
    
    //default constructor
    InMapModel() {
        party = new Character[1];
        party[0] = new Character(1, 10, 10, 90, 10, 10, 10, 10, "Hero", "Human", "NA");
        currentMap = 0;
        maps = new Location[1];
        maps[0] = new Location("city", 2, party);
//        switch((int)(Math.random()*3)) {
//            case 0: maps[0] = new Location("tower", (int)(Math.random()*3+1), party); break;
//            case 1: maps[0] = new Location("dungeon", (int)(Math.random()*3+1), party); break;
//            case 2: maps[0] = new Location("cave", (int)(Math.random()*3+1), party); break;
//        }
        
        maps[currentMap].getCurrentFloor().passControl(Direction.Up);
        inv = new Item[64];
        
        //testing
        inv[0] = new Item("thing");
        inv[1] = new Item("thing2");
        inv[3] = new Item("fish");
        gold = 500;
    }
    
    //process input
    void process(Direction direction) {
        maps[currentMap].process(direction);
    }
    
    //reset location
    void reset() {
        party[0].currentHP = party[0].maxHP;
        party[0].exists = true;
        switch((int)(Math.random()*3)) {
            case 0: maps[0] = new Location("tower", (int)(Math.random()*3+1), party); break;
            case 1: maps[0] = new Location("dungeon", (int)(Math.random()*3+1), party); break;
            case 2: maps[0] = new Location("cave", (int)(Math.random()*3+1), party); break;
        }
        maps[currentMap].getCurrentFloor().passControl(Direction.Up);
    }
    
    //return current map
    Location getCurrentLocation() {
        return maps[currentMap];
    }
    
    //return party
    Character[] getParty() {
        return party;
    }
    
    //return inventory
    Item[] getInventory() {
        return inv;
    }
    
    //return gold
    int getGold() {
        return gold;
    }
}