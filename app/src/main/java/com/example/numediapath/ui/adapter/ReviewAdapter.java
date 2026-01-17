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
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    // Petit modèle interne
    public static class Review {
        String author;
        String content;
        String rating;
        String avatarUrl;

        public Review(String author, String content, String rating, String avatarUrl) {
            this.author = author;
            this.content = content;
            this.rating = rating;
            this.avatarUrl = avatarUrl;
        }
    }

    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review r = reviews.get(position);
        holder.tvAuthor.setText(r.author);
        holder.tvContent.setText(r.content);
        holder.tvRating.setText("★ " + r.rating);

        // On utilise Glide pour charger l'avatar (cercle)
        if (r.avatarUrl != null) {
            Glide.with(holder.itemView.getContext())
                    .load(r.avatarUrl)
                    .circleCrop()
                    .into(holder.imgAvatar);
        }
    }

    @Override
    public int getItemCount() { return reviews.size(); }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvContent, tvRating;
        ImageView imgAvatar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_review_author);
            tvContent = itemView.findViewById(R.id.tv_review_content);
            tvRating = itemView.findViewById(R.id.tv_review_rating);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
        }
    }
}