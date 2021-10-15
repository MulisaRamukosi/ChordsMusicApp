package com.puzzle.industries.chordsmusicapp.services;

import java.io.File;

public interface IMediaFileManagerService {

    boolean createFile(String fileName);
    boolean deleteFile(String fileName);
    boolean fileExists(String fileName);
    String getFilePath(String fileName);
    File getFile(String fileName);
}
