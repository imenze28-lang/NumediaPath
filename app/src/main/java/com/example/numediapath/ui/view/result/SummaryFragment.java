package com.example.numediapath.ui.view.result;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.numediapath.R;
import com.example.numediapath.data.model.RoutePlan;
import com.example.numediapath.ui.adapter.CheckableStepAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SummaryFragment extends Fragment {

    private RoutePlan route;
    private TextView tvTitle, tvDuration, tvBudget, tvDistance;
    private RatingBar ratingBar;
    private EditText etComment;
    private RecyclerView recyclerView;
    private Button btnAddPhotos, btnHome, btnPublish;
    private LottieAnimationView animationConfetti;

    private final ActivityResultLauncher<Intent> photoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Toast.makeText(getContext(), "Photos ajoutées au récit ! 📸", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        if (getArguments() != null) {
            route = (RoutePlan) getArguments().getSerializable("finished_route");
            if (route != null) {
                displaySummary();
            }
        }
        setupListeners();
    }

    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_summary_title);
        tvDuration = view.findViewById(R.id.tv_summary_duration);
        tvBudget = view.findViewById(R.id.tv_summary_budget);
        tvDistance = view.findViewById(R.id.tv_summary_distance);
        recyclerView = view.findViewById(R.id.recycler_summary_steps);

        ratingBar = view.findViewById(R.id.rating_bar);
        etComment = view.findViewById(R.id.et_summary_comment);

        btnAddPhotos = view.findViewById(R.id.btn_add_photos);
        btnPublish = view.findViewById(R.id.btn_share_summary);
        btnHome = view.findViewById(R.id.btn_back_home);
        animationConfetti = view.findViewById(R.id.animation_confetti);
    }

    private void displaySummary() {
        tvTitle.setText(route.getName());
        tvDuration.setText(route.getTotalDuration() + " min");
        tvBudget.setText(route.getTotalCost() + " €");
        tvDistance.setText(route.getTotalDistance() + " km");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CheckableStepAdapter(route.getSteps()));

        if (animationConfetti != null) {
            animationConfetti.playAnimation();
        }
    }

    private void setupListeners() {
        // Retour à l'accueil
        btnHome.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_summary_to_home));

        // Ajout de photos
        btnAddPhotos.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerLauncher.launch(intent);
        });

        // ✅ ACTION : Le bouton rouge affiche la popup de confirmation
        btnPublish.setOnClickListener(v -> showPublishConfirmationDialog());

        // Partages rapides (WhatsApp/Insta)
        if (getView() != null) {
            getView().findViewById(R.id.img_insta).setOnClickListener(v -> shareToApp("com.instagram.android"));
            getView().findViewById(R.id.img_whatsapp).setOnClickListener(v -> shareToApp("com.whatsapp"));
        }
    }

    private void showPublishConfirmationDialog() {
        if (route == null) return;

        float stars = ratingBar.getRating();
        String userAvis = etComment.getText().toString();
        if (userAvis.isEmpty()) userAvis = "Expérience incroyable !";

        // Génération du texte final
        String finalMessage = "🌟 Voyage terminé : " + route.getName() + "\n" +
                "💬 Mon avis : " + userAvis + "\n" +
                "📊 Performance : " + route.getTotalDistance() + "km en " + route.getTotalDuration() + " min.";

        // ✅ POPUP DE CONFIRMATION
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Récapitulatif avant envoi")
                .setMessage("⭐ Note : " + stars + "/5\n\n" + finalMessage)
                .setNegativeButton("ANNULER", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("PUBLIER", (dialog, which) -> {
                    triggerFinalShare(finalMessage, stars);
                })
                .show();
    }

    private void triggerFinalShare(String summary, float stars) {
        String fullMessage = "Note : " + stars + "/5\n" + summary + "\nPublié via NumediaPath 🚀";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, fullMessage);
        startActivity(Intent.createChooser(intent, "Partager mon voyage"));

        Toast.makeText(getContext(), "Publication envoyée ! 🚀", Toast.LENGTH_SHORT).show();
    }

    private void shareToApp(String packageName) {
        if (route == null) return;

        float stars = ratingBar.getRating();
        String avis = etComment.getText().toString();
        if (avis.isEmpty()) avis = "Génial !";

        String message = "🌍 J'ai terminé " + route.getName() + " (" + stars + "/5 stars)\n" + avis;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setPackage(packageName);

        try {
            startActivity(intent);
        } catch (Exception ex) {
            intent.setPackage(null);
            startActivity(Intent.createChooser(intent, "Partager avec..."));
        }
    }
}