/*
    Owned by Location.
    Contains data for each floor of a location.
 */

package inmap;

import java.awt.Point;
import java.util.ArrayDeque;

import main.Control;

class Floor implements java.io.Serializable {
    InMapModel model;
    Location location;
    int size, sizeX, sizeY; //size is arbitrary, affects sizeX/Y
    int difficulty; //difficulty affects spawn rate, avg level
    int enterX, enterY, endX, endY; //stair locations
    int floorNum; //floor number of a given location
    Tile[][] tiles;
    Character[][] chars;
    Character[] party; //[0] is the player
    Character[] npcs;
    Item[][] items;
    
    //constructor
    Floor(InMapModel model, Location location, int floorNum, 
            String type, int diff, int size, Character[] party) {
        //init
        this.location = location;
        this.model = model;
        this.floorNum = floorNum;
        difficulty = diff;
        this.size = size;
        this.party = party;
        
        generate(type, size);
    }
    
    //ready for input processing
    void passControl(Control direction) {
        switch(direction) {
            case UP:
                party[0].x = enterX;
                party[0].y = enterY;
                chars[enterX][enterY] = party[0];
                break;
            case DOWN:
                party[0].x = endX;
                party[0].y = endY;
                chars[endX][endY] = party[0];
                break;
        }
        calculateVis();
    }
    
    //process player input
    int processPlayer(Control direction) {
        int sx = party[0].x;
        int sy = party[0].y;
        int ex = party[0].x;
        int ey = party[0].y;
        switch(direction) {
            case UP: ey = party[0].y - 1; break;
            case DOWN: ey = party[0].y + 1; break;
            case LEFT: ex = party[0].x - 1; break;
            case RIGHT: ex = party[0].x + 1; break;
        }
        
        int r = process(sx, sy, ex, ey);
        calculateVis();
        
        return r;
    }
    
    //process all npc movement
    void processAI() {
        for(Character n : npcs) {
            if(n.exists) {
                
                int dx = 0, dy = 0;
                switch(n.AIMode) {
                    case ATTACK:
                        //make new path
                        n.pathTo(new Point(party[0].x, party[0].y), booleanMap(), false);
                        //get next direction from npc path
                        switch(n.getNext()) {
                            case UP: dy = -1; break;
                            case DOWN: dy = 1; break;
                            case RIGHT: dx = 1; break;
                            case LEFT: dx = -1; break;
                        }
                        break;
                    case FLEE: 
                        //x movement
                        if(party[0].x < n.x)
                            dx = 1;
                        else if(party[0].x > n.x)
                            dx = -1;
                        else dx = 0;
                        //y movement
                        if(party[0].y < n.y)
                            dy = 1;
                        else if(party[0].y > n.y)
                            dy = -1;
                        else dy = 0;
                        //randomly move either vertically or horizontally
                        if(Math.random() * 100 < 50)
                            dx = (dy == 0 ? dx : 0);
                        else 
                            dy = (dx == 0 ? dy : 0);
                        break;
                    case WANDER:
                        //wander randomly
                        int i = (int)(Math.random() * 20);
                        if(i < 4) {
                            dx = 0;
                            dy = 0;
                            switch(i) {
                                case 0: dx = 1; break;
                                case 1: dx = -1; break;
                                case 2: dy = 1; break;
                                case 3: dy = -1; break;
                                default: break;
                            }
                        }
                        //start attacking if within range
                        if(n.hostile && Math.sqrt(Math.pow((party[0].x-n.x),2)+
                                Math.pow((party[0].y-n.y),2)) < 5) {
                            n.AIMode = AIType.ATTACK;
                        }
                        break;
                    case STILL:
                        break;
                    case NONE:
                        break;
                    default: 
                        break;
                }
                
                process(n.x, n.y, n.x+dx, n.y+dy);
            }
        }
    }
    
    //process movement, return action code
    private int process(int sx, int sy, int ex, int ey) {
        //standing still: do nothing
        if(sx == sy && ex == ey) {
            return 0;
        }
        //out of bounds: down a floor
        else if((ex < 0 || ex >= sizeX || ey < 0 || ey >= sizeY)) {
            if(chars[sx][sy].name.equals(party[0].name)) {
                location.changeFloor(-1);
                chars[sx][sy] = new Character();
            }
            
            return 1;
        }
        //interaction with npcs
        else if(chars[sx][sy].exists && chars[ex][ey].exists) {
            //attacking
            if(!chars[sx][sy].race.name.equals(chars[ex][ey].race.name) 
                    || chars[ex][ey].AIMode == AIType.ATTACK) {
                attack(chars[sx][sy], chars[ex][ey]);
                
                //if killed
                if(chars[ex][ey].currentHP <= 0) {
                    chars[ex][ey].currentHP = 0;
                    chars[sx][sy].gainEXP(chars[ex][ey]);
                    
                    //drop items
                    if(chars[ex][ey].race.name.equals("Monster") && Math.random() < 1) {
                        dropItem(Item.randomMonsterDrop(), ex, ey);
                    }
                    
                    //kill dead character
                    chars[ex][ey].kill();
                }
                
                return 2;
            }
            //talking with npcs: player only
            else if(chars[sx][sy].name.equals(party[0].name) &&
                    chars[ex][ey].AIMode == AIType.WANDER) {
                return 3;
            }
        }
        //moving floors
        else if(tiles[ex][ey].floorMovement != 0 && chars[sx][sy].name.equals(party[0].name)) {
            location.changeFloor(tiles[ex][ey].floorMovement);
            chars[sx][sy] = new Character();
            
            return 1;
        }
        //picking up items: still move
        else if(!tiles[ex][ey].isWall && items[ex][ey].exists && chars[sx][sy].equals(party[0])) {
            short temp = items[ex][ey].id;
            items[ex][ey].reset();
            swap(sx, sy, ex, ey);
            
            if(ey < sy) //up
                return 5000 + temp;
            else if(ex < sx) //left
                return 6000 + temp;
            else if(ey > sy) //down
                return 7000 + temp;
            else if(ex > sx) //right
                return 8000 + temp;
        }
        //moving normally
        else if(!tiles[ex][ey].isWall) {
            swap(sx, sy, ex, ey);
            
            if(ey < sy) //up
                return 5;
            else if(ex < sx) //left
                return 6;
            else if(ey > sy) //down
                return 7;
            else if(ex > sx) //right
                return 8;
        }
        //open doors
        else if(tiles[ex][ey].openable) {
            tiles[ex][ey].open();
            
            return 4;
        }
        
        return -1;
    }
    
    //simulate c1 attacking c2, return attack result
    /*  Return:
        0 - Hit
        1 - c2 Evaded
        2 - c1 Missed
        3 - Critical
    */
    private int attack(Character c1, Character c2) {
        int result = 0;
        
        //calculate evasion
        if((int)(Math.random() * 100) < c2.EVA) return 1;
        //calculate hit chance
        if((int)(Math.random() * 100) > c1.HIT) return 2;
        //start damage calculation
        double damage = c1.DMG;
        //crit chance
        if((int)(Math.random() * 100) < c1.CRT) damage *= 2;
        //calculate for defences and pierce
        if(c1.weapon.CRT == 0) { 
            if(c2.RES > c1.PRC)
                damage *= (c1.PRC / (c2.RES + c1.PRC));
            else if(c2.RES < c1.PRC)
                damage *= (c1.PRC / (c2.RES + c1.PRC));
        }
        else {
            if(c2.DEF > c1.PRC)
                damage *= (c1.PRC / (c2.DEF + c1.PRC));
            else if(c2.DEF < c1.PRC)
                damage *= (c1.PRC / (c2.DEF + c1.PRC));
        }
        //calculate critical chance
        if((int)(Math.random() * 100) < c1.CRT) {
            damage *= 2;
            result = 3;
        }
        
        c2.currentHP -= damage;
        
        return result;
    }
    
    //calculate tile visibility from party[0]
    void calculateVis() {
        byte[][] map = new byte[sizeX][sizeY];
        for(int x = 0; x < sizeX; x++)
            for(int y = 0; y < sizeY; y++)
                map[x][y] = -1;
        
        //lower visibilities for hostile maps
        if(location.type.equals("dungeon") || location.type.equals("cave")
                || location.type.equals("cave")) {
            for(int x = 0; x < sizeX; x++) {
                for(int y = 0; y < sizeY; y++) {
                    if(tiles[x][y].vis > 20)
                        tiles[x][y].vis = 20;
                }
            }
        }
        
        //breadth-first recursive fill
        map[party[0].x][party[0].y] = 0;
        int limit = model.getVisDist();
        tileVis(map, party[0].x, party[0].y, limit);
        
        //set tile visibilities
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                if(map[x][y] != -1) {
                    //set 3x3 area around visible tile
                    for(int x2 = x-1; x2 < x+2; x2++) {
                        for(int y2 = y-1; y2 < y+2; y2++) {
                            //bounds and check for higher visibility
                            if(x2 >= 0 && x2 < sizeX && y2 >= 0 && y2 < sizeY &&
                                    tiles[x2][y2].vis < 64 / limit * 
                                    (limit - map[x][y])) {
                                tiles[x2][y2].vis = (byte)(64 / limit * 
                                        (limit - map[x][y]));
                            }
                        }
                    }
                }
            }
        }
    }
    
    //recursive breadth-first pathing
    private void tileVis(byte[][] map, int x, int y, int limit) {
        if(map[x][y] == limit)
            return;
        
        if(x > 0 && (map[x-1][y] > map[x][y]+1 || map[x-1][y] == -1) 
                && !tiles[x-1][y].isWall) {
            map[x-1][y] = (byte)(map[x][y]+1);
            tileVis(map, x-1, y, limit);
        }
        if(x < sizeX-1 && (map[x+1][y] > map[x][y]+1 || map[x+1][y] == -1) 
                && !tiles[x+1][y].isWall) {
            map[x+1][y] = (byte)(map[x][y]+1);
            tileVis(map, x+1, y, limit);
        }
        if(y > 0 && (map[x][y-1] > map[x][y]+1 || map[x][y-1] == -1) 
                && !tiles[x][y-1].isWall) {
            map[x][y-1] = (byte)(map[x][y]+1);
            tileVis(map, x, y-1, limit);
        }
        if(y < sizeY-1 && (map[x][y+1] > map[x][y]+1 || map[x][y+1] == -1) 
                && !tiles[x][y+1].isWall) {
            map[x][y+1] = (byte)(map[x][y]+1);
            tileVis(map, x, y+1, limit);
        }
    }
    
    //swap two locations in the character array
    private void swap(int sx, int sy, int ex, int ey) {
        Character temp = chars[sx][sy];
        chars[sx][sy] = chars[ex][ey];
        chars[ex][ey] = temp;
        chars[sx][sy].x = sx;
        chars[sx][sy].y = sy;
        chars[ex][ey].x = ex;
        chars[ex][ey].y = ey;
    }
    
    //convert map to boolean for walls
    private boolean[][] booleanMap() {
        //default all false
        boolean[][] boolMap = new boolean[sizeX][sizeY];
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                //characters and walls are true
                if(tiles[x][y].isWall && !tiles[x][y].openable)
                    boolMap[x][y] = true;
                else if(chars[x][y].exists && !chars[x][y].equals(party[0]))
                    boolMap[x][y] = true;
            }
        }
        
        return boolMap;
    }
    
    //drop an item on the ground
    public void dropItem(Item item, int x, int y) {
        //simple placement if no item already exists
        if(!items[x][y].exists && !tiles[x][y].isWall && tiles[x][y].floorMovement == 0) {
            items[x][y] = item;
        }
        else {
            //breadth first search for empty space
            ArrayDeque<Point> ad = new ArrayDeque();
            Point p;
            for(p = new Point(x, y); p != null; p = ad.poll()) {
                if(!tiles[p.x][p.y].isWall) {
                    if(!items[p.x][p.y].exists && tiles[p.x][p.y].floorMovement == 0)
                        break;
                    if(p.x < sizeX - 1)
                        ad.add(new Point(p.x+1, p.y));
                    if(p.x > 0)
                        ad.add(new Point(p.x-1, p.y));
                    if(p.y < sizeY - 1)
                        ad.add(new Point(p.x, p.y+1));
                    if(p.y > 0)
                        ad.add(new Point(p.x, p.y-1));
                }
            }
            //if empty space was found
            if(p != null) {
                items[p.x][p.y] = item;
            }
        }
    }
    
    //generate a floor given parameters
    private void generate(String type, int size) {
        switch(type) {
            case "dungeon":
                generateDungeon(size);
                break;
            case "tower":
                generateTower(size);
                break;
            case "cave":
                generateCave(size);
                break;
            case "city":
                generateCity(size);
                break;
        }
    }
    
    //generate dungeon
    private void generateDungeon(int size) {
        //choose size, initialize arrays
        switch(size) {
            case 1: sizeX = 10; sizeY = 6; break;
            case 2: sizeX = 20; sizeY = 12; break;
            case 3: sizeX = 30; sizeY = 18; break;
            case 4: sizeX = 40; sizeY = 24; break;
        }
        tiles = new Tile[sizeX][sizeY];
        chars = new Character[sizeX][sizeY];
        items = new Item[sizeX][sizeY];
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                if(x == 0 || x == sizeX-1 || y == 0 || y == sizeY-1)
                    tiles[x][y] = new Tile("StoneWall");
                else tiles[x][y] = new Tile("StoneFloor"+(int)(Math.random()*3+1));
                items[x][y] = new Item();
                chars[x][y] = new Character();
            }
        }

        //generate wall somewhere
        int hWall = (int)(Math.random() * (sizeY-4)) + 2;
        for(int i = 0; i < sizeX; i++)
            tiles[i][hWall] = new Tile("StoneWall");
        int hDoor = (int)(Math.random() * (sizeX-2) + 1);
        tiles[hDoor][hWall] = new Tile("Door");

        //vertical wall somewhere
        int vWall1 = (int)(Math.random() * (sizeX-4)) + 2;
        vWall1 = (vWall1 == hDoor ? vWall1+1: vWall1);
        for(int i = hWall; i < sizeY; i++)
            tiles[vWall1][i] = new Tile("StoneWall");
        tiles[vWall1][(int)(Math.random() * (sizeY-2-hWall) +hWall+1)] = new Tile("Door");

        //other vertical wall
        int vWall2 = (int)(Math.random() * (sizeX-4)) + 2;
        vWall2 = (vWall2 == hDoor ? vWall2+1: vWall2);
        for(int i = hWall; i >= 0; i--)
            tiles[vWall2][i] = new Tile("StoneWall");
        tiles[vWall2][(int)(Math.random() * (hWall-1) + 1)] = new Tile("Door");

        //generate stairsup if not at top
        if(floorNum != location.numFloors - 1) {
            while(true) {
                int x = (int)(Math.random() * sizeX);
                int y = (int)(Math.random() * sizeY);
                if(!tiles[x][y].isWall) {
                    tiles[x][y] = new Tile("StairsUp");
                    endX = x;
                    endY = y;
                    break;
                }
            }
        }

        //generate stairsdown if not at bottom
        while(true) {
            int x = (int)(Math.random() * sizeX);
            int y = (int)(Math.random() * sizeY);
            if(!tiles[x][y].isWall && tiles[x][y].floorMovement == 0) {
                tiles[x][y] = new Tile("StairsDown");
                enterX = x;
                enterY = y;
                break;
            }
        }

        //generate enemies
        int nEnemies = (int)(Math.random() * (Math.sqrt((double)sizeX*sizeY)/1.5)+1);
        npcs = new Character[nEnemies];
        for(int i = 0; i < nEnemies; i++) {
            while(true) {
                int x = (int)(Math.random() * sizeX);
                int y = (int)(Math.random() * sizeY);
                if(!tiles[x][y].isWall && !chars[x][y].exists) {
                    npcs[i] = new Character();
                    npcs[i].generateEnemy();
                    npcs[i].x = x;
                    npcs[i].y = y;
                    chars[x][y] = npcs[i];
                    break;
                }
            }
        }
    }
    
    //generate tower
    private void generateTower(int size) {
        //choose size
        switch(size) {
            case 1: sizeX = 9; sizeY = 9; break;
            case 2: sizeX = 17; sizeY = 17; break;
            case 3: sizeX = 27; sizeY = 27; break;
            case 4: sizeX = 39; sizeY = 39; break;
        }
        tiles = new Tile[sizeX][sizeY];
        chars = new Character[sizeX][sizeY];
        items = new Item[sizeX][sizeY];
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                tiles[x][y] = new Tile("WoodWall");
                items[x][y] = new Item();
                chars[x][y] = new Character();
            }
        }

        //create rectangles and empty
        for(int x = sizeX / 2 - size, y = 1; x > 0 && y < sizeY / 2; 
                x -= (y < size + 1 ? size + 1 - y : 1), 
                y += (x < size + 1 ? size + 1 - x : 1)) {
            for(int x2 = x; x2 < sizeX - x; x2++) {
                for(int y2 = y; y2 < sizeY - y; y2++) {
                    tiles[x2][y2] = new Tile("WoodFloor");
                }
            }
        }

        //create walls and doors
        for(int x = 1; x < sizeX; x++)
            tiles[x][sizeY/2] = new Tile("WoodWall");
        for(int y = 1; y < sizeY; y++)
            tiles[sizeX/2][y] = new Tile("WoodWall");
        int rand = (int)(Math.random()*4);
        if(rand != 0) 
            tiles[(int)(Math.random()*(sizeX/2-1)+1)][sizeY/2] = new Tile("Door");
        if(rand != 1) 
            tiles[(int)(Math.random()*(sizeX/2-2)+sizeX/2+1)][sizeY/2] = new Tile("Door");
        if(rand != 2) 
            tiles[sizeX/2][(int)(Math.random()*(sizeY/2-1)+1)] = new Tile("Door");
        if(rand != 3) 
            tiles[sizeX/2][(int)(Math.random()*(sizeY/2-2)+sizeY/2+1)] = new Tile("Door");

        //generate stairsup if not at top
        if(floorNum != location.numFloors - 1) {
            while(true) {
                int x = (int)(Math.random() * sizeX);
                int y = (int)(Math.random() * sizeY);
                if(!tiles[x][y].isWall) {
                    tiles[x][y] = new Tile("StairsUp");
                    endX = x;
                    endY = y;
                    break;
                }
            }
        }

        //generate stairsdown
        while(true) {
            int x = (int)(Math.random() * sizeX);
            int y = (int)(Math.random() * sizeY);
            if(!tiles[x][y].isWall && tiles[x][y].floorMovement == 0) {
                tiles[x][y] = new Tile("StairsDown");
                enterX = x;
                enterY = y;
                break;
            }
        }

        //generate enemies
        int nEnemies = (int)(Math.random() * (Math.sqrt((double)sizeX * sizeY) / 1.5) + 1);
        if(floorNum == location.numFloors - 1) nEnemies++;
        npcs = new Character[nEnemies];
        for(int i = 0; i < nEnemies; i++) {
            while(true) {
                int x = (int)(Math.random() * sizeX);
                int y = (int)(Math.random() * sizeY);
                if(!tiles[x][y].isWall && !chars[x][y].exists) {
                    npcs[i] = new Character();
                    npcs[i].generateEnemy();
                    npcs[i].x = x;
                    npcs[i].y = y;
                    chars[x][y] = npcs[i];
                    break;
                }
            }
        }

        //temp boss
        if(floorNum == location.numFloors - 1) {
            while(true) {
                int x = (int)(Math.random() * sizeX);
                int y = (int)(Math.random() * sizeY);
                if(!tiles[x][y].isWall && !chars[x][y].exists) {
                    npcs[nEnemies-1].generateBoss();
                    npcs[nEnemies-1].x = x;
                    npcs[nEnemies-1].y = y;
                    chars[x][y] = npcs[nEnemies-1];
                    break;
                }
            }
        }
    }
    
    //generate cave
    private void generateCave(int size) {
        //choose size
        switch(size) {
            case 1: sizeX = 30; sizeY = 15; break;
            case 2: sizeX = 40; sizeY = 20; break;
            case 3: sizeX = 50; sizeY = 25; break;
            case 4: sizeX = 60; sizeY = 30; break;
        }
        tiles = new Tile[sizeX][sizeY];
        chars = new Character[sizeX][sizeY];
        items = new Item[sizeX][sizeY];

        //border
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                if(x == 0 || x == sizeX-1 || y == 0 || y == sizeY-1)
                    tiles[x][y] = new Tile("StoneWall");
                else tiles[x][y] = new Tile("StoneFloor"+(int)(Math.random()*3+1));
                items[x][y] = new Item();
                chars[x][y] = new Character();
            }
        }

        //generate upper wall
        for(int x = 0, y = sizeY / 5 + 2; x < sizeX; x++) {
            y += (int)(Math.random() * 3) - 1;
            for(int i = (int)(Math.random() * 4 + 2); i > 0 && x < sizeX; i--, x++) {
                for(int y2 = y; y2 > 0; y2--) {
                    tiles[x][y2] = new Tile("StoneWall");
                }
            }
            x--;
        }

        //generate lower wall
        for(int x = 0, y = sizeY / 5 * 4 - 2; x < sizeX; x++) {
            y += (int)(Math.random() * 3) - 1;
            //don't close off
            if(y <= sizeY && tiles[x][y-2].isWall)
                y++;
            for(int i = (int)(Math.random() * 4 + 2); i > 0 && x < sizeX; i--, x++) {
                for(int y2 = y; y2 < sizeY; y2++) {
                    tiles[x][y2] = new Tile("StoneWall");
                }
            }
            x--;
        }

        //generate stairsup if not at top
        if(floorNum != location.numFloors - 1) {
            while(true) {
                int x = (int)(Math.random() * sizeX);
                int y = (int)(Math.random() * sizeY);
                if(!tiles[x][y].isWall) {
                    tiles[x][y] = new Tile("StairsUp");
                    endX = x;
                    endY = y;
                    break;
                }
            }
        }

        //generate stairsdown
        while(true) {
            int x = (int)(Math.random() * sizeX);
            int y = (int)(Math.random() * sizeY);
            if(!tiles[x][y].isWall && tiles[x][y].floorMovement == 0) {
                tiles[x][y] = new Tile("StairsDown");
                enterX = x;
                enterY = y;
                break;
            }
        }

        //generate enemies
        int nEnemies = (int)(Math.random() * (Math.sqrt((double)sizeX * sizeY) / 1.5) + 1);
        npcs = new Character[nEnemies];
        for(int i = 0; i < nEnemies; i++) {
            while(true) {
                int x = (int)(Math.random() * sizeX);
                int y = (int)(Math.random() * sizeY);
                if(!tiles[x][y].isWall && !chars[x][y].exists) {
                    npcs[i] = new Character();
                    npcs[i].generateEnemy();
                    npcs[i].x = x;
                    npcs[i].y = y;
                    chars[x][y] = npcs[i];
                    break;
                }
            }
        }
    }
    
    //generate city
    private void generateCity(int size) {
        //initialize arrays
        sizeX = 4+6*(size*3);
        sizeY = 4+6*(size*3);
        tiles = new Tile[sizeX][sizeY];
        chars = new Character[sizeX][sizeY];
        items = new Item[sizeX][sizeY];
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                tiles[x][y] = new Tile("Grass");
                items[x][y] = new Item();
                chars[x][y] = new Character();
            }
        }
        
        boolean[][] filled = new boolean[size*3+1][size*3+1];
        
        //farm
        if(size == 1) {
            for(int x = 0; x < size*3; x++) {
                for(int y = 0; y < size*3; y++) {
                    if(!filled[x][y]) {
                        switch((int)(Math.random()*7)) {
                            case 0:
                                genSmallHouse(filled, x, y);
                                break;
                            case 1:
                                genRandomHouses(filled, x, y);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        //village
        else if(size == 2) {
            for(int x = 0; x < size*3; x++) {
                for(int y = 0; y < size*3; y++) {
                    if(!filled[x][y]) {
                        switch((int)(Math.random()*10)) {
                            case 0:
                                genSmallHouse(filled, x, y);
                                break;
                            case 1:
                                genRandomHouses(filled, x, y);
                                break;
                            case 2:
                                genSmallInn(filled, x, y);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        //stone
        else if(size == 3) {
            //outside wall
            for(int x = 1; x < sizeX-1; x++) {
                for(int y = 1; y < sizeY-1; y++) {
                    if(x == 1 || x == sizeX-2 || y == 1 || y == sizeY-2)
                        tiles[x][y] = new Tile("StoneWall");
                    else
                        tiles[x][y] = new Tile("StoneFloor" + (int)(Math.random()*3+1));
                }
            }
            //openings
            for(int i = -1; i < 2; i++) {
                tiles[sizeX/2+i][1] = new Tile("StoneFloor" + (int)(Math.random()*3+1));
                tiles[sizeX/2+i][sizeY-2] = new Tile("StoneFloor" + (int)(Math.random()*3+1));
            }
            
            //tiles
            for(int x = 0; x < size*3; x++) {
                for(int y = 0; y < size*3; y++) {
                    if(!filled[x][y]) {
                        switch((int)(Math.random()*7)) {
                            case 0:
                                genSmallHouse(filled, x, y);
                                break;
                            case 1:
                                genBigInn(filled, x, y);
                                break;
                            case 2:
                                genRandomHouses(filled, x, y);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        //bigger stone
        else if(size == 4) {
            //outside wall
            for(int x = 1; x < sizeX-1; x++) {
                for(int y = 1; y < sizeY-1; y++) {
                    if(x == 1 || x == sizeX-2 || y == 1 || y == sizeY-2)
                        tiles[x][y] = new Tile("StoneWall");
                    else
                        tiles[x][y] = new Tile("StoneFloor" + (int)(Math.random()*3+1));
                }
            }
            //openings
            for(int i = -2; i < 3; i++) {
                tiles[sizeX/2+i][1] = new Tile("StoneFloor" + (int)(Math.random()*3+1));
                tiles[sizeX/2+i][sizeY-2] = new Tile("StoneFloor" + (int)(Math.random()*3+1));
            }
            
            //tiles
            for(int x = 0; x < size*3; x++) {
                for(int y = 0; y < size*3; y++) {
                    if(!filled[x][y]) {
                        switch((int)(Math.random()*5)) {
                            case 0:
                                genSmallHouse(filled, x, y);
                                break;
                            case 1:
                                genBigHall(filled, x, y);
                                break;
                            case 2:
                                genBigInn(filled, x, y);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        //metropolis
        else if(size == 5) {
            
        }

        //start at bottom of map
        enterX = sizeX/2;
        enterY = sizeY-1;

        npcs = new Character[2];
        npcs[0] = new Character();
        npcs[0].generateNPC();
        npcs[1] = new Character();
        npcs[1].generateNPC();
        npcs[0].x = 2;
        npcs[0].y = 2;
        chars[2][2] = npcs[0];
        npcs[1].x = sizeX/2+1;
        npcs[1].y = sizeY-1;
        chars[sizeX/2+1][sizeY-1] = npcs[1];
        
        //temp: all tiles visible
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                tiles[x][y].vis = 64;
            }
        }
    }
    
        //extra methods for city gen
    //1x1
    private void genRandomHouses(boolean[][] filled, int x, int y) {
        if(!checkClear(filled, x, y, 1, 1))
            return;
        fillArea(filled, x, y, 1, 1);
        
        String[] s = null;
        switch((int)(Math.random()*5)) {
            case 0:
                s = condense(
                        "      ",
                        " 00   ",
                        " 12   ",
                        "      ",
                        "   00 ",
                        "   12 "
                );
                break;
            case 1:
                s = condense(
                        "    00",
                        "    12",
                        " 00   ",
                        " 12   ",
                        "      ",
                        "      "
                );
                break;
            case 2:
                s = condense(
                        "      ",
                        "00    ",
                        "12  00",
                        "    12",
                        " 00   ",
                        " 12   "
                );
                break;
            case 3:
                s = condense(
                        "      ",
                        "   00 ",
                        "   12 ",
                        "      ",
                        "00    ",
                        "12    "
                );
                break;
            case 4:
                s = condense(
                        "      ",
                        " 0000 ",
                        " 1212 ",
                        " 0000 ",
                        " 1212 ",
                        "      "
                );
                break;
        }
        
        interpretMap(s, x*6+2, y*6+2, "WoodWall", "WoodWallWindow", "WoodWallDoor");
    }
    
    //1x1
    private void genSmallHouse(boolean[][] filled, int x, int y) {
        if(!checkClear(filled, x, y, 1, 1))
            return;
        fillArea(filled, x, y, 1, 1);
        
        String[] s = condense(
                "      ",
                " 1111 ",
                " 1001 ",
                " 1001 ",
                " 1231 ",
                "      "
        );
        interpretMap(s, x*6+2, y*6+2, "WoodFloor", "WoodWall", "WoodWallWindow", "Door");
    }
    
    //1x2
    private void genSmallInn(boolean[][] filled, int x, int y) {
        if(!checkClear(filled, x, y, 1, 2))
            return;
        fillArea(filled, x, y, 1, 2);
        
        String[] s = condense(
                " 1111 ",
                " 1001 ",
                " 2002 ",
                " 1001 ",
                " 1001 ",
                "11121 ",
                "10001 ",
                "10001 ",
                "11121 ",
                "10001 ",
                "10001 ",
                "11111 "
        );
        interpretMap(s, x*6+2, y*6+2, "WoodFloor", "WoodWall", "Door");
    }
    
    //2x2
    private void genBigInn(boolean[][] filled, int x, int y) {
        if(!checkClear(filled, x, y, 2, 2))
            return;
        fillArea(filled, x, y, 2, 2);
        
        String[] s = condense(
                "            ",
                " 00000      ",
                " 01110      ",
                " 21112      ",
                " 01110      ",
                " 0020000200 ",
                " 0111111110 ",
                " 0020020020 ",
                " 0110110110 ",
                " 0110110110 ",
                " 0000000000 ",
                "            "
        );
        interpretMap(s, x*6+2, y*6+2, "WoodWall", "WoodFloor", "Door");
    }
    
    //3x2
    private void genBigHall(boolean[][] filled, int x, int y) {
        if(!checkClear(filled, x, y, 3, 2))
            return;
        fillArea(filled, x, y, 3, 2);
        
        String[] s = condense(
                "                  ",
                " 0000  0000  0000 ",
                " 0110000110000110 ",
                " 0111111111111110 ",
                " 0011111111111100 ",
                "  01111111111110  ",
                "  01111111111110  ",
                " 0011111111111100 ",
                " 0111111111111110 ",
                " 0110020000200110 ",
                " 0000   00   0000 ",
                "                  "
        );
        interpretMap(s, x*6+2, y*6+2, "WoodWall", "WoodFloor", "Door");
    }
    
    //check area to see if clear: also OOB
    private boolean checkClear(boolean[][] filled, int x, int y, int width, int height) {
        if(6*(x+width)+2 >= sizeX || 6*(y+height)+2 >= sizeY)
            return false;
        
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                if(x+i >= filled.length || y+j >= filled[0].length || filled[x+i][y+j]) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    //fill area: bounds should already be checked
    private void fillArea(boolean[][] filled, int x, int y, int width, int height) {
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                filled[x+i][y+j] = true;
            }
        }
    }
    
    //take map, fill tiles with given tile types
    private void interpretMap(String[] map, int sx, int sy, String... names) {
        for(int y = 0; y < map.length; y++) {
            for(int x = 0; x < map[0].length(); x++) {
                //ignore ' ' tiles
                if(map[y].charAt(x) != ' ') {
                    tiles[sx+x][sy+y] = new Tile(names[Integer.valueOf(map[y].substring(x, x+1))]);
                }
            }
        }
    }
    
    //condense strings into array
    private String[] condense(String... s) {
        return s;
    }
}