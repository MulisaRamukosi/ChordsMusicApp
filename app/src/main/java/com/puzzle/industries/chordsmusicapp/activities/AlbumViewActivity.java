package com.puzzle.industries.chordsmusicapp.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaActivity;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivityAlbumViewBinding;
import com.puzzle.industries.chordsmusicapp.helpers.MediaFragHelper;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.List;

public class AlbumViewActivity extends BaseMediaActivity{

    private ActivityAlbumViewBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAlbumViewBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        postponeEnterTransition();
        init();
    }

    private void init() {
        initAlbumDetails();
    }

    private void initAlbumDetails() {
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            AlbumArtistEntity album = bundle.getParcelable(Constants.KEY_ALBUM);
            displayImageFromLink(album.getCover_url(), mBinding.ivAlbum, R.drawable.bg_album, true);
            if (album.getCover_url() != null){
                displayImageFromLink(album.getCover_url(), mBinding.ivAlbum, R.drawable.bg_album, true);
            }
            else{
                displayImageFromDrawable(R.drawable.bg_album, mBinding.ivAlbum, R.drawable.bg_album,true);
            }
            mBinding.tvAlbumName.setText(album.getTitle());
            mBinding.tvAlbumArtist.setText(album.getName());

            initSongList(album.getId());
        }

        mBinding.lpi.setIndeterminate(false);
        mBinding.lpi.setVisibility(View.GONE);
    }

    private void initSongList(int albumId) {
        List<TrackArtistAlbumEntity> albumSongs = MUSIC_LIBRARY.getAlbumSongs(albumId);
        MediaFragHelper.bindMusicFragToContainer(getSupportFragmentManager(), mBinding.fcv.getId(), albumSongs);
    }

}
