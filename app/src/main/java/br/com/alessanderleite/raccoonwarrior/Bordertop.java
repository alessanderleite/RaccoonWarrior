package br.com.alessanderleite.raccoonwarrior;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bordertop extends GameObject {

    private Bitmap image;

    public Bordertop(Bitmap res, int x, int y) {
        height = 150;
        width = 20;
        this.x = x;
        this.y = y;
        dx = GamePanel.MOVESPEED;
        image = Bitmap.createBitmap(res, 0, 0, width, height);

    }

    public void update() {
        x += dx;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }
}
