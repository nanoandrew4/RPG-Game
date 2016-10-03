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

            //movement in floors
            if(control.equals("floor")) {
                switch(event.getCode()) {
                    //directional movement
                    case W: modelProcess(Control.Up); moving = true; break;
                    case UP: modelProcess(Control.Up); moving = true; break;
                    case A: modelProcess(Control.Left); moving = true; break;
                    case LEFT: modelProcess(Control.Left); moving = true; break;
                    case S: modelProcess(Control.Down); moving = true; break;
                    case DOWN: modelProcess(Control.Down); moving = true; break;
                    case D: modelProcess(Control.Right); moving = true; break;
                    case RIGHT: modelProcess(Control.Right); moving = true; break;
                    //reset location: debug purposes
                    case R: model.reset(); break;
                    //open menu
                    case C:
                        view.toggleMenu("", model.getParty(), model.getInventory(), model.getGold());
                        control = "menu";
                        break;
                    //display UI
                    case TAB:
                        if(!UIVisible) {
                            view.toggleUI(model.getCurrentLocation(), model.getParty(), model.getGold());
                            UIVisible = true;
                        }
                        break;
                    case SHIFT:
                        shiftHeld = true;
                    default: break;
                }

                updateDisplay();
            }
            
            //menu scrolling
            else if(control.equals("menu")) {
                switch(event.getCode()) {
                    
                    case A: view.changeMenu(-1, model.getParty(), model.getInventory(), model.getGold()); break;
                    case LEFT: view.changeMenu(-1, model.getParty(), model.getInventory(), model.getGold()); break;
                    case D: view.changeMenu(1, model.getParty(), model.getInventory(), model.getGold()); break;
                    case RIGHT: view.changeMenu(1, model.getParty(), model.getInventory(), model.getGold()); break;
                    
                    //close menu
                    case C:
                        view.toggleMenu("", model.getParty(), model.getInventory(), model.getGold());
                        control = "floor";
                        break;
                }
            }
            
            event.consume();
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            
            //stop moving
//            if(event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP 
//                    || event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT 
//                    || event.getCode() == KeyCode.S || event.getCode() == KeyCode.DOWN 
//                    || event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
//                moving = false;
//            }
            
            if(control.equals("floor"))
            {
                switch(event.getCode()) {
                    //release UI
                    case TAB:
                        view.toggleUI(model.getCurrentLocation(), model.getParty(), model.getGold());
                        UIVisible = false;
                        break;
                    //release shift
                    case SHIFT:
                        shiftHeld = false;
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
        model.getCurrentLocation().getCurrentFloor().processAI();
        
//        Task<Void> task = new Task<Void>() {
//            @Override
//            public Void call() throws Exception {
//                Thread.sleep(50);
//                return null;
//            }
//        };
//
//        task.setOnSucceeded(event -> {
//            System.out.println("a");
//            model.process(direction);
//            model.getCurrentLocation().getCurrentFloor().processAI();
//            if(moving)
//                new Thread(task).run();
//        });
//        
//        Thread mThread = new Thread(task);
//        mThread.run();
    }
    
}
