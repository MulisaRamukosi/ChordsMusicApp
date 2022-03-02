package com.puzzle.industries.chordsmusicapp.services.impl;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.ChordsMusicDB;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.services.IMediaBroadCastService;
import com.puzzle.industries.chordsmusicapp.services.IMediaDeleteService;
import com.puzzle.industries.chordsmusicapp.services.IMediaFileManagerService;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;

import java.util.List;

public class MediaDeleteService implements IMediaDeleteService {

    private static MediaDeleteService instance;
    private final ChordsMusicDB DATABASE = Chords.getDatabase();
    private final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();
    private final IMediaFileManagerService FILE_MANAGER = MediaFileManagerService.getInstance();
    private final IMediaBroadCastService MEDIA_BROADCAST = MediaBroadCastService.getInstance();

    public static MediaDeleteService getInstance() {
        if (instance == null) {
            synchronized (MediaDeleteService.class) {
                if (instance == null) {
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
        DATABASE.trackInfoDao().deleteTrackInfo(track.getId());
        DATABASE.trackLyricsDao().deleteTrackLyrics(track.getId());
        if (affectedRows > 0) {
            final boolean fileIsDeleted = FILE_MANAGER.deleteFile(track.getFileName());
            if (fileIsDeleted && MUSIC_LIBRARY.removeSong(track)) {
                MEDIA_BROADCAST.songRemoved(track);
                return true;
            }
        }

        return false;
    }

    private boolean deletePlaylistTrack(int playlistId, int playlistTrackId) {
        final PlaylistTrackEntity playlistTrack = MUSIC_LIBRARY.getPlaylistTrackByIds(playlistId, playlistTrackId);
        final int affectedRows = DATABASE.playlistTrackDao().delete(playlistTrack);
        if (affectedRows > 0) {
            if (MUSIC_LIBRARY.removePlaylistTrack(playlistTrack)) {
                MEDIA_BROADCAST.playlistTrackRemoved(playlistTrack);
                return true;
            }
        }
        return false;
    }

    private boolean deletePlaylistTracksFromDb(List<PlaylistTrackEntity> playlistTracks) {
        if (playlistTracks.isEmpty()) {
            MEDIA_BROADCAST.playlistTracksRemoved();
            return true;
        }

        final int affectedRows = DATABASE.playlistTrackDao().deleteMany(playlistTracks);
        if (affectedRows > 0) {
            for (final PlaylistTrackEntity playlistTrack : playlistTracks) {
                if (!MUSIC_LIBRARY.removePlaylistTrack(playlistTrack)) return false;
            }
            MEDIA_BROADCAST.playlistTracksRemoved();
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteAlbum(int albumId) {
        final AlbumArtistEntity album = MUSIC_LIBRARY.getAlbumById(albumId);
        final List<TrackArtistAlbumEntity> albumSongs = MUSIC_LIBRARY.getAlbumSongs(album.getId());
        for (TrackArtistAlbumEntity song : albumSongs) {
            if (!deleteSong(song.getId())) return false;
        }
        final int affectedRows = DATABASE.albumDao().delete(album.getId());
        if (affectedRows > 0 && MUSIC_LIBRARY.removeAlbum(album)) {
            MEDIA_BROADCAST.albumRemoved(album);
            return true;
        }

        return false;
    }

    @Override
    public boolean deletePlaylist(int playlistId) {
        final PlaylistEntity playlist = MUSIC_LIBRARY.getPlaylistById(playlistId);
        final List<PlaylistTrackEntity> playlistSongs = MUSIC_LIBRARY.getPlaylistTrackEntityList(playlist.getId());
        for (final PlaylistTrackEntity playlistSong : playlistSongs) {
            if (!deletePlaylistTrack(playlistId, playlistSong.getId())) return false;
        }
        final int affectedRows = DATABASE.playlistDao().delete(playlist);
        if (affectedRows > 0 && MUSIC_LIBRARY.removePlaylist(playlist)) {
            MEDIA_BROADCAST.playlistRemoved(playlist);
            return true;
        }
        return false;
    }

    @Override
    public boolean deletePlaylistTracks(List<PlaylistTrackEntity> playlistTracks) {
        return deletePlaylistTracksFromDb(playlistTracks);
    }

    @Override
    public boolean deleteArtist(int artistId) {
        final ArtistEntity artist = MUSIC_LIBRARY.getArtistById(artistId);
        final List<AlbumArtistEntity> albums = MUSIC_LIBRARY.getArtistAlbums(artist.getId());
        for (AlbumArtistEntity album : albums) {
            if (!deleteAlbum(album.getId())) return false;
        }
        final int affectedRows = DATABASE.artistDao().delete(artist.getId());
        if (affectedRows > 0 && MUSIC_LIBRARY.removeArtist(artist)) {
            MEDIA_BROADCAST.artistRemoved(artist);
            return true;
        }
        return false;
    }


}
