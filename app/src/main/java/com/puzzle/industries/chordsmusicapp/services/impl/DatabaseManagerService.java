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
import com.puzzle.industries.chordsmusicapp.helpers.SongFileNameHelper;
import com.puzzle.industries.chordsmusicapp.models.dataModels.AlbumDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.ArtistDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.services.IDatabaseManagerService;
import com.puzzle.industries.chordsmusicapp.services.IMediaBroadCastService;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;

public class DatabaseManagerService implements IDatabaseManagerService {

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
        final String fileName = SongFileNameHelper.generateSongFileName(downloadedSongInfo);
        final String songLocation = MediaFileManagerService.getInstance().getFilePath(fileName);

        final TrackEntity track = new TrackEntity(
                downloadedSongInfo.getId(),
                downloadedSongInfo.getSongName(),
                downloadedSongInfo.getTrack_position(),
                downloadedSongInfo.getRelease_date(),
                songLocation,
                artistDataStruct.getId(),
                albumDataStruct.getId()
        );

        final ArtistEntity artist = new ArtistEntity(
                artistDataStruct.getId(),
                artistDataStruct.getName(),
                artistDataStruct.getPicture_big()
        );

        final AlbumEntity album = new AlbumEntity(
                albumDataStruct.getId(),
                albumDataStruct.getTitle(),
                albumDataStruct.getCover_big(),
                albumDataStruct.getRelease_date(),
                artistDataStruct.getId()
        );

        ARTIST_DAO.insert(artist);
        ALBUM_DAO.insert(album);
        TRACK_DAO.insert(track);

        final IMusicLibraryService libraryService = MusicLibraryService.getInstance();
        final IMediaBroadCastService mediaBroadcastReceiver = MediaBroadCastService.getInstance();

        final AlbumArtistEntity albumArtist = new AlbumArtistEntity(
                albumDataStruct.getId(),
                albumDataStruct.getTitle(),
                albumDataStruct.getCover_big(),
                artist.getId(),
                artist.getName()
        );

        final TrackArtistAlbumEntity trackArtistAlbum = new TrackArtistAlbumEntity(
                track.getId(),
                track.getTitle(),
                track.getTrack_number(),
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
