/* 
    Path class for A* pathfinding.
 */

package main;

import java.util.PriorityQueue;
import java.awt.Point;

public class Path {
    byte[][] map;
    PriorityQueue<Point> frontier;
    Point start, end;
    /* 
    0: empty space
    1: wall
    2: path
    */
    
    //size constructor
    public Path(int sizeX, int sizeY, int sx, int sy, int ex, int ey) {
        map = new byte[sizeX][sizeY];
        frontier = new PriorityQueue();
        start.x = sx;
        start.y = sy;
        end.x = ex;
        end.y = ey;
        
        for(Point p: frontier) {
            
        }
//        frontier.offer();
    }
    
    //set value of map tile
    public void setMap(int x, int y, int value) {
        map[x][y] = (byte)value;
    }
    
    //pathfind
    public void search(int sx, int sy, int ex, int ey) {
        
    }
    
    public Control nextDirection() {
        return Control.LEFT;
    }
}
