package com.example.domatetodo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ProfileViewModel extends AndroidViewModel {
    private final AppRepository repository;
    private final LiveData<User> userProfile;
    private final int userId;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        
        SharedPreferences prefs = application.getSharedPreferences("DoMatePrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        
        userProfile = repository.getUserById(userId);
    }

    public LiveData<User> getUserProfile() {
        return userProfile;
    }

    public void updateProfile(String newUsername, String newEmail) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User current = userProfile.getValue();
            if (current != null) {
                current.setUsername(newUsername);
                current.setEmail(newEmail);
                repository.updateUser(current);
                
                // Also update stored username if it's used elsewhere
                SharedPreferences prefs = getApplication().getSharedPreferences("DoMatePrefs", Context.MODE_PRIVATE);
                prefs.edit().putString("username", newUsername).apply();
            }
        });
    }
}
