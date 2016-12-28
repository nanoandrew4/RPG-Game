/*
    Enumeration for item types.
 */

package inmap;

public enum ItemType {
    WEAPON, ARMOR, ACCESSORY, CONSUMABLE, MATERIAL;
    
    @Override
    public String toString() {
        switch(this) {
            case WEAPON: return "WEAPON";
            case ARMOR: return "ARMOR";
            case ACCESSORY: return "ACCESSORY";
            case CONSUMABLE: return "CONSUMABLE";
            case MATERIAL: return "MATERIAL";
        }
        return "";
    }
    
    public int value() {
        switch(this) {
            case WEAPON: return 0;
            case ARMOR: return 1;
            case ACCESSORY: return 2;
            case CONSUMABLE: return 3;
            case MATERIAL: return 4;
        }
        return -1;
    }
}
