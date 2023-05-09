package com.akvamarin.clientappfriends.view.ui.home;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.ViewCommentDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;
import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.utils.Utils;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder>{

    private static final String TAG = "recyclerEvents";
    private List<ViewEventDTO> eventList;
    private final IEventRecyclerListener eventListener;
    private static final String DATE_PATTERN = "d/MM/uuuu";

    public EventAdapter(List<ViewEventDTO> eventList, IEventRecyclerListener eventListener) {
        this.eventList = eventList;
        this.eventListener = eventListener;
    }

    /* инициализация views и создание viewHolder */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup root, int viewType) {
        View view = LayoutInflater.from(root.getContext()).inflate(R.layout.item_event_recycler, root, false);
        return new EventViewHolder(view, eventListener);
    }

    /* связывает view с содержимым */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        ViewEventDTO event = eventList.get(position);

        holder.getTextViewUserName().setText(event.getUserOwner().getNickname());

        LocalDate birthday = LocalDate.parse(event.getUserOwner().getDateOfBirthday());
        int age = Utils.getAgeWithCalendar(birthday.getYear(), birthday.getMonthValue(), birthday.getDayOfMonth());
        holder.getTextViewAge().setText(String.format("%s", age));

        holder.getTextViewCategory().setText(event.getEventCategory().getName());
        holder.getTextViewEventName().setText(event.getName());

        LocalDate eventDate = LocalDate.parse(event.getDate());
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(DATE_PATTERN);
        String textDate = eventDate.format(formatters);   // формат даты для польз-ля
        holder.getTextViewDate().setText(textDate);

        Partner.setImagePartnerHolder(holder, event);

        holder.setIndex(position);

        if (event.getUserOwner().getUrlAvatar().isEmpty()){                                  //TODO вынести блок
            holder.getUserAvatar().setImageResource(R.drawable.no_avatar); ///R.drawable.no_avatar
        } else {
            Picasso.get()
                    .load(event.getUserOwner().getUrlAvatar())
                    .fit()
                    .error(R.drawable.error_loading_image)
                    .into(holder.getUserAvatar());   //.setLoggingEnabled(true)
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void setFilter(List<ViewEventDTO> newListEvent) {
        eventList = new ArrayList<>();
        eventList.addAll(newListEvent);
        notifyDataSetChanged();
    }

    public void updateEventComments(int position, List<ViewCommentDTO> comments) {
        ViewEventDTO event = eventList.get(position);
        event.setComments(comments);
        notifyItemChanged(position);
    }

}
