package com.akvamarin.clientappfriends.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.akvamarin.clientappfriends.API.RetrofitService;
import com.akvamarin.clientappfriends.API.connection.CommentApi;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.CommentDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewCommentDTO;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.ui.home.CommentAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentOptionsDialog extends Dialog {
    private final Activity activity;
    private final CommentDTO comment;
    private final RetrofitService retrofitService;
    private final CommentApi commentApi;
    private final List<ViewCommentDTO> commentList;
    private final CommentDeleteListener deleteListener;
    private final CommentAdapter commentAdapter;

    public CommentOptionsDialog(Activity activity, CommentDTO comment, List<ViewCommentDTO> commentList,
                                CommentAdapter commentAdapter,
                                CommentDeleteListener deleteListener) {
        super(activity);
        this.activity = activity;
        this.comment = comment;
        this.retrofitService = RetrofitService.getInstance(activity);
        this.commentApi = retrofitService.getRetrofit().create(CommentApi.class);
        this.commentList = commentList;
        this.deleteListener = deleteListener;
        this.commentAdapter = commentAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_options);

        TextView tvEdit = findViewById(R.id.textViewEdit);
        TextView tvDelete = findViewById(R.id.textViewDelete);
        TextView tvCancel = findViewById(R.id.textViewCancel);

        tvEdit.setOnClickListener(view -> {
            editComment(comment);
            dismiss();
        });

        tvDelete.setOnClickListener(view -> {
            deleteComment(comment);
            dismiss();
        });

        tvCancel.setOnClickListener(v -> dismiss());
    }

    private void editComment(CommentDTO comment) {
        Call<ViewCommentDTO> call = commentApi.updateComment(comment.getId(), comment, getAuthToken());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ViewCommentDTO> call, @NonNull Response<ViewCommentDTO> response) {
                if (response.isSuccessful()) {
                    ViewCommentDTO viewCommentDTO = response.body();
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<ViewCommentDTO> call, @NonNull Throwable t) {

            }
        });
    }

    private void deleteComment(CommentDTO comment) {
        Call<Void> call = commentApi.deleteComment(comment.getId(), getAuthToken());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    commentList.remove(comment);

                    int position = commentList.indexOf(comment);
                    if (position != -1) {
                        commentAdapter.notifyItemRemoved(position);//notify adapter
                    }

                    // notify the listener that the comment is deleted
                    if (deleteListener != null) {
                        deleteListener.onCommentDelete();
                    }

                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

            }
        });
    }

    private AuthToken getAuthToken() {
        PreferenceManager preferenceManager = new PreferenceManager(activity);
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        return new AuthToken(authToken);
    }
}


