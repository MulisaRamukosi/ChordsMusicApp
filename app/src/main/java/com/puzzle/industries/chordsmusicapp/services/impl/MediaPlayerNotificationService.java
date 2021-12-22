package com.puzzle.industries.chordsmusicapp.services.impl;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.Target;
import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.activities.PlayerActivity;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.services.IMediaPlayerNotificationService;
import com.puzzle.industries.chordsmusicapp.utils.MediaPlayerActions;

public class MediaPlayerNotificationService implements IMediaPlayerNotificationService {

    public static int NOTIFICATION_ID = 1746;
    public final String CHANNEL_ID = "Chords Music player";
    private final String MEDIA_SESSION_TAG = "Chords_Media_Session";

    private final int REQUEST_CODE = 2239;

    private static final PendingIntent MAIN_PI = PendingIntent.getActivity(Chords.getAppContext(),
            0, new Intent(Chords.getAppContext(), PlayerActivity.class),
            PendingIntent.FLAG_UPDATE_CURRENT);


    private NotificationManager notificationManager;
    private static MediaPlayerNotificationService instance;

    public static MediaPlayerNotificationService getInstance(){
        if (instance == null){
            synchronized (MediaPlayerNotificationService.class){
                if (instance == null){
                    instance = new MediaPlayerNotificationService();
                }
            }
        }
        return instance;
    }

    @Override
    public void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    Chords.getAppResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);
            channel.setShowBadge(false);
            notificationManager = Chords.getAppContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public Notification createNotification() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(Chords.getAppContext(), CHANNEL_ID);

        notificationBuilder.setOngoing(true)
                .setContentTitle(Chords.getAppResources()
                        .getString(R.string.app_name));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationBuilder.setPriority(NotificationManager.IMPORTANCE_NONE)
                    .setCategory(Notification.CATEGORY_SERVICE);
        }


        return notificationBuilder.build();
    }

    @Override
    public void showMediaNotification(SongInfoProgressEvent song) {
        final String albumUrl = song.getCurrentAlbum().getCover_url();
        final String pictureUrl = albumUrl == null ? song.getCurrentArtist().getPicture_url() : albumUrl;
        Glide.with(Chords.getAppContext())
                .load(pictureUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        showMediaNotification(song, ContextCompat.getDrawable(Chords.getAppContext(), R.drawable.bg_album));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        showMediaNotification(song, resource);
                        return false;
                    }
                })
                .submit();
    }

    @Override
    public void closeMediaPlayerNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private PendingIntent createPendingIntentWithAction(String action){
        final Intent intent = new Intent(Chords.getAppContext(), MusicPlayerService.class);
        intent.setAction(action);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getService(Chords.getAppContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }
        else{
            return PendingIntent.getService(Chords.getAppContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    private void showMediaNotification(SongInfoProgressEvent event, Drawable albumArt){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            final MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(Chords.getAppContext(), MEDIA_SESSION_TAG);
            final MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();

            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(Chords.getAppContext().getResources(), R.mipmap.ic_launcher));
            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, drawableToBitmap(albumArt));
            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, drawableToBitmap(albumArt));
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, event.getCurrentSong().getTitle());
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, String.format("%s • %s", event.getCurrentArtist().getName(), event.getCurrentAlbum().getTitle()));

            mediaSessionCompat.setMetadata(metadataBuilder.build());


            final boolean isMusicPlaying = event.isPlaying();
            final int play_pause_drawable = isMusicPlaying ? R.drawable.ic_round_pause_24 : R.drawable.ic_round_play_arrow_24;
            String play_pause_title = isMusicPlaying ? "Pause" : "Play";
            PendingIntent play_pause_pi = createPendingIntentWithAction(isMusicPlaying ? MediaPlayerActions.ACTION_PAUSE : MediaPlayerActions.ACTION_RESUME);

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(Chords.getAppContext(), CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(drawableToBitmap(albumArt))
                    .setContentTitle(event.getCurrentSong().getTitle())
                    .setContentText(String.format("%s • %s", event.getCurrentArtist().getName(), event.getCurrentAlbum().getTitle()))
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setShowWhen(false)
                    .addAction(R.drawable.ic_round_skip_previous_24, "Previous", createPendingIntentWithAction(MediaPlayerActions.ACTION_PREV))
                    .addAction(play_pause_drawable, play_pause_title, play_pause_pi)
                    .addAction(R.drawable.ic_round_skip_next_24, "Next", createPendingIntentWithAction(MediaPlayerActions.ACTION_NEXT))
                    .setContentIntent(MAIN_PI)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
