/*
    Owned by floor.
    Includes weapons, armor, consumables.
 */

package inmap;

public class Item {
    boolean exists;
    String type, name;
    int DMG, HIT, CRT, PRC; //weapon stats
    int bVIT, bINT, bACC, bSTR, bDEX, bWIS, bLUK, bMHP, bCHP, bMMP, bCMP; //bonuses
    int DEF, RES, EVA; //armor stats
    int rarity; //0 = NA, 1-5 otherwise
    
    //duplicate an item
    Item(Item clone) {
        exists = clone.exists;
        type = clone.type;
        name = clone.name;
        DMG = clone.DMG;
        HIT = clone.HIT;
        CRT = clone.CRT;
        PRC = clone.PRC;
        bVIT = clone.bVIT;
        bINT = clone.bINT;
        bACC = clone.bACC;
        bSTR = clone.bSTR;
        bDEX = clone.bDEX;
        bWIS = clone.bWIS;
        bLUK = clone.bLUK;
        bMHP = clone.bMHP;
        bCHP = clone.bCHP;
        bMMP = clone.bMMP;
        DEF = clone.DEF;
        RES = clone.RES;
        EVA = clone.EVA;
        rarity = clone.rarity;
    }
    
    //constructor given its name
    Item(String entity) {
        reset();
        exists = true;
        switch(entity) {
            case "potion": type = "consumable"; name = "Potion"; bCHP = 50; break;
            default: break;
        }
    }
    
    //empty item
    Item() {
        reset();
    }
    
    //zero all properties
    final void reset() {
        exists = false;
        type = "";
        name = "";
        DMG = 0;
        HIT = 0;
        CRT = 0;
        PRC = 0;
        bVIT = 0;
        bINT = 0;
        bACC = 0;
        bSTR = 0;
        bDEX = 0;
        bWIS = 0;
        bLUK = 0;
        bMHP = 0;
        bCHP = 0;
        bMMP = 0;
        bCMP = 0;
        DEF = 0;
        RES = 0;
        EVA = 0;
        rarity = 0;
    }
}