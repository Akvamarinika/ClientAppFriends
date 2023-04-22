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
import android.view.MenuItem;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.view.AllEventsActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.Event;
import com.akvamarin.clientappfriends.domain.dto.User;
import com.akvamarin.clientappfriends.domain.enums.DayPeriodOfTime;
import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.view.ui.home.HomeAllEventsFragment;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private static final String TAG = "ADD";
    private static final int MIN_TEXT_LENGTH = 1;
    private static final int MAX_COUNT_LINE = 10;
    private static final String EMPTY_STRING = "";

    private Toolbar toolbar;
    private MenuItem searchMenuItem;
    private MenuItem filterMenuItem;
    private MenuItem arrowBackMenuItem;

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

        initSpinnerEventCategories();
        //getSelectedItemFromSpinnerEventCategories();

        buttonShowDialogDatePicker.setOnClickListener(view -> showSetDateDialog()); // new View.OnClickListener(){ /*method onClick*/ }
        setNewDate();

        checkCountLinesInEventDescription();

        buttonSave.setOnClickListener(view -> {
            if (checkTextFieldsInFormOnCount()){
                String category = spinnerEventCategories.getSelectedItem().toString();
                String eventName = editTextEventName.getText().toString().trim();
                String eventDescription = editTextEventDescription.getText().toString().trim();
                LocalDate date = Instant.ofEpochMilli(calendar.getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDate();

                int radioIDPartner = radioGroupPartner.getCheckedRadioButtonId();
                RadioButton radioBtnPartner = radioGroupPartner.findViewById(radioIDPartner);
                String partnerText = radioBtnPartner.getText().toString();
                Log.d(TAG, "radio partner: " + partnerText);

                int radioIDTwentyFourHours  = radioGroupTwentyFourHours.getCheckedRadioButtonId();
                RadioButton radioBtnTwentyFourHours = radioGroupTwentyFourHours.findViewById(radioIDTwentyFourHours);
                String periodOfTimeText = radioBtnTwentyFourHours.getText().toString();
                Log.d(TAG, "radio  periodOfTime" + periodOfTimeText);

                Event event = new Event();
                event.setCategory(category);
                event.setEventName(eventName);
                event.setEventDescription(eventDescription);
                event.setDate(date);
                event.setPartner(Partner.getEnumValue(partnerText));
                event.setPeriodOfTime(DayPeriodOfTime.getEnumValue(periodOfTimeText));
                event.setDateTimeCreated(LocalDateTime.now());

                /*TODO: добавить данные авторизированного пользов-ля */
                //event.setUser(new User("Test app", 27, "https://android-obzor.com/wp-content/uploads/2022/03/dc715986ea8bc0a25ea8655e6c2c1386.jpg"));

                String name = preferenceManager.getString(Constants.KEY_NAME);
                String age = preferenceManager.getString(Constants.KEY_AGE);
                String email = preferenceManager.getString(Constants.KEY_EMAIL);
                User userAutorizes = new User(name, Integer.parseInt(age), ""); //https://android-obzor.com/wp-content/uploads/2022/03/dc715986ea8bc0a25ea8655e6c2c1386.jpg
                userAutorizes.setEmail(email);
                event.setUser(userAutorizes);

                Log.d(TAG, "onClick: " + event);

                /*TODO: event отправить на сервер */

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

    }

    /*** Spinner category methods: ***/
    private void initSpinnerEventCategories(){
        spinnerEventCategories = findViewById(R.id.eventCategoriesSpinner);

        String[] stringArrayCategories = getResources().getStringArray(R.array.event_categories_lst);
        List<String> listCategories = Arrays.asList(stringArrayCategories);
        Collections.sort(listCategories); //sorted

        ArrayAdapter<?> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEventCategories.setAdapter(adapter);
    }

//    private void getSelectedItemFromSpinnerEventCategories(){
//        spinnerEventCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int selectedItemPosition, long selectedId) {
//                String itemSelected = adapterView.getItemAtPosition(selectedItemPosition).toString();
//                Log.d(TAG, "onItemSelected: " + itemSelected );
//                Toast toast = Toast.makeText(getApplicationContext(),
//                        "Ваш выбор: " + itemSelected , Toast.LENGTH_SHORT);
//                toast.show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) { }
//        });
//    }

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
        return true;
    }

}


//        editTextEventName.addTextChangedListener(new TextChangedListener<>(editTextEventName) {
//@Override
//public void onTextChanged(EditText editText, Editable s) {
//        Toast toast = Toast.makeText(getApplicationContext(),
//        "EditText: " + editText + "Editable: " + s, Toast.LENGTH_SHORT);
//        // toast.show();
//        }
//        });
//
//        editTextEventDescription.addTextChangedListener(new TextChangedListener<>(editTextEventDescription) {
//@Override
//public void onTextChanged(EditText editText, Editable s) {
//
//        }
//        });