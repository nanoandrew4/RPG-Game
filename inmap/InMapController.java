/*
    Controller for InMap.
 */

package inmap;

import java.awt.Point;
import java.io.IOException;
import javafx.application.Platform;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

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
        scene = view.getScene();
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
        Platform.runLater(() -> main.setStage(scene));
    }
    
    private void updateViewData() {
        if (model.getCurrentLocation() == null)
            viewdata.floor = null;
        else
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
        viewdata.saveImages = main.saveImages;
        viewdata.saveInfo = main.saveInfo;
    }

    //create new location
    public void newLocation(Point p, String type) {
        model.makeLocation(p, type);
    }

    //get name of current location
    public String getLocationName(Point p) {
        return model.getLocation(p).name;
    }

    //get difficulty of current location
    public String getDifficulty(Point p){
        return "Easy peasy"; // temp
    }
    
    public Pane getMenuPane() {
        return view.getMenuPane();
    }
    
    public boolean menuInput(Control input) {
        model.process(input);

        //save game
        if(model.saveGame != -1) {
            try {
                main.saveModel(model.saveGame);
            } catch (IOException e) {
                e.printStackTrace();
            }

            model.saveGame = -1;
                
            main.refreshSaveInfo();
            updateViewData();
            view.refreshMenu(viewdata);
        }
        //load game
        else if(model.loadGame != -1) {
            main.loadGame(model.loadGame);
        }
        
        //update view
        if(model.getFocus().equals("menu")) {
            updateViewData();
            view.update(viewdata);
        }
        
        return !model.getFocus().equals("menu");
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
            
            //save game
            if(model.saveGame != -1) {
                try {
                    main.saveModel(model.saveGame);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                model.saveGame = -1;
                
                main.refreshSaveInfo();
                updateViewData();
                view.refreshMenu(viewdata);
            }
            //load game
            else if(model.loadGame != -1) {
                main.loadGame(model.loadGame);
            }
            
            //update view
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
            
            if(!model.hasControl && hasControl) {
                hasControl = false;
                main.passControl(null);
            }
            
            event.consume();
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
