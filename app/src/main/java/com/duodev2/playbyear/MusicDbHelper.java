package com.duodev2.playbyear;

/**
 * Created by petraohlin8 on 2015-11-24.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class MusicDbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "MusicDb";

    // MusicItem table name
    private static final String TABLE_MUSIC = "music";

    // MusicItem Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_SONG = "song";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_URI = "uri";


    private static final String[] COLUMNS = {KEY_ID,KEY_ARTIST,KEY_SONG, KEY_CATEGORY, KEY_URI};

    public MusicDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_MUSIC_TABLE = "CREATE TABLE music ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "artist TEXT, "+
                "song TEXT, " +
                "category TEXT, " +
                "uri TEXT )";

        // create books table
        db.execSQL(CREATE_MUSIC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS music");

        // create fresh books table
        this.onCreate(db);
    }


    /**
     * CRUD operations (create "add", read "get", update, delete) MusicItem + get all music + delete all music
     */

    public void addMusicItem(MusicItem item){
        //for logging
        Log.d("addMusicItem", item.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ARTIST, item.getArtist());
        values.put(KEY_SONG, item.getSong());
        values.put(KEY_CATEGORY, item.getCategory());
        values.put(KEY_URI, item.getUri());

        // 3. insert
        db.insert(TABLE_MUSIC, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public MusicItem getMusicItem(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_MUSIC, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        MusicItem item = new MusicItem();
        item.setId(Integer.parseInt(cursor.getString(0)));
        item.setArtist(cursor.getString(1));
        item.setSong(cursor.getString(2));
        item.setCategory(cursor.getString(3));
        item.setUri(cursor.getString(4));

        //log
        Log.d("getMusicItem(" + id + ")", item.toString());

        // 5. return item
        return item;
    }

    public List<MusicItem> getAllMusic() {
        List<MusicItem> music = new LinkedList<MusicItem>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_MUSIC;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build MusicItem and add it to list
        MusicItem item = null;
        if (cursor.moveToFirst()) {
            do {
                item = new MusicItem();
                item.setId(Integer.parseInt(cursor.getString(0)));
                item.setArtist(cursor.getString(1));
                item.setSong(cursor.getString(2));
                item.setCategory(cursor.getString(3));
                item.setUri(cursor.getString(4));

                // Add MusicItem to music
                music.add(item);
            } while (cursor.moveToNext());
        }

        Log.d("getAllMusic()", music.toString());

        return music;
    }

    public List<String> getAllArtists() {
        List<String> artists = new LinkedList<String>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_MUSIC;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row and add artist to list
        if (cursor.moveToFirst()) {
            do {
                // Add artist to artists
                artists.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        Log.d("getAllArtists()", artists.toString());

        return artists;
    }

    public List<String> getAllSongs() {
        List<String> songs = new LinkedList<String>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_MUSIC;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row and add song to list
        if (cursor.moveToFirst()) {
            do {
                // Add song to songs
                songs.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }

        Log.d("getAllSongs()", songs.toString());

        return songs;
    }
    public int updateMusicItem(MusicItem item) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ARTIST, item.getArtist());
        values.put(KEY_SONG, item.getSong());
        values.put(KEY_CATEGORY, item.getCategory());
        values.put(KEY_URI, item.getUri());

        // 3. updating row
        int i = db.update(TABLE_MUSIC, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(item.getId())}); //selection args

        // 4. close
        db.close();

        return i;

    }

    public void deleteMusicItem(MusicItem item) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_MUSIC, //table name
                KEY_ID + " = ?",  // selections
                new String[]{String.valueOf(item.getId())}); //selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteMusicItem", item.toString());

    }

    public Boolean isEmpty(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM music", null);

        return mCursor.getCount() == 0;
    }
}