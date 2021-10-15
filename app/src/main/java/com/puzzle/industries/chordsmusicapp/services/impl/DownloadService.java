package com.puzzle.industries.chordsmusicapp.services.impl;

import android.util.Log;
import android.widget.Toast;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.callbacks.DownloadProgressCallback;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.services.IDownloadService;
import com.puzzle.industries.chordsmusicapp.services.IMediaFileManagerService;
import com.puzzle.industries.chordsmusicapp.services.impl.MediaFileManagerService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;

public class DownloadService implements IDownloadService {

    private final String TAG = "DOWN_SERV";
    private final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private final IMediaFileManagerService MEDIA_FILE_MANAGER = new MediaFileManagerService();

    private HttpURLConnection mConnection;

    @Override
    public void downloadSong(SongDataStruct song, String fileUrl, DownloadProgressCallback callback){
        final String fileName = String.format("%s_%s.mp3", song.getArtist().getName(), song.getSongName());

        if (!MEDIA_FILE_MANAGER.fileExists(fileName) && MEDIA_FILE_MANAGER.createFile(fileName)){

            Chords.applicationHandler.post(() -> Toast.makeText(Chords.getAppContext(),
                    String.format("Downloading %s", fileName), Toast.LENGTH_SHORT).show());

            EXECUTOR_SERVICE.execute(() -> {
                int count;

                try {
                    final URL url = new URL(fileUrl);
                    mConnection = (HttpURLConnection) url.openConnection();
                    mConnection.connect();

                    final int fileLength = mConnection.getContentLength();
                    final InputStream input = mConnection.getInputStream();
                    final OutputStream output = new FileOutputStream(MEDIA_FILE_MANAGER.getFile(fileName));
                    final byte[] data = new byte[1024];
                    long total = 0;

                    while ((count = input.read(data)) != -1){
                        total += count;
                        final int progress = Integer.parseInt("" + (int)((total * 100)/ fileLength));
                        callback.updateProgress(progress);
                        Log.d(TAG, String.valueOf(progress));
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
        else{
            Chords.applicationHandler.post(() ->
                    Toast.makeText(Chords.getAppContext(), String.format("%s already exists.", fileName),
                            Toast.LENGTH_SHORT).show());
        }

    }
}
