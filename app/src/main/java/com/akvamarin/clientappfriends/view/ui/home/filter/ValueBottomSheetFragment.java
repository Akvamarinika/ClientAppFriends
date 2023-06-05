package com.akvamarin.clientappfriends.view.ui.home.filter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.api.presentor.categorydata.CategoryCallback;
import com.akvamarin.clientappfriends.api.presentor.categorydata.CategoryDataApi;
import com.akvamarin.clientappfriends.domain.dto.EventCategoryDTO;
import com.akvamarin.clientappfriends.domain.dto.EventFilter;
import com.akvamarin.clientappfriends.domain.enums.DayOfWeek;
import com.akvamarin.clientappfriends.domain.enums.DayPeriodOfTime;
import com.akvamarin.clientappfriends.domain.enums.Layout;
import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.view.dialog.ErrorDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ValueBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String TAG = "ValueBottomSheetFragment";
    private ImageView cancelImageView;
    private Button okButton;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private Layout selectedLayout;
    private ValueBottomSheetListener listener;
    private List<CheckBoxItem> checkBoxItems = new ArrayList<>();
    private RecyclerView recyclerViewCheckbox;
    private CheckboxAdapter checkboxAdapter;
    private CategoryDataApi categoryDataApi;
    private EventFilter eventFilter;
    private FilterBottomSheetFragment filterBottomSheetFragment;
    private List<Long> categoryIdList = new ArrayList<>();

    public static ValueBottomSheetFragment newInstance(EventFilter eventFilter) {
        ValueBottomSheetFragment fragment = new ValueBottomSheetFragment();
        fragment.eventFilter = eventFilter;
        return fragment;
    }

    public void setListener(ValueBottomSheetListener listener) {
        this.listener = listener;
    }

    public void setValueLayout(Layout layout) {
        selectedLayout = layout;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_list_filters_value, container, false);

        initWidgets(view);
        generateCheckboxes();

        okButton.setOnClickListener(v -> {
            if (listener != null) {
                List<String> selectedValues = getSelectedValues();
                switch (selectedLayout) {
                    case DAY_OF_WEEK -> eventFilter.setDaysOfWeekList(convertToDayOfWeekList(selectedValues));
                    case PERIOD_OF_TIME -> eventFilter.setPeriodOfTimeList(convertToDayPeriodOfTimeList(selectedValues));
                    case CATEGORY -> {eventFilter.setCategory(selectedValues);
                        if (!categoryIdList.isEmpty()){
                            eventFilter.setCategoryIds(categoryIdList);
                            categoryIdList = new ArrayList<>();
                        }
                    }
                    case PARTNER -> eventFilter.setPartnerList(convertToPartnerList(selectedValues));
                }

                listener.onValuesSelected(eventFilter);

                // updated eventFilter back to FilterBottomSheetFragment
                if (filterBottomSheetFragment != null) {
                    filterBottomSheetFragment.setEventFilter(eventFilter);
                }
            }
            dismiss();
        });

        cancelImageView.setOnClickListener(v -> dismiss());

        return view;
    }

    private void initWidgets(View view){
        cancelImageView = view.findViewById(R.id.closeIcon);
        okButton = view.findViewById(R.id.buttonFilterOk);
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewDescription = view.findViewById(R.id.textViewDescription);
        recyclerViewCheckbox = view.findViewById(R.id.recyclerViewCheckbox);
        recyclerViewCheckbox.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryDataApi = new CategoryDataApi(requireActivity());

        checkboxAdapter = new CheckboxAdapter(checkBoxItems);
        recyclerViewCheckbox.setAdapter(checkboxAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generateCheckboxes() {
        checkBoxItems.clear();
        Log.d(TAG, "*** generateCheckboxes() *** " + selectedLayout);

        if (selectedLayout == Layout.DAY_OF_WEEK) {
            generateDayOfWeekCheckboxes();
        } else if (selectedLayout == Layout.PERIOD_OF_TIME) {
            generateDayPeriodOfTimeCheckboxes();
        } else if (selectedLayout == Layout.CATEGORY) {
            generateCategoryCheckboxes();
        } else if (selectedLayout == Layout.PARTNER) {
            generatePartnerCheckboxes();
        }

        checkboxAdapter.notifyDataSetChanged();
    }

    private void generateDayOfWeekCheckboxes() {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            CheckBoxItem checkboxItem = new CheckBoxItem(dayOfWeek.toString());
            checkBoxItems.add(checkboxItem);
        }

        textViewTitle.setText(R.string.filter_value_title_day_of_week);
        textViewDescription.setText(R.string.filter_value_description_day_of_week);
        Log.d(TAG, "*** generateDayOfWeekCheckboxes() *** checkBoxes size: " + checkBoxItems.size());
    }

    private void generateDayPeriodOfTimeCheckboxes() {
        DayPeriodOfTime[] periodOfTimes = DayPeriodOfTime.values();
        int itemCount = periodOfTimes.length - 1; // without last element

        for (int i = 0; i < itemCount; i++) {
            CheckBoxItem checkboxItem = new CheckBoxItem(periodOfTimes[i].toString());
            checkBoxItems.add(checkboxItem);
        }

        textViewTitle.setText(R.string.filter_value_title_period_of_time);
        textViewDescription.setText(R.string.filter_value_description_period_of_time);
        Log.d(TAG, "*** generateDayPeriodOfTimeCheckboxes() *** checkBoxes size: " + checkBoxItems.size());
    }

    private void generateCategoryCheckboxes() {
        categoryDataApi.requestAllEventCategories(new CategoryCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCategoryListRetrieved(List<EventCategoryDTO> retrievedCategoryDTOList) {
                for (EventCategoryDTO category : retrievedCategoryDTOList) {
                    CheckBoxItem checkboxItem = new CheckBoxItem(category.toString());
                    checkboxItem.setId(category.getId());
                    checkBoxItems.add(checkboxItem);
                }
                Log.d(TAG, "*** generateCategoryCheckboxes() *** checkBoxes size: " + checkBoxItems.size());
                textViewTitle.setText(R.string.filter_value_title_category);
                textViewDescription.setText(R.string.filter_value_description_category);
                checkboxAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCategoryListRetrievalError(int responseCode) {
                showErrorDialog(responseCode);
            }
        });
    }

    private void generatePartnerCheckboxes() {
        Partner[] partners = Partner.values();
        int itemCount = partners.length - 1; //without last element

        for (int i = 0; i < itemCount; i++) {
            CheckBoxItem checkboxItem = new CheckBoxItem(partners[i].toString());
            checkBoxItems.add(checkboxItem);
        }

        textViewTitle.setText(R.string.filter_value_title_partner);
        textViewDescription.setText(R.string.filter_value_description_partner);
        Log.d(TAG, "*** generatePartnerCheckboxes() *** checkBoxes size: " + checkBoxItems.size());
    }

    private List<String> getSelectedValues() {
        List<String> selectedValues = new ArrayList<>();
        for (CheckBoxItem checkBox : checkBoxItems) {
            if (checkBox.isChecked()) {
                selectedValues.add(checkBox.getText());

                if (checkBox.getId() != null){
                   categoryIdList.add(checkBox.getId());
                    Log.d(TAG, "*** getSelectedValues() *** categoryIdList size: " + categoryIdList.size());
                }
            }
        }
        return selectedValues;
    }

    private void showErrorDialog(int responseCode) {
        ErrorDialog dialog = new ErrorDialog(requireActivity(), responseCode);
        dialog.show();
    }

    private List<DayOfWeek> convertToDayOfWeekList(List<String> selectedValues) {
        List<DayOfWeek> selectedDaysOfWeek = new ArrayList<>();
        for (String value : selectedValues) {
            selectedDaysOfWeek.add(DayOfWeek.getEnumValue(value));
        }
        return selectedDaysOfWeek;
    }

    private List<DayPeriodOfTime> convertToDayPeriodOfTimeList(List<String> selectedValues) {
        List<DayPeriodOfTime> selectedPeriodsOfTime = new ArrayList<>();
        for (String value : selectedValues) {
            Log.d(TAG, "*** convertToDayPeriodOfTimeList() *** value: " + value);
            selectedPeriodsOfTime.add(DayPeriodOfTime.getEnumValue(value));
        }
        return selectedPeriodsOfTime;
    }

    private List<Partner> convertToPartnerList(List<String> selectedValues) {
        List<Partner> selectedPartners = new ArrayList<>();
        for (String value : selectedValues) {
            selectedPartners.add(Partner.getEnumValue(value));
        }
        return selectedPartners;
    }

}

