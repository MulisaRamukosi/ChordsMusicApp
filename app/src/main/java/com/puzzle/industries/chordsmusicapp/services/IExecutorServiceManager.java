package com.puzzle.industries.chordsmusicapp.services;

public interface IExecutorServiceManager {

    void executeRunnableOnSingeThread(Runnable runnable);
}
