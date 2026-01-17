package com.example.numediapath.ui.view;

import android.os.Bundle;
import android.view.View; // Import nécessaire pour la visibilité
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.numediapath.R;
import com.example.numediapath.data.remote.FirestoreSeeder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Peuplement de la base
        // Conseil : Assure-toi que seedDatabase vérifie si les données existent déjà
        // pour ne pas surcharger Firebase à chaque lancement.
        FirestoreSeeder.seedDatabase(this);

        // 2. Récupérer la barre de navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 3. Configuration du NavHostFragment et du NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        // peuplement de la base
        new Thread(() -> {
            // On lance le seeder dans un thread séparé
            FirestoreSeeder.seedDatabase(getApplicationContext());
        }).start();
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // 4. Lier la barre au contrôleur (Activation automatique des clics)
            // C'est cette ligne qui fait le lien avec les IDs de ton bottom_nav_menu.xml
            NavigationUI.setupWithNavController(bottomNav, navController);

            // 5. GESTION DE LA VISIBILITÉ (Pour un look plus "pro")
            // On cache la barre sur l'onboarding et les détails pour ne pas encombrer l'écran
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                if (id == R.id.homeFragment || id == R.id.detailsFragment) {
                    bottomNav.setVisibility(View.GONE);
                } else {
                    bottomNav.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        return (navHostFragment != null && navHostFragment.getNavController().navigateUp())
                || super.onSupportNavigateUp();
    }
}