package com.akvamarin.clientappfriends.view.dialog;

import static com.akvamarin.clientappfriends.domain.dto.CommentDTO.convertInSendServerDto;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.akvamarin.clientappfriends.api.RetrofitService;
import com.akvamarin.clientappfriends.api.connection.CommentApi;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.CommentDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewCommentDTO;
import com.akvamarin.clientappfriends.utils.CommentTag;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.ui.home.CommentAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentOptionsDialog extends Dialog{
    private final Activity activity;
    private final ViewCommentDTO viewCommentDTO;
    private final RetrofitService retrofitService;
    private final CommentApi commentApi;
    private final List<ViewCommentDTO> commentList;
    private final CommentDeleteListener deleteListener;
    private final CommentAdapter commentAdapter;
    private final int position;

    public CommentOptionsDialog(Activity activity, ViewCommentDTO viewCommentDTO, List<ViewCommentDTO> commentList,
                                CommentAdapter commentAdapter, int position,
                                CommentDeleteListener deleteListener) {
        super(activity);
        this.activity = activity;
        this.viewCommentDTO = viewCommentDTO;
        this.retrofitService = RetrofitService.getInstance(activity);
        this.commentApi = retrofitService.getRetrofit().create(CommentApi.class);
        this.commentList = commentList;
        this.deleteListener = deleteListener;
        this.commentAdapter = commentAdapter;
        this.position = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_options);

        TextView tvEdit = findViewById(R.id.textViewEdit);
        TextView tvDelete = findViewById(R.id.textViewDelete);
        TextView tvCancel = findViewById(R.id.textViewCancel);

        tvEdit.setOnClickListener(view -> {
            editModeVisible();
            CommentDTO comment = convertInSendServerDto(viewCommentDTO);
            editComment(comment);
            dismiss();
        });

        tvDelete.setOnClickListener(view -> {
            CommentDTO comment = convertInSendServerDto(viewCommentDTO);
            deleteComment(comment);
            dismiss();
        });

        tvCancel.setOnClickListener(v -> dismiss());
    }

    private void editComment(CommentDTO comment) {
        EditText inputMessageEditText = activity.findViewById(R.id.inputMessageEditText);
        inputMessageEditText.setText(comment.getText());

        Long commentId = comment.getId();
        if (commentId != null) {
            CommentTag commentTag = new CommentTag(commentId, position);
            inputMessageEditText.setTag(commentTag);
        }
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

    private void editModeVisible(){
        TextView textViewEditMode = activity.findViewById(R.id.textViewEditMode);
        AppCompatImageView imageModeEditCancel = activity.findViewById(R.id.imageModeEditCancel);
        AppCompatImageView chatsImageSend = activity.findViewById(R.id.chatsImageSend);

        textViewEditMode.setVisibility(View.VISIBLE);
        imageModeEditCancel.setVisibility(View.VISIBLE);
        chatsImageSend.setImageResource(R.drawable.ic_ok_check);
    }


}


