package com.puzzle.industries.chordsmusicapp.helpers;

import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.R;

public class ArtHelper {


    public static void displayArtistArtFromUrl(String url, ImageView iv) {
        displayImageFromUrl(url, iv, R.drawable.bg_artist);
    }

    public static void displayAlbumArtFromUrl(String url, ImageView iv) {
        displayImageFromUrl(url, iv, R.drawable.bg_album);
    }

    private static void displayImageFromUrl(String url, ImageView iv, int drawableFallbackId) {
        Glide.with(Chords.getAppContext())
                .load(url)
                .placeholder(drawableFallbackId)
                .fallback(drawableFallbackId)
                .into(iv);
    }

    public static void displayDefaultAlbumArt(ImageView iv) {
        Glide.with(Chords.getAppContext())
                .load(ContextCompat.getDrawable(Chords.getAppContext(), R.drawable.bg_album))
                .into(iv);
    }
}
