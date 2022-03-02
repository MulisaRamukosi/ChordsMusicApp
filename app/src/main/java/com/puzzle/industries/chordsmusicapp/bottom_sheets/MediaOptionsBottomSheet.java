package com.puzzle.industries.chordsmusicapp.bottom_sheets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.activities.OverrideSongActivity;
import com.puzzle.industries.chordsmusicapp.activities.SelectPlaylistActivity;
import com.puzzle.industries.chordsmusicapp.base.BaseBottomSheetDialogFragment;
import com.puzzle.industries.chordsmusicapp.callbacks.PlaylistCallback;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.BottomSheetMediaOptionsBinding;
import com.puzzle.industries.chordsmusicapp.events.PlaySongEvent;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.IPlaylistService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.PlaylistService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.workers.DeleteAlbumWorker;
import com.puzzle.industries.chordsmusicapp.workers.DeleteArtistWorker;
import com.puzzle.industries.chordsmusicapp.workers.DeletePlaylistWorker;
import com.puzzle.industries.chordsmusicapp.workers.DeleteSongWorker;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MediaOptionsBottomSheet<T> extends BaseBottomSheetDialogFragment implements ActivityResultCallback<PlaylistEntity>, PlaylistCallback {

    private final T mMediaItem;
    private final List<Integer> mSongIds;
    private final IPlaylistService PLAYLIST_SERVICE = PlaylistService.getInstance(this, this);
    private final ActivityResultContract<Void, PlaylistEntity> SELECT_PLAYLIST_CONTRACT = new ActivityResultContract<Void, PlaylistEntity>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void input) {
            return new Intent(context, SelectPlaylistActivity.class);
        }

        @Override
        public PlaylistEntity parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == Activity.RESULT_OK && intent != null) {
                return intent.getParcelableExtra(Constants.KEY_PLAYLIST);
            }
            return null;
        }
    };
    private BottomSheetMediaOptionsBinding mBinding;
    private IMusicLibraryService mMusicLibrary;
    private ActivityResultLauncher<Void> mSelectPlaylistLauncher;

    public MediaOptionsBottomSheet(T mediaItem, List<Integer> songIds) {
        this.mMediaItem = mediaItem;
        this.mSongIds = songIds;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).setCanceledOnTouchOutside(false);
        mBinding = BottomSheetMediaOptionsBinding.inflate(inflater, container, false);
        mMusicLibrary = MusicLibraryService.getInstance();
        mSelectPlaylistLauncher = registerForActivityResult(SELECT_PLAYLIST_CONTRACT, this);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initTitle();
        initOptions();
    }

    private void initTitle() {
        if (mMediaItem instanceof TrackArtistAlbumEntity) {
            mBinding.tvTitle.setText(String.format("Song: %s", ((TrackArtistAlbumEntity) mMediaItem).getTitle()));
        } else if (mMediaItem instanceof AlbumArtistEntity) {
            mBinding.tvTitle.setText(String.format("Album: %s", ((AlbumArtistEntity) mMediaItem).getTitle()));
        } else if (mMediaItem instanceof ArtistEntity) {
            mBinding.tvTitle.setText(String.format("Artist: %s", ((ArtistEntity) mMediaItem).getName()));
        } else if (mMediaItem instanceof PlaylistEntity) {
            mBinding.tvTitle.setText(String.format("Playlist: %s", ((PlaylistEntity) mMediaItem).getName()));
        }
    }

    private void initOptions() {
        mBinding.btnPlay.setOnClickListener(v -> play());
        mBinding.btnAddToQueue.setOnClickListener(v -> addToQueue());
        mBinding.btnDelete.setOnClickListener(v -> delete());
        mBinding.btnAddToPlaylist.setOnClickListener(v -> addToPlaylist());

        if (mMediaItem instanceof TrackArtistAlbumEntity) {
            mBinding.btnOverride.setVisibility(View.VISIBLE);
            mBinding.btnOverride.setOnClickListener(v -> overrideTrack());
        }
    }

    private void overrideTrack() {
        final TrackArtistAlbumEntity track = (TrackArtistAlbumEntity) mMediaItem;
        showAlert(getString(R.string.desc_override), true, getString(R.string.txt_continue), v -> {
            final Intent i = new Intent(v.getContext(), OverrideSongActivity.class);
            i.putExtra(Constants.KEY_SONG, track);
            startActivity(i);
            dismiss();
        });
    }

    private void addToQueue() {
        if (mMediaItem instanceof TrackArtistAlbumEntity) {
            final TrackArtistAlbumEntity track = (TrackArtistAlbumEntity) mMediaItem;
            addSongToQueue(track.getId());
        } else addSongsToQueue(mSongIds);
    }

    private void addToPlaylist() {
        mSelectPlaylistLauncher.launch(null);
    }

    private void play() {
        int songId;
        if (mMediaItem instanceof TrackArtistAlbumEntity) {
            final TrackArtistAlbumEntity track = (TrackArtistAlbumEntity) mMediaItem;
            songId = track.getId();
        } else {
            songId = mSongIds.get(0);
        }

        EventBus.getDefault().post(new PlaySongEvent(songId, mSongIds));
        dismiss();
    }

    private void delete() {
        if (mMediaItem instanceof TrackArtistAlbumEntity) {
            final TrackArtistAlbumEntity track = (TrackArtistAlbumEntity) mMediaItem;
            showAlert(getString(R.string.warning_delete_song, track.getTitle()), true, getString(R.string.action_yes_delete), v -> deleteSong(track));
        } else if (mMediaItem instanceof AlbumArtistEntity) {
            final AlbumArtistEntity album = (AlbumArtistEntity) mMediaItem;
            showAlert(getString(R.string.warning_delete_album, album.getName()), true, getString(R.string.action_yes_delete), v -> deleteAlbum(album));
        } else if (mMediaItem instanceof ArtistEntity) {
            final ArtistEntity artist = (ArtistEntity) mMediaItem;
            showAlert(getString(R.string.warning_delete_artist, artist.getName()), true, getString(R.string.action_yes_delete), v -> deleteArtist(artist));
        } else if (mMediaItem instanceof PlaylistEntity) {
            final PlaylistEntity playlist = (PlaylistEntity) mMediaItem;
            showAlert(getString(R.string.warning_delete_playlist, playlist.getName()), true, getString(R.string.action_yes_delete), v -> deletePlaylist(playlist));
        }
    }

    private void deletePlaylist(PlaylistEntity playlist) {
        setAsActiveDeleting(false);
        executeDeleteRequest(DeletePlaylistWorker.class, playlist.getId(), new DeleteMediaCallback() {
            @Override
            public void deleteSuccess() {
                dismiss();
            }

            @Override
            public void deleteFailed() {
                setAsActive(true);
                showAlert(getString(R.string.error_delete_playlist), true, getString(R.string.okay), null);
            }
        });
    }

    private void deleteSong(TrackArtistAlbumEntity track) {
        setAsActiveDeleting(false);
        executeDeleteRequest(DeleteSongWorker.class, track.getId(), new DeleteMediaCallback() {
            @Override
            public void deleteSuccess() {
                final int albumId = track.getAlbum_id();
                final List<TrackArtistAlbumEntity> albumSongs = mMusicLibrary.getAlbumSongs(albumId);
                if (albumSongs.size() > 0) {
                    dismiss();
                } else {
                    deleteAlbum(mMusicLibrary.getAlbumById(albumId));
                }
            }

            @Override
            public void deleteFailed() {
                setAsActiveDeleting(true);
                showAlert(getString(R.string.error_delete_song), true, getString(R.string.okay), null);
            }
        });
    }

    private void deleteAlbum(AlbumArtistEntity album) {
        setAsActiveDeleting(false);
        executeDeleteRequest(DeleteAlbumWorker.class, album.getId(), new DeleteMediaCallback() {
            @Override
            public void deleteSuccess() {
                final int artistId = album.getArtist_id();
                final List<AlbumArtistEntity> artistAlbums = mMusicLibrary.getArtistAlbums(artistId);
                if (artistAlbums.size() > 0) {
                    dismiss();
                } else {
                    deleteArtist(mMusicLibrary.getArtistById(artistId));
                }
            }

            @Override
            public void deleteFailed() {
                setAsActiveDeleting(true);
                showAlert(getString(R.string.error_delete_album), true, getString(R.string.okay), null);
            }
        });
    }

    private void deleteArtist(ArtistEntity artist) {
        setAsActiveDeleting(false);
        executeDeleteRequest(DeleteArtistWorker.class, artist.getId(), new DeleteMediaCallback() {
            @Override
            public void deleteSuccess() {
                dismiss();
            }

            @Override
            public void deleteFailed() {
                setAsActiveDeleting(true);
                showAlert(getString(R.string.error_delete_artist), true, getString(R.string.okay), null);
            }
        });
    }

    private void executeDeleteRequest(Class<? extends ListenableWorker> worker, int mediaId, DeleteMediaCallback callback) {
        final WorkRequest deleteRequest = new OneTimeWorkRequest.Builder(worker)
                .setInputData(new Data.Builder().putInt(Constants.KEY_MEDIA_ID, mediaId).build())
                .build();

        final WorkManager workManager = WorkManager.getInstance(requireContext());
        workManager.enqueue(deleteRequest);

        workManager.getWorkInfoByIdLiveData(deleteRequest.getId()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                callback.deleteSuccess();
            } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                callback.deleteFailed();
            }
        });

    }

    private void showToastNotification(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void addSongToQueue(int songId) {
        mMusicLibrary.addSongToQueue(songId);
        showToastNotification(getString(R.string.msg_song_s_added_to_queue));
        dismiss();
    }

    private void addSongsToQueue(List<Integer> songIds) {
        mMusicLibrary.addSongsToQueue(songIds);
        showToastNotification(getString(R.string.msg_song_s_added_to_queue));
        dismiss();
    }

    @Override
    public void onActivityResult(PlaylistEntity playlistEntity) {
        if (playlistEntity != null) {
            if (mMediaItem instanceof TrackArtistAlbumEntity) {
                final TrackArtistAlbumEntity track = (TrackArtistAlbumEntity) mMediaItem;
                addSongToPlaylist(playlistEntity, track.getId());

            } else addSongsToPlaylist(playlistEntity, mSongIds);
        }
    }

    private void addSongToPlaylist(PlaylistEntity playlistEntity, int songId) {
        setAsActiveAddToPlaylist(false);
        PLAYLIST_SERVICE.addSongToPlaylist(playlistEntity.getId(), songId);
    }

    private void addSongsToPlaylist(PlaylistEntity playlist, List<Integer> songIds) {
        PLAYLIST_SERVICE.addSongsToPlaylist(playlist.getId(), songIds.stream().map(songId -> new PlaylistTrackEntity(0, playlist.getId(), songId))
                .collect(Collectors.toList()));
    }

    private void setAsActiveDeleting(boolean isActive) {
        setAsActive(isActive);
        mBinding.btnDelete.setText(getString(!isActive ? R.string.deleting : R.string.delete));
        mBinding.lpiDelete.setVisibility(isActive ? View.GONE : View.VISIBLE);
    }

    private void setAsActiveAddToPlaylist(boolean isActive) {
        setAsActive(isActive);
        mBinding.btnAddToPlaylist.setText(getString(!isActive ? R.string.adding_to_playlist : R.string.add_to_playlist));
        mBinding.lpiAddToPlaylist.setVisibility(isActive ? View.GONE : View.VISIBLE);
    }

    @Override
    public void playlistCreated(PlaylistEntity playlistEntity) {

    }

    @Override
    public void playlistUpdated() {

    }

    @Override
    public void operationFailed() {

    }

    @Override
    public void songsAddedToPlaylist() {
        requireActivity().runOnUiThread(() -> {
            setAsActiveAddToPlaylist(true);
            dismiss();
            showAlert("Song(s) successfully added to playlist", false, getString(R.string.okay), null);
        });
    }

    private void setAsActive(boolean isActive) {
        mBinding.btnPlay.setEnabled(isActive);
        mBinding.btnAddToQueue.setEnabled(isActive);
        mBinding.btnAddToPlaylist.setEnabled(isActive);
        mBinding.btnDelete.setEnabled(isActive);
    }

    private interface DeleteMediaCallback {
        void deleteSuccess();

        void deleteFailed();
    }
}
