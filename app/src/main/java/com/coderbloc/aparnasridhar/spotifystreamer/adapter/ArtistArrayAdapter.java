package com.coderbloc.aparnasridhar.spotifystreamer.adapter;

import android.app.Activity;
import android.content.Context;
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

        LayoutInflater inflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // A holder will hold the references
        // to your views.
        ViewHolder holder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.artist_list_item, parent, false);
            holder = new ViewHolder();
            holder.artistName = (TextView) convertView.findViewById(R.id.artist_name);
            holder.artistImage = (ImageView) convertView.findViewById(R.id.artist_image);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Artist artist = getItem(position);
        holder.artistName.setText(artist.name);
        String imageUrl = null;

        if(artist.images.size() > 0){
            Image image = artist.images.get(artist.images.size() - 2);
            imageUrl = image.url;
            if(imageUrl != null) {
                Picasso.with(mActivity)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher)
                        .into(holder.artistImage);
            }
        }


        return convertView;
    }

    class ViewHolder {
        TextView artistName;
        ImageView artistImage;
    }
}
