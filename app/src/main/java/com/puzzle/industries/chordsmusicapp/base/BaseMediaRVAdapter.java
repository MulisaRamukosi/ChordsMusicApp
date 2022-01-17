package com.puzzle.industries.chordsmusicapp.base;

import androidx.recyclerview.widget.RecyclerView;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.callbacks.MediaItemLongClickCallback;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

import java.util.List;

public abstract class BaseMediaRVAdapter<ViewBinding extends androidx.viewbinding.ViewBinding, MediaModel> extends RecyclerView.Adapter<BaseViewHolder<ViewBinding>> {

    protected final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();
    protected MediaItemLongClickCallback<MediaModel> callback;
    protected List<MediaModel> mediaList;
    protected MediaModel currentMediaItem;

    public void itemRemoved(MediaModel f){
        int posOfItemToBeRemoved = mediaList.indexOf(f);
        if (mediaList.remove(posOfItemToBeRemoved) != null){
            notifyItemRemoved(posOfItemToBeRemoved);
        }
    }

    public void itemAdded(MediaModel f){
        if (!mediaList.contains(f)){
            mediaList.add(f);
            notifyItemInserted(mediaList.size() - 1);
        }
    }

    public void itemChanged(MediaModel itemInfo){
        if (currentMediaItem == null){
            currentMediaItem = itemInfo;
            final int index = mediaList.indexOf(itemInfo);
            notifyItemChanged(index);
        }
        else if (!currentMediaItem.equals(itemInfo)){
            final int oldPos = mediaList.indexOf(currentMediaItem);
            currentMediaItem = null;
            notifyItemChanged(oldPos);

            Chords.applicationHandler.postDelayed(() -> itemChanged(itemInfo), 100);
        }
    }

    public abstract void showSearchResults(String word);
    protected abstract boolean meetsFilterRequirements(MediaModel f, String word);

    public void setItemLongClickCallback(MediaItemLongClickCallback<MediaModel> itemLongClickCallback){
        this.callback = itemLongClickCallback;
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

}
