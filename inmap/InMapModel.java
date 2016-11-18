/*
    InMap Model.
    Contains important character data.
 */

package inmap;

import java.awt.Point;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.*;

public class InMapModel implements java.io.Serializable{
    //saved variables
    private final HashMap<Point, Location> maps;
    private Point currentMap;
    private Character[] party;
    private Item[] inv;
    private int gold;
    
    //temporary variables
    private transient String focus, menuWindow, invText;
    private transient final Point tempP, menuP; //menu cursor pointers
    private transient int useP, usePmax, selectP; //selection pointer
    private transient boolean qiVisible; //quick info window
    private transient boolean menuToggle; //menu toggle
    transient boolean hasControl;
    
    //new game stuff
    InMapModel() {
        DBManager dbManager = new DBManager("IMDATA");
        
        //should be loaded in
        party = new Character[5];
        maps = new HashMap();
        inv = new Item[64];
        Arrays.fill(party, new Character());
        Arrays.fill(inv, new Item());
        
        party[0] = new Character(1, 10, 10, 90, 10, 10, 10, 10, "Hero", "Human", "NA", new Path(), false);
        currentMap = new Point(0, 0);
        gold = 500;
        
        try {
            load(dbManager);
        }
        catch(SQLException s) {
            
        }
        
        //testing
        for(int i = 0; i < (int)(Math.random()*60)+10; i++) {
            inv[(int)(Math.random()*64)] = Item.randomItem(0, null);
        }
        
        //temporary variables
        focus = "floor";
        menuWindow = "inv";
        invText = "";
        menuP = new Point(0, -1);
        tempP = new Point(-1, -1);
        useP = -1;
        selectP = -1;
        hasControl = false;
    }
    
    //load game data
    private void load(DBManager dbManager) throws SQLException {
        //item template data
        ResultSet rs = dbManager.selectFromDatabase("ITEM_DATA");
        short id = 0;
        while(rs.next()) {
            Item.load(new Item(
                rs.getString("NAME"), rs.getString("TYPE"),
                rs.getString("DESCRIPTION"),
                rs.getInt("DMG"), rs.getInt("HIT"),
                rs.getInt("CRT"), rs.getInt("PRC"),
                rs.getInt("VIT"), rs.getInt("INT"),
                rs.getInt("ACC"), rs.getInt("STR"),
                rs.getInt("DEX"), rs.getInt("WIS"),
                rs.getInt("LUK"), rs.getInt("MHP"),
                rs.getInt("CHP"), rs.getInt("MMP"),
                rs.getInt("CMP"), rs.getInt("DEF"),
                rs.getInt("RES"), rs.getInt("EVA"),
                rs.getInt("VAL"), rs.getByte("RAR")), id);
            
            id++;
        }
//        
//        //inventory data
//        rs = dbManager.selectFromDatabase("INV_DATA");
//        byte index = 0;
//        while(rs.next()) {
//            if(rs.getShort("ID") == -1)
//                inv[index] = new Item();
//            else {
//                inv[index] = new Item(Item.get(rs.getShort("ID")), rs.getByte("RARITY"), rs.getByte("LVL"));
//            }
//            index++;
//        }
    }
    
    //save game data
//    private void save() throws SQLException {
//        dbManager.setAutoCommit(false);
//        
//        //item data
//        dbManager.deleteTable("INV_DATA");
//        dbManager.createTables("INV_DATA");
//        
//        for(int i = 0; i < 64; i++) {
//            if(inv[i].exists)
//                dbManager.insertIntoTable_INV_DATA(Item.getID(inv[i].name), inv[i].rarity, inv[i].LVL);
//            else
//                dbManager.insertIntoTable_INV_DATA((short)-1, (byte)-1, (byte)-1);
//        }
//        
//        dbManager.commit();
//        dbManager.setAutoCommit(true);
//    }
    
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
                case TOGGLE:
                    qiVisible = true;
                    break;
                case R:
                    reset();
                    break;
                case T:
                    party[0].gainEXP(10000);
                    break;
                case OPENINV:
                    toggleMenu("inv");
                    break;
                case OPENCHAR:
                    toggleMenu("char");
                    break;
                case OPENPARTY:
                    toggleMenu("party");
                    break;
                case OPENNOTES:
                    toggleMenu("notes");
                    break;
                case OPENOPTIONS:
                    toggleMenu("options");
                    break;
                default:
                    maps.get(currentMap).process(input);
                    if(hasControl)
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
                    }
                    //temporary pointer
                    else if(tempP.x != -1) {
                        tempP.x--;

                        if(tempP.x < 0)
                            tempP.x = 3;

                        invText = inv[tempP.x*16+tempP.y].des;
                    }
                    //use pointer
                    else if(useP != -1) {
                        break;
                    }
                    //selection: useless
                    else if(selectP != -1) {
                        break;
                    }
                    //scroll around menu
                    else {
                        menuP.x--;

                        //OOB
                        if(menuWindow.equals("inv") && menuP.x < 0) {
                            menuP.x = 3;
                        }
                        
                        invText = inv[menuP.x*16+menuP.y].des;
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
                    //temporary pointer
                    else if(tempP.x != -1) {
                        tempP.x++;

                        if(tempP.x > 3)
                            tempP.x = 0;
                        
                        invText = inv[tempP.x*16+tempP.y].des;
                    }
                    //use pointer
                    else if(useP != -1) {
                        break;
                    }
                    //selection
                    else if(selectP != -1) {
                        break;
                    }
                    //scroll around menu
                    else {
                        menuP.x++;

                        //OOB
                        if(menuWindow.equals("inv") && menuP.x > 3) {
                            menuP.x = 0;
                        }
                        
                        invText = inv[menuP.x*16+menuP.y].des;
                    }
                    break;
                    
                case UP:
                    //cannot move up in menu page select
                    if(menuP.y == -1) {
                        break;
                    }
                    //temp pointer
                    else if(tempP.x != -1) {
                        tempP.y--;
                        
                        if(tempP.y < 0)
                            tempP.y = 15;
                        
                        invText = inv[tempP.x*16+tempP.y].des;
                    }
                    //use pointer
                    else if(useP != -1) {
                        useP--;
                        
                        if(useP < 0) {
                            useP = usePmax;
                        }
                    }
                    //selection movement
                    else if(selectP != -1) {
                        selectP--;
                        
                        if(menuWindow.equals("inv") && selectP < 0) {
                            selectP = 3;
                        }
                    }
                    //scroll around menu
                    else {
                        menuP.y--;
                        
                        //OOB
                        if(menuWindow.equals("inv") && menuP.y < 0) {
                            menuP.y = 15;
                        }
                        else if(menuWindow.equals("char")) {
                            menuP.y = -1;
                        }
                        
                        invText = inv[menuP.x*16+menuP.y].des;
                    }
                    break;
                    
                case DOWN:
                    //move down in menu page select goes into menu
                    if(menuP.y == -1) {
                        break;
                    }
                    //temp pointer
                    else if(tempP.x != -1) {
                        tempP.y++;
                        
                        if(tempP.y > 15)
                            tempP.y = 0;
                        
                        invText = inv[tempP.x*16+tempP.y].des;
                    }
                    //use pointer
                    else if(useP != -1) {
                        useP++;
                        
                        if(useP > usePmax) {
                            useP = 0;
                        }
                    }
                    //selection movement
                    else if(selectP != -1) {
                        selectP++;
                        
                        if(menuWindow.equals("inv") && selectP > 3) {
                            selectP = 0;
                        }
                    }
                    //scroll around menu
                    else {
                        menuP.y++;

                        if(menuWindow.equals("inv") && menuP.y > 15) {
                            menuP.y = 0;
                        }
                        else if(menuWindow.equals("char")) {
                            menuP.y = -1;
                        }
                        
                        invText = inv[menuP.x*16+menuP.y].des;
                    }
                    break;
                    
                case MENU:
                    toggleMenu(false);
                    break;
                    
                case SELECT:
                    //for inventory
                    if(menuWindow.equals("inv")) {
                        //enter menu
                        if(menuP.y == -1) {
                            menuP.move(0, 0);
                            invText = inv[0].des;
                        }
                        //button selection
                        else if(selectP != -1) {
                            //use or equip
                            if(selectP == 0) {
                                if(useP != -1) {
                                    
                                }
                                else if(inv[menuP.x*16+menuP.y].type.equals("consumable")) {
                                    //open use menu
//                                    if(useP == -1) {
//                                        useP = 0;
//                                    }
//                                    else {
                                        //use on self: default
                                        party[0].currentHP += inv[menuP.x*16+menuP.y].CHP;
                                        if(party[0].currentHP > party[0].maxHP)
                                            party[0].currentHP = party[0].maxHP;

                                        party[0].currentMP += inv[menuP.x*16+menuP.y].CMP;
                                        if(party[0].currentMP > party[0].maxMP)
                                            party[0].currentMP = party[0].maxMP;

                                        invText = inv[menuP.x*16+menuP.y].name + " used.";
                                        inv[menuP.x*16+menuP.y].reset();
//                                    }
                                }
                                else if(inv[menuP.x*16+menuP.y].type.equals("weapon")) {
                                    //swap items
                                    Item temp = inv[menuP.x*16+menuP.y];
                                    inv[menuP.x*16+menuP.y] = party[0].weapon;
                                    party[0].weapon = temp;
                                    invText = party[0].weapon.name + " equipped.";
                                }
                                else if(inv[menuP.x*16+menuP.y].type.equals("armor")) {
                                    //swap items
                                    Item temp = inv[menuP.x*16+menuP.y];
                                    inv[menuP.x*16+menuP.y] = party[0].armor;
                                    party[0].armor = temp;
                                    invText = party[0].armor.name + " equipped.";
                                }
                                else if(inv[menuP.x*16+menuP.y].type.equals("accessory")) {
                                    //insert in first
                                    if(!party[0].acc1.exists) {
                                        party[0].acc1 = inv[menuP.x*16+menuP.y];
                                        invText = party[0].acc1.name + " equipped.";
                                        inv[menuP.x*16+menuP.y] = new Item();
                                    }
                                    //second
                                    else if(!party[0].acc2.exists) {
                                        party[0].acc2 = inv[menuP.x*16+menuP.y];
                                        invText = party[0].acc2.name + " equipped.";
                                        inv[menuP.x*16+menuP.y] = new Item();
                                    }
                                    //third
                                    else if(!party[0].acc3.exists) {
                                        party[0].acc3 = inv[menuP.x*16+menuP.y];
                                        invText = party[0].acc3.name + " equipped.";
                                        inv[menuP.x*16+menuP.y] = new Item();
                                    }
                                    //swap with first
                                    else {
                                        Item temp = inv[menuP.x*16+menuP.y];
                                        inv[menuP.x*16+menuP.y] = party[0].acc1;
                                        party[0].acc1 = temp;
                                        invText = party[0].acc1.name + " equipped.";
                                    }
                                }
                                else if(inv[menuP.x*16+menuP.y].type.equals("material")) {
                                    invText = "You can't use " + inv[menuP.x*16+menuP.y].name + ".";
                                }
                                
                                selectP = -1;
                            }
                            //move
                            else if(selectP == 1) {
                                //selected swap item
                                if(tempP.x != -1) {
                                    //swap
                                    Item temp = inv[tempP.x*16+tempP.y];
                                    inv[tempP.x*16+tempP.y] = inv[menuP.x*16+menuP.y];
                                    inv[menuP.x*16+menuP.y] = temp;
                                    tempP.move(-1, -1);
                                    selectP = -1;
                                }
                                else {
                                    tempP.move(0, 0);
                                }
                            }
                            //discard
                            else if(selectP == 2) {
                                invText = inv[menuP.x*16+menuP.y].name + " dropped.";
                                inv[menuP.x*16+menuP.y].reset();
                                selectP = -1;
                            }
                            //cancel
                            else if(selectP == 3) {
                                selectP = -1;
                            }
                        }
                        else {
                            selectP = 0;
                        }
                    }
                    break;
                    
                case BACK:
                    //menu pages
                    if(menuP.y == -1) {
                        toggleMenu(false);
                    }
                    //temp pointer exists
                    else if(tempP.x != -1) {
                        tempP.move(-1, -1);
                        selectP = -1;
                    }
                    //button selection
                    else if(selectP != -1) {
                        selectP = -1;
                    }
                    //scrolling around
                    else {
                        menuP.move(0, -1);
                        menuToggle = false;
                    }
                    break;
                    
                case SWITCH:
                    if(menuWindow.equals("inv") && selectP == -1) {
                        sortInventory();
                        if(menuP.y != -1)
                            invText = inv[menuP.x*16+menuP.y].des;
                    }
                    break;
                    
                case TOGGLE:
                    if(menuWindow.equals("inv") && menuP.y != -1)
                        menuToggle = !menuToggle;
                    break;
                    
                case OPENINV:
                    toggleMenu(false);
                    if(!menuWindow.equals("inv")) {
                        toggleMenu("inv");
                    }
                    break;
                    
                case OPENCHAR:
                    toggleMenu(false);
                    if(!menuWindow.equals("char")) {
                        toggleMenu("char");
                    }
                    break;
                    
                case OPENPARTY:
                    toggleMenu(false);
                    if(!menuWindow.equals("party")) {
                        toggleMenu("party");
                    }
                    break;
                    
                case OPENNOTES:
                    toggleMenu(false);
                    if(!menuWindow.equals("notes")) {
                        toggleMenu("notes");
                    }
                    break;
                    
                case OPENOPTIONS:
                    toggleMenu(false);
                    if(!menuWindow.equals("options")) {
                        toggleMenu("options");
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
        if(input == Control.TOGGLE) {
            qiVisible = false;
        }
    }
    
    //switch menu focus, reset vars on close
    void toggleMenu(boolean on) {
        if(on)
            focus = "menu";
        else {
            focus = "floor";
            menuP.move(0, -1);
            tempP.move(-1, -1);
            selectP = -1;
            menuToggle = false;
        }
    }
    
    void toggleMenu(String window) {
        focus = "menu";
        menuWindow = window;
    }
    
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
    
    //sort inventory
    void sortInventory() {
        //close gaps
        for(int x = 0; x < 64; x++) {
            if(!inv[x].exists) {
                for(int y = 63; y >= 0; y--) {
                    if(inv[y].exists) {
                        swapInventory(x, y);
                    }
                    else if(y <= x) {
                        x = 64;
                        break;
                    }
                }
            }
        }
        
        //brute force sort
        for(int i = 0; i < 63; i++) {
            for(int j = 0; j < 63; j++) {
                if(!inv[j+1].exists) {
                    break;
                }
                else if(Collator.getInstance().compare(inv[j].displayName, 
                        inv[j+1].displayName) > 0) {
                    swapInventory(j, j+1);
                }
            }
        }
    }
    
    //switch two items in inventory
    void swapInventory(int i1, int i2) {
        Item temp = inv[i1];
        inv[i1] = inv[i2];
        inv[i2] = temp;
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
    String getInvDes() { return invText; }
    int getGold() { return gold; }
    Point getMenuPoint() { return menuP; }
    Point getTempPoint() { return tempP; }
    int getUsePoint() { return useP; }
    int getSelectPoint() { return selectP; }
    String getMenuWindow() { return menuWindow; }
    String getFocus() { return focus; }
    boolean getQIVisible() { return qiVisible; }
    boolean getMenuToggle() { return menuToggle; }
}