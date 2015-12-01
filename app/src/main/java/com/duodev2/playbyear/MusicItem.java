package com.duodev2.playbyear;

/**
 * Created by petraohlin8 on 2015-11-24.
 */
public class MusicItem {

    private int id;
    private String artist;
    private String song;
    private String category;
    private String uri;


    public MusicItem(){}

    public MusicItem(String artist, String song, String category, String uri) {
        super();
        this.artist = artist;
        this.song = song;
        this.category = category;
        this.uri = uri;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getCategory() {
        return category;
    }

    public String getSong() {
        return song;
    }

    public String getUri() {
        return uri;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "id=" + id + ", artist=" + artist + ", song" + song;
    }
}