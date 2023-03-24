package com.akvamarin.clientappfriends.ui.profile;

import static com.akvamarin.clientappfriends.utils.Utils.getUserAgeVK;

import android.content.Intent;
import android.graphics.Bitmap;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.akvamarin.clientappfriends.AuthorizationActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.dto.User;
import com.akvamarin.clientappfriends.utils.BitmapConvertor;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.utils.Utils;
import com.akvamarin.clientappfriends.vk.models.VKUser;
import com.akvamarin.clientappfriends.vk.requests.VKUsersCommand;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private User user;
    private View viewProfileFragment;
    //private FragmentProfileBinding binding;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private ImageView imageProfileAvatar;
    private LinearLayout linearLayoutLogOut;
    private TextView tvProfileNameAge;
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
            AuthorizationActivity.startFrom(getActivity()); // Start AuthorizationActivity from this context
            requireActivity().finish(); // Finish the current activity
        });


        requestUsers();
        return viewProfileFragment;
    }

    private void initWidgets(){
        toolbar = requireActivity().findViewById(R.id.top_toolbar);
        floatingActionButton = requireActivity().findViewById(R.id.fab_btn);
        imageProfileAvatar = viewProfileFragment.findViewById(R.id.imageProfileAvatar);
        //imageProfileAvatar.setImageResource(R.drawable.avadevushki); //////////////////////////////////////ava
        linearLayoutLogOut = viewProfileFragment.findViewById(R.id.layoutLogOut);
        tvProfileNameAge = viewProfileFragment.findViewById(R.id.tvProfileNameAge);
        preferenceManager = new PreferenceManager(requireActivity().getApplicationContext());

        String name = preferenceManager.getString(Constants.KEY_NAME);
        String age = preferenceManager.getString(Constants.KEY_AGE);
        String imgBase64 = preferenceManager.getString(Constants.KEY_IMAGE_BASE64);
       /* tvProfileNameAge.setText(name + ", " + age);

        if (!imgBase64.equalsIgnoreCase("image")){
            Bitmap bitmap = BitmapConvertor.convertFromBase64ToBitmap(preferenceManager.getString(Constants.KEY_IMAGE_BASE64));
            imageProfileAvatar.setImageBitmap(bitmap);
        }*/

//        String img = preferenceManager.getString(Constants.KEY_IMAGE);
//        if (!img.equalsIgnoreCase("image")){
//            Picasso.get()
//                    .load(img)
//                    .fit()
//                    .error(R.drawable.error_loading_image)
//                    .into(imageProfileAvatar);   //.setLoggingEnabled(true)
//        }
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

    /** Все поля для VK API
     * https://vk.com/dev.php?method=user&prefix=objects
     * */
    private void requestUsers() {
        VK.execute(new VKUsersCommand(new int[0]), new VKApiCallback<List<VKUser>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void success(List<VKUser> result) {
                if (!requireActivity().isFinishing() && !result.isEmpty()) {
                    //TextView nameTV = findViewById(R.id.nameTV);
                    VKUser user = result.get(0);
                    String nameUser = String.format("%s %s, %s %s", user.firstName, user.lastName, getUserAgeVK(user.dateOfBirth), user.email);
                    tvProfileNameAge.setText(nameUser);

                    if (!TextUtils.isEmpty(user.photo)) {  // Check if the photo URL is not empty
                        Picasso.get().load(user.photo) // Load the image from the URL
                                .error(R.drawable.no_avatar) // If an error occurs, set a placeholder image
                                .into(imageProfileAvatar); // Set the loaded image to the ImageView
                    } else {  // If no photo URL is given, set a placeholder image
                        imageProfileAvatar.setImageResource(R.drawable.no_avatar);
                    }
                }
            }

            @Override
            public void fail(@NonNull Exception error) {
                Log.e(TAG, error.toString());
            }
        });
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