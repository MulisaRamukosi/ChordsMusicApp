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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@SuppressLint("NotifyDataSetChanged")
public class MusicRVAdapter extends BaseMediaRVAdapter<ItemMusicBinding, TrackArtistAlbumEntity> {

    @Setter private boolean isSelectionRequest;
    @Getter @Setter private List<Integer> selectedSongs = new ArrayList<>();

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
        if (isSelectionRequest){
            holder.mBinding.getRoot().setClickable(true);
            holder.mBinding.getRoot().setFocusable(true);
            holder.mBinding.getRoot().setCheckable(true);
        }

        final Context ctx = holder.itemView.getContext();
        final TrackArtistAlbumEntity song = mediaList.get(position);
        final ArtistEntity artist = MUSIC_LIBRARY.getArtistById(song.getArtist_id());
        final AlbumArtistEntity album = MUSIC_LIBRARY.getAlbumById(song.getAlbum_id());
        final boolean isCurrentlyPlaying = currentMediaItem != null && song.getId() == currentMediaItem.getId();
        final boolean[] isSelected = {selectedSongs.contains(song.getId())};
        final int txtColor = ContextCompat.getColor(ctx, isCurrentlyPlaying
                ? R.color.secondaryLightColor:
                R.color.primaryTextColor);

        holder.mBinding.getRoot().setChecked(isSelected[0]);
        holder.mBinding.tvName.setTextColor(txtColor);
        holder.mBinding.tvDetails.setTextColor(txtColor);
        holder.mBinding.tvName.setText(song.getTitle());
        holder.mBinding.tvDetails.setText(String.format("%s • %s", artist.getName(), album.getTitle()));

        holder.mBinding.getRoot().setOnLongClickListener(view -> {
            if (callback != null) callback.mediaItemLongClicked(song, mediaList.stream().mapToInt(TrackArtistAlbumEntity::getId).boxed().collect(Collectors.toList()));
            return true;
        });

        /*
         * Event subscriber = BaseMediaActivity
         * */
        holder.mBinding.getRoot().setOnClickListener(v -> {
            if (isSelectionRequest){
                if (isSelected[0]){
                    selectedSongs.remove(Integer.valueOf(song.getId()));
                    isSelected[0] = false;
                }
                else{
                    selectedSongs.add(song.getId());
                    isSelected[0] = true;
                }

                holder.mBinding.getRoot().setChecked(isSelected[0]);
            }
            else{
                EventBus
                        .getDefault()
                        .post(new PlaySongEvent(song.getId(), this.mediaList.stream()
                                .mapToInt(TrackArtistAlbumEntity::getId)
                                .boxed()
                                .collect(Collectors.toList())));
            }
        });
    }

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

    public void reset(){
        this.mediaList.clear();
        this.mediaList.addAll(MUSIC_LIBRARY.getSongs());
        notifyDataSetChanged();
    }

    public void setAlreadySelectedIds(List<Integer> mAlreadySelectedIds) {
        this.selectedSongs = mAlreadySelectedIds;
    }

    @Override
    protected boolean meetsFilterRequirements(TrackArtistAlbumEntity song, String word) {
        final ArtistEntity artist = MUSIC_LIBRARY.getArtistById(song.getArtist_id());
        final AlbumArtistEntity album = MUSIC_LIBRARY.getAlbumById(song.getAlbum_id());
        final String songName = song.getTitle().toLowerCase();
        final String artistName = artist.getName().toLowerCase();
        final String albumName = album.getTitle().toLowerCase();
        word = word.toLowerCase();

        return songName.contains(word) || artistName.contains(word) || albumName.contains(word);
    }

}
