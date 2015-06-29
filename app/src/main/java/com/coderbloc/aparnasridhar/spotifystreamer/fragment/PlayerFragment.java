package com.coderbloc.aparnasridhar.spotifystreamer.fragment;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.coderbloc.aparnasridhar.spotifystreamer.R;
import com.coderbloc.aparnasridhar.spotifystreamer.model.SongList;
import com.coderbloc.aparnasridhar.spotifystreamer.service.SpotifyMusicService;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.models.Track;


public class PlayerFragment extends DialogFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    private List<Track> trackList;
    private int playPosition = 0;
    private boolean hasStarted = false;
    private View rootView;
    private ImageButton play;
    private ImageButton previous;
    private ImageButton next;
    private TextView playStart;
    private TextView playEnd;
    private ProgressBar progressBar;

    private SpotifyMusicService musicSrv;
    private Intent playIntent;
    public ServiceConnection musicConnection;
    private SeekBar playerSeekBar;

    //To update the seek bar
    Handler seekHandler = new Handler();
    Runnable run = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
        }

    };

    public void updateSeekBar(){
        try {
            if (musicSrv.isPlaying()) {
                playStart.setText(formatTime(musicSrv.getCurrentPosition()));
                playEnd.setText(formatTime(musicSrv.getDuration()));
                playerSeekBar.setMax(musicSrv.getDuration());
                playerSeekBar.setProgress(musicSrv.getCurrentPosition());
                play.setImageDrawable(getResources().getDrawable(R.drawable.button_pause));

            } else if (playerSeekBar.getProgress() > 0){
                play.setImageDrawable(getResources().getDrawable(R.drawable.button_play));
                playerSeekBar.setProgress(0);
                playStart.setText("0:00");
            }
            seekHandler.postDelayed(run, 1000);
        } catch (Exception e){

        }
    }


    private String formatTime(long millis){
        StringBuffer buffer = new StringBuffer();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        String secondStr = String.valueOf(seconds);
        if(secondStr.length() < 2){
            secondStr = "0"+secondStr;
        }
        buffer.append("0").append(":").append(secondStr);
        return buffer.toString();
    }

    public PlayerFragment(){

    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //Receive the track to play
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra("position")){
            playPosition = intent.getIntExtra("position",-1);
        }

        //Load all tracks
        final SongList tracks = new SongList();
        trackList = tracks.getTopTenSongs();
        //connect to the service
        musicConnection = new ServiceConnection(){

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                SpotifyMusicService.MusicBinder binder = (SpotifyMusicService.MusicBinder)service;
                //get service
                musicSrv = binder.getService();
                //pass list
                musicSrv.setPlayList(tracks);
                //
                playSong(playPosition);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        if(playIntent==null){
            playIntent = new Intent(getActivity(), SpotifyMusicService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                return true;
        }
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

        if(url !=null ) {
            Picasso.with(getActivity()).load(url).placeholder(R.drawable.ic_launcher).into(albumCover);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_player, container, false);



        progressBar = (ProgressBar) rootView.findViewById(R.id.progressbar_play);

        playerSeekBar = (SeekBar) rootView.findViewById(R.id.audioSeekBar);
        playerSeekBar.setVisibility(View.VISIBLE);
        playerSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        seekHandler.postDelayed(run, 1000);

        playStart = (TextView) rootView.findViewById(R.id.playerStart);
        playEnd = (TextView) rootView.findViewById(R.id.playerEnd);

        //Setup play,pause, previous and next tracks
        play = (ImageButton) rootView.findViewById(R.id.playButton);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicSrv.isPlaying()) {

                    musicSrv.pause();
                    play.setImageDrawable(getResources().getDrawable(R.drawable.button_play));

                } else {
                    //resume song
                    if (!hasStarted){
                        playSong(playPosition);
                        hasStarted = true;
                    } else {
                        musicSrv.start();
                    }


                    play.setImageDrawable(getResources().getDrawable(R.drawable.button_pause));

                }
            }
        });

        next = (ImageButton) rootView.findViewById(R.id.playNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (playPosition < trackList.size() - 1) {
                    playPosition++;
                    playSong(playPosition);
                } else {
                    playPosition = 0;
                    playSong(playPosition);
                }
            }
        });

        previous = (ImageButton) rootView.findViewById(R.id.playPrevious);
        previous.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(playPosition > 0){
                    playPosition--;
                    playSong(playPosition);
                } else {
                    playPosition = trackList.size() - 1;
                    playSong(playPosition);
                }
            }
        });

        //Setup the track UI
        if(playPosition !=-1 && trackList !=null) {
            populateTrackUI(playPosition);

        }

        return rootView;
    }

    private void playSong(int songIndex){
        if(musicSrv.isPlaying()) {
            musicSrv.pause();
            musicSrv.reset();
        }
        play.setImageDrawable(getResources().getDrawable(R.drawable.button_pause));
        musicSrv.initMusicPlayer(songIndex);
        if(hasStarted) {
            populateTrackUI(playPosition);
        } else {
            hasStarted = true;
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        musicSrv.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().stopService(playIntent);
        musicSrv=null;

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
