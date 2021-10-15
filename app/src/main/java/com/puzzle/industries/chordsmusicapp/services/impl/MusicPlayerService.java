package com.puzzle.industries.chordsmusicapp.services.impl;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.services.IMusicPlayerService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MusicPlayerService extends Service implements IMusicPlayerService, MediaPlayer.OnCompletionListener {

    private List<TrackArtistAlbumEntity> mPlayList;
    private MediaPlayer mMediaPlayer;
    private TrackArtistAlbumEntity mCurrentSong;
    private ArtistEntity mCurrentArtist;
    private AlbumArtistEntity mCurrentAlbum;

    @Override
    public void onCreate() {
        super.onCreate();

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        broadCastSongInfo();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()){

            case Constants.ACTION_SET_LIST:
                final List<TrackArtistAlbumEntity> songList = intent.getParcelableArrayListExtra(Constants.KEY_SONG_LIST);
                setPlayList(songList);
                break;

            case Constants.ACTION_PLAY_PAUSE:
                final int id = intent.getIntExtra(Constants.KEY_SONG_ID, -1);
                playOrPause(id);
                break;

            case Constants.ACTION_SEEK_TO:
                final int progress = intent.getIntExtra(Constants.KEY_SEEK, 0);
                seekTo(progress);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void playOrPause(int id){
        if (mCurrentSong == null || id != mCurrentSong.getId()){
            playSong(id);
        }
        else if (mMediaPlayer.isPlaying()){
            pause();
        }
        else{
            resume();
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
    }

    @Override
    public void resume() {
        if (!mMediaPlayer.isPlaying() && mMediaPlayer.getCurrentPosition() > 1){
            mMediaPlayer.start();
        }
    }

    @Override
    public void playSong(int id) {
        final TrackArtistAlbumEntity song = mPlayList.stream()
                .filter(songDataStruct -> songDataStruct.getId() == id).collect(Collectors.toList())
                .get(0);

        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
        try {

            File file = new File(song.getLocation());
            boolean exists = file.exists();
            if (exists){
                file.getPath();
            }
            mMediaPlayer.setDataSource(song.getLocation());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mCurrentSong = song;
            mCurrentArtist = MusicLibraryService.getInstance().getArtistById(song.getArtist_id());
            mCurrentAlbum = MusicLibraryService.getInstance().getAlbumById(song.getAlbum_id());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPlayList(List<TrackArtistAlbumEntity> songs) {
        mPlayList = songs;
    }

    @Override
    public void playNextSong() {
        int newPos = mPlayList.indexOf(mCurrentSong) + 1;
        if (newPos >= mPlayList.size()){
            newPos = 0;
        }
        playSong(mPlayList.get(newPos).getId());
    }

    @Override
    public void playPreviousSong() {
        if (mMediaPlayer.getCurrentPosition() > 10000){
            seekTo(0);
        }
        else{
            int newPos = mPlayList.indexOf(mCurrentSong) - 1;
            if (newPos < 0){
                newPos = mPlayList.size() - 1;
            }
            playSong(mPlayList.get(newPos).getId());
        }
    }

    @Override
    public TrackArtistAlbumEntity getCurrentSong() {
        return mCurrentSong;
    }

    @Override
    public int getTotalDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public void seekTo(int pos) {
        mMediaPlayer.seekTo(pos);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        this.playNextSong();
    }

    private void broadCastSongInfo(){
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                    if (mMediaPlayer != null && mCurrentSong != null){
                        final Intent i = new Intent(Constants.KEY_MUSIC_UPDATE);
                        final SongInfoProgressEvent event = new SongInfoProgressEvent(
                                mCurrentSong,
                                mCurrentArtist,
                                mCurrentAlbum,
                                mMediaPlayer.getCurrentPosition(),
                                mMediaPlayer.getDuration(),
                                mMediaPlayer.isPlaying());
                        i.putExtra(Constants.KEY_MUSIC_PROGRESS, event);
                        sendBroadcast(i);

                    }
                }, 0, 1, TimeUnit.SECONDS);
    }
}
