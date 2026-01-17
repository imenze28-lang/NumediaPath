package com.example.numediapath.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.numediapath.R;
import com.example.numediapath.data.model.RouteStep;
import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    private List<RouteStep> steps;

    public StepAdapter(List<RouteStep> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        RouteStep step = steps.get(position);
        holder.tvTime.setText(step.getTime());
        holder.tvTitle.setText(step.getTitle());
        holder.tvDesc.setText(step.getDescription());

        // Cache la ligne verticale pour le dernier élément pour faire joli
        if (position == steps.size() - 1) {
            holder.line.setVisibility(View.INVISIBLE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() { return steps.size(); }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvTitle, tvDesc;
        View line;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_step_time);
            tvTitle = itemView.findViewById(R.id.tv_step_title);
            tvDesc = itemView.findViewById(R.id.tv_step_desc);
            line = itemView.findViewById(R.id.view_line);
        }
    }
}