package com.example.numediapath.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.numediapath.R;
import com.example.numediapath.data.model.RouteStep;
import java.util.List;

public class CheckableStepAdapter extends RecyclerView.Adapter<CheckableStepAdapter.ViewHolder> {

    private List<RouteStep> steps;

    public CheckableStepAdapter(List<RouteStep> steps) {
        this.steps = steps;
    }

    // Méthode pour récupérer la liste finale des lieux visités
    public List<RouteStep> getSteps() {
        return steps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_step_checkable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RouteStep step = steps.get(position);
        holder.tvTitle.setText(step.getTitle());
        holder.tvDesc.setText(step.getTime() + " - " + step.getDescription());

        // On évite que le recyclage coche/décoche n'importe quoi
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(step.isVisited());

        // Mise à jour du modèle quand on clique
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            step.setVisited(isChecked);
        });
    }

    @Override
    public int getItemCount() { return steps.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_step_visited);
            tvTitle = itemView.findViewById(R.id.tv_step_title);
            tvDesc = itemView.findViewById(R.id.tv_step_desc);
        }
    }
}