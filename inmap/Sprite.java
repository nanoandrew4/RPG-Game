/*
    Sprite animation for inmap.
*/

package inmap;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Sprite extends Transition {
    
    private final ImageView imageView;
    private final int count;
    private final int row;
    private final int column;
    private final int width;
    private final int height;
    private final Image image;

    Sprite(ImageView imageView, Duration duration, int count, int row,
            int column, int width, int height, Image image) {
        this.imageView = imageView;
        this.count = count;
        this.row = row;
        this.column = column;
        this.width = width;
        this.height = height;
        this.image = image;
        
        setCycleDuration(duration);
        setInterpolator(Interpolator.LINEAR);
    }
    
    public void start() {
        imageView.setImage(image);
//        imageView.setViewport(new Rectangle2D(column * width, row * height, 64, 96));

        play();
    }

    @Override
    protected void interpolate(double k) {
        final int index = (int)Math.floor(k * count);
        if(index < count) {
            final int x = (index + column) * width;
            final int y = row * height;
            imageView.setViewport(new Rectangle2D(x, y, width, height));
        }
    }
}
