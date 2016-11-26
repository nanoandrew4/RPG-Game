/*
    InMap Model Character class.
 */

package inmap;

import java.awt.Point;

import main.Path;
import main.Control;

public class Character implements java.io.Serializable {
    //static vars
    static transient String[] names = {
        "Andres", "Adrian", "Chow", "Cthulhu", "Top Kek",
        "John", "James", "Robert", "William", "Richard",
        "Maria", "Evelyn", "Anne", "Karen", "Sara",
        "Hercules", "Zeus", "Apollo", "Ares", "Poseidon",
        "Aphrodite", "Hera", "Hestia", "Athena", "Artemis",
        //"", "", "", "", "",
    };
    
    //vars
    boolean exists;
    int x, y;
    int LVL, EXP, ACC, VIT, INT, STR, WIS, LUK, CHA; //base stats
    int maxHP, currentHP, maxMP, currentMP; //calculated stats
    double CRT, HIT, EVA, DMG, DEF, RES, PRC; //combat stats
    String name;
    AIType AIMode;
    Path path;
    boolean hostile;
    Race race;
    Item weapon;
    Item armor;
    Item acc1, acc2, acc3;
    
        //static methods
    //random name
    public static String randomName() {
        return names[(int)(Math.random()*names.length)];
    }
    
    //empty character
    Character() {
        exists = false;
    }
    
    //generate character given parameters
    Character(int LVL, int VIT, int INT, int ACC, int STR, int WIS, int LUK, int CHA, 
            String name, String race, AIType AIMode, Path path, boolean hostile) {
        setStats(true, LVL, VIT, INT, ACC, STR, WIS, LUK, CHA, name, race, AIMode, path, hostile);
    }
    
    public String getName() {
        return name;
    }

    public int getLVL() {return LVL;}

    //set stats
    final void setStats(boolean exists, int LVL, int VIT, int INT, int ACC, int STR, int WIS,
                        int LUK, int CHA, String name, String race, AIType AIMode, Path path, boolean hostile) {
        this.exists = exists;
        this.LVL = LVL;
        EXP = 0;
        this.VIT = VIT;
        this.INT = INT;
        this.ACC = ACC;
        this.STR = STR;
        this.WIS = WIS;
        this.LUK = LUK;
        this.CHA = CHA;
        this.name = name;
        this.AIMode = AIMode;
        this.path = path;
        this.hostile = hostile;

        weapon = new Item();
        armor = new Item();
        acc1 = new Item();
        acc2 = new Item();
        acc3 = new Item();

        this.race = new Race(race);

        calculateStats();

        currentHP = maxHP;
        currentMP = maxMP;
    }
    
    //random NPC generation
    void generateNPC() {
        setStats(true, 1, 5, 1, 90, 1, 1, 1, 1, "NPC", "Human", AIType.WANDER, new Path(), false);
    }
    
    //random enemy generation
    void generateEnemy() {
        switch((int)(Math.random() * 17)) {
            //case: setStats(exist, lvl, vit, int, acc, str, wis, luk, cha, name,     race,      ai,          path, hostile);
//            case 0: setStats(true,   2,   3,   1,  80,   2,   4,   1,   0, "Spider", "Monster", "wandering", true); break;
//            case 1: setStats(true,   1,   5,   1,  90,   1,   1,   1,   0, "Slug",   "Monster", "wandering", true); break;
//            case 2: setStats(true,   5,   8,   3,  75,   6,   3,   2,   3, "Goblin", "Monster", "wandering", true); break;
//            case 3: setStats(true,   3,   6,   3,  85,   3,   3,   1,   2, "Bat",    "Monster", "wandering", true); break;
            case 0:  setStats(true,   5,   9,   3,  90,   9,   5,   5,   0, "adelf", "Monster", AIType.WANDER, new Path(), true); break;
            case 1:  setStats(true,   2,   6,   3,  75,   3,   3,   3,   0, "bat", "Monster", AIType.WANDER, new Path(), true); break;
            case 2:  setStats(true,   2,   5,   3,  80,   2,   2,   2,   0, "bell", "Monster", AIType.FLEE, new Path(), false); break;
            case 3:  setStats(true,  15,  10,   3,   0,   1,   1,   1,   0, "chest", "Monster", AIType.STILL, new Path(), false); break;
            case 4:  setStats(true,   1,   3,   3,  60,   2,   1,   1,   0, "chick", "Monster", AIType.WANDER, new Path(), false); break;
            case 5:  setStats(true,   2,   5,   3,  85,   4,   1,   1,   0, "chicken", "Monster", AIType.WANDER, new Path(), true); break;
            case 6:  setStats(true,   4,  11,   3,  85,   8,   3,   3,   0, "fishman", "Monster", AIType.WANDER, new Path(), true); break;
            case 7:  setStats(true,   5,   8,   3,  80,   6,   5,   5,   0, "flan", "Monster", AIType.WANDER, new Path(), true); break;
            case 8:  setStats(true,   4,   7,   3,  50,   5,   3,   3,   0, "ghost", "Monster", AIType.WANDER, new Path(), true); break;
            case 9:  setStats(true,   8,   9,   3,  90,  10,   3,   3,   0, "kingslime", "Monster", AIType.WANDER, new Path(), true); break;
            case 10: setStats(true,   5,   8,   3,  90,   9,   3,   3,   0, "longcat", "Monster", AIType.WANDER, new Path(), true); break;
            case 11: setStats(true,  10,  15,   3,  85,  15,   3,   3,   0, "manta", "Monster", AIType.WANDER, new Path(), true); break;
            case 12: setStats(true,   2,   3,   3,  60,   2,   3,   3,   0, "mote", "Monster", AIType.WANDER, new Path(), false); break;
            case 13: setStats(true,  13,  17,   3,  90,  22,   3,   3,   0, "skelebro", "Monster", AIType.WANDER, new Path(), true); break;
            case 14: setStats(true,   4,   6,   3,  70,   4,   3,   3,   0, "snail", "Monster", AIType.WANDER, new Path(), true); break;
            case 15: setStats(true,   7,  15,   3,  60,  13,   3,   3,   0, "spookyslime", "Monster", AIType.WANDER, new Path(), true); break;
            case 16: setStats(true,   6,   9,   3,  85,  16,   3,   3,   0, "spookyslug", "Monster", AIType.WANDER, new Path(), true); break;
        }
        weapon = new Item("Monster Sword");
        calculateStats();
    }
    
    //make boss: temporary
    void generateBoss() {
        setStats(true, 8, 15, 5, 90, 12, 12, 12, 12, "Clinton", "Monster", AIType.WANDER, new Path(), true);
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
            WIS += 1;
            CHA += 1;
            calculateStats();
            currentHP = maxHP;
        }
    }
    
    //gain exp based on defeated enemy
    void gainEXP(Character defeatedEnemy) {
        gainEXP((int)(Math.pow(defeatedEnemy.LVL, 2) / 2 + 10));
    }
    
    //set path
    void pathTo(Point target, boolean[][] map, boolean diag) {
        path.pathFind(map, new Point(x, y), target, diag);
    }
    
    //get next direction from path
    Control getNext() {
        return path.next();
    }
    
    //use base stats to calculate combat stats
    final void calculateStats() {
        maxHP = (int)(200 * Math.sqrt(VIT+weapon.VIT+armor.VIT+acc1.VIT+acc2.VIT+acc3.VIT+50) - 1300);
        maxHP += weapon.MHP + armor.MHP + acc1.MHP + acc2.MHP + acc3.MHP;
        maxMP = (int)(40 * Math.sqrt(INT+weapon.INT+armor.INT+acc1.INT+acc2.INT+acc3.INT + 10) - 100);
        maxMP += weapon.MMP + armor.MMP + acc1.MMP + acc2.MMP + acc3.MMP;
        CRT = -20000 / (LUK+weapon.LUK+armor.LUK+acc1.LUK+acc2.LUK+acc3.LUK+250) + 80;
        CRT += weapon.CRT + armor.CRT + acc1.CRT + acc2.CRT + acc3.CRT;
        HIT = weapon.HIT * (-200 / (ACC+weapon.ACC+armor.ACC+acc1.ACC+acc2.ACC+acc3.ACC+400) + 1.3);
        if(weapon.DMG == 0) { //melee weapon
            DMG = Math.pow(STR, 2) / 4;
            HIT = 90;
        }
        else if(weapon.CRT > 0) {
            DMG = weapon.DMG * (Math.sqrt(STR + 40) / 5 - 0.45);
        }
        else if(weapon.CRT == 0) { //ranged weapon
            DMG = weapon.DMG * (Math.sqrt(WIS + 40) / 5 - 0.45);
        }
        HIT += armor.HIT + acc1.HIT + acc2.HIT + acc3.HIT;
        DEF = weapon.DEF + armor.DEF + acc1.DEF + acc2.DEF + acc3.DEF;
        RES = weapon.RES + armor.RES + acc1.RES + acc2.RES + acc3.RES;
        PRC = weapon.PRC + armor.PRC + acc1.PRC + acc2.PRC + acc3.PRC;
        EVA = weapon.EVA + armor.EVA + acc1.EVA + acc2.EVA + acc3.EVA;
    }
    
    //kill
    final void kill() {
        exists = false;
    }
}