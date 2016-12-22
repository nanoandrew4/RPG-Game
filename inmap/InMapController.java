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
    private int returnCode;
    boolean hasControl;
    private long timer;
    private Control lastControl;
    
    //new game constructor
    public InMapController(Main main, int VIT, int INT, int STR, int WIS, int LUK, 
            int CHA, String race, String name, int sprite, String portrait) {
        this.main = main;
        model = new InMapModel(VIT, INT, STR, WIS, 
                LUK, CHA, race, name, sprite, portrait);
        view = new InMapView(main.screenWidth, main.screenHeight, name, sprite, portrait);
        view.setLog(model.getLog());
        viewdata = new InMapViewData();
        hasControl = false;
        lastControl = Control.NULL;
    }

    //constructor with loaded model
    public InMapController(Main main, InMapModel model) {
        this.main = main;
        this.model = model;
        view = new InMapView(main.screenWidth, main.screenHeight, 
                model.getName(), model.getSprite(), model.getPortrait());
        view.setLog(model.getLog());
        viewdata = new InMapViewData();
        hasControl = false;
        lastControl = Control.NULL;
    }
    
    //quick constructor
    public InMapController(Main main) {
        this.main = main;
        model = new InMapModel();
        view = new InMapView(main.screenWidth, main.screenHeight, 
                model.getName(), model.getSprite(), model.getPortrait());
        view.setLog(model.getLog());
        viewdata = new InMapViewData();
        hasControl = false;
        lastControl = Control.NULL;
    }
    
    @Override
    public void run() {
        scene = view.getScene();
        setInput(scene);
    }

    //return model
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
    
    //update data to give to view
    private void updateViewData() {
        if (model.getCurrentLocation() == null)
            viewdata.floor = null;
        else
            viewdata.floor = model.getCurrentLocation().getCurrentFloor();
        viewdata.menuWindow = model.getMenuWindow();
        viewdata.focus = model.getFocus();
        viewdata.gold = model.getGold();
        viewdata.inv = model.getInventory();
        viewdata.invStacks = model.getInvStacks();
        viewdata.menuP.setLocation(model.getMenuPoint());
        viewdata.tempP.setLocation(model.getTempPoint());
        viewdata.useP = model.getUsePoint();
        viewdata.selectP = model.getSelectPoint();
        viewdata.party = model.getParty();
        viewdata.qiVisible = model.getQIVisible();
        viewdata.menuToggle = model.getMenuToggle();
        viewdata.invDes = model.getInvDes();
        viewdata.talkText = model.getTalkText();
        viewdata.talkState = model.getTalkState();
        viewdata.talkSelect = model.getTalkSelect();
        viewdata.talkIndex = model.getTalkIndex();
        viewdata.locationType = model.getLocationType();
        viewdata.running = model.getRunning();
        viewdata.facing = model.getFacing();
        viewdata.saveImages = main.saveImages;
        viewdata.saveInfo = main.saveInfo;
        viewdata.returnCode = returnCode;
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
    
    //return menu
    public Pane getMenuPane() {
        return view.getMenuPane();
    }
    
    //menu input from overworld
    public boolean menuInput(Control input) {
        returnCode = model.process(input);

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
            view.update(viewdata);
        }
        //load game
        else if(model.loadGame != -1) {
            main.loadGame(model.loadGame);
        }
        //update view
        else if(model.getFocus().equals("menu")) {
            updateViewData();
            view.update(viewdata);
        }
        
        return !model.getFocus().equals("menu");
    }
    
    //keyboard input
    private void setInput(Scene scene) {

        //key press events
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            Control c = main.getControl(event.getCode());
            //if same key and some time hasn't passed, return
            if(c == lastControl && !model.getRunning() && System.currentTimeMillis() - 110 <= timer) {
                return;
            }
            lastControl = c;
            returnCode = model.process(c);
            
            //save game
            if(model.saveGame != -1) {
                try {
                    main.saveModel(model.saveGame);
                } catch (IOException e) {
                    System.out.println("Failed to save model.");
                }
                
                model.saveGame = -1;
                
                main.refreshSaveInfo();
                updateViewData();
                view.refreshMenu(viewdata);
                view.update(viewdata);
            }
            //load game
            else if(model.loadGame != -1) {
                main.loadGame(model.loadGame);
            }
            //return to menu
            else if(model.rip) {
                main.setStage(null);
            }
            //update view
            else if(model.hasControl) {
                updateViewData();
                view.update(viewdata);
            }
            
            if(!model.hasControl && hasControl) {
                hasControl = false;
                main.passControl(null);
            }
            
            //reset timer
            timer = System.currentTimeMillis();
            
            event.consume();
        });

        //key release events
        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            
            if(model.processRelease(main.getControl(event.getCode()))) {
                if(model.hasControl) {
                    updateViewData();
                    view.update(viewdata);
                }
            }
            
            event.consume();
        });
    }
    
}
