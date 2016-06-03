package game;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class OverworldMap{

    private Values values = Controller.values;

    private static Tile[][] mapArr;

    OverworldMap(){
        mapArr = new Tile[values.mapSize][values.mapSize];

        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get("src/data/player"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("No such file exists");
            // error trap
        }
        for(int x = 0; x < lines.size(); x++){
            if(lines.get(x).contains("locationX"))
                values.currPos[0] = (lines.get(x).charAt(lines.get(x).length()-2) - 48) * 10 + (lines.get(x).charAt(lines.get(x).length()-1) - 48);

            if(lines.get(x).contains("locationY"))
                values.currPos[0] = (lines.get(x).charAt(lines.get(x).length()-2) - 48) * 10 + (lines.get(x).charAt(lines.get(x).length()-1) - 48);

        }
        loadMap();
    }

    private void loadMap(){
        Random rand = new Random();
        long start = System.currentTimeMillis();

        for(int y = 0; y < values.mapSize; y++){
            for (int x = 0; x < values.mapSize; x++){
                int randNum = rand.nextInt(100);
                if(randNum < 30)
                    mapArr[x][y] = new Tile("ForestTest", true, false);
                if(randNum >= 30 && randNum < 31)
                    mapArr[x][y] = new settlementTile("Village", 'c');
                if(randNum >= 31 && randNum < 90)
                    mapArr[x][y] = new Tile("Grass", true, false);
                if(randNum >= 90 && randNum < 100)
                    mapArr[x][y] = new Tile("Mountain", true, false);
            }
        }

        System.out.println("Loading world arr took " + (double)(System.currentTimeMillis() - start) / 1000);
    }

    public static Tile[][] getMapArr(){
        return mapArr;
    }
}
