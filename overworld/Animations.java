package overworld;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Timer;
import java.util.TimerTask;

class ScaleAnimation {

    /*
        Increments imageView and image size while keeping center of image in same position as starting imageView
     */

    private ImageView imageView;
    private Image image;
    private double xIncrement, yIncrement, startPixelX, startPixelY, limit;
    private long period;

    ScaleAnimation(Image image, double startPixelX, double startPixelY, double limit) {
        this.image = image;
        this.startPixelX = startPixelX;
        this.startPixelY = startPixelY;
        xIncrement = image.getWidth() / 2;
        yIncrement = image.getHeight() / 2;
        this.limit = limit;
        period = 33;
        animate();
    }

    ScaleAnimation(double xIncrement, double yIncrement, Image image, double startPixelX, double startPixelY, double limit) {
        this.xIncrement = xIncrement;
        this.yIncrement = yIncrement;
        this.startPixelX = startPixelX;
        this.startPixelY = startPixelY;
        this.image = image;
        this.limit = limit;
        period = 33;
        animate();
    }

    private void animate() {

        imageView = new ImageView(image);
        imageView.setLayoutX(startPixelX);
        imageView.setLayoutY(startPixelY);

        Timer animTimer = new Timer();
        animTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Image bannerIMG = new Image("/media/graphics/overworld/banner/banner.png",
                        imageView.getImage().getWidth() + xIncrement, imageView.getImage().getHeight() + yIncrement, false, false);
                imageView.setImage(bannerIMG);
                imageView.setLayoutX(imageView.getLayoutX() - xIncrement / 2);
                imageView.setLayoutY(imageView.getLayoutY() - yIncrement / 2);
                if (imageView.getFitWidth() >= limit || imageView.getImage().getWidth() >= limit)
                    this.cancel();
            }
        }, 0, period);
    }

    // returns animated imageView to be added to pane
    ImageView getImageView() {
        return imageView;
    }
}
