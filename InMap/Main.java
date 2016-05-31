/*
    Rising Legend test program. :D
 */

package rlinmaptesting;

import java.util.*;

public class RLInMapTesting {
    
    public static int mX = 20, mY = 10;
    
    public static void main(String[] args) {
        //vars
        int nEnemies = mX*mY/20, floor = 1;
        Tile[][] tiles = new Tile[mX][mY];
        Character[][] chars = new Character[mX][mY];
        Character[] enemies = new Character[nEnemies];
        Character[] heroes = new Character[1]; //only one for now
        Scanner in = new Scanner(System.in);
        String input;
        
        //generate map
        nEnemies = generateMap(true, tiles, chars, heroes, enemies);
        
        //print once
        print(tiles, chars);
        
        //loop
        while(heroes[0].cHP > 0) {
            //get input
            input = in.nextLine();
            int dx = 0, dy = 0;
            switch(input) {
                case "w": dy = -1; break;
                case "a": dx = -1; break;
                case "s": dy = 1; break;
                case "d": dx = 1; break;
                default: break;
            }
            
            //process input
            if(dx == 0 && dy == 0) {
                //do nothing
            }
            //moving up floors
            else if(tiles[heroes[0].x+dx][heroes[0].y+dy].stairUp && !chars[heroes[0].x+dx][heroes[0].y+dy].exists) {
                floor++;
                nEnemies = generateMap(false, tiles, chars, heroes, enemies);
                System.out.println("Floor " + floor);
            }
            //if moved, change coordinate location
            else if(process(tiles, chars, heroes[0].x, heroes[0].y, heroes[0].x+dx, heroes[0].y+dy)) {
                heroes[0].x += dx;
                heroes[0].y += dy;
            }
            
            //npc moving: go through enemies list
            for(int i = 0; i < nEnemies; i++) {
                if(enemies[i].exists && !enemies[i].isItem) {
                    if(Math.random() * 100 < 95) { //move chance
                        //x movement
                        if(heroes[0].x < enemies[i].x)
                            dx = -1;
                        else if(heroes[0].x > enemies[i].x)
                            dx = 1;
                        else dx = 0;
                        //y movement
                        if(heroes[0].y < enemies[i].y)
                            dy = -1;
                        else if(heroes[0].y > enemies[i].y)
                            dy = 1;
                        else dy = 0;
                        //randomly move either vertically or horizontally
                        if(Math.random() * 100 < 50)
                            dx = (dy == 0 ? dx : 0);
                        else 
                            dy = (dx == 0 ? dy : 0);

                        if(process(tiles, chars, enemies[i].x, enemies[i].y, enemies[i].x+dx, enemies[i].y+dy)) {
                            enemies[i].x += dx;
                            enemies[i].y += dy;
                        }
                    }
                }
            }
            
            print(tiles, chars);
        }
        
        System.out.print("RIP.\nYou made it to floor " + floor + ".\n");
    }
    
    //print game
    static void print(Tile[][] tiles, Character[][] chars) {
        //top row
        System.out.print("+");
        for(int i = 0; i < mX; i++)
            System.out.print("--------+");
        System.out.print("\n");
        
        //print rest of tiles
        for(int i = 0; i < mY; i++) {
            for(int j = 0; j < mX; j++)
                System.out.print("|        ");
            System.out.print("|\n|");
            for(int j = 0; j < mX; j++) {
                if(chars[j][i].exists) {
                    switch(chars[j][i].name.length()) {
                        case 1: System.out.print("   "); break;
                        case 2: System.out.print("   "); break;
                        case 3: System.out.print("  "); break;
                        case 4: System.out.print("  "); break;
                        case 5: System.out.print(" "); break;
                        case 6: System.out.print(" "); break;
                        default: break;
                    }
                    System.out.print(chars[j][i].name);
                    switch(chars[j][i].name.length()) {
                        case 1: System.out.print("    "); break;
                        case 2: System.out.print("   "); break;
                        case 3: System.out.print("   "); break;
                        case 4: System.out.print("  "); break;
                        case 5: System.out.print("  "); break;
                        case 6: System.out.print(" "); break;
                        case 7: System.out.print(" "); break;
                        default: break;
                    }
                }
                else if(tiles[j][i].openable && tiles[j][i].isWall)
                    System.out.print("  Door  ");
                else if(tiles[j][i].openable && !tiles[j][i].isWall)
                    System.out.print("OpenDoor");
                else if(tiles[j][i].stairUp)
                    System.out.print(" Stairs ");
                else if(tiles[j][i].stairDown)
                    System.out.print("  DNE   ");
                else if(tiles[j][i].isWall)
                    System.out.print("  Wall  ");
                else 
                    System.out.print("        ");
                System.out.print("|");
            }
            //print HP if applicable
            System.out.print("\n|");
            for(int j = 0; j < mX; j++) {
                if(chars[j][i].exists && !chars[j][i].isItem) {
                    System.out.print("HP: ");
                    System.out.print(chars[j][i].cHP);
                    if(chars[j][i].cHP > 999)
                        System.out.print("|");
                    else if(chars[j][i].cHP > 99)
                        System.out.print(" |");
                    else if(chars[j][i].cHP > 9)
                        System.out.print("  |");
                    else if(chars[j][i].cHP > 0)
                        System.out.print("   |");
                }
                else System.out.print("        |");
            }
            System.out.print("\n+");
            for(int j = 0; j < mX; j++)
                System.out.print("--------+");
            System.out.print("\n");
        }
    }
    
    //process moving
    static boolean process(Tile[][] tiles, Character[][] chars, int sx, int sy, int ex, int ey) {
        if(sx == ex && sy == ey) {
            return false;
        }
        else if(chars[ex][ey].isItem && chars[sx][sy].name.equals("Hero")) {
            switch(chars[ex][ey].name) {
                case "Potion": chars[sx][sy].cHP = chars[sx][sy].mHP; break;
                default: break;
            }
            chars[ex][ey] = new Character();
            move(chars, sx, sy, ex, ey);
            return true;
        }
        else if(chars[ex][ey].exists) {
            System.out.print(attack(chars[sx][sy], chars[ex][ey]));
            if(chars[ex][ey].cHP <= 0)
                chars[ex][ey] = new Character();
            return false;
        }
        else if(!tiles[ex][ey].isWall) {
            move(chars, sx, sy, ex, ey);
            return true;
        }
        else if(tiles[ex][ey].openable) {
            tiles[ex][ey].isWall = false;
            return false;
        }
        else return false;
        //return whether the object moved or not
    }
    
    //simulate attacking
    static String attack(Character char1, Character char2) {
        if((int)(Math.random() * 100) < char2.eva) return (char2.name + " dodged " + char1.name + "'s attack!\n"); //evasion
        if((int)(Math.random() * 100) > char1.acc) return (char1.name + " missed " + char2.name + "!\n");
        int damage = char1.attack;
        if((int)(Math.random() * 100) < char1.crit) damage *= 2; // crit
        damage -= (char2.def > char1.prc ? char2.def - char1.prc : 0); //defense and piece calculation
        if(damage < 0) damage = 0;
        if((int)(Math.random() * 100) < char1.crit) {
            char2.cHP -= damage * 2;
            return (char1.name + " critically hit " + char2.name + "!\n");
        }
        else {
            char2.cHP -= damage;
            return (char1.name + " hit " + char2.name + "!\n");
        }
    }
    
    //swap two locations
    static void move(Character[][] chars, int sx, int sy, int ex, int ey) {
        Character temp = chars[sx][sy];
        chars[sx][sy] = chars[ex][ey];
        chars[ex][ey] = temp;
    }
    
    //make an enemy
    static Character generateEnemy() {
        if(Math.random() < 0.7) {
            switch((int)(Math.random() * 4)){
                //case: return new Character(mHP, atk, crit, acc, eva, def, prc, name);
                case 0: return new Character( 35,   4,    9,  77,  40,   0,  15, "Spider");
                case 1: return new Character( 70,   1,   11,  90,  10,   6,   0, "Slug");
                case 2: return new Character( 40,   7,    7,  85,   2,   2,   1, "Goblin");
                case 3: return new Character( 20,   5,   30,  60,   5,   0,   2, "Bat");
                default: return new Character();
            }
        }
        else {
            return new Character("potion");
        }
    }
    
    //regenerate map
    static int generateMap(boolean firstGen, Tile[][] tiles, Character[][] chars, Character[] heroes, Character[] enemies) {
        //basic init
        for(int x = 0; x < mX; x++) {
            for(int y = 0; y < mY; y++) {
                if(x == 0 || x == mX-1 || y == 0 || y == mY-1)
                    tiles[x][y] = new Tile("wall");
                else tiles[x][y] = new Tile("");
                chars[x][y] = new Character();
            }
        }
        //generate wall somewhere
        int hWall = (int)(Math.random() * (mY-4)) + 2;
        for(int i = 0; i < mX; i++)
            tiles[i][hWall] = new Tile("wall");
        int hDoor = (int)(Math.random() * (mX-2) + 1);
        tiles[hDoor][hWall] = new Tile("door");
        //vertical wall somewhere
        int vWall1 = (int)(Math.random() * (mX-4)) + 2;
        vWall1 = (vWall1 == hDoor ? vWall1+1: vWall1);
        for(int i = hWall; i < mY; i++)
            tiles[vWall1][i] = new Tile("wall");
        tiles[vWall1][(int)(Math.random() * (mY - 2 - hWall) + hWall + 1)] = new Tile("door");
        //other vertical wall
        int vWall2 = (int)(Math.random() * (mX-4)) + 2;
        vWall2 = (vWall2 == hDoor ? vWall2+1: vWall2);
        for(int i = hWall; i >= 0; i--)
            tiles[vWall2][i] = new Tile("wall");
        tiles[vWall2][(int)(Math.random() * (hWall) + 1)] = new Tile("door");
        //generate stairs
        while(true) {
            int x = (int)(Math.random() * 10);
            int y = (int)(Math.random() * 10);
            if(!tiles[x][y].isWall) {
                tiles[x][y] = new Tile("stairsUp");
                break;
            }
        }
        //generate hero if first generation
        if(firstGen)
            heroes[0] = new Character(100, 12, 20, 98, 12, 3, 5, "Hero");
        while(true) {
            int x = (int)(Math.random() * mX);
            int y = (int)(Math.random() * mY);
            if(!tiles[x][y].isWall && !chars[x][y].exists && !tiles[x][y].stairUp) {
                heroes[0].x = x; heroes[0].y = y;
                chars[x][y] = heroes[0];
                break;
            }
        }
        //generate enemies
        int nEnemies = (int)(Math.random() * (mX * mY / 20) + 1);
        for(int i = 0; i < nEnemies; i++) {
            while(true) {
                int x = (int)(Math.random() * mX);
                int y = (int)(Math.random() * mY);
                if(!tiles[x][y].isWall && !chars[x][y].exists) {
                    enemies[i] = generateEnemy();
                    enemies[i].x = x;
                    enemies[i].y = y;
                    chars[x][y] = enemies[i];
                    break;
                }
            }
        }
        
        return nEnemies;
    }
}
