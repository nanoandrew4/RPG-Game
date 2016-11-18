/*
    Owned by Location.
    Contains data for each floor of a location.
 */

package inmap;

import java.awt.Point;

import main.Control;

class Floor implements java.io.Serializable{
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
    Floor(InMapModel model, Location location, int floorNum, String type, int diff, int size, Character[] party) {
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
                chars[enterX][enterY].kill();
                chars[enterX][enterY] = party[0];
                break;
            case DOWN:
                party[0].x = endX;
                party[0].y = endY;
                chars[endX][endY].kill();
                chars[endX][endY] = party[0];
                break;
        }
    }
    
    //process player input
    void processPlayer(Control direction) {
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
        
        process(sx, sy, ex, ey);
    }
    
    //process all npc movement
    void processAI() {
        for(Character n : npcs) {
            if(n.exists) {
                
                int dx = 0, dy = 0;
                switch(n.AIMode) {
                    case "attacking":
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
                    case "fleeing": 
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
                    case "wandering":
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
                            n.AIMode = "attacking";
                        }
                        break;
                    case "stationary":
                        break;
                    case "NA":
                        break;
                    default: 
                        break;
                }
                
                process(n.x, n.y, n.x+dx, n.y+dy);
            }
        }
    }
    
    //process movement
    private void process(int sx, int sy, int ex, int ey) {
        //standing still: do nothing
        if(sx == sy && ex == ey) {
            
        }
        //interaction with npc
        else if(chars[sx][sy].exists && chars[ex][ey].exists) {
            //attacking
            if(!chars[sx][sy].race.name.equals(chars[ex][ey].race.name) 
                    || chars[ex][ey].AIMode.equals("attacking")) {
                attack(chars[sx][sy], chars[ex][ey]);
                if(chars[ex][ey].currentHP <= 0) {
                    chars[sx][sy].gainEXP(chars[ex][ey]);
                    chars[ex][ey].kill();
                }
            }
            //talking with npcs
            else if(chars[ex][ey].AIMode.equals("wandering")) {
                
            }
        }
        //moving floors
        else if(tiles[ex][ey].floorMovement != 0 && chars[sx][sy].name.equals("Hero")) {
            location.changeFloor(tiles[ex][ey].floorMovement);
            chars[sx][sy] = new Character();
        }
        //moving normally
        else if(!tiles[ex][ey].isWall) {
            swap(sx, sy, ex, ey);
        }
        //open doors
        else if(tiles[ex][ey].openable) {
            tiles[ex][ey].isWall = false;
        }
    }
    
    //simulate c1 attacking c2, return attack result
    /*  Legend:
        0 - Hit
        1 - c2 Evaded
        2 - c1 Missed
        3 - critical
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
        if(c1.weapon.type.equals("m") && c2.RES > c1.PRC)
            damage *= (c1.PRC / (c2.RES + c1.PRC));
        else if(c2.RES > c1.PRC)
            damage *= (c1.PRC / (c2.DEF + c1.PRC));
        //calculate critical chance
        if((int)(Math.random() * 100) < c1.CRT) {
            damage *= 2;
            result = 3;
        }
        
        c2.currentHP -= damage;
        
        return result;
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
    
    //convert map to boolean
    private boolean[][] booleanMap() {
        boolean[][] boolMap = new boolean[sizeX][sizeY];
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                if(tiles[x][y].isWall && !tiles[x][y].openable)
                    boolMap[x][y] = true;
                else if(chars[x][y].exists && !chars[x][y].equals(party[0]))
                    boolMap[x][y] = true;
            }
        }
        
        return boolMap;
    }
    
    //generate a floor given parameters
    private void generate(String type, int size) {
        
        int nEnemies;
        
        switch(type) {
            
            case "dungeon": {
                
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
                            tiles[x][y] = new Tile("wall");
                        else tiles[x][y] = new Tile();
                        chars[x][y] = new Character();
                    }
                }
                
                //generate wall somewhere
                int hWall = (int)(Math.random() * (sizeY-4)) + 2;
                for(int i = 0; i < sizeX; i++)
                    tiles[i][hWall] = new Tile("wall");
                int hDoor = (int)(Math.random() * (sizeX-2) + 1);
                tiles[hDoor][hWall] = new Tile("door");
                
                //vertical wall somewhere
                int vWall1 = (int)(Math.random() * (sizeX-4)) + 2;
                vWall1 = (vWall1 == hDoor ? vWall1+1: vWall1);
                for(int i = hWall; i < sizeY; i++)
                    tiles[vWall1][i] = new Tile("wall");
                tiles[vWall1][(int)(Math.random() * (sizeY-2-hWall) +hWall+1)] = new Tile("door");
                
                //other vertical wall
                int vWall2 = (int)(Math.random() * (sizeX-4)) + 2;
                vWall2 = (vWall2 == hDoor ? vWall2+1: vWall2);
                for(int i = hWall; i >= 0; i--)
                    tiles[vWall2][i] = new Tile("wall");
                tiles[vWall2][(int)(Math.random() * (hWall-1) + 1)] = new Tile("door");
                
                //generate stairsup if not at top
                if(floorNum != location.numFloors - 1) {
                    while(true) {
                        int x = (int)(Math.random() * sizeX);
                        int y = (int)(Math.random() * sizeY);
                        if(!tiles[x][y].isWall) {
                            tiles[x][y] = new Tile("stairsUp");
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
                        tiles[x][y] = new Tile("stairsDown");
                        enterX = x;
                        enterY = y;
                        break;
                    }
                }
                
                //generate enemies
                nEnemies = (int)(Math.random() * (Math.sqrt((double)sizeX*sizeY)/1.5)+1);
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
                break;
            }
            case "tower": {
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
                        tiles[x][y] = new Tile("wall");
                        chars[x][y] = new Character();
                    }
                }
                
                //create rectangles and empty
                for(int x = sizeX / 2 - size, y = 1; x > 0 && y < sizeY / 2; 
                        x -= (y < size + 1 ? size + 1 - y : 1), 
                        y += (x < size + 1 ? size + 1 - x : 1)) {
                    for(int x2 = x; x2 < sizeX - x; x2++) {
                        for(int y2 = y; y2 < sizeY - y; y2++) {
                            tiles[x2][y2] = new Tile();
                        }
                    }
                }
                
                //create walls and doors
                for(int x = 1; x < sizeX; x++)
                    tiles[x][sizeY/2] = new Tile("wall");
                for(int y = 1; y < sizeY; y++)
                    tiles[sizeX/2][y] = new Tile("wall");
                int rand = (int)(Math.random()*4);
                if(rand != 0) tiles[(int)(Math.random()*(sizeX/2-1)+1)][sizeY/2] = new Tile("door");
                if(rand != 1) tiles[(int)(Math.random()*(sizeX/2-2)+sizeX/2+1)][sizeY/2] = new Tile("door");
                if(rand != 2) tiles[sizeX/2][(int)(Math.random()*(sizeY/2-1)+1)] = new Tile("door");
                if(rand != 3) tiles[sizeX/2][(int)(Math.random()*(sizeY/2-2)+sizeY/2+1)] = new Tile("door");
                
                //generate stairsup if not at top
                if(floorNum != location.numFloors - 1) {
                    while(true) {
                        int x = (int)(Math.random() * sizeX);
                        int y = (int)(Math.random() * sizeY);
                        if(!tiles[x][y].isWall) {
                            tiles[x][y] = new Tile("stairsUp");
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
                        tiles[x][y] = new Tile("stairsDown");
                        enterX = x;
                        enterY = y;
                        break;
                    }
                }
                
                //generate enemies
                nEnemies = (int)(Math.random() * (Math.sqrt((double)sizeX * sizeY) / 1.5) + 1);
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
                
                break;
            }
            case "cave": {
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
                            tiles[x][y] = new Tile("wall");
                        else tiles[x][y] = new Tile();
                        chars[x][y] = new Character();
                    }
                }
                
                //generate upper wall
                for(int x = 0, y = sizeY / 5 + 2; x < sizeX; x++) {
                    y += (int)(Math.random() * 3) - 1;
                    for(int i = (int)(Math.random() * 4 + 2); i > 0 && x < sizeX; i--, x++) {
                        for(int y2 = y; y2 > 0; y2--) {
                            tiles[x][y2] = new Tile("wall");
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
                            tiles[x][y2] = new Tile("wall");
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
                            tiles[x][y] = new Tile("stairsUp");
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
                        tiles[x][y] = new Tile("stairsDown");
                        enterX = x;
                        enterY = y;
                        break;
                    }
                }
                
                //generate enemies
                nEnemies = (int)(Math.random() * (Math.sqrt((double)sizeX * sizeY) / 1.5) + 1);
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
                
                break;
            }
            case "city": {
                //initialize arrays
                sizeX = 12; sizeY = 8;
                tiles = new Tile[sizeX][sizeY];
                chars = new Character[sizeX][sizeY];
                items = new Item[sizeX][sizeY];
                for(int x = 0; x < sizeX; x++) {
                    for(int y = 0; y < sizeY; y++) {
                        if(x == 0 || x == sizeX-1 || y == 0 || y == sizeY-1)
                            tiles[x][y] = new Tile("wall");
                        else tiles[x][y] = new Tile();
                        chars[x][y] = new Character();
                    }
                }
                
                //stairs
                tiles[4][4] = new Tile("stairsUp");
                endX = 4; endY = 4;
                tiles[6][4] = new Tile("stairsDown");
                enterX = 6; enterY = 4;
                
                npcs = new Character[2];
                npcs[0] = new Character();
                npcs[0].generateNPC();
                npcs[1] = new Character();
                npcs[1].generateNPC();
                npcs[0].x = 2;
                npcs[0].y = 2;
                chars[2][2] = npcs[0];
                npcs[1].x = 4;
                npcs[1].y = 2;
                chars[4][2] = npcs[1];
                
                break;
            }
                
        }
    }
}