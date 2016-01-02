package com.duodev2.playbyear;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.PlayConfig;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GameActivity extends ListActivity  implements PlayerNotificationCallback, ConnectionStateCallback {


    private static final String CLIENT_ID = "acd9bb6058c849229c6952880af3b558";
    private static final String REDIRECT_URI = "playbyearprotocol://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;
    private PlayConfig mPlayConfig;
    private MusicItem correctMusicItem;
    private int score = 0;

    private MusicDbHelper db;
    private ProgressBar progressBar;
    private int questionNumber = 0;
    private Button nextButton;
    private List<String> alternatives = new LinkedList<String>();
    private ArrayList<Integer> indexes = new ArrayList<Integer>();
    private ArrayList<Integer> usedIndexes = new ArrayList<Integer>();
    private TextView scoreText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Request code that will be used to verify if the result comes from correct activity
        // Can be any integer
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);


        db = new MusicDbHelper(this);

        // add default items
        db.addMusicItem(new MusicItem("Hello Saferide", "My Best Friend", "Swedish Pop", "spotify:track:2WYUZcrkZuyXGgKE05UhEC"));
        db.addMusicItem(new MusicItem("Miss Li", "Dancing the Whole Way Home", "Swedish Pop", "spotify:track:67tmclJQToXoCjiDCm43Jg"));
        db.addMusicItem(new MusicItem("Marit Bergman", "This Is The Year", "Swedish Pop", "spotify:track:6rBzSjdrihboUkZ0YyhWLc"));
        db.addMusicItem(new MusicItem("Maia Hirasawa", "I Found This Boy", "Swedish Pop", "spotify:track:2unK0jqfq1VvN1J4FxB3CL"));
        db.addMusicItem(new MusicItem("Robyn", "Dancing On My Own", "Swedish Pop", "spotify:track:4g6AXLnnxNDp1D7VWRZXRs"));
        db.addMusicItem(new MusicItem("Veronica Maggio", "Snälla bli min", "Swedish Pop", "spotify:track:2jEPQKvz7dh1pfpRyq6G1C"));
        db.addMusicItem(new MusicItem("Veronica Maggio", "17 år", "Swedish Pop", "spotify:track:7MzmBmyI9KkyQJaPNLdtUi"));
        db.addMusicItem(new MusicItem("Oskar Linnros", "25", "Swedish Pop", "spotify:track:4UtF1MAeEfY4StWJsXO3Q1"));
        db.addMusicItem(new MusicItem("Little Jinder", "Vita Bergens klockor", "Swedish Pop", "spotify:track:07aAs90AMhw3schBMFN4vc"));
        db.addMusicItem(new MusicItem("Laleh", "Colors", "Swedish Pop", "spotify:track:0I0flDWPoUqDmCIX90X2I8"));
        db.addMusicItem(new MusicItem("Melissa Horn", "Du går nu", "Swedish Pop", "spotify:track:3k2gBhO3Dr7dZiFWCOKTWb"));
        db.addMusicItem(new MusicItem("Elin Ruth", "Love", "Swedish Pop", "spotify:track:7Dm7yCuOIOW81wgGcCXdlX"));
        db.addMusicItem(new MusicItem("Asha Ali", "Fire, fire", "Swedish Pop", "spotify:track:2k8Y82t7nDXpWq9mcVx7pU"));
        db.addMusicItem(new MusicItem("Håkan Hellström", "En midsommarnattsdröm", "Swedish Pop", "spotify:track:2PD70CPXPOsnTxJdoaaN95"));
        db.addMusicItem(new MusicItem("Markus Krunegård", "Du stör dig hårt på mig", "Swedish Pop", "spotify:track:1DAshXYxxLHC6otfko4Djs"));


        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        nextButton = (Button) findViewById(R.id.nextBtn);
        nextButton.setVisibility(View.INVISIBLE);
        scoreText = (TextView) findViewById(R.id.txtScore);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer = player;
                        mPlayer.addConnectionStateCallback(GameActivity.this);
                        mPlayer.addPlayerNotificationCallback(GameActivity.this);
                        loadNextQuestion();

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("GameActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }


    @Override
    // list: The ListView where the click happened
    // v: The item that was clicked with the ListView
    // position: The position of the clicked item in the list
    // id: The row ID of the item that was clicked
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        nextButton.setVisibility(View.VISIBLE);

        Random rn = new Random();

        String opt = alternatives.get(position);
        String rightOpt = correctMusicItem.getSong();
        //opt.equals(rightOpt)
        if(true) {
            toggleListView(l, v);
            score++;
            scoreText.setText(Integer.toString(score));
        }
        else {
            toggleListView(l, v);
        }

        System.out.println(score);
        if(questionNumber == 9)
        {
            // Pause the player for now TODO: Flush the player
            mPlayer.pause();
            Intent intent = new Intent(v.getContext(), EndActivity.class);
            intent.putExtra("score", score);
            startActivity(intent);
        }
    }

    //Help function to change all states on the listview and item
    public void toggleListView(ListView l, View v) {
        l.setEnabled(!l.isEnabled());
        v.setSelected(!v.isSelected());
        v.setPressed(!v.isPressed());
    }


    // Called when the user clicks the Next Question
    public void nextQuestion(View view) {

        loadNextQuestion();

        questionNumber++;
        progressBar.setProgress(questionNumber);
        nextButton.setVisibility(View.INVISIBLE);
        this.getListView().setEnabled(true);
    }


    private void loadNextQuestion() {

        alternatives.clear();
        indexes.clear();

        List<String> list = db.getAllSongs();
        for (int i=1; i<list.size()+1; i++) {
            indexes.add(i);
        }

        // Remove already used questions
        indexes.removeAll(usedIndexes);

        Collections.shuffle(indexes);

        for (int i=0; i<4; i++) {
            alternatives.add(db.getMusicItem(indexes.get(i)).getSong());
        }

        Collections.shuffle(alternatives);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.opt_row, R.id.text1, alternatives);
        setListAdapter(adapter);


        //Get current answer
        usedIndexes.add(indexes.get(0));
        correctMusicItem = db.getMusicItem(indexes.get(0));
        mPlayer.play(correctMusicItem.getUri());

    }



    @Override
    public void onLoggedIn() {
        Log.d("GameActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("GameActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("GameActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("GameActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("GameActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("GameActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("GameActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

}
