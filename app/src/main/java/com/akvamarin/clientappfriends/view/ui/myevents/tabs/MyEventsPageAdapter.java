package com.akvamarin.clientappfriends.view.ui.myevents.tabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyEventsPageAdapter extends FragmentStateAdapter {
    private final int numOfTabs;

    public MyEventsPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int numOfTabs) {
        super(fragmentManager, lifecycle);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> ParticipantEventFragment.newInstance();
            case 1 -> DesiredEventsFragment.newInstance();
            default -> throw new IllegalArgumentException("Invalid position: " + position);
        };
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }

}


