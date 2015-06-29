package com.coderbloc.aparnasridhar.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by aparnasridhar on 6/24/15.
 */
public class ArtistData implements Parcelable {

    private String name;
    private String spotifyId;
    private String thumbnailURL;

    public ArtistData(Artist artist) {
        this.name = artist.name;
        this.spotifyId = artist.id;
    }

    private ArtistData(Parcel in) {
        this.name = in.readString();
        this.spotifyId = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(spotifyId);
    }

    public static final Parcelable.Creator<ArtistData> CREATOR = new Parcelable.Creator<ArtistData>() {
        public ArtistData createFromParcel(Parcel in) {
            return new ArtistData(in);
        }

        public ArtistData[] newArray(int size) {
            return new ArtistData[size];
        }
    };

    public int describeContents() {
        return 0;
    }
}
