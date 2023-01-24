package com.akvamarin.clientappfriends.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> title = new MutableLiveData<>();

    public NotificationsViewModel() { }

    public LiveData<String> getTitle() {
        return title;
    }

    public void updateToolbarTitle(String title){
        this.title.setValue(title);
    }
}