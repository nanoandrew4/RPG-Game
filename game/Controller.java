package game;

public class Controller{

    public static Values values = new Values();
    public static OverworldMap overworldMap;

    Controller(){

        //while (!values.startGame); // waits for play to be pressed, does nothing in case user opens and closes without playing
        System.out.println("Starting game");
        overworldMap = new OverworldMap();

        Camera.launch(Camera.class);
        //values.initComplete = true; // allows camera to start displaying
    }

    public static void sleep(long time){
        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < time);
    }
}
