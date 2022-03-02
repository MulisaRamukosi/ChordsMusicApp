package com.puzzle.industries.chordsmusicapp.helpers;

import android.content.Intent;
import android.net.Uri;

import com.puzzle.industries.chordsmusicapp.Chords;

public class UrlHelper {

    public static void openLinkInExternalBrowser(String url){
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Chords.getAppContext().startActivity(intent);
    }
}
