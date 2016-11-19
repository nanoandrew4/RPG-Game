/*
    Save files for game.
*/

package main;

public class SaveFile {
    boolean exists;
    String name, file, sprite;
    int level, playtime, slot;
    
    SaveFile(String file, String name, String sprite,
            int level, int playtime, int slot) {
        exists = true;
        this.name = name;
        this.file = file;
        this.sprite = sprite;
        this.level = level;
        this.playtime = playtime;
        this.slot = slot;
    }
    
    SaveFile() {
        exists = false;
    }
}
