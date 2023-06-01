package com.akvamarin.clientappfriends.view.ui.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.akvamarin.clientappfriends.api.presentor.BaseCallback;
import com.akvamarin.clientappfriends.api.presentor.userdata.UserCallback;
import com.akvamarin.clientappfriends.api.presentor.userdata.UserDataApi;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.ViewUserDTO;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.utils.Utils;
import com.akvamarin.clientappfriends.view.AllEventsActivity;
import com.akvamarin.clientappfriends.view.AuthenticationActivity;
import com.akvamarin.clientappfriends.view.dialog.DelDialog;
import com.akvamarin.clientappfriends.view.dialog.DelDialogListener;
import com.akvamarin.clientappfriends.view.dialog.ErrorDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.vk.api.sdk.VK;

import java.time.LocalDate;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ProfileFragment extends Fragment implements DelDialogListener {
    private static final String TAG = "ProfileFragment";
    private View viewProfileFragment;
    //private FragmentProfileBinding binding;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private ImageView imageProfileAvatar;
    private LinearLayout linearLayoutLogOut;
    private LinearLayout layoutDeleteAccount;
    private ImageButton imageBtnEditProfileView;
    private TextView tvProfileNameAge;
    private ViewUserDTO user;
    private UserDataApi userDataApi;
    private PreferenceManager preferenceManager;
    private String loading;

    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    ViewUserDTO updatedViewUserDTO = result.getData().getParcelableExtra("viewUserDTO");
                    if (updatedViewUserDTO != null) {
                        updateViewUserDTO(updatedViewUserDTO);
                    }
                }
            }
    );

    public View onCreateView(@NonNull LayoutInflater layoutInflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewProfileFragment = layoutInflater.inflate(R.layout.fragment_profile, container, false);
        initWidgets();
        updateToolbar();
        updateMainFAB();
        startEditActivityWithFAB();

        linearLayoutLogOut.setOnClickListener(view -> {
            VK.logout();  // Log out of VK
            preferenceManager.clear(); // all values clear
            AuthenticationActivity.startFrom(getActivity()); // Start AuthorizationActivity from this context
            requireActivity().finish(); // Finish the current activity
        });

        layoutDeleteAccount.setOnClickListener(view -> showDeleteDialog());

        imageBtnEditProfileView.setOnClickListener(view -> startViewProfileActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initUser();
        }

        return viewProfileFragment;
    }

    private void initWidgets(){
        toolbar = requireActivity().findViewById(R.id.top_toolbar);
        floatingActionButton = requireActivity().findViewById(R.id.fab_btn);
        imageProfileAvatar = viewProfileFragment.findViewById(R.id.imageProfileAvatar);
        linearLayoutLogOut = viewProfileFragment.findViewById(R.id.layoutLogOut);
        layoutDeleteAccount = viewProfileFragment.findViewById(R.id.layoutDeleteAccount);
        tvProfileNameAge = viewProfileFragment.findViewById(R.id.tvProfileNameAge);
        imageBtnEditProfileView = viewProfileFragment.findViewById(R.id.imageBtnEditProfileView);

        userDataApi = new UserDataApi(requireActivity());
        preferenceManager = new PreferenceManager(requireActivity());
        loading = requireActivity().getString(R.string.loading);
    }


    private void updateToolbar(){
        toolbar.setTitle(R.string.title_profile);
        toolbar.setLogo(R.drawable.ic_person);
        setHasOptionsMenu(true);    // нужен для вызова onCreateOptionsMenu
    }

    private void updateMainFAB(){
        floatingActionButton.setImageResource(R.drawable.ic_profile_edit);
        floatingActionButton.setVisibility(View.VISIBLE);
    }

    private void startEditActivityWithFAB() {
        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });
    }

    private void startViewProfileActivity() {
        Intent intent = new Intent(getActivity(), ViewProfileActivity.class);
        intent.putExtra("userId", user.getId());
        startActivity(intent);
    }

    /** Получение данных пользователя
     * по его логину
     * */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initUser(){
        AllEventsActivity allEventsActivity = (AllEventsActivity) requireActivity();
        allEventsActivity.showProgressDialog(loading);

        userDataApi.getUserByLogin(new UserCallback() {
            @Override
            public void onUserRetrieved(ViewUserDTO user) {
                ProfileFragment.this.user = user;
                allEventsActivity.dismissProgressDialog();
                updateViewUserDTO(user);
                Log.d(TAG, "Save user ID: " + user.getId());
            }

            @Override
            public void onUserRetrievalError(int responseCode) {
                allEventsActivity.dismissProgressDialog();
                showErrorDialog(responseCode);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateViewUserDTO(ViewUserDTO viewUserDTO) {
        this.user = viewUserDTO;
        LocalDate birthday = LocalDate.parse(user.getDateOfBirthday());
        int age = Utils.getAgeWithCalendar(birthday.getYear(), birthday.getMonthValue(), birthday.getDayOfMonth());

        String nameUser = String.format("%s, %s ", user.getNickname(), age);
        tvProfileNameAge.setText(nameUser);

        Log.d(TAG, "user.getUrlAvatar() " + user.getUrlAvatar());
        if (!TextUtils.isEmpty(user.getUrlAvatar())) {
            Picasso.get().load(user.getUrlAvatar()) // Load the image from the URL
                    .error(R.drawable.no_avatar)
                    .into(imageProfileAvatar); // Set to ImageView
        } else {  // If no photo URL
            imageProfileAvatar.setImageResource(R.drawable.no_avatar);
        }
    }

    /** Удаление пользователя
     * через коллбэк диалогового окна
     * */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void deleteUser(){
        AllEventsActivity allEventsActivity = (AllEventsActivity) requireActivity();
        allEventsActivity.showProgressDialog(loading);

        userDataApi.deleteUser(new BaseCallback() {
            @Override
            public void onRetrieved() {
                preferenceManager.clear(); // all values clear
                VK.logout();  // Log out of VK ==> for delete user
                AuthenticationActivity.startFrom(getActivity()); // Start AuthorizationActivity from this context
                requireActivity().finish(); // Finish the current activity
            }

            @Override
            public void onRetrievalError(int responseCode) {
                allEventsActivity.dismissProgressDialog();
                showErrorDialog(responseCode);
            }
        });
    }

    @Override
    public void onDeleteButtonClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deleteUser();
        }
    }

    private void showDeleteDialog() {
        DelDialog dialog = new DelDialog(requireActivity(), this);
        dialog.show();
    }

    private void showErrorDialog(int responseCode) {
        ErrorDialog dialog = new ErrorDialog(requireActivity(), responseCode);
        dialog.show();
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        if (user == null) {
            initUser();
        }
    }
}
