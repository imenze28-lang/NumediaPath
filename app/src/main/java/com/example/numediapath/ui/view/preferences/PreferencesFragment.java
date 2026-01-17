package com.example.numediapath.ui.view.preferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.example.numediapath.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;

public class PreferencesFragment extends Fragment {

    private static final String PREF_BG_URL = "https://images.unsplash.com/photo-1488646953014-85cb44e25828?q=80&w=2070&auto=format&fit=crop";

    private int budget = 1000;
    private int duration = 240;
    private int effort = 2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 0. CHARGER L'IMAGE DE FOND
        ImageView ivBg = view.findViewById(R.id.iv_pref_bg);
        Glide.with(this).load(PREF_BG_URL).centerCrop().into(ivBg);

        // 1. SLIDERS (Budget & Durée)
        Slider sliderBudget = view.findViewById(R.id.slider_budget);
        TextView tvBudgetVal = view.findViewById(R.id.tv_budget_value);
        sliderBudget.addOnChangeListener((slider, value, fromUser) -> {
            budget = (int) value;
            tvBudgetVal.setText(budget + " €");
        });

        Slider sliderDuration = view.findViewById(R.id.slider_duration);
        TextView tvDurationVal = view.findViewById(R.id.tv_duration_value);
        sliderDuration.addOnChangeListener((slider, value, fromUser) -> {
            duration = (int) value;
            int h = duration / 60;
            int m = duration % 60;
            tvDurationVal.setText(h + "h " + (m > 0 ? String.format("%02d", m) : "00"));
        });

        // 2. TOGGLE EFFORT
        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.toggle_effort);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_effort_1) effort = 1;
                else if (checkedId == R.id.btn_effort_2) effort = 2;
                else if (checkedId == R.id.btn_effort_3) effort = 3;
            }
        });

        // 3. CHIPS ACTIVITÉS (NOUVEAU)
        ChipGroup chipGroup = view.findViewById(R.id.chip_group_activities);

        // 4. BOUTON GÉNÉRER
        MaterialButton btnGenerate = view.findViewById(R.id.btn_generate);
        btnGenerate.setOnClickListener(v -> {

            // Récupérer les activités cochées
            ArrayList<String> selectedActivities = new ArrayList<>();
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    // On prend le premier mot (ex: "Culture" de "Culture 🎨") et on met en majuscule
                    String tag = chip.getText().toString().split(" ")[0].toUpperCase();
                    selectedActivities.add(tag);
                }
            }

            Bundle bundle = new Bundle();
            bundle.putInt("user_budget", budget);
            bundle.putInt("user_duration", duration);
            bundle.putInt("user_effort", effort);
            bundle.putStringArrayList("user_activities", selectedActivities);

            Navigation.findNavController(v).navigate(R.id.action_preferences_to_result, bundle);
        });

        // 5. LIEN FAVORIS
        View btnFav = view.findViewById(R.id.btn_view_favorites);
        btnFav.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_favorites));
    }
}