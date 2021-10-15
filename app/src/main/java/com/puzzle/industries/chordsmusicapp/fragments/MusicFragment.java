package com.puzzle.industries.chordsmusicapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.puzzle.industries.chordsmusicapp.BaseFragment;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentMusicBinding;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.models.adapters.MusicRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class MusicFragment extends BaseFragment {

    private FragmentMusicBinding mBinding;
    private BroadcastReceiver mReceiver;
    private MusicRVAdapter mAdapter;
    private BroadcastReceiver mMusicUpdatedReceiver;
    private ArtistEntity mArtist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMusicBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    private void init(){
        initAdapter();
        initCurrentSelectedSong();
    }

    private void initCurrentSelectedSong() {
        Glide.with(requireActivity())
                .load(ContextCompat.getDrawable(requireContext(), R.drawable.bg_artist))
                .into(mBinding.ivArtist);
    }

    private void initAdapter() {
        mAdapter = new MusicRVAdapter(MusicLibraryService.getInstance().getMSongs());
        mBinding.rvMusic.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mReceiver == null){
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mAdapter.updateChanges();
                }
            };
        }

        if (mMusicUpdatedReceiver == null){
            mMusicUpdatedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final SongInfoProgressEvent songInfo = intent.getParcelableExtra(Constants.KEY_MUSIC_PROGRESS);
                    mAdapter.updateSongInfo(songInfo);

                    if (mArtist == null || mArtist.getId() != songInfo.getCurrentArtist().getId()){
                        initArtistPicture(songInfo.getCurrentArtist());
                    }
                }
            };
        }

        requireActivity().registerReceiver(mReceiver, new IntentFilter(Constants.ACTION_MUSIC_ADDED_TO_QUEUE));
        requireActivity().registerReceiver(mMusicUpdatedReceiver, new IntentFilter(Constants.KEY_MUSIC_UPDATE));
    }

    private void initArtistPicture(ArtistEntity artist) {
        Glide.with(this).load(artist.getPicture_url()).into(mBinding.ivArtist);
        mArtist = artist;
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(mReceiver);
        requireActivity().unregisterReceiver(mMusicUpdatedReceiver);
    }
}
