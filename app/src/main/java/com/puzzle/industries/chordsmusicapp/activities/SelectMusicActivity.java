package com.puzzle.industries.chordsmusicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.base.BaseActivity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivitySelectSongsBinding;
import com.puzzle.industries.chordsmusicapp.models.adapters.MusicRVAdapter;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectMusicActivity extends BaseActivity {

    private ActivitySelectSongsBinding mBinding;
    private MusicRVAdapter mAdapter;
    private List<Integer> mAlreadySelectedIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivitySelectSongsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        init();
    }

    private void init() {

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            mAlreadySelectedIds = bundle.getIntegerArrayList(Constants.KEY_PLAYLIST_TRACKS);
        if (mAlreadySelectedIds == null) mAlreadySelectedIds = new ArrayList<>();

        mAdapter = new MusicRVAdapter(MusicLibraryService.getInstance().getSongs());
        mAdapter.setSelectionRequest(true);
        mAdapter.setAlreadySelectedIds(mAlreadySelectedIds);
        mBinding.rvMusic.setAdapter(mAdapter);

        mBinding.btnSave.setOnClickListener(view -> {
            final Intent i = new Intent();

            i.putIntegerArrayListExtra(Constants.KEY_PLAYLIST_TRACKS, (ArrayList<Integer>) mAdapter.getSelectedSongs());
            setResult(RESULT_OK, i);
            finish();
        });

        mBinding.btnCancel.setOnClickListener(view -> finish());

        Objects.requireNonNull(mBinding.tilSearch.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String word = charSequence.toString().trim();

                if (word.isEmpty()) {
                    mAdapter.reset();
                } else {
                    mAdapter.showSearchResults(word);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}
