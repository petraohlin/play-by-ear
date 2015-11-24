package com.duodev2.playbyear;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class GameActivity extends ListActivity {

    private MusicDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        db = new MusicDbHelper(this);

        // add default items
        db.addMusicItem(new MusicItem("Hello Saferide", "My Best Friend", "Swedish Pop", "spotify:track:2WYUZcrkZuyXGgKE05UhEC"));
        db.addMusicItem(new MusicItem("Miss Li", "Dancing the Whole Way Home", "Swedish Pop", "spotify:track:67tmclJQToXoCjiDCm43Jg"));
        db.addMusicItem(new MusicItem("Marit Bergman", "This Is The Year", "Swedish Pop", "spotify:track:6rBzSjdrihboUkZ0YyhWLc"));
        db.addMusicItem(new MusicItem("Maia Hirasawa", "I Found This Boy", "Swedish Pop", "spotify:track:2unK0jqfq1VvN1J4FxB3CL"));


        // get all music
        List<String> songs = db.getAllSongs();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.opt_row, R.id.text1, songs);
        setListAdapter(adapter);

    }

    @Override
    // list: The ListView where the click happened
    // v: The item that was clicked with the ListView
    // position: The position of the clicked item in the list
    // id: The row ID of the item that was clicked
    protected void onListItemClick(ListView list, View v, int position, long id){
        super.onListItemClick(list,v,position,id);
        v.setSelected(true);
    }
}
