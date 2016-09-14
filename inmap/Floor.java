/*
    Owned by Location.
    Contains data for each floor of a location.
 */

package inmap;

public class Floor {
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
    Floor(Location location, int floorNum, String type, int diff, int size, Character[] party) {
        //init
        this.location = location;
        this.floorNum = floorNum;
        difficulty = diff;
        this.size = size;
        this.party = party;
        
        generate(type, size);
    }
    
    //ready for input processing
    void passControl(Direction direction) {
        switch(direction) {
            case Up:
                party[0].x = enterX;
                party[0].y = enterY;
                chars[enterX][enterY] = party[0];
                break;
            case Down:
                party[0].x = endX;
                party[0].y = endY;
                chars[endX][endY] = party[0];
                break;
        }
    }
    
    //process player input
    void processPlayer(Direction direction) {
        int sx = party[0].x;
        int sy = party[0].y;
        int ex = party[0].x;
        int ey = party[0].y;
        switch(direction) {
            case Up: ey = party[0].y - 1; break;
            case Down: ey = party[0].y + 1; break;
            case Left: ex = party[0].x - 1; break;
            case Right: ex = party[0].x + 1; break;
        }
        
        process(sx, sy, ex, ey);
    }
    
    //process all npc movement: currently just follow player
    void processAI() {
        for(Character n : npcs) {
            if(n.exists) {
                int dx, dy;
                switch(n.AIMode) {
                    case "NA": break;
                    case "hostile":
                        //x movement
                        if(party[0].x < n.x)
                            dx = -1;
                        else if(party[0].x > n.x)
                            dx = 1;
                        else dx = 0;
                        //y movement
                        if(party[0].y < n.y)
                            dy = -1;
                        else if(party[0].y > n.y)
                            dy = 1;
                        else dy = 0;
                        //randomly move either vertically or horizontally
                        if(Math.random() * 100 < 50)
                            dx = (dy == 0 ? dx : 0);
                        else 
                            dy = (dx == 0 ? dy : 0);

                        process(n.x, n.y, n.x+dx, n.y+dy);
                    default: break;
                }
            }
        }
    }
    
    //process movement
    private void process(int sx, int sy, int ex, int ey) {
        ///TODO: item processing
        if(chars[ex][ey].exists) {
            if(!chars[sx][sy].race.name.equals(chars[ex][ey].race.name) || chars[ex][ey].AIMode.equals("hostile")) {
                attack(chars[sx][sy], chars[ex][ey]);
                if(chars[ex][ey].currentHP <= 0) {
                    chars[sx][sy].gainEXP(chars[ex][ey]);
                    chars[ex][ey].kill();
                }
            }
            
        }
        else if(tiles[ex][ey].floorMovement != 0 && chars[sx][sy].name.equals("Hero")) {
            location.changeFloor(tiles[ex][ey].floorMovement);
            chars[sx][sy] = new Character();
        }
        else if(!tiles[ex][ey].isWall) {
            swap(sx, sy, ex, ey);
        }
        else if(tiles[ex][ey].openable) {
            tiles[ex][ey].isWall = false;
        }
    }
    
    //simulate c1 attacking c2, return attack result
    /*  Legend:
        0 - Hit
        1 - c2 Evaded
        2 - c1 Missed
    */
    private int attack(Character c1, Character c2) {
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
        
        c2.currentHP -= damage;
        
        return 0;
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
    
    //generate a floor with 4 rooms and 3 doors
    private void generate(String type, int size) {
        
        switch(type) {
            
            case "dungeon":
                
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
                tiles[vWall1][(int)(Math.random() * (sizeY - 2 - hWall) + hWall + 1)] = new Tile("door");
                
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
                int nEnemies = (int)(Math.random() * (Math.sqrt((double)sizeX * sizeY) / 1.5) + 1);
                npcs = new Character[nEnemies];
                for(int i = 0; i < nEnemies; i++) {
                    while(true) {
                        int x = (int)(Math.random() * sizeX);
                        int y = (int)(Math.random() * sizeY);
                        if(!tiles[x][y].isWall && !chars[x][y].exists) {
                            npcs[i] = new Character().generateEnemy();
                            npcs[i].x = x;
                            npcs[i].y = y;
                            chars[x][y] = npcs[i];
                            break;
                        }
                    }
                }
                break;
                
            case "tower":
                
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
                for(int x = sizeX / 2 - size, y = 1; x > 0 && y < sizeY / 2; x -= 1, y+= 1) {
                    for(int x2 = x; x2 < sizeX - x; x2++) {
                        for(int y2 = y; y2 < sizeY - y; y2++) {
                            tiles[x2][y2] = new Tile();
                        }
                    }
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
                nEnemies = (int)(Math.random() * (Math.sqrt((double)sizeX * sizeY) / 1000.5) + 1);
                npcs = new Character[nEnemies];
                for(int i = 0; i < nEnemies; i++) {
                    while(true) {
                        int x = (int)(Math.random() * sizeX);
                        int y = (int)(Math.random() * sizeY);
                        if(!tiles[x][y].isWall && !chars[x][y].exists) {
                            npcs[i] = new Character().generateEnemy();
                            npcs[i].x = x;
                            npcs[i].y = y;
                            chars[x][y] = npcs[i];
                            break;
                        }
                    }
                }
                
                break;
                
            case "cave":
                
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
                for(int x = 0; x < sizeX; x++) {
                    for(int y = 0; y < sizeY; y++) {
                        if(x == 0 || x == sizeX-1 || y == 0 || y == sizeY-1)
                            tiles[x][y] = new Tile("wall");
                        else tiles[x][y] = new Tile();
                        chars[x][y] = new Character();
                    }
                }
                
                //generate walls
                
                break;
        }
    }
}