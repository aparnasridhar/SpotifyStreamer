package com.coderbloc.aparnasridhar.spotifystreamer.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coderbloc.aparnasridhar.spotifystreamer.R;
import com.coderbloc.aparnasridhar.spotifystreamer.activity.PlayerActivity;
import com.coderbloc.aparnasridhar.spotifystreamer.adapter.TopTracksAdapter;
import com.coderbloc.aparnasridhar.spotifystreamer.model.ArtistData;
import com.coderbloc.aparnasridhar.spotifystreamer.model.SongList;
import com.coderbloc.aparnasridhar.spotifystreamer.utils.NetworkUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

public class TopTracksFragment extends Fragment {

    TopTracksAdapter adapter;
    private String spotifyID;
    private List<Track> trackList;
    private ProgressBar progressBar;
    private ListView view;
    private boolean mTwoPane;

    public static final String ARTIST_KEY = "ARTIST_KEY";

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

        setRetainInstance(true);

    }


    @Override
    public void onPause() {
        super.onPause();
        progressBar.setVisibility(View.GONE);
    }

    public void onResume(){
        super.onResume();
        if(trackList != null){
            adapter.addAll(trackList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        mTwoPane = getActivity().findViewById(R.id.main_container) != null;
        // Getting intent bundle
        Bundle args = getArguments();
        if (args != null && mTwoPane) {
            ArtistData artistData = args.getParcelable(ARTIST_KEY);
            if(artistData !=null) {
                spotifyID = artistData.getSpotifyId();
            }

        }

        //Setup List View and Adapter
        adapter = new TopTracksAdapter(getActivity(),new ArrayList<Track>());

        view = (ListView) rootView.findViewById(R.id.listview_songlist);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  if (!mTwoPane) {
                    Track trackInfo = adapter.getItem(position);
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }else{
                    DialogFragment playFragment = new PlayerFragment();
                    playFragment.show(getActivity().getSupportFragmentManager(), "dialog");

                }

            }
        });
        view.setAdapter(adapter);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressbar_track);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT) && !mTwoPane){
            spotifyID = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        //Load all tracks in case we pressed back from player fragment
        final SongList tracks = new SongList();
        trackList = tracks.getTopTenSongs();
        if(trackList !=null && !trackList.isEmpty()){
            adapter.clear();
            adapter.addAll(trackList);
            adapter.notifyDataSetChanged();
        }

        final NetworkUtility utils = new NetworkUtility(getActivity());

        if(spotifyID !=null) {
            if (utils.isNetworkAvailable()) {
                try {
                    FetchTopTracksTask task = new FetchTopTracksTask();
                    task.execute(spotifyID);
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

            }
        }

        return rootView;
    }

    public class FetchTopTracksTask extends AsyncTask<String,Void,List<Track>> {

        private final String LOG_TAG = FetchTopTracksTask.class.getSimpleName();
        List<Track> trackList = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Track> doInBackground(String... params) {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            //JSON organized with top level element "artists"
            //Below artists there are list of "items"
            //Each item contains details of one artist

            Map<String, Object> query = new HashMap<String, Object>();

            query.put("country","US");
            try{
                Tracks tracksResult = spotify.getArtistTopTrack(params[0], query);
                SongList list = new SongList();
                trackList = tracksResult.tracks;
                list.setTopTenSongs(trackList);
            } catch(RetrofitError ex) {
                Log.e("Error",ex.toString());
            }

            return trackList;
        }

        @Override
        protected void onPostExecute(List<Track> trackList) {
            progressBar.setVisibility(View.GONE);
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
