/*
    Tile class for Rising Legend.
 */

package InMap;

public class Tile {
    boolean isWall, stairUp, stairDown, openable;
    
    Tile(String entity) {
        isWall = false;
        stairUp = false;
        stairDown = false;
        openable = false;
        
        switch(entity) {
            case "wall": isWall = true; break;
            case "stairsUp": stairUp = true; break;
            case "stairsDown": stairDown = true; break;
            case "door": openable = true; isWall = true; break;
            default: break;
        }
    }
}
