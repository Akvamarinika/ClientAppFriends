package com.akvamarin.clientappfriends.ui.home;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.InfoEventActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.addevent.AddEventActivity;
import com.akvamarin.clientappfriends.dto.Event;
import com.akvamarin.clientappfriends.dto.User;
import com.akvamarin.clientappfriends.enums.DayPeriodOfTime;
import com.akvamarin.clientappfriends.enums.Partner;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HomeAllEventsFragment extends Fragment implements IEventRecyclerListener {

    private Toolbar toolbar;
    private MenuItem searchMenuItem;
    private MenuItem filterMenuItem;
    private MenuItem arrowBackMenuItem;

    private static final String TAG = "recyclerEvents";
    private HomeAllEventsViewModel viewModel;

    /* соединяются адаптером Recycler & List: */
    private RecyclerView recyclerViewAllEvents;
    private static final List<Event> eventList = new ArrayList<>();

    private IEventRecyclerListener eventListener;

    private FloatingActionButton floatingActionButton;
    private EventAdapter eventAdapter;

    public View onCreateView(@NonNull LayoutInflater layoutInflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View viewFragmentHome = layoutInflater.inflate(R.layout.fragment_home_all_events, container, false);
        recyclerViewAllEvents = viewFragmentHome.findViewById(R.id.recycler_view_all_events);
        setHasOptionsMenu(true);    // нужен для вызова onCreateOptionsMenu
        updateToolbar();

        return viewFragmentHome;
    }

    private void updateToolbar(){
        toolbar = requireActivity().findViewById(R.id.top_toolbar);
        toolbar.setLogo(R.drawable.logo1mini);
        toolbar.setTitle("");
    }

    private void updateMainFAB(){
        floatingActionButton.setImageResource(R.drawable.ic_add);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventListener = this;
        initEventList();
        eventAdapter = new EventAdapter(eventList, eventListener);
        recyclerViewAllEvents.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewAllEvents.setAdapter(eventAdapter);

        floatingActionButton = requireActivity().findViewById(R.id.fab_btn);
        updateMainFAB();
        floatingActionButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            startActivity(intent);
        });

    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initEventList(){
        /* TODO получить события с сервера и распарсить */

        Log.d(TAG, "init event list: preparing ...");

        if (eventList.isEmpty()) {

            Event event1 = new Event(new User("Виктор", 28, "https://meragor.com/files/styles//ava_800_800_wm/sfztn_boy_avatar_1.jpg"), "Бары, кафе и рестораны", "Пойти в бар 22222222222 2222 22222222 2222",
                    "Описание события", LocalDate.of(2022, 10, 29), Partner.COMPANY, LocalDateTime.now());
            event1.setPeriodOfTime(DayPeriodOfTime.EVENING);
            event1.setEventDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n" +
                    "Pellentesque sed nisi et diam suscipit ullamcorper.\n" +
                    "Cras feugiat enim finibus egestas ullamcorper.\n" +
                    "Phasellus id quam bibendum, luctus augue a, lacinia turpis.\n" +
                    "Duis vitae arcu id sapien eleifend sagittis ac eget risus.\n" +
                    "Quisque euismod lorem consectetur tellus elementum, a accumsan lorem commodo.\n" +
                    "Aenean dignissim enim at ligula varius, quis viverra metus maximus.\n" +
                    "Maecenas et ipsum eu tortor feugiat efficitur in sed ex.");
            eventList.add(event1);

            Event event2 = new Event(new User("Анна", 25, "https://meragor.com/files/styles//ava_800_800_wm/_5_5.jpg"), "Прогулки", "Погулять 111111111111111111111111111111111111111111111111 111111111111111111111111111111111111111111",
                    "Описание события", LocalDate.now(), Partner.ALL, LocalDateTime.now());
            event2.setPeriodOfTime(DayPeriodOfTime.MORNING);
            event2.setEventDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n" +
                    "Pellentesque sed nisi et diam suscipit ullamcorper.\n" +
                    "Cras feugiat enim finibus egestas ullamcorper.\n" +
                    "Phasellus id quam bibendum, luctus augue a, lacinia turpis.\n" +
                    "Duis vitae arcu id sapien eleifend sagittis ac eget risus.\n" +
                    "Quisque euismod lorem consectetur tellus elementum, a accumsan lorem commodo.\n" +
                    "Aenean dignissim enim at ligula varius, quis viverra metus maximus.\n" +
                    "Maecenas et ipsum eu tortor feugiat efficitur in sed ex.");
            eventList.add(event2);

            Event event3 = new Event(new User("Александр", 23, "https://demotivation.ru/wp-content/uploads/2020/11/905e85b5e1f2b5e1935c81b3c2478829.jpg"), "Бары, кафе и рестораны", "Выпить чашечку кофе",
                    "Описание события", LocalDate.now(), Partner.MAN, LocalDateTime.now());
            event3.setPeriodOfTime(DayPeriodOfTime.DAY);
            event3.setEventDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n" +
                    "Pellentesque sed nisi et diam suscipit ullamcorper.\n" +
                    "Cras feugiat enim finibus egestas ullamcorper.\n" +
                    "Phasellus id quam bibendum, luctus augue a, lacinia turpis.\n" +
                    "Duis vitae arcu id sapien eleifend sagittis ac eget risus.\n" +
                    "Quisque euismod lorem consectetur tellus elementum, a accumsan lorem commodo.\n" +
                    "Aenean dignissim enim at ligula varius, quis viverra metus maximus.\n" +
                    "Maecenas et ipsum eu tortor feugiat efficitur in sed ex.");
            eventList.add(event3);

            Event event4 = new Event(new User("Виктория", 20, "https://meragor.com/files/styles//ava_800_800_wm/avatar-211226-001768.png"), "Спорт", "Покататься на коньках",
                    "Описание события", LocalDate.of(2022, 8, 21), Partner.GIRL, LocalDateTime.now());
            event4.setPeriodOfTime(DayPeriodOfTime.NIGHT);
            eventList.add(event4);

            Event event5 = new Event(new User("Инна", 31, "https://meragor.com/files/styles//ava_800_800_wm/avatar-210866-000320.png"), "Кино, театры", "Пойти в театр",
                    "Описание события", LocalDate.of(2022, 5, 10), Partner.COMPANY, LocalDateTime.now());
            event5.setPeriodOfTime(DayPeriodOfTime.EVENING);
            eventList.add(event5);

            Event event6 = new Event(new User("Алекс", 35, "https://meragor.com/files/styles//ava_800_800_wm/sfztn_boy_avatar_18.jpg"), "Концерты", "Сходить на концерт Within Temptation",
                    "Описание события", LocalDate.of(2022, 3, 30), Partner.ALL, LocalDateTime.now());
            event6.setPeriodOfTime(DayPeriodOfTime.EVENING);
            eventList.add(event6);

            Event event7 = new Event(new User("Марина", 27, "https://meragor.com/files/styles//ava_800_800_wm/2_8.jpg"), "Походы", "Пойти в поход на Витязь",
                    "Описание события", LocalDate.of(2022, 6, 24), Partner.MAN, LocalDateTime.now());
            event7.setPeriodOfTime(DayPeriodOfTime.DAY);
            eventList.add(event7);

            Event event8 = new Event(new User("Пётр", 27, "https://it-doc.info/wp-content/uploads/2019/06/avatar-9.jpg"), "Фестивали", "Пойти на косплей фестиваль",
                    "Описание события", LocalDate.of(2022, 6, 24), Partner.ALL, LocalDateTime.now());
            event8.setPeriodOfTime(DayPeriodOfTime.NIGHT);
            eventList.add(event8);

            Event event9 = new Event(new User("Никита", 25, "https://it-doc.info/wp-content/uploads/2019/06/avatar-guitar-man.jpg"), "Бары, кафе и рестораны", "Выпить пива",
                    "Описание события", LocalDate.of(2022, 8, 20), Partner.COMPANY, LocalDateTime.now());
            event9.setPeriodOfTime(DayPeriodOfTime.MORNING);
            eventList.add(event9);

            Event event10 = new Event(new User("Николай", 32, "https://meragor.com/files/styles//ava_800_800_wm/sfztn_boy_avatar_64.jpg"), "Разное", "Отдохнуть на природе с шашлычками",
                    "Описание события", LocalDate.of(2022, 7, 27), Partner.COMPANY, LocalDateTime.now());
            event10.setPeriodOfTime(DayPeriodOfTime.DAY);
            eventList.add(event10);
        }

        eventList.sort((event, otherEvent) -> otherEvent.getDateTimeCreated().compareTo(event.getDateTimeCreated())); // DESC

    }

    /*** Click card Event: ***/
    @Override
    public void onClickRecyclerEventSelected(int index){
        //Toast.makeText(getActivity(), "Click on " + index, Toast.LENGTH_SHORT).show();

        Event currentEvent = eventList.get(index);
        Intent intent = new Intent(getActivity(), InfoEventActivity.class);
        intent.putExtra("current_event", currentEvent);
        startActivity(intent);

    }


    /* toolbar: наполнение: */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.top_nav_menu_all_events, menu);
        super.onCreateOptionsMenu(menu, inflater);

        searchMenuItem = menu.findItem(R.id.event_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        /* вызывается, когда меню-виджет поиска переходит в активное состояние */
//        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem menuItem) {
//                boolean isSearch = true;
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
//                boolean isSearch = false;
//                return true;
//            }
//        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // open search
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                List<Event> newEventList = new ArrayList<>();
                for (Event event : eventList) {
                    String eventName = event.getEventName().toLowerCase();

                    if (eventName.contains(newText)) {
                        newEventList.add(event);
                    }
                }
                eventAdapter.setFilter(newEventList);
                return true;
            }
        });


    }

    /* toolbar: выбор элемента*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.event_filter:
                Intent intent = new Intent();
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
    public static List<Event> getEventList() {
        return eventList;
    }




}




//        HomeAllEventsViewModel homeViewModel =
//                new ViewModelProvider(this).get(HomeAllEventsViewModel.class);

//        binding = FragmentHomeAllEventsBinding.inflate(layoutInflater, container, false);
//        View root = binding.getRoot();

//        final TextView textView = binding.textHomeAllEvents;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);