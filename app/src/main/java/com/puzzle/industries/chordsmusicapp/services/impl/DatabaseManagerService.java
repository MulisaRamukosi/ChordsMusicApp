package com.puzzle.industries.chordsmusicapp.services.impl;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.dao.AlbumDao;
import com.puzzle.industries.chordsmusicapp.database.dao.ArtistDao;
import com.puzzle.industries.chordsmusicapp.database.dao.TrackDao;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackEntity;
import com.puzzle.industries.chordsmusicapp.models.dataModels.AlbumDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.ArtistDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.services.IDatabaseManagerService;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DatabaseManagerService implements IDatabaseManagerService {

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    private final TrackDao TRACK_DAO = Chords.getDatabase().trackDao();
    private final ArtistDao ARTIST_DAO = Chords.getDatabase().artistDao();
    private final AlbumDao ALBUM_DAO = Chords.getDatabase().albumDao();

    private static DatabaseManagerService instance;

    public static DatabaseManagerService getInstance() {
        if (instance == null){
            synchronized (DownloadManagerService.class){
                if (instance == null){
                    instance = new DatabaseManagerService();
                }
            }
        }
        return instance;
    }

    @Override
    public void saveSongToDb(SongDataStruct downloadedSongInfo) {
        final ArtistDataStruct artistDataStruct = downloadedSongInfo.getArtist();
        final AlbumDataStruct albumDataStruct = downloadedSongInfo.getAlbum();
        final String fileName = String.format("%s_%s.mp3", artistDataStruct.getName(), downloadedSongInfo.getSongName());
        final String songLocation = MediaFileManagerService.getInstance().getFilePath(fileName);

        String trackReleaseDate = "";
        if (downloadedSongInfo.getRelease_date() != null){
            trackReleaseDate = DATE_FORMAT.format(downloadedSongInfo.getRelease_date());
        }

        TrackEntity track = new TrackEntity(
                downloadedSongInfo.getId(),
                downloadedSongInfo.getSongName(),
                downloadedSongInfo.getDisk_number(),
                trackReleaseDate,
                songLocation,
                artistDataStruct.getId(),
                albumDataStruct.getId()
        );

        ArtistEntity artist = new ArtistEntity(
                artistDataStruct.getId(),
                artistDataStruct.getName(),
                artistDataStruct.getPicture_big()
        );

        String albumReleaseDate = "";
        if (albumDataStruct.getRelease_date() != null){
            albumReleaseDate = DATE_FORMAT.format(albumDataStruct.getRelease_date());
        }

        AlbumEntity album = new AlbumEntity(
                albumDataStruct.getId(),
                albumDataStruct.getTitle(),
                albumDataStruct.getCover_big(),
                albumReleaseDate,
                artistDataStruct.getId()
        );

        ARTIST_DAO.insert(artist);
        ALBUM_DAO.insert(album);
        TRACK_DAO.insert(track);

        final MusicLibraryService libraryService = MusicLibraryService.getInstance();

        final AlbumArtistEntity albumArtist = new AlbumArtistEntity(
                albumDataStruct.getId(),
                albumDataStruct.getTitle(),
                albumDataStruct.getCover_big(),
                artist.getName()
        );

        final TrackArtistAlbumEntity trackArtistAlbum = new TrackArtistAlbumEntity(
                track.getId(),
                track.getTitle(),
                track.getLocation(),
                artist.getName(),
                artist.getId(),
                album.getId(),
                album.getCover_url(),
                artist.getPicture_url());

        libraryService.addArtist(artist);
        libraryService.addAlbum(albumArtist);
        libraryService.addSong(trackArtistAlbum);

        mediaBroadcastReceiver.songAdded(trackArtistAlbum);
        mediaBroadcastReceiver.albumAdded(albumArtist);
        mediaBroadcastReceiver.artistAdded(artist);
    }
}
