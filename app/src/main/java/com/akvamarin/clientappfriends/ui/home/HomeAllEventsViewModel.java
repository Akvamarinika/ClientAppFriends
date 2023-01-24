package com.akvamarin.clientappfriends.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeAllEventsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeAllEventsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}