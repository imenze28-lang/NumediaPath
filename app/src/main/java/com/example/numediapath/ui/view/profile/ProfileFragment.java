package com.example.numediapath.ui.view.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.numediapath.R;
import com.example.numediapath.data.model.UserProfile;
import com.example.numediapath.ui.view.auth.LoginActivity;
import com.example.numediapath.ui.viewmodel.RouteViewModel;
import com.example.numediapath.ui.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private RouteViewModel routeViewModel;
    private UserViewModel userViewModel;
    private TextView tvUserName, tvUserEmail, tvFavCount, tvTripCount, tvKmCount;
    private ImageView imgAvatar;
    private ImageButton btnLogout;
    private MaterialButton btnSettings, btnHelp, editProfileButton;

    private FirebaseAuth mAuth;

    // ✅ LANCEUR POUR LA GALERIE
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    loadAvatar(uri.toString());
                    if (userViewModel != null) {
                        userViewModel.updateProfileImage(uri.toString());
                    }
                    Toast.makeText(getContext(), "Photo de profil mise à jour", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        initViews(view);
        setupUserProfile();
        setupFavoriteObserver();
        setupListeners();
    }

    private void initViews(View view) {
        imgAvatar = view.findViewById(R.id.img_avatar_profile);
        tvUserName = view.findViewById(R.id.tv_profile_name);
        tvUserEmail = view.findViewById(R.id.tv_profile_email);
        tvFavCount = view.findViewById(R.id.tv_stat_favs);
        tvTripCount = view.findViewById(R.id.tv_stat_trips);
        tvKmCount = view.findViewById(R.id.tv_stat_km);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnHelp = view.findViewById(R.id.btn_help);
        editProfileButton = view.findViewById(R.id.btn_edit_profile);
    }

    private void setupUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // 1. AFFICHAGE IMMÉDIAT (Données de base Firebase)
            tvUserEmail.setText(user.isAnonymous() ? "Mode invité" : user.getEmail());
            String initialName = (user.getDisplayName() != null) ? user.getDisplayName() : "Imen";
            tvUserName.setText(initialName);

            // Image par défaut si Room est encore vide
            loadAvatar("https://i.pravatar.cc/423?u=" + initialName);

            // 2. MISE À JOUR DYNAMIQUE (Depuis Room)
            userViewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
                if (profile != null) {
                    // Si Room contient un nom, on l'utilise
                    if (profile.getName() != null) tvUserName.setText(profile.getName());

                    // Si Room contient une image personnalisée, on l'affiche
                    if (profile.getProfileImageUrl() != null && !profile.getProfileImageUrl().isEmpty()) {
                        loadAvatar(profile.getProfileImageUrl());
                    }
                } else {
                    // Initialisation du profil dans Room s'il n'existe pas encore
                    UserProfile newProfile = new UserProfile();
                    newProfile.setId(user.getUid());
                    newProfile.setName(initialName);
                    userViewModel.saveProfile(newProfile);
                }
            });
        }
    }

    private void loadAvatar(String url) {
        Glide.with(this)
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(imgAvatar);
    }

    private void setupFavoriteObserver() {
        routeViewModel.getFavoriteRoutes().observe(getViewLifecycleOwner(), favs -> {
            int count = (favs != null) ? favs.size() : 0;
            tvFavCount.setText(String.valueOf(count));
        });
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        editProfileButton.setOnClickListener(v -> mGetContent.launch("image/*"));

        btnSettings.setOnClickListener(v ->
                Toast.makeText(getContext(), "Paramètres : Notifications activées", Toast.LENGTH_SHORT).show());

        btnHelp.setOnClickListener(v ->
                Toast.makeText(getContext(), "Support : support@numediapath.dz", Toast.LENGTH_LONG).show());
    }
}