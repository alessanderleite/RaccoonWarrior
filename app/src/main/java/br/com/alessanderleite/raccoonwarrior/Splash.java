package br.com.alessanderleite.raccoonwarrior;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(4000);
                    Intent startGame = new Intent(getApplicationContext(), Game.class);
                    startActivity(startGame);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}
