package com.coderbloc.aparnasridhar.spotifystreamer.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coderbloc.aparnasridhar.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by aparnasridhar on 6/4/15.
 */
public class TopTracksAdapter extends ArrayAdapter<Track> {
    private Activity mActivity;

    public TopTracksAdapter(Activity activity, List<Track> tracks){

        super(activity,0,tracks);
        this.mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_list_item, parent, false);
        }

        final Track track= getItem(position);

        TextView trackName = (TextView) convertView.findViewById(R.id.artist_name);
        trackName.setText(track.name + "\n" + track.album.name);

        ImageView artistImage = (ImageView) convertView.findViewById(R.id.artist_image);
        String url = track.album.images.get(track.album.images.size() - 2).url;
        Picasso.with(getContext()).load(url).placeholder(R.drawable.ic_launcher).into(artistImage);

        return convertView;
    }
}
