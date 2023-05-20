package com.akvamarin.clientappfriends.view.infoevent;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.ViewUserSlimDTO;
import com.akvamarin.clientappfriends.utils.Utils;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.List;

public class UserSlimAdapter extends RecyclerView.Adapter<UserSlimViewHolder> {
    private final IUserSlimListener userSlimListener;

    private final List<ViewUserSlimDTO> userList;

    public UserSlimAdapter(List<ViewUserSlimDTO> userList, IUserSlimListener userSlimListener) {
        this.userList = userList;
        this.userSlimListener = userSlimListener;
    }

    @NonNull
    @Override
    public UserSlimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_slim, parent, false);
        return new UserSlimViewHolder(itemView, userSlimListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull UserSlimViewHolder holder, int position) {
        ViewUserSlimDTO user = userList.get(position);

        int age = getAge(user.getDateOfBirthday());
        String nicknameAge = user.getNickname() + ", " + age;
        holder.getNicknameTextView().setText(nicknameAge);

        if (user.getUrlAvatar().isEmpty()){
            holder.getAvatarImageView().setImageResource(R.drawable.no_avatar);
        } else {
            Picasso.get()
                    .load(user.getUrlAvatar())
                    .fit()
                    .error(R.drawable.error_loading_image)
                    .into(holder.getAvatarImageView());
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getAge(String dateOfBirthday){
        LocalDate birthday = LocalDate.parse(dateOfBirthday);
        return Utils.getAgeWithCalendar(birthday.getYear(), birthday.getMonthValue(), birthday.getDayOfMonth());
    }
}
