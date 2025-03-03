package com.example.grad02;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signupTextView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        usernameEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        loginButton = findViewById(R.id.login_button);
        signupTextView = findViewById(R.id.signup);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (validateInput(username, password)) {
                    boolean isValid = databaseHelper.checkUser(username, password);
                    if (isValid) {
                        int userId = databaseHelper.getUserId(username); // Retrieve user ID from the database
                        Intent intent = new Intent(MainActivity.this, MainPage.class);
                        intent.putExtra("userId", userId); // Pass user ID to MainPage
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // Signup text click listener
        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            usernameEditText.setError("Username cannot be empty");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            return false;
        }
        return true;
    }
}