package com.puzzle.industries.chordsmusicapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.puzzle.industries.chordsmusicapp.base.BaseFragment;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentDownloadSearchBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.AlbumResultRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.viewModels.AlbumVM;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerAlbumDataModel;

import java.util.Objects;

public class AlbumDownloadFragment extends BaseFragment {

    private FragmentDownloadSearchBinding mBinding;
    private AlbumVM mViewModel;
    private AlbumResultRVAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentDownloadSearchBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(AlbumVM.class);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getAlbumResultObservable().observe(getViewLifecycleOwner(), results -> {
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
                    searchForAlbum(searchText);
                }
            }
            return false;
        });
    }

    private void displayResults(DeezerAlbumDataModel results) {
        if (mAdapter == null){
            mAdapter = new AlbumResultRVAdapter();
            mBinding.rvResults.setAdapter(mAdapter);
        }
        mAdapter.setData(results.getData());
    }

    private void searchForAlbum(String songName) {
        setAsLoading(true);
        mViewModel.searchAlbumByName(songName);
    }

    private void setAsLoading(boolean isLoading){
        mBinding.li.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mBinding.tilSearch.setEnabled(!isLoading);
    }
}
