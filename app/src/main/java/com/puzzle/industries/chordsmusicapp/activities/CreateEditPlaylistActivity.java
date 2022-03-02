package com.puzzle.industries.chordsmusicapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseActivity;
import com.puzzle.industries.chordsmusicapp.callbacks.PlaylistCallback;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivityCreatePlaylistBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.PlaylistTracksRVAdapter;
import com.puzzle.industries.chordsmusicapp.services.IPlaylistService;
import com.puzzle.industries.chordsmusicapp.services.impl.ExecutorServiceManager;
import com.puzzle.industries.chordsmusicapp.services.impl.PlaylistService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CreateEditPlaylistActivity extends BaseActivity implements PlaylistCallback {

    private final Set<PlaylistTrackEntity> mRemovedSongs = new HashSet<>();
    private final ActivityResultContract<List<Integer>, List<Integer>> SELECT_SONGS_CONTRACT = new ActivityResultContract<List<Integer>, List<Integer>>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, List<Integer> input) {
            final Intent i = new Intent(context, SelectMusicActivity.class);
            i.putIntegerArrayListExtra(Constants.KEY_PLAYLIST_TRACKS, (ArrayList<Integer>) input);
            return i;
        }

        @Override
        public List<Integer> parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == RESULT_OK)
                return Objects.requireNonNull(intent).getIntegerArrayListExtra(Constants.KEY_PLAYLIST_TRACKS);
            else {
                return null;
            }
        }
    };
    private IPlaylistService mPlaylistService;
    private ActivityCreatePlaylistBinding mBinding;
    private PlaylistEntity mPlaylist;
    private PlaylistTracksRVAdapter mAdapter;
    private BroadcastReceiver mSongsRemovedBroadcastReceiver;
    private List<PlaylistTrackEntity> mPlaylistTracks = new ArrayList<>();
    private boolean isUpdate;
    private boolean wasUpdated;
    private ActivityResultLauncher<List<Integer>> selectSongsResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCreatePlaylistBinding.inflate(getLayoutInflater());
        mPlaylistService = PlaylistService.getInstance(this, this);
        setContentView(mBinding.getRoot());
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mSongsRemovedBroadcastReceiver, new IntentFilter(Constants.ACTION_PLAYLIST_TRACKS_DELETED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mSongsRemovedBroadcastReceiver);
    }

    private void init() {
        initBroadcastReceivers();
        registerContracts();

        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mPlaylist = extras.getParcelable(Constants.KEY_PLAYLIST);
            mPlaylistTracks = extras.getParcelableArrayList(Constants.KEY_PLAYLIST_TRACKS);
            mAdapter = new PlaylistTracksRVAdapter(mPlaylistTracks);
            isUpdate = mPlaylist != null;
        } else {
            mAdapter = new PlaylistTracksRVAdapter(new ArrayList<>());
        }

        setSaveUpdateButtonText();

        if (isUpdate) {
            Objects.requireNonNull(mBinding.tilPlaylist.getEditText()).setText(mPlaylist.getName());
        }

        mBinding.rvSongs.setAdapter(mAdapter);
        mBinding.btnCancel.setOnClickListener(v -> finish());
        mBinding.btnSave.setOnClickListener(v -> savePlaylistAndTracks());
        mBinding.tvAddSongs.setOnClickListener(v -> launchSelectSongsActivity());
    }

    private void initBroadcastReceivers() {
        mSongsRemovedBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPlaylistService.addSongsToPlaylist(mPlaylist.getId(), mAdapter.getPlaylistTracks());
            }
        };
    }

    private void registerContracts() {
        selectSongsResultLauncher = registerForActivityResult(SELECT_SONGS_CONTRACT, result -> {
            if (result != null) {
                mRemovedSongs.addAll(getRemovedSongs(result));
                mAdapter.addNewSongs(result);
                mAdapter.removeSongs(mRemovedSongs);
            }
        });
    }

    private List<PlaylistTrackEntity> getRemovedSongs(List<Integer> returnedList) {
        return this.mPlaylistTracks.stream().filter(playlistTrackEntity -> !returnedList.contains(playlistTrackEntity.getTrackId())).collect(Collectors.toList());
    }

    private void launchSelectSongsActivity() {
        final List<Integer> songIds = mAdapter.getPlaylistTracks()
                .stream()
                .mapToInt(PlaylistTrackEntity::getTrackId)
                .boxed()
                .collect(Collectors.toList());
        selectSongsResultLauncher.launch(songIds);
    }

    private void savePlaylistAndTracks() {
        final String playlistName = mBinding.tilPlaylist.getEditText().getText().toString().trim();
        if (playlistName.isEmpty()) {
            mBinding.tilPlaylist.setError(getString(R.string.error_playlist_name_required));
        } else {
            setActivityAsEnabled(false);
            if (isUpdate) {
                wasUpdated = true;
                updatePlaylist(playlistName);
            } else {
                mPlaylistService.addPlaylist(new PlaylistEntity(0, playlistName));
            }
        }
    }

    private void updatePlaylist(String playlistName) {
        setActivityAsEnabled(false);
        if (!playlistName.equals(mPlaylist.getName())) {
            mPlaylistService.updatePlaylistName(mPlaylist.getId(), playlistName);
        } else {
            updatePlaylistSongs();
        }
    }

    private void setSaveUpdateButtonText() {
        if (isUpdate) {
            mBinding.btnSave.setText(getString(R.string.update));
        }
    }

    private void setActivityAsEnabled(boolean enabled) {
        mBinding.cpi.setVisibility(enabled ? View.GONE : View.VISIBLE);
        mBinding.btnSave.setEnabled(enabled);
        mBinding.btnCancel.setEnabled(enabled);
        mBinding.tilPlaylist.setEnabled(enabled);
        mBinding.tvAddSongs.setEnabled(enabled);
        mBinding.rvSongs.setEnabled(enabled);
    }

    @Override
    public void playlistCreated(PlaylistEntity playlist) {
        mPlaylist = playlist;
        mPlaylistService.addSongsToPlaylist(playlist.getId(), mAdapter.getPlaylistTracks());
        runOnUiThread(() -> Toast.makeText(CreateEditPlaylistActivity.this, String.format("Playlist %s created", mPlaylist.getName()), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void songsAddedToPlaylist() {
        runOnUiThread(() -> {
            final Intent i = new Intent();
            i.putExtra(Constants.KEY_PLAYLIST, mPlaylist);
            if (wasUpdated) {
                i.putExtra(Constants.KEY_WAS_UPDATED, true);
            }
            setResult(RESULT_OK, i);
            finish();
        });

    }

    @Override
    public void playlistUpdated() {
        updatePlaylistSongs();
    }

    @Override
    public void operationFailed() {
        runOnUiThread(() -> {
            setActivityAsEnabled(true);
            showAlert("The operation failed.", getString(R.string.okay), null);
        });

    }

    private void updatePlaylistSongs() {
        ExecutorServiceManager
                .getInstance()
                .executeRunnableOnSingeThread(() -> mPlaylistService.removeSongsFromPlaylist(mPlaylist.getId(), mRemovedSongs));
    }
}
