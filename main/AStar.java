/* 
    A* pathfinding.
 */

package main;

import java.util.PriorityQueue;
import java.util.ArrayDeque;
import java.awt.Point;
import java.util.Comparator;

class AStar {
    //map representation
    byte[][] map;
    /* 
    0: empty space
    1: wall
    2: path
    */
    boolean[][] open;
    boolean[][] closed;
    int[][] f, g, h;
    int x, y;
    Control[][] camefrom;
    PriorityQueue<Point> frontier;
    Point start, end;
    String a;
    
    //frontier comparator
    class fCompare implements Comparator<Point> {
        @Override
        public int compare(Point a, Point b) {
            if(f[a.x][a.y] < f[b.x][b.y])
                return -1;
            else if(f[a.x][a.y] > f[b.x][b.y])
                return 1;
            else 
                return 0;
        }
    }
    
    //constructor
    AStar(boolean[][] boolMap, Point areaS, Point areaE, Point start, Point end) {
        //initialize
        map = new byte[areaE.x-areaS.x][areaE.y-areaS.y];
        frontier = new PriorityQueue(1, new fCompare());
        this.start.setLocation(start);
        this.end.setLocation(end);
        x = start.x;
        y = start.y;
        
        //populate map
        for(int i = 0; i < map.length; i++)
            for(int j = 0; j < map[0].length; j++)
                map[x][y] = boolMap[x][y] ? (byte)1 : 0;
    }
    
    //pathfind
    public void search(ArrayDeque path) {
        path.clear();
        
        //loop until finished
        while(x != end.x || y != end.y) {
            //close current point
            closed[x][y] = true;
            
            //open adjacent tiles with already open and OOB checks
            if(x < map.length-1 && map[x+1][y] != 1 && !closed[x+1][y] && !open[x+1][y]) {
                open[x+1][y] = true;
                camefrom[x+1][y] = Control.LEFT;
                g[x+1][y] = g[x][y] + 1;
                h[x+1][y] = Math.abs(map.length - x - 1) + Math.abs(map[0].length - y);
                f[x+1][y] = g[x+1][y] + h[x+1][y];
                frontier.add(new Point(x+1, y));
            }
            if(x > 0 && map[x-1][y] != 1 && !closed[x-1][y] && !open[x-1][y]) {
                open[x-1][y] = true;
                camefrom[x-1][y] = Control.RIGHT;
                g[x-1][y] = g[x][y] + 1;
                h[x-1][y] = Math.abs(map.length - x + 1) + Math.abs(map[0].length - y);
                f[x-1][y] = g[x-1][y] + h[x-1][y];
                frontier.add(new Point(x-1, y));
            }
            if(y < map[0].length-1 && map[x][y+1] != 1 && !closed[x][y+1] && !open[x][y+1]) {
                open[x][y+1] = true;
                camefrom[x][y+1] = Control.UP;
                g[x][y+1] = g[x][y] + 1;
                h[x][y+1] = Math.abs(map.length - x) + Math.abs(map[0].length - y - 1);
                f[x][y+1] = g[x][y+1] + h[x][y+1];
                frontier.add(new Point(x, y+1));
            }
            if(y > 0 && map[x][y-1] != 1 && !closed[x][y-1] && !open[x][y-1]) {
                open[x][y-1] = true;
                camefrom[x][y-1] = Control.DOWN;
                g[x][y-1] = g[x][y] + 1;
                h[x][y-1] = Math.abs(map.length - x) + Math.abs(map[0].length - y + 1);
                f[x][y-1] = g[x][y-1] + h[x][y-1];
                frontier.add(new Point(x, y-1));
            }
            
            Point temp = frontier.poll();
            x = temp.x;
            y = temp.y;
        }
        
        //find path back
        while(x != start.x || y != start.y) {
            path.add(camefrom[x][y]);
            switch(camefrom[x][y]) {
                case UP: y--; break;
                case DOWN: y++; break;
                case LEFT: x--; break;
                case RIGHT: x++; break;
            }
        }
        
        path.add(camefrom[x][y]);
    }
}
