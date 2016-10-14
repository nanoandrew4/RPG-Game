/*
    Path class containing queued Control actions.
 */

package main;

import java.util.ArrayDeque;
import java.awt.Point;

public class Path {
    ArrayDeque<Control> path;
    
    //constructor
    Path() {
        path = new ArrayDeque();
    }
    
    //pathfind with a overall map and boundaries
    public void pathFind(boolean[][] map, Point areaStart, Point areaEnd, Point start, Point end) {
        AStar a = new AStar(map, areaStart, areaEnd, start, end);
        a.search(path);
    }
    
    //pathfind with a limited map
    public void pathFind(boolean[][] map, Point start, Point end) {
        AStar a = new AStar(map, new Point(0, 0), new Point(map.length, map[0].length), start, end);
        a.search(path);
    }
    
    //get next control
    public Control next() {
        return path.poll();
    }
}
