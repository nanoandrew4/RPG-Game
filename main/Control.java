/*
    Global controls for game.
 */

package main;

public enum Control {
    UP, DOWN, LEFT, RIGHT, //directional
    UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT, //diagonal
    MENU, OPENINV, OPENCHAR, OPENPARTY, OPENNOTES, OPENOPTIONS, //open menu
    SELECT, BACK, TOGGLE, SWITCH, RUN, HIDEMENU, //other controls
    R, T, ALT, ESC,  //temporary
    NULL; //non-existent keybinding

    public static boolean isMovementKey(Control key) {
        return key == Control.UP || key == Control.DOWN || key == Control.LEFT || key == Control.RIGHT;
    }
}
