package com.coderbloc.aparnasridhar.spotifystreamer.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.coderbloc.aparnasridhar.spotifystreamer.R;
import com.coderbloc.aparnasridhar.spotifystreamer.fragment.TopTracksFragment;

public class TopTracksActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.topTracksContainer, new TopTracksFragment())
                .commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_tracks, menu);
        return true;
    }
}
