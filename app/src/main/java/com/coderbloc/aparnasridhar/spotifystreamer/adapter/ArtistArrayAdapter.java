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

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by aparnasridhar on 6/4/15.
 */
public class ArtistArrayAdapter extends ArrayAdapter<Artist>{

    private Activity mActivity;

    public ArtistArrayAdapter(Activity activity, List<Artist> artists){

        super(activity,0,artists);
        this.mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_list_item, parent, false);
        }
        final Artist artist = getItem(position);

        TextView artistName = (TextView) convertView.findViewById(R.id.artist_name);
        artistName.setText(artist.name);

        ImageView artistImage = (ImageView) convertView.findViewById(R.id.artist_image);
        String imageUrl = null;
        if(artist.images.size() > 0){
            Image image = artist.images.get(artist.images.size() - 2);
            imageUrl = image.url;
            Picasso.with(mActivity).load(imageUrl).placeholder(R.drawable.ic_launcher).into(artistImage);
        }

        return convertView;
    }
}
