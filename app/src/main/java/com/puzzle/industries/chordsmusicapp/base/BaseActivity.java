package com.puzzle.industries.chordsmusicapp.base;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.imageview.ShapeableImageView;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.bottom_sheets.AlertBottomSheet;
import com.puzzle.industries.chordsmusicapp.services.IMusicPlayerService;
import com.puzzle.industries.chordsmusicapp.services.IPermissionService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicPlayerService;
import com.puzzle.industries.chordsmusicapp.services.impl.PermissionService;

public abstract class BaseActivity extends AppCompatActivity implements IPermissionService, ServiceConnection {

    private IMusicPlayerService mMusicPlayerService;
    private final PermissionService PERMISSION_UTIL = PermissionService.getInstance();

    protected void showAlert(String message, boolean cancelable, String actionText, View.OnClickListener onClickListener){
        new AlertBottomSheet.AlertBottomSheetBuilder(getSupportFragmentManager())
                .setMessage(message)
                .setCancelable(cancelable)
                .setAction(actionText, onClickListener)
                .build().show();
    }

    protected IMusicPlayerService getMusicPlayerService(){
        return mMusicPlayerService;
    }

    protected void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                });
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!PERMISSION_UTIL.isAllRequestedPermissionsGranted(requestCode, grantResults)){
            showAlert(getString(R.string.error_permission_reject),
                    true, getString(R.string.request_again),
                    v -> PERMISSION_UTIL.requestPermissions(this));
        }
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

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        final MusicPlayerService.MusicPlayerBinder binder = (MusicPlayerService.MusicPlayerBinder) service;
        mMusicPlayerService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mMusicPlayerService = null;
    }

    protected void displayImageFromLink(String link, ImageView iv, boolean scheduleTrans){
        Glide.with(this).load(link)
                .apply(new RequestOptions().dontAnimate())
                .listener(getRequestListener(iv, scheduleTrans))
                .into(iv);
    }

    protected void displayImageFromDrawable(int drawableId, ImageView iv, boolean scheduleTrans) {
        Glide.with(this).load(ContextCompat.getDrawable(this, drawableId))
                .apply(new RequestOptions().dontAnimate())
                .listener(getRequestListener(iv, scheduleTrans))
                .into(iv);
    }

    private RequestListener<Drawable> getRequestListener(ImageView iv, boolean scheduleTrans){
        return new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (scheduleTrans) scheduleStartPostponedTransition(iv);
                return false;
            }
        };
    }
}
