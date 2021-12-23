package com.puzzle.industries.chordsmusicapp.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.callbacks.MediaItemLongClickCallback;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

import java.util.List;

public abstract class BaseMediaRVAdapter<G extends ViewBinding, F> extends RecyclerView.Adapter<BaseViewHolder<G>> {

    protected final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();
    protected MediaItemLongClickCallback<F> callback;
    protected List<F> mediaList;
    protected F currentMediaItem;

    public void itemRemoved(F f){
        int posOfItemToBeRemoved = mediaList.indexOf(f);
        if (mediaList.remove(posOfItemToBeRemoved) != null){
            notifyItemRemoved(posOfItemToBeRemoved);
        }
    }

    public void itemAdded(F f){
        if (!mediaList.contains(f)){
            mediaList.add(f);
            notifyItemInserted(mediaList.size() - 1);
        }
    }

    public void itemChanged(F itemInfo){
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
    protected abstract boolean meetsFilterRequirements(F f, String word);

    public void setItemLongClickCallback(MediaItemLongClickCallback<F> itemLongClickCallback){
        this.callback = itemLongClickCallback;
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

}
