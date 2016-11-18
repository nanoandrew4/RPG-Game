package overworld;

import java.awt.*;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import main.*;

public class OverworldModel implements java.io.Serializable {

    /*
        Data holder and handler for all non-graphical code
        Also handles any interaction with model related classes (such as Map) to retrieve data
        TODO: FIX OUTOFBOUNDS THAT HAPPENS AT FORCE REDIRECT
        TODO: FIX COASTLINE GEN (CHECK getPossibleDirections() and changeOnAxis northbound definitely has an issue)
        TODO: SWSE TILE PLACES INCORRECTLY
    */

    private Map map;
    private Party player;
    private ArrayList<Party> parties;
    private transient PartyAI partyAI;
    private String modelName;

    private FileAccess fileAccess;

    OverworldModel(int mapSize, boolean newGame) {
        parties = new ArrayList<>();

        fileAccess = new FileAccess();
        fileAccess.loadFile("src/data/player");

        if (newGame) {
            try {
                newGame(mapSize);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        startPartyAI();
    }

    void setModelName(String name) {
        this.modelName = name;
    }

    String getModelName() {
        return modelName;
    }

    void createPlayer(float baseSpeed, String faction) {
        player = new Party(0, 0, (short) fileAccess.getFromFile("locationX", "short"), (short) fileAccess.getFromFile("locationY", "short"), baseSpeed, (float) Math.random(), new ArrayList<>(), faction);
    }

    void createPlayer(float baseSpeed, ArrayList<String> members, String faction) {
        player = new Party(0, 0, (short) fileAccess.getFromFile("locationX", "short"), (short) fileAccess.getFromFile("locationY", "short"), baseSpeed, (float) Math.random(), members, faction);
    }

    void startPartyAI() {
        partyAI = new PartyAI();
        // must set all objects
        partyAI.setMap(map);
        partyAI.setParties(parties);
        partyAI.setPlayer(player);
        partyAI.start();
    }

    void genParties(float baseSpeed) {
        Random rand = new Random();
        for (int x = 0; x < 10; x++)
            parties.add(new Party(0, 0,
                    (short) (rand.nextInt(OverworldView.zoom * 2 + 1) + player.getTileX() - OverworldView.zoom),
                    (short) (rand.nextInt(OverworldView.zoom * 2 + 1) + player.getTileY() - OverworldView.zoom),
                    baseSpeed, 0.5f, new ArrayList<>(), "none"));
    }

    Tile[][] getTiles() { // returns 2D array of type Tile
        return map.getTiles();
    }

    boolean[][] getBooleanMap() {
        return map.getBooleanMap();
    }

    int getMapSize() { // returns map size
        return map.getMapSize();
    }

    Party getPlayer() {
        return player;
    }

    ArrayList<Party> getParties() {
        return parties;
    }

    Party getParty(int index) {
        return parties.get(index);
    }

    void setCurrPos(int index, int pos) { // sets current pos at index to current value plus sum
        if (index == 0)
            player.setTileX((short) pos);
        else
            player.setTileY((short) pos);
    }

    int getCurrPos(int index) {
        if (index == 0)
            return player.getTileX();
        else
            return player.getTileY();
    }

    private void newGame(int mapSize) throws SQLException {
        map = new Map(mapSize, true);
    }

    void saveGame() { // saves game
        /*try {
            map.save();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }
}

class Map implements java.io.Serializable {

    /*
        Map class holds all information related to the overworld map, such as the world gen algorithm and 2D array of type Tile
     */

    private Random rand; // random value seeded with current time to use in world gen
    //private DBManager dbManager;

    private Tile[][] tiles; // array containing all the information for each tile in the game
    private HashMap<String, Point> settlementHashMap;
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

    Map(int mapSize, boolean newGame) throws SQLException {
        //this.dbManager = dbManager;
        settlementHashMap = new HashMap<>();
        rand = new Random(System.currentTimeMillis());

        /*if (!newGame) {
            ResultSet rs = dbManager.selectFromDatabase("WORLD_DATA");

            while (rs.next()) {
                this.mapSize = rs.getRow();
            }
            this.mapSize = (int) Math.sqrt(this.mapSize) + 1;

        } else*/
        this.mapSize = mapSize;

        booleanMap = new boolean[this.mapSize][this.mapSize];

        //dbManager.setMapSize(this.mapSize);

        MIN_MOUNTAIN = (int) (1.0 * mapSize);
        MAX_MOUNTAIN = (int) (1.75 * mapSize);
        MIN_FOREST = 5 * mapSize;
        MAX_FOREST = (int) (12.5 * mapSize);
        MIN_SETTLEMENT = (int) (1.1 * mapSize);
        MAX_SETTLEMENT = 2 * mapSize;
        MIN_DUNGEON = (int) (0.7 * mapSize);
        MAX_DUNGEON = (int) (1.5 * mapSize);

        if (newGame) {
            try {
                newMap();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            //load();
        }

        updateBooleanMap();
    }

    private void newMap() throws SQLException {
        //dbManager.setMapSize(mapSize);
        tiles = genMap(mapSize);
        //dbManager.createTables();
    }

    int getMapSize() { // returns map size
        return mapSize;
    }

    Tile[][] getTiles() {
        return tiles;
    }

    // TODO: NEEDS IMPLEMENTING
    HashMap<String, Point> getSettlementHashMap() {
        return settlementHashMap;
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

    /*private void load() throws SQLException {

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

    void save() throws SQLException {

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
    }*/

    private int returnDiffTileBonus(int sameTileCount) {
        return (int) (5 * (Math.pow(2, -0.5 * sameTileCount)));
    }

    private int returnProximityBonus(int var, int mapSize) {
        return (int) ((1 / 8) * Math.pow(var - ((2 + (mapSize / 50)) / 2), 2));
    }

    private String genName() {
        return "Name";
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

        if (y <= 4 || x <= 4 || y >= mapSize - 5 || x >= mapSize - 5) // equal signs necessary to avoid rare crash
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
            Returns the tiles that match up with the previous one (on all sides)
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
            } else if (prev.contains("W")) {
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
            } else if (prev.contains("W")) {
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
                    return -1;
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
        return null; // should not
    }

    private boolean returnCanGenWater(String tile) {
        return tile.equals("WaterE") || tile.equals("WaterN") || tile.equals("WaterNE");
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
        String genDir = "east";
        //String currDir = "east";

        int endPos = mapSize - (rand.nextInt(waterLineMax) + 3);
        int y = rand.nextInt(waterLineMax) + 3;
        String tile = "";
        String prevTile;
        int sameTileCount = 0;

        for (int x = rand.nextInt(waterLineMax) + 3; x < mapSize - endPos; x += changeOnAxis(tile, true)) { // first iteration of coastline defining
            prevTile = tile;
            if (prevTile.equals(tile))
                sameTileCount++;
            else
                sameTileCount = 0;
            tile = nextWaterTile(tile, genDir, x, y, mapSize, sameTileCount);
            if (tile == null) {
                tile = forceRedirect(tiles, x, y, genDir);
                assert tile != null;
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

        tile = "WaterW";
        tiles[endPos++][y] = new Tile(tile);
        y++;

        System.out.println("Eastward generation finished");
        System.out.println("Last x,y positions : " + endPos + ", " + y);

        int x = endPos;
        genDir = "south";
        endPos = mapSize - (rand.nextInt(waterLineMax) + 3);
        for (; y < mapSize - endPos; y += changeOnAxis(tile, false)) { // second iteration of coastline defining
            prevTile = tile;
            assert prevTile != null;
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
        tiles[x][endPos++] = new Tile(tile);
        x--;

        System.out.println("Southward generation finished");
        System.out.println("Last x,y positions : " + x + ", " + endPos);

        y = endPos;
        genDir = "west";
        endPos = (rand.nextInt(waterLineMax) + 3);
        for (; x > endPos; x += changeOnAxis(tile, true)) { // third iteration of coastline defining
            prevTile = tile;
            assert prevTile != null;
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
        tiles[endPos--][y] = new Tile(tile);
        y--;

        System.out.println("Westward generation finished");
        System.out.println("Last x,y positions : " + endPos + ", " + y);

        x = endPos;
        genDir = "north";
        endPos = (rand.nextInt(waterLineMax) + 3);
        for (; y > endPos; y += changeOnAxis(tile, false)) { // fourth iteration of coastline defining
            prevTile = tile;
            assert prevTile != null;
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

        System.out.println("Northward generation finished");
        System.out.println("Last x,y positions : " + x + ", " + endPos);

        for (y = 0; y < mapSize; y++) {
            boolean genAllWater = true;
            for (x = 0; x < mapSize; x++) {
                if (tiles[x][y] == null) {
                    if (genAllWater)
                        tiles[x][y] = new Tile("WaterAll");
                } else {
                    for (; x < mapSize; x++)
                        if (tiles[x][y] == null && tiles[x - 1][y] != null && returnCanGenWater(tiles[x - 1][y].type)) {
                            genAllWater = true;
                            break;
                        }
                }
            }
        }

        // END WATER TILES GEN
        //////////////////////////////////////////////////////////

        System.out.println("Finished water tiles gen");

        // GEN MOUNTAINS

        int numOfType = rand.nextInt(MAX_MOUNTAIN) + MIN_MOUNTAIN;

        System.out.println("Starting mountain tiles gen");

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

        System.out.println("Finished mountain tiles gen");

        // GEN HEAVY FOREST, AND THEN LIGHT FOREST AROUND IT

        System.out.println("Starting forest tiles gen");

        numOfType = rand.nextInt(MAX_FOREST) + MIN_FOREST;

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

        System.out.println("Finished forest tiles gen");

        System.out.println("Starting dungeon tile gen");

        numOfType = rand.nextInt(MAX_DUNGEON) + MIN_DUNGEON;

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

        System.out.println("Starting settlement tiles gen");

        for (int a = 0; a < rand.nextInt(MAX_SETTLEMENT) + MIN_SETTLEMENT; a++) {
            // TODO: IMPROVE AND IMPLEMENT ALL SETTLEMENTS, CREATE KINGDOMS ETC...
            String name;
            while (true) {
                int randX = rand.nextInt(mapSize) + 2;
                int randY = rand.nextInt(mapSize) + 2;
                if (isAreaEmpty(tiles, randX, randY, 2, mapSize)) {
                    tiles[randX][randY] = new Tile("Settlement", "Village", "c", name = genName(), 50);
                    settlementHashMap.put(name, new Point(randX, randY));
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

class Party implements java.io.Serializable {
    /*
        Class contains all data for different parties that exist on the overworld map
        NOTE: Path will allow passage between mountain tiles that are diagonal of each other
        NOTE: ALL SHOULD HAVE SAME STARTING SPEED AND FOV, AND WILL CHANGE WITH PARTY MEMBERS AND STATS
        TODO: WHEN A LARGER PARTY IS BETWEEN YOU AND YOUR TARGET TILE, WILL BOUNCE BACK AND FORWARD UNTIL LARGER PARTY IS MOVED... MAYBE PATHFIND AROUND IT?
     */

    private Path path;

    private float xOffset, yOffset; // pixel offset from center of tile (only within tile)
    private short tileX, tileY; // position on global map in terms of tile
    private ArrayList<String> members; // members of party
    private String faction; // what faction they owe allegiance to
    boolean onScreen;
    private LinkedList<Party> targets, chasers;
    private float hostility;

    private Control dir;
    private Point pixelStartPos;
    private Point start;
    private Point dest;

    private float maxSpeed;
    private short fov;

    private char state;

    boolean temp;

    Party(float xOffset, float yOffset, short tileX, short tileY, float speed, float hostility, ArrayList<String> members, String faction) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.tileX = tileX;
        this.tileY = tileY;
        this.fov = 4;
        this.hostility = hostility;
        this.maxSpeed = speed;
        this.members = members;
        this.faction = faction;

        //System.out.println("xPos: " + tileX);
        //System.out.println("yPos: " + tileY);
        //System.out.println();
    }

    int getTileX() {
        return tileX;
    }

    int getTileY() {
        return tileY;
    }

    float getxOffset() {
        return xOffset;
    }

    float getyOffset() {
        return yOffset;
    }

    Control getDir() {
        return dir;
    }

    Path getPath() {
        return path;
    }

    Point getStart() {
        if (start == null)
            System.out.println("yelp");
        return start;
    }

    Point getPixelStartPos() {
        return pixelStartPos;
    }

    float getMaxSpeed() {
        return maxSpeed;
    }

    void setTileX(short tileX) {
        this.tileX = tileX;
    }

    void setTileY(short tileY) {
        this.tileY = tileY;
    }

    void setDir(Control dir) {
        this.dir = dir;
    }

    void setPath(Path path) {
        this.path = path;
    }

    void setStart(int x, int y) {
        start = new Point(x, y);
    }

    void setPixelStartPos(int x, int y) {
        pixelStartPos = new Point(x, y);
    }

    void nextMove(boolean[][] booleanMap, ArrayList<Party> parties) {
        // TODO: IF IN GROUPS FROM SAME FACTION (MULTIPLE PARTIES) LOWER TI FOR ALL MEMBERS

        if (!temp) {
            travelTo(booleanMap, 102, 75);
            temp = true;
            return;
        }

        LinkedList<Party> nearParties = parties.stream().filter(party -> Math.abs(party.getTileX() - tileX) < fov && Math.abs(party.getTileY() - tileY) < fov).collect(Collectors.toCollection(LinkedList::new));

        targets = new LinkedList<>();
        chasers = new LinkedList<>();

        if (state == 'c') { // chasing
            chase();
        } else if (state == 'f') { // fleeing
            flee();
        } else if (nearParties.size() > 0) { // doing nothing
            for (Party p = nearParties.pop(); nearParties.peek() != null; p = nearParties.pop()) {
                if (p.members != null) { // if owns more members make it viable for attacking
                    if (getTileDistanceToEntity(p) <= 1 && (float) members.size() / (float) p.members.size() > 1.2)
                        chasers.add(p);
                    else if (getTileDistanceToEntity(p) <= 1 && (float) members.size() / (float) p.members.size() < 0.8)
                        targets.add(p);
                }
            }

            if (chasers.size() == 0 && targets.size() > 0 && state != 't') { // for now, don't stop heading somewhere if viable target appears, only if being chased
                chase();
            } else if (chasers.size() > 0 && state != 't') {
                flee();
            } else if (state == 't') {
                if ((Math.abs(start.getX() - getTileX()) == 1 || Math.abs(start.getY() - getTileY()) == 1)
                        && (int) pixelStartPos.getX() == (int) getxOffset() && (int) pixelStartPos.getY() == (int) getyOffset()) {
                    //System.out.println("Change in direction");
                    start = new Point(getTileX(), getTileY());
                    pixelStartPos = new Point((int) getxOffset(), (int) getyOffset());
                    move(convertFromPath(dir = path.next()));
                } else {
                    move(convertFromPath(dir));
                    /*System.out.println(pixelStartPos.getX());
                    System.out.println(getxOffset());
                    System.out.println(pixelStartPos.getY());
                    System.out.println(getyOffset());
                    System.out.println();*/
                }
            } else {
                wander();
            }
        }
    }

    private int getTileDistanceToEntity(Party p) {
        return Math.abs(getTileX() - p.getTileX() > Math.abs(getTileY() - p.getTileY()) ? Math.abs(getTileX() - p.getTileX()) : Math.abs(getTileY() - p.getTileY()));
    }

    private float getPixelDistanceToEntity(Party p) {
        return (float) Math.abs(Math.sqrt(Math.pow(Math.abs((getTileX() * OverworldView.mapTileSize + xOffset) - (p.getTileX()) * OverworldView.mapTileSize + p.getxOffset()), 2) +
                Math.pow(Math.abs((getTileY() * OverworldView.mapTileSize + yOffset) - (p.getTileY() * OverworldView.mapTileSize + p.getyOffset())), 2)));
    }

    private float getXPixelDistanceToEntity(Party p) {
        return (float) ((p.getTileX() - getTileX()) * OverworldView.mapTileSize + getxOffset() + p.getxOffset());
    }

    private float getYPixelDistanceToEntity(Party p) {
        return (float) ((p.getTileY() - getTileY()) * OverworldView.mapTileSize + getyOffset() + p.getyOffset());
    }

    private double[] calcAngles(double xOffset, double yOffset, double mapTileSize) {

        /*
            Calculates and returns angles from current position on tile to leftmost and rightmost point
         */

        double[] angles = new double[2]; // stores langle, rangle

        angles[0] = Math.toDegrees(Math.atan(yOffset / (xOffset + (mapTileSize / 2)))); // left
        angles[1] = Math.toDegrees(Math.atan(yOffset / ((mapTileSize / 2) - xOffset))); // right

        return angles;
    }

    private void detectTileChange(double mapTileSize) {

        double[] angles = calcAngles(xOffset, yOffset, mapTileSize);
        double leftAngle = angles[0];
        double rightAngle = angles[1];

        /*System.out.println("xPos: " + getTileX());
        System.out.println("yPos: " + getTileY());
        System.out.println("Langle: " + leftAngle);
        System.out.println("Rangle: " + rightAngle);
        System.out.println("xOffset: " + xOffset);
        System.out.println("yOffset: " + yOffset);*/

        if (Math.abs(leftAngle) >= 22.5 || Math.abs(rightAngle) >= 22.5) { // new tile
            if (rightAngle >= 22.5) {
                xOffset -= mapTileSize / 2;
                yOffset -= mapTileSize / 4;
                tileY--;
            }
            if (rightAngle <= -22.5) {
                xOffset -= mapTileSize / 2;
                yOffset += mapTileSize / 4;
                tileX++;
            }
            if (leftAngle >= 22.5) {
                xOffset += mapTileSize / 2;
                yOffset -= mapTileSize / 4;
                tileX--;
            }
            if (leftAngle <= -22.5) {
                xOffset += mapTileSize / 2;
                yOffset += mapTileSize / 4;
                tileY++;
            }
        }
    }

    private void wander() { // wander around with no target
        if (Math.random() > 0.7d) { // 70% chance they'll move
            move(getRandDir());
            System.out.println("wandering");
        } else
            move(Control.NULL); // 30% chance they will stay stationary
    }

    private Control getRandDir() {
        Random rand = new Random();
        int num = rand.nextInt(8) + 1;

        switch (num) {
            case 1:
                return Control.UP;
            case 2:
                return Control.UPRIGHT;
            case 3:
                return Control.RIGHT;
            case 4:
                return Control.DOWNRIGHT;
            case 5:
                return Control.DOWN;
            case 6:
                return Control.DOWNLEFT;
            case 7:
                return Control.LEFT;
            case 8:
                return Control.UPLEFT;
            default:
                return Control.NULL;
        }
    }

    private void chase() { // chase another party to provoke combat
        state = 'c';

        float maxTargetValue = 0;
        Party target = null;

        for (Party p : targets) {
            float targetValue = 1 / (p.members.size() * getTileDistanceToEntity(p));
            if (targetValue > maxTargetValue) {
                target = p;
                maxTargetValue = targetValue;
            }
        }

        float x = getXPixelDistanceToEntity(target);
        float y = getYPixelDistanceToEntity(target);

        move(maxSpeed * Math.abs(x / (x + y)) * (x > 0 ? 1 : -1), (maxSpeed / 2) * Math.abs(y / (x + y)) * (y > 0 ? 1 : -1));
    }

    private void flee() { // flee from one or more parties
        state = 'f';

        int x = 0;
        int y = 0;

        for (Party p : chasers) {
            x += getXPixelDistanceToEntity(p);
            y += getYPixelDistanceToEntity(p);
        }

        move(maxSpeed * Math.abs(x / (x + y)) * (x > 0 ? 1 : -1), (maxSpeed / 2) * Math.abs(y / (x + y)) * (y > 0 ? 1 : -1));
    }

    private void move(Control direction) {
        //System.out.println(direction);
        xOffset += getSpeedX(direction);
        yOffset += getSpeedY(direction);
        detectTileChange(OverworldView.mapTileSize);
    }

    void move(float xOffset, float yOffset) {
        this.xOffset += xOffset;
        this.yOffset += yOffset;
        detectTileChange(OverworldView.mapTileSize);
    }


    Control convertFromPath(Control dir) {
        switch (dir) {
            case UP:
                return Control.UPRIGHT;
            case UPRIGHT:
                return Control.RIGHT;
            case RIGHT:
                return Control.DOWNRIGHT;
            case DOWNRIGHT:
                return Control.DOWN;
            case DOWN:
                return Control.DOWNLEFT;
            case DOWNLEFT:
                return Control.LEFT;
            case LEFT:
                return Control.UPLEFT;
            case UPLEFT:
                return Control.UP;
            default:
                return Control.NULL;
        }
    }

    float getSpeedX(Control dir) {
        if (dir == Control.UPRIGHT || dir == Control.DOWNRIGHT)
            return 0.7f * maxSpeed;
        else if (dir == Control.UPLEFT || dir == Control.DOWNLEFT)
            return 0.7f * -maxSpeed;
        else if (dir == Control.RIGHT)
            return maxSpeed;
        else if (dir == Control.LEFT)
            return -maxSpeed;
        else
            return 0f;
    }

    float getSpeedY(Control dir) {
        if (dir == Control.UPRIGHT || dir == Control.UPLEFT)
            return (0.7f * maxSpeed / 2);
        else if (dir == Control.DOWNRIGHT || dir == Control.DOWNLEFT)
            return 0.7f * (-maxSpeed / 2);
        else if (dir == Control.UP)
            return maxSpeed / 2;
        else if (dir == Control.DOWN)
            return -maxSpeed / 2;
        else
            return 0f;
    }

    void travelTo(boolean[][] booleanMap, int destinationTileX, int destinationTileY) { // travel to coords
        state = 't';
        start = new Point(getTileX(), getTileY());
        pixelStartPos = new Point((int) getxOffset(), (int) getyOffset()); // might need to use to only use until second decimal point if rounding errors occur
        path = new Path();
        path.pathFind(booleanMap, new Point(getTileX(), getTileY()), dest = new Point(destinationTileX, destinationTileY), true);
        dir = path.next();
        move(convertFromPath(dir));
    }
}

class PartyAI extends Thread implements java.io.Serializable {

    private Map map;
    private Party player;
    private ArrayList<Party> parties;

    private boolean running = true;

    PartyAI() {
        this.setDaemon(true);
    }

    void setMap(Map map) {
        this.map = map;
    }

    void setPlayer(Party player) {
        this.player = player;
    }

    void setParties(ArrayList<Party> parties) {
        this.parties = parties;
    }


    @Override
    public void run() {
        while (running) {
            long start = System.currentTimeMillis();
            for (Party p : parties)
                p.nextMove(map.getBooleanMap(), parties);
            //System.out.println("AI took " + (System.currentTimeMillis() - start) + "ms");
            if (System.currentTimeMillis() - start < 33) // hard cap at 33 moves per frame
                try {
                    synchronized (this) {
                        this.wait(33 - (System.currentTimeMillis() - start));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

    void stopThread() {
        running = false;
    }
}

class Faction implements Serializable {

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