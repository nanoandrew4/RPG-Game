/*
    Save files for game.
*/

package main;

class SaveFile implements java.io.Serializable{
    boolean exists;
    String name, file, sprite;
    int level, slot;
    double playtime;
    
    SaveFile(String file, String name, String sprite,
            int level, double playtime, int slot) {
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
