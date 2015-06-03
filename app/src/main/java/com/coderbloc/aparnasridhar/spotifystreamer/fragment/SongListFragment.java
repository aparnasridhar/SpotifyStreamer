package com.coderbloc.aparnasridhar.spotifystreamer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.coderbloc.aparnasridhar.spotifystreamer.R;

public class SongListFragment extends Fragment {
    ArrayAdapter<String> adapter;

    public SongListFragment() {
    }

    @Override
    public void onStart()
    {
        super.onStart();
        updateSongList();
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

    private void updateSongList()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] songArray = {
               "Summer of 69",
                "18 Till I Die",
                "Have you ever really loved a woman?",
                "Night to Remember",
                "On a day like Today"

        };
        adapter = new ArrayAdapter<String>
                (
                        //Context
                        getActivity(),
                        //ID of list item layout
                        R.layout.song_list_item,
                        //Id of frame to populate
                        R.id.artist_name,
                        //Forecast data
                        songArray);
        final View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);


        ListView view = (ListView) rootView.findViewById(R.id.listview_songlist);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), adapter.getItem(position), Toast.LENGTH_SHORT).show();

            }
        });
        view.setAdapter(adapter);


        return rootView;
    }

}
