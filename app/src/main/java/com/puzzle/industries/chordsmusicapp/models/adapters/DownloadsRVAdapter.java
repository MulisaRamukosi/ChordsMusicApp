package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseViewHolder;
import com.puzzle.industries.chordsmusicapp.databinding.ItemDownloadMusicBinding;
import com.puzzle.industries.chordsmusicapp.models.dataModels.AlbumDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.ArtistDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.DownloadItemDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.services.IDownloadManagerService;
import com.puzzle.industries.chordsmusicapp.services.impl.DownloadManagerService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.utils.DownloadState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;


public class DownloadsRVAdapter extends RecyclerView.Adapter<BaseViewHolder<ItemDownloadMusicBinding>>{

    private final List<MutableLiveData<DownloadItemDataStruct>> mSongDownloadItems;

    public DownloadsRVAdapter(Map<Integer, MutableLiveData<DownloadItemDataStruct>> songDownloadItems) {
        this.mSongDownloadItems = new ArrayList<>(songDownloadItems.values());
        Collections.sort(this.mSongDownloadItems, Comparator.comparing(item -> Objects.requireNonNull(item.getValue()).getDownloadState()));
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
        final MutableLiveData<DownloadItemDataStruct> downloadItem = mSongDownloadItems.get(position);
        final DownloadItemDataStruct songDownload = downloadItem.getValue();
        assert songDownload != null;
        final SongDataStruct song = songDownload.getSong();
        final ArtistDataStruct artist = song.getArtist();
        final AlbumDataStruct album = song.getAlbum();

        holder.mBinding.tvName.setText(song.getSongName());
        holder.mBinding.tvDetails.setText(String.format("%s • %s", artist.getName(), album == null ? "" : album.getTitle()));

        downloadItem.observeForever(downloadItemDataStruct -> {
            final DownloadState downloadState = downloadItemDataStruct.getDownloadState();

            updateDownloadStateView(holder, downloadItemDataStruct);
            if (downloadState == DownloadState.DOWNLOADING){
                updateDownloadProgress(holder, downloadItemDataStruct);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mSongDownloadItems.size();
    }

    public void updateDownloadItems(Map<Integer, MutableLiveData<DownloadItemDataStruct>> downloadQueue) {
        int lastKnownPos = getItemCount() - 1;
        for (MutableLiveData<DownloadItemDataStruct> item : downloadQueue.values()){
            if (!mSongDownloadItems.contains(item)){
                mSongDownloadItems.add(item);
            }
        }
        notifyItemRangeChanged(lastKnownPos, getItemCount());
    }

    private void updateDownloadProgress(BaseViewHolder<ItemDownloadMusicBinding> holder, DownloadItemDataStruct item){
        final int downloadProgress = item.getDownloadProgress();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) holder.mBinding.lpi.setProgress(downloadProgress, true);
        else holder.mBinding.lpi.setProgress(downloadProgress);
    }

    private void updateDownloadStateView(BaseViewHolder<ItemDownloadMusicBinding> holder, DownloadItemDataStruct item){
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
                holder.mBinding.ivRetry.setOnClickListener(v -> {
                    item.setDownloadState(DownloadState.PENDING);
                    updateDownloadStateView(holder, item);
                    DownloadManagerService.getInstance().retryDownload(item.getSong());
                });
                break;
        }

    }
}
