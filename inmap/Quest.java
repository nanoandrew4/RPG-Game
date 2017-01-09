/*
    Quests
*/

package inmap;

public class Quest {
    Character requester;
    Character receiver;
    String questType;
    String itemName;
    int quantity;
    
    //fetch quest
    Quest(String itemName, int quantity) {
        questType = "fetch";
        this.itemName = itemName;
        this.quantity = quantity;
    }
}
