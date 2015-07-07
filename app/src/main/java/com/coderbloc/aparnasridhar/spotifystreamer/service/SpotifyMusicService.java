package com.coderbloc.aparnasridhar.spotifystreamer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.coderbloc.aparnasridhar.spotifystreamer.model.SongList;

/**
 * Created by aparnasridhar on 6/28/15.
 */
public class SpotifyMusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {


    private MediaPlayer mediaPlayer;
    private SongList playList;
    private int songPosition = 0;
    private Context mContext;


    private final IBinder musicBind = new MusicBinder();
    public boolean isCompleted;


    public SpotifyMusicService(){

    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }


    public void onCreate() {
        //create the service

        super.onCreate();
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    //add a method to initialize the MediaPlayer class, after the onCreate method:
    public void initMusicPlayer(int position) {

        try {
            isCompleted = false;
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            String path = playList.get(position).preview_url;
            mediaPlayer.setDataSource(playList.get(position).preview_url);
            mediaPlayer.prepareAsync();
        } catch (Exception ex){
            Toast.makeText(mContext,"Error playing the selected track", Toast.LENGTH_SHORT).show();
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

    }


    @Override
    public void onCompletion(MediaPlayer mp) {

        isCompleted = true;

    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public void setPlayList(SongList playList) {
        this.playList = playList;
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

    }

    public void start() {

        mediaPlayer.start();

    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    public void setContext(Context context){
        mContext = context;
    }

    public void reset(){
        mediaPlayer.reset();
    }

    public class MusicBinder extends Binder {
        public SpotifyMusicService getService() {
            return SpotifyMusicService.this;

        }
    }


}