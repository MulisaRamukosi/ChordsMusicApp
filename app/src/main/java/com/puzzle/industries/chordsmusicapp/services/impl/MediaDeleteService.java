package com.puzzle.industries.chordsmusicapp.services.impl;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.ChordsMusicDB;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.services.IMediaBroadCastService;
import com.puzzle.industries.chordsmusicapp.services.IMediaDeleteService;
import com.puzzle.industries.chordsmusicapp.services.IMediaFileManagerService;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;

import java.util.List;

public class MediaDeleteService implements IMediaDeleteService {

    private final ChordsMusicDB DATABASE = Chords.getDatabase();
    private final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();
    private final IMediaFileManagerService FILE_MANAGER = MediaFileManagerService.getInstance();
    private final IMediaBroadCastService MEDIA_BROADCAST = MediaBroadCastService.getInstance();

    private static MediaDeleteService instance;

    public static MediaDeleteService getInstance(){
        if (instance == null){
            synchronized (MediaDeleteService.class){
                if (instance == null){
                    instance = new MediaDeleteService();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean deleteSong(int songId) {
        final TrackArtistAlbumEntity track = MUSIC_LIBRARY.getSongById(songId);
        final int affectedRows = DATABASE.trackDao().delete(track.getId());
        if (affectedRows > 0){
            final boolean fileIsDeleted = FILE_MANAGER.deleteFile(track.getFileName());
            if (fileIsDeleted && MUSIC_LIBRARY.removeSong(track)){
                MEDIA_BROADCAST.songRemoved(track);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean deleteAlbum(int albumId) {
        final AlbumArtistEntity album = MUSIC_LIBRARY.getAlbumById(albumId);
        final List<TrackArtistAlbumEntity> albumSongs = MUSIC_LIBRARY.getAlbumSongs(album.getId());
        for (TrackArtistAlbumEntity song : albumSongs){
            if (!deleteSong(song.getId())) return false;
        }
        final int affectedRows = DATABASE.albumDao().delete(album.getId());
        if (affectedRows > 0 && MUSIC_LIBRARY.removeAlbum(album)){
            MEDIA_BROADCAST.albumRemoved(album);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteArtist(int artistId) {
        final ArtistEntity artist = MUSIC_LIBRARY.getArtistById(artistId);
        final List<AlbumArtistEntity> albums = MUSIC_LIBRARY.getArtistAlbums(artist.getId());
        for (AlbumArtistEntity album : albums){
            if (!deleteAlbum(album.getId())) return false;
        }
        final int affectedRows = DATABASE.artistDao().delete(artist.getId());
        if (affectedRows > 0 && MUSIC_LIBRARY.removeArtist(artist)){
            MEDIA_BROADCAST.artistRemoved(artist);
            return true;
        }
        return false;
    }
}
