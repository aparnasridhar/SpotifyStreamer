package com.coderbloc.aparnasridhar.spotifystreamer.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coderbloc.aparnasridhar.spotifystreamer.R;
import com.coderbloc.aparnasridhar.spotifystreamer.adapter.ArtistArrayAdapter;
import com.coderbloc.aparnasridhar.spotifystreamer.model.ArtistData;
import com.coderbloc.aparnasridhar.spotifystreamer.utils.NetworkUtility;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.RetrofitError;

public class ArtistSearchFragment extends Fragment {
    ArtistArrayAdapter adapter;
    private String searchKeyword = "";
    private ProgressDialog mProgressDialog;
    private List<Artist> artistList = null;
    private ProgressBar progressBar;
    private int mPosition;

    public interface Callback{
        /**
         * Callback for when an item has been selected
         * @param artistData
         */
        public void onItemSelected(ArtistData artistData);
    }
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

        //Please also note that my Android manifest has configChanges="orientation|screenSize"
        //So the activity/fragment isn't recreated on orientation change

    }

    @Override
    public void onPause() {
        super.onPause();
        progressBar.setVisibility(View.GONE);
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
                ArtistData artistData = new ArtistData(adapter.getItem(position));
                ((Callback)getActivity()).onItemSelected(artistData);
                mPosition = position;

            }
        });

        //Set the adapter for the list
        view.setAdapter(adapter);

        final NetworkUtility utils = new NetworkUtility(getActivity());

        final SearchView searchText = (SearchView) rootView.findViewById(R.id.searchText);

        searchText.setIconifiedByDefault(false);
        searchText.setQueryHint(getResources().getString(R.string.artist_search_hint));
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                    searchKeyword = searchText.getQuery().toString();
                    if(utils.isNetworkAvailable()) {
                            FetchArtistTask task= new FetchArtistTask();
                            task.execute(searchText.getQuery().toString());
                       }else {
                        Toast.makeText(getActivity(),getResources().getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
                    }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        progressBar = (ProgressBar) rootView.findViewById(R.id.progressbar_artist);
        return rootView;
    }

    //Prefer using Async Task instead of the Spotify Callback Wrapper
    //Reason is because I need to update the UI thread once the results are obtained
    //Async Task maintains thread pools better to handle UI updates in onPostExecute
    //Doing it by ourselves using a Handler or runOnUIThread seems in efficient
    public class FetchArtistTask extends AsyncTask<String,Void,List<Artist>> {

        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();
        private List<Artist> artistList = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Artist> doInBackground(String... params) {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            //JSON organized with top level element "artists"
            //Below artists there are list of "items"
            //Each item contains details of one artist
            try {
                ArtistsPager searchResults = spotify.searchArtists(params[0]);
                Pager<Artist> artists = searchResults.artists;
                artistList = artists.items;
            }catch(RetrofitError ex){
                Toast.makeText(getActivity(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }

            return artistList;
        }

        @Override
        protected void onPostExecute(List<Artist> artistList) {
            progressBar.setVisibility(View.GONE);
            if (artistList != null) {
                adapter.clear();
                if (artistList.isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_artists_error), Toast.LENGTH_SHORT).show();
                } else {
                    adapter.addAll(artistList);
                }
            } else {
                Toast.makeText(getActivity(),getResources().getString(R.string.no_artists_error),Toast.LENGTH_SHORT).show();
            }
        }
    }
}

