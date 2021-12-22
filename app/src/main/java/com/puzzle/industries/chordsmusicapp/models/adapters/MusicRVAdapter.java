package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaRVAdapter;
import com.puzzle.industries.chordsmusicapp.base.BaseViewHolder;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ItemMusicBinding;
import com.puzzle.industries.chordsmusicapp.events.PlaySongEvent;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MusicRVAdapter extends BaseMediaRVAdapter<ItemMusicBinding, TrackArtistAlbumEntity> {

    public MusicRVAdapter(List<TrackArtistAlbumEntity> mediaList) {
        this.mediaList = mediaList;
    }

    @NonNull
    @Override
    public BaseViewHolder<ItemMusicBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemMusicBinding binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BaseViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemMusicBinding> holder, int position) {
        final Context ctx = holder.itemView.getContext();
        final MusicLibraryService musicLibrary = MusicLibraryService.getInstance();
        final TrackArtistAlbumEntity song = mediaList.get(position);
        final ArtistEntity artist = musicLibrary.getArtistById(song.getArtist_id());
        final AlbumArtistEntity album = musicLibrary.getAlbumById(song.getAlbum_id());
        final boolean isCurrentlyPlaying = currentMediaItem != null && song.getId() == currentMediaItem.getId();

        final int txtColor = ContextCompat.getColor(ctx, isCurrentlyPlaying
                ? R.color.secondaryLightColor:
                R.color.primaryTextColor);

        holder.mBinding.tvName.setTextColor(txtColor);
        holder.mBinding.tvDetails.setTextColor(txtColor);
        holder.mBinding.tvName.setText(song.getTitle());
        holder.mBinding.tvDetails.setText(String.format("%s • %s", artist.getName(), album.getTitle()));

        holder.mBinding.getRoot().setOnLongClickListener(view -> {
            callback.mediaItemLongClicked(song, mediaList.stream().mapToInt(TrackArtistAlbumEntity::getId).boxed().collect(Collectors.toList()));
            return true;
        });
        /*
         * Event subscriber = BaseMediaActivity
         * */
        holder.mBinding.getRoot().setOnClickListener(v -> EventBus
                .getDefault()
                .post(new PlaySongEvent(song.getId(), this.mediaList.stream()
                        .mapToInt(TrackArtistAlbumEntity::getId)
                        .boxed()
                        .collect(Collectors.toList()))));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showSearchResults(String word) {
        if (word.isEmpty()) {
            mediaList.clear();
            notifyDataSetChanged();
            return;
        }

        mediaList.clear();
        mediaList.addAll(MUSIC_LIBRARY.getSongs().stream().filter(trackArtistAlbumEntity -> meetsFilterRequirements(trackArtistAlbumEntity, word)).collect(Collectors.toList()));
        notifyDataSetChanged();
    }

    @Override
    protected boolean meetsFilterRequirements(TrackArtistAlbumEntity trackArtistAlbumEntity, String word) {
        return trackArtistAlbumEntity.getFileName().toLowerCase().contains(word.toLowerCase());
    }


}
