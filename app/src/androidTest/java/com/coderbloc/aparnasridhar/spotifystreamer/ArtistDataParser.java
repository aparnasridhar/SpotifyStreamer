package com.coderbloc.aparnasridhar.spotifystreamer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aparnasridhar on 6/3/15.
 */
public class ArtistDataParser {

    public String getArtistNames(String artistJsonStr) throws JSONException {
        JSONObject artists = new JSONObject(artistJsonStr);
        JSONArray artist = artists.getJSONArray("items");
        JSONObject name = artist.getJSONObject(0);
        return name.toString();
    }
}
