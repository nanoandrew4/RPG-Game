/*
    InMap model.
    Contains map and character data.
 */

package inmap;

import java.awt.Point;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import main.*;

public class InMapModel implements java.io.Serializable {
    //saved variables
    private final HashMap<Point, Location> maps; //all maps
    private final HashMap<Character, Item[]> invs; //npc inventories
    private final HashMap<Character, int[]> stacks; //npc stacks
    private Point currentMap; //current map location
    private Character[] party; //[0] is player
    private int visDist; //visibility distance
    private Item[] inv; //inventory: size 64
    private int[] invStacks; //stacks in inventory
    private Item[] tradeInv; //trading inventory
    private int[] tradeStacks; //stacks in trading inventory
    private int gold;
    private String name, portrait; //filepath of portrait
    private int sprite; //sprite number in Images.playerSprites
    
    //temporary variables
    private String focus, menuWindow, invText;
    private boolean tradeState, confirm;
    private final Point tempP, menuP; //menu cursor pointers
    private int useP, usePmax, selectP; //selection pointer
    private int sortType; //sorting types
    private boolean qiVisible; //quick info window
    private boolean menuToggle; //menu toggle
    private boolean running; //holding shift: mostly for movement
    private int talkState; //not talking, talking, selecting
    private int talkSelect; //selection pointer for talking
    private Dialogue dialogue; //current dialogue;
    private String[] talkText; //text in the talk box
    private int talkIndex; //index of talk
    private long timer, talkTimer; //timers
    private ArrayList<String> log; //console log
    private Control facing; //player facing direction
    
    boolean hasControl;
    int saveGame, loadGame;
    boolean rip;
    
    //new game constructor
    InMapModel(int VIT, int INT, int STR, int WIS, int LUK, int CHA, 
            String race, String name, int sprite, String portrait) {
        //init arrays
        party = new Character[5];
        maps = new HashMap();
        invs = new HashMap();
        stacks = new HashMap();
        inv = new Item[64];
        invStacks = new int[64];
        Arrays.fill(party, new Character());
        Arrays.fill(inv, new Item());
        Arrays.fill(invStacks, 0);
        visDist = 7;
        
        //create character
        party[0] = new Character(1, VIT, INT, 90, STR, WIS, LUK, CHA, 
                name, race, AIType.NONE, NPCType.NA, new Path(), false);
        party[0].id = 99;
        this.name = name;
        this.sprite = sprite;
        this.portrait = portrait;
        currentMap = new Point(-1, -1);
        gold = 50;
        
        //load item data
        try {
            DBManager dbManager = new DBManager("IMDATA");
            load(dbManager);
        }
        catch(SQLException e) {
            
        }
        
        //initial items
        party[0].weapon = new Item("Sharp Stick");
        party[0].armor = new Item("Rags");
        inv[0] = new Item("Apple");
        inv[1] = new Item("Bread");
        inv[2] = new Item("Lucky Coin");
        invStacks[0] = 1;
        invStacks[1] = 1;
        invStacks[2] = 1;
        
        //temporary variables
        focus = "floor";
        menuWindow = "inv";
        invText = "";
        menuP = new Point(0, -1);
        tempP = new Point(-1, -1);
        useP = -1;
        selectP = -1;
        hasControl = false;
        saveGame = -1;
        loadGame = -1;
        timer = -1;
        log = new ArrayList();
        facing = Control.DOWN;
        talkSelect = -1;
    }
    
    //quick start game
    InMapModel() {
        //init arrays
        party = new Character[5];
        maps = new HashMap();
        invs = new HashMap();
        stacks = new HashMap();
        inv = new Item[64];
        invStacks = new int[64];
        Arrays.fill(party, new Character());
        Arrays.fill(inv, new Item());
        Arrays.fill(inv, new Item());
        Arrays.fill(invStacks, 0);
        visDist = 7;
        
        //create temp hero
        party[0] = new Character(1, 10, 10, 90, 10, 10, 10, 10, 
                "Hero " + (char)(Math.random()*10+48), "Human", 
                AIType.NONE, NPCType.NA, new Path(), false);
        party[0].id = 99;
        name = party[0].name;
        sprite = 99;
        portrait = "/media/graphics/inmap/portrait.jpg";
        currentMap = new Point(-1, -1);
        gold = 500;
        
        //load item data
        try {
            DBManager dbManager = new DBManager("IMDATA");
            load(dbManager);
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        
        //testing
        for(int i = (int)(Math.random()*30)+20; i >= 0; i--) {
            inv[i] = Item.randomItem(0, null);
            invStacks[i] = 1;
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
        saveGame = -1;
        loadGame = -1;
        timer = -1;
        log = new ArrayList();
        facing = Control.DOWN;
        talkSelect = -1;
    }
    
    //load game data
    private void load(DBManager dbManager) throws SQLException {
        //item template data
        ResultSet rs = dbManager.selectFromDatabase("ITEM_DATA");
        short id = 0;
        while(rs.next()) {
            Item.load(rs.getString("NAME"), new Item(
                id, rs.getString("TYPE"),
                rs.getString("DESCRIPTION"),
                rs.getInt("DMG"), rs.getInt("HIT"),
                rs.getInt("CRT"), rs.getInt("PRC"),
                rs.getInt("VIT"), rs.getInt("INT"),
                rs.getInt("ACC"), rs.getInt("STR"),
                rs.getInt("WIS"), rs.getInt("LUK"),
                rs.getInt("CHA"), rs.getInt("MHP"),
                rs.getInt("CHP"), rs.getInt("MMP"),
                rs.getInt("CMP"), rs.getInt("DEF"),
                rs.getInt("RES"), rs.getInt("EVA"),
                rs.getInt("VAL"), rs.getByte("RAR")));
            
            id++;
        }
    }
    
    //general input process
    int process(Control input) {
        int r = -1;
        
        //floor input
        switch(focus) {
            case "floor":
                //general floor process
                if(talkState == 0) {
                    r = processFloorInput(input);
                }
                //if talking, continue to next
                if(!focus.equals("talk")) {
                    talkText = null;
                    return r;
                }
            case "talk":
                processTalkInput(input);
                return r;
            case "trade":
                processTradeInput(input);
                return r;
            case "menu":
                return processMenuInput(input);
            case "rip":
                if(input == Control.SELECT) {
                    rip = true;
                }
                return -1;
            default:
                System.out.println("Failed focus.");
                return -1;
        }
    }
    
    //process floor input
    private int processFloorInput(Control input) {
        int r = -1;
        
        switch (input) {
            case BACK:
                //default open menu
                toggleMenu(true);
                break;

            case TOGGLE:
                if(timer == -1) {
                    timer = System.currentTimeMillis();
                    qiVisible = !qiVisible;
                }
                break;

            case R:
                //reset location: testing only
                reset();
                break;

            case T:
                //cheat
                party[0].gainEXP(10000);
                break;

            case MENU:
                toggleMenu(true);
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

            case RUN:
                running = true;
                break;

            default:
                //process movement input
                r = maps.get(currentMap).process(input);
                if(input == Control.UP || input == Control.LEFT 
                        || input == Control.RIGHT || input == Control.DOWN)
                    facing = input;

                //process ai if haven't left location
                if(r != 1 && r != 3 && hasControl) {
                    maps.get(currentMap).getCurrentFloor().processAI();
                }
                //pick up item
                if(r >= 1000) {
                    short id = (short)(r % 1000);
                    for(int i = 0; i < 64; i++) {
                        //stacking items
                        if(inv[i].id == id && (inv[i].type == ItemType.CONSUMABLE 
                                || inv[i].type == ItemType.MATERIAL)) {
                            invStacks[i]++;
                            logAdd("Picked up another " + Item.idname.get((short)id) + ".");
                            break;
                        }
                        //no item exists
                        else if(!inv[i].exists) {
                            inv[i] = new Item(Item.get((short)id), (byte)0, (byte)0);
                            logAdd("Picked up " + Item.idname.get((short)id) + ".");
                            break;
                        }
                        //no space
                        else if(i == 63) {
                            getCurrentLocation().getCurrentFloor().items[party[0].x][party[0].y] 
                                    = new Item(Item.get((short)id), (byte)0, (byte)0);
                            logAdd("Inventory full.");
                            break;
                        }
                    }
                }
                //start talking to npc
                else if(r == 3) {
                    focus = "talk";
                    talkState = 1;
                }
                
                //player died
                if(party[0].currentHP <= 0) {
                    focus = "rip";
                }
                break;
        }
        
        return r;
    }
    
    //process conversation input
    private void processTalkInput(Control input) {
        if(talkState == 1) {
            //get text if none exists
            if(dialogue == null) {
                dialogue = Dialogue.getMerchantDialogue();
                talkText = dialogue.getText();
                talkIndex = 0;
                talkTimer = System.currentTimeMillis();
            }
            //input
            switch(input) {
                case SELECT:
                    //select option
                    if(talkSelect >= 0) {
                        dialogue.nextState(talkSelect);
                        talkSelect = -3;
                        talkText = dialogue.getText();
                        talkIndex = 0;
                        talkTimer = System.currentTimeMillis();

                        //start talkSelect if prompted, analyze commands
                        if(talkText[talkIndex].charAt(0) == '$') {
                            talkSelect = 0;
                        }
                        else if(talkText[talkIndex].charAt(0) == '%') {
                            //start trade
                            if(talkText[talkIndex].substring(1).equals("trade")) {
                                focus = "trade";
                                tradeState = true;
                                int dx = 0, dy = 0;
                                switch(facing) {
                                    case UP:
                                        dy = -1;
                                        break;
                                    case LEFT:
                                        dx = -1;
                                        break;
                                    case RIGHT:
                                        dx = 1;
                                        break;
                                    case DOWN:
                                        dy = 1;
                                        break;
                                }
                                loadInventory(getCurrentLocation().getCurrentFloor().
                                        chars[party[0].x+dx][party[0].y+dy]);
                            }
                        }
                        break;
                    }
                    //SELECT and BACK have some of the same processing
                case BACK:
                    if(talkSelect == -3) {
                        talkSelect = -1;
                    }
                    //nothing if selecting
                    if(talkSelect >= 0) {
                        break;
                    }
                    //skip to end of text
                    else if(talkSelect == -1 && System.currentTimeMillis() - talkTimer < 
                            talkText[talkIndex].length() * TextType.getCharDuration()) {
                        talkSelect = -2;
                        break;
                    }
                    //advance to next line
                    else if(talkIndex < talkText.length - 1) {
                        talkSelect = -1;
                        talkIndex++;
                        talkTimer = System.currentTimeMillis();
                        
                        //start talkSelect if prompted, analyze commands
                        if(talkText[talkIndex].charAt(0) == '$') {
                            talkSelect = 0;
                        }
                        else if(talkText[talkIndex].charAt(0) == '%') {
                            //start trade
                            if(talkText[talkIndex].substring(1).equals("trade")) {
                                focus = "trade";
                                tradeState = true;
                                int dx = 0, dy = 0;
                                switch(facing) {
                                    case UP:
                                        dy = -1;
                                        break;
                                    case LEFT:
                                        dx = -1;
                                        break;
                                    case RIGHT:
                                        dx = 1;
                                        break;
                                    case DOWN:
                                        dy = 1;
                                        break;
                                }
                                loadInventory(getCurrentLocation().getCurrentFloor().
                                        chars[party[0].x+dx][party[0].y+dy]);
                            }
                        }
                        
                        break;
                    }
                    
                    //finish conversation
                    dialogue = null;
                    talkText = null;
                    talkIndex = 0;
                    talkState = 0;
                    talkSelect = -1;
                    focus = "floor";
                    break;
                    
                case UP:
                    if(talkSelect >= 0) {
                        talkSelect--;
                        if(talkSelect < 0)
                            talkSelect = 1;
                        break;
                    }
                    
                case DOWN:
                    if(talkSelect >= 0) {
                        talkSelect++;
                        if(talkSelect > 1)
                            talkSelect = 0;
                        break;
                    }
                    
                default:
                    if(talkSelect == -3) {
                        talkSelect = -1;
                    }
                    break;
            }
        }
    }
    
    //process trade input
    private void processTradeInput(Control input) {
        switch(input) {
            case LEFT:
                //shift menu page left
                if(menuP.y == -1) {
                    tradeState = !tradeState;
                }
                //selection
                else if(selectP != -1) {
                    break;
                }
                //scroll around menu
                else {
                    menuP.x--;
                    if(menuP.x < 0)
                        menuP.x = 3;
                    if(tradeState)
                        invText = tradeInv[menuP.x*16+menuP.y].des;
                    else
                        invText = inv[menuP.x*16+menuP.y].des;
                }
                break;

            case RIGHT:
                //shift page right
                if(menuP.y == -1) {
                    tradeState = !tradeState;
                }
                //selection
                else if(selectP != -1) {
                    break;
                }
                //scroll around menu
                else {
                    menuP.x++;
                    if(menuP.x > 3)
                        menuP.x = 0;
                    if(tradeState)
                        invText = tradeInv[menuP.x*16+menuP.y].des;
                    else
                        invText = inv[menuP.x*16+menuP.y].des;
                }
                break;

            case UP:
                //cannot move up in menu page select
                if(menuP.y == -1) {
                    break;
                }
                //selection movement
                else if(selectP != -1) {
                    selectP--;
                    if(selectP < 0) {
                        selectP = 1;
                    }
                }
                //scroll around menu
                else {
                    menuP.y--;
                    if(menuP.y < 0)
                        menuP.y = 15;
                    if(tradeState)
                        invText = tradeInv[menuP.x*16+menuP.y].des;
                    else
                        invText = inv[menuP.x*16+menuP.y].des;
                }
                break;

            case DOWN:
                //nothing
                if(menuP.y == -1) {
                    break;
                }
                //selection movement
                else if(selectP != -1) {
                    selectP++;
                    if(selectP > 1) {
                        selectP = 0;
                    }
                }
                //scroll around menu
                else {
                    menuP.y++;
                    if(menuP.y > 15)
                        menuP.y = 0;
                    if(tradeState)
                        invText = tradeInv[menuP.x*16+menuP.y].des;
                    else
                        invText = inv[menuP.x*16+menuP.y].des;
                }
                break;

            case SELECT:
                //enter menu
                if(menuP.y == -1) {
                    menuP.move(0, 0);
                    if(tradeState)
                        invText = tradeInv[0].des;
                    else
                        invText = inv[0].des;
                }
                //button selection
                else if(selectP != -1) {
                    //buy/sell
                    if(selectP == 0) {
                        //buying
                        if(tradeState) {
                            if(!confirm) {
                                confirm = true;
                                menuToggle = false;
                                invText = "Are you sure you want to purchase " 
                                        + tradeInv[menuP.x*16+menuP.y].displayName 
                                        + "? It will cost " 
                                        + tradeInv[menuP.x*16+menuP.y].VAL 
                                        + " gold.";
                            }
                            else {
                                confirm = false;
                                selectP = -1;

                                if(gold < tradeInv[menuP.x*16+menuP.y].VAL) {
                                    invText = "Not enough gold.";
                                }
                                else {
                                    //find empty spot
                                    int empty = -1;
                                    for(int i = 0; i < 64; i++) {
                                        if(!inv[i].exists) {
                                            empty = i;
                                            break;
                                        }
                                        else if(inv[i].id == tradeInv[menuP.x*16+menuP.y].id 
                                                && (inv[i].type == ItemType.CONSUMABLE
                                                || inv[i].type == ItemType.MATERIAL)) {
                                            empty = i;
                                            break;
                                        }
                                    }
                                    //no space left
                                    if(empty == -1) {
                                        invText = "No space in inventory.";
                                        break;
                                    }
                                    //buy
                                    else {
                                        invText = tradeInv[menuP.x*16+menuP.y].displayName + " purchased.";
                                        gold -= tradeInv[menuP.x*16+menuP.y].VAL;
                                        inv[empty] = tradeInv[menuP.x*16+menuP.y].copy();
                                        tradeStacks[menuP.x*16+menuP.y]--;
                                        invStacks[empty]++;
                                        if(tradeStacks[menuP.x*16+menuP.y] <= 0)
                                            tradeInv[menuP.x*16+menuP.y].reset();
                                    }
                                }
                            }
                        }
                        //selling
                        else {
                            if(!confirm) {
                                confirm = true;
                                menuToggle = false;
                                invText = "Are you sure you want to sell " 
                                        + inv[menuP.x*16+menuP.y].displayName 
                                        + " for " 
                                        + inv[menuP.x*16+menuP.y].VAL 
                                        + " gold?";
                            }
                            else {
                                confirm = false;
                                selectP = -1;
                                
                                //find empty spot
                                int empty = -1;
                                for(int i = 0; i < 64; i++) {
                                    if(!tradeInv[i].exists) {
                                        empty = i;
                                        break;
                                    }
                                    else if(tradeInv[i].id == inv[menuP.x*16+menuP.y].id 
                                            && (tradeInv[i].type == ItemType.CONSUMABLE
                                            || tradeInv[i].type == ItemType.MATERIAL)) {
                                        empty = i;
                                        break;
                                    }
                                }
                                if(empty == -1) {
                                    invText = "Merchant cannot buy any more.";
                                    break;
                                }
                                else {
                                    invText = inv[menuP.x*16+menuP.y].displayName + " sold.";
                                    gold += inv[menuP.x*16+menuP.y].VAL;
                                    tradeInv[empty] = inv[menuP.x*16+menuP.y].copy();
                                    tradeStacks[empty]++;
                                    invStacks[menuP.x*16+menuP.y]--;
                                    if(invStacks[menuP.x*16+menuP.y] <= 0)
                                        inv[menuP.x*16+menuP.y].reset();
                                }
                            }
                            
                        }
                    }
                    //cancel
                    else if(selectP == 1) {
                        if(confirm) {
                            confirm = false;
                        if(tradeState)
                            invText = tradeInv[menuP.x*16+menuP.y].des;
                        else
                            invText = inv[menuP.x*16+menuP.y].des;                        }
                        selectP = -1;
                    }
                }
                //start select if item exists
                else {
                    if((tradeState && tradeInv[menuP.x*16+menuP.y].exists) 
                            || (!tradeState && inv[menuP.x*16+menuP.y].exists))
                        selectP = 0;
                }
                break;

            case BACK:
                //menu pages
                if(menuP.y == -1) {
                    focus = "talk";
                    talkIndex++;
                }
                //button selection
                else if(selectP != -1) {
                    confirm = false;
                    selectP = -1;
                    if(tradeState)
                        invText = tradeInv[menuP.x*16+menuP.y].des;
                    else
                        invText = inv[menuP.x*16+menuP.y].des;   
                }
                //back to buy/sell select
                else {
                    menuP.move(0, -1);
                    menuToggle = false;
                }
                break;

            case TOGGLE:
                if(menuP.y != -1)
                    menuToggle = !menuToggle;
                break;

            case SWITCH:
                //sort both inventories
                if(selectP == -1) {
                    sortType++;
                    sortInventory(tradeInv, tradeStacks);
                    sortInventory(inv, invStacks);
                    if(menuP.y != -1) {
                        if(tradeState)
                            invText = tradeInv[menuP.x*16+menuP.y].des;
                        else
                            invText = inv[menuP.x*16+menuP.y].des;
                    }
                }
                break;

            default:
                break;
        }
    }
    
    //process menu input
    private int processMenuInput(Control input) {
        switch(input) {
            case LEFT:
                //shift menu page left
                if(menuP.y == -1) {
                    if(menuWindow.equals("inv")) 
                        menuWindow = "options";
                    else if(menuWindow.equals("char")) 
                        menuWindow = "inv";
                    else if(menuWindow.equals("party")) 
                        menuWindow = "char";
                    else if(menuWindow.equals("notes")) 
                        menuWindow = "party";
                    else if(menuWindow.equals("options")) 
                        menuWindow = "notes";
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
                //selection
                else if(selectP != -1) {
                    if(menuWindow.equals("options")) {
                        selectP += selectP % 2 == 0 ? 1 : -1;
                    }
                }
                //scroll around menu
                else {
                    menuP.x--;

                    if(menuWindow.equals("inv")) {
                        if(menuP.x < 0)
                            menuP.x = 3;
                        invText = inv[menuP.x*16+menuP.y].des;
                    }
                    else if(menuWindow.equals("options")) {
                        menuP.x = 0;
                    }
                }
                break;

            case RIGHT:
                //shift page right
                if(menuP.y == -1) {
                    if(menuWindow.equals("inv")) 
                        menuWindow = "char";
                    else if(menuWindow.equals("char")) 
                        menuWindow = "party";
                    else if(menuWindow.equals("party")) 
                        menuWindow = "notes";
                    else if(menuWindow.equals("notes")) 
                        menuWindow = "options";
                    else if(menuWindow.equals("options")) 
                        menuWindow = "inv";
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
                    if(menuWindow.equals("options")) {
                        selectP += selectP % 2 == 0 ? 1 : -1;
                    }
                }
                //scroll around menu
                else {
                    menuP.x++;

                    if(menuWindow.equals("inv")) {
                        if(menuP.x > 3)
                            menuP.x = 0;
                        invText = inv[menuP.x*16+menuP.y].des;
                    }
                    else if(menuWindow.equals("options")) {
                        menuP.x = 0;
                    }
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
                    else if(menuWindow.equals("options")) {
                        selectP--;

                        if (selectP < 0)
                            selectP += 6;
                    }
                }
                //scroll around menu
                else {
                    menuP.y--;

                    //OOB
                    if(menuWindow.equals("inv")) {
                        if(menuP.y < 0)
                            menuP.y = 15;
                        invText = inv[menuP.x*16+menuP.y].des;
                    }
                    else if(menuWindow.equals("char")) {
                        menuP.y = -1;
                    }
                    else if(menuWindow.equals("options") && menuP.y < 0) {
                        menuP.y = 3;
                    }
                }
                break;

            case DOWN:
                //nothing
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
                    else if(menuWindow.equals("options")) {
                        selectP++;

                        if (selectP > 5)
                            selectP -= 6;
                    }
                }
                //scroll around menu
                else {
                    menuP.y++;

                    if(menuWindow.equals("inv")) {
                        if(menuP.y > 15)
                            menuP.y = 0;
                        invText = inv[menuP.x*16+menuP.y].des;
                    }
                    else if(menuWindow.equals("char")) {
                        menuP.y = -1;
                    }
                    else if(menuWindow.equals("options") && menuP.y > 3) {
                        menuP.y = 0;
                    }
                }
                break;

            case MENU:
                //close menu
                toggleMenu(false);
                break;

            case SELECT:
                //inventory
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
                                //use on selected entity
                            }
                            else if(inv[menuP.x*16+menuP.y].type == ItemType.CONSUMABLE) {
                                //open use menu
                                //if(useP == -1) {
                                //    useP = 0;
                                //}
                                //else {
                                    //use on self: default
                                    party[0].currentHP += inv[menuP.x*16+menuP.y].CHP;
                                    if(party[0].currentHP > party[0].maxHP)
                                        party[0].currentHP = party[0].maxHP;

                                    party[0].currentMP += inv[menuP.x*16+menuP.y].CMP;
                                    if(party[0].currentMP > party[0].maxMP)
                                        party[0].currentMP = party[0].maxMP;

                                    invText = Item.idname.get(inv[menuP.x*16+menuP.y].id) + " used.";
                                    
                                    //if none left, reset item
                                    if(invStacks[menuP.x*16+menuP.y] == 1)
                                        inv[menuP.x*16+menuP.y].reset();
                                    //reduce stack
                                    invStacks[menuP.x*16+menuP.y]--;
                                //}
                            }
                            else if(inv[menuP.x*16+menuP.y].type == ItemType.WEAPON) {
                                //swap items
                                Item temp = inv[menuP.x*16+menuP.y];
                                inv[menuP.x*16+menuP.y] = party[0].weapon;
                                party[0].weapon = temp;
                                party[0].calculateStats();
                                invText = Item.idname.get(party[0].weapon.id) + " equipped.";
                            }
                            else if(inv[menuP.x*16+menuP.y].type == ItemType.ARMOR) {
                                //swap items
                                Item temp = inv[menuP.x*16+menuP.y];
                                inv[menuP.x*16+menuP.y] = party[0].armor;
                                party[0].armor = temp;
                                party[0].calculateStats();
                                invText = Item.idname.get(party[0].armor.id) + " equipped.";
                            }
                            else if(inv[menuP.x*16+menuP.y].type == ItemType.ACCESSORY) {
                                //insert in first
                                if(!party[0].acc1.exists) {
                                    party[0].acc1 = inv[menuP.x*16+menuP.y];
                                    inv[menuP.x*16+menuP.y] = new Item();
                                    party[0].calculateStats();
                                    invText = Item.idname.get(party[0].acc1.id) + " equipped.";
                                }
                                //second
                                else if(!party[0].acc2.exists) {
                                    party[0].acc2 = inv[menuP.x*16+menuP.y];
                                    inv[menuP.x*16+menuP.y] = new Item();
                                    party[0].calculateStats();
                                    invText = Item.idname.get(party[0].acc2.id) + " equipped.";
                                }
                                //third
                                else if(!party[0].acc3.exists) {
                                    party[0].acc3 = inv[menuP.x*16+menuP.y];
                                    inv[menuP.x*16+menuP.y] = new Item();
                                    party[0].calculateStats();
                                    invText = Item.idname.get(party[0].acc3.id) + " equipped.";
                                }
                                //swap with first
                                else {
                                    Item temp = inv[menuP.x*16+menuP.y];
                                    inv[menuP.x*16+menuP.y] = party[0].acc1;
                                    party[0].acc1 = temp;
                                    party[0].calculateStats();
                                    invText = Item.idname.get(party[0].acc1.id) + " equipped.";
                                }
                            }
                            //using a material...
                            else if(inv[menuP.x*16+menuP.y].type == ItemType.MATERIAL) {
                                invText = "You can't use or equip" + 
                                        Item.idname.get(inv[menuP.x*16+menuP.y].id) + "...";
                            }

                            selectP = -1;
                        }
                        //move
                        else if(selectP == 1) {
                            //selected swap item
                            if(tempP.x != -1) {
                                //swap
                                swapInventory(tempP.x*16+tempP.y, menuP.x*16+menuP.y);
                                tempP.move(-1, -1);
                                selectP = -1;
                            }
                            else {
                                tempP.move(0, 0);
                                invText = inv[0].des;
                            }
                        }
                        //discard
                        else if(selectP == 2) {
                            if(hasControl)
                                getCurrentLocation().getCurrentFloor().dropItem(
                                        inv[menuP.x*16+menuP.y].copy(), party[0].x, party[0].y);
                            invText = Item.idname.get(inv[menuP.x*16+menuP.y].id) + " dropped.";
                            inv[menuP.x*16+menuP.y].reset();
                            invStacks[menuP.x*16+menuP.y] = 0;
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
                else if(menuWindow.equals("options")) {
                    //enter menu
                    if(menuP.y == -1) {
                        menuP.move(0, 0);
                    }
                    //save selection
                    else if(selectP != -1) {
                        //save game
                        if(menuP.y == 0) {
                            saveGame = selectP;
                            selectP = -1;
                        }
                        //load game
                        else if(menuP.y == 1) {
                            loadGame = selectP;
                            selectP = -1;
                        }
                    }
                    //enter save selection
                    else if(menuP.y == 0 || menuP.y == 1) {
                        selectP = 0;
                    }
                    //options
                    else if(menuP.y == 2) {
                        
                    }
                    //quit
                    else if(menuP.y == 3) {
                        System.exit(0);
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
                //back to menu page selection
                else {
                    menuP.move(0, -1);
                    menuToggle = false;
                }
                break;

            case SWITCH:
                if(menuWindow.equals("inv") && selectP == -1) {
                    sortType++;
                    sortInventory(inv, invStacks);
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
        return 100;
    }
    
    //process release of input, return if view update required
    public boolean processRelease(Control input) {
        switch(input) {
            case TOGGLE:
                if(System.currentTimeMillis() - timer > 200) {
                    qiVisible = !qiVisible;
                }
                timer = -1;

                return true;
                
            case RUN:
                running = false;
                return false;
        }
        
        return false;
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
    
    //open a specific page in menu
    void toggleMenu(String window) {
        focus = "menu";
        menuWindow = window;
    }
    
    //testing: reset location
    void reset() {
        party[0].currentHP = party[0].maxHP;
        party[0].exists = true;
        Location temp;
        switch((int)(Math.random()*4)) {
            case 0: temp = new Location(this, "tower", (int)(Math.random()*3+1), party); break;
            case 1: temp = new Location(this, "dungeon", (int)(Math.random()*3+1), party); break;
            case 2: temp = new Location(this, "cave", (int)(Math.random()*3+1), party); break;
            case 3: temp = new Location(this, "city", (int)(Math.random()*4+1), party); break;
            default: temp = null;
        }
        maps.put(new Point(-1, -1), temp);
        maps.get(currentMap).getCurrentFloor().passControl(Control.UP);
    }
    
    //sort a given inventory with stacks
    private void sortInventory(Item[] items, int[] itemStacks) {
        int max = 64;
        
        //stack consumables and materials
        for(int i = 0; i < 63; i++) {
            for(int j = i+1; j < 64; j++) {
                if(items[i].id == items[j].id && (items[i].type == ItemType.CONSUMABLE 
                        || items[i].type == ItemType.MATERIAL)) {
                    itemStacks[i] += itemStacks[j];
                    itemStacks[j] = 0;
                    items[j].reset();
                }
            }
        }
        
        //close gaps
        for(int x = 0; x < max; x++) {
            if(!items[x].exists) {
                //find furthest item from back
                for(int y = 63; y > 0; y--) {
                    if(y <= x) {
                        max = y;
                        break;
                    }
                    else if(items[y].exists) {
                        Item temp = items[x];
                        items[x] = items[y];
                        items[y] = temp;
                        int tempi = itemStacks[x];
                        itemStacks[x] = itemStacks[y];
                        itemStacks[y] = tempi;
                        max = y;
                        break;
                    }
                }
            }
        }
        
        //comparator for items
        Comparator sorter;
        
        //reset sort type if over
        if(sortType > 4)
            sortType -= 5;
        
        //create comparator
        switch(sortType) {
            //by name
            case 0:
                class NameSorter implements Comparator {
                    @Override
                    public int compare(Object o1, Object o2) {
                        return Collator.getInstance().compare(Item.idname.get(((Item)o1).id),
                                Item.idname.get(((Item)o2).id));
                    }
                }
                sorter = new NameSorter();
                break;
            //by type
            case 1:
                class TypeSorter implements Comparator {
                    @Override
                    public int compare(Object o1, Object o2) {
                        if(((Item)o1).type.value() > ((Item)o2).type.value())
                            return 1;
                        else if(((Item)o1).type.value() < ((Item)o2).type.value())
                            return -1;
                        else
                            return 0;
                    }
                }
                sorter = new TypeSorter();
                break;
            //by value
            case 2:
                class ValueSorter implements Comparator {
                    @Override
                    public int compare(Object o1, Object o2) {
                        if(((Item)o1).VAL > ((Item)o2).VAL)
                            return -1;
                        else if(((Item)o1).VAL < ((Item)o2).VAL)
                            return 1;
                        else
                            return 0;
                    }
                }
                sorter = new ValueSorter();
                break;
            //by DMG
            case 3:
                class DamageSorter implements Comparator {
                    @Override
                    public int compare(Object o1, Object o2) {
                        if(((Item)o1).DMG > ((Item)o2).DMG)
                            return -1;
                        else if(((Item)o1).DMG < ((Item)o2).DMG)
                            return 1;
                        else
                            return 0;
                    }
                }
                sorter = new DamageSorter();
                break;
            //by rarity
            case 4:
                class RaritySorter implements Comparator {
                    @Override
                    public int compare(Object o1, Object o2) {
                        if(((Item)o1).rarity > ((Item)o2).rarity)
                            return -1;
                        else if(((Item)o1).rarity < ((Item)o2).rarity)
                            return 1;
                        else
                            return 0;
                    }
                }
                sorter = new RaritySorter();
                break;
            //by id: not really useful
            default:
                class Sorter implements Comparator {
                    @Override
                    public int compare(Object o1, Object o2) {
                        if(((Item)o1).id > ((Item)o2).id)
                            return 1;
                        else if(((Item)o1).id < ((Item)o2).id)
                            return -1;
                        else
                            return 0;
                    }
                }
                sorter = new Sorter();
                break;
        }
        
        //brute force sort
        for(int i = 0; i < max; i++) {
            for(int j = 0; j < max - 1; j++) {
                if(sorter.compare(items[j], items[j+1]) > 0) {
                    Item temp = items[j];
                    items[j] = items[j+1];
                    items[j+1] = temp;
                    int tempi = itemStacks[j];
                    itemStacks[j] = itemStacks[j+1];
                    itemStacks[j+1] = tempi;
                }
            }
        }
    }
    
    //switch two items in inventory
    private void swapInventory(int i1, int i2) {
        Item temp = inv[i1];
        inv[i1] = inv[i2];
        inv[i2] = temp;
        int tempi = invStacks[i1];
        invStacks[i1] = invStacks[i2];
        invStacks[i2] = tempi;
    }
    
    //set current map
    void setCurrentMap(Point p) {
        currentMap = p;
        getCurrentLocation().currentFloor = 0;
        getCurrentLocation().getCurrentFloor().passControl(Control.UP);
    }
    
    //make a location
    void makeLocation(Point p, String type) {
        if(!maps.containsKey(p)) {
            if(!type.equals("random"))
                maps.put(p, new Location(this, type, (int)(Math.random()*3+1), party));
            else {
                maps.put(p, new Location(this, "city", 3, party));
//                switch((int)(Math.random()*3)) {
//                    case 0: maps.put(p, new Location(this, "tower", 
//                            (int)(Math.random()*3+1), party)); break;
//                    case 1: maps.put(p, new Location(this, "dungeon", 
//                            (int)(Math.random()*3+1), party)); break;
//                    case 2: maps.put(p, new Location(this, "cave", 
//                            (int)(Math.random()*3+1), party)); break;
//                    default: maps.put(p, new Location(this, "city", 
//                            (int)(Math.random()*5+1), party)); break;
//                }
            }
        }
    }
    
    //load npc inventory
    void loadInventory(Character c) {
        if(invs.containsKey(c)) {
            tradeInv = invs.get(c);
            tradeStacks = stacks.get(c);
        }
        else {
            Item[] newInv = new Item[64];
            int[] newStacks = new int[64];
            for(int i = 0; i < 64; i++) {
                newInv[i] = new Item();
            }
            for(int i = (int)(Math.random()*30)+20; i >= 0; i--) {
                newInv[i] = Item.randomItem(0, null);
                newStacks[i] = 1;
            }
            
            invs.put(c, newInv);
            stacks.put(c, newStacks);
            
            tradeInv = newInv;
            tradeStacks = newStacks;
        }
    }
    
    //log something
    void logAdd(String s) {
        log.add(0, s);
        
        //reduce log size
        while(log.size() >= 30) {
            log.remove(log.size()-1);
        }
    }
    void addGold(int g) { gold += g; }
    
    //getters
    public int getSprite() { return sprite; }
    public Character[] getParty() { return party; }
    Location getCurrentLocation() { return maps.get(currentMap); }
    Location getLocation(Point id) { return maps.get(id); }
    String getLocationType() { return getCurrentLocation().type; }
    String getName() { return name; }
    String getPortrait() { return portrait; }
    Item[] getInventory() { return inv; }
    Item[] getTradeInv() { return tradeInv; }
    int[] getInvStacks() { return invStacks; }
    int[] getTradeStacks() { return tradeStacks; }
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
    int getTalkState() { return talkState; }
    int getTalkSelect() { return talkSelect; }
    String[] getTalkText() { return talkText; }
    boolean getTradeState() { return tradeState; }
    int getTalkIndex() { return talkIndex; }
    boolean getRunning() { return running; }
    ArrayList<String> getLog() { return log; } 
    Control getFacing() { return facing; }
    int getVisDist() { return visDist; }
    int getSortType() { return sortType; }
}