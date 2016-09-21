/*
    Graphical controller for InMap.
 */

package inmap;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import main.Main;

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
                    case W: modelProcess(Direction.Up); moving = true; break;
                    case UP: modelProcess(Direction.Up); moving = true; break;
                    case A: modelProcess(Direction.Left); moving = true; break;
                    case LEFT: modelProcess(Direction.Left); moving = true; break;
                    case S: modelProcess(Direction.Down); moving = true; break;
                    case DOWN: modelProcess(Direction.Down); moving = true; break;
                    case D: modelProcess(Direction.Right); moving = true; break;
                    case RIGHT: modelProcess(Direction.Right); moving = true; break;
                    //reset location: debug purposes
                    case R: model.reset(); break;
                    //open menu
                    case C:
                        view.toggleMenu("", model.getCurrentLocation().getCurrentFloor());
                        control = "menu";
                        break;
                    //display UI
                    case TAB:
                        if(!UIVisible) {
                            view.toggleUI();
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
                    //close menu
                    case C:
                        view.toggleMenu("", model.getCurrentLocation().getCurrentFloor());
                        control = "floor";
                        break;
                }
            }
            
            event.consume();
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            
            //stop moving
            if(event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP 
                    || event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT 
                    || event.getCode() == KeyCode.S || event.getCode() == KeyCode.DOWN 
                    || event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT)
                moving = false;
            
            switch(event.getCode()) {
                //release UI
                case TAB:
                    view.toggleUI();
                    UIVisible = false;
                    break;
                //release shift
                case SHIFT:
                    shiftHeld = false;
                    break;
            }
            
            event.consume();
        });
    }
    
    //directional movement processing
    private void modelProcess(Direction direction) {
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
