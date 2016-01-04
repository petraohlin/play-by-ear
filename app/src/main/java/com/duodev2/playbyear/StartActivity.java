package com.duodev2.playbyear;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


public class StartActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setButtonClickListener();

        //Get font awesome for the start button
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        Button button = (Button) findViewById( R.id.startBtn);
        button.setTypeface(font);


    }

    private void setButtonClickListener(){
        Button toggleButton = (Button) findViewById(R.id.startBtn);

        //Starts the game acticity
        toggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GameActivity.class);
                startActivity(intent);
            }
        });
    }


}
