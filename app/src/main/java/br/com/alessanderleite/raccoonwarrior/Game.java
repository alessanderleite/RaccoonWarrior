package br.com.alessanderleite.raccoonwarrior;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class Game extends AppCompatActivity {

    GamePanel gameView;
    FrameLayout game;
    RelativeLayout GameButtons;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GamePanel(this);
        game = new FrameLayout(this);
        GameButtons = new RelativeLayout(this);

        //first button
        Button butOne = new Button(this);
        butOne.setText("Button1");
        butOne.setId(123456);

        //second button
        Button butTwo = new Button(this);
        butTwo.setText("Button2");
        butTwo.setId(789111);

        //Define the layout prarmeter for the button to wrap the content for both width and height
        RelativeLayout.LayoutParams b1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams b2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        GameButtons.setLayoutParams(params);

        //add buttons to Relative layout
        GameButtons.addView(butOne);
        GameButtons.addView(butTwo);

        //button 1 position in the Relative Layout
        b1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        b1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        butOne.setLayoutParams(b1);

        //button 2 position in the Relative Layout
        b2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        b2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        butTwo.setLayoutParams(b2);

        //no title
        getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        game.addView(gameView);
        game.addView(GameButtons);
        setContentView(game);
    }
}
