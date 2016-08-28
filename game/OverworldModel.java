/*
    Data holder and handler for all non-graphical code
*/

package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OverworldModel {
    private double mapTileSize;
    private int mapSize, zoom = 6;
    private int[] currPos = new int[2];

    private Tile[][] tiles;

    private Map map = new Map();
    private FileAccess fileAccess = new FileAccess();

    OverworldModel(int mapSize) {

        this.mapSize = mapSize;

        tiles = new Tile[mapSize][mapSize];

        fileAccess.loadFile("src/data/player");
        currPos[0] = (int) fileAccess.getFromFile("locationX", "int");
        currPos[1] = (int) fileAccess.getFromFile("locationY", "int");

        tiles = map.genMap(mapSize);
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public int getMapSize() {
        return mapSize;
    }

    public int getZoom() {
        return zoom;
    }

    public double getMapTileSize() {
        return mapTileSize;
    }

    public int[] getCurrPos() {
        return currPos;
    }

    public int getCurrPos(int index) {
        return currPos[index];
    }

    public void setCurrPos(int index, int sum) {
        currPos[index] += sum;
    }

    public void setMapTileSize(double mapTileSize) {
        this.mapTileSize = mapTileSize;
    }

    public void gameInit() {

        while (true) {

        }
        // notify controller?
    }

    private void sleep(long time) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < time) ;
    }
}

class Map {

    Random rand;

    private final int MIN_MOUNTAIN = 2500;
    private final int MAX_MOUNTAIN = 3500;
    private final int MIN_FOREST = 15000;
    private final int MAX_FOREST = 20000;
    private final int MIN_SETTLEMENT = 2000;
    private final int MAX_SETTLEMENT = 1000;

    Map() {
        rand = new Random(System.currentTimeMillis());
    }

    public Tile[][] loadMap() { // maybe just void
        // use global tiles array
        return null;
    }

    public Tile[][] tmpgenMap(int mapSize) {
        Tile[][] tiles = new Tile[mapSize][mapSize];
        long start = System.currentTimeMillis();

        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                int randNum = rand.nextInt(100);
                if (randNum < 30)
                    tiles[x][y] = new Tile("ForestLight", true, false);
                if (randNum >= 30 && randNum < 31)
                    tiles[x][y] = new Tile("Settlement", true, true, "Name", "Village", 'c', 50);
                if (randNum >= 31 && randNum < 95)
                    tiles[x][y] = new Tile("Grass", true, false);
                if (randNum >= 95 && randNum < 100)
                    tiles[x][y] = new Tile("Mountain", true, false);
            }
        }

        System.out.println("Loading world arr took " + (double) (System.currentTimeMillis() - start) / 1000);

        return tiles;
    }

    private String nextWaterTile(String prev, String dir, int x, int y, int mapSize) {
        // if returns null, force redirect of direction must happen to prevent outOfBoundsException
        String[] possibleDirections = getPossibleDirections(prev);
        int[] priority = new int[possibleDirections.length];

        // IMPORTANT: CODE IS REDUNDANT FOR SPEED, TO MINIMIZE OPERATIONS SINCE WORLD GEN WILL BE TAKING TONS OF CALCULATION
        // MIGHT REDUCE REDUNDANCY IN THE FUTURE
        // Depending on direction, eliminate some options since they only work one way

        // TODO: REVERSE COORD DIRECTION OF BONUSES FOR TILES AND FOR LIMITING (IF AND LOOP)
        // TODO: FORMULA FOR PREFERENCE OF 5,7,9 LETTER WATER TILES BASED ON PROXIMITY TO EDGE OF MAP

        if (prev.equalsIgnoreCase(""))
            return "WaterSW";

        if (y <= 2 || x <= 2 || y >= mapSize - 3 || x >= mapSize - 3) // equal signs necessary to avoid rare crash
            return null; // activates force redirect

        if (dir.equalsIgnoreCase("north")) {
            for (int strings = 0; strings < possibleDirections.length - 1; strings++) {
                if (possibleDirections[strings].length() == 7) // NW NE SW SE, preferable
                    priority[strings] += 5;
                else if (possibleDirections[strings].length() == 9)
                    priority[strings] += 3;
                else
                    priority[strings]++; // N W S E least preferable since they make the algorithm run longer with abrupt changes in direction
                if (possibleDirections[strings].contains("SE"))
                    priority[strings] += (1 / 8) * Math.pow(x - ((2 + (mapSize / 50)) / 2), 2);
                else
                    priority[strings] -= (1 / 8) * Math.pow(x - ((2 + (mapSize / 50)) / 2), 2);
            }
        } else if (dir.equalsIgnoreCase("west")) {
            for (int strings = 0; strings < possibleDirections.length - 1; strings++) {
                if (possibleDirections[strings].length() == 7) // NW NE SW SE, preferable
                    priority[strings] += 5;
                else if (possibleDirections[strings].length() == 9)
                    priority[strings] += 3;
                else
                    priority[strings]++; // N W S E least preferable since they make the algorithm run longer with abrupt changes in direction
                if (possibleDirections[strings].contains("SW"))
                    priority[strings] += (1 / 8) * Math.pow(y - ((2 + (mapSize / 50)) / 2), 2);
                else
                    priority[strings] -= (1 / 8) * Math.pow(y - ((2 + (mapSize / 50)) / 2), 2);
            }
        } else if (dir.equalsIgnoreCase("south")) {
            for (int strings = 0; strings < possibleDirections.length - 1; strings++) {
                if (possibleDirections[strings].length() == 7) // NW NE SW SE, preferable
                    priority[strings] += 5;
                else if (possibleDirections[strings].length() == 9)
                    priority[strings] += 3;
                else
                    priority[strings]++; // N W S E least preferable since they make the algorithm run longer with abrupt changes in direction
                if (possibleDirections[strings].contains("SE"))
                    priority[strings] += (1 / 8) * Math.pow(x - ((2 + (mapSize / 50)) / 2), 2);
                else
                    priority[strings] -= (1 / 8) * Math.pow(x - ((2 + (mapSize / 50)) / 2), 2);
            }
        } else if (dir.equalsIgnoreCase("east")) {
            for (int strings = 0; strings < possibleDirections.length - 1; strings++) {
                if (possibleDirections[strings].length() == 7) // NW NE SW SE, preferable
                    priority[strings] += 5;
                else if (possibleDirections[strings].length() == 9)
                    priority[strings] += 3;
                else
                    priority[strings]++; // N W S E least preferable since they make the algorithm run longer with abrupt changes in direction
                if (possibleDirections[strings].contains("NE"))
                    priority[strings] += (1 / 8) * Math.pow(y - ((2 + (mapSize / 50)) / 2), 2);
                else
                    priority[strings] -= (1 / 8) * Math.pow(y - ((2 + (mapSize / 50)) / 2), 2);
            }
        }

        for (int a = 0; a < possibleDirections.length; a++) // may be redundant
            if (priority[a] < 0)
                priority[a] = 0;

        return possibleDirections[chooseString(priority)];
    }

    private int chooseString(int[] priority) {
        int num = rand.nextInt(priority[0] + priority[1] + priority[2] + priority[3] + priority[4]);
        if (num < priority[0])
            return 0;
        else if (num < priority[0] + priority[1])
            return 1;
        else if (num < priority[0] + priority[1] + priority[2])
            return 2;
        else if (num < priority[0] + priority[1] + priority[2] + priority[3])
            return 3;
        else
            return 4;
    }

    private String[] getPossibleDirections(String prev) {

        // returns possible next tiles using the previous one

        String[] dirs = new String[5];

        if (prev.contains("NWSW")) {
            dirs[0] = "WaterSWSE";
            dirs[1] = "WaterNWNE";
            dirs[2] = "WaterNW";
            dirs[3] = "WaterSW";
            dirs[4] = "WaterW";
        } else if (prev.contains("NWNE")) {
            dirs[0] = "WaterNWSW";
            dirs[1] = "WaterNESE";
            dirs[2] = "WaterNW";
            dirs[3] = "WaterNE";
            dirs[4] = "WaterN";
        } else if (prev.contains("SWSE")) {
            dirs[0] = "WaterNWSW";
            dirs[1] = "WaterNESE";
            dirs[2] = "WaterSW";
            dirs[3] = "WaterSE";
            dirs[4] = "WaterS";
        } else if (prev.contains("NESE")) {
            dirs[0] = "WaterNWNE";
            dirs[1] = "WaterSWSE";
            dirs[2] = "WaterNE";
            dirs[3] = "WaterSE";
            dirs[4] = "WaterE";
        } else if (prev.contains("NW")) {
            dirs[0] = "WaterNWSW";
            dirs[1] = "WaterNWNE";
            dirs[2] = "WaterN";
            dirs[3] = "WaterW";
            dirs[4] = "WaterNW";
        } else if (prev.contains("NE")) {
            dirs[0] = "WaterNWNE";
            dirs[1] = "WaterNESE";
            dirs[2] = "WaterN";
            dirs[3] = "WaterE";
            dirs[4] = "WaterNE";
        } else if (prev.contains("SE")) {
            dirs[0] = "WaterSWSE";
            dirs[1] = "WaterNESE";
            dirs[2] = "WaterS";
            dirs[3] = "WaterE";
            dirs[4] = "WaterSE";
        } else if (prev.contains("SW")) {
            dirs[0] = "WaterNWSW";
            dirs[1] = "WaterSWSE";
            dirs[2] = "WaterS";
            dirs[3] = "WaterW";
            dirs[4] = "WWaterSW";
        } else if (prev.contains("W")) {
            dirs[0] = "WaterNW";
            dirs[1] = "WaterSW";
            dirs[2] = "WaterNWSW";
            dirs[3] = "WaterN";
            dirs[4] = "WaterS";
        } else if (prev.contains("S")) {
            dirs[0] = "WaterSE";
            dirs[1] = "WaterSE";
            dirs[2] = "WaterSWSE";
            dirs[3] = "WaterW";
            dirs[4] = "WaterE";
        } else if (prev.contains("E")) {
            dirs[0] = "WaterSE";
            dirs[1] = "WaterNE";
            dirs[2] = "WaterNESE";
            dirs[3] = "WaterN";
            dirs[4] = "WaterS";
        } else if (prev.contains("N")) {
            dirs[0] = "WaterNE";
            dirs[1] = "WaterNW";
            dirs[2] = "WaterNWNE";
            dirs[3] = "WaterW";
            dirs[4] = "WaterE";
        }

        return dirs;
    }

    private int changeOnAxis(String tile, boolean x) { // if to continue, wait on currPos or go backwards
        // if change on x is wanted, true, else, false
        if (tile.equals("WaterSW")) {
            if (x)
                return 1;
            else
                return 0;
        } else if (tile.equals("WaterW")) {
            if (x)
                return 0;
            else
                return 1;
        } else if (tile.equals("WaterSWSE")) {
            if (x)
                return 0;
            else
                return -1;
        } else if (tile.equals("WaterNW")) {
            if (x)
                return 0;
            else
                return 1;
        } else if (tile.equals("WaterNWSW")) {
            if (x)
                return 1;
            else
                return 0;
        } else if (tile.equals("WaterN")) {
            if (x)
                return -1;
            else
                return 0;
        } else if (tile.equals("WaterNE")) {
            if (x)
                return -1;
            else
                return 0;
        } else if (tile.equals("WaterE")) {
            if (x)
                return 0;
            else
                return -1;
        } else if (tile.equals("WaterNESE")) {
            if (x)
                return -1;
            else
                return 0;
        } else if (tile.equals("WaterSE")) {
            if (x)
                return 0;
            else
                return -1;
        } else if (tile.equals("WaterS")) {
            if (x)
                return 1;
            else
                return 0;
        } else if (tile.equals("WaterNWNE")) {
            if (x)
                return 0;
            else
                return 1;
        } else // maybe input manually for special scenarios...?
            return 0;
    }

    private String forceRedirect(Tile[][] tiles, int x, int y, String dir) {
        if (dir.equals("north")) {
            tiles[x][y] = new Tile("WaterE", false, false);
            tiles[x][y - 1] = new Tile("WaterS", false, false);
            if (rand.nextBoolean()) {
                tiles[x + 1][y - 1] = new Tile("WaterSWSE", false, false);
                return "WaterSWSE";
            } else {
                tiles[x + 1][y - 1] = new Tile("WaterSW", false, false);
                return "WaterSW";
            }
        } else if (dir.equals("east")) {
            tiles[x][y] = new Tile("WaterS", false, false);
            tiles[x + 1][y] = new Tile("WaterW", false, false);
            if (rand.nextBoolean()) {
                tiles[x + 1][y + 1] = new Tile("WaterNWSW", false, false);
                return "WaterNWSW";
            } else {
                tiles[x + 1][y + 1] = new Tile("WaterNW", false, false);
                return "WaterNW";
            }
        } else if (dir.equals("south")) {
            tiles[x][y] = new Tile("WaterW", false, false);
            tiles[x][y + 1] = new Tile("WaterN", false, false);
            if (rand.nextBoolean()) {
                tiles[x - 1][y + 1] = new Tile("WaterNWNE", false, false);
                return "WaterNWNE";
            } else {
                tiles[x - 1][y + 1] = new Tile("WaterNE", false, false);
                return "WaterNE";
            }
        } else if (dir.equals("west")) {
            tiles[x][y] = new Tile("WaterN", false, false);
            tiles[x - 1][y] = new Tile("WaterE", false, false);
            if (rand.nextBoolean()) {
                tiles[x - 1][y - 1] = new Tile("WaterNESE", false, false);
                return "WaterNESE";
            } else {
                tiles[x - 1][y - 1] = new Tile("WaterSE", false, false);
                return "WaterSE";
            }
        }
        return null; // should not
    }

    public Tile[][] genMap(int mapSize) {
        Tile[][] tiles = new Tile[mapSize][mapSize];

        System.out.println("Starting water tiles gen");

        //////////////////////////////////////////////////////////
        // WATER TILES GEN

        int waterLineMax = mapSize / 20;
        System.out.println("MaxWaterline: " + waterLineMax);
        String currDir = "east";

        int endPos = rand.nextInt(waterLineMax) + 2;
        int y = rand.nextInt(waterLineMax) + 2;
        String tile = "";
        for (int x = rand.nextInt(waterLineMax) + 2; x < mapSize - endPos; x += changeOnAxis(tile, true)) { // first iteration of coastline defining
            tile = nextWaterTile(tile, currDir, x, y, mapSize);
            if (tile == null) {
                tile = forceRedirect(tiles, x, y, currDir);
                if (tile.equals("WaterNWSW")) {
                    x++;
                    y++;
                }
                // change x and y coords to new position
            } else {
                tiles[x][y] = new Tile(tile, false, false);
            }
            y += changeOnAxis(tile, false);
        }

        System.out.println("Eastward generation finished");

        int x = endPos;
        currDir = "south";
        endPos = mapSize - (rand.nextInt(waterLineMax) + 2);
        for (; y < mapSize - endPos; y += changeOnAxis(tile, false)) { // second iteration of coastline defining
            tile = nextWaterTile(tile, currDir, x, y, mapSize);
            if (tile == null) {
                tile = forceRedirect(tiles, x, y, currDir);
                x--;
                y++;
            } else {
                tiles[x][y] = new Tile(tile, false, false);
            }
            x += changeOnAxis(tile, true);
        }

        System.out.println("Southward generation finished");

        y = endPos;
        currDir = "west";
        endPos = mapSize - (rand.nextInt(waterLineMax) + 2);
        for (; x > endPos; x -= changeOnAxis(tile, true)) { // third iteration of coastline defining
            tile = nextWaterTile(tile, currDir, x, y, mapSize);
            if (tile == null) {
                tile = forceRedirect(tiles, x, y, currDir);
                x--;
                y--;
            } else {
                tiles[x][y] = new Tile(tile, false, false);
            }
            y -= changeOnAxis(tile, false);
        }

        System.out.println("Westward generation finished");

        x = endPos;
        currDir = "north";
        endPos = rand.nextInt(waterLineMax) + 2;
        for (; y > endPos; y -= changeOnAxis(tile, false)) { // fourth iteration of coastline defining
            tile = nextWaterTile(tile, currDir, x, y, mapSize);
            if (tile == null) {
                tile = forceRedirect(tiles, x, y, currDir);
                x++;
                y--;
            } else {
                tiles[x][y] = new Tile(tile, false, false);
            }
            x -= changeOnAxis(tile, true);
        }

        System.out.println("Northward generation finished");

/*
        for (y = 0; y < mapSize; y++){
            boolean genAllWater = true;
            for (x = 0; x < mapSize; x++){
                if(tiles[x][y] == null && genAllWater)
                    tiles[x][y] = new Tile("WaterAll", false, false);
                else {
                    if (genAllWater)
                        genAllWater = false;
                    else
                        genAllWater = true;
                }
            }
        }
*/
        //////////////////////////////////////////////////////////

        System.out.println("Finished water tiles gen");

        // GEN MOUNTAINS

        System.out.println("Starting mountain tiles gen");

        for (int a = 0; a < rand.nextInt(MAX_MOUNTAIN) + MIN_MOUNTAIN; a++) {
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

        System.out.println("Finished mountain tiles gen");

        // GEN HEAVY FOREST, AND THEN LIGHT FOREST AROUND IT

        System.out.println("Starting forest tiles gen");

        for (int a = 0; a < rand.nextInt(MAX_FOREST) + MIN_FOREST; a++) {
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

        System.out.println("Finished forest tiles gen");

        System.out.println("Starting settlement tiles gen");

        for (int a = 0; a < rand.nextInt(MAX_SETTLEMENT) + MIN_SETTLEMENT; a++) {
            // TODO: IMPROVE AND IMPLEMENT ALL SETTLEMENTS, CREATE KINGDOMS ETC...
            while (true) {
                int randX = rand.nextInt(mapSize) + 2;
                int randY = rand.nextInt(mapSize) + 2;
                if (isAreaEmpty(tiles, randX, randY, 1, mapSize)) {
                    tiles[randX][randY] = new Tile("Settlement", false, true, "Name", "Village", 'c', 50);
                    break;
                }
            }
        }

        System.out.println("Filling in null tiles...");

        for (int a = 0; a < mapSize; a++) {
            for (int b = 0; b < mapSize; b++) {
                if (tiles[b][a] == null)
                    tiles[b][a] = new Tile("Grass", true, false);
            }
        }

        System.out.println("World gen done!");
/*
        for (int a = 0; a < mapSize; a++)
            for (int b = 0; b < mapSize; b++)
                if (tiles[b][a].type.contains("Water"))
                    System.out.println(tiles[b][a].type);
*/
        return tiles;
    }

    private boolean isAreaEmpty(Tile[][] tiles, int x, int y, int radius, int mapSize) {
        for (int a = y - radius; a < y + radius; a++)
            for (int b = x - radius; b < x + radius; b++)
                if (a < 0 || b < 0 || a >= mapSize || b >= mapSize || tiles[a][b] != null)
                    return false;

        return true;
    }

    private void populateArea(Tile[][] tiles, int x, int y, int radius, String type) {
        for (int a = y - radius; a < y + radius; a++)
            for (int b = x - radius; b < x + radius; b++)
                if (tiles[a][b] == null && rand.nextInt(10) < 7) // 70 percent chance of spawning tile
                    tiles[a][b] = new Tile(type, false, false);
    }
}

class Faction {

    String kingdomName;
    String capitalSettlement;
    List<String> memberSettlements;

    Faction() {
        kingdomName = "";
        capitalSettlement = "";
        memberSettlements = new ArrayList<>();
    }

    public void addToKingdom(String name) {
        memberSettlements.add(name);
    }

    public void removeFromKingdom(String name) {
        memberSettlements.remove(name);
    }

    public boolean isInKingdom(String name) {
        return memberSettlements.contains(name);
    }
}
