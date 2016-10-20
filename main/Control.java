/*
    Global controls for game.
 */

package main;

public enum Control {
    UP, DOWN, LEFT, RIGHT, //directional
    UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT, //diagonal
    SELECT, BACK, MENU, TAB, ESC, //other controls
    R, T, ALT, //temporary
    NULL; //non-existent keybinding
}
