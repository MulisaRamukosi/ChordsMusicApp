package com.puzzle.industries.chordsmusicapp.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.puzzle.industries.chordsmusicapp.bottom_sheets.AlertBottomSheet;
import com.puzzle.industries.chordsmusicapp.services.IMusicPlayerService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicPlayerService;

public abstract class BaseFragment extends Fragment implements ServiceConnection {

    private IMusicPlayerService mMusicPlayerService;

    protected IMusicPlayerService getMusicPlayerService(){
        return mMusicPlayerService;
    }

    protected void showAlert(String message, boolean cancelable, String actionText, View.OnClickListener onClickListener){
        new AlertBottomSheet.AlertBottomSheetBuilder(getParentFragmentManager())
                .setMessage(message)
                .setCancelable(cancelable)
                .setAction(actionText, onClickListener)
                .build().show();
    }

    private void bindToMusicService(){
        final Intent intent = new Intent(requireContext(), MusicPlayerService.class);
        requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    private void unbindMusicService(){
        if (mMusicPlayerService != null){
            try {
                requireContext().unbindService(this);
            }
            catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindToMusicService();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbindMusicService();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        final MusicPlayerService.MusicPlayerBinder binder = (MusicPlayerService.MusicPlayerBinder) service;
        mMusicPlayerService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mMusicPlayerService = null;
    }
}
