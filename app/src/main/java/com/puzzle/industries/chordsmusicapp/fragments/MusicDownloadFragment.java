package com.puzzle.industries.chordsmusicapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.puzzle.industries.chordsmusicapp.BaseFragment;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentDownloadSearchBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.MusicResultRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.viewModels.MusicVM;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerTrackDataModel;

import java.util.Objects;

public class MusicDownloadFragment extends BaseFragment {

    private FragmentDownloadSearchBinding mBinding;
    private MusicVM mViewModel;
    private MusicResultRVAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentDownloadSearchBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(MusicVM.class);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getMusicResultObservable().observe(getViewLifecycleOwner(), results -> {
            setAsLoading(false);
            if (results != null){
                displayResults(results);
            }
            else{
                showAlert(getString(R.string.error_search_music), true, getString(R.string.okay), null);
            }
        });

        Objects.requireNonNull(mBinding.tilSearch.getEditText()).setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                final String searchText = mBinding.tilSearch.getEditText().getText().toString().trim();
                if (searchText.isEmpty()){
                    mBinding.tilSearch.setError(getString(R.string.error_search_input_required));
                }
                else{
                    searchForMusic(searchText);
                }
            }
            return false;
        });
    }

    private void displayResults(DeezerTrackDataModel results) {
        if (mAdapter == null){
            mAdapter = new MusicResultRVAdapter();
            mBinding.rvResults.setAdapter(mAdapter);
        }
        mAdapter.setData(results);
    }

    private void searchForMusic(String songName) {
        setAsLoading(true);
        mViewModel.searchMusicByName(songName);
    }

    private void setAsLoading(boolean isLoading){
        mBinding.li.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mBinding.tilSearch.setEnabled(!isLoading);
    }

}
