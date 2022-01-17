package com.puzzle.industries.chordsmusicapp.bottom_sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.puzzle.industries.chordsmusicapp.databinding.BottomSheetCurrentPlaylistBinding;
import com.puzzle.industries.chordsmusicapp.databinding.BottomSheetMediaOptionsBinding;
import com.puzzle.industries.chordsmusicapp.fragments.MusicFragment;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.Objects;

public class CurrentPlaylistBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetCurrentPlaylistBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).setCanceledOnTouchOutside(false);
        mBinding = BottomSheetCurrentPlaylistBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){
        final MusicFragment musicFragment = new MusicFragment();
        final Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_DISPLAY_CURRENT_PLAYLIST, true);
        musicFragment.setArguments(bundle);
        getChildFragmentManager().beginTransaction().replace(mBinding.fc.getId(), musicFragment).commit();
    }
}
