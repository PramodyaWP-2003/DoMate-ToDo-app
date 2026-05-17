package com.example.domatetodo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;

public class SignupActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private AppRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        
        repository = new AppRepository(getApplication());
        
        etUsername = findViewById(R.id.et_signup_username);
        etEmail = findViewById(R.id.et_signup_email);
        etPassword = findViewById(R.id.et_signup_password);
        etConfirmPassword = findViewById(R.id.et_signup_confirm_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int paddingInPx = (int) (24 * getResources().getDisplayMetrics().density);
            v.setPadding(systemBars.left + paddingInPx, systemBars.top + paddingInPx, systemBars.right + paddingInPx, systemBars.bottom + paddingInPx);
            return insets;
        });

        MaterialButton btnCreateAccount = findViewById(R.id.btn_create_account);
        btnCreateAccount.setOnClickListener(v -> handleSignup());

        TextView tvSignInLink = findViewById(R.id.tv_signin_link);
        tvSignInLink.setOnClickListener(v -> finish());
    }

    private void handleSignup() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert user into database
        User newUser = new User(username, email, password);
        repository.insertUser(newUser);

        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
        
        // Return to Login Screen
        finish();
    }
}