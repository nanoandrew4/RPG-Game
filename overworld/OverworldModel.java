package overworld;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.Control;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

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
    private Random rand = new Random();

    private boolean controlsLocked = false;
    private boolean menuOpen = false;

    private Control verticalDir, horizontalDir, dir;

    OverworldModel() {
        parties = new ArrayList<>();

        try {
            newGame(1000);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        startPartyAI();
    }

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
            if (getTiles()[x][y].settlementTile != null) {
                startPos[0] = x;
                startPos[1] = y;
                return startPos;
            }
        } while (true);
    }

    // starts PartyAI thread
    void startPartyAI() {
        partyAI = new PartyAI();
        partyAI.setDaemon(true);
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

    void setControlsLocked(boolean locked) {
        this.controlsLocked = locked;
    }

    void setMenuOpen(boolean menuOpen) {
        this.menuOpen = menuOpen;
    }

    boolean getMenuOpen() {
        return menuOpen;
    }

    // generates new map
    private void newGame(int mapSize) throws SQLException {
        map = new Map(mapSize);
    }

    javafx.geometry.Point2D getAngles() {
        return player.calcAngles(player.getxOffset(), player.getyOffset(), OverworldView.mapTileSize);
    }

    int process(Control key, boolean released) {

        /*
            Return code -1 = remove pane from stack
            Return code 0 = controls locked, do nothing
            Return code 1 = open/close menu (depends on menuOpen boolean)
            Return code 2 = select
            Return code 3 = show tile borders
         */

        // remove one menu from scene
        if (key == Control.BACK && !menuOpen)
            return -1;

        if (key == Control.SELECT)
            return 2;

        // if looking at a menu, lock controls
        if (controlsLocked || !OverworldController.hasControl)
            return 0;

        // when player touches key, stops automatic moving through map (moving by clicking on a tile)
        player.setPath(null);

        // open menu
        if (key == Control.BACK || key == Control.MENU || key == Control.OPENCHAR || key == Control.OPENINV || key == Control.OPENNOTES || key == Control.OPENOPTIONS || key == Control.OPENPARTY)
            return 1;

        //System.out.println("XPos: " + getCurrPos(0));
        //System.out.println("YPos: " + getCurrPos(1));
        //System.out.println("Tile size: " + getMapTileSize());

        // test code for AI movement
        if (key == Control.T) {
            //model.getParty(0).nextMove(model.getBooleanMap(), model.getParties());
            getParty(0).nextMove(getTiles(), getBooleanMap(), getParties());
        }

        // shows tile borders
        if (key == Control.ALT)
            return 3;

        // movement on map processing
        if (key == Control.LEFT || key == Control.RIGHT || key == Control.UP || key == Control.DOWN) {
            if (key == Control.UP || key == Control.DOWN)
                verticalDir = key;
            else
                horizontalDir = key;

            if (verticalDir == Control.UP && horizontalDir == Control.RIGHT)
                dir = Control.UPRIGHT;
            else if (verticalDir == Control.UP && horizontalDir == Control.LEFT)
                dir = Control.UPLEFT;
            else if (verticalDir == Control.DOWN && horizontalDir == Control.RIGHT)
                dir = Control.DOWNRIGHT;
            else if (verticalDir == Control.DOWN && horizontalDir == Control.LEFT)
                dir = Control.DOWNLEFT;
            else if (verticalDir == Control.UP || verticalDir == Control.DOWN)
                dir = verticalDir;
            else
                dir = horizontalDir;

            if (!released) {
                getPlayer().setSpeedY(getPlayer().getSpeedY(dir, getTiles()));
                getPlayer().setSpeedX(getPlayer().getSpeedX(dir, getTiles()));
            } else {
                if (key == Control.RIGHT || key == Control.LEFT) {
                    getPlayer().setSpeedX(0);
                    horizontalDir = Control.NULL;
                } else {
                    getPlayer().setSpeedY(0);
                    verticalDir = Control.NULL;
                }
            }

            if (player.detectTileChange(OverworldView.mapTileSize, true)) {
                return 4;
            }
        }

        return 0;
    }

    int process(MouseEvent event, int xPos, int yPos) {
        if (controlsLocked)
            return 0;

        System.out.println("Tile type: " + getTiles()[xPos][yPos].type);

        if (event.getButton() == MouseButton.PRIMARY) {
            // processing mouse events for left click
            System.out.println("Tile @ pos " + xPos + ", " + yPos);
            if (getTiles()[xPos][yPos].type.equalsIgnoreCase("Settlement") || getTiles()[xPos][yPos].type.equalsIgnoreCase("InMap")) {
                // display additional menus if dungeon or settlement
                // lock controls to prevent moving on world map while scrolling menu
                // stops player to prevent the controls locking from causing the player to move by itself
                if (player.getTileX() - xPos == 0 && player.getTileY() - yPos == 0) {
                    controlsLocked = true;
                    player.setSpeedX(0);
                    player.setSpeedY(0);
                    if (getTiles()[xPos][yPos].type.equalsIgnoreCase("Settlement"))
                        return 11;
                    else if (getTiles()[xPos][yPos].type.equalsIgnoreCase("InMap"))
                        return 12;
                }
            }

        } else if (event.getButton() == MouseButton.SECONDARY) {
            return 21;
        }

        return 0;
    }
}

