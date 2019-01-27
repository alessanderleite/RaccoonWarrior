package br.com.alessanderleite.raccoonwarrior;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Hero extends GameObject {

    private Bitmap spritesheet;
    private int score;
    private double dya;
    private boolean up;
    private boolean playing;

    private Animation animation = new Animation();
    private long startTime;

    public Hero(Bitmap res, int w, int h, int numFrames) {
        x = 100;
        y = GamePanel.HEIGHT / 2;
        dy = 0;
        score = 0;
        height = h;
        width = w;

        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;

        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(10);

        startTime = System.nanoTime();
    }

    public void update() {}

    public void draw(Canvas canvas) {}
}
