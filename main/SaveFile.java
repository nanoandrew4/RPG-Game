/*
    Save files for game.
*/

package main;

public class SaveFile {
    String name, file;
    int level;
    
    SaveFile(String file, String name, int level) {
        this.name = name;
        this.file = file;
        this.level = level;
    }
}
