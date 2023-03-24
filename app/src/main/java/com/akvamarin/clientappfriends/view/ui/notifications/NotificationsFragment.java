package com.akvamarin.clientappfriends.view.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.akvamarin.clientappfriends.R;

public class NotificationsFragment extends Fragment {

    private Toolbar toolbar;
    private NotificationsViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater layoutInflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View viewFragmentNotification = layoutInflater.inflate(R.layout.fragment_notifications, container, false);
        updateToolbar();

        viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        viewModel.updateToolbarTitle("Оповещения");

        return viewFragmentNotification;
    }

    private void updateToolbar(){
        toolbar = requireActivity().findViewById(R.id.top_toolbar);
        toolbar.setTitle(R.string.title_notifications);
        toolbar.setLogo(R.drawable.ic_notifications);
    }


}


//
//        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getTitle().observe(getViewLifecycleOwner(), textView::setText);