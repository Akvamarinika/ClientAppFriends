package com.akvamarin.clientappfriends.view.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.api.presentor.eventdata.EventDataApi;
import com.akvamarin.clientappfriends.api.presentor.eventdata.EventListCallback;
import com.akvamarin.clientappfriends.domain.dto.EventFilter;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;
import com.akvamarin.clientappfriends.domain.enums.SortingType;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.EventFilterPreferences;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.dialog.ErrorDialog;
import com.akvamarin.clientappfriends.view.infoevent.InfoEventActivity;
import com.akvamarin.clientappfriends.view.addevent.AddEventActivity;
import com.akvamarin.clientappfriends.view.dialog.AuthDialog;
import com.akvamarin.clientappfriends.view.ui.home.filter.FilterBottomSheetFragment;
import com.akvamarin.clientappfriends.view.ui.home.filter.FilterBottomSheetListener;
import com.akvamarin.clientappfriends.view.ui.home.sort.SortBottomSheetFragment;
import com.akvamarin.clientappfriends.view.ui.home.sort.SortBottomSheetListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeAllEventsFragment extends Fragment implements IEventRecyclerListener, SortBottomSheetListener, FilterBottomSheetListener {

    private Toolbar toolbar;
    private MenuItem searchMenuItem;
    private MenuItem filterMenuItem;
    private MenuItem arrowBackMenuItem;
    private View rootViewFragmentHome;

    private static final String TAG = "HomeFragment";
    private HomeAllEventsViewModel viewModel;

    /* соединяются адаптером Recycler & List: */
    private RecyclerView recyclerViewAllEvents;
    private final List<ViewEventDTO> eventList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private IEventRecyclerListener eventListener;

    private FloatingActionButton floatingActionButton;
    private ProgressBar progressBar;

    private EventDataApi eventDataApi;

    private PreferenceManager preferenceManager;
    private EventFilter eventFilter = new EventFilter();

    public View onCreateView(@NonNull LayoutInflater layoutInflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        rootViewFragmentHome = layoutInflater.inflate(R.layout.fragment_home_all_events, container, false);
        recyclerViewAllEvents = rootViewFragmentHome.findViewById(R.id.recycler_view_all_events);
        progressBar = rootViewFragmentHome.findViewById(R.id.progressBar);
        setHasOptionsMenu(true);    // фрагмент имеет собственное меню параметров
        updateToolbar();
        init();

       // showPopupWindow(rootViewFragmentHome, container);

        return rootViewFragmentHome;
    }

    private void init(){
        preferenceManager = new PreferenceManager(requireActivity());
        eventDataApi = new EventDataApi(requireActivity());
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
        boolean isApplyFilters = setStoredFilters();

        if (isApplyFilters){
            filteredEventList();
        } else {
            initEventList();
        }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean setStoredFilters() {
        EventFilterPreferences filterPreferences = new EventFilterPreferences(requireContext());
        EventFilter storedFilter = filterPreferences.getEventFilter();

        if (storedFilter != null) {
            this.eventFilter = storedFilter;
            return true;
        }
        return false;
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
        progressBar.setVisibility(View.VISIBLE);

        if (eventList.isEmpty()) {
            eventDataApi.requestAllEvents(new EventListCallback() {
                @Override
                public void onEventListRetrieved(List<ViewEventDTO> viewEventDTOList) {
                    progressBar.setVisibility(View.GONE);
                    updateEventAdapter(viewEventDTOList);
                }

                @Override
                public void onEventListRetrievalError(int responseCode) {
                    progressBar.setVisibility(View.GONE);
                    showErrorDialog(responseCode);
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void filteredEventList(){
        Log.d(TAG, "filtered event list: preparing ...");
        progressBar.setVisibility(View.VISIBLE);

        eventDataApi.requestFilterEvents(eventFilter, new EventListCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEventListRetrieved(List<ViewEventDTO> viewEventDTOList) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "filtered event list size: " + viewEventDTOList.size());
                updateEventAdapter(viewEventDTOList);
            }

            @Override
            public void onEventListRetrievalError(int responseCode) {
                progressBar.setVisibility(View.GONE);
                showErrorDialog(responseCode);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateEventAdapter(List<ViewEventDTO> viewEventDTOList){
        eventList.clear();
        eventList.addAll(viewEventDTOList);
        eventAdapter.notifyDataSetChanged(); // Notify adapter
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

        // visible menu item
        MenuItem itemFilter = menu.findItem(R.id.event_filter);
        MenuItem itemSort = menu.findItem(R.id.event_sort);
        MenuItem itemSearch = menu.findItem(R.id.event_search);
        itemFilter.setVisible(true);
        itemSort.setVisible(true);
        itemSearch.setVisible(true);
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
                openFilterBottomSheet();  // filters
                break;
            case R.id.event_sort:
                openSortBottomSheet();    // sort
                break;
            case R.id.event_search:
                Log.i("item id ", item.getItemId() + "");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
//        if (item.getItemId() == R.id.arrow_back){
//            return true;
//        }
    }

    private void showErrorDialog(int responseCode) {
        ErrorDialog dialog = new ErrorDialog(requireActivity(), responseCode);
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        setStoredFilters();
        filteredEventList();

        Bundle sortArgs = getArguments();
        if (sortArgs != null && sortArgs.containsKey("sortingType")) {
            SortingType sortingType = (SortingType) sortArgs.getSerializable("sortingType");
            eventFilter.setSortingType(sortingType);
            saveFilterParams(eventFilter);
            getArguments().remove("sortingType");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSortOptionSelected(SortingType sortingType) {
        Log.d(TAG,"onSortOptionSelected " + sortingType);

        if (sortingType != null) {
            eventFilter.setSortingType(sortingType);
            saveFilterParams(eventFilter);
            filteredEventList();
        }

        SortBottomSheetFragment sortBottomSheetFragment = (SortBottomSheetFragment) getChildFragmentManager().findFragmentByTag("sortBottomSheet");
        if (sortBottomSheetFragment != null) {
            sortBottomSheetFragment.dismiss();
        }
    }

    private void openSortBottomSheet() {
        SortBottomSheetFragment sortBottomSheet = SortBottomSheetFragment.newInstance();
        sortBottomSheet.setListener(this); //listener to receive callback in this fragment
        sortBottomSheet.show(getChildFragmentManager(), "sortBottomSheet");
        Log.d(TAG, "openSortBottomSheet() " + getChildFragmentManager());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onFilterApplied(EventFilter eventFilter) {
        Log.d(TAG,"onFilterOptionSelected " + eventFilter);

        if (eventFilter != null) {
            this.eventFilter.setPartnerList(eventFilter.getPartnerList());
            this.eventFilter.setCategory(eventFilter.getCategory());
            this.eventFilter.setCategoryIds(eventFilter.getCategoryIds());
            this.eventFilter.setPeriodOfTimeList(eventFilter.getPeriodOfTimeList());
            this.eventFilter.setDaysOfWeekList(eventFilter.getDaysOfWeekList());
            saveFilterParams(eventFilter);
            filteredEventList();
        }
    }

    private void saveFilterParams(EventFilter eventFilter) {
        EventFilterPreferences filterPreferences = new EventFilterPreferences(requireContext());
        filterPreferences.saveEventFilter(eventFilter);
    }

    private void openFilterBottomSheet() {
        FilterBottomSheetFragment filterBottomSheet = FilterBottomSheetFragment.newInstance();
        filterBottomSheet.setListener(this); //listener to receive callback in this fragment
        filterBottomSheet.show(getChildFragmentManager(), "filterBottomSheet");
        Log.d(TAG, "openFilterBottomSheet() " + getChildFragmentManager());
    }

}
