package com.example.killbill;

import static com.example.killbill.GameView.screenRatioX;
import static com.example.killbill.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;


public class Flight {

    int toShoot = 0;
    boolean isGoingUp = false;
    int x, y, width, height, wingCounter = 0, shootCounter = 1;
    Bitmap flight1, flight2, shoot1, shoot2, shoot3, shoot4, shoot5, dead;
    private GameView gameView;


    Flight (GameView gameView, int screenY, Resources res)
    {

        this.gameView = gameView;

        flight1 = BitmapFactory.decodeResource(res, R.drawable.fly1); //initialise the flight object
        flight2 = BitmapFactory.decodeResource(res, R.drawable.fly2);

        width = flight1.getWidth();
        height = flight1.getHeight();

        width /= 4;  // as the image is too big we will reduce its size
        height /= 4;

        width = (int) ((float)width * screenRatioX);  //to make compatible with other devices
        height = (int) ((float)height * screenRatioY);

        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false); //resize the flight bitmap
        flight2 = Bitmap.createScaledBitmap(flight2, width, height, false);

        shoot1 = BitmapFactory.decodeResource(res, R.drawable.shoot1); //initialise the shoot obje ct
        shoot2 = BitmapFactory.decodeResource(res, R.drawable.shoot2);
        shoot3 = BitmapFactory.decodeResource(res, R.drawable.shoot3);
        shoot4 = BitmapFactory.decodeResource(res, R.drawable.shoot4);
        shoot5 = BitmapFactory.decodeResource(res, R.drawable.shoot5);

        shoot1 = Bitmap.createScaledBitmap(shoot1, width, height, false); //rescale the shoot bitmap
        shoot2 = Bitmap.createScaledBitmap(shoot2, width, height, false);
        shoot3 = Bitmap.createScaledBitmap(shoot3, width, height, false);
        shoot4 = Bitmap.createScaledBitmap(shoot4, width, height, false);
        shoot5 = Bitmap.createScaledBitmap(shoot5, width, height, false);

        dead = BitmapFactory.decodeResource(res, R.drawable.dead);
        dead = Bitmap.createScaledBitmap(dead, width, height, false);

        y = screenY / 2; // to set flight on the screen center vertically
        x = (int) (64 * screenRatioX); // to give some margin from the x axis (64 pixels)

    }

    Bitmap getFlight () //we will call this function when we will be drawing our flight on the canvas
    {

        if (toShoot != 0) {

            if (shootCounter == 1) {
                shootCounter++;
                return shoot1; //return shoot1 bitmap
            }

            if (shootCounter == 2) {
                shootCounter++;
                return shoot2; //return shoot2  bitmap
            }

            if (shootCounter == 3) {
                shootCounter++;
                return shoot3;
            }

            if (shootCounter == 4) {
                shootCounter++;
                return shoot4;
            }

            // if none of the above statement is true

            shootCounter = 1; //so the next time when the bullet is shot,the animation repeats itself
            toShoot--;
            gameView.newBullet();

            return shoot5;
        }

        if (wingCounter == 0) { //when function is called for the first time, third time, fifth time and so on
            wingCounter++;
            return flight1;
        }
        wingCounter--; //when function is called for the second time,4th time and so on

        return flight2;
    }

    Rect getCollisionShape () //creates rectangle around the flight
    {
        return new Rect(x, y, x + width, y + height);
    }

    Bitmap getDead () {
        return dead;
    }

}
