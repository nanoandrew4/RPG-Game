package overworld;

import javafx.geometry.Point2D;
import main.Control;
import main.Path;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Collectors;

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
    private LinkedList<Party> targets, chasers;
    private float hostility;

    private Control dir;
    private Point pixelStartPos;
    private Point start;
    private Point dest;

    private float maxSpeed;
    private float speedX, speedY; // only for player use
    private short fov;

    private char state;
    transient private Point2D prevAngles;

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
        prevAngles = new Point2D(0,0);
    }

    int getTileX() {
        return tileX;
    }

    void setTileX(short tileX) {
        this.tileX = tileX;
    }

    int getTileY() {
        return tileY;
    }

    void setTileY(short tileY) {
        this.tileY = tileY;
    }

    float getxOffset() {
        return xOffset;
    }

    void setxOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    float getyOffset() {
        return yOffset;
    }

    void setyOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    float getSpeedX() {
        return speedX;
    }

    void setSpeedX(float speed) {
        this.speedX = speed;
    }

    float getSpeedY() {
        return speedY;
    }

    void setSpeedY(float speed) {
        this.speedY = speed;
    }

    Control getDir() {
        return dir;
    }

    void setDir(Control dir) {
        this.dir = dir;
    }

    Path getPath() {
        return path;
    }

    void setPath(Path path) {
        this.path = path;
    }

    Point getStart() {
        return start;
    }

    Point getPixelStartPos() {
        return pixelStartPos;
    }

    float getMaxSpeed() {
        return maxSpeed;
    }

    void setStart(int x, int y) {
        start = new Point(x, y);
    }

    void setPixelStartPos(int x, int y) {
        pixelStartPos = new Point(x, y);
    }

    void nextMove(Tile[][] tiles, boolean[][] booleanMap, ArrayList<Party> parties) {
        // TODO: IF IN GROUPS FROM SAME FACTION (MULTIPLE PARTIES) LOWER TI FOR ALL MEMBERS

        // makes list with nearby parties
        LinkedList<Party> nearParties = parties.stream().filter(party -> Math.abs(party.getTileX() - tileX) < fov && Math.abs(party.getTileY() - tileY) < fov).collect(Collectors.toCollection(LinkedList::new));

        targets = new LinkedList<>();
        chasers = new LinkedList<>();

        // determines AI action
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
                    start = new Point(getTileX(), getTileY());
                    pixelStartPos = new Point((int) getxOffset(), (int) getyOffset());
                    move(convertFromPath(dir = path.next()), tiles);
                    setDir(dir);
                } else {
                    move(convertFromPath(dir), tiles);
                    setDir(dir);
                }
            } else {
                wander(tiles);
            }
        }
    }

    // returns distance from this party to given entity on map in tiles
    private int getTileDistanceToEntity(Party p) {
        return Math.abs(getTileX() - p.getTileX() > Math.abs(getTileY() - p.getTileY()) ? Math.abs(getTileX() - p.getTileX()) : Math.abs(getTileY() - p.getTileY()));
    }

    // returns pixel distance from this party to entity on map
    private float getPixelDistanceToEntity(Party p) {
        return (float) Math.abs(Math.sqrt(Math.pow(Math.abs((getTileX() * OverworldView.mapTileSize + xOffset) - (p.getTileX()) * OverworldView.mapTileSize + p.getxOffset()), 2) +
                Math.pow(Math.abs((getTileY() * OverworldView.mapTileSize + yOffset) - (p.getTileY() * OverworldView.mapTileSize + p.getyOffset())), 2)));
    }

    // returns distance on x axis from this party to entity on map
    private float getXPixelDistanceToEntity(Party p) {
        return (float) ((p.getTileX() - getTileX()) * OverworldView.mapTileSize + getxOffset() + p.getxOffset());
    }

    // returns distance on y axis from this party to entity on map
    private float getYPixelDistanceToEntity(Party p) {
        return (float) ((p.getTileY() - getTileY()) * OverworldView.mapTileSize + getyOffset() + p.getyOffset());
    }

    Point2D calcAngles(double xOffset, double yOffset, double mapTileSize) {

        /*
            Calculates and returns angles from current position on tile to leftmost and rightmost point respectively
         */

        return new Point2D(Math.toDegrees(Math.atan(yOffset / (xOffset + (mapTileSize / 2)))), Math.toDegrees(Math.atan(yOffset / ((mapTileSize / 2) - xOffset))));
    }

    boolean detectTileChange(double mapTileSize, boolean player) {

        /*
            Function to determine whether a change in tiles has occurred or not
         */

        Point2D angles = calcAngles(xOffset, yOffset, mapTileSize);
        double leftAngle = angles.getX();
        double rightAngle = angles.getY();

//        System.out.println("xPos: " + getTileX());
//        System.out.println("yPos: " + getTileY());
//        System.out.println("Langle: " + leftAngle);
//        System.out.println("Rangle: " + rightAngle);
//        System.out.println("xOffset: " + xOffset);
//        System.out.println("yOffset: " + yOffset);
//        System.out.println();

        // changed tiles
        if ((Math.abs(leftAngle) > 30.0 || Math.abs(rightAngle) > 30.0 || Math.abs(xOffset) > mapTileSize / 2) && !angles.equals(prevAngles)) {
            prevAngles = angles;
            //System.out.println("Langle: " + leftAngle);
            //System.out.println("Rangle: " + rightAngle);
            if (OverworldController.debug)
                System.out.println((System.currentTimeMillis() - OverworldController.start) + " - " + leftAngle + ", " + rightAngle + " - " + getTileX() + ", " + getTileY());

            if (rightAngle > 30.0) {
                if (!player) {
                    xOffset -= mapTileSize / 2;
                    yOffset -= mapTileSize / 4;
                }
                tileY--;
            }
            if (rightAngle < -30.0) {
                if (!player) {
                    xOffset -= mapTileSize / 2;
                    yOffset += mapTileSize / 4;
                }
                tileX++;
            }
            if (leftAngle > 30.0) {
                if (!player) {
                    xOffset += mapTileSize / 2;
                    yOffset -= mapTileSize / 4;
                }
                tileX--;
            }
            if (leftAngle < -30.0) {
                if (!player) {
                    xOffset += mapTileSize / 2;
                    yOffset += mapTileSize / 4;
                }
                tileY++;
            }
            if (Math.abs(xOffset) >= OverworldView.mapTileSize / 2) {
                if (xOffset > 0) {
                    if (!player)
                        xOffset -= mapTileSize;
                    tileX++;
                    tileY--;
                } else {
                    if (!player)
                        xOffset += mapTileSize;
                    tileX--;
                    tileY++;
                }
            }

            return true;
        }

        return false;
    }

    private void wander(Tile[][] tiles) { // wander around with no target
        if (Math.random() > 0.7d) { // 70% chance they'll move
            move(getRandDir(), tiles);
            System.out.println("wandering");
        } else
            move(Control.NULL, tiles); // 30% chance they will stay stationary
    }

    // returns a random direction for wandering AI
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

    // chase another party to provoke combat
    private void chase() {
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

    // flee from one or more parties
    private void flee() {
        state = 'f';

        int x = 0;
        int y = 0;

        for (Party p : chasers) {
            x += getXPixelDistanceToEntity(p);
            y += getYPixelDistanceToEntity(p);
        }

        move(maxSpeed * Math.abs(x / (x + y)) * (x > 0 ? 1 : -1), (maxSpeed / 2) * Math.abs(y / (x + y)) * (y > 0 ? 1 : -1));
    }

    // moves this party
    private void move(Control direction, Tile[][] tiles) {
        xOffset += getSpeedX(direction, tiles);
        yOffset += getSpeedY(direction, tiles);
        detectTileChange(OverworldView.mapTileSize, false);
    }

    private void move(float xOffset, float yOffset) {
        this.xOffset += xOffset;
        this.yOffset += yOffset;
        detectTileChange(OverworldView.mapTileSize, false);
    }

    // since path dir returns upright directions and map is angled at 45 degrees, converts direction returned by path to one used by entities on the map
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

    // returns the speed on the x axis of a given direction
    float getSpeedX(Control dir, Tile[][] tiles) {
        float speed;

        if (dir == Control.UPRIGHT || dir == Control.DOWNRIGHT)
            speed = 0.7f * maxSpeed;
        else if (dir == Control.UPLEFT || dir == Control.DOWNLEFT)
            speed = 0.7f * -maxSpeed;
        else if (dir == Control.RIGHT)
            speed = maxSpeed;
        else if (dir == Control.LEFT)
            speed = -maxSpeed;
        else
            speed = 0f;

        if (!canMove(tiles, calcAngles(xOffset, yOffset, OverworldView.mapTileSize), dir))
            speed = 0;

        return speed;
    }

    // returns the speed on the y axis of a given direction
    float getSpeedY(Control dir, Tile[][] tiles) {
        float speed;
        if (dir == Control.UPRIGHT || dir == Control.UPLEFT)
            speed = (0.7f * maxSpeed / 2);
        else if (dir == Control.DOWNRIGHT || dir == Control.DOWNLEFT)
            speed = 0.7f * (-maxSpeed / 2);
        else if (dir == Control.UP)
            speed = maxSpeed / 2;
        else if (dir == Control.DOWN)
            speed = -maxSpeed / 2;
        else
            speed = 0f;

        if (!canMove(tiles, calcAngles(xOffset, yOffset, OverworldView.mapTileSize), dir))
            speed = 0;

        //setDir(yDir);

        return speed;
    }

    private boolean canMove(Tile[][] tiles, Point2D angles, Control dir) {
        /*
            If player moves on to a tile that is not tresspassable, only allows the player to move away from the tile
         */

        if (!tiles[getTileX()][getTileY()].tresspassable) {
            if (Math.abs(angles.getX()) > Math.abs(angles.getY())) {
                if (angles.getX() >= 0) {
                    if (!(dir == Control.UPLEFT || dir == Control.LEFT || dir == Control.UP))
                        return false;
                } else {
                    if (!(dir == Control.DOWNLEFT || dir == Control.LEFT || dir == Control.DOWN))
                        return false;
                }
            } else {
                if (angles.getY() >= 0) {
                    if (!(dir == Control.UPRIGHT || dir == Control.RIGHT || dir == Control.UP))
                        return false;
                } else {
                    if (!(dir == Control.DOWNRIGHT || dir == Control.RIGHT || dir == Control.DOWN))
                        return false;
                }
            }
        }

        return true;
    }

    // using a path and pathfinding, makes AI move towards a destination tile, as opposed ot wandering
    void travelTo(Tile[][] tiles, boolean[][] booleanMap, int destinationTileX, int destinationTileY) { // travel to coords
        state = 't';
        start = new Point(getTileX(), getTileY());
        pixelStartPos = new Point((int) getxOffset(), (int) getyOffset()); // might need to use to only use until second decimal point if rounding errors occur
        path = new Path();
        path.pathFind(booleanMap, new Point(getTileX(), getTileY()), dest = new Point(destinationTileX, destinationTileY), true);
        dir = path.next();
        setDir(convertFromPath(dir));
        move(convertFromPath(dir), tiles);
    }
}