/*
    Graphical controller for InMap.
 */

package inmap;

import java.awt.Point;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.input.KeyEvent;

import main.Main;

public class InMapController implements Runnable {
    
    final Main main;
    private Scene scene;
    private final InMapModel model;
    private final InMapView view;
    
    //constructor
    public InMapController(Main main) {
        this.main = main;
        model = new InMapModel();
        view = new InMapView(main.screenWidth, main.screenHeight);
    }
    
    @Override
    public void run() {
        scene = new Scene(new Pane());
        setInput(scene);
    }
    
    //take control of stage
    public void passControl(Point p) {
        model.setCurrentMap(p);
        scene = view.initDisplay(model.getCurrentLocation().getCurrentFloor());
        main.setStage(scene);
        setInput(scene);
    }
    
    //create new location
    public void newLocation(Point p, String type) {
        model.makeLocation(p, type);
    }
    
    //keyboard input
    private void setInput(Scene scene) {

        //key press events
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            
            model.process(main.getControl(event.getCode()));
            view.update(model.getFocus(), model.getCurrentLocation().getCurrentFloor(), 
                    model.getMenuPoint(), model.getMenuWindow(), model.getParty(), 
                    model.getInventory(), model.getGold(), model.getQIVisible());
            
            event.consume();
        });

        //key release events
        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            
            model.processRelease(main.getControl(event.getCode()));
            view.update(model.getFocus(), model.getCurrentLocation().getCurrentFloor(), 
                    model.getMenuPoint(), model.getMenuWindow(), model.getParty(), 
                    model.getInventory(), model.getGold(), model.getQIVisible());
            
            event.consume();
        });
    }
    
}
