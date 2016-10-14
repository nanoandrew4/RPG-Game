/*
    InMap Model.
    Contains important character data.
 */

package inmap;

import java.awt.Point;
import java.util.HashMap;

import main.Control;
import main.Path;

class InMapModel {
    //saved variables
    private final HashMap<Point, Location> maps;
    private Point currentMap;
    private Character[] party;
    private Item[] inv;
    private int gold;
    
    //temporary variables
    private String focus, menuWindow;
    private Point menuP; //menu cursor pointer
    private boolean qiVisible; //quick info window
    
    //default constructor
    InMapModel() {
        party = new Character[1];
        party[0] = new Character(1, 10, 10, 90, 10, 10, 10, 10, "Hero", "Human", "NA", false);
        currentMap = new Point(0, 0);
        maps = new HashMap();
        maps.put(new Point(0, 0), new Location(this, "tower", (int)(Math.random()*3+1), party));
        
//        switch((int)(Math.random()*3)) {
//            case 0: maps[0] = new Location(this, "tower", (int)(Math.random()*3+1), party); break;
//            case 1: maps[0] = new Location(this, "dungeon", (int)(Math.random()*3+1), party); break;
//            case 2: maps[0] = new Location(this, "cave", (int)(Math.random()*3+1), party); break;
//        }
        
        maps.get(currentMap).getCurrentFloor().passControl(Control.UP);
        inv = new Item[64];
        
        for(int i = 0; i < 64; i++)
            inv[i] = new Item();
        
        //testing
        inv[0] = new Item("thing");
        inv[1] = new Item("thing2");
        inv[3] = new Item("fish");
        inv[10] = new Item("comb");
        inv[30] = new Item("Great Knight Halberd");
        gold = 500;
        focus = "floor";
        menuWindow = "inv";
        menuP = new Point(0, -1);
    }
    
    //process input
    void process(Control input) {
        if(focus.equals("floor")) {
            switch (input) {
                case MENU:
                    toggleMenu(true);
                    break;
                case TAB:
                    qiVisible = true;
                    break;
                case R:
                    reset();
                    break;
                case T:
                    party[0].gainEXP(10000);
                    break;
                default:
                    maps.get(currentMap).process(input);
                    maps.get(currentMap).getCurrentFloor().processAI();
                    break;
            }
        }
        else if(focus.equals("menu")) {
            switch(input) {
                case LEFT:
                    //shift page left
                    if(menuP.y == -1) {
                        if(menuWindow.equals("inv")) menuWindow = "options";
                        else if(menuWindow.equals("char")) menuWindow = "inv";
                        else if(menuWindow.equals("party")) menuWindow = "char";
                        else if(menuWindow.equals("notes")) menuWindow = "party";
                        else if(menuWindow.equals("options")) menuWindow = "notes";
                        else menuWindow = "inv";
                    }
                    else
                        menuP.x--;
                    
                    if(menuWindow.equals("inv") && menuP.x < 0)
                        menuP.x = 3;
                    break;
                    
                case RIGHT:
                    //shift page right
                    if(menuP.y == -1) {
                        if(menuWindow.equals("inv")) menuWindow = "char";
                        else if(menuWindow.equals("char")) menuWindow = "party";
                        else if(menuWindow.equals("party")) menuWindow = "notes";
                        else if(menuWindow.equals("notes")) menuWindow = "options";
                        else if(menuWindow.equals("options")) menuWindow = "inv";
                    }
                    else 
                        menuP.x++;
                    
                    if(menuWindow.equals("inv") && menuP.x > 3)
                        menuP.x = 0;
                    break;
                    
                case UP:
                    if(menuP.y == -1) break;
                    
                    menuP.y--;
                    
                    if(menuWindow.equals("inv") && menuP.y < 0)
                        menuP.y = 15;                 
                    break;
                    
                case DOWN:
                    if(menuP.y != -1)
                        menuP.y++;
                    
                    if(menuWindow.equals("inv") && menuP.y > 15)
                        menuP.y = 0;
                    break;
                    
                case MENU:
                    toggleMenu(false);
                    menuP.y = -1;
                    menuP.x = 0;
                    break;
                    
                case SELECT:
                    menuP.y = 0;
                    menuP.x = 0;
                    break;
                    
                case BACK:
                    if(menuP.y == -1) {
                        toggleMenu(false);
                    }
                    else {
                        menuP.y = -1;
                        menuP.x = 0;
                    }
                    break;
                    
                default:
                    break;
            }
        }
        else System.out.println("Failed focus.");
    }
    
    //process release of input
    public void processRelease(Control input) {
        if(input == Control.TAB) {
            qiVisible = false;
        }
    }
    
    //switch menu focus
    void toggleMenu() { focus = focus.equals("menu") ? "floor" : "menu"; }
    void toggleMenu(boolean on) { focus = on ? "menu" : "floor"; }
    
    //debug: reset location
    void reset() {
        party[0].currentHP = party[0].maxHP;
        party[0].exists = true;
        Location temp;
        switch((int)(Math.random()*3)) {
            case 0: temp = new Location(this, "tower", (int)(Math.random()*3+1), party); break;
            case 1: temp = new Location(this, "dungeon", (int)(Math.random()*3+1), party); break;
            case 2: temp = new Location(this, "cave", (int)(Math.random()*3+1), party); break;
            default: temp = new Location(this, "city", 2, party); break;
        }
        maps.replace(new Point(0, 0), temp);
        maps.get(currentMap).getCurrentFloor().passControl(Control.UP);
    }
    
    //make a dungeon
    void makeDungeon(Point p, String type) {
        if(!maps.containsKey(p))
            maps.put(p, new Location(this, type, (int)(Math.random()*3+1), party));
    }
    
    //getters
    Location getCurrentLocation() { return maps.get(currentMap); }
    Location getLocation(Point id) { return maps.get(id); }
    Character[] getParty() { return party; }
    Item[] getInventory() { return inv; }
    int getGold() { return gold; }
    Point getMenuPoint() { return menuP; }
    String getMenuWindow() { return menuWindow; }
    String getFocus() { return focus; }
    boolean getQIVisible() { return qiVisible; }
}