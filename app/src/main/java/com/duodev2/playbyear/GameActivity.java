package com.duodev2.playbyear;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GameActivity extends ListActivity {

    private ProgressBar progressBar;
    private int questionNumber = 0;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String[] items = new String[] {"Deportees","First Aid Kit","Robyn","De vet du"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.opt_row, R.id.text1, items);
        setListAdapter(adapter);

        nextButton = (Button) findViewById(R.id.nextBtn);
        nextButton.setVisibility(View.INVISIBLE);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    // list: The ListView where the click happened
    // v: The item that was clicked with the ListView
    // position: The position of the clicked item in the list
    // id: The row ID of the item that was clicked
    protected void onListItemClick(ListView list, View v, int position, long id){
        super.onListItemClick(list, v, position, id);
        v.setSelected(true);
        nextButton.setVisibility(View.VISIBLE);
    }

    // Called when the user clicks the Next Question
    public void nextQuestion(View view) {

        questionNumber = questionNumber + 10;
        progressBar.setProgress(questionNumber);
        nextButton.setVisibility(View.INVISIBLE);
    }
}
