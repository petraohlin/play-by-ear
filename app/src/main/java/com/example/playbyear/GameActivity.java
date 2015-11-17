package com.example.playbyear;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class GameActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String[] items = new String[] {"Deportees","First Aid Kit","Robyn","De vet du"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.opt_row, R.id.text1, items);
        setListAdapter(adapter);
    }
}
