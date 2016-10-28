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
    private int selectP; //selection pointer
    private boolean qiVisible; //quick info window
    boolean hasControl;
    
    //default constructor
    InMapModel() {
        party = new Character[1];
        party[0] = new Character(1, 10, 10, 90, 10, 10, 10, 10, "Hero", "Human", "NA", new Path(), false);
        currentMap = new Point(0, 0);
        maps = new HashMap();
        gold = Integer.MAX_VALUE;
        focus = "floor";
        menuWindow = "inv";
        menuP = new Point(0, -1);
        selectP = -1;
        hasControl = false;
        inv = new Item[64];
        
        for(int i = 0; i < 64; i++)
            inv[i] = new Item();
        
        //testing
        for(int i = 0; i < 21; i++) {
            inv[(int)(Math.random()*64)] = new Item("s" + Math.pow(i, 3));
        }
        inv[0] = new Item("thing");
        inv[1] = new Item("thing2");
        inv[3] = new Item("fish");
        inv[10] = new Item("comb");
        inv[30] = new Item("Great Knight Halberd");
    }
    
    //process input
    void process(Control input) {
        //floor input
        if(focus.equals("floor")) {
            switch (input) {
                case MENU:
                    toggleMenu(true);
                    break;
                case BACK:
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
                    if(hasControl)
                        maps.get(currentMap).getCurrentFloor().processAI();
                    break;
            }
        }
        //menu input
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
                    //if not in selection
                    else if(selectP == -1) {
                        menuP.x--;
                    }
                    else
                        break;
                    
                    if(menuWindow.equals("inv") && menuP.x < 0) {
                        menuP.x = 3;
                    }
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
                    //if not in selection
                    else if(selectP == -1) {
                        menuP.x++;
                    }
                    else 
                        break;
                    
                    if(menuWindow.equals("inv") && menuP.x > 3) {
                        menuP.x = 0;
                    }
                    break;
                    
                case UP:
                    //cannot move up in menu page select
                    if(menuP.y == -1)
                        break;
                    //selection movement
                    else if(selectP != -1) {
                        selectP--;
                        
                        if(menuWindow.equals("inv") && selectP < 0) {
                            selectP = 4;
                        }
                    }
                    //menu movement
                    else {
                        menuP.y--;
                        
                        if(menuWindow.equals("inv") && menuP.y < 0) {
                            menuP.y = 15;
                        }
                        else if(menuWindow.equals("char")) {
                            menuP.y = -1;
                        }
                    }
                    break;
                    
                case DOWN:
                    //cannot move up in menu page select
                    if(menuP.y == -1)
                        break;
                    //selection movement
                    else if(selectP != -1) {
                        selectP++;
                        
                        if(menuWindow.equals("inv") && selectP > 4) {
                            selectP = 0;
                        }
                    }
                    else {
                        menuP.y++;

                        if(menuWindow.equals("inv") && menuP.y > 15) {
                            menuP.y = 0;
                        }
                        else if(menuWindow.equals("char")) {
                            menuP.y = -1;
                        }
                    }
                    break;
                    
                case MENU:
                    toggleMenu(false);
                    menuP.y = -1;
                    menuP.x = 0;
                    break;
                    
                case SELECT:
                    if(menuWindow.equals("inv")) {
                        if(menuP.y == -1) {
                            menuP.x = 0;
                            menuP.y = 0;
                        }
                        else if(selectP != -1) {
                            
                        }
                        else {
                            selectP = 0;
                        }
                    }
                    break;
                    
                case BACK:
                    if(menuP.y == -1) {
                        toggleMenu(false);
                    }
                    else if(selectP != -1) {
                        selectP = -1;
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
    
    //set current map
    void setCurrentMap(Point p) {
        currentMap = p;
        getCurrentLocation().currentFloor = 0;
        getCurrentLocation().getCurrentFloor().passControl(Control.UP);
    }
    
    //make a location
    void makeLocation(Point p, String type) {
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
    int getSelectPoint() { return selectP; }
    String getMenuWindow() { return menuWindow; }
    String getFocus() { return focus; }
    boolean getQIVisible() { return qiVisible; }
}