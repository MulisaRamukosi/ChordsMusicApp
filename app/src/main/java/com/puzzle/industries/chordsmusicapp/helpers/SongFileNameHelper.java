package com.puzzle.industries.chordsmusicapp.helpers;

import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;

public class SongFileNameHelper {

    private static final String[] charactersToBeRemoved = new String[]{
            "#", "<", "$", "%", ">", "!", "&", "*", "'", "{", "}", "?", "\"",
            "/", ":", "\\", "@", "+", "`", "|", "="
    };

    public static String generateSongFileName(SongDataStruct song){
        String fileName = String.format("%s_%s_%s.mp3", song.getArtist().getName(),
                song.getAlbum().getTitle(), song.getSongName())
                .replace(' ', '_');
        for (String c : charactersToBeRemoved){
            fileName = fileName.replace(c, "");
        }
        return fileName;
    }
}
