package com.puzzle.industries.chordsmusicapp.services.impl;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.events.PlaySongEvent;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.services.IMediaPlayerNotificationService;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.IMusicPlayerService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.utils.MediaPlayerActions;
import com.puzzle.industries.chordsmusicapp.utils.RepeatMode;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MusicPlayerService extends Service implements IMusicPlayerService, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, AudioManager.OnAudioFocusChangeListener {

    private final IBinder BINDER = new MusicPlayerBinder();
    private final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();

    private MediaPlayer mMediaPlayer;
    private TrackArtistAlbumEntity mCurrentSong;
    private ArtistEntity mCurrentArtist;
    private AlbumArtistEntity mCurrentAlbum;
    private RepeatMode mRepeatMode = RepeatMode.REPEAT_OFF;
    private AudioManager mAudioManager;
    private AudioFocusRequest mFocusRequest;
    private IMediaPlayerNotificationService mMediaPlayerNotificationService;

    private boolean isShuffleEnabled = false;
    private boolean resumeOnFocusGain = false;
    private boolean playbackDelayed = false;
    private boolean isPrepared = false;

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange){
            case AudioManager.AUDIOFOCUS_GAIN:
                if (playbackDelayed || resumeOnFocusGain){
                    synchronized (MusicPlayerService.class){
                        playbackDelayed = false;
                        resumeOnFocusGain = false;
                    }
                    mMediaPlayer.start();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                synchronized (MusicPlayerService.class){
                    playbackDelayed = false;
                    resumeOnFocusGain = false;
                }
                mMediaPlayer.pause();
                displayNotification();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                synchronized (MusicPlayerService.class){
                    resumeOnFocusGain = mMediaPlayer.isPlaying();
                    playbackDelayed = false;
                }
                mMediaPlayer.pause();
                displayNotification();
                break;
        }
    }

    public class MusicPlayerBinder extends Binder{
        public MusicPlayerService getService(){
            return MusicPlayerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return BINDER;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMediaPlayerNotificationService = MediaPlayerNotificationService.getInstance();
        startForeground(MediaPlayerNotificationService.NOTIFICATION_ID, mMediaPlayerNotificationService.createNotification());
        initMediaPlayer();
        initAudioManager();
        broadCastSongInfo();

    }

    private void initMediaPlayer(){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setScreenOnWhilePlaying(true);
        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void initAudioManager(){
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        initFocusRequest();
    }

    private void initFocusRequest() {
        final AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(this, Chords.applicationHandler)
                    .build();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            final String action = intent.getAction();
            if (action != null){
                switch (action){
                    case MediaPlayerActions.ACTION_RESUME:
                        requestToPlay();
                        break;

                    case MediaPlayerActions.ACTION_PAUSE:
                        pause();
                        break;

                    case MediaPlayerActions.ACTION_PREV:
                        playPreviousSong();
                        break;

                    case MediaPlayerActions.ACTION_NEXT:
                        playNextSong();
                        break;
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public void play(PlaySongEvent event){
        if (mCurrentSong == null
                || event.getId() != mCurrentSong.getId()
                || MUSIC_LIBRARY.getPlaylist().size() != event.getPlayList().size()
                || playListIsDifferent(event)){
            playSong(event.getId(), event.getPlayList());
        }
    }



    @Override
    public void initMiniPlayer(ImageView playPause, SeekBar songProgress) {
        playPause.setOnClickListener(v -> {
            if (mMediaPlayer.isPlaying()){
                pause();
            }
            else{
                requestToPlay();
            }
        });

        songProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void initPlayer(ImageView playPause, SeekBar songProgress, ImageView nextSong, ImageView prevSong) {
        initMiniPlayer(playPause, songProgress);
        prevSong.setOnClickListener(v -> playPreviousSong());
        nextSong.setOnClickListener(v -> playNextSong());
    }

    @Override
    public void pause() {
        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            displayNotification();
            if (mFocusRequest != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                mAudioManager.abandonAudioFocusRequest(mFocusRequest);
            }
            else{
                mAudioManager.abandonAudioFocus(this);
            }
        }
    }

    @Override
    public void playSong(int id, List<Integer> playList) {
        if(isShuffleEnabled){
            Collections.shuffle(playList);
        }
        MUSIC_LIBRARY.setMusicPlaylist(playList);
        final int pos = MUSIC_LIBRARY.getPlaylist().indexOf(id);
        playSong(pos);
    }

    @Override
    public void playNextSong() {
        int newPos = MUSIC_LIBRARY.getPlaylist().indexOf(mCurrentSong.getId())
                + (mRepeatMode == RepeatMode.REPEAT_SONG ? 0 : 1);
        if (newPos >= MUSIC_LIBRARY.getPlaylist().size()){
            if (mRepeatMode == RepeatMode.REPEAT_LIST){
                newPos = 0;
                playSong(newPos);
            }
            else {
                seekTo(0);
                pause();
            }
        }
        else{
            playSong(newPos);
        }

    }

    @Override
    public void playPreviousSong() {
        if (mMediaPlayer.getCurrentPosition() > 10000 || mRepeatMode == RepeatMode.REPEAT_SONG){
            seekTo(0);
        }
        else{
            int newPos = MUSIC_LIBRARY.getPlaylist().indexOf(mCurrentSong.getId()) - 1;
            if (newPos < 0){
                newPos = MUSIC_LIBRARY.getPlaylist().size() - 1;
            }
            playSong(newPos);
        }
    }

    @Override
    public void setRepeatMode(RepeatMode repeatMode) {
        mRepeatMode = repeatMode;
    }

    @Override
    public void setShuffleModeEnabled(boolean enabled) {
        isShuffleEnabled = enabled;
        MUSIC_LIBRARY.setAsShuffled(enabled);
    }

    @Override
    public boolean isShuffleModeEnabled() {
        return isShuffleEnabled;
    }

    @Override
    public RepeatMode getRepeatMode() {
        return mRepeatMode;
    }

    @Override
    public TrackArtistAlbumEntity getCurrentSong() {
        return mCurrentSong;
    }

    @Override
    public void seekTo(int pos) {
        mMediaPlayer.seekTo(pos);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        displayNotification();
        this.playNextSong();
    }

    private boolean playListIsDifferent(PlaySongEvent event){
        final List<Integer> playList = MUSIC_LIBRARY.getPlaylist();
        for (int id : event.getPlayList()){
            if (!playList.contains(id)){
                return false;
            }
        }

        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        isPrepared = true;
        requestToPlay();
    }

    @Override
    public void requestToPlay(){
        synchronized (MusicPlayerService.class){
            int res;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) res = mAudioManager.requestAudioFocus(mFocusRequest);
            else res = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

            if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mMediaPlayer.start();
                displayNotification();
            }
            else if (res == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) playbackDelayed = true;

        }
    }

    private synchronized void playSong(int pos){
        isPrepared = false;
        if (pos <= -1){
            mMediaPlayer.stop();
            mCurrentSong = null;
            mCurrentArtist = null;
            mCurrentAlbum = null;
            return;
        }
        MUSIC_LIBRARY.setCurrentQueuePos(pos);
        final int songId = MUSIC_LIBRARY.getPlaylist().get(pos);
        final TrackArtistAlbumEntity song = MUSIC_LIBRARY.getSongById(songId);

        try {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(song.getLocation());
            mMediaPlayer.prepare();
            mCurrentSong = song;
            mCurrentArtist = MusicLibraryService.getInstance().getArtistById(song.getArtist_id());
            mCurrentAlbum = MusicLibraryService.getInstance().getAlbumById(song.getAlbum_id());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadCastSongInfo(){
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                    if (mMediaPlayer != null && mCurrentSong != null && isPrepared){
                        final Intent i = new Intent(Constants.ACTION_MUSIC_PROGRESS_UPDATE);
                        i.putExtra(Constants.KEY_MUSIC_PROGRESS, buildSongProgressInfo());
                        sendBroadcast(i);
                    }
                    else if (mMediaPlayer != null){
                        final Intent i = new Intent(Constants.ACTION_MUSIC_PROGRESS_UPDATE);
                        sendBroadcast(i);
                    }
                }, 0, 200, TimeUnit.MILLISECONDS);
    }

    private SongInfoProgressEvent buildSongProgressInfo(){
        return new SongInfoProgressEvent(
                mCurrentSong,
                mCurrentArtist,
                mCurrentAlbum,
                mMediaPlayer.getCurrentPosition(),
                mMediaPlayer.getDuration(),
                mMediaPlayer.isPlaying());
    }

    private void displayNotification(){
        mMediaPlayerNotificationService.showMediaNotification(buildSongProgressInfo());
    }
}
