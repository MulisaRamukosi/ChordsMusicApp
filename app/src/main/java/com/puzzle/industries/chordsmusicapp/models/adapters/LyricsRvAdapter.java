package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseViewHolder;
import com.puzzle.industries.chordsmusicapp.databinding.ItemVerseBinding;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.VerseModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import lombok.AllArgsConstructor;

public class LyricsRvAdapter extends RecyclerView.Adapter<BaseViewHolder<ItemVerseBinding>> {

    private final ArrayList<VerseModel> verses;
    private Set<String> selectedExplanations;

    public LyricsRvAdapter(ArrayList<VerseModel> verses) {
        this.verses = verses;
        selectedExplanations = new HashSet<>();
    }

    @NonNull
    @Override
    public BaseViewHolder<ItemVerseBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(ItemVerseBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemVerseBinding> holder, int position) {
        final Context ctx = holder.itemView.getContext();

        final VerseModel verse = verses.get(position);
        holder.mBinding.tvVerse.setText(verse.getVerse().trim());
        holder.mBinding.tvVerse.setTextColor(ContextCompat.getColor(ctx,
                verse.hasExplanation() ? R.color.secondaryColor : R.color.primaryTextColor));
        if (!verse.hasExplanation()) holder.mBinding.tvExplanation.setVisibility(View.GONE);
        else {
            final String explanation = verse.getExplanation();

            holder.mBinding.tvVerse.setOnClickListener(v -> {
                if (selectedExplanations.contains(explanation)) selectedExplanations.remove(explanation);
                else selectedExplanations.add(explanation);
                explanationStateChanged(explanation);
            });

            final boolean explanationHasBeenOpened = selectedExplanations.contains(explanation);
            if (explanationHasBeenOpened) {
                holder.mBinding.tvExplanation.setText(explanation);
            }

            holder.mBinding.tvExplanation.setVisibility(explanationHasBeenOpened ? View.VISIBLE : View.GONE);
        }
    }

    private void explanationStateChanged(String explanation){
        final ArrayList<Integer> versesPos = new ArrayList<>();
        for (int i = 0; i < verses.size(); i++){
            final VerseModel verse = verses.get(i);
            if (verse.getExplanation().equals(explanation)){
                versesPos.add(i);
            }
        }

        for (int pos : versesPos) notifyItemChanged(pos);
    }

    @Override
    public int getItemCount() {
        return verses.size();
    }
}
