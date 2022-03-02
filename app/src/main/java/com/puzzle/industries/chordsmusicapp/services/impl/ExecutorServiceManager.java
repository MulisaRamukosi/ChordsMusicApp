package com.puzzle.industries.chordsmusicapp.services.impl;

import com.puzzle.industries.chordsmusicapp.services.IExecutorServiceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceManager implements IExecutorServiceManager {

    private static final ExecutorServiceManager instance = new ExecutorServiceManager();

    public static ExecutorServiceManager getInstance(){
        return instance;
    }

    @Override
    public void executeRunnableOnSingeThread(Runnable runnable) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(runnable);
        executorService.shutdown();
    }
}
