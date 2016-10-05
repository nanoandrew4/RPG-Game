/*
    Graphical controller for InMap.
 */

package inmap;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import main.Main;
import main.Control;

public class InMapController implements Runnable {
    
    final Main main;
    private Scene scene;
    private final InMapModel model;
    private final InMapView view;
    private String control = "floor";
    private boolean UIVisible, shiftHeld, moving;
    
    //constructor
    public InMapController(Main main) {
        this.main = main;
        model = new InMapModel();
        view = new InMapView(main.screenWidth, main.screenHeight);
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

            //movement in floor
            if(control.equals("floor")) {
                //get control values for keycodes
                switch(main.getControl(event.getCode())) {
                    //directional movement
                    case UP: modelProcess(Control.UP); break;
                    case LEFT: modelProcess(Control.LEFT); break;
                    case DOWN: modelProcess(Control.DOWN); break;
                    case RIGHT: modelProcess(Control.RIGHT); break;
                    //open/close menu
                    case MENU:
                        model.toggleMenu();
                        view.toggleMenu("", model.getParty(), model.getInventory(), model.getGold());
                        control = "menu";
                        break;
                    //toggle UI
                    case TAB:
                        if(!UIVisible) {
                            view.toggleUI(model.getCurrentLocation(), model.getParty(), model.getGold());
                            UIVisible = true;
                        }
                        break;
                    //debug
                    case R:
                        model.reset(); break;
                    case T:
                        model.getParty()[0].gainEXP(10000);
                    default: break;
                }

                updateDisplay();
            }
            
            //menu scrolling
            else if(control.equals("menu")) {
                switch(main.getControl(event.getCode())) {
                    //switching menus
                    case LEFT: view.changeMenu(-1, model.getParty(), model.getInventory(), model.getGold()); break;
                    case RIGHT: view.changeMenu(1, model.getParty(), model.getInventory(), model.getGold()); break;
                    
                    //close menu
                    case MENU:
                        view.toggleMenu("", model.getParty(), model.getInventory(), model.getGold());
                        control = "floor";
                        break;
                }
            }
            
            event.consume();
        });

        //key release events
        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            
            if(control.equals("floor"))
            {
                switch(main.getControl(event.getCode())) {
                    //release UI
                    case TAB:
                        view.toggleUI(model.getCurrentLocation(), model.getParty(), model.getGold());
                        UIVisible = false;
                        break;
                        
                }
            }
            else if(control.equals("menu")) {
                
            }
            
            event.consume();
        });
    }
    
    //directional movement processing
    private void modelProcess(Control direction) {
        model.process(direction);
    }
    
}
