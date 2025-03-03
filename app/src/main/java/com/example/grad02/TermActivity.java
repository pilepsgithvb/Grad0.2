package com.example.grad02;

// FIND ERROR (I THINK GALING SA INTENT COZ OF USING "SUBJECTS" INSTEAD OF "SUBJECT" WITH NO S

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class TermActivity extends AppCompatActivity {
    private TextView termNumText;
    private Button plusButton;
    private ImageButton backButton;
    private LinearLayout subjectsLayout;
    private DatabaseHelper databaseHelper;
    private int userId;
    private int termId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term); // Ensure correct XML layout is used

        userId = getIntent().getIntExtra("userId", -1);
        termId = getIntent().getIntExtra("termId", -1);
        databaseHelper = new DatabaseHelper(this);

        // Initialize UI elements
        termNumText = findViewById(R.id.TermNum);
        plusButton = findViewById(R.id.plus);
        backButton = findViewById(R.id.back);
        subjectsLayout = findViewById(R.id.subjectsLayout);

        // Get term name from intent
        String termName = getIntent().getStringExtra("termName");
        termNumText.setText(termName);

        // Load subjects
        loadSubjects();

        // Plus button click listener
        plusButton.setOnClickListener(v -> showAddSubjectsDialog());

        // Back button functionality
        backButton.setOnClickListener(v -> finish());
    }

    private void loadSubjects() {
        subjectsLayout.removeAllViews(); // Clear previous views
        List<String> subjectsList = databaseHelper.getSubjects(termId);

        for (String subject : subjectsList) {
            addSubjectsView(subject);
        }
    }

    // Adds subject view with buttons for opening and deleting
    private void addSubjectsView(String subject) {
        LinearLayout subjectContainer = new LinearLayout(this);
        subjectContainer.setOrientation(LinearLayout.HORIZONTAL);
        subjectContainer.setPadding(10, 10, 10, 10);

        TextView subjectsText = new TextView(this);
        subjectsText.setText(subject);
        subjectsText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));

        Button openButton = new Button(this);
        openButton.setText("Open");
        openButton.setOnClickListener(v -> {
            Intent intent = new Intent(TermActivity.this, SubjectActivity.class);
            int subjectsId = databaseHelper.getSubjectsIdByName(subject, termId);
            intent.putExtra("subjectId", subjectsId);
            intent.putExtra("subjectName", subject);
            startActivity(intent);
        });

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(TermActivity.this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this subject?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        databaseHelper.deleteSubjects(subject, termId);
                        loadSubjects(); // Refresh list
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        subjectContainer.addView(subjectsText);
        subjectContainer.addView(openButton);
        subjectContainer.addView(deleteButton);
        subjectsLayout.addView(subjectContainer);
    }

    // Dialog for adding a new subject
    private void showAddSubjectsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText input = new EditText(this);

        builder.setTitle("Add Subject")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String subjectsName = input.getText().toString().trim();
                    if (!subjectsName.isEmpty()) {
                        databaseHelper.addSubjects(subjectsName, termId);
                        loadSubjects(); // Refresh list
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // closes activity and returns to previous activity
    }
}
