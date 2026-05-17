package com.example.domatetodo;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    public interface OnTaskActionListener {
        void onToggle(Task task);
        void onDelete(Task task);
        void onEdit(Task task);
    }

    private final OnTaskActionListener listener;

    public TaskAdapter(OnTaskActionListener listener) {
        super(new DiffUtil.ItemCallback<Task>() {
            @Override
            public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return oldItem.getTitle().equals(newItem.getTitle()) &&
                        oldItem.isCompleted() == newItem.isCompleted();
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCheckbox;
        private final TextView tvTitle;
        private final ImageView ivEdit;
        private final ImageView ivDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCheckbox = itemView.findViewById(R.id.iv_checkbox);
            tvTitle = itemView.findViewById(R.id.tv_task_title);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }

        public void bind(Task task, OnTaskActionListener listener) {
            tvTitle.setText(task.getTitle());
            
            if (task.isCompleted()) {
                ivCheckbox.setImageResource(R.drawable.ic_checkbox_checked);
                tvTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.task_text_completed));
                // Apply strikethrough
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                // Apply lower opacity (disabled state look)
                itemView.setAlpha(0.5f);
            } else {
                ivCheckbox.setImageResource(R.drawable.ic_checkbox_blank);
                tvTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.login_title));
                // Remove strikethrough
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                // Reset opacity
                itemView.setAlpha(1.0f);
            }

            ivCheckbox.setOnClickListener(v -> listener.onToggle(task));
            ivDelete.setOnClickListener(v -> listener.onDelete(task));
            ivEdit.setOnClickListener(v -> listener.onEdit(task));
        }
    }
}
