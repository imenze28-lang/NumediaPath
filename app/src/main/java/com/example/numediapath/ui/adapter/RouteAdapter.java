package com.example.numediapath.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.numediapath.R;
import com.example.numediapath.data.model.RoutePlan;
import java.util.ArrayList;
import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private List<RoutePlan> routes = new ArrayList<>();

    // Interface pour le clic
    public interface OnRouteClickListener {
        void onRouteClick(RoutePlan route);
    }

    private OnRouteClickListener listener;

    public void setOnRouteClickListener(OnRouteClickListener listener) {
        this.listener = listener;
    }

    public void setRoutes(List<RoutePlan> routes) {
        this.routes = routes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        RoutePlan currentRoute = routes.get(position);

        holder.tvName.setText(currentRoute.getName());
        holder.tvPrice.setText(currentRoute.getTotalCost() + " €");
        holder.tvType.setText(currentRoute.getType());
        holder.tvInfos.setText("⏱️ " + currentRoute.getTotalDuration() + " min • 👣 " + currentRoute.getTotalDistance() + " m");

        // Gestion de l'image principale avec Glide
        if (currentRoute.getImageUrl() != null && !currentRoute.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(currentRoute.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivThumb);
        }

        // --- CORRECTION MAJEURE ICI ---
        // On gère la visibilité de l'étoile TOUT DE SUITE (pas dans le clic)
        if (holder.ivFav != null) {
            holder.ivFav.setVisibility(currentRoute.isFavorite() ? View.VISIBLE : View.GONE);
        }

        // Gestion du Clic sur la carte
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRouteClick(currentRoute);
            }
        });
    }

    @Override
    public int getItemCount() { return routes.size(); }

    static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvInfos, tvType;
        ImageView ivThumb;
        ImageView ivFav; // Ajout de la référence ici pour être plus efficace

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_route_name);
            tvPrice = itemView.findViewById(R.id.tv_route_price);
            tvInfos = itemView.findViewById(R.id.tv_route_infos);
            tvType = itemView.findViewById(R.id.tv_route_type);
            ivThumb = itemView.findViewById(R.id.iv_route_thumb);
            ivFav = itemView.findViewById(R.id.iv_favorite_indicator); // Liaison ici
        }
    }
}