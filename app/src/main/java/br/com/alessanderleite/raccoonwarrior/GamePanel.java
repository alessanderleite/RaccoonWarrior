package br.com.alessanderleite.raccoonwarrior;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private Random rand = new Random();

    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;

    Bitmap heartA;
    Bitmap heartB;
    Bitmap heartC;
    private int hearts = 3;

    Bitmap coinBonus;
    public int heroCoins;
    private long bonusStartTime;
    private ArrayList<Bonus> myCoins;

    MediaPlayer mp;
    SoundPool coinSound;
    int coinSoundId;

    Bitmap myPanel;

    public static final int MOVESPEED = -5;
    private Background bg;
    private Hero hero;

    private ArrayList<Bullet> bullet;
    private long bulletStartTime;

    private ArrayList<Enemy> alien;
    private long alienStartTime;

    private ArrayList<Obstacle> obstacle;
    private long obstacleStartTime;

    private ArrayList<Borderbottom> botborder;
    private long borderStartTime;

    private boolean newGameCreated;
    private long startReset;
    private boolean reset;
    private boolean disappear;
    private boolean started;

    private Explosion explosion;
    private int best;

    private MainThread thread;

    public GamePanel(Context context) {
        super(context);

        mp = MediaPlayer.create(context, R.raw.arcademusicloop);
        coinSound = new SoundPool(99, AudioManager.STREAM_MUSIC, 0);
        coinSoundId = coinSound.load(context, R.raw.pickedcoin,1);

        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new MainThread(getHolder(), this);

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background));
        hero = new Hero(BitmapFactory.decodeResource(getResources(), R.drawable.hero), 45, 45, 2);

        bullet = new ArrayList<Bullet>();
        bulletStartTime = System.nanoTime();

        alien = new ArrayList<Enemy>();
        alienStartTime = System.nanoTime();

        obstacle = new ArrayList<Obstacle>();
        obstacleStartTime = System.nanoTime();

        botborder = new ArrayList<Borderbottom>();
        borderStartTime = System.nanoTime();

        myCoins = new ArrayList<Bonus>();
        bonusStartTime = System.nanoTime();

        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;

        while (retry && counter < 1000) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!hero.getPlaying() && newGameCreated && reset) {
                hero.setPlaying(true);
                hero.setUp(true);
            }
            if (hero.getPlaying()){
                if (!started)started = true;
                reset = false;
                hero.setUp(true);
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            hero.setUp(false);
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void update() {
        if (hero.getPlaying()) {
            mp.start();

            bg.update();
            hero.update();

            // add coin behavior
            long bonustime = (System.nanoTime() - bonusStartTime)/1000000;
            if (((bonustime > (3000 - hero.getScore()/4)))) {
                myCoins.add(new Bonus((BitmapFactory.decodeResource(getResources(), R.drawable.coin)),
                        WIDTH + 1, (int)(rand.nextDouble()*(HEIGHT - 200)), 20, 20, 1));
                bonusStartTime = System.nanoTime();
            }
            for (int i = 0; i < myCoins.size(); i++) {
                myCoins.get(i).update();
                if (collision(myCoins.get(i), hero)) {
                    //sound
                    coinSound.play(coinSoundId,5,5,1,0,1);

                    myCoins.remove(i);
                    heroCoins += 1;
                    break;
                }
                if (myCoins.get(i).getX() < - 100) {
                    myCoins.remove(i);
                    break;
                }
            }

            //add bot border behavior
            long borderElapsed = (System.nanoTime() - borderStartTime)/1000000;
            if (borderElapsed > 100) {
                botborder.add(new Borderbottom(BitmapFactory.decodeResource(getResources(), R.drawable.borderbottom),WIDTH + 10, ((HEIGHT - 80) + rand.nextInt(10))));
                botborder.add(new Borderbottom(BitmapFactory.decodeResource(getResources(), R.drawable.bordertop), WIDTH + 10, ((HEIGHT - 540) + rand.nextInt(10))));
                borderStartTime = System.nanoTime();
            }
            for (int i = 0; i < botborder.size(); i++) {
                botborder.get(i).update();

                if (collision(botborder.get(i), hero)) {
                    hero.setPlaying(false);
                    break;
                }
                if (botborder.get(i).getX() < 10) {
                    botborder.remove(i);
                }
            }

            //add bot obstacle
            long obstacleElapsed = (System.nanoTime() - obstacleStartTime)/1000000;
            if (obstacleElapsed > (15000 - hero.getScore())/4) {
                obstacle.add(new Obstacle(BitmapFactory.decodeResource(getResources(), R.drawable.obstacle),
                        WIDTH + 10, HEIGHT - 290 + rand.nextInt(150), 90, 300, hero.getScore(), 1));
                obstacleStartTime = System.nanoTime();
            }
            for (int i = 0; i < obstacle.size(); i++) {
                obstacle.get(i).update();
                if (collision(obstacle.get(i), hero)) {
                    hero.setPlaying(false);
                    break;
                }
            }

            //add bullet on timer
            long bulletTimer = (System.nanoTime() - bulletStartTime)/1000000;
            if (bulletTimer > (2500 - hero.getScore()/4)) {
                bullet.add(new Bullet((BitmapFactory.decodeResource(getResources(), R.drawable.bullet)), hero.getX()+60, hero.getY()+24,15,7, 7));
                bulletStartTime = System.nanoTime();
            }
            for (int i = 0; i < bullet.size(); i++) {
                bullet.get(i).update();
                if (bullet.get(i).getX()<-10) {
                    bullet.remove(1);
                }
            }

            //add enemy aliens
            long alienElapsed = (System.nanoTime() - alienStartTime)/1000000;
            if (alienElapsed > (10000 - hero.getScore()/4)) {
                alien.add(new Enemy(BitmapFactory.decodeResource(getResources(), R.drawable.enemy),
                        WIDTH + 10, (int)(rand.nextDouble() * (HEIGHT - 50)), 40, 60, hero.getScore(), 3));
                alienStartTime = System.nanoTime();
            }
            for (int i = 0; i < alien.size(); i++) {
                alien.get(i).update();

                if (collision(alien.get(i), hero)) {
                    alien.remove(i);

                    hearts--;
//                    hero.setPlaying(false);
                    break;
                }

                //remove alien if it is way off the screen
                if (alien.get(i).getX() < -100) {
                    alien.remove(i);
                    break;
                }

                //collision alien with bullet (fire)
                for (int j = 0; j < bullet.size(); j++) {
                    if (collision(alien.get(i), bullet.get(j))) {
                        explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), alien.get(i).getX(),
                                alien.get(i).getY(), 100, 100, 15);

                        alien.remove(i);
                        bullet.remove(j);

                        best += 30;

                        break;
                    }
                    bullet.get(j).update();
                }
            }
        }//end if playing

        else {
            hero.resetDYA();

            if (!reset) {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                disappear = true;

                explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), hero.getX(),
                        hero.getY(), 100,100,15);
            }
            explosion.update();

            long resetElapsed = (System.nanoTime() - startReset)/1000000;
            if (resetElapsed > 2500 && !newGameCreated) {
                newGame();
            }
        }
    }

    public boolean collision(GameObject a, GameObject b) {
        if (Rect.intersects(a.getRectangle(), b.getRectangle())) {
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        final float scaleFactorX = getWidth()/(WIDTH*1.f);
        final float scaleFactorY = getHeight()/(HEIGHT*1.f);

        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            if (!disappear) {
                hero.draw(canvas);
            }


            //draw bullet versions
            for (Bullet fp : bullet) {
                fp.draw(canvas);
            }

            //draw enemy
            for (Enemy aln : alien) {
                aln.draw(canvas);
            }

            //draw bot obstacle
            for (Obstacle obsb : obstacle) {
                obsb.draw(canvas);
            }

            //draw bot border
            for (Borderbottom brb : botborder) {
                brb.draw(canvas);
            }

            //draw coins
            for (Bonus mb : myCoins) {
                mb.draw(canvas);
            }

            //draw the explosion when we lose
            if (started) {
                explosion.draw(canvas);
            }

            drawText(canvas);
            canvas.restoreToCount(savedState);
        }
    }

    public void newGame() {

        disappear = false;
        alien.clear();
        obstacle.clear();
        botborder.clear();
        bullet.clear();

        hero.resetDYA();
        hero.resetScore();
        myCoins.clear();

        hero.setY(HEIGHT/2);

        newGameCreated = true;
    }

    public void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Distance: " + (hero.getScore()*2),10,HEIGHT - 10, paint);
        canvas.drawText("Score: " + best, WIDTH - 215, HEIGHT - 10, paint);

        coinBonus = BitmapFactory.decodeResource(getResources(), R.drawable.coin);
        canvas.drawBitmap(coinBonus, WIDTH - 130, 0, null);
        canvas.drawText(" " + heroCoins, WIDTH - 90, 25, paint);

        if (hearts == 3) {
            heartA = BitmapFactory.decodeResource(getResources(), R.drawable.lifea);
            canvas.drawBitmap(heartA, WIDTH/2 - 120, 0, null);
            heartB = BitmapFactory.decodeResource(getResources(), R.drawable.lifeb);
            canvas.drawBitmap(heartB, WIDTH/2 - 80, 0, null);
            heartC = BitmapFactory.decodeResource(getResources(), R.drawable.lifec);
            canvas.drawBitmap(heartC, WIDTH/2 - 40, 0, null);
        }
        if (hearts == 2) {
            heartA = BitmapFactory.decodeResource(getResources(), R.drawable.lifea);
            canvas.drawBitmap(heartA, WIDTH/2 - 120, 0, null);
            heartB = BitmapFactory.decodeResource(getResources(), R.drawable.lifeb);
            canvas.drawBitmap(heartB, WIDTH/2 - 80, 0, null);
        }
        if (hearts == 1) {
            heartA = BitmapFactory.decodeResource(getResources(), R.drawable.lifea);
            canvas.drawBitmap(heartA, WIDTH/2 - 120, 0, null);
        }
        if (hearts == 0) {
            hero.setPlaying(false);
            hearts = 3;
        }
        //intro panel
        if (!hero.getPlaying() && newGameCreated && reset) {
            Paint paint1 = new Paint();
            paint1.setTextSize(25);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            myPanel = BitmapFactory.decodeResource(getResources(), R.drawable.panel);
            canvas.drawBitmap(myPanel, WIDTH/2-210, HEIGHT/2-120, null);
            canvas.drawText("PRESS TO START", WIDTH/2-100, HEIGHT/2-70, paint1);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH/2-90, HEIGHT/2-20, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH/2-90, HEIGHT/2+20, paint1);


        }
    }
}