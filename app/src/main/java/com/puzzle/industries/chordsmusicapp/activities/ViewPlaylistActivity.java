package com.puzzle.industries.chordsmusicapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaActivity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivityViewPlaylistBinding;
import com.puzzle.industries.chordsmusicapp.helpers.MediaFragHelper;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewPlaylistActivity extends BaseMediaActivity implements ActivityResultCallback<Boolean> {

    private final ActivityResultContract<PlaylistEntity, Boolean> EDIT_PLAYLIST_CONTRACT = new ActivityResultContract<PlaylistEntity, Boolean>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, PlaylistEntity input) {
            final Intent i = new Intent(context, CreateEditPlaylistActivity.class);
            i.putExtra(Constants.KEY_PLAYLIST, input);
            i.putParcelableArrayListExtra(Constants.KEY_PLAYLIST_TRACKS, (ArrayList<? extends Parcelable>) MUSIC_LIBRARY.getPlaylistTrackEntityList(input.getId()));
            return i;
        }

        @Override
        public Boolean parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == Activity.RESULT_OK && intent != null) {
                return intent.getBooleanExtra(Constants.KEY_WAS_UPDATED, false);
            }
            return false;
        }
    };
    private List<TrackArtistAlbumEntity> mPlaylistTracks;
    private ActivityViewPlaylistBinding mBinding;
    private PlaylistEntity mPlaylist;
    private boolean mChangesOccurred;
    private ActivityResultLauncher<PlaylistEntity> mPlaylistResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityViewPlaylistBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.tb);
        init();
    }

    private void init() {
        if (initPlaylist()) {
            registerContracts();
            initPlaylistTracks();
            initPlaylistView();
            initFragmentContainer();
        }
    }

    private void registerContracts() {
        mPlaylistResultLauncher = registerForActivityResult(EDIT_PLAYLIST_CONTRACT, this);
    }

    private boolean initPlaylist() {
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPlaylist = bundle.getParcelable(Constants.KEY_PLAYLIST);
            return mPlaylist != null;
        }
        return false;
    }

    private void initPlaylistView() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(mPlaylist.getName());
    }

    private void initPlaylistTracks() {
        mPlaylistTracks = MUSIC_LIBRARY.getPlaylistTracks(mPlaylist.getId());
    }

    private void editPlaylist() {
        mPlaylistResultLauncher.launch(mPlaylist);
    }

    private void initFragmentContainer() {
        MediaFragHelper.bindMusicFragToContainer(getSupportFragmentManager(), mBinding.fc.getId(), this.mPlaylistTracks);
    }

    @Override
    public void onActivityResult(Boolean wasUpdated) {
        if (wasUpdated) {
            mChangesOccurred = true;
            Chords.applicationHandler.postDelayed(() -> {
                mPlaylist = MUSIC_LIBRARY.getPlaylistById(mPlaylist.getId());
                initPlaylistTracks();
                initPlaylistView();
                initFragmentContainer();
            }, 100);

        }
    }

    @Override
    public void onBackPressed() {
        if (mChangesOccurred) {
            final Intent i = new Intent();
            i.putExtra(Constants.KEY_PLAYLIST, mPlaylist);
            setResult(Activity.RESULT_OK, i);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_playlist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mi_edit) {
            editPlaylist();
            return true;
        }

        return false;
    }
}
