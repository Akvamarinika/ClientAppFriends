package com.akvamarin.clientappfriends.view;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.akvamarin.clientappfriends.BaseActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.enums.SortingType;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.dialog.AuthDialog;
import com.akvamarin.clientappfriends.view.ui.home.HomeAllEventsFragment;
import com.akvamarin.clientappfriends.view.ui.myevents.MyEventsFragment;
import com.akvamarin.clientappfriends.view.ui.notifications.NotificationsFragment;
import com.akvamarin.clientappfriends.view.ui.notifications.NotificationsViewModel;
import com.akvamarin.clientappfriends.view.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class AllEventsActivity extends BaseActivity{
    private static final String TAG = "viewModel";
    NotificationsViewModel notificationsViewModel;

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigation;
    private PreferenceManager preferenceManager;
    private String lastFragmentTag;

    //private InternetModeChangeReceiver internetModeChangeReceiver;
    //private BroadcastReceiver broadcastReceiver = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);

        replaceFragment(new HomeAllEventsFragment(), HomeAllEventsFragment.class.getSimpleName());
        initBottomNavigationView();
        initToolbar();

        preferenceManager = new PreferenceManager(getApplicationContext());
        lastFragmentTag = preferenceManager.getString(Constants.PREF_LAST_FRAGMENT);

        if (lastFragmentTag == null) {
            lastFragmentTag = HomeAllEventsFragment.class.getSimpleName();
        }

        Fragment lastFragment = getSupportFragmentManager().findFragmentByTag(lastFragmentTag);

        if (lastFragment == null) {
            lastFragment = new HomeAllEventsFragment(); //создает нов. фрагмент, если он не существует
        }

        replaceFragment(lastFragment, lastFragmentTag);
    }

    private void initToolbar(){
        /* для обратной совместимости API Level < 21.  Экшнбар */
        toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);

        // Toolbar: backArrow
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initBottomNavigationView() {
        bottomNavigation = findViewById(R.id.bottom_navigation_view);

        bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home_all_events:
                    replaceFragment(new HomeAllEventsFragment(), HomeAllEventsFragment.class.getSimpleName());
                    return true;
                case R.id.navigation_notifications:
                    if (isAuthenticated()) {
                        replaceFragment(new NotificationsFragment(), NotificationsFragment.class.getSimpleName());
                        return true;
                    } else {
                        showAuthDialog();
                        return false;
                    }
                case R.id.navigation_my_events:
                    if (isAuthenticated()) {
                        replaceFragment(new MyEventsFragment(), MyEventsFragment.class.getSimpleName());
                        return true;
                    } else {
                        showAuthDialog();
                        return false;
                    }
                case R.id.navigation_profile:
                    if (isAuthenticated()) {
                        replaceFragment(new ProfileFragment(), ProfileFragment.class.getSimpleName());
                        return true;
                    } else {
                        showAuthDialog();
                        return false;
                    }
            }
            return false;
        });
    }


    private boolean isAuthenticated() {
        return preferenceManager != null && preferenceManager.getString(Constants.KEY_APP_TOKEN) != null;
    }

    private void showAuthDialog() {
        AuthDialog dialog = new AuthDialog(AllEventsActivity.this);
        dialog.show();
    }

    private void replaceFragment(Fragment fragment, String tag) {
        lastFragmentTag = tag; // update last displayed fragment tag
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_container, fragment, tag);
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
       /*  IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);//
        registerReceiver(broadcastReceiver, filter);*/
    }

    @Override
    public void onPause() {
        super.onPause();
        preferenceManager.putString(Constants.PREF_LAST_FRAGMENT, lastFragmentTag);
    }

    /** запускает AllEventsActivity из текущего Контекста
     * очистит все существующие действия в верхней части стека, перед запуском нового
     * **/
    public static void startFrom(Context context) {
        Intent intent = new Intent(context, AllEventsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    public void onSortOptionSelected(SortingType sortingType) {
        HomeAllEventsFragment homeFragment = (HomeAllEventsFragment) getSupportFragmentManager()
                .findFragmentByTag(HomeAllEventsFragment.class.getSimpleName());

        if (homeFragment != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            homeFragment.onSortOptionSelected(sortingType);
            homeFragment.filteredEventList();
        }
    }
}