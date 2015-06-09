package com.coderbloc.aparnasridhar.spotifystreamer.model;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by aparnasridhar on 6/6/15.
 */
public class SongList {

    public static List<Track> getTopTenSongs() {
        return topTenSongs;
    }

    public static void setTopTenSongs(List<Track> songs) {
        topTenSongs = songs;
    }

    private static List<Track> topTenSongs;


}
