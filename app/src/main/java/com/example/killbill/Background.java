package com.example.killbill;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Background {
    int x = 0, y = 0;
    Bitmap background;
    /*
    Bitmap-->
     method by which a display space (such as a graphics image file) is defined, including the colour of each of its pixels (or bits).
    */


    Background (int screenX, int screenY, Resources res) //constructor to take object's coordinates
    {
     //also take object of Resource to decode the the bitmap from the drawable folder
        background = BitmapFactory.decodeResource(res, R.drawable.background);
        background = Bitmap.createScaledBitmap(background, screenX, screenY, false); //resize the bitmap to fit on our entire screen

    }
}
