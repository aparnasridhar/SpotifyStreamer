package com.coderbloc.aparnasridhar.spotifystreamer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.coderbloc.aparnasridhar.spotifystreamer.R;
import com.coderbloc.aparnasridhar.spotifystreamer.fragment.ArtistSearchFragment;
import com.coderbloc.aparnasridhar.spotifystreamer.fragment.TopTracksFragment;
import com.coderbloc.aparnasridhar.spotifystreamer.model.ArtistData;


public class MainActivity extends ActionBarActivity implements ArtistSearchFragment.Callback{

    private boolean mTwoPane = false;

    @Override
    public void onItemSelected(ArtistData artistData) {
        // In single-pane mode, add the trackFragment to the container
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(TopTracksFragment.ARTIST_KEY, artistData);

            TopTracksFragment fragment = new TopTracksFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.topTracksContainer, fragment)
                    .commit();
            // In two-pane mode, start TrackActivity
        } else {
            Intent intent = new Intent(this, TopTracksActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, artistData.getSpotifyId());
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.title);

        if(findViewById(R.id.main_container) !=null){

            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.topTracksContainer, new TopTracksFragment())
                        .commit();
            }
        } else{
            mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
