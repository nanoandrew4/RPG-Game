package game;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OverworldModel {

    /*
        Data holder and handler for all non-graphical code
        Also handles any interaction with model related classes (such as Map) to retrieve data
        TODO: FIX OUTOFBOUNDS THAT HAPPENS AT FORCE REDIRECT
        TODO: FIX COASTLINE GEN
    */

    private double mapTileSize;
    private int zoom = 6;
    private int[] currPos = new int[2];

    private Map map;
    private FileAccess fileAccess = new FileAccess();

    OverworldModel(int mapSize, boolean newGame) {

        try {
            map = new Map(mapSize, newGame);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        fileAccess.loadFile("src/data/player");
        currPos[0] = (int) fileAccess.getFromFile("locationX", "int");
        currPos[1] = (int) fileAccess.getFromFile("locationY", "int");

    }

    public Tile[][] getTiles() { // returns 2D array of type Tile
        return map.getTiles();
    }

    public int getMapSize() { // returns map size
        return map.getMapSize();
    }

    public int getZoom() { // returns zoom
        return zoom;
    }

    public double getMapTileSize() { // returns the tile size to be used
        return mapTileSize;
    }

    public int[] getCurrPos() { // returns current position on overworld, x -> 0 and y -> 1
        return currPos;
    }

    public int getCurrPos(int index) { // returns position specified by index, x -> 0 and y -> 1
        return currPos[index];
    }

    public void setCurrPos(int index, int sum) { // sets current pos at index to current value plus sum
        currPos[index] += sum;
    }

    public void setMapTileSize(double mapTileSize) { // sets map tile size to be used by view
        this.mapTileSize = mapTileSize;
    }

    public void saveGame() { // saves game
        try {
            map.save();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class Map {

    /*
        Map class holds all information related to the overworld map, such as the world gen algorithm and 2D array of type Tile
     */

    private Random rand; // random value seeded with current time to use in world gen
    private DBManager dbManager;

    private Tile[][] tiles; // array containing all the information for each tile in the game
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

    Map(int mapSize, boolean newGame) throws SQLException {
        dbManager = new DBManager("test");
        rand = new Random(System.currentTimeMillis());

        if (!newGame) {
            ResultSet rs = dbManager.selectFromDatabase("WORLD_DATA");

            while (rs.next()) {
                this.mapSize = rs.getRow();
            }

            this.mapSize = (int) Math.sqrt(this.mapSize) + 1;

            System.out.println(this.mapSize);
        } else
            this.mapSize = mapSize;

        dbManager.setMapSize(this.mapSize);

        MIN_MOUNTAIN = (int) (2.5 * mapSize);
        MAX_MOUNTAIN = (int) (3.5 * mapSize);
        MIN_FOREST = 15 * mapSize;
        MAX_FOREST = 20 * mapSize;
        MIN_SETTLEMENT = 1 * mapSize;
        MAX_SETTLEMENT = 2 * mapSize;

        if (newGame) {
            tiles = genMap(mapSize);
            try {
                dbManager.createTables();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            load();
        }
    }

    public int getMapSize() { // returns map size
        return mapSize;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void load() throws SQLException {

        System.out.println("Starting world load");

        long start = System.currentTimeMillis();

        ResultSet rs = dbManager.selectFromDatabase("WORLD_DATA");
        int x = 0, y = 0;

        tiles = new Tile[mapSize][mapSize];

        while (rs.next()) {

            if (x >= mapSize - 1) {
                x = 0;
                y++;
            }

            String type = rs.getString("TYPE");
            String subType = rs.getString("SUBTYPE");
            String branch = rs.getString("BRANCH");
            String name = rs.getString("NAME");
            int relationship = rs.getInt("RELATIONSHIP");

            if (type.equalsIgnoreCase("Settlement")) // add other dynamic tiles in the future
                tiles[x][y] = new Tile(type, subType, branch, name, relationship);

            else
                tiles[x][y] = new Tile(type);

            x++;
        }

        System.out.println("Load took: " + ((double) (System.currentTimeMillis() - start) / 1000) + "s");
    }

    public void save() throws SQLException {

        System.out.println("Starting game save");
        long start = System.currentTimeMillis();

        dbManager.deleteTable("WORLD_DATA");
        dbManager.createTables();

        dbManager.setAutoCommit(false);

        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                if (tiles[x][y].settlementTile != null) {
                    dbManager.insertIntoTable_WORLD_DATA(tiles[x][y].type, tiles[x][y].settlementTile.subType, tiles[x][y].settlementTile.branch,
                            tiles[x][y].settlementTile.settlementName, tiles[x][y].settlementTile.relationship);
                } else
                    dbManager.insertIntoTable_WORLD_DATA(tiles[x][y].type, null, null, null, 0);
            }
        }

        dbManager.commit();
        dbManager.setAutoCommit(true);

        System.out.println("Game save took: " + ((float) (System.currentTimeMillis() - start) / 1000) + "s");
    }

    private String nextWaterTile(String prev, String dir, int x, int y, int mapSize) {

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

        if (y <= 2 || x <= 2 || y >= mapSize - 3 || x >= mapSize - 3) // equal signs necessary to avoid rare crash
            return null; // activates force redirect

        if (dir.equalsIgnoreCase("north")) {
            for (int strings = 0; strings < possibleDirections.length - 1; strings++) {
                if (possibleDirections[strings].length() == 7)
                    priority[strings] += 5;
                else if (possibleDirections[strings].length() == 9)
                    priority[strings] += 3;
                else
                    priority[strings]++;
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
                    priority[strings]++;
                if (possibleDirections[strings].contains("SW"))
                    priority[strings] += (1 / 8) * Math.pow(y - ((2 + (mapSize / 50)) / 2), 2);
                else
                    priority[strings] -= (1 / 8) * Math.pow(y - ((2 + (mapSize / 50)) / 2), 2);
            }
        } else if (dir.equalsIgnoreCase("south")) {
            for (int strings = 0; strings < possibleDirections.length - 1; strings++) {
                if (possibleDirections[strings].length() == 7)
                    priority[strings] += 5;
                else if (possibleDirections[strings].length() == 9)
                    priority[strings] += 3;
                else
                    priority[strings]++;
                if (possibleDirections[strings].contains("SE"))
                    priority[strings] += (1 / 8) * Math.pow(x - ((2 + (mapSize / 50)) / 2), 2);
                else
                    priority[strings] -= (1 / 8) * Math.pow(x - ((2 + (mapSize / 50)) / 2), 2);
            }
        } else if (dir.equalsIgnoreCase("east")) {
            for (int strings = 0; strings < possibleDirections.length - 1; strings++) {
                if (possibleDirections[strings].length() == 7)
                    priority[strings] += 5;
                else if (possibleDirections[strings].length() == 9)
                    priority[strings] += 3;
                else
                    priority[strings]++;
                if (possibleDirections[strings].contains("NE"))
                    priority[strings] += (1 / 8) * Math.pow(y - ((2 + (mapSize / 50)) / 2), 2);
                else
                    priority[strings] -= (1 / 8) * Math.pow(y - ((2 + (mapSize / 50)) / 2), 2);
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

    private String[] getPossibleDirections(String prev, String currDir) {

        /*
            Returns the tiles that match up with the previous one (on all sides)
         */

        String[] dirs = new String[5];

        if (currDir.equalsIgnoreCase("north")) {

        }

        /*
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
        }
            */
        if (prev.contains("NW")) {
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

    private int changeOnAxis(String tile, boolean x) {

        /*
            Returns change in position the algorithm should make to continue generating the coastline
         */

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
         */

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
        return null; // should not
    }

    private Tile[][] genMap(int mapSize) {

        /*
            Core of the world gen algorithm
            Generates coastline in 4 steps, one for each cardinal direction
            Then generates static tiles (lakes, mountains, forests)
            Generates settlements
            Fills in null tiles with grass type tiles
         */

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
                if (tiles[x][y] == null) // prevents overwriting tiles from forceRedirect
                    tiles[x][y] = new Tile(tile);
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
                if (tiles[x][y] == null) // prevents overwriting tiles from forceRedirect
                    tiles[x][y] = new Tile(tile);
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
                if (tiles[x][y] == null) // prevents overwriting tiles from forceRedirect
                    tiles[x][y] = new Tile(tile);
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
                if (tiles[x][y] == null) // prevents overwriting tiles from forceRedirect
                    tiles[x][y] = new Tile(tile);
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
        // END WATER TILES GEN
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
                    tiles[randX][randY] = new Tile("Settlement", "Village", "c", "Name,", 50);
                    break;
                }
            }
        }

        System.out.println("Filling in null tiles...");

        for (int a = 0; a < mapSize; a++) {
            for (int b = 0; b < mapSize; b++) {
                if (tiles[b][a] == null)
                    tiles[b][a] = new Tile("Grass");
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

        /*
            Returns true if the area has not been yet initialized or is not out of bounds
            Returns false if any part of the area has been initialized or is out of bounds
         */

        for (int a = y - radius; a < y + radius; a++)
            for (int b = x - radius; b < x + radius; b++)
                if (a < 0 || b < 0 || a >= mapSize || b >= mapSize || tiles[a][b] != null)
                    return false;

        return true;
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

class Faction {

    /*
        Class contains all data related to factions
     */

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
