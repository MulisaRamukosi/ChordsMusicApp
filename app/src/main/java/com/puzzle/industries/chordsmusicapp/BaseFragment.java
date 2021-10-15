package com.puzzle.industries.chordsmusicapp;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.puzzle.industries.chordsmusicapp.bottom_sheets.AlertBottomSheet;

public abstract class BaseFragment extends Fragment {

    protected void showAlert(String message, boolean cancelable, String actionText, View.OnClickListener onClickListener){
        new AlertBottomSheet.AlertBottomSheetBuilder(getParentFragmentManager())
                .setMessage(message)
                .setCancelable(cancelable)
                .setAction(actionText, onClickListener)
                .build().show();
    }
}
