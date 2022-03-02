package com.puzzle.industries.chordsmusicapp.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class BaseVPAdapter extends FragmentStateAdapter {

    private final List<Fragment> mFragments;

    public BaseVPAdapter(@NonNull Fragment fragment, List<Fragment> fragments) {
        super(fragment);
        this.mFragments = fragments;
    }

    public BaseVPAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
        super(fragmentActivity);
        this.mFragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }
}
