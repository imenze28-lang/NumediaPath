package com.example.numediapath.ui.view.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.numediapath.R;
import com.example.numediapath.ui.adapter.PopularAdapter;
import com.example.numediapath.ui.viewmodel.RouteViewModel;
import com.example.numediapath.ui.viewmodel.UserViewModel; // Ajouté

import java.util.ArrayList;
import java.util.List;

public class ExplorerFragment extends Fragment {

    private RouteViewModel routeViewModel;
    private UserViewModel userViewModel; // Ajouté
    private View notificationBadge;
    private AutoCompleteTextView etSearch;
    private TextView tvUserName; // Ajouté
    private ImageView imgProfile; // Ajouté

    private final String[] COUNTRIES = {
            "Algérie", "Dubaï", "France", "Italie", "Japon",
            "Vietnam", "Mexique", "USA", "Maroc", "Ukraine"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explorer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- INITIALISATION DES VIEWMODELS ---
        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class); // Pour le profil

        // --- LIAISON DES VUES ---
        etSearch = view.findViewById(R.id.etSearch);
        tvUserName = view.findViewById(R.id.tvUserNameExplorer); // Liaison
        imgProfile = view.findViewById(R.id.imgProfileExplorer); // Liaison
        RecyclerView rvPopular = view.findViewById(R.id.rvPopularDestinations);
        ImageButton btnNotification = view.findViewById(R.id.btnNotification);
        notificationBadge = view.findViewById(R.id.notificationBadge);
        LottieAnimationView lottieAnimation = view.findViewById(R.id.lottieAnimation);

        // --- 1. LOGIQUE PROFIL DYNAMIQUE (PRÉNOM + URL PHOTO) ---
        userViewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                // Mise à jour du nom
                tvUserName.setText(profile.getName() != null ? profile.getName() : "Imen");

                // Chargement de l'URL de l'image de profil
                String url = profile.getProfileImageUrl();
                Glide.with(this)
                        .load(url != null && !url.isEmpty() ? url : "https://i.pravatar.cc/423?u=imen")
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .into(imgProfile);
            }
        });

        // --- 2. CONFIGURATION LOTTIE (CONSERVÉ) ---
        if (lottieAnimation != null) {
            lottieAnimation.setAnimation(R.raw.travel_animation);
            lottieAnimation.playAnimation();
        }

        // --- 3. AUTO-COMPLÉTION (CONSERVÉ) ---
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        etSearch.setAdapter(searchAdapter);
        etSearch.setThreshold(1);
        etSearch.setOnItemClickListener((parent, v, position, id) -> {
            String selectedCountry = (String) parent.getItemAtPosition(position);
            navigateToPreferences(selectedCountry);
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String country = etSearch.getText().toString().trim();
                if (!country.isEmpty()) navigateToPreferences(country);
                return true;
            }
            return false;
        });

        // --- 4. DESTINATIONS POPULAIRES (CONSERVÉ V1) ---
        setupPopularList(rvPopular);

        // --- 5. NOTIFICATIONS (CONSERVÉ) ---
        btnNotification.setOnClickListener(v -> {
            notificationBadge.setVisibility(View.GONE);
            Toast.makeText(getContext(), "TravelShare : 3 nouveaux récits de voyage !", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupPopularList(RecyclerView rv) {
        List<PopularDestination> populars = new ArrayList<>();

        populars.add(new PopularDestination(
                "Algérie", 4.8f, "La Casbah est une merveille historique !",
                "https://firebasestorage.googleapis.com/v0/b/numediapath-imen.firebasestorage.app/o/destinations%2Fdz_alger_casbah.jpg?alt=media&token=464600b6-acfc-4843-8b35-914a6d59dd02",
                "https://i.pravatar.cc/150?u=imen"
        ));

        populars.add(new PopularDestination(
                "Dubaï", 4.5f, "Architecture futuriste incroyable.",
                "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?w=800",
                "https://i.pravatar.cc/150?u=sara"
        ));

        populars.add(new PopularDestination(
                "France", 4.2f, "Paris est toujours une bonne idée.",
                "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=800",
                "https://i.pravatar.cc/150?u=jean"
        ));

        PopularAdapter adapter = new PopularAdapter(populars, this::navigateToPreferences);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
    }

    private void navigateToPreferences(String country) {
        routeViewModel.setSelectedCountry(country);
        Navigation.findNavController(requireView()).navigate(R.id.action_explorer_to_preferencesFragment);
    }
}