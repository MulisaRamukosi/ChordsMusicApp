package com.puzzle.industries.chordsmusicapp.base;

import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.puzzle.industries.chordsmusicapp.bottom_sheets.AlertBottomSheet;

public abstract class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {

    protected void showAlert(String message, boolean cancelable, String actionText, View.OnClickListener onClickListener) {
        new AlertBottomSheet.AlertBottomSheetBuilder(getParentFragmentManager())
                .setMessage(message)
                .setCancelable(cancelable)
                .setAction(actionText, onClickListener)
                .build().show();
    }

}
