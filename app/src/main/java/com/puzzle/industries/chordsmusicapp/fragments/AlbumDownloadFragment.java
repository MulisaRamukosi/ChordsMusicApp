package com.puzzle.industries.chordsmusicapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.base.BaseFragment;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentDownloadSearchBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.AlbumResultRVAdapter;
import com.puzzle.industries.chordsmusicapp.remote.deezer.api.DeezerApiCall;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerAlbumDataModel;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;

import java.util.Objects;

public class AlbumDownloadFragment extends BaseFragment {

    private FragmentDownloadSearchBinding mBinding;
    private AlbumResultRVAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentDownloadSearchBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    private void searchForAlbum(String albumName) {
        setAsLoading(true);

        DeezerApiCall.getInstance().searchAlbums(albumName, new ApiCallBack<DeezerAlbumDataModel>() {
            @Override
            public void onSuccess(DeezerAlbumDataModel deezerAlbumDataModel) {
                setAsLoading(false);
                displayResults(deezerAlbumDataModel);
            }

            @Override
            public void onFailure(Throwable t) {
                setAsLoading(false);
                showAlert(getString(R.string.error_search_music), true, getString(R.string.okay), null);
            }
        });
    }

    private void setAsLoading(boolean isLoading){
        mBinding.li.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mBinding.tilSearch.setEnabled(!isLoading);
    }
}
