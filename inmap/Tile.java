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
    int id, floorMovement;
    
        //static methods
    //static initializer
    static {
        //load tiles
        load("StoneFloor", new Tile(false, false, 0, 0));
        load("StoneTop", new Tile(true, false, 1, 0));
        load("StoneWall", new Tile(true, false, 2, 0));
        load("Door", new Tile(true, true, 3, 0));
        load("OpenDoor", new Tile(false, false, 4, 0));
        load("StairsUp", new Tile(false, false, 5, 1));
        load("StairsDown", new Tile(false, false, 6, -1));
    }
    
    //load tile
    static void load(String name, Tile t) {
        tiles.put(name, t);
        idname.put(t.id, name);
    }
    
    //get tile template given id
    static Tile get(int id) {
        return tiles.get(idname.get(id));
    }
    
    //constructor given entity name
    Tile(String entity) {
        Tile t = tiles.get(entity);
        isWall = t.isWall;
        openable = t.openable;
        floorMovement = t.floorMovement;
        id = t.id;
    }
    
    //constructor for loading
    Tile(boolean isWall, boolean openable, int id, int floorMovement) {
        this.isWall = isWall;
        this.openable = openable;
        this.id = id;
        this.floorMovement = floorMovement;
    }
    
    //default constructor
    Tile() {
        isWall = false;
        openable = false;
        floorMovement = 0;
        id = -1;
    }
    
    //open openable tiles
    void open() {
        id++;
        isWall = false;
        openable = false;
    }
}