package com.akvamarin.clientappfriends;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.akvamarin.clientappfriends.receivers.InternetReceiver;
import com.akvamarin.clientappfriends.ui.home.HomeAllEventsFragment;
import com.akvamarin.clientappfriends.ui.myevents.MyEventsFragment;
import com.akvamarin.clientappfriends.ui.notifications.NotificationsFragment;
import com.akvamarin.clientappfriends.ui.notifications.NotificationsViewModel;
import com.akvamarin.clientappfriends.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class AllEventsActivity extends AppCompatActivity {
    private static final String TAG = "viewModel";
    NotificationsViewModel notificationsViewModel;

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigation;

    //private InternetModeChangeReceiver internetModeChangeReceiver;
    private BroadcastReceiver broadcastReceiver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);

        replaceFragment(new HomeAllEventsFragment());
        initBottomNavigationView();
        initToolbar();

        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        notificationsViewModel.getTitle().observe(this, titleNotifications -> {
            Log.d(TAG, "set title: оповещения ");
            Log.d(TAG, titleNotifications);
            toolbar.setTitle(titleNotifications);
        });





        broadcastReceiver = new InternetReceiver();
        //IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);//
        //registerReceiver(broadcastReceiver, filter);
        //broadcastReceiver.getResultData()


    }

    private void initToolbar(){
        /* для обратной совместимости API Level < 21.  Экшнбар */
        toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);

        // Toolbar: backArrow
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initBottomNavigationView(){
        bottomNavigation = findViewById(R.id.bottom_navigation_view);

        bottomNavigation.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.navigation_home_all_events:
                    HomeAllEventsFragment homeAllEventsFragment = new HomeAllEventsFragment();
                    replaceFragment(homeAllEventsFragment);
                    return true;
                case R.id.navigation_notifications:
                    NotificationsFragment notificationsFragment = new NotificationsFragment();
                    replaceFragment(notificationsFragment);
                    return true;
                case R.id.navigation_my_events:
                    MyEventsFragment myEventsFragment = new MyEventsFragment();
                    replaceFragment(myEventsFragment);
                    return true;
                case R.id.navigation_profile:
                    ProfileFragment profileFragment = new ProfileFragment();
                    replaceFragment(profileFragment);
                    return true;
            }

            return false;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onSupportNavigateUp() {  // Toolbar: backArrow
        onBackPressed();    //нажатие на кнопку назад внизу экрана
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);//
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}