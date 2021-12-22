package com.puzzle.industries.chordsmusicapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.tabs.TabLayoutMediator;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.activities.AlbumViewActivity;
import com.puzzle.industries.chordsmusicapp.activities.ArtistViewActivity;
import com.puzzle.industries.chordsmusicapp.base.BaseFragment;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaFragment;
import com.puzzle.industries.chordsmusicapp.base.BaseVPAdapter;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentLibraryBinding;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentLibraryTabBinding;
import com.puzzle.industries.chordsmusicapp.models.viewModels.MediaVM;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibraryFragment extends BaseFragment {

    private FragmentLibraryBinding mBinding;
    private MediaVM mMediaViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLibraryBinding.inflate(inflater, container, false);
        mMediaViewModel = new ViewModelProvider(requireActivity()).get(MediaVM.class);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){
        initTabs();
        initObservers();
        initMediaInfoSection();
    }

    private void initObservers() {
        mMediaViewModel.getObservableSong().observe(getViewLifecycleOwner(), song -> mBinding.tvSongName.setText(song.getTitle()));

        mMediaViewModel.getObservableAlbum().observe(getViewLifecycleOwner(), album -> {
            Glide.with(requireContext()).load(album.getCover_url()).fallback(R.drawable.bg_album).into(mBinding.ivAlbum);
            mBinding.tvAlbumName.setText(album.getTitle());
        });

        mMediaViewModel.getObservableArtist().observe(getViewLifecycleOwner(), artist -> {
            Glide.with(requireContext()).load(artist.getPicture_url()).fallback(R.drawable.bg_artist).into(mBinding.ivArtistArt);
            mBinding.tvArtistName.setText(artist.getName());
        });
    }

    private void initMediaInfoSection(){
        final View.OnClickListener artistClickListener = view -> {
            final Intent i = new Intent(requireContext(), ArtistViewActivity.class);
            final Pair<View, String> albumPic = Pair.create(mBinding.ivArtistArt, getString(R.string.trans_artist_pic));
            final Pair<View, String> albumName = Pair.create(mBinding.tvArtistName, getString(R.string.trans_artist_name));
            final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(), albumPic, albumName
            );

            i.putExtra(Constants.KEY_ARTIST, mMediaViewModel.getObservableArtist().getValue());
            startActivity(i, optionsCompat.toBundle());
        };

        final View.OnClickListener albumClickListener = view -> {
            final AlbumArtistEntity album = mMediaViewModel.getObservableAlbum().getValue();
            if (album != null){
                final Intent i = new Intent(requireContext(), AlbumViewActivity.class);
                i.putExtra(Constants.KEY_ALBUM, album);

                final Pair<View, String> albumPic = Pair.create(mBinding.ivAlbum, getString(R.string.trans_album_pic));
                final Pair<View, String> albumName = Pair.create(mBinding.tvAlbumName, getString(R.string.trans_album_name));
                final Pair<View, String> artistName = Pair.create(mBinding.tvArtistName, getString(R.string.trans_artist_name));

                final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(), albumPic, artistName, albumName
                );

                startActivity(i, optionsCompat.toBundle());
            }

        };

        mBinding.ivArtistArt.setOnClickListener(artistClickListener);
        mBinding.tvArtistName.setOnClickListener(artistClickListener);
        mBinding.ivAlbum.setOnClickListener(albumClickListener);
        mBinding.tvAlbumName.setOnClickListener(albumClickListener);

    }
    private void initTabs(){
        final List<String> fragmentTitles = new ArrayList<>(Arrays.asList(
                getString(R.string.Music),
                getString(R.string.albums),
                getString(R.string.artists)
        ));

        final List<Fragment> fragmentList = new ArrayList<>(Arrays.asList(
                new MusicFragment(),
                new AlbumFragment(),
                new ArtistFragment()
        ));

        mBinding.vp.setAdapter(new BaseVPAdapter(requireActivity(), fragmentList));

        new TabLayoutMediator(mBinding.tl, mBinding.vp, (tab, position) -> tab.setText(fragmentTitles.get(position)))
                .attach();
    }

}
