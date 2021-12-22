package com.puzzle.industries.chordsmusicapp.base;

import android.content.ComponentName;
import android.os.IBinder;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.puzzle.industries.chordsmusicapp.bottom_sheets.AlertBottomSheet;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicPlayerService;

public abstract class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {

    protected void showAlert(String message, boolean cancelable, String actionText, View.OnClickListener onClickListener){
        new AlertBottomSheet.AlertBottomSheetBuilder(getParentFragmentManager())
                .setMessage(message)
                .setCancelable(cancelable)
                .setAction(actionText, onClickListener)
                .build().show();
    }

}
