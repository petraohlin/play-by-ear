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

    private MusicDbHelper db;
    private List<String> songs;
    private ProgressBar progressBar;
    private int questionNumber = 0;
    private Button nextButton;

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
                        //mPlayer.play("spotify:user:spotifydiscover:playlist:40CisY8BXopJP0FhxOnrl7");
                        //mPlayConfig = mPlayConfig.createFor("spotify:user:spotifydiscover:playlist:40CisY8BXopJP0FhxOnrl7");

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
        if(rn.nextInt(2) == 1)
            toggleListView(l, v);
        else {
            toggleListView(l, v);
        }

        if(questionNumber == 9)
        {
            Intent intent = new Intent(v.getContext(), EndActivity.class);
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

        loadNextQuestion(songs);

        questionNumber++;
        progressBar.setProgress(questionNumber);
        nextButton.setVisibility(View.INVISIBLE);
        this.getListView().setEnabled(true);
    }

    public void loadNextQuestion(List<String> list) {
        List<String> alternatives = new LinkedList<String>();
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for (int i=1; i<list.size(); i++) {
            indexes.add(new Integer(i));
        }
        Collections.shuffle(indexes);
        for (int i=0; i<4; i++) {
            alternatives.add(list.get(indexes.get(i)));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.opt_row, R.id.text1, alternatives);
        setListAdapter(adapter);
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
