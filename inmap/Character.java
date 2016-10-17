/*
    InMap Model Character class.
 */

package inmap;

import java.awt.Point;

import main.Path;
import main.Control;

public class Character {
    //vars
    boolean exists;
    int x, y;
    int LVL, EXP, VIT, ACC, INT, STR, DEX, WIS, LUK; //base stats
    int maxHP, currentHP, maxMP, currentMP; //calculated stats
    double CRT, HIT, EVA, DMG, DEF, RES, PRC; //combat stats
    String name, AIMode;
    Path path;
    boolean hostile;
    Race race;
    Item weapon;
    Item headArmor, torsoArmor, legArmor, footArmor;
    Item accessory1, accessory2, accessory3;
    
    //empty character
    Character() {
        exists = false;
    }
    
    //generate character given parameters
    Character(int LVL, int VIT, int INT, int ACC, int STR, int DEX, int WIS, int LUK, String name, String race, String AIMode, Path path, boolean hostile) {
        setStats(true, LVL, VIT, INT, ACC, STR, DEX, WIS, LUK, name, race, AIMode, path, hostile);
    }
    
    //set stats
    final void setStats(boolean exists, int LVL, int VIT, int INT, int ACC, int STR, int DEX, int WIS, int LUK, String name, String race, String AIMode, Path path, boolean hostile) {
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
        this.path = path;
        this.hostile = hostile;
        
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
        setStats(true, 1, 5, 1, 90, 1, 1, 1, 1, "NPC", "Human", "wandering", new Path(), false);
    }
    
    //random enemy generation
    void generateEnemy() {
        switch((int)(Math.random() * 17)) {
            //case: setStats(exist, lvl, vit, int, acc, str, dex, wis, luk, name,     race,      ai,          path, hostile);
//            case 0: setStats(true,   2,   3,   1,  80,   2,   4,   1,   0, "Spider", "Monster", "wandering", true); break;
//            case 1: setStats(true,   1,   5,   1,  90,   1,   1,   1,   0, "Slug",   "Monster", "wandering", true); break;
//            case 2: setStats(true,   5,   8,   3,  75,   6,   3,   2,   3, "Goblin", "Monster", "wandering", true); break;
//            case 3: setStats(true,   3,   6,   3,  85,   3,   3,   1,   2, "Bat",    "Monster", "wandering", true); break;
            case 0:  setStats(true,   5,   9,   3,  90,   9,   5,   5,   5, "adelf", "Monster", "wandering", new Path(), true); break;
            case 1:  setStats(true,   2,   6,   3,  75,   3,   3,   3,   3, "bat", "Monster", "wandering", new Path(), true); break;
            case 2:  setStats(true,   2,   5,   3,  80,   2,   2,   2,   2, "bell", "Monster", "fleeing", new Path(), false); break;
            case 3:  setStats(true,  15,  10,   3,   0,   1,   1,   1,   1, "chest", "Monster", "stationary", new Path(), false); break;
            case 4:  setStats(true,   1,   3,   3,  60,   2,   1,   1,   1, "chick", "Monster", "wandering", new Path(), false); break;
            case 5:  setStats(true,   2,   5,   3,  85,   4,   1,   1,   1, "chicken", "Monster", "wandering", new Path(), true); break;
            case 6:  setStats(true,   4,  11,   3,  85,   8,   3,   3,   3, "fishman", "Monster", "wandering", new Path(), true); break;
            case 7:  setStats(true,   5,   8,   3,  80,   6,   5,   5,   5, "flan", "Monster", "wandering", new Path(), true); break;
            case 8:  setStats(true,   4,   7,   3,  50,   5,   3,   3,   3, "ghost", "Monster", "wandering", new Path(), true); break;
            case 9:  setStats(true,   8,   9,   3,  90,  10,   3,   3,   3, "kingslime", "Monster", "wandering", new Path(), true); break;
            case 10: setStats(true,   5,   8,   3,  90,   9,   3,   3,   3, "longcat", "Monster", "wandering", new Path(), true); break;
            case 11: setStats(true,  10,  15,   3,  85,  15,   3,   3,   3, "manta", "Monster", "wandering", new Path(), true); break;
            case 12: setStats(true,   2,   3,   3,  60,   2,   3,   3,   3, "mote", "Monster", "wandering", new Path(), false); break;
            case 13: setStats(true,  13,  17,   3,  90,  22,   3,   3,   3, "skelebro", "Monster", "wandering", new Path(), true); break;
            case 14: setStats(true,   4,   6,   3,  70,   4,   3,   3,   3, "snail", "Monster", "wandering", new Path(), true); break;
            case 15: setStats(true,   7,  15,   3,  60,  13,   3,   3,   3, "spookyslime", "Monster", "wandering", new Path(), true); break;
            case 16: setStats(true,   6,   9,   3,  85,  16,   3,   3,   3, "spookyslug", "Monster", "wandering", new Path(), true); break;
            
        }
    }
    
    //make boss: temporary
    void generateBoss() {
        setStats(true, 8, 15, 5, 90, 12, 12, 12, 12, "Clinton", "Monster", "wandering", new Path(), true);
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
    
    //set path
    void pathTo(Point target, boolean[][] map) {
        path.pathFind(map, new Point(x, y), target);
    }
    
    //get next direction from path
    Control getNext() {
        return path.next();
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