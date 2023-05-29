package com.akvamarin.clientappfriends.view.addevent;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.akvamarin.clientappfriends.api.ErrorResponse;
import com.akvamarin.clientappfriends.api.ErrorUtils;
import com.akvamarin.clientappfriends.api.RetrofitService;
import com.akvamarin.clientappfriends.api.connection.EventApi;
import com.akvamarin.clientappfriends.api.connection.EventCategoryApi;
import com.akvamarin.clientappfriends.BaseActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.EventCategoryDTO;
import com.akvamarin.clientappfriends.domain.dto.EventDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;
import com.akvamarin.clientappfriends.domain.enums.DayPeriodOfTime;
import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.AllEventsActivity;
import com.akvamarin.clientappfriends.view.infoevent.InfoEventActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener{
    private static final String TAG = "AddEvent";
    private static final int MIN_TEXT_LENGTH = 1;
    private static final int MAX_COUNT_LINE = 10;
    private static final String EMPTY_STRING = "";
    private static final String PATTERN_DATE_FORMAT = "yyyy-MM-dd";

    private Toolbar toolbar;
    //private MenuItem searchMenuItem;
    //private MenuItem filterMenuItem;
    //private MenuItem arrowBackMenuItem;

    private Spinner spinnerEventCategories;

    private Button buttonShowDialogDatePicker;
    private final Calendar calendarSelectedDate = Calendar.getInstance();

    private TextInputLayout textInputLayoutEventName;
    private EditText editTextEventName;

    private TextInputLayout textInputLayoutEventDescription;
    private EditText editTextEventDescription;

    private RadioGroup radioGroupPartner;
    private RadioGroup radioGroupTwentyFourHours;

    private Button buttonSave;

    private PreferenceManager preferenceManager;

    private List<EventCategoryDTO> categoriesList = new ArrayList<>();
    private RetrofitService retrofitService;
    private EventCategoryApi categoryApi;
    private EventApi eventApi;

    private String loading;
    private ViewEventDTO viewEventDTO;
    private Long eventId;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        eventId = (Long) getIntent().getSerializableExtra("current_event_id");

        initWidgets();
        setCreateTextOnWidgets();

        initEventFromServer();
        loadSpinnerEventCategoriesFromServer();

        buttonShowDialogDatePicker.setOnClickListener(view -> showSetDateDialog()); // new View.OnClickListener(){ /*method onClick*/ }
        setNewDate(null);

        checkCountLinesInEventDescription();

        buttonSave.setOnClickListener(view -> {
            if (checkTextFieldsInFormOnCount()){
                EventDTO event = setValuesForEventDTO();

                if (eventId == null) {
                    sendNewEventOnServer(event); //event create
                } else if (isOrganizer()){
                    Log.d(TAG, "event date : " + viewEventDTO.getDate());
                    event.setId(eventId);
                    updateEventOnServer(event); //event update
                } else if (isNeedAnalogEvent()){
                    sendNewEventOnServer(event); //event analog
                }
            }
        });

        editTextEventDescription.setOnTouchListener((v, event) -> {
            if (editTextEventDescription.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });

    }

    private void initWidgets(){
        loading = getApplicationContext().getString(R.string.loading);
        toolbar = findViewById(R.id.top_toolbar);
        buttonShowDialogDatePicker = findViewById(R.id.buttonShowDateDialog);
        textInputLayoutEventName = findViewById(R.id.textInputLayoutEventName);
        editTextEventName = findViewById(R.id.editTextEventName);
        textInputLayoutEventDescription = findViewById(R.id.textInputLayoutEventDescription);
        editTextEventDescription = findViewById(R.id.editTextEventDescription);
        radioGroupPartner = findViewById(R.id.radioGroupPartner);
        radioGroupTwentyFourHours = findViewById(R.id.radioGroupTwentyFourHours);
        buttonSave = findViewById(R.id.buttonSaveNewEvent);
        spinnerEventCategories = findViewById(R.id.eventCategoriesSpinner);

        preferenceManager = new PreferenceManager(getApplicationContext());
        retrofitService = RetrofitService.getInstance(getApplicationContext());
        categoryApi = retrofitService.getRetrofit().create(EventCategoryApi.class);
        eventApi = retrofitService.getRetrofit().create(EventApi.class);
    }

    private void setCreateTextOnWidgets(){
        /* для обратной совместимости API Level < 21.  Экшнбар */
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.title_add_new_event));
        buttonSave.setText(R.string.add_btn_label_done);

        // Toolbar: backArrow
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setUpdateTextOnWidgets(){
        buttonSave.setText(R.string.update_btn_label_done);
        toolbar.setTitle(getString(R.string.title_update_event));
    }

    private boolean isOrganizer(){
        long userId = preferenceManager.getLong(Constants.KEY_USER_ID);
        return viewEventDTO != null && viewEventDTO.getUserOwner().getId() != null && userId == viewEventDTO.getUserOwner().getId();
    }

    private boolean isNeedAnalogEvent(){
        long userId = preferenceManager.getLong(Constants.KEY_USER_ID);
        return viewEventDTO != null && viewEventDTO.getUserOwner().getId() != null && userId != viewEventDTO.getUserOwner().getId();
    }

    /*** Заполнение полей формы при
     * методе Update:
     * ***/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void eventFillingOutFormFields() {
        String eventName = viewEventDTO.getName();
        String eventDescription = viewEventDTO.getDescription();
        String eventDate = viewEventDTO.getDate();
        DayPeriodOfTime periodOfTime = viewEventDTO.getPeriodOfTime();
        Partner partner = viewEventDTO.getPartner();

        editTextEventName.setText(eventName);
        editTextEventDescription.setText(eventDescription);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_DATE_FORMAT);
        LocalDate localDate = LocalDate.parse(eventDate, formatter);
        Date date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        setNewDate(date);

        int periodOfTimeIdCheckedElement =  DayPeriodOfTime.getIdCheckedElement(periodOfTime);
        radioGroupTwentyFourHours.check(periodOfTimeIdCheckedElement);

        int partnerIdCheckedElement =  Partner.getIdCheckedElement(partner);
        radioGroupPartner.check(partnerIdCheckedElement);


    }

    /*** Spinner category methods:
     * Получение значений от Сервера
     * ***/
    private void loadSpinnerEventCategoriesFromServer(){
        showProgressDialog(loading);

        categoryApi.getAllCategories().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<EventCategoryDTO>> call, @NonNull Response<List<EventCategoryDTO>> response) {
                dismissProgressDialog();

                if (response.isSuccessful()) {
                    categoriesList = response.body();
                    ArrayAdapter<EventCategoryDTO> adapter = new ArrayAdapter<>(AddEventActivity.this, android.R.layout.simple_spinner_item, categoriesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerEventCategories.setAdapter(adapter);

                    // Set the selected item
                    Log.d(TAG, "viewEventDTO : " + viewEventDTO);
                    if (viewEventDTO != null) {
                        EventCategoryDTO selectedCategory = viewEventDTO.getEventCategory();
                        int selectedItemPosition = adapter.getPosition(selectedCategory);
                        spinnerEventCategories.setSelection(selectedItemPosition);
                    }

                    Log.d(TAG, "categoriesList size: " + categoriesList.size());
                } else {
                    Toast.makeText(getApplicationContext(), "getAllCategoriesDTOs() code:" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<EventCategoryDTO>> call, @NonNull Throwable t) {
                dismissProgressDialog();
                Toast.makeText(AddEventActivity.this, "getAllCategoriesDTOs() filed!!!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "error: " + t.fillInStackTrace());
            }
        });
    }

    /*** Заполнение объекта EventDTO
     * из формы:
     * ***/
    private EventDTO setValuesForEventDTO(){
        EventCategoryDTO selectedCategory = (EventCategoryDTO) spinnerEventCategories.getSelectedItem();
        Long categoryId = selectedCategory.getId();
        Log.d(TAG, "selectedCategory : " + selectedCategory.getId());

        String eventName = editTextEventName.getText().toString().trim();
        String eventDescription = editTextEventDescription.getText().toString().trim();

        //LocalDate date = Instant.ofEpochMilli(calendar.getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN_DATE_FORMAT);
        String dateString = dateFormat.format(calendarSelectedDate.getTime());

        int radioIDPartner = radioGroupPartner.getCheckedRadioButtonId();
        RadioButton radioBtnPartner = radioGroupPartner.findViewById(radioIDPartner);
        String partnerText = radioBtnPartner.getText().toString();
        Log.d(TAG, "radio partner: " + partnerText);

        int radioIDTwentyFourHours  = radioGroupTwentyFourHours.getCheckedRadioButtonId();
        RadioButton radioBtnTwentyFourHours = radioGroupTwentyFourHours.findViewById(radioIDTwentyFourHours);
        String periodOfTimeText = radioBtnTwentyFourHours.getText().toString();
        Log.d(TAG, "radio  periodOfTime" + periodOfTimeText);

        EventDTO event = new EventDTO();
        event.setEventCategoryId(categoryId); //id
        event.setName(eventName);
        event.setDescription(eventDescription);
        event.setDate(dateString); //format "yyyy-MM-dd"
        event.setPartner(Partner.getEnumValue(partnerText));
        event.setPeriodOfTime(DayPeriodOfTime.getEnumValue(periodOfTimeText));

        event.setOwnerId(Long.valueOf("-1")); // назначит Владельца сервер по токену, чтобы значение было не null
        Log.d(TAG, "onClick: " + event);

        return event;
    }

    /*** EventDTO send on Server:
     * ***/
    private void sendNewEventOnServer(EventDTO event){
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);

        eventApi.createEvent(event, new AuthToken(authToken)).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Log.d(TAG, String.format("Response: %s %s%n", response.code(), response));
                Log.d(TAG, String.format("My token: %s", authToken));

                if(response.isSuccessful()){
                    Log.d(TAG, response.code() + " event added");
                    startAllEventsActivityActivity();
                } else {
                    ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                    Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.d(TAG, "error: " + t.fillInStackTrace());
            }
        });
    }

    /*** EventDTO update on Server:
     * ***/
    private void updateEventOnServer(EventDTO event){
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);

        if (event.getId() != null) {
            eventApi.updateEvent(event, new AuthToken(authToken)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ViewEventDTO> call, @NonNull Response<ViewEventDTO> response) {
                    Log.d(TAG, String.format("Response: %s %s%n", response.code(), response));
                    Log.d(TAG, String.format("My token: %s", authToken));

                    if(response.isSuccessful()){
                        Log.d(TAG, response.code() + " event update");

                        Intent intent = new Intent(getApplicationContext(), InfoEventActivity.class);
                        intent.putExtra("current_event_id", event.getId()); // event id
                        startActivity(intent);
                        finish();

                    } else {
                        ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                        Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ViewEventDTO> call, @NonNull Throwable t) {
                    Log.d(TAG, "error Update event: " + t.fillInStackTrace());
                }
            });
        }
    }

    /*** Get ViewEventDTO from on Server
     * for insert in form and next Update:
     * ***/
    private void initEventFromServer(){
        Log.d(TAG, "init event from server...");
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);

        if (eventId != null) {
            showProgressDialog(loading);
            eventApi.getEventById(eventId, new AuthToken(authToken)).enqueue(new Callback<>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(@NonNull Call<ViewEventDTO> call, @NonNull Response<ViewEventDTO> response) {
                    dismissProgressDialog();

                    if (response.isSuccessful() && response.body() != null) {
                        viewEventDTO = response.body();
                        setUpdateTextOnWidgets();
                        loadSpinnerEventCategoriesFromServer();
                        eventFillingOutFormFields();
                    } else {

                    }
                }

                @Override
                public void onFailure(@NonNull Call<ViewEventDTO> call, @NonNull Throwable t) {
                    dismissProgressDialog();
                    Log.d(TAG, "Error fetching one event: " + t.fillInStackTrace());
                }
            });
        }
    }

    /*** Methods for Date field: ***/
    private void setNewDate(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar selectedDate = Calendar.getInstance();

        if (date != null) {
            selectedDate.setTime(date);
            if (selectedDate.before(today)) {
                selectedDate.setTime(today.getTime());
            }
        } else {
            selectedDate.setTime(today.getTime());
        }

        calendarSelectedDate.set(Calendar.YEAR, selectedDate.get(Calendar.YEAR));
        calendarSelectedDate.set(Calendar.MONTH, selectedDate.get(Calendar.MONTH));
        calendarSelectedDate.set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH));

        buttonShowDialogDatePicker.setText(DateUtils.formatDateTime(this, calendarSelectedDate.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }


    //диалоговое окно для выбора даты
    private void showSetDateDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                calendarSelectedDate.get(Calendar.YEAR),
                calendarSelectedDate.get(Calendar.MONTH),
                calendarSelectedDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());

        datePickerDialog.show();
    }

    //метод-listener срабатывает, когда закрыли диалоговое окно
    @Override
    public void onDateSet(DatePicker datePickerView, int year, int month, int dayOfMonth) {
        calendarSelectedDate.set(Calendar.YEAR, year);
        calendarSelectedDate.set(Calendar.MONTH, month);
        calendarSelectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        setNewDate(calendarSelectedDate.getTime());
    }



    /*** Method count enters: ***/
    private void checkCountLinesInEventDescription(){
        editTextEventDescription.setOnKeyListener((v, keyCode, event) -> {

            if (keyCode == KeyEvent.KEYCODE_ENTER  && event.getAction() == KeyEvent.ACTION_DOWN) {
                return ((EditText) v).getLineCount() >= MAX_COUNT_LINE;
            }

            return false;
        });
    }

    /*** Methods check empty fields: ***/
    private boolean checkTextFieldsInFormOnCount(){
        String error = getString(R.string.error_count_symbols);
        boolean checkResult = true;

        if (this.checkCountSymbolsInEditText(editTextEventName)) {
            textInputLayoutEventName.setError(error);
            checkResult = false;
        } else {
            textInputLayoutEventName.setError(EMPTY_STRING);
        }

        if (this.checkCountSymbolsInEditText(editTextEventDescription)) {
            textInputLayoutEventDescription.setError(error);
            checkResult = false;
        } else {
            textInputLayoutEventDescription.setError(EMPTY_STRING);
        }

        return checkResult;
    }

    private boolean checkCountSymbolsInEditText(EditText editText) {
        int textLength = editText.getText().toString().trim().length();
        return textLength <= MIN_TEXT_LENGTH;
    }

    /***
     * Перенаправить в AllEventsActivity
     * **/
    private void startAllEventsActivityActivity() {
        AddEventActivity.startFrom(getApplicationContext());
        finish();
    }

    /** запускает AllEventsActivity из текущего Контекста
     * очистит все существующие действия в верхней части стека, перед запуском нового
     * **/
    private static void startFrom(Context context) {
        Intent intent = new Intent(context, AllEventsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {  // Toolbar: backArrow
        onBackPressed();    //нажатие на кнопку назад внизу экрана
        finish(); // close the current activity
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        initEventFromServer();
    }

}