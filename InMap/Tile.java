/*
    InMap Model Tile class.
 */

package inmap;

public class Tile {
    boolean isWall, openable;
    String name;
    int floorMovement;
    
    //constructor given entity name
    Tile(String entity) {
        isWall = false;
        openable = false;
        floorMovement = 0;
        
        switch(entity) {
            case "wall": isWall = true; name = "Wall"; break;
            case "stairsUp": floorMovement = 1; name = "Stairs"; break;
            case "stairsDown": floorMovement = -1; name = "Stairs"; break;
            case "door": openable = true; isWall = true; name = "Door"; break;
            default: break;
        }
    }
    
    //empty constructor
    Tile() {
        isWall = false;
        openable = false;
        floorMovement = 0;
        name = "";
    }
}
