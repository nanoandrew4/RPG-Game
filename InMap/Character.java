/*
    Character class for Rising Legend.
 */

package InMap;

public class Character
{
    boolean exists, npc, isItem;
    int mHP, cHP, attack, crit, acc, eva, def, prc;
    int x, y;
    String name;
    
    Character(int mHP, int attack, int crit, int acc, int eva, int def, int prc, String name) {
        exists = true;
        isItem = false;
        this.mHP = mHP;
        cHP = mHP;
        this.attack = attack;
        this.crit = crit;
        this.acc = acc;
        this.eva = eva;
        this.def = def;
        this.prc = prc;
        this.name = name;
        npc = !name.equals("  hero  ");
    }
    
    Character(String item) {
        exists = true;
        isItem = true;
        switch(item) {
            case "potion": name = "Potion"; break;
            case "trap": name = "Trap"; break;
            default: break;
        }
    }
    
    Character() {
        exists = false;
    }
}
