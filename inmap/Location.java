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
        difficulty = (int)(Math.random() * 5 + 1);
        currentFloor = 0;
        numFloors = (int)(Math.random() * 20 + 5); 
        floors = new Floor[numFloors];
        for(int i = 0; i < numFloors; i++) {
            floors[i] = new Floor(this, i, type, difficulty, size, party);
        }
    }
    
    //changing floors
    void changeFloor(int floorMovement) {
        currentFloor += floorMovement;
        if(currentFloor >= numFloors) currentFloor = numFloors - 1;
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