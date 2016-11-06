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

class InMapModel {
    //saved variables
    private final HashMap<Point, Location> maps;
    private Point currentMap;
    private Character[] party;
    private Item[] inv;
    private int gold;
    
    //temporary variables
    private String focus, menuWindow, invText;
    private final Point tempP, menuP; //menu cursor pointers
    private int selectP; //selection pointer
    private boolean qiVisible; //quick info window
    private boolean menuToggle; //menu toggle
    boolean hasControl;
    
    private DBManager dbManager;
    
    //default constructor
    InMapModel(DBManager dbManager) {
        this.dbManager = dbManager;
        
        //should be loaded in
        party = new Character[5];
        maps = new HashMap();
        inv = new Item[64];
        Arrays.fill(party, new Character());
        Arrays.fill(inv, new Item());
        
        try {
            load();
        } 
        catch (SQLException ex) {
            Logger.getLogger(InMapModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //should be loaded
        party[0] = new Character(1, 10, 10, 90, 10, 10, 10, 10, "Hero", "Human", "NA", new Path(), false);
        currentMap = new Point(0, 0);
        gold = 500;
        
        //testing
//        inv[0] = new Item(items.get("Apple"), 0);
//        inv[1] = new Item(items.get("Fish"), 0);
//        inv[3] = new Item(items.get("Longsword"), 3);
//        inv[8] = new Item(items.get("Leather Vest"), 0);
//        inv[9] = new Item(items.get("Metal Bracelet"), 2);
//        inv[10] = new Item(items.get("Top Hat"), 5);
//        inv[30] = new Item(items.get("Halberd"), 1);
//        inv[40] = new Item(items.get("Kitchen Knife"), 1);
        for(int i = 0; i < (int)(Math.random()*60)+10; i++) {
            inv[(int)(Math.random()*64)] = Item.randomItem(0, null);
        }
        
        //temporary variables
        focus = "floor";
        menuWindow = "inv";
        invText = "";
        menuP = new Point(0, -1);
        tempP = new Point(-1, -1);
        selectP = -1;
        hasControl = false;
    }
    
    //load game data
    private void load() throws SQLException {
        //item data
        assert dbManager != null;
        ResultSet rs = dbManager.selectFromDatabase("ITEM_DATA");
        
        String name, type, des;
        int DMG, HIT, CRT, PRC, VIT, INT, ACC, STR, DEX, WIS, LUK, 
                MHP, CHP, MMP, CMP, DEF, RES, EVA, VAL, RAR;
        
        while(rs.next()) {
            name = rs.getString("NAME");
            type = rs.getString("TYPE");
            des = rs.getString("DESCRIPTION");
            DMG = rs.getInt("DMG");
            HIT = rs.getInt("HIT");
            CRT = rs.getInt("CRT");
            PRC = rs.getInt("PRC");
            VIT = rs.getInt("VIT");
            INT = rs.getInt("INT");
            ACC = rs.getInt("ACC");
            STR = rs.getInt("STR");
            DEX = rs.getInt("DEX");
            WIS = rs.getInt("WIS");
            LUK = rs.getInt("LUK");
            MHP = rs.getInt("MHP");
            CHP = rs.getInt("CHP");
            MMP = rs.getInt("MMP");
            CMP = rs.getInt("CMP");
            DEF = rs.getInt("DEF");
            RES = rs.getInt("RES");
            EVA = rs.getInt("EVA");
            VAL = rs.getInt("VAL");
            RAR = rs.getInt("RAR");
            
            Item.load(new Item(name, type, des, DMG, HIT, 
                    CRT, PRC, VIT, INT, ACC, STR, DEX, WIS, LUK, 
                    MHP, CHP, MMP, CMP, DEF, RES, EVA, VAL, RAR));
        }
    }
    
    //save game data
    private void save() throws SQLException {
        
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
                    toggleMenu(true);
                    menuWindow = "inv";
                    invText = "";
                    break;
                case OPENCHAR:
                    toggleMenu(true);
                    menuWindow = "char";
                    break;
                case OPENPARTY:
                    toggleMenu(true);
                    menuWindow = "party";
                    break;
                case OPENNOTES:
                    toggleMenu(true);
                    menuWindow = "notes";
                    break;
                case OPENOPTIONS:
                    toggleMenu(true);
                    menuWindow = "options";
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
                                if(inv[menuP.x*16+menuP.y].type.equals("consumable")) {
                                    //use on self: default
                                    party[0].currentHP += inv[menuP.x*16+menuP.y].CHP;
                                    if(party[0].currentHP > party[0].maxHP)
                                        party[0].currentHP = party[0].maxHP;
                                    
                                    party[0].currentMP += inv[menuP.x*16+menuP.y].CMP;
                                    if(party[0].currentMP > party[0].maxMP)
                                        party[0].currentMP = party[0].maxMP;
                                    
                                    invText = inv[menuP.x*16+menuP.y].name + " used.";
                                    inv[menuP.x*16+menuP.y].reset();
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
                        toggleMenu(true);
                        menuWindow = "inv";
                    }
                    break;
                    
                case OPENCHAR:
                    toggleMenu(false);
                    if(!menuWindow.equals("char")) {
                        toggleMenu(true);
                        menuWindow = "char";
                    }
                    break;
                    
                case OPENPARTY:
                    toggleMenu(false);
                    if(!menuWindow.equals("party")) {
                        toggleMenu(true);
                        menuWindow = "party";
                    }
                    break;
                    
                case OPENNOTES:
                    toggleMenu(false);
                    if(!menuWindow.equals("notes")) {
                        toggleMenu(true);
                        menuWindow = "notes";
                    }
                    break;
                    
                case OPENOPTIONS:
                    toggleMenu(false);
                    if(!menuWindow.equals("options")) {
                        toggleMenu(true);
                        menuWindow = "options";
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
    
    //switch menu focus
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
    int getSelectPoint() { return selectP; }
    String getMenuWindow() { return menuWindow; }
    String getFocus() { return focus; }
    boolean getQIVisible() { return qiVisible; }
    boolean getMenuToggle() { return menuToggle; }
}