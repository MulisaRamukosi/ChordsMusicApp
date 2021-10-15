package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.puzzle.industries.chordsmusicapp.databinding.ItemDownloadMusicBinding;
import com.puzzle.industries.chordsmusicapp.models.dataModels.AlbumDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.ArtistDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DownloadsRVAdapter extends RecyclerView.Adapter<DownloadsRVAdapter.ViewHolder>{

    private final List<SongDataStruct> mSongs;
    private final Map<Integer, Integer> mDownloadProgress;

    public DownloadsRVAdapter(List<SongDataStruct> mSongs) {
        this.mSongs = mSongs;
        this.mDownloadProgress = new HashMap<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemDownloadMusicBinding binding = ItemDownloadMusicBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SongDataStruct song = mSongs.get(position);
        final ArtistDataStruct artist = song.getArtist();
        final AlbumDataStruct album = song.getAlbum();

        holder.mBinding.tvName.setText(song.getSongName());
        holder.mBinding.tvDetails.setText(String.format("%s • %s", artist.getName(), album == null ? "" : album.getTitle()));

        final Integer progress = mDownloadProgress.get(song.getId());
        if (progress != null) holder.mBinding.lpi.setProgress(progress);
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public void addSongToQueue(SongDataStruct song){
        if (!mSongs.contains(song)){
            mSongs.add(song);
            notifyItemInserted(mSongs.size() - 1);
        }
    }

    public void updateProgress(SongDataStruct song, int progress){
        mDownloadProgress.put(song.getId(), progress);
        int i = mSongs.indexOf(song);
        notifyItemChanged(i);
    }

    public void updateDownloadQueue(List<SongDataStruct> downloadsQueue) {
        int startPos = mSongs.size();

        for (SongDataStruct s : downloadsQueue){
            if (!mSongs.contains(s)){
                mSongs.add(s);
            }
        }

        notifyItemRangeInserted(startPos, mSongs.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemDownloadMusicBinding mBinding;

        public ViewHolder(@NonNull ItemDownloadMusicBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }
    }
}
