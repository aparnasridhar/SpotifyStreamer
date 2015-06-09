package com.coderbloc.aparnasridhar.spotifystreamer.fragment;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coderbloc.aparnasridhar.spotifystreamer.R;
import com.coderbloc.aparnasridhar.spotifystreamer.model.SongList;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;


public class PlayerFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private List<Track> trackList;
    private int playPosition;
    private int currentTrackPosition = 0;
    private boolean isPaused = true;
    private View rootView;
    private ImageButton play;
    private ImageButton previous;
    private ImageButton next;

    public PlayerFragment(){

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

    public void populateTrackUI(int position){
        TextView songName = (TextView) rootView.findViewById(R.id.songName);
        TextView albumName = (TextView) rootView.findViewById(R.id.albumName);
        TextView artistName = (TextView) rootView.findViewById(R.id.artistName);
        ImageView albumCover = (ImageView) rootView.findViewById(R.id.albumImage);

        Track currentTrack = trackList.get(position);
        songName.setText(currentTrack.name);
        albumName.setText(currentTrack.album.name);
        artistName.setText(currentTrack.artists.get(0).name);
        String url = currentTrack.album.images.get(currentTrack.album.images.size() - 2).url;
        Picasso.with(getActivity()).load(url).placeholder(R.drawable.ic_launcher).into(albumCover);

        if(position == trackList.size()-1){
            next.setEnabled(false);
        } else {
            next.setEnabled(true);
        }


        if(position == 0){
            previous.setEnabled(false);
        } else {
            previous.setEnabled(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_player, container, false);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        //Receive the track to play
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra("position")){
            playPosition = intent.getIntExtra("position",-1);
        }

        //Load all tracks
        SongList tracks = new SongList();
        trackList = tracks.getTopTenSongs();

        //Setup play,pause, previous and next tracks
        play = (ImageButton) rootView.findViewById(R.id.playButton);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPaused){
                      playSong(playPosition);
                }else{
                    pauseSong();
                }
                isPaused = !isPaused;
            }
        });

        next = (ImageButton) rootView.findViewById(R.id.playNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playPosition + 1 < trackList.size()) {
                    playPosition++;
                    stopSong();
                    populateTrackUI(playPosition);
                }
            }
        });

        previous = (ImageButton) rootView.findViewById(R.id.playPrevious);
        previous.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(playPosition - 1 > -1){
                    playPosition--;
                    stopSong();
                    populateTrackUI(playPosition);
                }
            }
        });

        //Setup the track UI
        if(playPosition !=-1 && trackList !=null) {
            populateTrackUI(playPosition);

        }


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseSong();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSong();
    }

    private void pauseSong(){
        play.setImageDrawable(getResources().getDrawable(R.drawable.button_play));
        mediaPlayer.pause();
        currentTrackPosition = mediaPlayer.getCurrentPosition();
    }
    private void stopSong(){
        mediaPlayer.stop();
        mediaPlayer.reset();
        isPaused = true;
        currentTrackPosition = 0;
        play.setImageDrawable(getResources().getDrawable(R.drawable.button_play));
    }

    private void playSong(int songIndex){
        try{
            play.setImageDrawable(getResources().getDrawable(R.drawable.button_pause));
            mediaPlayer.setDataSource(trackList.get(songIndex).preview_url);
            if(currentTrackPosition !=0) {
                mediaPlayer.seekTo(currentTrackPosition);
            }
            mediaPlayer.prepare();
            mediaPlayer.start();


        }catch(Exception e){
            Toast.makeText(getActivity(), "Sorry, cannot play this song", Toast.LENGTH_SHORT).show();
        }
    }

}
