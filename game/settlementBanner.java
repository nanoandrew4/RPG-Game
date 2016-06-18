package game;

import javafx.scene.image.ImageView;

public class settlementBanner {

    ImageView banner, attack, enter, diplomacy;
    private int relationship;
    private String name;
    private String faction;
    private double parentSize, parentX, parentY;

    settlementBanner(String name, String faction, int relationship, double parentSize, double parentX, double parentY){
        this.name = name;
        this.faction = faction;
        this.parentSize = parentSize;
        updateData(relationship, parentX, parentY);

        banner = new ImageView();
        attack = new ImageView(); // on click listeners will be in controller
        enter = new ImageView();
        diplomacy = new ImageView();
    }

    public void updateData(int relationship,double parentX, double parentY){
        this.relationship = relationship;
        this.parentX = parentX;
        this.parentY = parentY;
    }
}
