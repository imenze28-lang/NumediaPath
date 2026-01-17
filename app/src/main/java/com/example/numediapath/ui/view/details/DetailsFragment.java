package com.example.numediapath.ui.view.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.bumptech.glide.Glide;
import com.example.numediapath.R;
import com.example.numediapath.data.model.RoutePlan;
import com.example.numediapath.data.model.RouteStep;
import com.example.numediapath.ui.adapter.StepAdapter;
import com.example.numediapath.ui.adapter.ReviewAdapter;
import com.example.numediapath.ui.viewmodel.RouteViewModel;

import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailsFragment extends Fragment {

    private TextView tvName, tvType, tvMetrics, tvTags, tvPrice, tvWeather, tvDistance;
    private ImageView imgHeader;
    private ImageButton btnBack, btnPdf, btnShare, btnFavoriteStar;
    private Button btnFinish;

    private RecyclerView recyclerSteps, recyclerReviews;
    private RoutePlan route;
    private RouteViewModel routeViewModel;
    private MapView map;

    private final OkHttpClient client = new OkHttpClient();
    private final String API_KEY = "90558f92e495caa809512d168767bf4f";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);

        initViews(view);
        setupMap();
        setupRecyclerViews();
        setupListeners(view);

        if (getArguments() != null) {
            route = (RoutePlan) getArguments().getSerializable("selected_route");
            if (route != null) {
                displayRouteData(route);
                updateFavoriteStar(route.isFavorite());
                drawRouteOnMap(route);
                loadSteps(route);
                fetchWeather(route);
                loadSimulatedReviews();
            }
        }
    }

    private void initViews(View view) {
        tvName = view.findViewById(R.id.tv_route_name);
        tvType = view.findViewById(R.id.tv_route_type);
        tvMetrics = view.findViewById(R.id.tv_route_metrics);
        tvDistance = view.findViewById(R.id.tv_route_distance);
        tvTags = view.findViewById(R.id.tv_route_desc);
        tvPrice = view.findViewById(R.id.tv_route_price);
        tvWeather = view.findViewById(R.id.tv_weather_info);
        imgHeader = view.findViewById(R.id.img_header);

        btnBack = view.findViewById(R.id.btn_back);
        btnShare = view.findViewById(R.id.btn_share);
        btnPdf = view.findViewById(R.id.btn_export_pdf);
        btnFavoriteStar = view.findViewById(R.id.btn_favorite_star);
        btnFinish = view.findViewById(R.id.btn_finish_trip);

        recyclerSteps = view.findViewById(R.id.recycler_steps);
        recyclerReviews = view.findViewById(R.id.recycler_reviews);
        map = view.findViewById(R.id.map_osm);
    }

    private void setupRecyclerViews() {
        recyclerSteps.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerSteps.setNestedScrollingEnabled(false);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupListeners(View view) {
        if (btnBack != null) btnBack.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                if (route != null) shareRoute(route);
            });
        }

        if (btnFinish != null) {
            btnFinish.setOnClickListener(v -> {
                if (route != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("finished_route", route);
                    Navigation.findNavController(v).navigate(R.id.action_details_to_summary, bundle);
                }
            });
        }

        // ✅ CORRECTION : Appel de la génération réelle du PDF
        if (btnPdf != null) {
            btnPdf.setOnClickListener(v -> {
                if (route != null) {
                    generateRoutePDF(route);
                }
            });
        }

        btnFavoriteStar.setOnClickListener(v -> {
            if (route != null) {
                boolean newStatus = !route.isFavorite();
                route.setFavorite(newStatus);
                updateFavoriteStar(newStatus);
                routeViewModel.toggleFavorite(route);
                Toast.makeText(getContext(), newStatus ? "Ajouté aux favoris ⭐" : "Retiré", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWeather(RoutePlan r) {
        if (tvWeather == null || r.getSteps() == null || r.getSteps().isEmpty()) return;

        double lat = r.getSteps().get(0).getLat();
        double lon = r.getSteps().get(0).getLon();

        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat +
                "&lon=" + lon + "&units=metric&lang=fr&appid=" + API_KEY;

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                android.util.Log.e("WEATHER_ERROR", "Échec réseau : " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() -> tvWeather.setText("Erreur réseau 🌐"));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        double temp = json.getJSONObject("main").getDouble("temp");
                        String desc = json.getJSONArray("weather").getJSONObject(0).getString("description");

                        // Capitalisation
                        String capDesc = desc.substring(0, 1).toUpperCase() + desc.substring(1);

                        new Handler(Looper.getMainLooper()).post(() ->
                                tvWeather.setText(String.format("🌡️ %.1f°C - %s", temp, capDesc)));
                    } catch (Exception e) {
                        android.util.Log.e("WEATHER_ERROR", "Erreur JSON : " + e.getMessage());
                    }
                } else {
                    android.util.Log.e("WEATHER_ERROR", "Code erreur API : " + response.code());
                    new Handler(Looper.getMainLooper()).post(() ->
                            tvWeather.setText("Service météo indisponible (" + response.code() + ")"));
                }
            }
        });
    }

    private void setupMap() {
        if (map == null) return;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
    }

    @Override public void onResume() { super.onResume(); if (map != null) map.onResume(); }
    @Override public void onPause() { super.onPause(); if (map != null) map.onPause(); }

    private void drawRouteOnMap(RoutePlan r) {
        if (map == null || r.getSteps() == null || r.getSteps().isEmpty()) return;
        map.getOverlays().clear();

        List<GeoPoint> pathPoints = new ArrayList<>();
        for (RouteStep step : r.getSteps()) {
            GeoPoint gp = new GeoPoint(step.getLat(), step.getLon());
            pathPoints.add(gp);
            Marker marker = new Marker(map);
            marker.setPosition(gp);
            marker.setTitle(step.getTitle());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(marker);
        }

        if (pathPoints.size() > 1) {
            Polyline line = new Polyline();
            line.setPoints(pathPoints);
            line.setColor(Color.parseColor("#FF5722"));
            line.setWidth(10.0f);
            map.getOverlays().add(line);
        }
        map.getController().setCenter(pathPoints.get(0));
        map.invalidate();
    }

    private void shareRoute(RoutePlan r) {
        Intent si = new Intent(Intent.ACTION_SEND);
        si.setType("text/plain");
        si.putExtra(Intent.EXTRA_TEXT, "Regarde ce voyage sur NumediaPath : " + r.getName());
        startActivity(Intent.createChooser(si, "Partager via"));
    }

    private void updateFavoriteStar(boolean isFav) {
        if (btnFavoriteStar != null) {
            btnFavoriteStar.setImageResource(isFav ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        }
    }

    private void displayRouteData(RoutePlan r) {
        tvName.setText(r.getName());
        tvType.setText(r.getType());
        tvPrice.setText(r.getTotalCost() + " €");
        tvMetrics.setText(r.getTotalDuration() + " min");
        tvDistance.setText(r.getTotalDistance() + " km");
        tvTags.setText(r.getTags());
        Glide.with(this).load(r.getImageUrl()).centerCrop().placeholder(R.color.primary_variant).into(imgHeader);
    }

    private void loadSteps(RoutePlan r) {
        if (r.getSteps() != null && !r.getSteps().isEmpty()) {
            recyclerSteps.setAdapter(new StepAdapter(r.getSteps()));
        }
    }

    private void loadSimulatedReviews() {
        List<ReviewAdapter.Review> reviews = new ArrayList<>();
        reviews.add(new ReviewAdapter.Review("Sophie L.", "Génial !", "5.0", null));
        recyclerReviews.setAdapter(new ReviewAdapter(reviews));
    }

    // ---  MÉTHODES GÉNÉRATION PDF ---

    private void generateRoutePDF(RoutePlan route) {
        android.graphics.pdf.PdfDocument document = new android.graphics.pdf.PdfDocument();
        android.graphics.pdf.PdfDocument.PageInfo pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create();
        android.graphics.pdf.PdfDocument.Page page = document.startPage(pageInfo);

        android.graphics.Canvas canvas = page.getCanvas();
        android.graphics.Paint paint = new android.graphics.Paint();

        paint.setColor(android.graphics.Color.parseColor("#00796B"));
        paint.setTextSize(22f);
        paint.setFakeBoldText(true);
        canvas.drawText("FICHE TRAJET : " + route.getName().toUpperCase(), 50, 50, paint);

        paint.setStrokeWidth(2f);
        canvas.drawLine(50, 65, 545, 65, paint);

        paint.setColor(android.graphics.Color.BLACK);
        paint.setTextSize(14f);
        paint.setFakeBoldText(false);
        canvas.drawText("Type : " + route.getType(), 50, 100, paint);
        canvas.drawText("Prix estimé : " + route.getTotalCost() + " €", 50, 125, paint);
        canvas.drawText("Durée : " + route.getTotalDuration() + " min", 50, 150, paint);
        canvas.drawText("Distance : " + route.getTotalDistance() + " km", 50, 175, paint);

        paint.setFakeBoldText(true);
        canvas.drawText("Description :", 50, 215, paint);
        paint.setFakeBoldText(false);
        canvas.drawText(route.getTags(), 50, 235, paint);

        paint.setFakeBoldText(true);
        canvas.drawText("Étapes du parcours :", 50, 280, paint);
        paint.setFakeBoldText(false);

        int y = 310;
        if (route.getSteps() != null) {
            for (RouteStep step : route.getSteps()) {
                canvas.drawCircle(60, y - 5, 3, paint);
                canvas.drawText(step.getTitle() + " (" + step.getTime() + ")", 80, y, paint);
                y += 25;
                if (y > 800) break;
            }
        }

        document.finishPage(page);

        String fileName = "Details_" + route.getName().replaceAll("\\s+", "_") + ".pdf";
        java.io.File file = new java.io.File(requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS), fileName);

        try {
            document.writeTo(new java.io.FileOutputStream(file));
            Toast.makeText(getContext(), "Génération du PDF terminée...", Toast.LENGTH_SHORT).show();
            openPDFFile(file);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            document.close();
        }
    }

    private void openPDFFile(java.io.File file) {
        android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + ".provider", file);

        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(android.content.Intent.createChooser(intent, "Ouvrir avec..."));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Aucun lecteur PDF installé", Toast.LENGTH_SHORT).show();
        }
    }
}