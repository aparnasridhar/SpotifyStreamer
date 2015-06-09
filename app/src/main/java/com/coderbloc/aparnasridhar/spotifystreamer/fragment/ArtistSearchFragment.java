package com.coderbloc.aparnasridhar.spotifystreamer.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coderbloc.aparnasridhar.spotifystreamer.R;
import com.coderbloc.aparnasridhar.spotifystreamer.activity.TopTracksActivity;
import com.coderbloc.aparnasridhar.spotifystreamer.adapter.ArtistArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;

public class ArtistSearchFragment extends Fragment {
    ArtistArrayAdapter adapter;
    private String searchKeyword = "";

    public ArtistSearchFragment() {
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

        //inflate the view for the artist search
        final View rootView = inflater.inflate(R.layout.fragment_artist_list, container, false);

        //Initialize the adapter for search results
        adapter = new ArtistArrayAdapter(getActivity(), new ArrayList<Artist>());

        //Get the list view and handle clicks
        ListView view = (ListView) rootView.findViewById(R.id.listview_songlist);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get the Artist's spotify ID to fetch topTracks and launch the intent
                String spotifyID = adapter.getItem(position).id;
                Intent intent = new Intent(getActivity(), TopTracksActivity.class).putExtra(Intent.EXTRA_TEXT,spotifyID);
                startActivity(intent);
            }
        });

        //Get the search text
        final EditText searchText = (EditText)rootView.findViewById(R.id.searchText);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_NULL
                        && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    searchKeyword = searchText.getText().toString();
                    if(isNetworkAvailable()) {
                        FetchArtistTask task = new FetchArtistTask();
                        task.execute(searchKeyword);
                    }else {
                        Toast.makeText(getActivity(),getResources().getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }
        });


        //Set the adapter for the list
        view.setAdapter(adapter);

        return rootView;
    }

    //Based on a stackoverflow snippet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class FetchArtistTask extends AsyncTask<String,Void,List<Artist>>{

        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();
        private List<Artist> artistList = null;

        @Override
        protected List<Artist> doInBackground(String... params) {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            //JSON organized with top level element "artists"
            //Below artists there are list of "items"
            //Each item contains details of one artist

            ArtistsPager searchResults = spotify.searchArtists(params[0]);
            Pager<Artist> artists = searchResults.artists;
            artistList = artists.items;

            return artistList;
        }



        @Override
        protected void onPostExecute(List<Artist> artistList) {
            if(artistList !=null){
                adapter.clear();
                if(artistList.isEmpty()){
                    Toast.makeText(getActivity(),getResources().getString(R.string.no_artists_error),Toast.LENGTH_SHORT).show();
                } else {
                    //This should also call notifyDataSetChanged to update the list
                    adapter.addAll(artistList);
                }
            } else {
                Toast.makeText(getActivity(),getResources().getString(R.string.no_artists_error),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
