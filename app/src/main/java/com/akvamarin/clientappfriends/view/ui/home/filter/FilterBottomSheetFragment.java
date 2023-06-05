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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.EventFilter;
import com.akvamarin.clientappfriends.domain.enums.DayOfWeek;
import com.akvamarin.clientappfriends.domain.enums.DayPeriodOfTime;
import com.akvamarin.clientappfriends.domain.enums.Layout;
import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.utils.EventFilterPreferences;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment implements ValueBottomSheetListener {
    private static final String TAG = "FilterBottomSheetFragment";
    private LinearLayout layoutDayOfWeek;
    private LinearLayout layoutPeriodOfTime;
    private LinearLayout layoutCategory;
    private LinearLayout layoutPartner;

    private TextView selectedDayOfWeek;
    private TextView selectedPeriodOfTime;
    private TextView selectedCategory;
    private TextView selectedPartner;

    private Button applyButton;
    private TextView tvClean;
    private ImageView cancelImageView;
    private FilterBottomSheetListener listener;
    private EventFilter eventFilter = new EventFilter();
    private ValueBottomSheetFragment valueBottomSheetFragment;

    public static FilterBottomSheetFragment newInstance() {
        return new FilterBottomSheetFragment();
    }

    public void setListener(FilterBottomSheetListener listener) {
        this.listener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_list_filters, container, false);

        initWidgets(view);

        layoutDayOfWeek.setOnClickListener(v -> {
            valueBottomSheetFragment = ValueBottomSheetFragment.newInstance(eventFilter);
            valueBottomSheetFragment.setValueLayout(Layout.DAY_OF_WEEK);
            valueBottomSheetFragment.setListener(this);
            valueBottomSheetFragment.show(getChildFragmentManager(), "valueBottomSheet");
        });

        layoutPeriodOfTime.setOnClickListener(v -> {
            valueBottomSheetFragment = ValueBottomSheetFragment.newInstance(eventFilter);
            valueBottomSheetFragment.setValueLayout(Layout.PERIOD_OF_TIME);
            valueBottomSheetFragment.setListener(this);
            valueBottomSheetFragment.show(getChildFragmentManager(), "valueBottomSheet");
        });

        layoutCategory.setOnClickListener(v -> {
            valueBottomSheetFragment = ValueBottomSheetFragment.newInstance(eventFilter);
            valueBottomSheetFragment.setValueLayout(Layout.CATEGORY);
            valueBottomSheetFragment.setListener(this);
            valueBottomSheetFragment.show(getChildFragmentManager(), "valueBottomSheet");
        });

        layoutPartner.setOnClickListener(v -> {
            valueBottomSheetFragment = ValueBottomSheetFragment.newInstance(eventFilter);
            valueBottomSheetFragment.setValueLayout(Layout.PARTNER);
            valueBottomSheetFragment.setListener(this);
            valueBottomSheetFragment.show(getChildFragmentManager(), "valueBottomSheet");
        });

        applyButton.setOnClickListener(v -> {

            if (listener != null) {
                listener.onFilterApplied(eventFilter);
            }
            dismiss();
        });

        tvClean.setOnClickListener(v -> {
            eventFilter = new EventFilter();
            deleteFilters();

            if (valueBottomSheetFragment != null) {
                selectedDayOfWeek.setText("");
                selectedPeriodOfTime.setText("");
                selectedCategory.setText("");
                selectedPartner.setText("");
            }
        });

        cancelImageView.setOnClickListener(v -> dismiss());

        setStoredFilters();

        return view;
    }

    private void initWidgets(View view){
        layoutDayOfWeek = view.findViewById(R.id.layout_day_of_week);
        layoutPeriodOfTime = view.findViewById(R.id.layout_period_of_time);
        layoutCategory = view.findViewById(R.id.layout_category);
        layoutPartner = view.findViewById(R.id.layout_partner);

        selectedDayOfWeek = view.findViewById(R.id.tv_day_of_week_selected);
        selectedPeriodOfTime = view.findViewById(R.id.tv_period_of_time_selected);
        selectedCategory = view.findViewById(R.id.tv_category_selected);
        selectedPartner = view.findViewById(R.id.tv_partner_selected);

        cancelImageView = view.findViewById(R.id.closeIcon);
        tvClean = view.findViewById(R.id.tvClean);
        applyButton = view.findViewById(R.id.buttonFilterApply);
    }

    public void setEventFilter(EventFilter eventFilter) {
        this.eventFilter = eventFilter;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onValuesSelected(EventFilter eventFilter) {
        this.eventFilter = eventFilter;

        setValuesFilters();

        Log.d(TAG, "Event Filter Values:");
        Log.d(TAG, "Days of Week: " + eventFilter.getDaysOfWeekList());
        Log.d(TAG, "Period of Time: " + eventFilter.getPeriodOfTimeList());
        Log.d(TAG, "Categories IDs: " + eventFilter.getCategoryIds());
        Log.d(TAG, "Categories text: " + eventFilter.getCategory());
        Log.d(TAG, "Partners: " + eventFilter.getPartnerList());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setStoredFilters() {
        EventFilterPreferences filterPreferences = new EventFilterPreferences(requireContext());
        EventFilter storedFilter = filterPreferences.getEventFilter();

        if (storedFilter != null) {
            this.eventFilter = storedFilter;
            setValuesFilters();
            Log.d(TAG, "setStoredFilters() eventFilter =" + eventFilter);
        }
    }

    private void deleteFilters() {
        EventFilterPreferences filterPreferences = new EventFilterPreferences(requireContext());
        filterPreferences.deleteEventFilter();
    }

    private void setValuesFilters(){
        if (valueBottomSheetFragment != null) {
            String yourSelect = getString(R.string.selected_filter);

            List<DayOfWeek> daysOfWeekList = eventFilter.getDaysOfWeekList();
            selectedDayOfWeek.setText((daysOfWeekList != null ? (yourSelect + " " + daysOfWeekList) : ""));

            List<DayPeriodOfTime> periodOfTimeList = eventFilter.getPeriodOfTimeList();
            selectedPeriodOfTime.setText((periodOfTimeList != null ? (yourSelect + " " + periodOfTimeList) : ""));

            List<String> categoryList = eventFilter.getCategory();
            selectedCategory.setText((categoryList != null ? (yourSelect + " " + categoryList) : ""));

            List<Partner> partnerList = eventFilter.getPartnerList();
            selectedPartner.setText((partnerList != null ? (yourSelect + " " + partnerList) : ""));
        }
    }
}
