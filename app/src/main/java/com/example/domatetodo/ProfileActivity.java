package com.example.domatetodo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    private ProfileViewModel viewModel;
    private TextView tvUsername, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        
        tvUsername = findViewById(R.id.tv_profile_username);
        tvEmail = findViewById(R.id.tv_profile_email);

        // Fetch and observe user profile
        viewModel.getUserProfile().observe(this, profile -> {
            if (profile != null) {
                tvUsername.setText(profile.getUsername());
                tvEmail.setText(profile.getEmail());
            }
        });

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // Edit Info Button
        MaterialButton btnEditInfo = findViewById(R.id.btn_edit_info);
        btnEditInfo.setOnClickListener(v -> showEditProfileDialog());

        // Sign Out Button
        MaterialButton btnSignOut = findViewById(R.id.btn_sign_out);
        btnSignOut.setOnClickListener(v -> showSignOutDialog());
    }

    private void showEditProfileDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        EditText etUsername = dialogView.findViewById(R.id.et_edit_username);
        EditText etEmail = dialogView.findViewById(R.id.et_edit_email);

        // Pre-fill dialog with current profile data
        User currentProfile = viewModel.getUserProfile().getValue();
        if (currentProfile != null) {
            etUsername.setText(currentProfile.getUsername());
            etEmail.setText(currentProfile.getEmail());
        }

        dialogView.findViewById(R.id.btn_edit_cancel).setOnClickListener(v -> dialog.dismiss());
        
        dialogView.findViewById(R.id.btn_edit_save).setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            
            if (!newUsername.isEmpty() && !newEmail.isEmpty()) {
                viewModel.updateProfile(newUsername, newEmail);
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void showSignOutDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_sign_out, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogView.findViewById(R.id.btn_sign_out_cancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btn_sign_out_confirm).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("DoMatePrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("isLoggedIn", false).apply();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }
}