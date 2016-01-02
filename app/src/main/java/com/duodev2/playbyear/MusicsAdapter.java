package com.duodev2.playbyear;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mattiaspalmgren on 2016-01-02.
 */

public class MusicsAdapter extends ArrayAdapter<MusicItem> {
    public MusicsAdapter(Context context, ArrayList<MusicItem> musics) {
        super(context, 0, musics);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        MusicItem musicItm = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_music, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.songName);

        // Populate the data into the template view using the data object
        tvName.setText(musicItm.getSong());

        // Return the completed view to render on screen
        return convertView;
    }
}