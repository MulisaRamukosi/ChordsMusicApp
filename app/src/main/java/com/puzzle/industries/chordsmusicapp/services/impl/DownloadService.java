package com.puzzle.industries.chordsmusicapp.services.impl;

import com.puzzle.industries.chordsmusicapp.callbacks.DownloadProgressCallback;
import com.puzzle.industries.chordsmusicapp.callbacks.OverrideDownloadProgressCallback;
import com.puzzle.industries.chordsmusicapp.helpers.SongFileNameHelper;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.services.IDownloadService;
import com.puzzle.industries.chordsmusicapp.services.IMediaFileManagerService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService implements IDownloadService {

    private final IMediaFileManagerService MEDIA_FILE_MANAGER = MediaFileManagerService.getInstance();

    @Override
    public void downloadSong(SongDataStruct song, String fileUrl, DownloadProgressCallback callback){
        final String fileName = SongFileNameHelper.generateSongFileName(song);

        if(MEDIA_FILE_MANAGER.fileExists(fileName) && !MusicLibraryService.getInstance().containsSong(song.getId())){
            //song file exists but was not saved to db due to some error
            MEDIA_FILE_MANAGER.deleteFile(fileName);
        }

        if (!MEDIA_FILE_MANAGER.fileExists(fileName) && MEDIA_FILE_MANAGER.createFile(fileName)){
            int count;

            try {
                final URL url = new URL(fileUrl);
                final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                final int fileLength = httpURLConnection.getContentLength();
                final InputStream input = httpURLConnection.getInputStream();
                final OutputStream output = new FileOutputStream(MEDIA_FILE_MANAGER.getFile(fileName));
                final byte[] data = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1){
                    total += count;
                    final int progress = Integer.parseInt("" + (int)((total * 100)/ fileLength));
                    callback.updateProgress(progress);
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                callback.downloadComplete(song);
            }
            catch (IOException e) {
                e.printStackTrace();
                callback.downloadFailed();
            }
        }
        else{
            callback.downloadComplete(song);
        }

    }

    @Override
    public void downloadSong(String fileName, String fileUrl, OverrideDownloadProgressCallback callback) {
        if (!MEDIA_FILE_MANAGER.fileExists(fileName) && MEDIA_FILE_MANAGER.createFile(fileName)){
            int count;

            try {
                final URL url = new URL(fileUrl);
                final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                final int fileLength = httpURLConnection.getContentLength();
                final InputStream input = httpURLConnection.getInputStream();
                final OutputStream output = new FileOutputStream(MEDIA_FILE_MANAGER.getFile(fileName));
                final byte[] data = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1){
                    total += count;
                    final int progress = Integer.parseInt("" + (int)((total * 100)/ fileLength));
                    callback.updateProgress(progress);
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                callback.downloadComplete();
            }
            catch (IOException e) {
                e.printStackTrace();
                callback.downloadFailed();
            }
        }
        else{
            callback.downloadComplete();
        }
    }

}
