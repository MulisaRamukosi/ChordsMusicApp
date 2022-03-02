package com.puzzle.industries.chordsmusicapp.services.impl;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.services.IMediaFileManagerService;

import java.io.File;
import java.io.IOException;

public class MediaFileManagerService implements IMediaFileManagerService {

    private static MediaFileManagerService instance;
    private final File MEDIA_FOLDER = Chords.getAppContext().getExternalMediaDirs()[0];

    public static MediaFileManagerService getInstance() {
        if (instance == null) {
            synchronized (MediaFileManagerService.class) {
                if (instance == null) {
                    instance = new MediaFileManagerService();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean createFile(String fileName) {
        final File songFile = new File(MEDIA_FOLDER, fileName);
        if (!songFile.exists()) {
            try {
                return songFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean deleteFile(String fileName) {
        final File songFile = new File(MEDIA_FOLDER, fileName);
        if (songFile.exists()) {
            return songFile.delete();
        }
        return false;
    }

    @Override
    public boolean fileExists(String fileName) {
        final File songFile = new File(MEDIA_FOLDER, fileName);
        return songFile.exists();
    }

    @Override
    public String getFilePath(String fileName) {
        return getFile(fileName).getAbsolutePath();
    }

    @Override
    public File getFile(String fileName) {
        return new File(MEDIA_FOLDER, fileName);
    }
}
