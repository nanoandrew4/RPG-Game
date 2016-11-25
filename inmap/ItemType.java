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
}
