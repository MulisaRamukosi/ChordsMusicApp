package com.puzzle.industries.chordsmusicapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;

import com.puzzle.industries.chordsmusicapp.base.BaseActivity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivitySelectPlaylistBinding;
import com.puzzle.industries.chordsmusicapp.fragments.PlaylistFragment;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class SelectPlaylistActivity extends BaseActivity {

    private ActivitySelectPlaylistBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySelectPlaylistBinding.inflate(getLayoutInflater());
        init();
        setContentView(mBinding.getRoot());
    }

    private void init(){
        final PlaylistFragment playlistFragment = new PlaylistFragment();
        final Bundle bundle = new Bundle();

        bundle.putBoolean(Constants.KEY_SELECT_REQUEST, true);
        playlistFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(mBinding.fc.getId(), playlistFragment).commit();
        getSupportFragmentManager().setFragmentResultListener(Constants.KEY_SELECT_REQUEST, this, (requestKey, result) -> {
            final PlaylistEntity selectedPlaylist = result.getParcelable(Constants.KEY_PLAYLIST);
            if (selectedPlaylist != null){
                sendPlaylistResult(selectedPlaylist);
            }
        });
    }

    private void sendPlaylistResult(@NonNull PlaylistEntity playlist){
        final Intent intent = new Intent();
        intent.putExtra(Constants.KEY_PLAYLIST, playlist);
        setResult(RESULT_OK, intent);
        finish();
    }
}
