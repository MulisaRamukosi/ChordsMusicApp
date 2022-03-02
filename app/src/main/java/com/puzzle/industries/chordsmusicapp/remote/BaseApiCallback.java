package com.puzzle.industries.chordsmusicapp.remote;

import androidx.annotation.NonNull;

import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;

import lombok.AllArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AllArgsConstructor
public class BaseApiCallback<T> implements Callback<T> {

    ApiCallBack<T> callBack;

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.isSuccessful() && response.body() != null) {
            callBack.onSuccess(response.body());
        } else {
            callBack.onFailure(new Throwable("Failed to get results"));
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        callBack.onFailure(t);
    }
}
