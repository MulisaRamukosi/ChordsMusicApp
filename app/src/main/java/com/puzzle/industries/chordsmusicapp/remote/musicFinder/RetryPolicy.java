package com.puzzle.industries.chordsmusicapp.remote.musicFinder;

import android.os.CountDownTimer;
import android.util.Log;

public class RetryPolicy implements IRetryPolicy{

    private int retryAttempts = 0;

    private final int waitDuration;
    private final int maxRetryAttempts;
    private final CountDownTimer countDownTimer;

    public RetryPolicy(int waitDuration, int maxRetryAttempts, RetryPolicyListener listener) {
        this.waitDuration = waitDuration * 1000;
        this.maxRetryAttempts = maxRetryAttempts;

        this.countDownTimer = new CountDownTimer(this.waitDuration, this.waitDuration) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                retryAttempts++;
                Log.d(MusicFinderApi.TAG, String.format("Retry policy just hit %d", retryAttempts));
                if (retryAttempts >= maxRetryAttempts){
                    listener.retryAttemptsFinished();
                }
                else{
                    listener.onRetryPolicy();
                }
            }
        };
    }

    @Override
    public void startRetryPolicy() {
        Log.d(MusicFinderApi.TAG, "START RETRY");
        this.countDownTimer.cancel();
        this.countDownTimer.start();
    }


    @Override
    public void stopRetryPolicy() {
        this.countDownTimer.cancel();
    }

    @Override
    public boolean allAttemptsUsed() {
        return retryAttempts >= maxRetryAttempts;
    }

}
