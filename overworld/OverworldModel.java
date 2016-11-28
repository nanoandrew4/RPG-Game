package overworld;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

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
    private long startTime = System.currentTimeMillis();
    private Random rand = new Random();

    OverworldModel() {
        parties = new ArrayList<>();

        try {
            newGame(1000);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        startPartyAI();
    }

    void setModelName(String name) {
        this.modelName = name;
    }

    String getModelName() {
        return modelName;
    }

    public long getStartTime() {return startTime;}

    // creates player party
    void createPlayer(float baseSpeed, String faction) {
        int[] startPos = getStartPos();
        player = new Party(0, 0, (short) startPos[0], (short) startPos[1], baseSpeed, (float) Math.random(), new ArrayList<>(), faction);
    }

    // generates starting coords for player on new game
    private int[] getStartPos() {
        int[] startPos = new int[2];

        do {
            int x = rand.nextInt(getMapSize() - 1);
            int y = rand.nextInt(getMapSize() - 1);
            if (getTiles()[x][y].tresspassable) {
                startPos[0] = x; startPos[1] = y;
                return startPos;
            }
        } while (true);
    }

    // starts PartyAI thread
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

    // sets current pos at index to current value plus sum
    void setCurrPos(int index, int pos) {
        if (index == 0)
            player.setTileX((short) pos);
        else
            player.setTileY((short) pos);
    }

    // gets player position on either x or y axis
    int getCurrPos(int index) {
        if (index == 0)
            return player.getTileX();
        else
            return player.getTileY();
    }

    // generates new map
    private void newGame(int mapSize) throws SQLException {
        map = new Map(mapSize);
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