package overworld;

import main.Main;

import java.util.ArrayList;

class PartyAI extends Thread implements java.io.Serializable {

    private Map map;
    private Party player;
    private ArrayList<Party> parties;

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
        while (OverworldController.running) {
            long start = System.currentTimeMillis();
            for (Party p : parties)
                p.nextMove(map.getTiles(), map.getBooleanMap(), parties);
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
}