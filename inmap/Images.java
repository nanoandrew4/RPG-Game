/*
    Inmap images: tiles, sprites, and extra.
*/

package inmap;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class Images {
    static Image[] tiles;
    static Image[] sprites;
    public static Image[] playerSprites;
    
    static Image hero;
    static Image playerAnimation;
    static Image portrait;
    
    static Image health;
    static Image mana;
    
    static Image black;
    static Image blackcircle;
    
    //temp
    static Image weapon;
    static Image armor;
    static Image accessory;
    static Image material;
    static Image consumable;
    
    static {
        //load tiles
        PixelReader pr = new Image("/media/graphics/inmap/IMTiles.png", 640, 960, false, false).getPixelReader();
        tiles = new Image[100];
        for(int i = 0; i < 100; i++) {
            tiles[i] = new WritableImage(pr, i%10*64, (int)(Math.floor(i/10))*96, 64, 96);
        }
        
        //load sprites
        pr = new Image("/media/graphics/inmap/IMSprites.png", 640, 960, false, false).getPixelReader();
        sprites = new Image[100];
        for(int i = 0; i < 100; i++) {
            sprites[i] = new WritableImage(pr, i%10*64, (int)(Math.floor(i/10))*96, 64, 96);
        }
        
        //individual images
        health = new Image("/media/graphics/inmap/health.jpg", 64, 64, true, false);
        mana = new Image("/media/graphics/inmap/mana.jpg", 64, 64, true, false);
        black = new Image("/media/graphics/inmap/black.jpg", 64, 64, true, false);
        blackcircle = new Image("/media/graphics/inmap/blackcircle.png", 64, 64, true, false);
        playerAnimation = new Image("/media/graphics/inmap/TrumpSprites.png");
        
        //get playersprites from images
        playerSprites = new Image[5];
        playerSprites[0] = sprites[3];
        playerSprites[1] = sprites[6];
        playerSprites[2] = sprites[7];
        playerSprites[3] = sprites[11];
        playerSprites[4] = sprites[12];
        
        //temp
        weapon = new Image("/media/graphics/inmap/weapon.png", 64, 64, false, false);
        armor = new Image("/media/graphics/inmap/armor.png", 64, 64, false, false);
        accessory = new Image("/media/graphics/inmap/accessory.png", 64, 64, false, false);
        material = new Image("/media/graphics/inmap/material.png", 64, 64, false, false);
        consumable = new Image("/media/graphics/inmap/consumable.png", 64, 64, false, false);
    }
    
    public static void setHeroSprite(double width, double height, int heroSprite, String heroPortrait) {
        if(heroSprite == 99)
            hero = new Image("/media/graphics/inmap/trump.png", 64, 96, false, false);
        else
            hero = playerSprites[heroSprite];
            
        portrait = new Image(heroPortrait, width*2, width*2, false, false);
    }
    
    public static Image getSprite(int id) {
        if(id == 99)
            return hero;
        else if(id >= 0)
            return sprites[id];
        else
            return null;
    }
}
