/*
    Owned by Location.
    Contains data for each floor of a location.
 */

package inmap;

public class Floor {
    int sizeX, sizeY, difficulty;
    Tile[][] tiles;
    Character[][] chars;
    Character[] party; //[0] is the hero
    Character[] enemies;
    Item[][] items;
    
    //constructor
    Floor(int diff, int sX, int sY) {
        //init
        difficulty = diff;
        sizeX = sX;
        sizeY = sY;
        tiles = new Tile[sizeX][sizeY];
        chars = new Character[sizeX][sizeY];
        items = new Item[sizeX][sizeY];
        party = new Character[1];
        
        //empty everything
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                tiles[x][y] = new Tile();
                chars[x][y] = new Character();
                items[x][y] = new Item();
            }
        }
        
        generate(sizeX, sizeY);
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
    
    //process enemy movement
    void processAI() {
        for(Character e : enemies) {
            int dx, dy;
            if(e.exists) {
                //x movement
                if(party[0].x < e.x)
                    dx = -1;
                else if(party[0].x > e.x)
                    dx = 1;
                else dx = 0;
                //y movement
                if(party[0].y < e.y)
                    dy = -1;
                else if(party[0].y > e.y)
                    dy = 1;
                else dy = 0;
                //randomly move either vertically or horizontally
                if(Math.random() * 100 < 50)
                    dx = (dy == 0 ? dx : 0);
                else 
                    dy = (dx == 0 ? dy : 0);

                process(e.x, e.y, e.x+dx, e.y+dy);
            }
        }
    }
    
    //process movement
    private void process(int sx, int sy, int ex, int ey) {
        ///TODO: item processing
        if(chars[ex][ey].exists) {
             ///TODO: check for allies
             attack(chars[sx][sy], chars[ex][ey]);
             if(chars[ex][ey].currentHP <= 0)
                 chars[ex][ey] = new Character();
        }
        else if(tiles[ex][ey].floorMovement != 0 && chars[sx][sy].name.equals("Hero")) {
            generate(sizeX, sizeY);
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
    private void generate(int sX, int sY) {
        //basic init
        for(int x = 0; x < sX; x++) {
            for(int y = 0; y < sY; y++) {
                if(x == 0 || x == sX-1 || y == 0 || y == sY-1)
                    tiles[x][y] = new Tile("wall");
                else tiles[x][y] = new Tile();
                chars[x][y] = new Character();
            }
        }
        //generate wall somewhere
        int hWall = (int)(Math.random() * (sY-4)) + 2;
        for(int i = 0; i < sX; i++)
            tiles[i][hWall] = new Tile("wall");
        int hDoor = (int)(Math.random() * (sX-2) + 1);
        tiles[hDoor][hWall] = new Tile("door");
        //vertical wall somewhere
        int vWall1 = (int)(Math.random() * (sX-4)) + 2;
        vWall1 = (vWall1 == hDoor ? vWall1+1: vWall1);
        for(int i = hWall; i < sY; i++)
            tiles[vWall1][i] = new Tile("wall");
        tiles[vWall1][(int)(Math.random() * (sY - 2 - hWall) + hWall + 1)] = new Tile("door");
        //other vertical wall
        int vWall2 = (int)(Math.random() * (sX-4)) + 2;
        vWall2 = (vWall2 == hDoor ? vWall2+1: vWall2);
        for(int i = hWall; i >= 0; i--)
            tiles[vWall2][i] = new Tile("wall");
        tiles[vWall2][(int)(Math.random() * (hWall) + 1)] = new Tile("door");
        //generate stairs
        while(true) {
            int x = (int)(Math.random() * sX);
            int y = (int)(Math.random() * sY);
            if(!tiles[x][y].isWall) {
                tiles[x][y] = new Tile("stairsUp");
                break;
            }
        }
        //generate hero if first generation
        party[0] = new Character(1, 0, 10, 10, 90, 10, 10, 10, 10, "Hero");
        while(true) {
            int x = (int)(Math.random() * sX);
            int y = (int)(Math.random() * sY);
            if(!tiles[x][y].isWall && !chars[x][y].exists && tiles[x][y].floorMovement == 0) {
                party[0].x = x; party[0].y = y;
                chars[x][y] = party[0];
                break;
            }
        }
        //generate enemies
        int nEnemies = (int)(Math.random() * (sX * sY / 20) + 1);
        enemies = new Character[nEnemies];
        for(int i = 0; i < nEnemies; i++) {
            while(true) {
                int x = (int)(Math.random() * sX);
                int y = (int)(Math.random() * sY);
                if(!tiles[x][y].isWall && !chars[x][y].exists) {
                    enemies[i] = new Character().generateEnemy();
                    enemies[i].x = x;
                    enemies[i].y = y;
                    chars[x][y] = enemies[i];
                    break;
                }
            }
        }
    }
}
