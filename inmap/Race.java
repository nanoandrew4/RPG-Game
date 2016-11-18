/*
    Race class.
 */

package inmap;

class Race implements java.io.Serializable{
    String name;
    
    Race() {
        name = "None";
    }
    
    Race(String name) {
        this.name = name;
    }
}