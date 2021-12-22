package com.puzzle.industries.chordsmusicapp.helpers;

import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;

public class SongFileNameHelper {

    public static String generateSongFileName(SongDataStruct song){
        return String.format("%s_%s_%s.mp3", song.getArtist().getName(),
                song.getAlbum().getTitle(), song.getSongName()).replace(' ', '_');
    }
}
