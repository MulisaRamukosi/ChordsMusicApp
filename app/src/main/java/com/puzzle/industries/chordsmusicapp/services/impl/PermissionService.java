package com.puzzle.industries.chordsmusicapp.services.impl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.puzzle.industries.chordsmusicapp.services.IPermissionService;

public class PermissionService implements IPermissionService {

    private static final PermissionService instance = new PermissionService();

    private final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private final int REQUEST_CODE = 1906;

    public static PermissionService getInstance() {
        return instance;
    }

    @Override
    public void requestPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, PERMISSIONS, REQUEST_CODE);
    }

    @Override
    public boolean isPermissionsGranted(Activity activity) {
        for (String permission : PERMISSIONS){
            if (ActivityCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isAllRequestedPermissionsGranted(int requestCode, int[] grantResults) {
        if(requestCode == REQUEST_CODE){
            for (int result : grantResults){
                if (result != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
