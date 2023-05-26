package com.akvamarin.clientappfriends.view.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.API.RetrofitService;
import com.akvamarin.clientappfriends.API.connection.EventApi;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.EventFilter;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.EventFilterPreferences;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.infoevent.InfoEventActivity;
import com.akvamarin.clientappfriends.view.addevent.AddEventActivity;
import com.akvamarin.clientappfriends.view.dialog.AuthDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeAllEventsFragment extends Fragment implements IEventRecyclerListener {

    private Toolbar toolbar;
    private MenuItem searchMenuItem;
    private MenuItem filterMenuItem;
    private MenuItem arrowBackMenuItem;
    private View rootViewFragmentHome;

    private static final String TAG = "recyclerEvents";
    private HomeAllEventsViewModel viewModel;

    /* соединяются адаптером Recycler & List: */
    private RecyclerView recyclerViewAllEvents;
    private final List<ViewEventDTO> eventList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private IEventRecyclerListener eventListener;

    private FloatingActionButton floatingActionButton;

    private RetrofitService retrofitService;
    private EventApi eventApi;

    private PreferenceManager preferenceManager;

    public View onCreateView(@NonNull LayoutInflater layoutInflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        rootViewFragmentHome = layoutInflater.inflate(R.layout.fragment_home_all_events, container, false);
        recyclerViewAllEvents = rootViewFragmentHome.findViewById(R.id.recycler_view_all_events);
        setHasOptionsMenu(true);    // фрагмент имеет собственное меню параметров
        updateToolbar();
        init();

       // showPopupWindow(rootViewFragmentHome, container);

        return rootViewFragmentHome;
    }

    private void init(){
        preferenceManager = new PreferenceManager(requireActivity());
        retrofitService = RetrofitService.getInstance(getContext());
        eventApi = retrofitService.getRetrofit().create(EventApi.class);
    }

    private void updateToolbar(){
        toolbar = requireActivity().findViewById(R.id.top_toolbar);
        toolbar.setLogo(R.drawable.logo1mini);
        toolbar.setTitle("");
    }

    private void updateMainFAB(){
        floatingActionButton.setImageResource(R.drawable.ic_add);
        floatingActionButton.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEventList();
        eventListener = this;
        eventAdapter = new EventAdapter(eventList, eventListener);
        recyclerViewAllEvents.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewAllEvents.setAdapter(eventAdapter);

        floatingActionButton = requireActivity().findViewById(R.id.fab_btn);
        updateMainFAB();

        floatingActionButton.setOnClickListener(v -> {
            if (isAuthenticated()) {
                Intent intent = new Intent(getActivity(), AddEventActivity.class);
                startActivity(intent);
            } else {
                showAuthDialog();
            }
        });

    }

    private boolean isAuthenticated() {
        return preferenceManager != null && preferenceManager.getString(Constants.KEY_APP_TOKEN) != null;
    }

    private void showAuthDialog() {
        AuthDialog dialog = new AuthDialog(requireActivity());
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initEventList(){
        Log.d(TAG, "init event list: preparing ...");

        eventApi.getAllEvents().enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<ViewEventDTO>> call, @NonNull Response<List<ViewEventDTO>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    eventList.clear();
                    eventList.addAll(response.body());
                    eventAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                } else {
                    Toast.makeText(requireActivity(), "getAllEvents()", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ViewEventDTO>> call, @NonNull Throwable t) {
                Toast.makeText(requireActivity(), "getAllEvents() --- Fail", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error fetching events: " + t.fillInStackTrace());
            }
        });

   //     eventList.sort((event, otherEvent) -> otherEvent.getDateTimeCreated().compareTo(event.getDateTimeCreated())); // DESC

    }

    /*** Click card Event: ***/
    @Override
    public void onClickRecyclerEventSelected(int index){
        if (isAuthenticated()) {
            ViewEventDTO currentEvent = eventList.get(index);
            Long currentEventId = currentEvent.getId();
            Intent intent = new Intent(getActivity(), InfoEventActivity.class);
            intent.putExtra("current_event_id", currentEventId);
            startActivity(intent);
        } else {
            showAuthDialog();
        }
    }


    /* toolbar: наполнение: */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.top_nav_menu_all_events, menu); //наполнение меню

        // Hide a menu item by ID
        //MenuItem itemToHide = menu.findItem(R.id.event_filter);
        //itemToHide.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);

        searchMenuItem = menu.findItem(R.id.event_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // open search
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                List<ViewEventDTO> newEventList = new ArrayList<>();
                for (ViewEventDTO event : eventList) {
                    String eventName = event.getName().toLowerCase();

                    if (eventName.contains(newText)) {
                        newEventList.add(event);
                    }
                }
                eventAdapter.setFilter(newEventList);
                return true;
            }
        });
    }


    /* toolbar: для обработки щелчков по элементам меню*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.event_filter:
                showPopupWindow(rootViewFragmentHome);  // filters
            case R.id.event_search:
                Log.i("item id ", item.getItemId() + "");
            default:
                return super.onOptionsItemSelected(item);
        }

//        if (item.getItemId() == R.id.arrow_back){
//            return true;
//        }
    }


    /** For ADD event from Client **/
    public List<ViewEventDTO> getEventList() {
        return eventList;
    }





    public void showPopupWindow(View view) {

        // Create a new PopupWindow
        View popupView = LayoutInflater.from(requireActivity()).inflate(R.layout.bottom_sheet_list_filters, (ViewGroup) rootViewFragmentHome, false);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Show the PopupWindow at half the screen height
        int popupHeight = getResources().getDisplayMetrics().heightPixels / 2;
        popupWindow.setHeight(popupHeight);

        // Set an animation for the PopupWindow
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

        // Set a background color for the PopupWindow
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Show the PopupWindow at the center of the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void showBottomSheetWithCustomItems() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_list_filters, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        // Get references to the views in the bottom sheet layout
        TextView textViewItem1 = bottomSheetView.findViewById(R.id.partnerLabel);
        Button buttonCancel = bottomSheetView.findViewById(R.id.partnerLabel);
        // Add more views as needed

        // Set click listeners for the items
        textViewItem1.setOnClickListener(v -> {
            // Handle item 1 click
            bottomSheetDialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        // Show the bottom sheet
        bottomSheetDialog.show();
    }


    public void saveFiltersParams() {
        EventFilter eventFilter = EventFilter.builder()
                .cityId(1L)
                .categoryIds(Arrays.asList(1L, 2L))
                .isUserOrganizer(true)
                // Set other properties
                .build();

        EventFilterPreferences preferences = new EventFilterPreferences(requireContext());
        preferences.saveEventFilter(eventFilter);

    }

    public void applyFilteringOptions() {
        EventFilterPreferences preferences = new EventFilterPreferences(requireContext());
        EventFilter eventFilter = preferences.getEventFilter();

        if (eventFilter != null) {
            // Use the retrieved eventFilter object
        } else {
            // No saved eventFilter found
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        initEventList();
    }

}




//        HomeAllEventsViewModel homeViewModel =
//                new ViewModelProvider(this).get(HomeAllEventsViewModel.class);

//        binding = FragmentHomeAllEventsBinding.inflate(layoutInflater, container, false);
//        View root = binding.getRoot();

//        final TextView textView = binding.textHomeAllEvents;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);