package com.akvamarin.clientappfriends.view.addevent;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.akvamarin.clientappfriends.API.ErrorResponse;
import com.akvamarin.clientappfriends.API.ErrorUtils;
import com.akvamarin.clientappfriends.API.RetrofitService;
import com.akvamarin.clientappfriends.API.connection.EventApi;
import com.akvamarin.clientappfriends.API.connection.EventCategoryApi;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.EventCategoryDTO;
import com.akvamarin.clientappfriends.domain.dto.EventDTO;
import com.akvamarin.clientappfriends.domain.enums.DayPeriodOfTime;
import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.AllEventsActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private static final String TAG = "AddEvent";
    private static final int MIN_TEXT_LENGTH = 1;
    private static final int MAX_COUNT_LINE = 10;
    private static final String EMPTY_STRING = "";

    private Toolbar toolbar;
    //private MenuItem searchMenuItem;
    //private MenuItem filterMenuItem;
    //private MenuItem arrowBackMenuItem;

    private Spinner spinnerEventCategories;

    private Button buttonShowDialogDatePicker;
    private final Calendar calendar = Calendar.getInstance();

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

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        initWidgets();

        /* для обратной совместимости API Level < 21.  Экшнбар */
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.title_add_new_event));

        // Toolbar: backArrow
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        uploadSpinnerEventCategoriesFromServer();
        //getSelectedItemFromSpinnerEventCategories();

        buttonShowDialogDatePicker.setOnClickListener(view -> showSetDateDialog()); // new View.OnClickListener(){ /*method onClick*/ }
        setNewDate();

        checkCountLinesInEventDescription();

        buttonSave.setOnClickListener(view -> {
            if (checkTextFieldsInFormOnCount()){

                EventDTO event = setValuesForEventDTO();
                sendNewEventOnServer(event); //event отправить на сервер

             //   HomeAllEventsFragment.getEventList().add(event);
                Intent intent = new Intent(this, AllEventsActivity.class);
                //intent.putExtra("Event_obj", event);
                startActivity(intent);
                finish();
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
        toolbar = findViewById(R.id.top_toolbar);
        buttonShowDialogDatePicker = findViewById(R.id.buttonShowDateDialog);
        textInputLayoutEventName = findViewById(R.id.textInputLayoutEventName);
        editTextEventName = findViewById(R.id.editTextEventName);
        textInputLayoutEventDescription = findViewById(R.id.textInputLayoutEventDescription);
        editTextEventDescription = findViewById(R.id.editTextEventDescription);
        radioGroupPartner = findViewById(R.id.radioGroupPartner);
        radioGroupTwentyFourHours = findViewById(R.id.radioGroupTwentyFourHours);
        buttonSave = findViewById(R.id.buttonSaveNewEvent);

        preferenceManager = new PreferenceManager(getApplicationContext());
        retrofitService = RetrofitService.getInstance(getApplicationContext());
        categoryApi = retrofitService.getRetrofit().create(EventCategoryApi.class);
        eventApi = retrofitService.getRetrofit().create(EventApi.class);
    }


    /*** Spinner category methods:
     * Получение значений от Сервера
     * ***/
    private void uploadSpinnerEventCategoriesFromServer(){
        spinnerEventCategories = findViewById(R.id.eventCategoriesSpinner);

        if (categoriesList.isEmpty()) {
            categoryApi.getAllCategories().enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<List<EventCategoryDTO>> call, @NonNull Response<List<EventCategoryDTO>> response) {
                    if (response.isSuccessful()) {
                        categoriesList = response.body();
                        ArrayAdapter<?> adapter = new ArrayAdapter<>(AddEventActivity.this, android.R.layout.simple_spinner_item, categoriesList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerEventCategories.setAdapter(adapter);
                        Log.d(TAG, "categoriesList size: " + categoriesList.size());
                    } else {
                        Toast.makeText(getApplicationContext(), "getAllCategoriesDTOs() code:" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<EventCategoryDTO>> call, @NonNull Throwable t) {
                    Toast.makeText(AddEventActivity.this, "getAllCategoriesDTOs() filed!!!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "error: " + t.fillInStackTrace());
                }
            });
        }
    }

    /*** Заполнение объекта EventDTO
     * из формы:
     * ***/
    private EventDTO setValuesForEventDTO(){
        EventCategoryDTO selectedCategory = (EventCategoryDTO) spinnerEventCategories.getSelectedItem();
        Long categoryId = selectedCategory.getId();

        String eventName = editTextEventName.getText().toString().trim();
        String eventDescription = editTextEventDescription.getText().toString().trim();

        //LocalDate date = Instant.ofEpochMilli(calendar.getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(calendar.getTime());

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

    /*** Methods for Date field: ***/
    private void setNewDate(){
        buttonShowDialogDatePicker.setText(DateUtils.formatDateTime(this, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    //диалоговое окно для выбора даты
    private void showSetDateDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());

        datePickerDialog.show();
    }

    //метод-listener срабатывает, когда закрыли диалоговое окно
    @Override
    public void onDateSet(DatePicker datePickerView, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setNewDate();
    }

    /*** Method hide keyboard, when editText not focus : ***/
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN && getCurrentFocus() != null) {
            View view = getCurrentFocus();

            if (view instanceof TextInputEditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect); //возвращает глобальную позицию представления контейнера

                if(!outRect.contains((int) event.getRawX(), (int) event.getRawY())){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    view.clearFocus();
                }
            }
        }

        return super.dispatchTouchEvent(event);
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

    @Override
    public boolean onSupportNavigateUp() {  // Toolbar: backArrow
        onBackPressed();    //нажатие на кнопку назад внизу экрана
        finish(); // close the current activity
        return true;
    }

}