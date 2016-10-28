/*
    Graphical controller for InMap.
 */

package inmap;

import java.awt.Point;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import main.Main;

public class InMapController implements Runnable {
    
    final Main main;
    private Scene scene;
    private final InMapModel model;
    private final InMapView view;
    boolean hasControl;
    
    //constructor
    public InMapController(Main main) {
        this.main = main;
        model = new InMapModel();
        view = new InMapView(main.screenWidth, main.screenHeight);
        hasControl = false;
    }
    
    @Override
    public void run() {
        scene = view.initDisplay();
        setInput(scene);
    }
    
    //take control of stage
    public void passControl(Point p) {
        hasControl = true;
        model.hasControl = true;
        model.setCurrentMap(p);
        view.update(model.getFocus(), model.getCurrentLocation().getCurrentFloor(), 
                model.getMenuPoint(), model.getSelectPoint(), model.getMenuWindow(), 
                model.getParty(), model.getInventory(), model.getGold(), model.getQIVisible());
        main.setStage(scene);
    }

    //create new location
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
            if(model.hasControl)
                view.update(model.getFocus(), model.getCurrentLocation().getCurrentFloor(), 
                        model.getMenuPoint(), model.getSelectPoint(), model.getMenuWindow(), 
                        model.getParty(), model.getInventory(), model.getGold(), model.getQIVisible());
            
//            switch(main.getControl(event.getCode())) {
//                case UP: view.speedY.set(view.speedYVal); break;
//                case RIGHT: view.speedX.set(-view.speedXVal); break;
//                case DOWN: view.speedY.set(-view.speedYVal); break;
//                case LEFT: view.speedX.set(view.speedXVal); break;
//            }
            
            event.consume();
            
            if(!model.hasControl && hasControl) {
                hasControl = false;
                main.overworldController.passControl();
            }
        });

        //key release events
        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            
            model.processRelease(main.getControl(event.getCode()));
            if(model.hasControl)
                view.update(model.getFocus(), model.getCurrentLocation().getCurrentFloor(), 
                        model.getMenuPoint(), model.getSelectPoint(), model.getMenuWindow(), 
                        model.getParty(), model.getInventory(), model.getGold(), model.getQIVisible());
            
//            switch(main.getControl(event.getCode())) {
//                case UP: view.speedY.set(0); break;
//                case RIGHT: view.speedX.set(0); break;
//                case DOWN: view.speedY.set(0); break;
//                case LEFT: view.speedX.set(0); break;
//            }
            
            event.consume();
        });
    }
    
}
