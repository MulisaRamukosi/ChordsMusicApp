package com.puzzle.industries.chordsmusicapp.helpers;

import com.puzzle.industries.chordsmusicapp.models.dataModels.AlbumDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;

import java.util.Locale;

public class SongFileNameHelper {

    private static final String[] charactersToBeRemoved = new String[]{
            "#", "<", "$", "%", ">", "!", "&", "*", "'", "{", "}", "?", "\"",
            "/", ":", "\\", "@", "+", "`", "|", "="
    };

    public static String generateSongFileName(SongDataStruct song){
        final AlbumDataStruct album = song.getAlbum();
        return generateFileName(song.getArtist().getName(), album.getTitle(), song.getSongName(), album.getId());
    }

    public static String generateSongFileName(String artistName, String albumName, String songName, int albumId){
        return generateFileName(artistName, albumName, songName, albumId);
    }

    private static String generateFileName(String artistName, String albumName, String songName, int albumId){
        String fileName = String.format(Locale.getDefault(), "%s_%s_%s_%d.mp3", artistName,
                albumName, songName, albumId)
                .replace(' ', '_');
        for (String c : charactersToBeRemoved){
            fileName = fileName.replace(c, "");
        }
        return fileName;
    }
}
