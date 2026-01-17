package com.example.numediapath.ui.view.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class FavoritesFragment extends Fragment {

    private RouteViewModel routeViewModel;
    private RouteAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout emptyState;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Initialisation UI
        recyclerView = view.findViewById(R.id.recycler_favorites);
        emptyState = view.findViewById(R.id.layout_empty_state);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RouteAdapter();
        recyclerView.setAdapter(adapter);

        // --- NOUVEAU : BOUTON RETOUR ---
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            // Revient à l'écran précédent
            Navigation.findNavController(v).popBackStack();
        });
        // -------------------------------

        // 2. Connexion au ViewModel
        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        // 3. Observation des Favoris
        routeViewModel.getFavoriteRoutes().observe(getViewLifecycleOwner(), favRoutes -> {
            if (favRoutes == null || favRoutes.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
                adapter.setRoutes(favRoutes);
            }
        });

        // 4. Clic sur un favori -> Ouvre le détail
        adapter.setOnRouteClickListener(route -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selected_route", route);
            Navigation.findNavController(view).navigate(R.id.action_favorites_to_details, bundle);
        });
    }
}