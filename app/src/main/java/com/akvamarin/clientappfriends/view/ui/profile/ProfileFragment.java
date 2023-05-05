package com.akvamarin.clientappfriends.view.ui.profile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.akvamarin.clientappfriends.API.RetrofitService;
import com.akvamarin.clientappfriends.API.connection.UserApi;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.CityDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewUserDTO;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.utils.Utils;
import com.akvamarin.clientappfriends.view.AuthenticationActivity;
import com.akvamarin.clientappfriends.view.dialog.DelDialog;
import com.akvamarin.clientappfriends.view.dialog.DelDialogListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.vk.api.sdk.VK;

import java.time.LocalDate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements DelDialogListener {
    private static final String TAG = "ProfileFragment";
    private View viewProfileFragment;
    //private FragmentProfileBinding binding;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private ImageView imageProfileAvatar;
    private LinearLayout linearLayoutLogOut;
    private LinearLayout layoutDeleteAccount;
    private TextView tvProfileNameAge;

    private ViewUserDTO user;
    private RetrofitService retrofitService;
    private UserApi userApi;
    private PreferenceManager preferenceManager;

    public View onCreateView(@NonNull LayoutInflater layoutInflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewProfileFragment = layoutInflater.inflate(R.layout.fragment_profile, container, false);
        initWidgets();
        updateToolbar();
        updateMainFAB();
        startEditActivityWithFAB();

        linearLayoutLogOut.setOnClickListener(view -> {
            VK.logout();  // Log out of VK
            preferenceManager.putString(Constants.KEY_APP_TOKEN, null);
            preferenceManager.putString(Constants.KEY_USER_ID, "id");
            preferenceManager.putString(Constants.KEY_LOGIN, "login");
            preferenceManager.putString(Constants.KEY_PASSWORD, "pass");
            AuthenticationActivity.startFrom(getActivity()); // Start AuthorizationActivity from this context
            requireActivity().finish(); // Finish the current activity
        });

        layoutDeleteAccount.setOnClickListener(view -> showDeleteDialog());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initUser();
        }

           // requestUsers();
        return viewProfileFragment;
    }

    private void initWidgets(){
        toolbar = requireActivity().findViewById(R.id.top_toolbar);
        floatingActionButton = requireActivity().findViewById(R.id.fab_btn);
        imageProfileAvatar = viewProfileFragment.findViewById(R.id.imageProfileAvatar);
        linearLayoutLogOut = viewProfileFragment.findViewById(R.id.layoutLogOut);
        layoutDeleteAccount = viewProfileFragment.findViewById(R.id.layoutDeleteAccount);
        tvProfileNameAge = viewProfileFragment.findViewById(R.id.tvProfileNameAge);

        preferenceManager = new PreferenceManager(requireActivity());
        retrofitService = RetrofitService.getInstance(getContext());
        userApi = retrofitService.getRetrofit().create(UserApi.class);
    }


    private void updateToolbar(){
        toolbar.setTitle(R.string.title_profile);
        toolbar.setLogo(R.drawable.ic_person);
        setHasOptionsMenu(true);    // нужен для вызова onCreateOptionsMenu
    }

    private void updateMainFAB(){
        floatingActionButton.setImageResource(R.drawable.ic_profile_edit);
    }

    private void startEditActivityWithFAB(){
        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initUser(){
        String login = preferenceManager.getString(Constants.KEY_LOGIN);
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        Log.d(TAG, "Login user: " + login);

        if (!login.equals("login") && authToken != null) {
            userApi.getUserByLogin(login, new AuthToken(authToken)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ViewUserDTO> call, @NonNull Response<ViewUserDTO> response) {

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        user = response.body();
                        preferenceManager.putString(Constants.KEY_USER_ID, Long.toString(user.getId())); // user id
                        Log.d(TAG, "Save user ID: " + user.getId());

                        CityDTO cityDTO = user.getCityDTO();
                        String gender = user.getSex().toString();
                        LocalDate birthday = LocalDate.parse(user.getDateOfBirthday());
                        int age = Utils.getAgeWithCalendar(birthday.getYear(), birthday.getMonthValue(), birthday.getDayOfMonth());

                        String nameUser = String.format("%s, %s %s %s %s", user.getNickname(), age,
                                cityDTO.getName(), cityDTO.getCountryName(), gender);
                        tvProfileNameAge.setText(nameUser);

                        Log.d(TAG, "user.getUrlAvatar() " + user.getUrlAvatar());
                        if (!TextUtils.isEmpty(user.getUrlAvatar())) {  // Check if the photo URL is not empty
                            Picasso.get().load(user.getUrlAvatar()) // Load the image from the URL
                                    .error(R.drawable.no_avatar) // If an error occurs, set a placeholder image
                                    .into(imageProfileAvatar); // Set the loaded image to the ImageView
                        } else {  // If no photo URL is given, set a placeholder image
                            imageProfileAvatar.setImageResource(R.drawable.no_avatar);
                        }

                    } else {
                        Log.d(TAG, "getUserByLogin(), response code " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ViewUserDTO> call, @NonNull Throwable t) {
                    Toast.makeText(requireActivity(), "getUserByLogin() --- Fail", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error fetching user: " + t.fillInStackTrace());
                }
            });
        }
    }

    /** Удаление пользователя
     * через коллбэк диалогового окна
     * */
    private void deleteUser() {
        String id = preferenceManager.getString(Constants.KEY_USER_ID);
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        Log.d(TAG, "ID user: " + id);

        if (!id.equals("id") && authToken != null) {
            userApi.deleteUser(id, new AuthToken(authToken)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                    if (response.isSuccessful()) {
                        preferenceManager.putString(Constants.KEY_APP_TOKEN, null);
                        preferenceManager.putString(Constants.KEY_USER_ID, "id");
                        preferenceManager.putString(Constants.KEY_LOGIN, "login");
                        VK.logout();  // Log out of VK ==> for delete user
                        AuthenticationActivity.startFrom(getActivity()); // Start AuthorizationActivity from this context
                        requireActivity().finish(); // Finish the current activity
                    } else {
                        Toast.makeText(requireActivity(), "deleteUser()", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(requireActivity(), "deleteUser() --- Fail", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error delete user: " + t.fillInStackTrace());
                }
            });
        }
    }

    @Override
    public void onDeleteButtonClick() {
        deleteUser();
    }

    private void showDeleteDialog() {
        DelDialog dialog = new DelDialog(requireActivity(), this);
        dialog.show();
    }
}




//    NotificationsViewModel notificationsViewModel =
//                new ViewModelProvider(this).get(NotificationsViewModel.class);
//
//        binding = FragmentProfileBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final TextView textView = binding.textProfile;
//        notificationsViewModel.getTitle().observe(getViewLifecycleOwner(), textView::setText);

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }