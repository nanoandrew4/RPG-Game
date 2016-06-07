/*
    InMap Model.
 */

package inmap;

public class InMapModel {
    private final Location[] maps;
    private int currentMap;
    
    //default constructor
    InMapModel() {
        maps = new Location[1];
        currentMap = 0;
        maps[0] = new Location();
    }
    
    //process input
    void process(Direction direction) {
        maps[currentMap].process(direction);
    }
    
    //return current map
    Location getCurrentLocation() {
        return maps[currentMap];
    }
}
