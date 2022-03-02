package com.puzzle.industries.chordsmusicapp.remote.musicFinder;

public interface RetryPolicyListener {
    void onRetryPolicy();

    void retryAttemptsFinished();
}
