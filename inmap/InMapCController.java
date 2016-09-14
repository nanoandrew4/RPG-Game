/*
    Console controller for InMap.
 */

package inmap;

import java.util.*;

public class InMapCController {
    InMapModel map;
    InMapCView view;
    
    void run() {
        map = new InMapModel();
        view = new InMapCView();
        view.display(map.getCurrentLocation().getCurrentFloor());
        
        Scanner in = new Scanner(System.in);
        
        //loop
        while(true) {
            String input = in.nextLine();
            switch(input) {
                case "w": map.process(Direction.Up); break;
                case "a": map.process(Direction.Left); break;
                case "s": map.process(Direction.Down); break;
                case "d": map.process(Direction.Right); break;
                default: break;
            }
            
            map.getCurrentLocation().getCurrentFloor().processAI();
            
            view.display(map.getCurrentLocation().getCurrentFloor());
        }
    }
}