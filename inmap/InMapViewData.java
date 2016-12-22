/*
    Contains all necessary data from model to display view.
*/

package inmap;

import java.awt.Point;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import main.Control;

public class InMapViewData {
    String focus, menuWindow, invDes, locationType;
    String[] talkText;
    Floor floor;
    Point tempP, menuP;
    int useP, selectP, gold, talkIndex,
            talkState, talkSelect, returnCode;
    Control facing;
    Character[] party;
    Item[] inv;
    int[] invStacks;
    boolean qiVisible, menuToggle, running;
    Text[][] saveInfo;
    ImageView[] saveImages;
    
    InMapViewData() {
        tempP = new Point();
        menuP = new Point();
    }
}
