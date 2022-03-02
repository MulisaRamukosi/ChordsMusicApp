package com.puzzle.industries.chordsmusicapp.models.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchVM extends ViewModel {

    private MutableLiveData<String> mSearchWord;

    public LiveData<String> getObservableSearchWord() {
        if (mSearchWord == null) {
            mSearchWord = new MutableLiveData<>();
        }

        return mSearchWord;
    }

    public void updateSearchWord(String word) {
        mSearchWord.setValue(word);
    }
}
