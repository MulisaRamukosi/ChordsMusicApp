package com.puzzle.industries.chordsmusicapp.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaActivity;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivityArtistViewBinding;
import com.puzzle.industries.chordsmusicapp.fragments.AlbumFragment;
import com.puzzle.industries.chordsmusicapp.fragments.ArtistAlbumFragment;
import com.puzzle.industries.chordsmusicapp.base.BaseVPAdapter;
import com.puzzle.industries.chordsmusicapp.fragments.MusicFragment;
import com.puzzle.industries.chordsmusicapp.helpers.MediaFragHelper;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArtistViewActivity extends BaseMediaActivity {

    private ActivityArtistViewBinding mBinding;
    private ArtistEntity mArtist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityArtistViewBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        postponeEnterTransition();
        init();
    }

    private void init(){
        if (initArtist()){
            initArtistInfo();
            initArtistSongsSection();
        }
    }

    private void initArtistSongsSection() {
        final List<TrackArtistAlbumEntity> artistSongs = MUSIC_LIBRARY.getArtistSongs(mArtist.getId());
        final List<AlbumArtistEntity> artistAlbums = MUSIC_LIBRARY.getArtistAlbums(mArtist.getId());

        final MusicFragment musicFragment = MediaFragHelper.getMusicFragWithAttachedSongBundle(artistSongs);
        final AlbumFragment albumFragment = MediaFragHelper.getAlbumFragWithAttachedAlbumBundle(artistAlbums);


        final List<String> fragmentTitles = new ArrayList<>(Arrays.asList(
                getString(R.string.songs),
                getString(R.string.albums)
        ));

        final List<Fragment> fragmentList = new ArrayList<>(Arrays.asList(
                musicFragment,
                albumFragment
        ));

        mBinding.vp.setAdapter(new BaseVPAdapter(this, fragmentList));
        new TabLayoutMediator(mBinding.tl, mBinding.vp, (tab, position) -> tab.setText(fragmentTitles.get(position)))
                .attach();
    }

    private boolean initArtist(){
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mArtist = bundle.getParcelable(Constants.KEY_ARTIST);
            return mArtist != null;
        }
        return false;
    }

    private void initArtistInfo(){
        if (mArtist.getPicture_url() != null){
            displayImageFromLink(mArtist.getPicture_url(), mBinding.ivArtist, R.drawable.bg_artist, true);
        }
        else{
            displayImageFromDrawable(R.drawable.bg_artist, mBinding.ivArtist, R.drawable.bg_artist, true);
        }

        mBinding.tvArtistName.setText(mArtist.getName());
    }

}
