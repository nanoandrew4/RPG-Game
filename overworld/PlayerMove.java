package overworld;

import javafx.application.Platform;
import main.Control;
import main.Path;

import java.awt.*;

class PlayerMove extends Thread {

    /*
        When tile is right-clicked thread runs to get player to clicked tile
     */

    private OverworldController controller;
    private OverworldModel model;
    private OverworldView view;

    private int xPos, yPos;

    PlayerMove(OverworldController controller, OverworldModel model, OverworldView view) {
        this.controller = controller;
        this.model = model;
        this.view = view;
    }

    void setPos(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        int areaStartX = model.getPlayer().getTileX() < xPos ? model.getPlayer().getTileX() - 10 > 0 ? model.getPlayer().getTileX() : 0 : xPos - 10 > 0 ? xPos : 0;
        int areaStartY = model.getPlayer().getTileY() < yPos ? model.getPlayer().getTileY() - 10 > 0 ? model.getPlayer().getTileY() : 0 : yPos - 10 > 0 ? yPos : 0;
        int areaDestX = model.getPlayer().getTileX() < xPos ? xPos + 10 < model.getMapSize() - 1 ? xPos : model.getMapSize() - 1 : model.getPlayer().getTileX() + 1 < model.getMapSize() - 1 ? model.getPlayer().getTileX() : model.getMapSize() - 1;
        int areaDestY = model.getPlayer().getTileY() < yPos ? yPos + 10 < model.getMapSize() - 1 ? yPos : model.getMapSize() - 1 : model.getPlayer().getTileY() + 1 < model.getMapSize() - 1 ? model.getPlayer().getTileY() : model.getMapSize() - 1;
        long start = System.currentTimeMillis();
        model.getPlayer().setPath(new Path());
        model.getPlayer().getPath().pathFind(
                model.getBooleanMap(), // implement area search later, use general for now
                new Point(model.getPlayer().getTileX(), model.getPlayer().getTileY()), new Point(xPos, yPos), true
        );
        model.getPlayer().setStart(model.getPlayer().getTileX(), model.getPlayer().getTileY());
        model.getPlayer().setPixelStartPos((int) view.getPlayerXOffset(), (int) view.getPlayerYOffset());
        model.getPlayer().setDir(model.getPlayer().getPath().next());

        model.getPlayer().setSpeedX(model.getPlayer().getSpeedX(model.getPlayer().convertFromPath(model.getPlayer().getDir()), model.getTiles()));
        model.getPlayer().setSpeedY(model.getPlayer().getSpeedY(model.getPlayer().convertFromPath(model.getPlayer().getDir()), model.getTiles()));

        System.out.println("Startpos: " + model.getPlayer().getTileX() + ", " + model.getPlayer().getTileY());
        System.out.println("Endpos: " + xPos + ", " + yPos);

        System.out.println("Path to target tile created in " + (System.currentTimeMillis() - start) + "ms");

        // moves until destination reached, which breaks loop
        while (true) {
            if ((Math.abs(model.getPlayer().getStart().getX() - model.getPlayer().getTileX()) == 1 || Math.abs(model.getPlayer().getStart().getY() - model.getPlayer().getTileY()) == 1)
                    && (int) model.getPlayer().getPixelStartPos().getX() == (int) model.getPlayer().getxOffset() && (int) model.getPlayer().getPixelStartPos().getY() == (int) model.getPlayer().getyOffset()) {
                model.getPlayer().setPixelStartPos((int) view.getPlayerXOffset(), (int) view.getPlayerYOffset());
                model.getPlayer().setStart(model.getPlayer().getTileX(), model.getPlayer().getTileY());
                model.getPlayer().setDir(model.getPlayer().getPath().next());
                model.getPlayer().setSpeedX(model.getPlayer().getSpeedX(model.getPlayer().convertFromPath(model.getPlayer().getDir()), model.getTiles()));
                model.getPlayer().setSpeedY(model.getPlayer().getSpeedY(model.getPlayer().convertFromPath(model.getPlayer().getDir()), model.getTiles()));
                System.out.println("Change dir");
                if (model.getPlayer().getDir() == Control.NULL) {
                    System.out.println("Player reached destination");
                    model.getPlayer().setSpeedX(0);
                    model.getPlayer().setSpeedY(0);
                    model.getPlayer().setDir(Control.NULL);
                    model.getPlayer().setStart(0, 0);
                    model.getPlayer().setPath(null);
                    controller.setMouseEvents();
                    break;
                }
            } else {
                //System.out.println((Math.abs(model.getPlayer().getTileX() - model.getPlayer().getStart().getX())));
                //System.out.println(Math.abs(model.getPlayer().getTileY() - model.getPlayer().getStart().getY()));
                model.getPlayer().setxOffset(view.getPlayerXOffset());
                model.getPlayer().setyOffset(view.getPlayerYOffset());

                int returnCode = model.process(model.getPlayer().getDir(), false);

                if (returnCode == 3) {
                    view.reDraw(model.getAngles(), model.getTiles(), model.getPlayer(), model.getParties());
                    controller.setMouseEvents();
                }
            }
        }
        controller.setMouseEvents();
    }
}