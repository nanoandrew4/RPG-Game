/*
    Owned by model.
    Contains floors.
 */

package inmap;

import main.Control;

class Location implements java.io.Serializable{
    private InMapModel model;
    int currentFloor, difficulty, numFloors;
    String name, type;
    private Floor[] floors;
    
    //random constructor
    Location(InMapModel model, String type, int size, Character[] party) {
        this.model = model;
        this.type = type;
        difficulty = (int)(Math.random() * 5 + 1);
        currentFloor = 0;
        numFloors = (int)(Math.random() * 10 + 5);
        floors = new Floor[numFloors];
        randName();
        for(int i = 0; i < numFloors; i++) {
            floors[i] = new Floor(model, this, i, type, difficulty, size, party);
        }
    }
    
    //generate random name
    void randName() {
        name = type.toUpperCase() + " OF ";
        switch((int)(Math.random()*8)) {
            case 0: name += "TRUMP"; break;
            case 1: name += "CANCER"; break;
            case 2: name += "DEATH"; break;
            case 3: name += "ANDRES"; break;
            case 4: name += "RIP"; break;
            case 5: name += "EBOLA"; break;
            case 6: name += "DANGEROUS"; break;
            case 7: name += "NO GOOD"; break;
            default: name += "NOPE"; break;
        }
    }
    
    //changing floors
    void changeFloor(int floorMovement) {
        currentFloor += floorMovement;
        //exit location
        if(currentFloor < 0)
            model.hasControl = false;
        else 
            floors[currentFloor].passControl(floorMovement > 0 ? Control.UP : Control.DOWN);
    }
    
    //process input
    int process(Control direction) {
        return floors[currentFloor].processPlayer(direction);
    }
    
    //return current floor
    Floor getCurrentFloor() {
        if(currentFloor >= 0)
            return floors[currentFloor];
        return null;
    }
}