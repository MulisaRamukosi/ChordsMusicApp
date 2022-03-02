package com.puzzle.industries.chordsmusicapp.services.impl;

import android.content.Context;
import android.os.Vibrator;

import androidx.fragment.app.FragmentManager;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.bottom_sheets.MediaOptionsBottomSheet;
import com.puzzle.industries.chordsmusicapp.services.IMediaOptionsService;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MediaOptionsService<T> implements IMediaOptionsService<T> {

    private final FragmentManager mFragManager;

    @Override
    public void showMediaOptionBottomSheet(T t, List<Integer> songIds) {
        final Vibrator vibrator = (Vibrator) Chords.getAppContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(30);
        showMediaOptions(t, songIds);
    }

    private void showMediaOptions(T t, List<Integer> songIds) {
        final MediaOptionsBottomSheet<T> mediaOptions = new MediaOptionsBottomSheet<>(t, songIds);
        mediaOptions.show(mFragManager, mediaOptions.getTag());
    }
}
