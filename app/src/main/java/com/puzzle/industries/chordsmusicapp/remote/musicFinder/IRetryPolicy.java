package com.puzzle.industries.chordsmusicapp.remote.musicFinder;

public interface IRetryPolicy {
    void startRetryPolicy();
    void stopRetryPolicy();
    boolean allAttemptsUsed();
}
