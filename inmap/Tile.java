/*
    InMap Model Tile class.
 */

package inmap;

import java.util.HashMap;

class Tile implements java.io.Serializable{
    //static vars
    static transient HashMap<String, Tile> tiles = new HashMap();
    static transient HashMap<Integer, String> idname = new HashMap();
    
    //vars
    boolean isWall, openable;
    int id;
    String name;
    int floorMovement;
    
        //static methods
    static void load(String name, Tile t) {
        tiles.put(name, t);
    }
    
    //constructor given entity name
    Tile(String entity) {
//        Tile t = tiles.get(entity);
//        isWall = t.isWall;
//        openable = t.openable;
//        floorMovement = t.floorMovement;
        
        isWall = false;
        openable = false;
        floorMovement = 0;
        
        switch(entity) {
            case "wall": isWall = true; name = "Wall"; break;
            case "stairsUp": floorMovement = 1; name = "StairsUp"; break;
            case "stairsDown": floorMovement = -1; name = "StairsDown"; break;
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