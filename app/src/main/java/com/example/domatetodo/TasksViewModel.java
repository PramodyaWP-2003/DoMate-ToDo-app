package com.example.domatetodo;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.UUID;

public class TasksViewModel extends AndroidViewModel {
    private final AppRepository repository;
    private final LiveData<List<Task>> tasks;

    public TasksViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        tasks = repository.getAllTasks();
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public void addTask(String title) {
        Task task = new Task(UUID.randomUUID().toString(), title, false);
        repository.insertTask(task);
    }

    public void toggleTaskCompletion(String taskId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Task task = repository.getTaskById(taskId);
            if (task != null) {
                task.setCompleted(!task.isCompleted());
                repository.updateTask(task);
            }
        });
    }

    public void deleteTask(Task task) {
        repository.deleteTask(task);
    }

    public void editTask(String taskId, String newTitle) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Task task = repository.getTaskById(taskId);
            if (task != null) {
                task.setTitle(newTitle);
                repository.updateTask(task);
            }
        });
    }
}
