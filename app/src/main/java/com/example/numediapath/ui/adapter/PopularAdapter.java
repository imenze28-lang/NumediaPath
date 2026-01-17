package com.example.numediapath.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.numediapath.R;
import com.example.numediapath.ui.view.onboarding.PopularDestination;
import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private final List<PopularDestination> destinations;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String countryName);
    }

    public PopularAdapter(List<PopularDestination> destinations, OnItemClickListener listener) {
        this.destinations = destinations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popular_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PopularDestination d = destinations.get(position);

        // Liaison des textes
        holder.name.setText(d.name);
        holder.rating.setRating(d.rating);
        holder.review.setText("\"" + d.review + "\"");

        // 1. Chargement de l'image de la destination
        Glide.with(holder.itemView.getContext())
                .load(d.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.bg_rounded_card)
                .into(holder.image);

        // 2. Chargement de la photo de profil (Nouveauté)
        if (holder.ivUserProfile != null) {
            Glide.with(holder.itemView.getContext())
                    .load(d.profileImageUrl)
                    .circleCrop() // Pour être sûr que c'est bien rond
                    .placeholder(R.drawable.ic_profile_placeholder) // Image par défaut
                    .into(holder.ivUserProfile);
        }

        // Clic sur la carte
        holder.itemView.setOnClickListener(v -> listener.onItemClick(d.name));
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, ivUserProfile;
        TextView name, review;
        RatingBar rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // On récupère les IDs exacts du XML
            image = itemView.findViewById(R.id.ivPopularImage);
            name = itemView.findViewById(R.id.tvPopularName);
            review = itemView.findViewById(R.id.tvPopularReview); // Corrigé
            rating = itemView.findViewById(R.id.popularRating);
            ivUserProfile = itemView.findViewById(R.id.ivUserProfile); // Nouveau
        }
    }
}