package com.puzzle.industries.chordsmusicapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.puzzle.industries.chordsmusicapp.bottom_sheets.AlertBottomSheet;
import com.puzzle.industries.chordsmusicapp.services.IMusicPlayerService;
import com.puzzle.industries.chordsmusicapp.services.IPermissionService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicPlayerService;
import com.puzzle.industries.chordsmusicapp.services.impl.PermissionService;

public abstract class BaseActivity extends AppCompatActivity implements IPermissionService, ServiceConnection {

    protected IMusicPlayerService mMusicPlayerService;
    private final PermissionService PERMISSION_UTIL = PermissionService.getInstance();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!PERMISSION_UTIL.isAllRequestedPermissionsGranted(requestCode, grantResults)){
            showAlert(getString(R.string.error_permission_reject),
                    true, getString(R.string.request_again),
                    v -> PERMISSION_UTIL.requestPermissions(this));
        }
    }

    protected void showAlert(String message, boolean cancelable, String actionText, View.OnClickListener onClickListener){
        new AlertBottomSheet.AlertBottomSheetBuilder(getSupportFragmentManager())
                .setMessage(message)
                .setCancelable(cancelable)
                .setAction(actionText, onClickListener)
                .build().show();
    }

    @Override
    public void requestPermissions(Activity activity){
        PERMISSION_UTIL.requestPermissions(activity);
    }

    @Override
    public boolean isPermissionsGranted(Activity activity){
        return PERMISSION_UTIL.isPermissionsGranted(activity);
    }

    @Override
    public boolean isAllRequestedPermissionsGranted(int requestCode, int[] grantResults){
        return PERMISSION_UTIL.isAllRequestedPermissionsGranted(requestCode, grantResults);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindToMusicService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindMusicService();
    }

    private void bindToMusicService(){
        final Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    private void unbindMusicService(){
        if (mMusicPlayerService != null){
            unbindService(this);
        }
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
