package com.puzzle.industries.chordsmusicapp.models.dataModels;

import com.puzzle.industries.chordsmusicapp.utils.DownloadState;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DownloadItemDataStruct {

    private final int downloadId;
    private final SongDataStruct song;
    @Setter private DownloadState downloadState;
    @Setter private int downloadProgress;

    public DownloadItemDataStruct(int downloadId, @NotNull SongDataStruct song, DownloadState downloadState){
        this.downloadId = downloadId;
        this.song = song;
        this.downloadState = downloadState;
        this.downloadProgress = 0;
    }
}
