package com.puzzle.industries.chordsmusicapp.bottom_sheets;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseBottomSheetDialogFragment;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.BottomSheetMediaOptionsBinding;
import com.puzzle.industries.chordsmusicapp.events.PlaySongEvent;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.workers.DeleteAlbumWorker;
import com.puzzle.industries.chordsmusicapp.workers.DeleteArtistWorker;
import com.puzzle.industries.chordsmusicapp.workers.DeleteSongWorker;
import com.puzzle.industries.chordsmusicapp.workers.FetchSongsWorker;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class MediaOptionsBottomSheet<T> extends BaseBottomSheetDialogFragment {

    private final T mMediaItem;
    private final List<Integer> mSongIds;
    private BottomSheetMediaOptionsBinding mBinding;
    private IMusicLibraryService mMusicLibrary;


    public MediaOptionsBottomSheet(T mediaItem, List<Integer> songIds){
        this.mMediaItem = mediaItem;
        this.mSongIds = songIds;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).setCanceledOnTouchOutside(false);
        mBinding = BottomSheetMediaOptionsBinding.inflate(inflater, container, false);
        mMusicLibrary = MusicLibraryService.getInstance();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){
        initTitle();
        initOptions();
    }

    private void initTitle(){
        if (mMediaItem instanceof TrackArtistAlbumEntity){
            mBinding.tvTitle.setText(String.format("Song: %s", ((TrackArtistAlbumEntity) mMediaItem).getTitle()));
        }
        else if (mMediaItem instanceof AlbumArtistEntity){
            mBinding.tvTitle.setText(String.format("Album: %s", ((AlbumArtistEntity) mMediaItem).getTitle()));
        }
        else if (mMediaItem instanceof ArtistEntity){
            mBinding.tvTitle.setText(String.format("Artist: %s", ((ArtistEntity) mMediaItem).getName()));
        }
    }

    private void initOptions(){
        mBinding.btnPlay.setOnClickListener(v -> play());
        mBinding.btnAddToQueue.setOnClickListener(v -> addToQueue());
        mBinding.btnDelete.setOnClickListener(v -> delete());
    }

    private void addToQueue(){
        if (mMediaItem instanceof TrackArtistAlbumEntity){
            final TrackArtistAlbumEntity track = (TrackArtistAlbumEntity) mMediaItem;
            addSongToQueue(track.getId());

        }
        else addSongsToQueue(mSongIds);
    }

    private void play(){
        int songId;
        if (mMediaItem instanceof TrackArtistAlbumEntity){
            final TrackArtistAlbumEntity track = (TrackArtistAlbumEntity) mMediaItem;
            songId = track.getId();
        }
        else{
            songId = mSongIds.get(0);
        }

        EventBus.getDefault().post(new PlaySongEvent(songId, mSongIds));
        dismiss();
    }

    private void delete(){
        if (mMediaItem instanceof TrackArtistAlbumEntity){
            final TrackArtistAlbumEntity track = (TrackArtistAlbumEntity) mMediaItem;
            showAlert(getString(R.string.warning_delete_song, track.getTitle()), true, getString(R.string.action_yes_delete), v -> deleteSong(track));
        }
        else if (mMediaItem instanceof AlbumArtistEntity){
            final AlbumArtistEntity album = (AlbumArtistEntity) mMediaItem;
            showAlert(getString(R.string.warning_delete_album, album.getName()), true, getString(R.string.action_yes_delete), v -> deleteAlbum(album));
        }
        else if (mMediaItem instanceof ArtistEntity){
            final ArtistEntity artist = (ArtistEntity) mMediaItem;
            showAlert(getString(R.string.warning_delete_artist, artist.getName()), true, getString(R.string.action_yes_delete), v -> deleteArtist(artist));
        }
    }

    private void deleteSong(TrackArtistAlbumEntity track){
        setAsActive(false);
        executeDeleteRequest(DeleteSongWorker.class, track.getId(), new DeleteMediaCallback() {
            @Override
            public void deleteSuccess() {
                final int albumId = track.getAlbum_id();
                final List<TrackArtistAlbumEntity> albumSongs = mMusicLibrary.getAlbumSongs(albumId);
                if (albumSongs.size() > 0){
                    dismiss();
                }
                else{
                    deleteAlbum(mMusicLibrary.getAlbumById(albumId));
                }
            }

            @Override
            public void deleteFailed() {
                setAsActive(true);
                showAlert(getString(R.string.error_delete_song), true, getString(R.string.okay), null);
            }
        });
    }

    private void deleteAlbum(AlbumArtistEntity album){
        setAsActive(false);
        executeDeleteRequest(DeleteAlbumWorker.class, album.getId(), new DeleteMediaCallback() {
            @Override
            public void deleteSuccess() {
                final int artistId = album.getArtist_id();
                final List<AlbumArtistEntity> artistAlbums = mMusicLibrary.getArtistAlbums(artistId);
                if (artistAlbums.size() > 0){
                    dismiss();
                }
                else{
                    deleteArtist(mMusicLibrary.getArtistById(artistId));
                }
            }

            @Override
            public void deleteFailed() {
                setAsActive(true);
                showAlert(getString(R.string.error_delete_album), true, getString(R.string.okay), null);
            }
        });
    }

    private void deleteArtist(ArtistEntity artist){
        setAsActive(false);
        executeDeleteRequest(DeleteArtistWorker.class, artist.getId(), new DeleteMediaCallback() {
            @Override
            public void deleteSuccess() {
                dismiss();
            }

            @Override
            public void deleteFailed() {
                setAsActive(true);
                showAlert(getString(R.string.error_delete_artist), true, getString(R.string.okay), null);
            }
        });
    }

    private void executeDeleteRequest(Class<? extends ListenableWorker> worker, int mediaId, DeleteMediaCallback callback){
        final WorkRequest deleteRequest = new OneTimeWorkRequest.Builder(worker)
                .setInputData(new Data.Builder().putInt(Constants.KEY_MEDIA_ID, mediaId).build())
                .build();

        final WorkManager workManager = WorkManager.getInstance(requireContext());
        workManager.enqueue(deleteRequest);

        workManager.getWorkInfoByIdLiveData(deleteRequest.getId()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED){
                callback.deleteSuccess();
            }
            else if (workInfo.getState() == WorkInfo.State.FAILED){
                callback.deleteFailed();
            }
        });

    }

    private void showToastNotification(String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void addSongToQueue(int songId){
        mMusicLibrary.addSongToQueue(songId);
        showToastNotification(getString(R.string.msg_song_s_added_to_queue));
        dismiss();
    }

    private void addSongsToQueue(List<Integer> songIds){
        mMusicLibrary.addSongsToQueue(songIds);
        showToastNotification(getString(R.string.msg_song_s_added_to_queue));
        dismiss();
    }

    private interface DeleteMediaCallback{
        void deleteSuccess();
        void deleteFailed();
    }

    private void setAsActive(boolean isActive){
        mBinding.btnPlay.setEnabled(isActive);
        mBinding.btnAddToQueue.setEnabled(isActive);
        mBinding.btnDelete.setEnabled(isActive);
        mBinding.btnDelete.setText(getString(!isActive ? R.string.deleting : R.string.delete));
        mBinding.lpiDelete.setVisibility(isActive ? View.GONE : View.VISIBLE);
    }
}
