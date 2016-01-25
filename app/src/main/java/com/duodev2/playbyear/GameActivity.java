package com.duodev2.playbyear;

import android.animation.ObjectAnimator;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Timer;
import java.util.TimerTask;

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
    private ArrayList<MusicItem> alternatives = new ArrayList<MusicItem>();
    private ArrayList<Integer> indexes = new ArrayList<Integer>();
    private ArrayList<Integer> usedIndexes = new ArrayList<Integer>();
    private TextView scoreText;
    private TextView questionText;


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

        Boolean isEmpty = db.isEmpty();

        if (isEmpty){
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
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        questionText = (TextView) findViewById(R.id.question);

        nextButton = (Button) findViewById(R.id.nextBtn);
        nextButton.setVisibility(View.INVISIBLE);
        scoreText = (TextView) findViewById(R.id.txtScore);
        scoreText.setText(Integer.toString(score).concat(" ".concat(getResources().getString(R.string.icon_music))));
        ImageView vinyl = (ImageView) findViewById(R.id.musicImage);
        runAnimation(vinyl, R.anim.scale);

        //Get font awesome for the restart button
        Typeface awesomeFont = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        nextButton.setTypeface(awesomeFont);
        scoreText.setTypeface(awesomeFont);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        mPlayer.pause();
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
    // l: The ListView where the click happened
    // v: The item that was clicked with the ListView
    // position: The position of the clicked item in the list
    // id: The row ID of the item that was clicked
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ImageView vinyl = (ImageView) findViewById(R.id.musicImage);
        nextButton.setVisibility(View.VISIBLE);
        TextView txt = (TextView) v.findViewById(R.id.songName);

        //String for correct alternative and choosen alternative
        String opt = alternatives.get(position).getSong();
        String rightOpt = correctMusicItem.getSong();
        String altText = txt.getText().toString();
        String newText;

        int rightPos = alternatives.indexOf(correctMusicItem);

        //Go into pushed state
        listPushedState(txt, rightOpt, l, opt.equals(rightOpt), v);

        //The game has come to an end
        if(questionNumber == 9)
        {
            // Pause the player for now
            mPlayer.pause();
            Intent intent = new Intent(v.getContext(), EndActivity.class);
            intent.putExtra("score", score);
            vinyl.clearAnimation();
            startActivity(intent);

        }
    }

    public void listPushedState(TextView txtSelected, String rightOpt, ListView l, Boolean sucess, View v)  {

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        v.setBackgroundResource(R.color.pressed);

        //Make all alternatives transparent and in case of wrong answer, show the right answer
        for(int i = 0; i < l.getChildCount(); i++){
            TextView songTxt = (TextView) l.getChildAt(i).findViewById(R.id.songName);
            TextView indicationTxt = (TextView) l.getChildAt(i).findViewById(R.id.indication);
            songTxt.setTextColor(getResources().getColor(R.color.transparent));

            if(!sucess && songTxt.getText().equals(rightOpt)) {
                songTxt.setTextColor(getResources().getColor(R.color.green));
                indicationTxt.setTypeface(font);
                indicationTxt.setText(getResources().getString(R.string.icon_arrow_left));
            }
        }

        TextView indicationTxt = (TextView) v.findViewById(R.id.indication);
        indicationTxt.setTypeface(font);

        //Set chosen alternative
        if(sucess) {
            txtSelected.setTextColor(getResources().getColor(R.color.green));
            indicationTxt.setText(getResources().getString(R.string.icon_check));
            indicationTxt.setTextColor(getResources().getColor(R.color.green));
            score++;
            scoreText.setText(Integer.toString(score).concat(" ".concat(getResources().getString(R.string.icon_music))));
            runAnimation(scoreText, R.anim.scaleonce);
        } else {
            /*txtSelected.setTextColor(getResources().getColor(R.color.red));
            indicationTxt.setText(getResources().getString(R.string.icon_wrong));
            indicationTxt.setTextColor(getResources().getColor(R.color.red));*/
            listTimeoutState(txtSelected, rightOpt, l, v);
        }

        //Disable to ability to choose alternative
        l.setEnabled(!l.isEnabled());
    }

    public void listTimeoutState(TextView txtSelected, String rightOpt, ListView l, View v)  {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        //Make all alternatives transparent
        for(int i = 0; i < l.getChildCount(); i++){
            TextView songTxt = (TextView) l.getChildAt(i).findViewById(R.id.songName);
            TextView iconTxt = (TextView) l.getChildAt(i).findViewById(R.id.indication);
            songTxt.setTextColor(getResources().getColor(R.color.transparent));

            if(songTxt.getText().equals(rightOpt)) {
                songTxt.setTextColor(getResources().getColor(R.color.green));
                iconTxt.setTypeface(font);
                iconTxt.setText(getResources().getString(R.string.icon_arrow_left));
            }
            else {
                songTxt.setTextColor(getResources().getColor(R.color.red));
                iconTxt.setTypeface(font);
                iconTxt.setText(getResources().getString(R.string.icon_wrong));
                iconTxt.setTextColor(getResources().getColor(R.color.red));
            }
        }

        //Disable to ability to choose alternative
        l.setEnabled(!l.isEnabled());
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
            alternatives.add(db.getMusicItem(indexes.get(i)));
        }

        Collections.shuffle(alternatives);

        // Create the adapter to convert the array to views
        MusicsAdapter adapter = new MusicsAdapter(this, alternatives);

        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);


        //Get current answer
        usedIndexes.add(indexes.get(0));
        correctMusicItem = db.getMusicItem(indexes.get(0));
        mPlayer.play(correctMusicItem.getUri());

        //Set questionText
        questionText.setText("Hey, what's the title?");

        //Progressbar
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.circularProgress);
        ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 100);
        animation.setDuration (900000); //in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println(" let the virtual dog out ");
            }
        }, 45000);

    }

    private void runAnimation(View v, int id){
        Animation a = AnimationUtils.loadAnimation(this, id);
        v.clearAnimation();
        v.startAnimation(a);
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
