/*
    InMap Model Tile class.
 */

package inmap;

import java.util.HashMap;

class Tile implements java.io.Serializable{
    //static vars
    static HashMap<String, Tile> tiles = new HashMap();
    static HashMap<Integer, String> idname = new HashMap();
    
    //vars
    boolean isWall, openable;
    int id; 
    int floorMovement; //for stairs and other movement tiles
    byte vis; //visibility/opacity: 0-64
    
        //static methods
    //static initializer
    static {
        //load tiles
        load("StoneFloor1", new Tile(false, false, 0, 0));
        load("StoneFloor2", new Tile(false, false, 1, 0));
        load("StoneFloor3", new Tile(false, false, 2, 0));
        load("StoneWall", new Tile(true, false, 3, 0));
        load("Door", new Tile(true, true, 4, 0));
        load("OpenDoor", new Tile(false, false, 5, 0));
        load("StairsUp", new Tile(false, false, 6, 1));
        load("StairsDown", new Tile(false, false, 7, -1));
        load("Grass", new Tile(false, false, 10, 0));
        load("WoodFloor", new Tile(false, false, 11, 0));
        load("WoodWall", new Tile(true, false, 12, 0));
        load("WoodWallDoor", new Tile(true, false, 13, 0));
        load("WoodWallWindow", new Tile(true, false, 14, 0));
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
    
        //normal methods
    //constructor given entity name
    Tile(String entity) {
        Tile t = tiles.get(entity);
        isWall = t.isWall;
        openable = t.openable;
        floorMovement = t.floorMovement;
        id = t.id;
        vis = 0;
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
        vis = 0;
    }
    
    //open openable tiles
    void open() {
        id++;
        isWall = false;
        openable = false;
    }
}