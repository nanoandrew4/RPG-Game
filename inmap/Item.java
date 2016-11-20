/*
    Owned by floor.
    Includes equipment, consumables, and materials.
 */

package inmap;

import java.util.HashMap;
import java.util.ArrayList;

public class Item implements java.io.Serializable {
    //static vars
    static transient HashMap<String, Item> items = new HashMap();
    static transient HashMap<Short, String> iditem = new HashMap();
    static transient HashMap<String, Short> itemid = new HashMap();
    static transient ArrayList<String> 
            wpns = new ArrayList(), //weapons
            arms = new ArrayList(), //armor
            accs = new ArrayList(), //accessories
            mats = new ArrayList(), //materials
            cons = new ArrayList(); //consumables
    
    //vars
    boolean exists;
    String type, displayName, name, des;
    int DMG, HIT, CRT, PRC; //weapon stats
    int VIT, INT, ACC, STR, WIS, LUK, CHA, MHP, CHP, MMP, CMP; //bonuses
    int DEF, RES, EVA; //armor stats
    int VAL; //value
    byte RAR; //base item rarity
    byte rarity; //0 = NA, 1-5 otherwise
    byte LVL; //level 
    
        //static methods
    //get item template given name
    static Item get(String name) { 
        return items.get(name); 
    }
    
    //get item template given id
    static Item get(short id) {
        return items.get(iditem.get(id));
    }
    
    //get item id given name
    static short getID(String name) {
        return itemid.get(name);
    }
    
    //load an item into lists
    static void load(Item i, short id) {
        items.put(i.name, i);
        if(i.type.equals("weapon"))
            wpns.add(i.name);
        else if(i.type.equals("armor"))
            arms.add(i.name);
        else if(i.type.equals("accessory"))
            accs.add(i.name);
        else if(i.type.equals("material"))
            mats.add(i.name);
        else if(i.type.equals("consumable"))
            cons.add(i.name);
        iditem.put(id, i.name);
        itemid.put(i.name, id);
    }
    
    //return completely random item: shouldn't be used
    static Item randomItem() {
        return new Item(items.get((String)items.keySet().toArray()
                [(int)(Math.random()*items.size())]), (byte)(Math.random()*6), (byte)0);
    }
    
    //return random item given type and rarity context
    static Item randomItem(int rarcon, String type) {
        Item i;
        
        //get random type if null
        if(type == null) {
            switch((int)(Math.random()*5)) {
                case 0: type = "weapon"; break;
                case 1: type = "armor"; break;
                case 2: type = "accessory"; break;
                case 3: type = "material"; break;
                case 4: type = "consumable"; break;
                default: type = ""; break;
            }
        }
        
        //loop for rarity checks
        do {
            switch(type) {
                //get random weapon
                case "weapon":
                    i =  items.get(wpns.get((int)(Math.random()*wpns.size())));
                    break;
                case "armor":
                    i = items.get(arms.get((int)(Math.random()*arms.size())));
                    break;
                case "accessory":
                    i = items.get(accs.get((int)(Math.random()*accs.size())));
                    break;
                case "material":
                    i = items.get(mats.get((int)(Math.random()*mats.size())));
                    break;
                case "consumable":
                    i = items.get(cons.get((int)(Math.random()*cons.size())));
                    break;
                default:
                    i = new Item();
                    break;
            }
            
            //check for rarity
            if((int)(Math.random()*6) > i.RAR-rarcon)
                break;
        } while(true);
        
        return new Item(i, randRarity(rarcon), (byte)0);
    }
    
    //get total number of items
    static int size() {
        return items.size();
    }
    
    //get random rarity
    static byte randRarity(int rarcon) {
        int temp = (int)(Math.random()*1000000);
        if(temp < 900000) return 0; //90%
        else if(temp < 990000) return 1; //9%
        else if(temp < 999000) return 2; //.9%
        else if(temp < 999900) return 3; //.09%
        else if(temp < 999990) return 4; //.009%
        else if(temp < 1000000) return 5;//.001%
        else return 0;
    }
    
        //regular methods
    //default constructor with name
    Item(String name) {
        exists = true;
        this.name = name;
        Item i = get(name);
        des = i.des;
        setStats(i.type, i.DMG, i.HIT, i.CRT, i.PRC, i.VIT, i.INT, i.ACC, i.STR,
                i.WIS, i.LUK, i.CHA, i.MHP, i.CHP, i.MMP, i.CMP, i.DEF, i.RES,
                i.EVA, i.VAL, i.RAR, (byte)0);
        rarity = randRarity(0);
        
        calculateRarity();
    }
    
    //constructor given an item template, rarity, and level
    Item(Item i, byte rarity, byte LVL) {
        //copy stats
        exists = true;
        name = i.name;
        des = i.des;
        setStats(i.type, i.DMG, i.HIT, i.CRT, i.PRC, i.VIT, i.INT, i.ACC, i.STR,
                i.WIS, i.LUK, i.CHA, i.MHP, i.CHP, i.MMP, i.CMP, i.DEF, i.RES,
                i.EVA, i.VAL, i.RAR, LVL);
        this.rarity = rarity;
        
        calculateRarity();
    }
    
    //constructor with all stats: mostly for loading
    Item(String name, String type, String des, int DMG, int HIT, int CRT, int PRC, 
            int VIT, int INT, int ACC, int STR, int WIS, int LUK, int CHA, int MHP, 
            int CHP, int MMP, int CMP, int DEF, int RES, int EVA, int VAL, byte RAR) {
        exists = false;
        this.name = name;
        this.des = des;
        setStats(type, DMG, HIT, CRT, PRC, VIT, INT, ACC, STR, 
                WIS, LUK, CHA, MHP, CHP, MMP, CMP, DEF, RES, EVA, VAL, RAR, (byte)0);
    }
    
    //construct an empty item
    Item() {
        reset();
    }
    
    //rarity adjustments
    final void calculateRarity() {
        
        //rarity
        this.rarity = rarity;
        if(rarity == 0 || type.equals("consumable")) {
            displayName = name;
            this.rarity = 0;
        }
        else if(rarity == 1) {
            displayName = name + " ★";
            des += " An unusual item.";
        }
        else if(rarity == 2) {
            displayName = name + " ▲";
            des += " A strange item.";
        }
        else if(rarity == 3) {
            displayName = name + " ☆";
            des += " An unique item.";
        }
        else if(rarity == 4) {
            displayName = name + " ✪";
            des += " An extraordinary item.";
        }
        else if(rarity == 5) {
            displayName = name + " ꕤ";
            des += " A fear-inducing item.";
        }
        else {
            System.out.println("Rarity issue.");
            displayName = name;
        }
        
        //added stats for rarity based on original item
        DMG += rarity * get(name).DMG * .2;
        HIT += rarity * get(name).HIT * .05;
        CRT += rarity * get(name).CRT * .2;
        PRC += rarity * get(name).PRC * .1;
        VIT += rarity * get(name).VIT * .2;
        INT += rarity * get(name).INT * .2;
        ACC += rarity * get(name).ACC * .1;
        STR += rarity * get(name).STR * .2;
        WIS += rarity * get(name).WIS * .2;
        LUK += rarity * get(name).LUK * .2;
        CHA += rarity * get(name).CHA * .1;
        MHP += rarity * get(name).MHP * .4;
        CHP += rarity * get(name).CHP * .3;
        MMP += rarity * get(name).MMP * .4;
        CMP += rarity * get(name).CMP * .3;
        DEF += rarity * get(name).DEF * .2;
        RES += rarity * get(name).RES * .2;
        EVA += rarity * get(name).EVA * .1;
        VAL += rarity * get(name).VAL * .5;
    }
    
    //set stats
    final void setStats(String type, int DMG, int HIT, int CRT, int PRC, int VIT, 
            int INT, int ACC, int STR, int WIS, int LUK, int CHA, int MHP, int CHP, 
            int MMP, int CMP, int DEF, int RES, int EVA, int VAL, byte RAR, byte LVL) {
        this.type = type;
        this.DMG = DMG;
        this.HIT = HIT;
        this.CRT = CRT;
        this.PRC = PRC;
        this.VIT = VIT;
        this.INT = INT;
        this.ACC = ACC;
        this.STR = STR;
        this.WIS = WIS;
        this.LUK = LUK;
        this.MHP = MHP;
        this.CHP = CHP;
        this.MMP = MMP;
        this.CMP = CMP;
        this.DEF = DEF;
        this.RES = RES;
        this.EVA = EVA;
        this.VAL = VAL;
        this.RAR = RAR;
        this.LVL = LVL;
    }
    
    //zero all properties
    final void reset() {
        exists = false;
        displayName = "";
        name = "";
        rarity = 0;
        setStats("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
                0, 0, 0, 0, 0, 0, 0, (byte)0, (byte)0);
    }
}