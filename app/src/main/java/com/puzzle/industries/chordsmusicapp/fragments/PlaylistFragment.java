package com.puzzle.industries.chordsmusicapp.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.activities.CreateEditPlaylistActivity;
import com.puzzle.industries.chordsmusicapp.activities.ViewPlaylistActivity;
import com.puzzle.industries.chordsmusicapp.base.BaseFragment;
import com.puzzle.industries.chordsmusicapp.callbacks.RVAdapterItemClickCallback;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentLibraryTabBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.PlaylistRVAdapter;
import com.puzzle.industries.chordsmusicapp.services.IMediaOptionsService;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.MediaOptionsService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.stream.Collectors;

public class PlaylistFragment extends BaseFragment implements RVAdapterItemClickCallback<PlaylistEntity>,
        ActivityResultCallback<PlaylistEntity> {

    private final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();
    private final ActivityResultContract<Void, PlaylistEntity> CREATE_PLAYLIST_CONTRACT = new ActivityResultContract<Void, PlaylistEntity>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void input) {
            return new Intent(context, CreateEditPlaylistActivity.class);
        }

        @Override
        public PlaylistEntity parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == Activity.RESULT_OK && intent != null) {
                return intent.getParcelableExtra(Constants.KEY_PLAYLIST);
            }
            return null;
        }
    };
    private final ActivityResultContract<PlaylistEntity, PlaylistEntity> VIEW_PLAYLIST_CONTRACT = new ActivityResultContract<PlaylistEntity, PlaylistEntity>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, PlaylistEntity input) {
            final Intent i = new Intent(context, ViewPlaylistActivity.class);
            i.putExtra(Constants.KEY_PLAYLIST, input);
            return i;
        }

        @Override
        public PlaylistEntity parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == Activity.RESULT_OK && intent != null) {
                return intent.getParcelableExtra(Constants.KEY_PLAYLIST);
            }
            return null;
        }
    };
    private FragmentLibraryTabBinding mBinding;
    private PlaylistRVAdapter mAdapter;
    private IMediaOptionsService<PlaylistEntity> mMediaOptionsService;
    private BroadcastReceiver mPlaylistRemovedReceiver;
    private boolean mIsSelectRequest;
    private ActivityResultLauncher<Void> createPlaylistResultLauncher;
    private ActivityResultLauncher<PlaylistEntity> viewPlaylistResultLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlaylistRemovedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceivedItemRemovedBroadCast(intent);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLibraryTabBinding.inflate(inflater, container, false);
        mMediaOptionsService = new MediaOptionsService<>(getChildFragmentManager());
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initExtras();
        registerContracts();

        mAdapter = new PlaylistRVAdapter(MUSIC_LIBRARY.getPlaylists());
        mAdapter.setItemSelectionCallback(this);
        mBinding.getRoot().setAdapter(mAdapter);
    }

    private void initExtras() {
        final Bundle bundle = getArguments();
        if (bundle != null) {
            mIsSelectRequest = bundle.getBoolean(Constants.KEY_SELECT_REQUEST, false);
        }
    }

    private void registerContracts() {
        createPlaylistResultLauncher = registerForActivityResult(CREATE_PLAYLIST_CONTRACT, this);
        viewPlaylistResultLauncher = registerForActivityResult(VIEW_PLAYLIST_CONTRACT, playlistEntity -> {
            if (playlistEntity != null) {
                mAdapter.updatePlaylist(playlistEntity);
            }
        });
    }

    @Override
    public void itemClicked(PlaylistEntity playlistEntity) {
        if (mIsSelectRequest) {
            Bundle result = new Bundle();
            result.putParcelable(Constants.KEY_PLAYLIST, playlistEntity);
            getParentFragmentManager().setFragmentResult(Constants.KEY_SELECT_REQUEST, result);
        } else {
            viewPlaylistResultLauncher.launch(playlistEntity);
        }
    }

    @Override
    public void itemLongClicked(PlaylistEntity playlistEntity) {
        mMediaOptionsService.showMediaOptionBottomSheet(playlistEntity,
                MUSIC_LIBRARY.getPlaylistTracks(playlistEntity.getId()).stream()
                        .mapToInt(TrackArtistAlbumEntity::getId)
                        .boxed().collect(Collectors.toList()));
    }

    @Override
    public void neutralItemClick() {
        createPlaylistResultLauncher.launch(null);
    }

    @Override
    public void onActivityResult(PlaylistEntity playlist) {
        if (playlist != null) {
            mAdapter.addPlaylist(playlist);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceivers();
    }

    private void unregisterReceivers() {
        requireActivity().unregisterReceiver(mPlaylistRemovedReceiver);
    }

    private void registerReceivers() {
        requireActivity().registerReceiver(mPlaylistRemovedReceiver, new IntentFilter(Constants.ACTION_PLAYLIST_DELETED));
    }

    private void onReceivedItemRemovedBroadCast(Intent intent) {
        final PlaylistEntity playlist = intent.getParcelableExtra(Constants.KEY_PLAYLIST);
        if (playlist != null) {
            mAdapter.removePlaylist(playlist);
        }
    }
}
