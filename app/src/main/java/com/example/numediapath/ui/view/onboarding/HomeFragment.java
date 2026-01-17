package com.example.numediapath.ui.view.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.example.numediapath.R;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Liaison avec le fichier fragment_home.xml
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Récupération des vues
        ImageView ivBg = view.findViewById(R.id.iv_home_bg);
        Button btnStart = view.findViewById(R.id.btn_start);

        // ✅ AMÉLIORATION VISUELLE : Chargement de l'image locale
        // On utilise Glide pour charger R.drawable.bg_home_travel.
        // Cela permet de gérer la mémoire (RAM) efficacement tout en affichant TA photo.
        Glide.with(this)
                .load(R.drawable.bg_home_travel)
                .centerCrop()
                .into(ivBg);

        // ✅ FONCTIONNALITÉ CONSERVÉE : Navigation vers Explorer
        // Utilise l'action définie dans ton nav_graph.xml
        btnStart.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigate(R.id.action_home_to_explorer);
            } catch (IllegalArgumentException e) {
                // Sécurité au cas où l'ID de l'action changerait dans le nav_graph
                e.printStackTrace();
            }
        });
    }
}