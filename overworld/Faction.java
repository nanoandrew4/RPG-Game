package overworld;

import java.util.ArrayList;
import java.util.List;

class Faction implements java.io.Serializable {

    /*
        Class contains all data related to factions
     */

    private String kingdomName;
    private String capitalSettlement;
    private List<String> memberSettlements;

    Faction() {
        kingdomName = "";
        capitalSettlement = "";
        memberSettlements = new ArrayList<>();
    }

    public void addToKingdom(String name) {
        memberSettlements.add(name);
    }

    public void removeFromKingdom(String name) {
        memberSettlements.remove(name);
    }

    public boolean isInKingdom(String name) {
        return memberSettlements.contains(name);
    }
}