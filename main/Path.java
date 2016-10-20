/*
    Path class containing queued Control actions.
 */

package main;

import java.util.ArrayDeque;
import java.awt.Point;

public class Path {
    private ArrayDeque<Control> path;
    
    //constructor
    public Path() {
        path = new ArrayDeque();
    }
    
    //pathfind with a overall map and boundaries
    public void pathFind(boolean[][] map, Point areaStart, Point areaEnd, Point start, Point end, boolean diag) {
        new AStar(map, areaStart, areaEnd, start, end).search(path, diag);
    }
    
    //pathfind with a limited map
    public void pathFind(boolean[][] map, Point start, Point end, boolean diag) {
        new AStar(map, new Point(0, 0), new Point(map.length, map[0].length), start, end).search(path, diag);
    }
    
    //get next control
    public Control next() {
        return path.poll();
    }
    
    //check if empty
    public boolean isEmpty() {
        return path.isEmpty();
    }
}
