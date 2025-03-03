package com.example.grad02;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainPage extends AppCompatActivity {
    private TextView usernameText;
    private Button plusButton;
    private LinearLayout termsLayout;
    private DatabaseHelper databaseHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        userId = getIntent().getIntExtra("userId", -1);
        databaseHelper = new DatabaseHelper(this);

        // Initialize UI elements
        usernameText = findViewById(R.id.username);
        plusButton = findViewById(R.id.plus);
        termsLayout = findViewById(R.id.termsLayout);

        // Set username
        String username = getIntent().getStringExtra("username");
        usernameText.setText("Hello, " + username);

        // Load terms
        loadTerms();

        // Plus button click listener
        plusButton.setOnClickListener(v -> showAddTermDialog());
    }

    private void loadTerms() {
        termsLayout.removeAllViews(); // Clear previous views
        List<String> termList = databaseHelper.getTerms(userId);

        for (String term : termList) {
            addTermView(term);
        }
    }

    // buttons for opening and deleting a term
    private void addTermView(String term) {
        LinearLayout termContainer = new LinearLayout(this);
        termContainer.setOrientation(LinearLayout.HORIZONTAL);
        termContainer.setPadding(10, 10, 10, 10);

        TextView termText = new TextView(this);
        termText.setText(term);
        termText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));

        Button openButton = new Button(this);
        openButton.setText("Open");
        openButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainPage.this, TermActivity.class);
            int termId = databaseHelper.getTermIdByName(term, userId);
            intent.putExtra("termId", termId);
            intent.putExtra("termName", term);
            startActivity(intent);
        });

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(MainPage.this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this term?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        databaseHelper.deleteTerm(term, userId);
                        loadTerms(); // Refresh list
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        termContainer.addView(termText);
        termContainer.addView(openButton);
        termContainer.addView(deleteButton);
        termsLayout.addView(termContainer);
    }

    // add term
    private void showAddTermDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText input = new EditText(this);

        builder.setTitle("Add Term")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String termName = input.getText().toString().trim();
                    if (!termName.isEmpty()) {
                        databaseHelper.addTerm(termName, userId);
                        loadTerms(); // Refresh list
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }
}
