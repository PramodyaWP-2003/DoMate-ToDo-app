package com.example.domatetodo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TasksActivity extends AppCompatActivity {

    private TasksViewModel viewModel;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tasks);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tasks_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(TasksViewModel.class);
        
        setupRecyclerView();

        // FAB - Add New Task
        FloatingActionButton fabAddTask = findViewById(R.id.fab_add_task);
        fabAddTask.setOnClickListener(v -> showTaskDialog(null));

        // Bottom Navigation
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        findViewById(R.id.nav_dev_info).setOnClickListener(v -> {
            startActivity(new Intent(this, DevInfoActivity.class));
        });

        // Observe tasks
        viewModel.getTasks().observe(this, tasks -> {
            adapter.submitList(tasks);
        });
    }

    private void setupRecyclerView() {
        RecyclerView rvTasks = findViewById(R.id.rv_tasks);
        adapter = new TaskAdapter(new TaskAdapter.OnTaskActionListener() {
            @Override
            public void onToggle(Task task) {
                viewModel.toggleTaskCompletion(task.getId());
            }

            @Override
            public void onDelete(Task task) {
                viewModel.deleteTask(task);
            }

            @Override
            public void onEdit(Task task) {
                showTaskDialog(task);
            }
        });
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(adapter);
    }

    private void showTaskDialog(Task taskToEdit) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        EditText etTitle = dialogView.findViewById(R.id.et_task_title);
        
        if (taskToEdit != null) {
            tvTitle.setText("Edit Task");
            etTitle.setText(taskToEdit.getTitle());
        } else {
            tvTitle.setText("Add New Task");
        }

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btn_ok).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                if (taskToEdit != null) {
                    viewModel.editTask(taskToEdit.getId(), title);
                } else {
                    viewModel.addTask(title);
                }
                dialog.dismiss();
            } else {
                etTitle.setError("Title cannot be empty");
            }
        });

        dialog.show();
    }
}
