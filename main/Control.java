/*
    Global controls for game.
 */

package main;

public enum Control {
    UP, DOWN, LEFT, RIGHT, //directional
    UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT, //diagonal
    MENU, OPENINV, OPENCHAR, OPENPARTY, OPENNOTES, OPENOPTIONS, //open menu
    SELECT, BACK, TOGGLE, SWITCH, RUN, //other controls
    R, T, ALT, ESC,  //temporary
    NULL; //non-existent keybinding
}
