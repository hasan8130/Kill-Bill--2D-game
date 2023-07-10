package com.example.killbill;

import static com.example.killbill.GameView.screenRatioX;
import static com.example.killbill.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;


public class Bird {

    public int speed = 20; //default speed
    public boolean wasShot = true;
    int x = 0, y, width, height, birdCounter = 1;
    Bitmap bird1, bird2, bird3, bird4;

    Bird (Resources res) {

        bird1 = BitmapFactory.decodeResource(res, R.drawable.bird1);
        bird2 = BitmapFactory.decodeResource(res, R.drawable.bird2);
        bird3 = BitmapFactory.decodeResource(res, R.drawable.bird3);
        bird4 = BitmapFactory.decodeResource(res, R.drawable.bird4);

        width = bird1.getWidth();
        height = bird1.getHeight();

        width /= 6;  //reduce size
        height /= 6;

        width = (int) ((float)width * screenRatioX);
        height = (int) ( (float)height * screenRatioY);

        bird1 = Bitmap.createScaledBitmap(bird1, width, height, false);
        bird2 = Bitmap.createScaledBitmap(bird2, width, height, false);
        bird3 = Bitmap.createScaledBitmap(bird3, width, height, false);
        bird4 = Bitmap.createScaledBitmap(bird4, width, height, false);

        y = -height;  //the bird will be placed off the screen in the start
    }

    Bitmap getBird () {

        if (birdCounter == 1) {
            birdCounter++;
            return bird1;
        }

        if (birdCounter == 2) {
            birdCounter++;
            return bird2;
        }

        if (birdCounter == 3) {
            birdCounter++;
            return bird3;
        }

        // so that after the animation ends,it restarts itself
        birdCounter = 1;

        return bird4;
    }

    Rect getCollisionShape ()  //creates rectangle around the bird and return that
    {
        return new Rect(x, y, x + width, y + height);
    }

}
