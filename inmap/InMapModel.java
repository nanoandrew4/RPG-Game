/*
    InMap Model.
    Contains important character data.
 */

package inmap;

import main.Control;
import main.Path;

public class InMapModel {
    private final Location[] maps;
    private int currentMap;
    private Character[] party;
    private Item[] inv;
    private int gold;
    private String focus, menuFocus;
    private int menuX;
    private int menuY;
    
    //default constructor
    InMapModel() {
        party = new Character[1];
        party[0] = new Character(1, 10, 10, 90, 10, 10, 10, 10, "Hero", "Human", "NA", false);
        currentMap = 0;
        maps = new Location[1];
//        maps[0] = new Location(this, "city", 2, party);
        switch((int)(Math.random()*3)) {
            case 0: maps[0] = new Location(this, "tower", (int)(Math.random()*3+1), party); break;
            case 1: maps[0] = new Location(this, "dungeon", (int)(Math.random()*3+1), party); break;
            case 2: maps[0] = new Location(this, "cave", (int)(Math.random()*3+1), party); break;
        }
        
        maps[currentMap].getCurrentFloor().passControl(Control.UP);
        inv = new Item[64];
        
        for(int i = 0; i < 64; i++)
            inv[i] = new Item();
        
        //testing
        inv[0] = new Item("thing");
        inv[1] = new Item("thing2");
        inv[3] = new Item("fish");
        inv[30] = new Item("Great Knight Halberd");
        gold = 500;
        focus = "floor";
        menuFocus = "inv";
        menuX = 0;
        menuY = -1;
    }
    
    //process input
    void process(Control input) {
        if(focus.equals("floor")) {
            maps[currentMap].process(input);
            maps[currentMap].getCurrentFloor().processAI();
        }
        else if(focus.equals("menu")) {
            switch(input) {
                case LEFT:
                    break;
                case RIGHT:
                    break;
                case UP:
                    break;
                case DOWN:
                    break;
                case SELECT:
                    break;
                case BACK:
                    break;
                default:
                    break;
            }
        }
        else System.out.println("Failed focus.");
    }
    
    //switch to menu
    void toggleMenu() {
        if(focus.equals("menu"))
            focus = "floor";
        else focus = "menu";
    }
    
    //debug: reset location
    void reset() {
        party[0].currentHP = party[0].maxHP;
        party[0].exists = true;
        switch((int)(Math.random()*3)) {
            case 0: maps[0] = new Location(this, "tower", (int)(Math.random()*3+1), party); break;
            case 1: maps[0] = new Location(this, "dungeon", (int)(Math.random()*3+1), party); break;
            case 2: maps[0] = new Location(this, "cave", (int)(Math.random()*3+1), party); break;
        }
        maps[currentMap].getCurrentFloor().passControl(Control.UP);
    }
    
    //A* pathfinding
    Path pathfind(Floor floor, int sx, int sy, int ex, int ey) {
        Path path = new Path(floor.sizeX, floor.sizeY, sx, sy, ex, ey);
        
        for(int x = 0; x < floor.sizeX; x++) {
            for(int y = 0; y < floor.sizeY; y++) {
                if((floor.tiles[x][y].isWall && !floor.tiles[x][y].openable) 
                        || (floor.chars[x][y].exists))
                    path.setMap(x, y, 1);
                else
                    path.setMap(x, y, 0);
            }
        }
        
        path.search(sx, sy, ex, ey);
        
        return path;
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