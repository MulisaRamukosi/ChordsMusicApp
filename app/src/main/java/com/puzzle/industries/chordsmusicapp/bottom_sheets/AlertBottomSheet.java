package com.puzzle.industries.chordsmusicapp.bottom_sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.databinding.BottomSheetAlertBinding;

import org.jetbrains.annotations.NotNull;

public class AlertBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetAlertBinding mBinding;
    private final FragmentManager fragmentManager;
    private final String message;
    private final String actionBtnText;
    private final boolean cancelable;
    private final View.OnClickListener actionBtnClickListener;

    public static class AlertBottomSheetBuilder{
        private String message;
        private String actionBtnText;
        private boolean cancelable;
        private View.OnClickListener actionBtnClickListener;
        private final FragmentManager fragmentManager;

        public AlertBottomSheetBuilder(FragmentManager fragmentManager){
            this.fragmentManager = fragmentManager;
        }

        public AlertBottomSheetBuilder setMessage(String message){
            this.message = message;
            return this;
        }

        public AlertBottomSheetBuilder setCancelable(boolean cancelable){
            this.cancelable = cancelable;
            return this;
        }

        public AlertBottomSheetBuilder setAction(String actionBtnText, View.OnClickListener actionBtnClickListener){
            this.actionBtnText = actionBtnText;
            this.actionBtnClickListener = actionBtnClickListener;
            return this;
        }

        public AlertBottomSheet build(){
            return new AlertBottomSheet(this);
        }
    }

    public AlertBottomSheet(AlertBottomSheetBuilder builder){
        this.fragmentManager = builder.fragmentManager;
        this.message = builder.message;
        this.cancelable = builder.cancelable;
        this.actionBtnText = builder.actionBtnText;
        this.actionBtnClickListener = builder.actionBtnClickListener;
    }

    public void show(){
        show(fragmentManager, "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = BottomSheetAlertBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setCanceledOnTouchOutside(cancelable);
        mBinding.tvMessage.setText(message);
        if (actionBtnText != null){
            mBinding.btnAction.setText(actionBtnText);

            if (actionBtnClickListener != null){
                mBinding.btnAction.setOnClickListener(actionBtnClickListener);
                mBinding.btnAction.setFocusableInTouchMode(true);
                mBinding.btnAction.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus){
                        v.callOnClick();
                    }
                    Chords.applicationHandler.postDelayed(this::dismiss, 300);
                });
            }
            else{
                mBinding.btnAction.setOnClickListener(v -> dismiss());
            }
        }
        else{
            mBinding.btnAction.setVisibility(View.GONE);
        }
    }
}
