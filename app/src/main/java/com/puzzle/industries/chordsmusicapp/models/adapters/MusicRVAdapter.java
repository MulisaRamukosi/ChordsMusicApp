package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ItemMusicBinding;
import com.puzzle.industries.chordsmusicapp.events.PlayPauseSongEvent;
import com.puzzle.industries.chordsmusicapp.events.SongInfoProgressEvent;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicPlayerService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import lombok.AllArgsConstructor;

public class MusicRVAdapter extends RecyclerView.Adapter<MusicRVAdapter.ViewHolder> {

    private final List<TrackArtistAlbumEntity> mSongs;
    private SongInfoProgressEvent mSongInfo;

    public MusicRVAdapter(List<TrackArtistAlbumEntity> mSongs) {
        this.mSongs = mSongs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemMusicBinding binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Context ctx = holder.itemView.getContext();
        final MusicLibraryService musicLibrary = MusicLibraryService.getInstance();
        final TrackArtistAlbumEntity song = mSongs.get(position);
        final ArtistEntity artist = musicLibrary.getArtistById(song.getArtist_id());
        final AlbumArtistEntity album = musicLibrary.getAlbumById(song.getAlbum_id());
        final boolean isCurrentlyPlaying = mSongInfo != null && song.getId() == mSongInfo.getCurrentSong().getId();

        if (isCurrentlyPlaying){
            holder.mBinding.tvName.setTextColor(ContextCompat.getColor(ctx, R.color.secondaryLightColor));
            holder.mBinding.tvDetails.setTextColor(ContextCompat.getColor(ctx, R.color.secondaryLightColor));
            holder.mBinding.ivPlayPause.setImageDrawable(ContextCompat.getDrawable(
                    ctx, mSongInfo.isPlaying() ? R.drawable.ic_round_pause_24 : R.drawable.ic_round_play_arrow_24));
        }

        holder.mBinding.tvName.setText(song.getTitle());
        holder.mBinding.tvDetails.setText(String.format("%s • %s", artist.getName(), album.getTitle()));

        //TODO: set click listener for item
        holder.mBinding.getRoot().setOnClickListener(v -> {
            if (holder.mBinding.llOptions.getVisibility() == View.VISIBLE){
                holder.mBinding.llOptions.setVisibility(View.GONE);
            }
            else{
                holder.mBinding.llOptions.setVisibility(View.VISIBLE);
            }
        });

        holder.mBinding.ivPlayPause.setOnClickListener(v -> {
            EventBus.getDefault().post(new PlayPauseSongEvent(song.getId()));
        });
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public void updateChanges() {
        notifyItemInserted(mSongs.size() - 1);
    }

    public void updateSongInfo(SongInfoProgressEvent songInfo) {
        if (mSongInfo == null){
            mSongInfo = songInfo;
            final int index = mSongs.indexOf(songInfo.getCurrentSong());
            notifyItemChanged(index);
        }
        else if (mSongInfo.getCurrentSong().getId() != songInfo.getCurrentSong().getId()){
            final int oldPos = mSongs.indexOf(songInfo.getCurrentSong());
            mSongInfo = null;
            notifyItemChanged(oldPos);
            updateSongInfo(songInfo);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ItemMusicBinding mBinding;

        public ViewHolder(@NonNull ItemMusicBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;

        }
    }
}
