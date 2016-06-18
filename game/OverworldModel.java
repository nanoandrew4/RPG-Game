/*
    Data holder and handler for all non-graphical code
 */

package game;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class OverworldModel {
    public double mapTileSize;
    public int mapSize, zoom = 8;
    public int[] currPos = new int[2];

    public Tile[][] tiles;

    Random rand;

    OverworldModel(int mapSize){

        this.mapSize = mapSize;

        tiles = new Tile[mapSize][mapSize];

        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get("src/data/player"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("No such file exists");
            // error trap
        }
        for(int x = 0; x < lines.size(); x++){
            if(lines.get(x).contains("locationX"))
                currPos[0] = (lines.get(x).charAt(lines.get(x).length()-2) - 48) * 10 + (lines.get(x).charAt(lines.get(x).length()-1) - 48);

            if(lines.get(x).contains("locationY"))
                currPos[1] = (lines.get(x).charAt(lines.get(x).length()-2) - 48) * 10 + (lines.get(x).charAt(lines.get(x).length()-1) - 48);
        }

        tmpgenMap();
    }

    private void genMap(){
        rand = new Random();

        //////////////////////////////////////////////////////////

        int waterLineMax = mapSize / 20;
        int startPos = rand.nextInt(waterLineMax) + 2; int endPos = rand.nextInt(waterLineMax) + 2;
        int y = startPos;
        for(int x = startPos; x < mapSize - endPos; x++){ // first iteration of coastline defining
            int dir = rand.nextInt(100);
            String name;
            if(dir < 25 && y < waterLineMax) {
                y++;
                name = "coastDown";
            }
            else if(dir > 75 && y > 2) {
                y--;
                name = "coastUp";
            }
            else {
                name = "coastStraight";
            }
            tiles[x][y] = new Tile(name, false, false);
        }

        int x = endPos;
        startPos = y; endPos = mapSize - (rand.nextInt(waterLineMax) + 2);
        for(y = startPos; y < mapSize - endPos; y++){ // second iteration of coastline defining
            int dir = rand.nextInt(100);
            String name;
            if(dir < 25 && y < waterLineMax) {
                x++;
                name = "coastDown";
            }
            else if(dir > 75 && y > 2) {
                x--;
                name = "coastUp";
            }
            else {
                name = "coastStraight";
            }
            tiles[x][y] = new Tile(name, false, false);
        }

        y = endPos;
        startPos = x; endPos = mapSize - (rand.nextInt(waterLineMax) + 2);

        for(x = startPos; x > endPos; x--){ // third iteration of coastline defining
            int dir = rand.nextInt(100);
            String name;
            if(dir < 25 && y < waterLineMax) {
                y++;
                name = "coastDown";
            }
            else if(dir > 75 && y > 2) {
                y--;
                name = "coastUp";
            }
            else {
                name = "coastStraight";
            }
            tiles[x][y] = new Tile(name, false, false);
        }

        x = endPos;
        startPos = y; endPos = rand.nextInt(waterLineMax) + 2;
        for(y = startPos; y > endPos; y--){ // second iteration of coastline defining
            int dir = rand.nextInt(100);
            String name;
            if(dir < 25 && y < waterLineMax) {
                x++;
                name = "coastDown";
            }
            else if(dir > 75 && y > 2) {
                x--;
                name = "coastUp";
            }
            else {
                name = "coastStraight";
            }
            tiles[x][y] = new Tile(name, false, false);
        }

        //////////////////////////////////////////////////////////
    }

    private void tmpgenMap(){
        Random rand = new Random();
        long start = System.currentTimeMillis();

        for(int y = 0; y < mapSize; y++){
            for (int x = 0; x < mapSize; x++){
                int randNum = rand.nextInt(100);
                if(randNum < 30)
                    tiles[x][y] = new Tile("ForestTest", true, false);
                if(randNum >= 30 && randNum < 31)
                    tiles[x][y] = new settlementTile("Village", 'c', 50);
                if(randNum >= 31 && randNum < 90)
                    tiles[x][y] = new Tile("Grass", true, false);
                if(randNum >= 90 && randNum < 100)
                    tiles[x][y] = new Tile("Mountain", true, false);
            }
        }

        System.out.println("Loading world arr took " + (double)(System.currentTimeMillis() - start) / 1000);
    }

    private void loadMap(){

    }
}
