package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseViewHolder;
import com.puzzle.industries.chordsmusicapp.databinding.ItemDownloadMusicBinding;
import com.puzzle.industries.chordsmusicapp.models.dataModels.AlbumDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.ArtistDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.DownloadItemDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.services.impl.DownloadManagerService;
import com.puzzle.industries.chordsmusicapp.utils.DownloadState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;


public class DownloadsRVAdapter extends RecyclerView.Adapter<BaseViewHolder<ItemDownloadMusicBinding>>{

    private final List<DownloadItemDataStruct> mSongDownloadItems;
    private final Map<Integer, BaseViewHolder<ItemDownloadMusicBinding>> mSongHolderTrack;

    public DownloadsRVAdapter(Map<Integer, DownloadItemDataStruct> songDownloadItems) {
        this.mSongHolderTrack = new HashMap<>();
        this.mSongDownloadItems = new ArrayList<>(songDownloadItems.values());
        Collections.sort(this.mSongDownloadItems, Comparator.comparing(DownloadItemDataStruct::getDownloadState));
    }

    @NonNull
    @Override
    public BaseViewHolder<ItemDownloadMusicBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemDownloadMusicBinding binding = ItemDownloadMusicBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BaseViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemDownloadMusicBinding> holder, int position) {
        holder.setIsRecyclable(false);
        final DownloadItemDataStruct songDownload = mSongDownloadItems.get(position);
        final SongDataStruct song = songDownload.getSong();
        final ArtistDataStruct artist = song.getArtist();
        final AlbumDataStruct album = song.getAlbum();

        holder.mBinding.tvName.setText(song.getSongName());
        holder.mBinding.tvDetails.setText(String.format("%s • %s", artist.getName(), album == null ? "" : album.getTitle()));
        mSongHolderTrack.put(songDownload.getDownloadId(), holder);
        updateDownloadStateView(songDownload);
    }

    @Override
    public int getItemCount() {
        return mSongDownloadItems.size();
    }

    public void updateState(SongDataStruct song, DownloadState downloadState){
        for (int i = 0; i < mSongDownloadItems.size(); i++){
            if (song.getId() == mSongDownloadItems.get(i).getDownloadId()){
                final DownloadItemDataStruct songInQueue = mSongDownloadItems.get(i);
                songInQueue.setDownloadState(downloadState);
                mSongDownloadItems.set(i, songInQueue);
                updateDownloadStateView(songInQueue);
                break;
            }
        }
    }

    public synchronized void updateProgress(SongDataStruct song, int progress){
        for (int i = 0; i < mSongDownloadItems.size(); i++){
            if (song.getId() == mSongDownloadItems.get(i).getDownloadId()){
                final DownloadItemDataStruct songInQueue = mSongDownloadItems.get(i);
                songInQueue.setDownloadState(DownloadState.DOWNLOADING);
                songInQueue.setDownloadProgress(progress);
                mSongDownloadItems.set(i, songInQueue);
                updateDownloadProgressView(songInQueue);
                break;
            }
        }
    }

    public void updateDownloadItems(Map<Integer, DownloadItemDataStruct> downloadQueue) {
        int lastKnownPos = getItemCount() - 1;
        for (DownloadItemDataStruct item : downloadQueue.values()){
            if (!mSongDownloadItems.contains(item)){
                mSongDownloadItems.add(item);
            }
        }
        notifyItemRangeChanged(lastKnownPos, getItemCount());
    }

    private void updateDownloadStateView(DownloadItemDataStruct item){
        final BaseViewHolder<ItemDownloadMusicBinding> holder = mSongHolderTrack.get(item.getDownloadId());
        if (holder != null) {
            final Context ctx = holder.itemView.getContext();
            switch (item.getDownloadState()){
                case IN_QUEUE:
                    holder.mBinding.lpi.setIndicatorColor(
                            ContextCompat.getColor(ctx, R.color.secondaryColor),
                            ContextCompat.getColor(ctx, R.color.red),
                            ContextCompat.getColor(ctx, R.color.blue)
                    );
                    holder.mBinding.lpi.setIndeterminateAnimationType(LinearProgressIndicator.INDETERMINATE_ANIMATION_TYPE_CONTIGUOUS);
                    holder.mBinding.lpi.setIndeterminate(true);
                    break;

                case PENDING:
                    holder.mBinding.lpi.setIndeterminate(false);
                    holder.mBinding.lpi.setIndeterminateAnimationType(LinearProgressIndicator.INDETERMINATE_ANIMATION_TYPE_DISJOINT);
                    holder.mBinding.lpi.setIndicatorColor(ContextCompat.getColor(ctx, R.color.secondaryColor));
                    holder.mBinding.lpi.setIndeterminate(true);
                    break;

                case COMPLETE:
                    holder.mBinding.ivDownloadComplete.setVisibility(View.VISIBLE);
                    holder.mBinding.lpi.setVisibility(View.GONE);
                    break;

                case DOWNLOADING:
                    holder.mBinding.lpi.setIndeterminate(false);
                    break;

                case FAILED:
                    holder.mBinding.lpi.setVisibility(View.GONE);
                    holder.mBinding.ivRetry.setVisibility(View.VISIBLE);
                    holder.mBinding.ivRetry.setOnClickListener(v -> DownloadManagerService.getInstance().retryDownload(item.getSong()));
                    break;
            }
        }

    }

    private void updateDownloadProgressView(DownloadItemDataStruct item){
        final BaseViewHolder<ItemDownloadMusicBinding> holder = mSongHolderTrack.get(item.getDownloadId());
        if (holder != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.mBinding.lpi.setProgress(item.getDownloadProgress(), true);
            }
            else{
                holder.mBinding.lpi.setProgress(item.getDownloadProgress());
            }
        }

    }
}
