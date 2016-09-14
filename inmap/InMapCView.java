/*
    Console view for InMap.
 */

package inmap;

public class InMapCView {
    void display(Floor floor) {
        //top row
        System.out.print("+");
        for(int i = 0; i < floor.sizeX; i++)
            System.out.print("--------+");
        System.out.print("\n");
        
        //print rest of tiles
        for(int y = 0; y < floor.sizeY; y++) {
            for(int x = 0; x < floor.sizeX; x++) {
                System.out.print("|        ");
            }
            System.out.print("|\n|");
            
            //print with priority: char -> item -> tile -> empty
            for(int x = 0; x < floor.sizeX; x++) {
                String name;
                if(floor.chars[x][y].exists)
                    name = floor.chars[x][y].name;
                else if(floor.items[x][y].exists)
                    name = floor.items[x][y].name;
                else {
                    name = floor.tiles[x][y].name;
                    if(floor.tiles[x][y].openable && !floor.tiles[x][y].isWall)
                        name = "Open" + name;
                }
                switch(name.length()) {
                    case 0: System.out.print("        "); break;
                    case 1: System.out.print("   "); break;
                    case 2: System.out.print("   "); break;
                    case 3: System.out.print("  "); break;
                    case 4: System.out.print("  "); break;
                    case 5: System.out.print(" "); break;
                    case 6: System.out.print(" "); break;
                    default: break;
                }
                System.out.print(name);
                switch(name.length()) {
                    case 1: System.out.print("    "); break;
                    case 2: System.out.print("   "); break;
                    case 3: System.out.print("   "); break;
                    case 4: System.out.print("  "); break;
                    case 5: System.out.print("  "); break;
                    case 6: System.out.print(" "); break;
                    case 7: System.out.print(" "); break;
                    default: break;
                }
                System.out.print("|");
            }
            
            //print HP if applicable
            System.out.print("\n|");
            for(int x = 0; x < floor.sizeX; x++) {
                if(floor.chars[x][y].exists) {
                    System.out.print("HP: ");
                    System.out.print(floor.chars[x][y].currentHP);
                    if(floor.chars[x][y].currentHP > 999)
                        System.out.print("|");
                    else if(floor.chars[x][y].currentHP > 99)
                        System.out.print(" |");
                    else if(floor.chars[x][y].currentHP > 9)
                        System.out.print("  |");
                    else if(floor.chars[x][y].currentHP > 0)
                        System.out.print("   |");
                }
                else System.out.print("        |");
            }
            
            //final line
            System.out.print("\n+");
            for(int x = 0; x < floor.sizeX; x++)
                System.out.print("--------+");
            System.out.print("\n");
        }
    }
}