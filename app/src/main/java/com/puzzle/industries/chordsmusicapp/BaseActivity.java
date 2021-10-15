package com.puzzle.industries.chordsmusicapp;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.puzzle.industries.chordsmusicapp.bottom_sheets.AlertBottomSheet;
import com.puzzle.industries.chordsmusicapp.services.IPermissionService;
import com.puzzle.industries.chordsmusicapp.services.impl.PermissionService;

public abstract class BaseActivity extends AppCompatActivity implements IPermissionService {

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

}
