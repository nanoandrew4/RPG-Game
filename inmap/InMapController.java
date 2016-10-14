/*
    Graphical controller for InMap.
 */

package inmap;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.input.KeyEvent;

import main.Main;

import java.awt.*;

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
    
    public void passControl(Point p) {
        model.setCurrentMap(p);
        scene = view.initDisplay(model.getCurrentLocation().getCurrentFloor());
        main.setStage(scene);
        setInput(scene);
    }

    public void newLocation(Point p, String type) {
        model.makeLocation(p, type);
    }

    public String getName(Point p) {
        return model.getLocation(p).name;
    }

    public String getDifficulty(Point p){
        return "Easy peasy"; // temp
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
