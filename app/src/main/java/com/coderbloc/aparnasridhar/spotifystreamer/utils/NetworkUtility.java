package com.coderbloc.aparnasridhar.spotifystreamer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by aparnasridhar on 6/29/15.
 */
public class NetworkUtility {

    Context mContext;
    public NetworkUtility(Context context){
        mContext = context;
    }

    //Based on a stackoverflow snippet
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
