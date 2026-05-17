package com.example.domatetodo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private AppRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        
        repository = new AppRepository(getApplication());
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int p = (int) (24 * getResources().getDisplayMetrics().density);
            v.setPadding(systemBars.left + p, systemBars.top + p, systemBars.right + p, systemBars.bottom + p);
            return insets;
        });

        MaterialButton btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(v -> handleLogin());

        TextView tvSignUp = findViewById(R.id.tv_signup);
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        TextView tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvForgotPassword.setOnClickListener(v -> showResetPasswordDialog());
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = repository.login(username, password);
            runOnUiThread(() -> {
                if (user != null) {
                    SharedPreferences prefs = getSharedPreferences("DoMatePrefs", MODE_PRIVATE);
                    prefs.edit()
                            .putBoolean("isLoggedIn", true)
                            .putInt("userId", user.getId())
                            .putString("username", user.getUsername())
                            .apply();

                    Intent intent = new Intent(LoginActivity.this, TasksActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showResetPasswordDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reset_password, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        EditText etResetUsername = dialogView.findViewById(R.id.et_reset_username);
        EditText etNewPassword = dialogView.findViewById(R.id.et_reset_password);

        dialogView.findViewById(R.id.btn_reset_cancel).setOnClickListener(v -> dialog.dismiss());
        
        dialogView.findViewById(R.id.btn_reset_confirm).setOnClickListener(v -> {
            String username = etResetUsername.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            
            if (!username.isEmpty() && !newPassword.isEmpty()) {
                Toast.makeText(this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}