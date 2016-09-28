/*
    Owned by model.
    Contains floors.
 */

package inmap;

public class Location {
    int currentFloor, difficulty, numFloors;
    String name, type;
    private Floor[] floors;
    
    //random constructor
    Location(String type, int size, Character[] party) {
        this.type = type;
        difficulty = (int)(Math.random() * 5 + 1);
        currentFloor = 0;
        numFloors = (int)(Math.random() * 10 + 5); 
        floors = new Floor[numFloors];
        randName();
        for(int i = 0; i < numFloors; i++) {
            floors[i] = new Floor(this, i, type, difficulty, size, party);
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
        if(currentFloor < 0) currentFloor = 0;
        floors[currentFloor].passControl(floorMovement > 0 ? Direction.Up : Direction.Down);
    }
    
    //process input
    void process(Direction direction) {
        floors[currentFloor].processPlayer(direction);
    }
    
    //return current floor
    Floor getCurrentFloor() {
        return floors[currentFloor];
    }
}