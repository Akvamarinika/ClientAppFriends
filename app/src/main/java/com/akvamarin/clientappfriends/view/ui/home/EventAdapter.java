package com.akvamarin.clientappfriends.view.ui.home;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.Event;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;
import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.utils.BitmapConvertor;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
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

    private PreferenceManager preferenceManager;
    //private final List<Event> eventListAll;

    public EventAdapter(List<ViewEventDTO> eventList, IEventRecyclerListener eventListener) {
        this.eventList = eventList;
        this.eventListener = eventListener;

        //this.eventListAll = new ArrayList<>();
        //eventListAll.addAll(0, eventList);
    }

    /* инициализация views и создание viewHolder */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup root, int viewType) {
        View view = LayoutInflater.from(root.getContext()).inflate(R.layout.item_event_recycler, root, false);
        preferenceManager = new PreferenceManager(root.getContext()); /// preference
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

        // костыль.....
       /* String imgBase64 = preferenceManager.getString(Constants.KEY_IMAGE_BASE64);
        String email = preferenceManager.getString(Constants.KEY_EMAIL);
        String userEmail = event.getUserOwner().getEmail();

        Log.d(TAG, "onBindViewHolder EMAIL: " + userEmail );

        if (imgBase64 != null && userEmail != null && !imgBase64.equalsIgnoreCase("image")
                && (event.getUserOwner().getEmail().equalsIgnoreCase(email))){
            Bitmap bitmap = BitmapConvertor.convertFromBase64ToBitmap(preferenceManager.getString(Constants.KEY_IMAGE_BASE64));
            holder.getUserAvatar().setImageBitmap(bitmap);
        }*/

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
}
