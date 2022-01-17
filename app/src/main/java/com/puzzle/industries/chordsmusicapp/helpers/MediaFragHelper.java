package com.puzzle.industries.chordsmusicapp.helpers;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.fragment.app.FragmentManager;

import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.fragments.AlbumFragment;
import com.puzzle.industries.chordsmusicapp.fragments.MusicFragment;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MediaFragHelper {

    public static void bindMusicFragToContainer(FragmentManager fragmentManager, int containerId, List<TrackArtistAlbumEntity> songs) {
        fragmentManager.beginTransaction().replace(containerId, buildMusicFrag(songs)).commit();
    }

    public static MusicFragment getMusicFragWithAttachedSongBundle(List<TrackArtistAlbumEntity> songs) {
        return buildMusicFrag(songs);
    }

    public static AlbumFragment getAlbumFragWithAttachedAlbumBundle(List<AlbumArtistEntity> artistAlbums) {
        return buildAlbumFrag(artistAlbums);
    }

    private static MusicFragment buildMusicFrag(List<TrackArtistAlbumEntity> songs){
        final MusicFragment musicFragment = new MusicFragment();
        final Bundle bundle = new Bundle();

        bundle.putParcelableArrayList(Constants.KEY_PLAYLIST_TRACKS, (ArrayList<? extends Parcelable>) songs);
        musicFragment.setArguments(bundle);
        return musicFragment;
    }

    private static AlbumFragment buildAlbumFrag(List<AlbumArtistEntity> albums){
        final AlbumFragment albumFragment = new AlbumFragment();
        final Bundle bundle = new Bundle();

        bundle.putParcelableArrayList(Constants.KEY_ALBUMS, (ArrayList<? extends Parcelable>) albums);
        albumFragment.setArguments(bundle);
        return albumFragment;
    }
}
