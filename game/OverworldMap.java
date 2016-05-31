package game;

import javafx.scene.layout.Pane;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class loadMap extends Thread{ // thread has to be able to run in parallel to regular game

    Values values = Main.values;
    int xPos = 0, yPos = 0;
    Tile[][] map;
    Random rand = new Random();

    loadMap(Tile[][] map){this.map = map;}

    private void getPlayerVals(){
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get("src/data/player"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("No such file exists");
            // error trap
        }
        for(int x = 0; x < lines.size(); x++){
            if(lines.get(x).contains("locationX")){
                values.initxPos = (lines.get(x).charAt(lines.get(x).length()-2) - 48) * 10 + (lines.get(x).charAt(lines.get(x).length()-1) - 48);
                xPos = values.initxPos;
            }
            if(lines.get(x).contains("locationY")){
                values.inityPos = (lines.get(x).charAt(lines.get(x).length()-2) - 48) * 10 + (lines.get(x).charAt(lines.get(x).length()-1) - 48);
                yPos = values.inityPos;
            }
        }
        values.currPos[0] = xPos; values.currPos[1] = yPos;
    }

    @Override
    public void run(){
        getPlayerVals();

        long start = System.currentTimeMillis();

        for(int y = 0; y < values.mapSize; y++){
            for (int x = 0; x < values.mapSize; x++){
                int randNum = rand.nextInt(4);
                String name = "";
                if(randNum == 0)
                    name = "ForestTest";
                if(randNum == 1)
                    name = "Village";
                if(randNum == 2)
                    name = "Grass";
                if(randNum == 3)
                    name = "Mountain";
                map[x][y] = new Tile(name, true, false);
            }
        }

        System.out.println("Loading world arr took " + (double)(System.currentTimeMillis() - start) / 1000);

        OverworldMap.minAreaLoaded = true;
    }
}

public class OverworldMap{

    private Values values = Main.values;

    public Tile[][] mapArr;
    public static boolean minAreaLoaded = false;

    int xPos, yPos;

    public Camera camera;

    ExecutorService threadPool = Executors.newFixedThreadPool(1);

    OverworldMap(){
        mapArr = new Tile[values.mapSize][values.mapSize];

        threadPool.execute(new loadMap(mapArr));
        threadPool.shutdown();

        xPos = values.initxPos;
        yPos = values.inityPos;

        while (!minAreaLoaded){
            System.out.println(String.valueOf(minAreaLoaded));
            Main.sleep(1000);
        }
    }

    public Pane getLayout(int zoomLevel){
        camera = new Camera(mapArr, xPos, yPos, zoomLevel);
        return camera.overworldLayout;
    }
}
