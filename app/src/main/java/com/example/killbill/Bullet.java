package com.example.killbill;

import static com.example.killbill.GameView.screenRatioX;
import static com.example.killbill.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;


public class Bullet {

    int x, y, width, height;
    Bitmap bullet;

    Bullet (Resources res) {

        bullet = BitmapFactory.decodeResource(res, R.drawable.bullet);

        width = bullet.getWidth();
        height = bullet.getHeight();

        width /= 4; //reduce bullet size ,as it is too big for the screen
        height /= 4;

        width = (int) ((float)width * screenRatioX); //to make bullet compatible to other displays
        height = (int) ((float)height * screenRatioY);

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false);

    }

    Rect getCollisionShape () {
        return new Rect(x, y, x + width, y + height);
    }

}