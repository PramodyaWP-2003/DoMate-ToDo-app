package com.example.domatetodo;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class AppRepository {
    private final UserDao userDao;
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    // User operations
    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    public void insertUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.insertUser(user));
    }

    public void updateUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.updateUser(user));
    }

    public User login(String username, String password) {
        return userDao.login(username, password);
    }

    // Task operations
    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insertTask(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.insertTask(task));
    }

    public void updateTask(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.updateTask(task));
    }

    public void deleteTask(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.deleteTask(task));
    }

    public Task getTaskById(String taskId) {
        return taskDao.getTaskById(taskId);
    }
}
