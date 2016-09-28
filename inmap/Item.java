/*
    Owned by floor.
    Includes equipment, consumables, and materials.
 */

package inmap;

public class Item {
    boolean exists;
    String type, name;
    int DMG, HIT, CRT, PRC; //weapon stats
    int bVIT, bINT, bACC, bSTR, bDEX, bWIS, bLUK, bMHP, bCHP, bMMP, bCMP; //bonuses
    int DEF, RES, EVA; //armor stats
    int rarity; //0 = NA, 1-5 otherwise
    
    //constructor given its name
    Item(String entity) {
        reset();
        exists = true;
        name = entity;
    }
    
    //construct an empty item
    Item() {
        reset();
    }
    
    //zero all properties
    final void reset() {
        exists = false;
        type = "";
        name = "";
        rarity = 0;
        
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
    }
}