package com.akvamarin.clientappfriends.view.ui.myevents;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyEventsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MyEventsViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is MyEvents fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}