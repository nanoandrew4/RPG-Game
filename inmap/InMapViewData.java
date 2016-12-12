/*
    Contains all necessary data from model to display view.
*/

package inmap;

import java.awt.Point;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class InMapViewData {
    String focus, menuWindow, invDes, talkText;
    Floor floor;
    Point tempP, menuP;
    int useP, selectP, gold, 
            talkState, talkSelect, returnCode;
    Character[] party;
    Item[] inv;
    boolean qiVisible, menuToggle, shiftHeld;
    Text[][] saveInfo;
    ImageView[] saveImages;
    
    InMapViewData() {
        tempP = new Point();
        menuP = new Point();
    }
}
