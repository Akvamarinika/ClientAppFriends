package com.akvamarin.clientappfriends.view.infoevent;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.API.ErrorResponse;
import com.akvamarin.clientappfriends.API.ErrorUtils;
import com.akvamarin.clientappfriends.API.RetrofitService;
import com.akvamarin.clientappfriends.API.connection.CommentApi;
import com.akvamarin.clientappfriends.API.connection.EventApi;
import com.akvamarin.clientappfriends.API.connection.NotificationParticipantApi;
import com.akvamarin.clientappfriends.BaseActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.CityDTO;
import com.akvamarin.clientappfriends.domain.dto.CommentDTO;
import com.akvamarin.clientappfriends.domain.dto.NotificationDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewCommentDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewNotificationDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewUserSlimDTO;
import com.akvamarin.clientappfriends.domain.enums.DayOfWeek;
import com.akvamarin.clientappfriends.domain.enums.DayPeriodOfTime;
import com.akvamarin.clientappfriends.domain.enums.FeedbackType;
import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.utils.CommentTag;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.addevent.AddEventActivity;
import com.akvamarin.clientappfriends.view.ui.home.CommentAdapter;
import com.akvamarin.clientappfriends.view.ui.profile.ViewUserInfoActivity;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoEventActivity extends BaseActivity {
    private static final String TAG = "TagInfoEventActivity";
    private static final String DATE_PATTERN = "d/MM/uuuu";

    private Toolbar toolbar;
    private LinearLayout linearLayoutUserInfo;

    private Button buttonParticipate;
    private CircleImageView circleAvatarBig;
    private TextView textViewUserName;
    private TextView textViewCountryCity;
    private TextView textViewEventTitle;
    private TextView textViewPartner;
    private TextView textViewDate;
    private TextView textViewDayOfWeek;
    private TextView textViewTwentyFourHours;
    private TextView textViewDescription;
    private TextView textViewTitleComment;
    private TextView textViewEditMode;
    private AppCompatImageView imageModeEditCancel;

    private EditText inputMessageEditText;
    private ImageView chatsImageSend;

    //соединяются адаптером Recycler & List:
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private List<ViewCommentDTO> commentList = new ArrayList<>();

    private PreferenceManager preferenceManager;
    private RetrofitService retrofitService;
    private EventApi eventApi;
    private CommentApi commentApi;
    private NotificationParticipantApi notificationParticipantApi;
    private Long eventId;
    private String loading;
    private String titleComment;

    private ViewEventDTO event;
    private ViewNotificationDTO viewNotificationDTO;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_event);
        eventId = (Long) getIntent().getSerializableExtra("current_event_id"); // Parcelable ?
        Log.d(TAG, "current_event_id eventId = " + eventId);

        initWidgets();
        initEventFromServer();

        showProgressDialog(loading);
        requestCommentsFromServer();

        showProgressDialog(loading);
        requestNotificationFromServer();

        //просмотр анкеты пользователя
        linearLayoutUserInfo.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewUserInfoActivity.class);
            startActivity(intent);
        });

        buttonParticipate.setOnClickListener(view -> {
            long userId = preferenceManager.getLong(Constants.KEY_USER_ID);

            if (event != null && event.getUserOwner().getId() != null && userId == event.getUserOwner().getId()){
                startAddEventActivity(); // update
            } else if (viewNotificationDTO != null && viewNotificationDTO.getFeedbackType() == FeedbackType.WAITING ){
                deleteNotificationOnServer();
            } else if(viewNotificationDTO != null && viewNotificationDTO.getFeedbackType() == FeedbackType.APPROVED){
                deleteNotificationOnServer();
            } else if (viewNotificationDTO != null && viewNotificationDTO.getFeedbackType() == FeedbackType.REJECTED) {
                startAddEventActivity(); // create analog
            } else {
                sendNotificationOnServer();
            }
        });

        chatsImageSend.setOnClickListener(v -> {
            String commentText = inputMessageEditText.getText().toString().trim();

            if (!TextUtils.isEmpty(commentText))  {
                CommentDTO comment = initCommentDTO(commentText);

                if (inputMessageEditText.getTag() instanceof CommentTag) {
                    CommentTag commentTag = (CommentTag) inputMessageEditText.getTag();
                    updateCommentOnServer(comment, commentTag);
                } else {
                    sendNewCommentOnServer(comment);
                }

                showProgressDialog(loading);
            }
        });

        imageModeEditCancel.setOnClickListener(view -> {
            inputMessageEditText.setText(""); // clear input
            inputMessageEditText.setTag(""); // clear
            editModeGone();
        });
    }

    private void initWidgets(){
        titleComment = getApplicationContext().getString(R.string.text_title_comment);
        loading = getApplicationContext().getString(R.string.loading);
        toolbar = findViewById(R.id.top_toolbar);
        linearLayoutUserInfo = findViewById(R.id.layoutUserInfo);
        buttonParticipate = findViewById(R.id.buttonParticipate);
        circleAvatarBig = findViewById(R.id.imageEventInfoAvatarBig);
        textViewUserName = findViewById(R.id.textViewEventInfoUserName);
        textViewCountryCity = findViewById(R.id.textViewEventInfoCountryCity);
        textViewEventTitle = findViewById(R.id.eventInfoTitle);
        textViewPartner = findViewById(R.id.textViewEventInfoPartner);
        textViewDate = findViewById(R.id.textViewEventInfoDate);
        textViewDayOfWeek = findViewById(R.id.textViewEventInfoDayOfWeek);
        textViewTwentyFourHours = findViewById(R.id.textViewEventInfoTwentyFourHours);
        textViewDescription = findViewById(R.id.textViewEventInfoDescription);
        textViewTitleComment = findViewById(R.id.titleComment);
        inputMessageEditText = findViewById(R.id.inputMessageEditText);
        chatsImageSend = findViewById(R.id.chatsImageSend);

        textViewEditMode = findViewById(R.id.textViewEditMode);
        imageModeEditCancel = findViewById(R.id.imageModeEditCancel);
        editModeGone();

        // comments
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        commentAdapter = new CommentAdapter(getApplicationContext());
        commentAdapter.setCommentList(commentList);
        recyclerViewComments.setAdapter(commentAdapter);

        preferenceManager = new PreferenceManager(getApplicationContext());
        retrofitService = RetrofitService.getInstance(getApplicationContext());
        eventApi = retrofitService.getRetrofit().create(EventApi.class);
        commentApi = retrofitService.getRetrofit().create(CommentApi.class);
        notificationParticipantApi = retrofitService.getRetrofit().create(NotificationParticipantApi.class);
    }

    private void startAddEventActivity(){
        Intent intent = new Intent(getApplicationContext(), AddEventActivity.class);
        intent.putExtra("current_event_id", event.getId()); // event id
        startActivity(intent);
    }

    private void initEventFromServer(){
        Log.d(TAG, "init event from server... eventId = " + eventId);

        if (eventId != null) {
            showProgressDialog(loading);
            eventApi.getEventById(eventId, getAuthToken()).enqueue(new Callback<>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(@NonNull Call<ViewEventDTO> call, @NonNull Response<ViewEventDTO> response) {
                    dismissProgressDialog();

                    if (response.isSuccessful() && response.body() != null) {
                        ViewEventDTO viewEventDTO = response.body();
                        event = viewEventDTO;
                        setValuesEventInfo(viewEventDTO);
                        setParticipantButtonStyle();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setValuesEventInfo(ViewEventDTO event){
        ViewUserSlimDTO user = event.getUserOwner();
        CityDTO city = user.getCityDTO();

        if (user.getUrlAvatar().isEmpty()){              //TODO вынести блок
            circleAvatarBig.setImageResource(R.drawable.no_avatar); //R.drawable.no_avatar
        } else {
            Picasso.get()
                    .load(user.getUrlAvatar())
                    .fit()
                    .error(R.drawable.error_loading_image)
                    .into(circleAvatarBig);
        }

        String cityCountry = String.format("%s, %s", city.getCountryName(), city.getName());
        textViewCountryCity.setText(cityCountry);

        textViewUserName.setText(user.getNickname());
        textViewEventTitle.setText(event.getName());

        LocalDate eventDate = LocalDate.parse(event.getDate());
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(DATE_PATTERN);
        String textDate = eventDate.format(formatters);   // формат даты для польз-ля
        textViewDate.setText(textDate);

        textViewDayOfWeek.setText(DayOfWeek.valueOf(eventDate.getDayOfWeek().name()).toString());
        textViewDescription.setText(event.getDescription());

        Partner.setImagePartnerTextView(textViewPartner, event, this);

        textViewTwentyFourHours.setText(event.getPeriodOfTime().toString());
        DayPeriodOfTime.setImageTwentyFourHoursInTextView(textViewTwentyFourHours, event);

    }

    private void requestCommentsFromServer(){
        if (eventId != null) {
            commentApi.getEventComments(eventId).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<List<ViewCommentDTO>> call, @NonNull Response<List<ViewCommentDTO>> response) {
                    dismissProgressDialog();

                    if (response.isSuccessful()) {
                        commentList = response.body();
                        commentAdapter.setCommentList(commentList);
                        textViewTitleComment.setText(String.format("%s %s ", titleComment, commentList.size()));
                        Log.d(TAG, "requestCommentsFromServer(), comments size = " + commentList.size());
                    } else {
                        Log.d(TAG, "requestCommentsFromServer(), response code " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<ViewCommentDTO>> call, @NonNull Throwable t) {
                    dismissProgressDialog();
                    Log.d(TAG, "Error requestCommentsFromServer ViewCommentDTO: " + t.fillInStackTrace());
                }
            });
        }
    }

    private CommentDTO initCommentDTO(String commentText){
        return CommentDTO.builder()
                .text(commentText)
                .eventId(eventId)
                .build();
    }

    private void sendNewCommentOnServer(CommentDTO newComment) {
        if (eventId != null) {
            commentApi.createComment(eventId, newComment, getAuthToken()).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    dismissProgressDialog();
                    Log.d(TAG, "sendNewCommentOnServer(), response code " + response.code());

                    if (response.isSuccessful()) {
                        String url = response.headers().get("Location");

                        if (url != null) {
                            String commentId = url.substring(url.lastIndexOf("/") + 1);
                            showProgressDialog(loading);
                            requestCommentById(Long.valueOf(commentId));
                        }

                    } else {
                        Log.d(TAG, "sendNewCommentOnServer(), response code " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    dismissProgressDialog();
                }
            });

        }
    }

    public void requestCommentById(Long commentId) {
        commentApi.findCommentById(commentId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ViewCommentDTO> call, @NonNull Response<ViewCommentDTO> response) {
                dismissProgressDialog();

                if (response.isSuccessful()) {
                    ViewCommentDTO comment = response.body();
                    addCommentToList(comment); // add new comment to list
                } else {
                    Log.d(TAG, "requestCommentById(), response code " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ViewCommentDTO> call, @NonNull Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    private void addCommentToList(ViewCommentDTO comment) {
        if (commentAdapter != null) {
            commentAdapter.getCommentList().add(0, comment); // Добавляем комментарий с индексом 0, чтобы вставить его первым
            commentAdapter.notifyItemInserted(0); // Сообщаем адаптеру о новом элементе
            recyclerViewComments.scrollToPosition(0); // Прокрутить к началу списка
            textViewTitleComment.setText(String.format("%s %s", titleComment, commentAdapter.getItemCount()));
            inputMessageEditText.setText(""); // Clear input
            inputMessageEditText.setTag(""); // Clear
        }
    }

    private AuthToken getAuthToken() {
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        return new AuthToken(authToken);
    }

    private void updateCommentOnServer(CommentDTO comment, CommentTag commentTag){
        Long commentId = commentTag.getCommentId();
        int position = commentTag.getPosition();
        comment.setId(commentId);

        Call<ViewCommentDTO> call = commentApi.updateComment(comment.getId(), comment, getAuthToken());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ViewCommentDTO> call, @NonNull Response<ViewCommentDTO> response) {
                dismissProgressDialog();
                Log.d(TAG, "updateCommentOnServer(CommentDTO comment), response code " + response.code());

                if (response.isSuccessful()) {
                    ViewCommentDTO updatedComment = response.body();
                    //int position = commentList.indexOf(comment);
                    Log.d(TAG, "position == " + position);

                    if (position != -1) {
                        commentList.set(position, updatedComment);
                        commentAdapter.updateComment(position, updatedComment);
                        recyclerViewComments.scrollToPosition(position); // scroll to the updated comment
                        textViewTitleComment.setText(String.format("%s %s", titleComment, commentAdapter.getItemCount()));
                        inputMessageEditText.setText(""); // clear input
                        inputMessageEditText.setTag(""); // clear
                        editModeGone();
                    }

                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<ViewCommentDTO> call, @NonNull Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    private void editModeGone(){
        textViewEditMode.setVisibility(View.GONE);
        imageModeEditCancel.setVisibility(View.GONE);
        chatsImageSend.setImageResource(R.drawable.ic_chats_send);
    }

    private void setParticipantButtonStyle(){
        Log.d(TAG, "setParticipantButtonStyle() viewNotificationDTO: " + viewNotificationDTO);
        Log.d(TAG, "setParticipantButtonStyle() eventID: " + eventId);
        int colorBaseForText = ContextCompat.getColor(this, R.color.white);

        if (viewNotificationDTO != null && viewNotificationDTO.getFeedbackType() == FeedbackType.WAITING ){
            int color = ContextCompat.getColor(this, R.color.bgcolor);
            int colorText = ContextCompat.getColor(this, R.color.colorPrimary);
            buttonParticipate.setBackgroundColor(color);
            buttonParticipate.setTextColor(colorText);
            buttonParticipate.setText(R.string.btn_text_participate_waiting);
        } else if(viewNotificationDTO != null && viewNotificationDTO.getFeedbackType() == FeedbackType.APPROVED) {
            int color = ContextCompat.getColor(this, R.color.lightColorAccent);
            buttonParticipate.setBackgroundColor(color);
            buttonParticipate.setTextColor(colorBaseForText);
            buttonParticipate.setText(R.string.btn_text_participate_approved);
        } else if (viewNotificationDTO != null && viewNotificationDTO.getFeedbackType() == FeedbackType.REJECTED) {
            int color = ContextCompat.getColor(this, R.color.grey);
            buttonParticipate.setBackgroundColor(color);
            buttonParticipate.setTextColor(colorBaseForText);
            buttonParticipate.setText(R.string.btn_text_participate_rejected);  // "Вашу заявку отклонил орг"
        } else if (isOrganizer()) {
            Log.d(TAG, "setButtonIfOrganizer, event.getUserOwner().getId(): " + event.getUserOwner().getId());
            int color = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            buttonParticipate.setBackgroundColor(color);
            buttonParticipate.setText(R.string.btn_text_participate_organizer);
        } else {
            int color = ContextCompat.getColor(this, R.color.colorPrimary);
            buttonParticipate.setBackgroundColor(color);
            buttonParticipate.setTextColor(colorBaseForText);
            buttonParticipate.setText(R.string.btn_text_participate);
        }

    }

    private boolean isOrganizer(){
        long userId = preferenceManager.getLong(Constants.KEY_USER_ID);
        return event != null && event.getUserOwner().getId() != null && userId == event.getUserOwner().getId();
    }

    private void requestNotificationFromServer() {
        Long userId = preferenceManager.getLong(Constants.KEY_USER_ID);
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        Log.d(TAG, "ID user: " + userId);

        if (authToken != null && userId != 0L && eventId != null) {
            notificationParticipantApi.findNotification(eventId, userId,
                    new AuthToken(authToken)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ViewNotificationDTO> call, @NonNull Response<ViewNotificationDTO> response) {
                    dismissProgressDialog();

                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "requestNotificationFromServer() ");
                        Log.d(TAG, "response.body() " + response.body());
                        viewNotificationDTO = response.body();
                        setParticipantButtonStyle();
                    } else {
                        setParticipantButtonStyle();
                        ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                        Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ViewNotificationDTO> call, @NonNull Throwable t) {
                    dismissProgressDialog();
                    Log.d(TAG, "Error requestNotificationFromServer(): " + t.fillInStackTrace());
                }
            });
        }
    }

    private void sendNotificationOnServer() {
        Long userId = preferenceManager.getLong(Constants.KEY_USER_ID);
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        Log.d(TAG, "ID user: " + userId);

        if (authToken != null && userId != 0L && eventId != null) {
            notificationParticipantApi.createParticipantRequest(new NotificationDTO(eventId, userId),
                    new AuthToken(authToken)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Log.d(TAG, "Response sendNotificationOnServer() " + response.headers());

                    if (response.isSuccessful()) {
                        Log.d(TAG, "Save notification " + response.headers());
                        String url = response.headers().get("Location");

                        if (url != null) {
                            String requestId = url.substring(url.lastIndexOf("/") + 1);
                            viewNotificationDTO = ViewNotificationDTO.builder()
                                    .id(Long.valueOf(requestId))
                                    .userId(userId)
                                    .eventId(eventId)
                                    .feedbackType(FeedbackType.WAITING)
                                    .build();

                            setParticipantButtonStyle();
                        }
                    } else {
                        setParticipantButtonStyle();
                        ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                        Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.d(TAG, "Error sendNotificationOnServer(): " + t.fillInStackTrace());
                }
            });
        }
    }

    private void deleteNotificationOnServer() {
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        Log.d(TAG, "Notification delete: " + viewNotificationDTO);

        if (authToken != null && viewNotificationDTO != null) {
            notificationParticipantApi.deleteParticipantRequest(viewNotificationDTO.getId(), new AuthToken(authToken)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Log.d(TAG, "deleteNotificationOnServer() " + response.headers());

                    if (response.isSuccessful()) {
                        viewNotificationDTO = null;
                        setParticipantButtonStyle();
                    } else {
                        setParticipantButtonStyle();
                        ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                        Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.d(TAG, "Error deleteNotificationOnServer(): " + t.fillInStackTrace());
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initEventFromServer();
    }
}