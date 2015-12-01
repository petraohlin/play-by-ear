package com.duodev2.playbyear;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GameActivity extends ListActivity {

    private MusicDbHelper db;
    private List<String> songs;
    private ProgressBar progressBar;
    private int questionNumber = 0;
    private Button nextButton;

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
        db.addMusicItem(new MusicItem("Robyn", "Dancing On My Own", "Swedish Pop", "spotify:track:4g6AXLnnxNDp1D7VWRZXRs"));
        db.addMusicItem(new MusicItem("Petra Öhlin", "My Own Song", "Swedish Pop", "spotify:track:2WYUZcrkZuyXGgKE05UhEC"));
        db.addMusicItem(new MusicItem("Mattias Palmgren", "Kill People, Burn Shit, Fuck School", "Swedish Pop", "spotify:track:2WYUZcrkZuyXGgKE05UhEC"));


        // get all music
        songs = db.getAllSongs();

        loadNextQuestion(songs);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        nextButton = (Button) findViewById(R.id.nextBtn);
        nextButton.setVisibility(View.INVISIBLE);
    }

    @Override
    // list: The ListView where the click happened
    // v: The item that was clicked with the ListView
    // position: The position of the clicked item in the list
    // id: The row ID of the item that was clicked
    protected void onListItemClick(ListView list, View v, int position, long id) {
        super.onListItemClick(list, v, position, id);

        nextButton.setVisibility(View.VISIBLE);

        Random rn = new Random();
        if(rn.nextInt(2) == 1) {
            v.setPressed(false);
            v.setSelected(true);
            list.setEnabled(false);
        }
        else {
            v.setSelected(false);
            v.setPressed(true);
            list.setEnabled(false);
        }

        if(questionNumber == 9)
        {
            Intent intent = new Intent(v.getContext(), EndActivity.class);
            startActivity(intent);
        }
    }

    // Called when the user clicks the Next Question
    public void nextQuestion(View view) {

        loadNextQuestion(songs);

        questionNumber++;
        progressBar.setProgress(questionNumber);
        nextButton.setVisibility(View.INVISIBLE);
    }

    public void loadNextQuestion(List<String> list) {
        List<String> alternatives = new LinkedList<String>();

        Random rn = new Random();
        for(int i =0; i < 4; i++)
        {
            int index = rn.nextInt(list.size());
            alternatives.add(list.get(index));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.opt_row, R.id.text1, alternatives);
        setListAdapter(adapter);
    }
}
