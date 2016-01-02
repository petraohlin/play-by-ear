package com.duodev2.playbyear;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.duodev2.playbyear.R;

public class EndActivity extends AppCompatActivity {

    private TextView scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        Intent intent = getIntent();
        int score = intent.getIntExtra("score", 0);
        scoreText = (TextView) findViewById(R.id.txtScore);
        scoreText.setText(Integer.toString(score) + "/10 points");

        //Get font awesome for the restart button
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        Button button = (Button) findViewById( R.id.restartBtn);
        button.setTypeface(font);
    }

    private void setButtonClickListener(){
        Button toggleButton = (Button) findViewById(R.id.restartBtn);

        //Starts the game activity
        toggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("hej");
            }
        });


    }

}
