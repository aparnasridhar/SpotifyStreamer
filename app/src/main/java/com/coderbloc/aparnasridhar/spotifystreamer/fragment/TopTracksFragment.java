package com.coderbloc.aparnasridhar.spotifystreamer.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.coderbloc.aparnasridhar.spotifystreamer.R;
import com.coderbloc.aparnasridhar.spotifystreamer.activity.PlayerActivity;
import com.coderbloc.aparnasridhar.spotifystreamer.adapter.TopTracksAdapter;
import com.coderbloc.aparnasridhar.spotifystreamer.model.SongList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TopTracksFragment extends Fragment {

    TopTracksAdapter adapter;
    private String spotifyID;
    FetchTopTracksTask task;
    private List<Track> trackList;

    private SongList tracks;

    public TopTracksFragment(){

    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        adapter = new TopTracksAdapter(getActivity(),new ArrayList<Track>());
        ListView view = (ListView) rootView.findViewById(R.id.listview_songlist);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), adapter.getItem(position).name, Toast.LENGTH_SHORT).show();
                Track trackInfo = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);

            }
        });
        view.setAdapter(adapter);
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            spotifyID = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        task = new FetchTopTracksTask();
        task.execute(spotifyID);
        return rootView;
    }

    public class FetchTopTracksTask extends AsyncTask<String,Void,List<Track>> {

        private final String LOG_TAG = FetchTopTracksTask.class.getSimpleName();
        List<Track> trackList = null;

        @Override
        protected List<Track> doInBackground(String... params) {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            //JSON organized with top level element "artists"
            //Below artists there are list of "items"
            //Each item contains details of one artist

            Map<String, Object> query = new HashMap<String, Object>();


            query.put("country","US");
            Tracks tracksResult = spotify.getArtistTopTrack(params[0], query);
            trackList = tracksResult.tracks;

            SongList list = new SongList();
            list.setTopTenSongs(trackList);

            return trackList;
        }



        @Override
        protected void onPostExecute(List<Track> trackList) {
            if(trackList !=null){
                adapter.clear();
                if(trackList == null || trackList.isEmpty()){
                    Toast.makeText(getActivity(),getResources().getString(R.string.no_track_error),Toast.LENGTH_SHORT).show();
                } else {
                   adapter.addAll(trackList);
                }
            } else {
                Toast.makeText(getActivity(),getResources().getString(R.string.no_track_error),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
