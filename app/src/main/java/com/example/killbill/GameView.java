package com.example.killbill;

import android.content.Context;
import android.view.SurfaceView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY, score = 0;
    public static float screenRatioX, screenRatioY; //public as we want to access it from th flight.java class
    private Paint paint;
    private Bird[] birds;
    private SharedPreferences prefs;
    private Random random;
    private SoundPool soundPool;
    private List<Bullet> bullets;
    private int sound;
    private Flight flight;
    private GameActivity activity;
    private Background background1, background2;
  // we will need 2 instances of the background class, as this will help us to make the background move
    public GameView(GameActivity activity, int screenX, int screenY) { //takes object of the GameActivity class
        super(activity);

        this.activity = activity; //refer current object's activity to the activity in the constructor.

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE); //mode private hides the content of the shared preference from other apps in the phone


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else
            //using older version to instantiate SoundPool
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        sound = soundPool.load(activity, R.raw.shoot, 1); //using an external raw music file named shoot stored in res directory

        this.screenX = screenX;
        this.screenY = screenY;

        screenRatioX = 1920f / screenX; //to make compatible on every screen
        screenRatioY = 1080f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        flight = new Flight(this, screenY, getResources()); //Flight class object

        bullets = new ArrayList<>();

        background2.x = screenX; //initially our second background will not be on the screen and it will be placed just after the screen end on the x axis

        paint = new Paint();  //to display the score at the end
        paint.setTextSize(128); //128 pixels.
        paint.setColor(Color.WHITE);

        birds = new Bird[4];

        for (int i = 0;i < 4;i++) {

            Bird bird = new Bird(getResources());
            birds[i] = bird;

        }

        random = new Random();

    }

    @Override
    public void run() {

        while (isPlaying) {

            update ();
            draw ();
            sleep ();

        }

    }

    private void update () {
         // update the value on the x_axis by 10 pixels
        //we wont update in the y axis,as we do not want it to move int the y axis
        //everytime this update method is called our background will move towards left by 10pixels on the axis
        background1.x -= 10 * screenRatioX;
        background2.x -= 10 * screenRatioX;

        //our background is off the screen
        //when this happens we place our background just after the screen ends
        // ex--> -150 +100(screen width) =-50
        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = screenX;
        }

        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = screenX;
        }

        //******************************************************************************8
        if (flight.isGoingUp)
            flight.y -= 30 * screenRatioY; //reduce y value by 30 (means we are placing our flight towards the top of the screen)
        else
            flight.y += 30 * screenRatioY; // pull the flight down

        if (flight.y < 0)  // to ensure the flight do not go off the screen from the top
            flight.y = 0;

        if (flight.y >= screenY - flight.height) //to ensure the flight do not go off the screen from the bottom
            flight.y = screenY - flight.height;

        //*************************************************************************

        List<Bullet> trash = new ArrayList<>();

        for (Bullet bullet : bullets) {

            if (bullet.x > screenX) // bullet is off the screen
                trash.add(bullet);  //add the bullets which are off the screen

            bullet.x += 50 * screenRatioX; //move this bullet by 50 pixels on the x axi
            for (Bird bird : birds) { // if the bullet hits the bird

                if (Rect.intersects(bird.getCollisionShape(),
                        bullet.getCollisionShape())) {

                    score++; //when the bullet hits we reward the user with one point
                    bird.x = -500; // the bird gets off the screen, and so the off screen condition again sends it to the end
                    bullet.x = screenX + 500; // the bullet gets off the screen, and is added to trash later.
                    bird.wasShot = true; //to see if the bullet shot each bird or not, before it gets off the screen

                }

            }

        }

        //******************************************************************

        for (Bullet bullet : trash)
            bullets.remove(bullet);  //remove bullets which are in the trash list
        //*****************************************************************

        for (Bird bird : birds) {

            bird.x -= bird.speed;

            if (bird.x + bird.width < 0) { // the bird is off the screen from the left side

                if (!bird.wasShot) {  // if the bird was not shot and it is still off the screen ,the game will be over
                    isGameOver = true;
                    return;
                }

                int bound = (int) (30 * screenRatioX); //speed limit for our bird
                bird.speed = random.nextInt(bound); //this will give a random speed for our bird

                //random can also return 0, in which case it wont move at all. so we decide the min speed to be 10
                if (bird.speed < 10 * screenRatioX)
                    bird.speed = (int) (10 * screenRatioX);

                bird.x = screenX; // we are setting the bird on the screen at the right side
                bird.y = random.nextInt(screenY - bird.height); // y position will be random
                //we are subtracting bird.height as if it return screenY ,then our bird will be placed off the screen

                bird.wasShot = false;
            }
            //****************************************************************************



            //if the bird hits the flight or the flight is not able to hit the bird then the game will be over.
            if (Rect.intersects(bird.getCollisionShape(), flight.getCollisionShape())) {
            // we will create a rectangle around the bird and the flight ,if those two rect intersects then the game will be over.
                isGameOver = true;
                return;
            }

        }

    }

    private void draw () {

        if (getHolder().getSurface().isValid()) {

            //get the canvas from the surface view to draw our images and other stuff
            Canvas canvas = getHolder().lockCanvas();
            //on this current canvas we will draw both of our backgrounds
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint); //(bitmap,x_pos,y_pos,object of paint class)
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            for (Bird bird : birds)
                canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint);

            canvas.drawText(score + "", screenX / 2f, 154, paint); //draw the score on the screen

            if (isGameOver) { //this will break the thread
                isPlaying = false;
                canvas.drawBitmap(flight.getDead(), flight.x, flight.y, paint); //the bitmap when the flight is dead
                getHolder().unlockCanvasAndPost(canvas); //draw the canvas
                saveIfHighScore(); //checks if current score is the highest score of the user
                waitBeforeExiting ();
                return;
            }

            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint); //draw the flight on the canvas
            //we have to make sure that we draw this after the background,as otherwise it will be drawn below the background

            for (Bullet bullet : bullets)  //to draw the bullets
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas); //shows the canvas on the screen

        }

    }

    private void waitBeforeExiting() { //wait for 3 sec after the game is over and then return to the main screen

        try {
            Thread.sleep(3000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void saveIfHighScore() { // checks if current score is the highest score of the user

        if (prefs.getInt("highscore", 0) < score) { //if the current score is greater then the highest score(default value is 0)
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }

    }

    private void sleep () //wait for 17 milli seconds --> 1000 ms /17 ms = 60
    {
        /*
           1000 ms =1sec . so in 1 second we will be updating the position of the images and drawing it on our screen
           60 times.
           so this will give us 60 frames per sec

        */

        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume () // when this method is called we will resume or start the game
    {

        isPlaying = true;
        thread = new Thread(this); //initialise the thread
        thread.start(); // starting this thread will call the run function

    }

    public void pause ()  //pause our game
    {

        try {
            isPlaying = false;
            thread.join(); //terminates the thread
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) // to make the flight go up and down on touch
    {
       /*
          if the user taps on the left side of the screen, then only we will let our flight go up.
          and if the user taps on the right side of the screen then only the flight will shoot.
       */

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < screenX / 2) { // to check if the user taps on the left side
                    flight.isGoingUp = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                flight.isGoingUp = false;
                if (event.getX() > screenX / 2) // true when the touch event is coming from the right side of the screen-->shoot bullet
                    flight.toShoot++;
                break;
        }

        return true;
    }

    public void newBullet() {

        if (!prefs.getBoolean("isMute", false)) //if the current sound is not on mute then we will play the sound everytime a bullet hits
            soundPool.play(sound, 1, 1, 0, 0, 1);

        Bullet bullet = new Bullet(getResources());
        bullet.x = flight.x + flight.width; // initially this bullet will be placed near the wings of the flight
        bullet.y = flight.y + (flight.height / 2);
        bullets.add(bullet);

    }
}
