/*
    Owned by floor.
    Includes equipment, consumables, and materials.
 */

package inmap;

import java.util.HashMap;
import java.util.ArrayList;

public class Item implements java.io.Serializable {
        //static vars
    //take name, return item template
    static transient HashMap<String, Item> items = new HashMap();
    //take id, return item template
    static transient HashMap<Short, Item> iditems = new HashMap();
    //take id, return name
    static transient HashMap<Short, String> idname = new HashMap();
    //take name, return id
    static transient HashMap<String, Short> nameid = new HashMap();
    static transient ArrayList<String> 
            wpns = new ArrayList(), //weapons
            arms = new ArrayList(), //armor
            accs = new ArrayList(), //accessories
            mats = new ArrayList(), //materials
            cons = new ArrayList(); //consumables
    
    //vars
    boolean exists;
    String displayName, des;
    ItemType type;
    short id;
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
        return iditems.get(id);
    }
    
    //get item id given name
    static short getID(String name) {
        return nameid.get(name);
    }
    
    //load an item into lists
    static void load(String name, Item i) {
        items.put(name, i);
        iditems.put(i.id, i);
        idname.put(i.id, name);
        nameid.put(name, i.id);
        switch(i.type) {
            case WEAPON: wpns.add(name); break;
            case ARMOR: arms.add(name); break;
            case ACCESSORY: accs.add(name); break;
            case MATERIAL: mats.add(name); break;
            case CONSUMABLE: cons.add(name); break;
        }
    }
    
    //return completely random item: shouldn't be used
    static Item randomItem() {
        return new Item(items.get((String)items.keySet().toArray()
                [(int)(Math.random()*items.size())]), (byte)(Math.random()*6), (byte)0);
    }
    
    //return random monster drop
    static Item randomMonsterDrop() {
        Item i;
        
        double rand = Math.random();
        if(rand < .8)
            return randomItem(0, ItemType.MATERIAL);
        else if(rand < .85)
            return randomItem(0, ItemType.WEAPON);
        else if(rand < .90)
            return randomItem(0, ItemType.ARMOR);
        else if(rand < .95)
            return randomItem(0, ItemType.ACCESSORY);
        else if(rand < 1)
            return randomItem(0, ItemType.CONSUMABLE);
        else
            i = null;
        
        return i;
    }
    
    //return random item given type and rarity context
    static Item randomItem(int rarcon, ItemType t) {
        Item i;
        
        //get random type if null
        if(t == null) {
            t = ItemType.values()[(int)(Math.random()*ItemType.values().length)];
        }
        
        //loop for rarity checks
        do {
            switch(t) {
                //get random weapon
                case WEAPON:
                    i =  items.get(wpns.get((int)(Math.random()*wpns.size())));
                    break;
                case ARMOR:
                    i = items.get(arms.get((int)(Math.random()*arms.size())));
                    break;
                case ACCESSORY:
                    i = items.get(accs.get((int)(Math.random()*accs.size())));
                    break;
                case MATERIAL:
                    i = items.get(mats.get((int)(Math.random()*mats.size())));
                    break;
                case CONSUMABLE:
                    i = items.get(cons.get((int)(Math.random()*cons.size())));
                    break;
                default:
                    i = new Item();
                    break;
            }
            
            //check for rarity
            if((int)(Math.random()*6) > i.RAR)
                break;
        } while(true);
        
        
        return new Item(i, randRarity(rarcon), (byte)0);
    }
    
    //get total number of items
    static int size() {
        return items.size();
    }
    
    //get random rarity: need to implement context
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
        Item i = get(name);
        id = i.id;
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
        id = i.id;
        des = i.des;
        setStats(i.type, i.DMG, i.HIT, i.CRT, i.PRC, i.VIT, i.INT, i.ACC, i.STR,
                i.WIS, i.LUK, i.CHA, i.MHP, i.CHP, i.MMP, i.CMP, i.DEF, i.RES,
                i.EVA, i.VAL, i.RAR, LVL);
        this.rarity = rarity;
        
        calculateRarity();
    }
    
    //constructor with all stats: mostly for loading
    Item(short id, String type, String des, int DMG, int HIT, int CRT, int PRC, 
            int VIT, int INT, int ACC, int STR, int WIS, int LUK, int CHA, int MHP, 
            int CHP, int MMP, int CMP, int DEF, int RES, int EVA, int VAL, byte RAR) {
        exists = false;
        this.id = id;
        this.des = des;
        ItemType it;
        switch(type) {
            case "weapon": it = ItemType.WEAPON; break;
            case "armor": it = ItemType.ARMOR; break;
            case "accessory": it = ItemType.ACCESSORY; break;
            case "consumable": it = ItemType.CONSUMABLE; break;
            case "material": it = ItemType.MATERIAL; break;
            default: it = null;
        }
        setStats(it, DMG, HIT, CRT, PRC, VIT, INT, ACC, STR, 
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
            displayName = idname.get(id);
            this.rarity = 0;
        }
        else if(rarity == 1) {
            displayName = idname.get(id) + " ★";
            des += " An unusual item.";
        }
        else if(rarity == 2) {
            displayName = idname.get(id) + " ▲";
            des += " A strange item.";
        }
        else if(rarity == 3) {
            displayName = idname.get(id) + " ☆";
            des += " An unique item.";
        }
        else if(rarity == 4) {
            displayName = idname.get(id) + " ✪";
            des += " An extraordinary item.";
        }
        else if(rarity == 5) {
            displayName = idname.get(id) + " ꕤ";
            des += " A fear-inducing item.";
        }
        else {
            System.out.println("Rarity issue.");
            displayName = idname.get(id);
        }
        
        //added stats for rarity based on original item
        DMG += rarity * get(id).DMG * .2;
        HIT += rarity * get(id).HIT * .05;
        CRT += rarity * get(id).CRT * .2;
        PRC += rarity * get(id).PRC * .1;
        VIT += rarity * get(id).VIT * .2;
        INT += rarity * get(id).INT * .2;
        ACC += rarity * get(id).ACC * .1;
        STR += rarity * get(id).STR * .2;
        WIS += rarity * get(id).WIS * .2;
        LUK += rarity * get(id).LUK * .2;
        CHA += rarity * get(id).CHA * .1;
        MHP += rarity * get(id).MHP * .4;
        CHP += rarity * get(id).CHP * .3;
        MMP += rarity * get(id).MMP * .4;
        CMP += rarity * get(id).CMP * .3;
        DEF += rarity * get(id).DEF * .2;
        RES += rarity * get(id).RES * .2;
        EVA += rarity * get(id).EVA * .1;
        VAL += rarity * get(id).VAL * .5;
    }
    
    //set stats
    final void setStats(ItemType type, int DMG, int HIT, int CRT, int PRC, int VIT, 
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
        id = -1;
        rarity = 0;
        setStats(null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
                0, 0, 0, 0, 0, 0, 0, (byte)0, (byte)0);
    }
}