/*
    Graphical controller for InMap.
 */

package inmap;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class InMapController implements Runnable {
    
    private final Main main;
    private Scene scene;
    private final InMapModel model;
    private final InMapView view;
    
    //constructor
    InMapController(Main main) {
        this.main = main;
        model = new InMapModel();
        view = new InMapView(model.getCurrentLocation().getCurrentFloor().sizeX, 
                model.getCurrentLocation().getCurrentFloor().sizeY, main.screenWidth, main.screenHeight);
    }
    
    @Override
    public void run() {
        scene = view.initDisplay(model.getCurrentLocation().getCurrentFloor());
        main.setStage(scene);
        setInput(scene);
    }
    
    //update display
    private void updateDisplay() {
        view.updateDisplay(model.getCurrentLocation().getCurrentFloor());
    }
    
    //keyboard input
    private void setInput(Scene scene) {

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            //directional movement
            switch(event.getCode()) {
                case W: model.process(Direction.Up); model.getCurrentLocation().getCurrentFloor().processAI(); break;
                case UP: model.process(Direction.Up); model.getCurrentLocation().getCurrentFloor().processAI(); break;
                case A: model.process(Direction.Left); model.getCurrentLocation().getCurrentFloor().processAI(); break;
                case LEFT: model.process(Direction.Left); model.getCurrentLocation().getCurrentFloor().processAI(); break;
                case S: model.process(Direction.Down); model.getCurrentLocation().getCurrentFloor().processAI(); break;
                case DOWN: model.process(Direction.Down); model.getCurrentLocation().getCurrentFloor().processAI(); break;
                case D: model.process(Direction.Right); model.getCurrentLocation().getCurrentFloor().processAI(); break;
                case RIGHT: model.process(Direction.Right); model.getCurrentLocation().getCurrentFloor().processAI(); break;
            }
            
            //reset
            if(event.getCode() == KeyCode.R) {
                model.reset();
            }

            updateDisplay();
            
            event.consume();
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            
            //directional movement
//            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.D || event.getCode() == KeyCode.W || event.getCode() == KeyCode.S) {
//                view.speedX.set(0);
//                view.speedY.set(0);
//            }
            
            event.consume();
        });
    }
    
}
