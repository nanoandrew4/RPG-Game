/*
    Graphical controller for InMap.
 */

package inmap;

import java.awt.Point;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import main.*;

public class InMapController implements Runnable {
    
    final Main main;
    private Scene scene;
    private final InMapModel model;
    private final InMapView view;
    private final InMapViewData viewdata;
    boolean hasControl;
    
    //new game constructor
    public InMapController(Main main, int VIT, int INT, int STR, int WIS, int LUK, 
            int CHA, String race, String name, String sprite, String portrait) {
        this.main = main;
        model = new InMapModel(VIT, INT, STR, WIS, 
                LUK, CHA, race, name, sprite, portrait);
        view = new InMapView(main.screenWidth, main.screenHeight, name, sprite, portrait);
        viewdata = new InMapViewData();
        hasControl = false;
    }
    
    //quick constructor
    public InMapController(Main main) {
        this.main = main;
        model = new InMapModel();
        view = new InMapView(main.screenWidth, main.screenHeight, 
                model.getName(), model.getSprite(), model.getPortrait());
        viewdata = new InMapViewData();
        hasControl = false;
    }

    //constructor with loaded model
    public InMapController(Main main, InMapModel model) {
        this.main = main;
        this.model = model;
        view = new InMapView(main.screenWidth, main.screenHeight, 
                model.getName(), model.getSprite(), model.getPortrait());
        viewdata = new InMapViewData();
        hasControl = false;
    }
    
    @Override
    public void run() {
        scene = view.initDisplay();
        setInput(scene);
    }

    public InMapModel getModel() {
        return model;
    }
    
    //take control of stage
    public void passControl(Point p) {
        hasControl = true;
        model.hasControl = true;
        model.setCurrentMap(p);
        updateViewData();
        view.update(viewdata);
        main.setStage(scene);
    }
    
    private void updateViewData() {
        viewdata.floor = model.getCurrentLocation().getCurrentFloor();
        viewdata.menuWindow = model.getMenuWindow();
        viewdata.focus = model.getFocus();
        viewdata.gold = model.getGold();
        viewdata.inv = model.getInventory();
        viewdata.menuP.setLocation(model.getMenuPoint());
        viewdata.tempP.setLocation(model.getTempPoint());
        viewdata.useP = model.getUsePoint();
        viewdata.selectP = model.getSelectPoint();
        viewdata.party = model.getParty();
        viewdata.qiVisible = model.getQIVisible();
        viewdata.menuToggle = model.getMenuToggle();
        viewdata.invDes = model.getInvDes();
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
    
    public void menuInput(Control input) {
        model.process(input);
        updateViewData();
        view.update(viewdata);
    }
    
    public void toggleMenu(boolean on) {
        model.toggleMenu(on);
    }
    
    public void toggleMenu(String window) {
        model.toggleMenu(window);
    }
    
    //keyboard input
    private void setInput(Scene scene) {

        //key press events
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            
            model.process(main.getControl(event.getCode()));
            if(model.hasControl) {
                updateViewData();
                view.update(viewdata);
            }
            
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
            if(model.hasControl) {
                updateViewData();
                view.update(viewdata);
            }
            
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
