package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.puzzle.industries.chordsmusicapp.base.BaseViewHolder;
import com.puzzle.industries.chordsmusicapp.callbacks.RVAdapterItemClickCallback;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ItemPlaylistAddBinding;
import com.puzzle.industries.chordsmusicapp.databinding.ItemPlaylistBinding;
import com.puzzle.industries.chordsmusicapp.helpers.ArtHelper;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import lombok.Getter;

public class PlaylistRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<PlaylistEntity> mPlaylist;
    private final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();

    private final int VIEW_TYPE_ADD_PLAYLIST = 2144;

    private RVAdapterItemClickCallback<PlaylistEntity> mCallback;

    public PlaylistRVAdapter(List<PlaylistEntity> playlist){
        this.mPlaylist = playlist;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mPlaylist.size()){
            return VIEW_TYPE_ADD_PLAYLIST;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ADD_PLAYLIST){
            final ItemPlaylistAddBinding binding = ItemPlaylistAddBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new BaseViewHolder<>(binding);
        }
        final ItemPlaylistBinding binding = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BaseViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == mPlaylist.size()){
            holder.itemView.setOnClickListener(v -> {
                if (mCallback != null){
                    mCallback.neutralItemClick();
                }
            });
        }
        else{
            initPlaylistViewHolder((BaseViewHolder<ItemPlaylistBinding>) holder, position);
        }
    }

    private void initPlaylistViewHolder(BaseViewHolder<ItemPlaylistBinding> holder, int position){
        final PlaylistEntity playlist = this.mPlaylist.get(position);
        final List<TrackArtistAlbumEntity> playlistTracks = MusicLibraryService.getInstance().getPlaylistTracks(playlist.getId());

        if (!playlistTracks.isEmpty()){
            final Random random = new Random();
            final int randPos = random.nextInt(playlistTracks.size());
            final TrackArtistAlbumEntity randomTrack = playlistTracks.get(randPos);
            final ArtistEntity randomArtist = MUSIC_LIBRARY.getArtistById(randomTrack.getArtist_id());
            ArtHelper.displayArtistArtFromUrl(randomArtist.getPicture_url(), holder.mBinding.ivPlaylistPic);
        }
        else{
            ArtHelper.displayDefaultAlbumArt(holder.mBinding.ivPlaylistPic);
        }

        List<String> artistNames = playlistTracks.stream()
                .map(trackArtistAlbumEntity -> MUSIC_LIBRARY.getArtistById(trackArtistAlbumEntity.getArtist_id()).getName())
                .collect(Collectors.toList());
        artistNames = artistNames.stream().distinct().collect(Collectors.toList());

        holder.mBinding.tvPlaylistName.setText(playlist.getName());
        holder.mBinding.tvArtistNames.setText(artistNames.stream().collect(Collectors.joining(",")));
        holder.mBinding.tvPlaylistName.setText(playlist.getName());

        holder.itemView.setOnClickListener(v -> {
            if (mCallback != null){
                mCallback.itemClicked(playlist);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (mCallback != null){
                mCallback.itemLongClicked(playlist);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mPlaylist.size() + 1;
    }

    public void setItemSelectionCallback(RVAdapterItemClickCallback<PlaylistEntity> callback){
        this.mCallback = callback;
    }

    public void addPlaylist(PlaylistEntity playlist) {
        this.mPlaylist.add(playlist);
        notifyItemInserted(this.mPlaylist.size() - 1);
    }

    public void removePlaylist(PlaylistEntity playlist) {
        final int removedPos = this.mPlaylist.indexOf(playlist);
        if (this.mPlaylist.remove(playlist)){
            notifyItemRemoved(removedPos);
        }
    }

    public void updatePlaylist(PlaylistEntity playlistEntity) {
        int targetPos = -1;
        for (int i = 0; i < mPlaylist.size(); i++){
            final PlaylistEntity playlist = mPlaylist.get(i);
            if (playlist.getId() == playlistEntity.getId()) {
                targetPos = i;
                break;
            }
        }

        if (targetPos != -1){
            this.mPlaylist.set(targetPos, playlistEntity);
            notifyItemChanged(targetPos);
        }
    }
}
