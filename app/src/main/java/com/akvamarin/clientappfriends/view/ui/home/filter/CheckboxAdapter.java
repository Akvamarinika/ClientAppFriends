package com.akvamarin.clientappfriends.view.ui.home.filter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;

import java.util.List;

public class CheckboxAdapter extends RecyclerView.Adapter<CheckboxAdapter.CheckboxViewHolder> {
    private final List<CheckBoxItem> checkBoxItems;

    public CheckboxAdapter(List<CheckBoxItem> checkBoxItems) {
        this.checkBoxItems = checkBoxItems;
    }

    @NonNull
    @Override
    public CheckboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkbox, parent, false);
        return new CheckboxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckboxViewHolder holder, int position) {
        CheckBoxItem checkBoxItem = checkBoxItems.get(position);
        CheckBox checkbox = holder.getCheckbox();
        checkbox.setChecked(checkBoxItem.isChecked());
        checkbox.setText(checkBoxItem.getText());
    }

    @Override
    public int getItemCount() {
        return checkBoxItems.size();
    }

    public class CheckboxViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkbox;

        public CheckboxViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkboxItem);

            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    CheckBoxItem checkBoxItem = checkBoxItems.get(position);
                    checkBoxItem.setChecked(isChecked);

                    Drawable background = checkBoxItem.isChecked() ? AppCompatResources.getDrawable(itemView.getContext(), R.drawable.item_background_rounded_selected) :
                            AppCompatResources.getDrawable(itemView.getContext(), R.drawable.item_background_rounded);
                    itemView.setBackground(background);
                }
            });
        }

        public CheckBox getCheckbox() {
            return checkbox;
        }
    }
}

