/*
    Contains all necessary data from model to display view.
*/

package inmap;

import java.awt.Point;

public class InMapViewData {
    String focus, menuWindow, invDes;
    Floor floor;
    Point tempP, menuP;
    int useP, selectP, gold;
    Character[] party;
    Item[] inv;
    boolean qiVisible, menuToggle;
    
    InMapViewData() {
        tempP = new Point();
        menuP = new Point();
    }
}
