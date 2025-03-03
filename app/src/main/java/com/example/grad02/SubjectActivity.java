package com.example.grad02;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SubjectActivity extends AppCompatActivity {

    private TextView subjectText;
    private Button plusButton;
    private ImageButton backButton;
    private ImageButton categoryButton;
    private ListView listView;
    private ArrayList<String> gradesList;
    private ArrayAdapter<String> adapter;
    private List<Category> categoryList = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;
    private List<String> categoryNames = new ArrayList<>();
    private TextView totalGradeText;
    private double totalScore = 0;
    private double totalWeight = 0;
    private DatabaseHelper dbHelper;
    private int subjectId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        // Initialize views
        subjectText = findViewById(R.id.Subject);
        plusButton = findViewById(R.id.plus);
        backButton = findViewById(R.id.back);
        categoryButton = findViewById(R.id.category);
        listView = findViewById(R.id.listView);
        totalGradeText = findViewById(R.id.TotalGrade);
        dbHelper = new DatabaseHelper(this);

        // get subject name galing sa previous activity
        String subjectName = getIntent().getStringExtra("subjectsName");
        subjectText.setText(subjectName);

        gradesList = dbHelper.getGrades(subjectId);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gradesList);
        listView.setAdapter(adapter);

        int subjectId = getIntent().getIntExtra("subjectId", -1); // Get subject ID
        categoryNames.addAll(dbHelper.getCategories(subjectId));
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryNames);

        // buttons
        plusButton.setOnClickListener(v -> showAddGradeDialog());
        categoryButton.setOnClickListener(v -> showCategoryDialog());
        backButton.setOnClickListener(v -> onBackPressed());

        updateTotalGrade();
    }

    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Categories");

        View customLayout = getLayoutInflater().inflate(R.layout.dialog_weight, null);
        builder.setView(customLayout);

        LinearLayout categoryContainer = customLayout.findViewById(R.id.categoryContainer);
        EditText newCategoryName = customLayout.findViewById(R.id.newCategoryName);
        EditText newCategoryWeight = customLayout.findViewById(R.id.newCategoryWeight);
        Button addCategoryButton = customLayout.findViewById(R.id.addCategoryButton);

        updateCategoryList(categoryContainer);

        addCategoryButton.setOnClickListener(v -> {
            String name = newCategoryName.getText().toString().trim();
            String weight = newCategoryWeight.getText().toString().trim();

            if (!name.isEmpty() && !weight.isEmpty()) {
                if (dbHelper.addCategory(name, Double.parseDouble(weight), subjectId)) {
                    categoryNames.add(name);
                    categoryAdapter.notifyDataSetChanged();
                    updateCategoryList(categoryContainer);
                    newCategoryName.setText("");
                    newCategoryWeight.setText("");
                    Toast.makeText(this, "Category added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add category!", Toast.LENGTH_SHORT).show();
                }
                newCategoryName.setText("");
                newCategoryWeight.setText("");
            } else {
                Toast.makeText(this, "Enter both category name and weight", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Save", (dialog, which) ->
                Toast.makeText(this, "Categories Saved!", Toast.LENGTH_SHORT).show());

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateCategoryList(LinearLayout categoryContainer) {
        categoryContainer.removeAllViews();

        for (int i = 0; i < categoryList.size(); i++) {
            View categoryView = getLayoutInflater().inflate(R.layout.item_category, null);
            TextView categoryName = categoryView.findViewById(R.id.categoryName);
            TextView categoryWeight = categoryView.findViewById(R.id.categoryWeight);
            Button removeButton = categoryView.findViewById(R.id.removeCategory);
            Category category = categoryList.get(i);

            categoryName.setText(category.getName());
            categoryWeight.setText(category.getWeight() + "%");

            removeButton.setOnClickListener(v -> {
                if (dbHelper.deleteCategory(category.getName(), subjectId)) {
                    categoryNames.remove(category.getName());
                    categoryAdapter.notifyDataSetChanged();
                    updateCategoryList(categoryContainer);
                    Toast.makeText(this, "Category removed!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to remove category!", Toast.LENGTH_SHORT).show();
                }
            });

            categoryContainer.addView(categoryView);
        }
    }

    private void showAddGradeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Grade");

        View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_grade, null);
        builder.setView(customLayout);

        EditText titleInput = customLayout.findViewById(R.id.TitleName);
        EditText gradeInput = customLayout.findViewById(R.id.inputGrade);
        EditText itemsInput = customLayout.findViewById(R.id.inputItems);
        Spinner categorySpinner = customLayout.findViewById(R.id.weightcategory);
        categorySpinner.setAdapter(categoryAdapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = titleInput.getText().toString().trim();
            String gradeStr = gradeInput.getText().toString().trim();
            String itemsStr = itemsInput.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();

            if (!title.isEmpty() && !gradeStr.isEmpty() && !itemsStr.isEmpty()) {
                try {
                    double grade = Double.parseDouble(gradeStr);
                    double items = Double.parseDouble(itemsStr);

                    double weight = 0;
                    for (Category cat : categoryList) {
                        if (cat.getName().equals(category)) {
                            weight = Double.parseDouble(cat.getWeight());
                            break;
                        }
                    }

                    double percentage = (grade / items) * 100;
                    totalScore += percentage * (weight / 100);
                    totalWeight += (weight / 100);

                    if (dbHelper.addGrade(title, grade, items, subjectId, category)) {
                        gradesList.add(title + ": " + grade + "/" + items + " (" + category + ")");
                        adapter.notifyDataSetChanged();
                        updateTotalGrade();
                        Toast.makeText(this, "Grade added successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to add grade!", Toast.LENGTH_SHORT).show();
                    }


                    updateTotalGrade();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    static class Category {
        private String name;
        private String weight;

        public Category(String name, String weight) {
            this.name = name;
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public String getWeight() {
            return weight;
        }
    }

    private void updateTotalGrade() {
        if (totalWeight > 0) {
            double finalGrade = totalScore / totalWeight;
            totalGradeText.setText(String.format("%.2f%%", finalGrade));
            Log.d("UpdateGrade", "Total Grade Updated: " + finalGrade);
        } else {
            totalGradeText.setText("0.00%");
            Log.d("UpdateGrade", "Total Grade set to 0.00%");
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // closes activity and returns to previous activity
    }
}