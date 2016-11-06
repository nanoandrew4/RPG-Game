/*
    Owned by floor.
    Includes equipment, consumables, and materials.
 */

package inmap;

import java.util.HashMap;
import java.util.ArrayList;

public class Item {
    //static vars
    static HashMap<String, Item> items = new HashMap();
    static ArrayList<String> 
            wpns = new ArrayList(), //weapons
            arms = new ArrayList(), //amor
            accs = new ArrayList(), //accessories
            mats = new ArrayList(); //materials
    
    //vars
    boolean exists;
    String type, displayName, name, des;
    int DMG, HIT, CRT, PRC; //weapon stats
    int VIT, INT, ACC, STR, DEX, WIS, LUK, MHP, CHP, MMP, CMP; //bonuses
    int DEF, RES, EVA; //armor stats
    int VAL, RAR; //value and base item rarity
    int rarity; //0 = NA, 1-5 otherwise
    
        //static methods
    //get item template given name
    static Item get(String name) { 
        return items.get(name); 
    }
    
    //load an item into lists
    static void load(Item i) {
        items.put(i.name, i);
        if(i.type.equals("weapon"))
            wpns.add(i.name);
        else if(i.type.equals("armor"))
            arms.add(i.name);
        else if(i.type.equals("accessory"))
            accs.add(i.name);
        else if(i.type.equals("material"))
            mats.add(i.name);
    }
    
    //return completely random item: shouldn't be used
    static Item randomItem() {
        return new Item(items.get((String)items.keySet().toArray()
                [(int)(Math.random()*items.size())]), (int)(Math.random()*6));
    }
    
    //return random item given type and rarity context
    static Item randomItem(int rarcon, String type) {
        Item i;
        
        //get random type if null
        if(type == null) {
            switch((int)(Math.random()*4)) {
                case 0: type = "weapon"; break;
                case 1: type = "armor"; break;
                case 2: type = "accessory"; break;
                case 3: type = "material"; break;
                default: type = ""; break;
            }
        }
        
        //loop for rarity checks
        do {
            switch(type) {
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
                default:
                    i = new Item();
                    break;
            }
            
            if((int)(Math.random()*6) > i.RAR-rarcon)
                break;
        } while(true);
        
        return new Item(i, randRarity(rarcon));
    }
    
    //get total number of items
    static int size() {
        return items.size();
    }
    
    //get random rarity
    static int randRarity(int rarcon) {
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
    //constructor given an item and rarity
    Item(Item i, int rarity) {
        //copy stats
        exists = true;
        type = i.type;
        name = i.name;
        des = i.des;
        DMG = i.DMG;
        HIT = i.HIT;
        CRT = i.CRT;
        PRC = i.PRC;
        VIT = i.VIT;
        INT = i.INT;
        ACC = i.ACC;
        STR = i.STR;
        DEX = i.DEX;
        WIS = i.WIS;
        LUK = i.LUK;
        MHP = i.MHP;
        CHP = i.CHP;
        MMP = i.MMP;
        CMP = i.CMP;
        DEF = i.DEF;
        RES = i.RES;
        EVA = i.EVA;
        VAL = i.VAL;
        RAR = i.RAR;
        
        //special
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
    }
    
    //constructor with all stats
    Item(String name, String type, String des, int DMG, int HIT, int CRT, int PRC, 
            int VIT, int INT, int ACC, int STR, int DEX, int WIS, int LUK, int MHP, 
            int CHP, int MMP, int CMP, int DEF, int RES, int EVA, int VAL, int RAR) {
        exists = false;
        this.name = name;
        this.des = des;
        setStats(type, DMG, HIT, CRT, PRC, VIT, INT, ACC, STR, DEX, 
                WIS, LUK, MHP, CHP, MMP, CMP, DEF, RES, EVA, VAL, RAR);
    }
    
    //construct an empty item
    Item() {
        reset();
    }
    
    //set stats
    final void setStats(String type, int DMG, int HIT, int CRT, int PRC, int VIT, 
            int INT, int ACC, int STR, int DEX, int WIS, int LUK, int MHP, int CHP, 
            int MMP, int CMP, int DEF, int RES, int EVA, int VAL, int RAR) {
        this.type = type;
        this.DMG = DMG;
        this.HIT = HIT;
        this.CRT = CRT;
        this.PRC = PRC;
        this.VIT = VIT;
        this.INT = INT;
        this.ACC = ACC;
        this.STR = STR;
        this.DEX = DEX;
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
    }
    
    //zero all properties
    final void reset() {
        exists = false;
        type = "";
        displayName = "";
        name = "";
        rarity = 0;
        
        DMG = 0;
        HIT = 0;
        CRT = 0;
        PRC = 0;
        VIT = 0;
        INT = 0;
        ACC = 0;
        STR = 0;
        DEX = 0;
        WIS = 0;
        LUK = 0;
        MHP = 0;
        CHP = 0;
        MMP = 0;
        CMP = 0;
        DEF = 0;
        RES = 0;
        EVA = 0;
        VAL = 0;
        RAR = 0;
    }
}