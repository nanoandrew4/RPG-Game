/*
    Owned by model.
    Contains floors.
 */

package inmap;

public class Location {
    int baseFloor, currentFloor, difficulty, size;
    String name, type;
    Floor[] floors;
    
    //random constructor
    Location() {
        difficulty = (int)(Math.random() * 5 + 1);
        size = (int)(Math.random() * 5 + 1); 
        floors = new Floor[size];
        for(int i = 0; i < size; i++) {
            floors[i] = new Floor(difficulty, 10, 10);
        }
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
