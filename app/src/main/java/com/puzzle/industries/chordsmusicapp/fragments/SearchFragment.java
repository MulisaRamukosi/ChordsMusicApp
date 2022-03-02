package com.puzzle.industries.chordsmusicapp.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayoutMediator;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseFragment;
import com.puzzle.industries.chordsmusicapp.base.BaseVPAdapter;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentSearchBinding;
import com.puzzle.industries.chordsmusicapp.models.viewModels.SearchVM;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.utils.MediaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends BaseFragment {

    private FragmentSearchBinding mBinding;
    private SearchVM mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSearchBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(SearchVM.class);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Fragment trackArtistFragment = new ResultsContainerFragment<TrackArtistAlbumEntity>();
        final Fragment albumArtistFragment = new ResultsContainerFragment<AlbumArtistEntity>();
        final Fragment artistFragment = new ResultsContainerFragment<ArtistEntity>();

        trackArtistFragment.setArguments(getMediaBundle(MediaType.SONG));
        albumArtistFragment.setArguments(getMediaBundle(MediaType.ALBUM));
        artistFragment.setArguments(getMediaBundle(MediaType.ARTIST));

        final List<String> fragmentTitles = new ArrayList<>(Arrays.asList(
                getString(R.string.Music),
                getString(R.string.albums),
                getString(R.string.artists)
        ));

        final List<Fragment> fragmentList = new ArrayList<>(Arrays.asList(
                trackArtistFragment,
                albumArtistFragment,
                artistFragment
        ));

        final BaseVPAdapter mAdapter = new BaseVPAdapter(requireActivity(), fragmentList);
        mBinding.vp.setAdapter(mAdapter);

        new TabLayoutMediator(mBinding.tl, mBinding.vp, (tab, position) -> tab.setText(fragmentTitles.get(position)))
                .attach();

        Objects.requireNonNull(mBinding.tilSearch.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String word = charSequence.toString().trim();
                mViewModel.updateSearchWord(word);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private Bundle getMediaBundle(MediaType type) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_MEDIA_TYPE, type);
        return bundle;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.updateSearchWord("");
    }
}
