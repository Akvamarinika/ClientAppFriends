package com.akvamarin.clientappfriends.ui.myevents;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.chats.AddUsersActivity;
import com.akvamarin.clientappfriends.ui.myevents.tabs.MyEventsPageAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MyEventsFragment extends Fragment {

    private static final String TAG = "MyEventsFragment";
    private View viewMyEventsFragment;
    //private FragmentMyEventsBinding binding;
    private FloatingActionButton floatingActionButton;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private MyEventsPageAdapter myEventsPageAdapter;
    private TabItem tabChats;
    private TabItem tabStatus;

    public View onCreateView(@NonNull LayoutInflater layoutInflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewMyEventsFragment = layoutInflater.inflate(R.layout.fragment_my_events, container, false);
        initWidgets();
        updateToolbar();
        updateMainFAB();
        setListeners();

        myEventsPageAdapter = new MyEventsPageAdapter(getChildFragmentManager(), getLifecycle(), tabLayout.getTabCount());
        viewPager2.setAdapter(myEventsPageAdapter);
        setColorForTabLayout();








        return viewMyEventsFragment;
    }


    private void initWidgets(){
        floatingActionButton = requireActivity().findViewById(R.id.fab_btn);
        toolbar = requireActivity().findViewById(R.id.top_toolbar);
        tabLayout = viewMyEventsFragment.findViewById(R.id.myEventsTabLayout);
        tabChats = viewMyEventsFragment.findViewById(R.id.myEventsTabChats);
        tabStatus = viewMyEventsFragment.findViewById(R.id.myEventsTabStatus);
        viewPager2 = viewMyEventsFragment.findViewById(R.id.myEventsViewPager);

        //Log.i(TAG, "tabLayout.getTabCount: " + tabLayout.getTabCount());
        //Log.i(TAG, "tabLayout.getTabCount: " + viewPager2.toString());
    }


    private void updateToolbar(){
        toolbar.setTitle(R.string.title_my_events);
        toolbar.setLogo(R.drawable.ic_event_note);
    }

    private void updateMainFAB(){
        floatingActionButton.setImageResource(R.drawable.ic_chats_person_add);
    }

    private void setListeners(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), AddUsersActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setColorForTabLayout(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.white);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.teal_200);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}

//    MyEventsViewModel myEventsViewModel =
//                new ViewModelProvider(this).get(MyEventsViewModel.class);
//
//        binding = FragmentMyEventsBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final TextView textView = binding.textMyEvents;
//        myEventsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }


// TAB - COLORS
//                if (tab.getPosition() == 1) {
//                        toolbar.setBackgroundColor(ContextCompat.getColor(requireActivity(),
//                        R.color.colorAccent));
//                        tabLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(),
//                        R.color.colorAccent));
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireActivity(),
//                        R.color.colorAccent));
//                        }