package com.puzzle.industries.chordsmusicapp.bottom_sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.base.BaseBottomSheetDialogFragment;
import com.puzzle.industries.chordsmusicapp.callbacks.SongInfoCallback;
import com.puzzle.industries.chordsmusicapp.databinding.BottomSheetNotCorrectInfoBinding;
import com.puzzle.industries.chordsmusicapp.databinding.ItemSongInfoBinding;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongInfoStruct;
import com.puzzle.industries.chordsmusicapp.remote.genius.api.GeniusApiCall;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.HitsModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.SongResultModel;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;

import java.util.Objects;

public class NotCorrectSongInfoBottomSheet extends BaseBottomSheetDialogFragment{

    private BottomSheetNotCorrectInfoBinding mBinding;

    private final SongInfoStruct mSongInfo;
    private final SongInfoCallback mCallback;

    public NotCorrectSongInfoBottomSheet(SongInfoStruct mSongInfo, SongInfoCallback callback) {
        this.mSongInfo = mSongInfo;
        this.mCallback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = BottomSheetNotCorrectInfoBinding.inflate(inflater, container, false);
        Objects.requireNonNull(getDialog()).setCanceledOnTouchOutside(false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initSongInfos();
    }

    private void initSongInfos() {
        for (HitsModel hitModel : mSongInfo.getSearchResult().getResponse().getHits()){
            final ItemSongInfoBinding binding = ItemSongInfoBinding.inflate(LayoutInflater.from(requireContext()), mBinding.llInfoHolder, false);
            mBinding.llInfoHolder.addView(binding.getRoot());
            binding.tvSongInfo.setText(hitModel.getResult().getFull_title());
            binding.getRoot().setOnClickListener(v -> {
                showAlert("You are about to change this songs info", true,"proceed", vv -> {
                    attemptToGetSong(hitModel, binding);

                });
            });
        }
    }

    private void attemptToGetSong(HitsModel hitModel, ItemSongInfoBinding binding){
        setAsEnabled(false);
        binding.lpi.setVisibility(View.VISIBLE);

        if (mSongInfo.getSearchResult().getResponse().getHits().remove(hitModel)){
            mSongInfo.getSearchResult().getResponse().getHits().set(0, hitModel);
            GeniusApiCall.getInstance().getSongInfoById(hitModel.getResult().getSongId(), new ApiCallBack<SongResultModel>() {
                @Override
                public void onSuccess(SongResultModel songResultModel) {
                    mSongInfo.setSongResult(songResultModel);
                    mCallback.songInfoChanged(mSongInfo);
                    Objects.requireNonNull(getDialog()).dismiss();
                }

                @Override
                public void onFailure(Throwable t) {
                    mCallback.songInfoChangeFailed();
                    Objects.requireNonNull(getDialog()).dismiss();
                }
            });

        }
        else{
            setAsEnabled(true);
            binding.lpi.setVisibility(View.GONE);
        }
    }

    private void setAsEnabled(boolean isEnabled){
        for (int i = 0; i < mBinding.llInfoHolder.getChildCount(); i++){
            final View v = mBinding.llInfoHolder.getChildAt(i);
            v.setEnabled(isEnabled);
        }
    }
}
