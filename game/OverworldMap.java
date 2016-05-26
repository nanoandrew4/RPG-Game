package game;

import javafx.scene.layout.Pane;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class loadMap extends Thread{ // thread has to be able to run in parallel to regular game

    Values values = Main.values;
    int xPos = 0, yPos = 0;

    loadMap(){}

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
    }

    @Override
    public void run(){
        getPlayerVals();

        for(int a = 0; a < values.mapSize; a++){ // WIP
            if(a > values.mapZoomMax)
                OverworldMap.minAreaLoaded = true;
            System.out.println(((float)a / (float)values.mapSize) * 100f + "% done");
            for(int x = xPos - a; x < xPos + a; x++){
                for(int y = yPos - a; y < yPos + a; y++)
                    if(OverworldMap.mapArr[(x > 0 ? (x < values.mapSize ? x : values.mapSize -1) : 0)][(y > 0 ? (y < values.mapSize ? y : values.mapSize -1) : 0)] == null)
                        OverworldMap.mapArr[(x > 0 ? (x < values.mapSize ? x : values.mapSize -1) : 0)][(y > 0 ? (y < values.mapSize ? y : values.mapSize -1) : 0)] = new Tile("Village", false);
            }
        }
    }
}

public class OverworldMap{

    private Values values = Main.values;

    public static Tile[][] mapArr;
    public static boolean minAreaLoaded = false;

    int xPos, yPos;

    public Camera camera;

    ExecutorService threadPool = Executors.newFixedThreadPool(1);

    OverworldMap(){
        mapArr = new Tile[values.mapSize][values.mapSize];

        if(values.screenWidth > values.screenHeight){
            values.mapTileSize = (int)(values.screenHeight / values.mapSize);
        }
        else {
            values.mapTileSize = (int)(values.screenWidth / values.mapSize);
        }

        threadPool.execute(new loadMap());
        threadPool.shutdown();

        xPos = values.initxPos;
        yPos = values.inityPos;

        while (!minAreaLoaded){
            System.out.println(String.valueOf(minAreaLoaded));
            Main.sleep(1000);
        }
    }

    public Pane getLayout(int zoomLevel){
        camera = new Camera(mapArr, xPos, yPos);
        return camera.zoomLevels[zoomLevel];
    }
}
