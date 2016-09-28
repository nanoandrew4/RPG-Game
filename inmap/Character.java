/*
    InMap Model Character class.
 */

package inmap;

public class Character {
    //vars
    boolean exists;
    int x, y;
    int LVL, EXP, VIT, ACC, INT, STR, DEX, WIS, LUK; //base stats
    int maxHP, currentHP, maxMP, currentMP; //calculated stats
    double CRT, HIT, EVA, DMG, DEF, RES, PRC; //combat stats
    String name, AIMode;
    Race race;
    Item weapon;
    Item headArmor, torsoArmor, legArmor, footArmor;
    Item accessory1, accessory2, accessory3;
    
    //empty character
    Character() {
        exists = false;
    }
    
    //generate character given parameters
    Character(int LVL, int VIT, int INT, int ACC, int STR, int DEX, int WIS, int LUK, String name, String race, String AIMode) {
        setStats(true, LVL, VIT, INT, ACC, STR, DEX, WIS, LUK, name, race, AIMode);
    }
    
    //set stats
    final void setStats(boolean exists, int LVL, int VIT, int INT, int ACC, int STR, int DEX, int WIS, int LUK, String name, String race, String AIMode) {
        this.exists = exists;
        this.LVL = LVL;
        EXP = 0;
        this.VIT = VIT;
        this.INT = INT;
        this.ACC = ACC;
        this.STR = STR;
        this.DEX = DEX;
        this.WIS = WIS;
        this.LUK = LUK;
        this.name = name;
        this.AIMode = AIMode;
        
        weapon = new Item();
        headArmor = new Item();
        torsoArmor = new Item();
        legArmor = new Item();
        accessory1 = new Item();
        accessory2 = new Item();
        accessory3 = new Item();
        
        this.race = new Race(race);
        
        calculateStats();
        
        currentHP = maxHP;
        currentMP = maxMP;
    }
    
    //random NPC generation
    void generateNPC() {
        setStats(true, 1, 5, 1, 90, 1, 1, 1, 1, "NPC", "Human", "wandering");
    }
    
    //random enemy generation
    void generateEnemy() {
        switch((int)(Math.random() * 4)) {
            //case: setStats(true, lvl, vit, int, acc, str, dex, wis, luk, name,     race,      ai);
            case 0: setStats(true,   2,   3,   1,  80,   2,   4,   1,   0, "Spider", "Monster", "hostile");
            case 1: setStats(true,   1,   5,   1,  90,   1,   1,   1,   0, "Slug",   "Monster", "wandering");
            case 2: setStats(true,   5,   8,   3,  75,   6,   3,   2,   3, "Goblin", "Monster", "hostile");
            case 3: setStats(true,   3,   6,   3,  85,   3,   3,   1,   2, "Bat",    "Monster", "hostile");
        }
    }
    
    //make boss: temporary
    void generateBoss() {
        setStats(true, 8, 15, 5, 90, 12, 12, 12, 12, "Clinton", "Monster", "hostile");
    }
    
    //gain exp, calculate level
    void gainEXP(int exp) {
        EXP += exp;
        
        //level up
        if(EXP >= 150 * Math.sqrt(LVL + 10) - 430) {
            LVL++;
            EXP = 0;
            
            //stat increases
            VIT += 1;
            STR += 1;
            DEX += 1;
            WIS += 1;
            calculateStats();
            currentHP = maxHP;
        }
    }
    
    //gain exp based on defeated enemy
    void gainEXP(Character defeatedEnemy) {
        gainEXP((int)(Math.pow(defeatedEnemy.LVL, 2) / 2 + 10));
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
    
    //kill
    final void kill() {
        exists = false;
    }
}