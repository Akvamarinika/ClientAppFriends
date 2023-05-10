package com.akvamarin.clientappfriends.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.API.RetrofitService;
import com.akvamarin.clientappfriends.API.connection.CommentApi;
import com.akvamarin.clientappfriends.BaseActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.CityDTO;
import com.akvamarin.clientappfriends.domain.dto.CommentDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewCommentDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewUserSlimDTO;
import com.akvamarin.clientappfriends.domain.enums.DayOfWeek;
import com.akvamarin.clientappfriends.domain.enums.DayPeriodOfTime;
import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.utils.CommentTag;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
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
    private CommentApi commentApi;
    private Long eventId;
    private String loading;
    private String titleComment;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_event);

        initWidgets();
        setValuesEventInfo();

        showProgressDialog(loading);
        requestCommentsFromServer();

        //просмотр анкеты пользователя
        linearLayoutUserInfo.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewUserInfoActivity.class);
            startActivity(intent);
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
        commentApi = retrofitService.getRetrofit().create(CommentApi.class);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setValuesEventInfo(){
        ViewEventDTO event = (ViewEventDTO) getIntent().getSerializableExtra("current_event"); // Parcelable ?
        ViewUserSlimDTO user = event.getUserOwner();
        CityDTO city = user.getCityDTO();
        eventId = event.getId();

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
}