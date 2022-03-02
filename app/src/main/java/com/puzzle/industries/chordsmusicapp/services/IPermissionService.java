package com.puzzle.industries.chordsmusicapp.services;

import android.app.Activity;

public interface IPermissionService {

    void requestPermissions(Activity activity);

    boolean isPermissionsGranted(Activity activity);

    boolean isAllRequestedPermissionsGranted(int requestCode, int[] grantResults);

}
