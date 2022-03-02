package com.puzzle.industries.chordsmusicapp.remote.interfaces;

public interface ApiCallBack<T> {

    void onSuccess(T t);

    void onFailure(Throwable t);

}
