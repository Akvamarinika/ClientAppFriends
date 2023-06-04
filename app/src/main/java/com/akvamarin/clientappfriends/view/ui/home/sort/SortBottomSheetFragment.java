package com.akvamarin.clientappfriends.view.ui.home.sort;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.content.res.AppCompatResources;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.enums.SortingType;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SortBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String ARG_CURRENT_SORTING_TYPE = "current_sorting_type";
    private RadioGroup radioGroupSort;
    private RadioButton radioCreationDate;
    private RadioButton radioEventDate;
    private RadioButton radioLocation;
    private RadioButton radioLocationDateCreation;
    private RadioButton radioLocationDateEvent;
    private ImageView cancelImageView;

    private SortBottomSheetListener listener;

    public static SortBottomSheetFragment newInstance() {
        return new SortBottomSheetFragment();
    }

    public void setListener(SortBottomSheetListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_list_sort, container, false);

        initWidgets(view);

        Bundle args = getArguments();
        if (args != null) {
            SortingType currentSortingType = (SortingType) args.getSerializable(ARG_CURRENT_SORTING_TYPE);
            setCheckedRadioButton(currentSortingType);
        }

        radioGroupSort.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = view.findViewById(checkedId);
            SortingType selectedSortingType = (SortingType) selectedRadioButton.getTag();
            Log.d("listener", String.valueOf(selectedSortingType));

            if (args != null) {
                args.putSerializable(ARG_CURRENT_SORTING_TYPE, selectedSortingType);
            }

            if (listener != null) {
                listener.onSortOptionSelected(selectedSortingType);
            }

            dismiss();
        });

        cancelImageView.setOnClickListener(v -> dismiss());

        return view;
    }

    private void initWidgets(View view){
        radioGroupSort = view.findViewById(R.id.radioGroupSort);
        radioCreationDate = view.findViewById(R.id.radioCreationDate);
        radioEventDate = view.findViewById(R.id.radioEventDate);
        radioLocation = view.findViewById(R.id.radioLocation);
        radioLocationDateCreation = view.findViewById(R.id.radioLocationDateCreation);
        radioLocationDateEvent = view.findViewById(R.id.radioLocationDateEvent);
        cancelImageView = view.findViewById(R.id.closeIcon);

        radioCreationDate.setTag(SortingType.CREATION_DATE);
        radioEventDate.setTag(SortingType.EVENT_DATE);
        radioLocation.setTag(SortingType.CLOSEST_LOCATION);
        radioLocationDateCreation.setTag(SortingType.CLOSEST_LOCATION_AND_CREATION_DATE);
        radioLocationDateEvent.setTag(SortingType.CLOSEST_LOCATION_AND_EVENT_DATE);
    }

    private void setCheckedRadioButton(SortingType sortingType) {
        RadioButton radioButton;

        switch (sortingType) {
            case CREATION_DATE:
                radioButton = radioCreationDate;
                break;
            case EVENT_DATE:
                radioButton = radioEventDate;
                break;
            case CLOSEST_LOCATION:
                radioButton = radioLocation;
                break;
            case CLOSEST_LOCATION_AND_CREATION_DATE:
                radioButton = radioLocationDateCreation;
                break;
            case CLOSEST_LOCATION_AND_EVENT_DATE:
                radioButton = radioLocationDateEvent;
                break;
            default:
                return;
        }

        radioButton.setChecked(true);
    }


}
