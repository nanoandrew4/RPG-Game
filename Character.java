/*
    InMap Model Character class.
 */

package inmap;

public class Character {
    //vars
    boolean exists;
    int AIMode, x, y;
    int LVL, EXP, VIT, ACC, INT, STR, DEX, WIS, LUK; //base stats
    int maxHP, currentHP, maxMP, currentMP;
    double CRT, HIT, EVA, DMG, DEF, RES, PRC; //combat stats
    String name;
    Race race;
    Item weapon;
    Item headArmor, torsoArmor, legArmor, footArmor;
    Item accessory1, accessory2, accessory3;
    
    //empty character
    Character() {
        exists = false;
    }
    
    //duplicate a character
    Character(Character clone) {
        exists = clone.exists;
    }
    
    //generate character given parameters
    Character(int LVL, int EXP, int VIT, int INT, int ACC, int STR, int DEX, int WIS, int LUK, String name) {
        exists = true;
        this.LVL = LVL;
        this.EXP = EXP;
        this.VIT = VIT;
        this.INT = INT;
        this.ACC = ACC;
        this.STR = STR;
        this.DEX = DEX;
        this.WIS = WIS;
        this.LUK = LUK;
        this.name = name;
        
        weapon = new Item();
        headArmor = new Item();
        torsoArmor = new Item();
        legArmor = new Item();
        accessory1 = new Item();
        accessory2 = new Item();
        accessory3 = new Item();
        
        race = new Race();
        
        calculateStats();
        
        currentHP = maxHP;
        currentMP = maxMP;
    }
    
    //randomized character given approximate level
    Character(int level) {
        exists = true;
        this.LVL = Math.max(1, level + (int)(Math.random() * 6 - 3));
    }
    
    Character generateEnemy() {
        switch((int)(Math.random() * 4)) {
            //case: return new Character(lvl, exp, vit, int, acc, str, dex, wis, luk, name);
            case 0: return new Character(  2,   0,   3,   1,  80,   2,   4,   1,   0, "Spider");
            case 1: return new Character(  1,   0,   5,   1,  90,   1,   1,   1,   0, "Slug");
            case 2: return new Character(  5,   0,   8,   3,  75,   6,   3,   2,   3, "Goblin");
            case 3: return new Character(  3,   0,   6,   3,  85,   3,   3,   1,   2, "Bat");
            default: return new Character();
        }
    }
    
    //use base stats to calculate combat stats
    final void calculateStats() {
        maxHP = (int)(200 * Math.sqrt(VIT + 50) - 1300);
        maxMP = (int)(40 * Math.sqrt(INT + 10) - 100);
        CRT = -20000 / (LUK + 250) + 80;
        HIT = weapon.HIT * 200 / (ACC + 400) + 1.3;
        switch(weapon.type) {
            case "w": DMG = weapon.DMG * (Math.sqrt(STR + 40) / 5 - 0.45); break;
            case "a": DMG = weapon.DMG * (Math.sqrt(DEX + 40) / 5 - 0.45); break;
            case "m": DMG = weapon.DMG * (Math.sqrt(WIS + 40) / 5 - 0.45); break;
            default: DMG = 10;
        }
        //below are not implemented correctly
        DMG = Math.pow(STR, 2) / 4;
        DEF = 50;
        RES = 50;
        PRC = 100;
        EVA = 0;
        HIT = ACC;
    }
}
