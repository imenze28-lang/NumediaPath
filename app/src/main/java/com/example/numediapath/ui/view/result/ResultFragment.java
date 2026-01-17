package com.example.numediapath.ui.view.result;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.numediapath.R;
import com.example.numediapath.ui.adapter.RouteAdapter;
import com.example.numediapath.ui.viewmodel.RouteViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class ResultFragment extends Fragment {

    private RouteViewModel routeViewModel;
    private RouteAdapter adapter;
    private TextView tvResultCount;
    private TextView tvResultTitle;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ✅ Correction : Bien retourner le layout
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Liaison des vues
        RecyclerView recyclerView = view.findViewById(R.id.recycler_routes);
        MaterialButton btnBackSearch = view.findViewById(R.id.btn_back_search);
        tvResultCount = view.findViewById(R.id.tv_result_count);
        tvResultTitle = view.findViewById(R.id.tv_result_title);

        // 2. Configuration RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RouteAdapter();
        recyclerView.setAdapter(adapter);

        // 3. ViewModel Partagé (Indispensable pour le pays)
        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);

        // 4. Titre dynamique
        String country = routeViewModel.getSelectedCountry();
        if (country != null && !country.isEmpty()) {
            tvResultTitle.setText("Destinations trouvées pour " + country);
        }

        // 5. Bouton Modifier
        btnBackSearch.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // 6. Clic sur un itinéraire
        adapter.setOnRouteClickListener(route -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selected_route", route);
            Navigation.findNavController(view).navigate(R.id.action_result_to_details, bundle);
        });

        // 7. Récupération et application des filtres
        if (getArguments() != null) {
            int budget = getArguments().getInt("user_budget", 1000);
            int duration = getArguments().getInt("user_duration", 480);
            int effort = getArguments().getInt("user_effort", 2);
            ArrayList<String> activities = getArguments().getStringArrayList("user_activities");

            Log.d("DEBUG_RESULT", "Filtrage lancé pour : " + country + " | Budget: " + budget);
            routeViewModel.generateFilteredRoutes(budget, duration, effort, activities);
        }

        // 8. Observation des résultats
        routeViewModel.getFilteredRoutes().observe(getViewLifecycleOwner(), routes -> {
            if (routes != null) {
                Log.d("DEBUG_RESULT", "Nombre de routes reçues : " + routes.size());
                adapter.setRoutes(routes);
                tvResultCount.setText(String.valueOf(routes.size()));

                if (routes.isEmpty()) {
                    tvResultTitle.setText("Aucun résultat pour " + country);
                }
            } else {
                Log.e("DEBUG_RESULT", "La liste des routes est NULL");
            }
        });
    }
}