package com.akvamarin.clientappfriends.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.akvamarin.clientappfriends.AuthorizationActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.dto.User;
import com.akvamarin.clientappfriends.utils.BitmapConvertor;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vk.api.sdk.VK;

public class ProfileFragment extends Fragment {
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


            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
            GoogleSignIn.getClient(requireActivity().getApplicationContext(), googleSignInOptions).signOut();

            if (VK.isLoggedIn()) {
                VK.logout();
            }

            Intent intent = new Intent(requireActivity(), AuthorizationActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });



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
        tvProfileNameAge.setText(name + ", " + age);

        if (!imgBase64.equalsIgnoreCase("image")){
            Bitmap bitmap = BitmapConvertor.convertFromBase64ToBitmap(preferenceManager.getString(Constants.KEY_IMAGE_BASE64));
            imageProfileAvatar.setImageBitmap(bitmap);
        }

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