package com.example.grad02;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signupButton;
    private TextView loginTextView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // Initialize views
        usernameEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edittext);
        signupButton = findViewById(R.id.signup_button);
        loginTextView = findViewById(R.id.login);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Signup button click listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (validateInput(username, password, confirmPassword)) {
                    if (databaseHelper.checkUsername(username)) {
                        Toast.makeText(SignupActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isSuccess = databaseHelper.addUser(username, password);
                        if (isSuccess) {
                            Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(SignupActivity.this, "Signup failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        // Login text click listener
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean validateInput(String username, String password, String confirmPassword) {
        if (username.isEmpty()) {
            usernameEditText.setError("Username cannot be empty");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            return false;
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Confirm password cannot be empty");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }
        return true;
    }
}