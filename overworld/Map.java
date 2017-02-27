package overworld;

import main.Main;

import java.awt.*;
import java.sql.SQLException;
import java.util.*;

class Map implements java.io.Serializable {

    /*
        Map class holds all information related to the overworld map, such as the world gen algorithm and 2D array of type Tile
        TODO: FIX CHANGEONAXIS FOR NWNE TILE
     */

    private Random rand; // random value seeded with current time to use in world gen

    private Tile[][] tiles; // array containing all the information for each tile in the game
    private boolean[][] booleanMap;
    private int mapSize = 0;

    /*
        Minimum and maximum tiles to be used in world gen by tile type
     */

    private final int MIN_MOUNTAIN;
    private final int MAX_MOUNTAIN;
    private final int MIN_FOREST;
    private final int MAX_FOREST;
    private final int MIN_SETTLEMENT;
    private final int MAX_SETTLEMENT;
    private final int MIN_DUNGEON;
    private final int MAX_DUNGEON;

    Map(int mapSize) throws SQLException {
        rand = new Random(System.currentTimeMillis());

        this.mapSize = mapSize;

        booleanMap = new boolean[this.mapSize][this.mapSize];

        MIN_MOUNTAIN = (int) (0.7 * mapSize); // smallest number of mountain chains (700)
        MAX_MOUNTAIN = (int) (1.2 * mapSize); // most amount of mountain chains (1200)
        MIN_FOREST = (int) (7.5 * mapSize); // least amount of forests (7500)
        MAX_FOREST = (int) (12.5 * mapSize); // most amount of forests (12500)
        MIN_SETTLEMENT = (int) (0.9 * mapSize);  // least amount of initial settlements (900)
        MAX_SETTLEMENT = (int) (1.5 * mapSize); // most amount of initial settlements (1500)
        MIN_DUNGEON = (int) (1.5 * mapSize); // least amount of dungeons (all types) (1500)
        MAX_DUNGEON = (int) (2.5 * mapSize); // most amount of dungeons (all types) (2500)

        try {
            newMap();
            int returnCode;
            if ((returnCode = mapCheck()) != 0) {
                System.out.println("Map contains error, error code " + returnCode);
                System.exit(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateBooleanMap();
    }

    private void newMap() throws SQLException {
        genMap(mapSize);
    }

    private int mapCheck(){
        /*
            Checks integrity of map, returns true if no issues are found and false if there is something wrong
            Error code table:

            1 - InMap or Settlement tile exists outside coastline (in water)
         */

        long start = System.currentTimeMillis();

        for (int y = 0; y < getMapSize(); y++)
            for (int x = 0; x < getMapSize(); x++)
                if (getTiles()[x][y].settlementTile != null)
                    for (int b = y - 1; b < y + 1; b++)
                        for (int a = x - 1; a < x + 1; a++)
                            if (getTiles()[a][b].type.equals("WaterAll"))
                                return 1;

        if (OverworldController.debug)
            System.out.println("Map check completed succesfully in " + (System.currentTimeMillis() - start) + "ms");

        return 0;
    }

    int getMapSize() { // returns map size
        return mapSize;
    }

    Tile[][] getTiles() {
        return tiles;
    }

    void updateBooleanMap() {
        booleanMap = new boolean[mapSize][mapSize];

        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                booleanMap[x][y] = (tiles[x][y].type.equals("Mountain") || tiles[x][y].type.contains("Water"));
            }
        }
    }

    boolean[][] getBooleanMap() {
        return booleanMap;
    }

    private int returnDiffTileBonus(int sameTileCount) {
        return (int) (5 * (Math.pow(2, -0.5 * sameTileCount)));
    }

    private int returnProximityBonus(int var, int mapSize) {
        return (int) ((1 / 8) * Math.pow(var - ((2 + (mapSize / 50)) / 2), 2));
    }

    private String nextWaterTile(String prev, String dir, int x, int y, int mapSize, int sameTileCount) {

        /*
            Determines which water tile can come after the current one, and assigns scores to each possibility
            Straight lines get better scores because they shorten the runtime of the algorithm
            If the function return null, it calls the force redirect function to prevent going beyond the limits of the array
            Code is purposefully redundant to improve performance (inside the if statements)
         */

        String[] possibleDirections = getPossibleDirections(prev, dir);
        int[] priority = new int[possibleDirections.length];

        // TODO: REVERSE COORD DIRECTION OF BONUSES FOR TILES AND FOR LIMITING (IF AND LOOP)
        // TODO: FORMULA FOR PREFERENCE OF 5,7,9 LETTER WATER TILES BASED ON PROXIMITY TO EDGE OF MAP

        if (prev.equalsIgnoreCase(""))
            return "WaterSW";

        if (y <= 3 || x <= 3 || y >= mapSize - 5 || x >= mapSize - 5) // equal signs necessary to avoid rare crash
            return null; // activates force redirect

        if (dir.equalsIgnoreCase("north")) {
            for (int strings = 0; strings < possibleDirections.length; strings++) {
                if (possibleDirections[strings].length() == 7)
                    priority[strings] += 5;
                else if (possibleDirections[strings].length() == 9)
                    priority[strings] += 4;
                else
                    priority[strings] += 4;
            }
        } else if (dir.equalsIgnoreCase("west")) {
            for (int strings = 0; strings < possibleDirections.length; strings++) {
                if (possibleDirections[strings].length() == 7)
                    priority[strings] += 5;
                else if (possibleDirections[strings].length() == 9)
                    priority[strings] += 4;
                else
                    priority[strings] += 4;
            }
        } else if (dir.equalsIgnoreCase("south")) {
            for (int strings = 0; strings < possibleDirections.length; strings++) {
                if (possibleDirections[strings].length() == 7)
                    priority[strings] += 5;
                else if (possibleDirections[strings].length() == 9)
                    priority[strings] += 4;
                else
                    priority[strings] += 4;
            }
        } else if (dir.equalsIgnoreCase("east")) {
            for (int strings = 0; strings < possibleDirections.length; strings++) {
                if (possibleDirections[strings].length() == 7)
                    priority[strings] += 5;
                else if (possibleDirections[strings].length() == 9)
                    priority[strings] += 4;
                else
                    priority[strings] += 4;
            }
        }

        for (int a = 0; a < possibleDirections.length; a++) // checks that no negative values will be passed
            if (priority[a] < 0)
                priority[a] = 0;

        return possibleDirections[chooseString(priority)];
    }

    private int chooseString(int[] priority) {

        /*
            Chooses a random direction to go in, but statistically the better choices have higher chances of being picked
         */

        if (priority.length == 3) {
            int num = rand.nextInt(priority[0] + priority[1] + priority[2]);
            if (num < priority[0])
                return 0;
            else if (num < priority[0] + priority[1])
                return 1;
            else
                return 2;
        } else if (priority.length == 2) {
            int num = rand.nextInt(priority[0] + priority[1]);
            if (num < priority[0])
                return 0;
            else
                return 1;
        } else
            return -1;
    }

    private String[] getPossibleDirections(String prev, String genDir) {

        /*
            Returns the tiles that match up with the previous one, in the direction that the generation is happening
         */

        String[] dirs = new String[3];

        if (genDir.equalsIgnoreCase("north")) {
            if (prev.contains("NESE")) {
                dirs[0] = "WaterE";
                dirs[1] = "WaterNE";
            } else if (prev.contains("SWSE")) {
                dirs[0] = "WaterSE";
                dirs[1] = "WaterNESE";
                dirs[2] = "WaterS";
            } else if (prev.contains("SE")) {
                dirs[0] = "WaterNESE";
                dirs[1] = "WaterS";
                dirs[2] = "WaterSE";
            } else if (prev.contains("NE")) {
                dirs[0] = "WaterE";
                dirs[1] = "WaterNE";
            } else if (prev.contains("SW")) {
                dirs[0] = "WaterSWSE";
                dirs[1] = "WaterSW";
            } else if (prev.contains("E")) {
                dirs[0] = "WaterSE";
                dirs[1] = "WaterNESE";
                dirs[2] = "WaterS";
            } else if (prev.contains("S")) {
                dirs[0] = "WaterSW";
                dirs[1] = "WaterSWSE";
            }
        } else if (genDir.equalsIgnoreCase("east")) {
            if (prev.contains("SWSE")) {
                dirs[0] = "WaterSE";
                dirs[1] = "WaterS";
            } else if (prev.contains("NWSW")) {
                dirs[0] = "WaterSW";
                dirs[1] = "WaterW";
                dirs[2] = "WaterSWSE";
            } else if (prev.contains("NW")) {
                dirs[0] = "WaterNWSW";
                dirs[1] = "WaterNW";
            } else if (prev.contains("SE")) {
                dirs[0] = "WaterS";
                dirs[1] = "WaterSE";
            } else if (prev.contains("SW")) {
                dirs[0] = "WaterSW";
                dirs[1] = "WaterW";
                dirs[2] = "WaterSWSE";
            } else if (prev.contains("S")) {
                dirs[0] = "WaterW";
                dirs[1] = "WaterSW";
                dirs[2] = "WaterSWSE";
            } else if (prev.endsWith("W")) {
                dirs[0] = "WaterNW";
                dirs[1] = "WaterNWSW";
            }
        } else if (genDir.equalsIgnoreCase("south")) {
            if (prev.contains("NWSW")) {
                dirs[0] = "WaterSW";
                dirs[1] = "WaterW";
            } else if (prev.contains("NWNE")) {
                dirs[0] = "WaterNW";
                dirs[1] = "WaterN";
            } else if (prev.contains("NW")) {
                dirs[0] = "WaterNWSW";
                dirs[1] = "WaterNW";
                dirs[2] = "WaterN";
            } else if (prev.contains("NE")) {
                dirs[0] = "WaterNWNE";
                dirs[1] = "WaterNE";
            } else if (prev.contains("SW")) {
                dirs[0] = "WaterSW";
                dirs[1] = "WaterW";
            } else if (prev.contains("N")) {
                dirs[0] = "WaterNE";
                dirs[1] = "WaterNWNE";
            } else if (prev.endsWith("W")) {
                dirs[0] = "WaterNW";
                dirs[1] = "WaterNWSW";
                dirs[2] = "WaterN";
            }
        } else if (genDir.equalsIgnoreCase("west")) {
            if (prev.contains("NESE")) {
                dirs[0] = "WaterNE";
                dirs[1] = "WaterE";
            } else if (prev.contains("NWNE")) {
                dirs[0] = "WaterNW";
                dirs[1] = "WaterN";
            } else if (prev.contains("NE")) {
                dirs[0] = "WaterNE";
                dirs[1] = "WaterE";
                dirs[2] = "WaterNWNE";
            } else if (prev.contains("NW")) {
                dirs[0] = "WaterNW";
                dirs[1] = "WaterN";
            } else if (prev.contains("SE")) {
                dirs[0] = "WaterSE";
                dirs[1] = "WaterNESE";
            } else if (prev.contains("E")) {
                dirs[0] = "WaterSE";
                dirs[1] = "WaterNESE";
            } else if (prev.contains("N")) {
                dirs[0] = "WaterNE";
                dirs[1] = "WaterNWNE";
                dirs[2] = "WaterE";
            }
        }

        if (dirs[2] == null) {
            String[] tmp = new String[2];
            tmp[0] = dirs[0];
            tmp[1] = dirs[1];
            return tmp;
        }

        return dirs;
    }

    private int changeOnAxis(String tile, boolean x) {

        /*
            Returns change in position the algorithm should make to continue generating the coastline
         */

        //String dir = returnDir(genDir, tile);

        switch (tile) {
            case "WaterSW":
                if (x)
                    return 1;
                else
                    return 0;
            case "WaterW":
                if (x)
                    return 0;
                else
                    return 1;
            case "WaterSWSE":
                if (x)
                    return 0;
                else
                    return -1;
            case "WaterNW":
                if (x)
                    return 0;
                else
                    return 1;
            case "WaterNWSW":
                if (x)
                    return 1;
                else
                    return 0;
            case "WaterN":
                if (x)
                    return -1;
                else
                    return 0;
            case "WaterNE":
                if (x)
                    return -1;
                else
                    return 0;
            case "WaterE":
                if (x)
                    return 0;
                else
                    return -1;
            case "WaterNESE":
                if (x)
                    return -1;
                else
                    return 0;
            case "WaterSE":
                if (x)
                    return 0;
                else
                    return -1;
            case "WaterS":
                if (x)
                    return 1;
                else
                    return 0;
            case "WaterNWNE":
                if (x)
                    return 0;
                else
                    return 1;
            default:
                return 0;
        }
    }

    private String forceRedirect(Tile[][] tiles, int x, int y, String dir) {

        /*
            Called when algorithm gets too close to the limits of the array
            Turns the direction around by force placing tiles
            TODO: FIX RANDOM BUG THAT HAPPENS WHEN THE ARRAY RUNS OUT (-1)
         */

        if (OverworldController.debug)
            System.out.println("Force redirect activated");

        if (dir.equals("north")) {
            tiles[x][y] = new Tile("WaterE");
            tiles[x][y - 1] = new Tile("WaterS");
            if (rand.nextBoolean()) {
                tiles[x + 1][y - 1] = new Tile("WaterSWSE");
                return "WaterSWSE";
            } else {
                tiles[x + 1][y - 1] = new Tile("WaterSW");
                return "WaterSW";
            }
        } else if (dir.equals("east")) {
            tiles[x][y] = new Tile("WaterS");
            tiles[x + 1][y] = new Tile("WaterW");
            if (rand.nextBoolean()) {
                tiles[x + 1][y + 1] = new Tile("WaterNWSW");
                return "WaterNWSW";
            } else {
                tiles[x + 1][y + 1] = new Tile("WaterNW");
                return "WaterNW";
            }
        } else if (dir.equals("south")) {
            tiles[x][y] = new Tile("WaterW");
            tiles[x][y + 1] = new Tile("WaterN");
            if (rand.nextBoolean()) {
                tiles[x - 1][y + 1] = new Tile("WaterNWNE");
                return "WaterNWNE";
            } else {
                tiles[x - 1][y + 1] = new Tile("WaterNE");
                return "WaterNE";
            }
        } else if (dir.equals("west")) {
            tiles[x][y] = new Tile("WaterN");
            tiles[x - 1][y] = new Tile("WaterE");
            if (rand.nextBoolean()) {
                tiles[x - 1][y - 1] = new Tile("WaterNESE");
                return "WaterNESE";
            } else {
                tiles[x - 1][y - 1] = new Tile("WaterSE");
                return "WaterSE";
            }
        }
        return "WaterAll"; // should not
    }

    private void genWaterTiles(Tile[][] tiles) {
        /*
            Fills outside of coastline with water tiles
         */

        ArrayDeque<Point> queue = new ArrayDeque<>();

        queue.push(new Point(0, 0));

        for (Point p = queue.poll(); p != null; p = queue.poll()) {
            tiles[p.x][p.y] = new Tile("WaterAll");
            if (p.x + 1 < mapSize && tiles[p.x + 1][p.y] == null)
                queue.push(new Point(p.x + 1, p.y));
            if (p.x > 0 && tiles[p.x - 1][p.y] == null)
                queue.push(new Point(p.x - 1, p.y));
            if (p.y + 1 < mapSize && tiles[p.x][p.y + 1] == null)
                queue.push(new Point(p.x, p.y + 1));
            if (p.y > 0 && tiles[p.x][p.y - 1] == null)
                queue.push(new Point(p.x, p.y - 1));
        }
    }

    private void genMap(int mapSize) {

        /*
            Core of the world gen algorithm
            Generates coastline in 4 steps, one for each cardinal direction
            Then generates static tiles (lakes, mountains, forests)
            Generates settlements
            Fills in null tiles with grass type tiles
         */

        tiles = new Tile[mapSize][mapSize];

        if (OverworldController.debug)
            System.out.println("Starting water tiles gen");

        //////////////////////////////////////////////////////////
        // WATER TILES GEN

        int waterLineMax = mapSize / 20;
        int startY = rand.nextInt(waterLineMax) + 3;

        String genDir = "east";
        int endPos = mapSize - (rand.nextInt(waterLineMax) + 3);
        int y = startY;
        String tile = "";
        String prevTile;
        int sameTileCount = 0;

        // first iteration of coastline defining
        for (int x = waterLineMax + 3; x < endPos; x += changeOnAxis(tile, true)) {
            prevTile = tile;
            tile = nextWaterTile(tile, genDir, x, y, mapSize, sameTileCount);
            if (tile != null && prevTile.equals(tile))
                sameTileCount++;
            else
                sameTileCount = 0;
            if (tile == null) {
                tile = forceRedirect(tiles, x, y, genDir);
                x++;
                y++;
                // change x and y coords to new position
            } else {
                // prevents overwriting tiles from forceRedirect
                if (tiles[x][y] == null)
                    tiles[x][y] = new Tile(tile);
            }
            y += changeOnAxis(tile, false);
        }

        tile = "WaterW";
        tiles[endPos][y] = new Tile(tile);
        y++;

        if (OverworldController.debug) {
            System.out.println("Eastward generation finished");
            System.out.println("Last x ,y positions : " + endPos + ", " + y);
        }

        int x = endPos;
        genDir = "south";
        endPos = mapSize - (rand.nextInt(waterLineMax) + 3);
        for (; y < endPos; y += changeOnAxis(tile, false)) { // second iteration of coastline defining
            prevTile = tile;
            if (prevTile.equals(tile))
                sameTileCount++;
            else
                sameTileCount = 0;
            tile = nextWaterTile(tile, genDir, x, y, mapSize, sameTileCount);
            if (tile == null) {
                tile = forceRedirect(tiles, x, y, genDir);
                x--;
                y++;
            } else {
                if (tiles[x][y] == null) // prevents overwriting tiles from forceRedirect
                    tiles[x][y] = new Tile(tile);
            }
            x += changeOnAxis(tile, true);
        }

        tile = "WaterN";
        tiles[x][endPos] = new Tile(tile);
        x--;

        if (OverworldController.debug) {
            System.out.println("Southward generation finished");
            System.out.println("Last x,y positions : " + x + ", " + endPos);
        }

        y = endPos;
        genDir = "west";
        endPos = (rand.nextInt(waterLineMax) + 3);
        for (; x > endPos; x += changeOnAxis(tile, true)) { // third iteration of coastline defining
            prevTile = tile;
            if (prevTile.equals(tile))
                sameTileCount++;
            else
                sameTileCount = 0;
            tile = nextWaterTile(tile, genDir, x, y, mapSize, sameTileCount);
            if (tile == null) {
                tile = forceRedirect(tiles, x, y, genDir);
                x--;
                y--;
            } else {
                if (tiles[x][y] == null) // prevents overwriting tiles from forceRedirect
                    tiles[x][y] = new Tile(tile);
            }
            y += changeOnAxis(tile, false);
        }

        tile = "WaterE";
        tiles[endPos][y] = new Tile(tile);
        y--;

        if (OverworldController.debug) {
            System.out.println("Westward generation finished");
            System.out.println("Last x,y positions : " + endPos + ", " + y);
        }

        x = endPos;
        genDir = "north";
        endPos = (rand.nextInt(waterLineMax) + 3);
        for (; y > startY; y += changeOnAxis(tile, false)) { // fourth iteration of coastline defining
            prevTile = tile;
            if (prevTile.equals(tile))
                sameTileCount++;
            else
                sameTileCount = 0;
            tile = nextWaterTile(tile, genDir, x, y, mapSize, sameTileCount);
            if (tile == null) {
                tile = forceRedirect(tiles, x, y, genDir);
                x++;
                y--;
            } else {
                if (tiles[x][y] == null) // prevents overwriting tiles from forceRedirect
                    tiles[x][y] = new Tile(tile);
            }
            x += changeOnAxis(tile, true);
        }

        tiles[x][y] = new Tile("WaterS");
        x++;

        if (OverworldController.debug) {
            System.out.println("Northward generation finished");
            System.out.println("Last x,y positions : " + x + ", " + endPos);
        }

        for (; tiles[x][y] == null; x++)
            tiles[x][y] = new Tile("WaterSW");

        genWaterTiles(tiles);

        // END WATER TILES GEN
        //////////////////////////////////////////////////////////

        if (OverworldController.debug) {
            System.out.println("Finished water tiles gen");
            System.out.println("Starting mountain tiles gen");
        }

        // GEN MOUNTAINS

        int numOfType = rand.nextInt(MAX_MOUNTAIN) + MIN_MOUNTAIN;

        // generates mountains
        for (int a = 0; a < numOfType; a++) {
            while (true) {
                int randX = rand.nextInt(mapSize) + 2;
                int randY = rand.nextInt(mapSize) + 2;
                int radius = rand.nextInt(4) + 1;
                if (isAreaEmpty(tiles, randX, randY, radius, mapSize)) {
                    populateArea(tiles, randX, randY, radius, "Mountain");
                    break;
                }
            }
        }

        if (OverworldController.debug) {
            System.out.println("Finished mountain tiles gen");
            System.out.println("Starting forest tiles gen");
        }

        // GEN HEAVY FOREST, AND THEN LIGHT FOREST AROUND IT

        numOfType = rand.nextInt(MAX_FOREST) + MIN_FOREST;

        // generates forests
        for (int a = 0; a < numOfType; a++) {
            while (true) {
                int randX = rand.nextInt(mapSize) + 2;
                int randY = rand.nextInt(mapSize) + 2;
                int radiusHeavy = rand.nextInt(8) + 1;
                int radiusLight = rand.nextInt(15) + radiusHeavy;
                if (isAreaEmpty(tiles, randX, randY, radiusLight, mapSize)) {
                    populateArea(tiles, randX, randY, radiusHeavy, "ForestHeavy");
                    populateArea(tiles, randX, randY, radiusLight, "ForestLight");
                    break;
                }
            }
        }

        if (OverworldController.debug) {
            System.out.println("Finished forest tiles gen");
            System.out.println("Starting dungeon tile gen");
        }

        numOfType = rand.nextInt(MAX_DUNGEON) + MIN_DUNGEON;

        // generates dungeons
        for (int a = 0; a < numOfType; a++) {
            while (true) {
                int randX = rand.nextInt(mapSize) + 2;
                int randY = rand.nextInt(mapSize) + 2;
                if (isAreaEmpty(tiles, randX, randY, 2, mapSize)) {
                    tiles[randX][randY] = new Tile("InMap", "Tower");
                    break;
                }
            }
        }

        if (OverworldController.debug) {
            System.out.println("Finished dungeon tile gen");
            System.out.println("Starting settlement tiles gen");
        }

        // TODO: FIGURE OUT HOW TO GENERATE KINGDOMS, IF RANDOMLY OR BY SECTIONS OF MAP

        int settlementsTotal = rand.nextInt(MAX_SETTLEMENT) + MIN_SETTLEMENT;
        ArrayList<String> names = new ArrayList<>(); // used to prevent duplicate names

        // generates villages
        for (int a = 0; a < settlementsTotal; a++) {
            String settlementType;
            String name = "";

            if (a < settlementsTotal * 0.3)
                settlementType = "Hamlet";
            else if (a < settlementsTotal * 0.6)
                settlementType = "Village";
            else if (a < settlementsTotal * 0.75)
                settlementType = "Town";
            else if (a < settlementsTotal * 0.9) {
                if (rand.nextBoolean())
                    settlementType = "Castle";
                else
                    settlementType = "City";
            } else {
                if (rand.nextBoolean())
                    settlementType = "Citadel";
                else
                    settlementType = "Metropolis";
            }

            while (true) {
                int randX = rand.nextInt(mapSize) + 2;
                int randY = rand.nextInt(mapSize) + 2;
                if (isAreaEmpty(tiles, randX, randY, 2, mapSize) && !nameExists(names, name)) {
                    tiles[randX][randY] = new Tile("Settlement", settlementType, name = Main.genRandName(rand.nextInt(4) + 4), 50);
                    break;
                }
            }

            names.add(name);
        }

        if (OverworldController.debug)
            System.out.println("Filling in null tiles...");

        // fills in all non assigned tiles with grass tiles
        for (int a = 0; a < mapSize; a++) {
            for (int b = 0; b < mapSize; b++) {
                if (tiles[b][a] == null)
                    tiles[b][a] = new Tile("Grass");
            }
        }

        if (OverworldController.debug)
            System.out.println("World gen done!");
    }

    private boolean isAreaEmpty(Tile[][] tiles, int x, int y, int radius, int mapSize) {

        /*
            Returns true if the area has not been yet initialized or is not out of bounds
            Returns false if any part of the area has been initialized or is out of bounds
         */

        for (int a = y - radius; a < y + radius; a++)
            for (int b = x - radius; b < x + radius; b++)
                if (a < 0 || b < 0 || a >= mapSize || b >= mapSize || tiles[b][a] != null)
                    return false;
        return true;
    }

    private boolean nameExists(ArrayList<String> names, String name) {
        for (String name1 : names) {
            if (name1.equals(name))
                return true;
        }
        return false;
    }

    private void populateArea(Tile[][] tiles, int x, int y, int radius, String type) {

        /*
            Populates area of radius radius in a square with a center at x, y with tiles of type type
         */

        for (int a = y - radius; a < y + radius; a++)
            for (int b = x - radius; b < x + radius; b++)
                if (tiles[a][b] == null && rand.nextInt(10) < 7) // 70 percent chance of spawning tile
                    tiles[a][b] = new Tile(type);
    }
}