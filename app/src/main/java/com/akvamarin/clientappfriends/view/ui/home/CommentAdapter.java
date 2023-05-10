package com.akvamarin.clientappfriends.view.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.ViewCommentDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewUserSlimDTO;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.utils.Utils;
import com.akvamarin.clientappfriends.view.dialog.CommentOptionsDialog;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<ViewCommentDTO> commentList;
    private final Context context;
    private final PreferenceManager preferenceManager;

    public CommentAdapter(Context context) {
        this.commentList = new ArrayList<>();
        this.context = context;
        this.preferenceManager = new PreferenceManager(context);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCommentList(List<ViewCommentDTO> comments) {
        commentList = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        ViewCommentDTO comment = commentList.get(position);

        // Data:
        ViewUserSlimDTO userSlimDTO = comment.getUserSlimDTO();
        LocalDate birthday = LocalDate.parse(userSlimDTO.getDateOfBirthday());
        int age = Utils.getAgeWithCalendar(birthday.getYear(), birthday.getMonthValue(), birthday.getDayOfMonth());
        String nickAndAge = userSlimDTO.getNickname() + " " + age;
        String updateDateTime = formatDateTime(comment.getUpdatedAt());

        if (userSlimDTO.getUrlAvatar().isEmpty()) {
            holder.imageAvatar.setImageResource(R.drawable.no_avatar);
        } else {
            Picasso.get()
                    .load(userSlimDTO.getUrlAvatar())
                    .fit()
                    .error(R.drawable.error_loading_image)
                    .into(holder.imageAvatar);
        }

        if (comment.getEdited()){
            updateDateTime = updateDateTime + " (изменено)";
        }

        holder.textUserName.setText(nickAndAge);
        holder.textComment.setText(comment.getText());
        holder.textDateTime.setText(updateDateTime);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy в HH:mm");
        return dateTime.format(formatter);
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    public void addComment(ViewCommentDTO comment) {
        commentList.add(comment);
        notifyItemInserted(commentList.size() - 1);
    }

    public void removeComment(int position) {
        if (position >= 0 && position < commentList.size()) {
            commentList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateComment(int position, ViewCommentDTO updatedComment) {
        if (position >= 0 && position < commentList.size()) {
            commentList.set(position, updatedComment);
            notifyItemChanged(position);
        }
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView textUserName;
        private final TextView textComment;
        private final TextView textDateTime;
        private final ImageView imageAvatar;
        private final ConstraintLayout layoutComment;
        private final CommentAdapter adapter;

        public CommentViewHolder(@NonNull View itemView, CommentAdapter adapter) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.textUserName);
            textComment = itemView.findViewById(R.id.textComment);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            layoutComment = itemView.findViewById(R.id.layoutComment);

            layoutComment.setOnClickListener(this);
            this.adapter = adapter;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.layoutComment) {
                showCommentOptionsDialog(getAdapterPosition());
            }
        }

        private void showCommentOptionsDialog(int position) {
            ViewCommentDTO viewCommentDTO = commentList.get(position);
            String currentUser = preferenceManager.getString(Constants.KEY_LOGIN);

            if (viewCommentDTO.getUserSlimDTO().getUsername().equals(currentUser)) {
                CommentOptionsDialog dialog = new CommentOptionsDialog((Activity) itemView.getContext(),
                        viewCommentDTO, commentList, adapter, position,
                        () -> {
                            adapter.removeComment(position);
                            updateUICountComments();
                        });
                dialog.show();
            }
        }


        private void updateUICountComments() {
            String titleComment = itemView.getContext().getString(R.string.text_title_comment);
            TextView textViewTitleComment = ((Activity) itemView.getContext()).findViewById(R.id.titleComment);
            int commentCount = adapter.getItemCount();
            textViewTitleComment.setText(String.format("%s %s", titleComment, commentCount));
        }


    }
}

