package com.example.grad02;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GradoDB";
    private static final int DATABASE_VERSION = 1;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Terms table
    private static final String TABLE_TERMS = "terms";
    private static final String COLUMN_TERM_ID = "term_id";
    private static final String COLUMN_TERM_NAME = "term_name";
    private static final String COLUMN_USER_ID = "user_id";

    // Subjects table
    private static final String TABLE_SUBJECTS = "subjects";
    private static final String COLUMN_SUBJECT_ID = "subject_id";
    private static final String COLUMN_SUBJECT_NAME = "subject_name";
    private static final String COLUMN_TERM_ID_FK = "term_id";

    // Grades table
    private static final String TABLE_GRADES = "grades";
    private static final String COLUMN_GRADE_ID = "grade_id";
    private static final String COLUMN_GRADE_SUBJECT_ID = "subject_id";
    private static final String COLUMN_GRADE_TITLE = "title";
    private static final String COLUMN_GRADE_SCORE = "grade";
    private static final String COLUMN_GRADE_ITEMS = "items";
    private static final String COLUMN_GRADE_CATEGORY = "category";

    // Categories table
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_CATEGORY_SUBJECT_ID = "subject_id";
    private static final String COLUMN_CATEGORY_NAME = "name";
    private static final String COLUMN_CATEGORY_WEIGHT = "weight";


    // Create table query
    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT"
            + ")";

    // Create terms table query
    private static final String CREATE_TERMS_TABLE = "CREATE TABLE " + TABLE_TERMS + "("
            + COLUMN_TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TERM_NAME + " TEXT,"
            + COLUMN_USER_ID + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    // Create subjects table query
    private static final String CREATE_SUBJECTS_TABLE = "CREATE TABLE " + TABLE_SUBJECTS + "("
            + COLUMN_SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_SUBJECT_NAME + " TEXT,"
            + COLUMN_TERM_ID_FK + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_TERM_ID_FK + ") REFERENCES " + TABLE_TERMS + "(" + COLUMN_TERM_ID + ")"
            + ")";

    // Create grades table query
    private static final String CREATE_GRADES_TABLE = "CREATE TABLE " + TABLE_GRADES + "("
            + COLUMN_GRADE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_GRADE_SUBJECT_ID + " INTEGER,"
            + COLUMN_GRADE_TITLE + " TEXT,"
            + COLUMN_GRADE_SCORE + " REAL,"
            + COLUMN_GRADE_ITEMS + " REAL,"
            + COLUMN_GRADE_CATEGORY + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_GRADE_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECTS + "(" + COLUMN_SUBJECT_ID + ")"
            + ")";

    // Create categories table query
    private static final String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
            + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CATEGORY_SUBJECT_ID + " INTEGER,"
            + COLUMN_CATEGORY_NAME + " TEXT,"
            + COLUMN_CATEGORY_WEIGHT + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_CATEGORY_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECTS + "(" + COLUMN_SUBJECT_ID + ")"
            + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_TERMS_TABLE);
        db.execSQL(CREATE_SUBJECTS_TABLE);
        db.execSQL(CREATE_GRADES_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add upgrade logic instead of dropping tables
        if (oldVersion < 2) {
            db.execSQL(CREATE_TERMS_TABLE);
            db.execSQL(CREATE_SUBJECTS_TABLE);
            db.execSQL(CREATE_GRADES_TABLE);
            db.execSQL(CREATE_CATEGORIES_TABLE);
        }
    }

    // ------ LOGIN PAGE ------
    // Add new user
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Check if user exists
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + "=?" + " AND " + COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Check if username exists
    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + "=?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // get user's id method
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }
        cursor.close();
        return -1; // Return -1 if user not found
    }

    // ------ MAIN PAGE ------
    // Add a new term
    public boolean addTerm(String termName, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TERM_NAME, termName);
        values.put(COLUMN_USER_ID, userId);

        long result = db.insert(TABLE_TERMS, null, values);
        db.close();
        return result != -1;  // Return true if insertion was successful
    }

    // Get all terms for a user
    public ArrayList<String> getTerms(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> termsList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_TERM_NAME + " FROM " + TABLE_TERMS +
                " WHERE " + COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                termsList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return termsList;
    }

    // Delete a term
    public boolean deleteTerm(String termName, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TERMS, COLUMN_TERM_NAME + "=? AND " + COLUMN_USER_ID + "=?",
                new String[]{termName, String.valueOf(userId)});
        db.close();
        return result > 0;
    }

    // Get termId by termName
    public int getTermIdByName(String termName, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int termId = -1; // Default value if not found

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_TERM_ID + " FROM " + TABLE_TERMS +
                        " WHERE " + COLUMN_TERM_NAME + "=? AND " + COLUMN_USER_ID + "=?",
                new String[]{termName, String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            termId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return termId;
    }


    // ------ TERM PAGE ------
    // Add a new subject
    public boolean addSubjects(String subjectName, int termId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBJECT_NAME, subjectName);
        values.put(COLUMN_TERM_ID_FK, termId);

        long result = db.insert(TABLE_SUBJECTS, null, values);
        db.close();
        return result != -1;
    }


    // Get all subjects for a term
    public ArrayList<String> getSubjects(int termId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> subjectsList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_SUBJECT_NAME + " FROM " + TABLE_SUBJECTS +
                " WHERE " + COLUMN_TERM_ID_FK + "=?", new String[]{String.valueOf(termId)});

        if (cursor.moveToFirst()) {
            do {
                subjectsList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return subjectsList;
    }

    // Delete a subject by subject name and termId
    public boolean deleteSubjects(String subjectName, int termId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_SUBJECTS, COLUMN_SUBJECT_NAME + "=? AND " + COLUMN_TERM_ID_FK + "=?",
                new String[]{subjectName, String.valueOf(termId)});
        db.close();
        return result > 0;
    }

    // Get subjectsId by subjectsName
    public int getSubjectsIdByName(String subjectsName, int termId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int subjectsId = -1; // Default value if not found

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_SUBJECT_ID + " FROM " + TABLE_SUBJECTS +
                        " WHERE " + COLUMN_SUBJECT_NAME + "=? AND " + COLUMN_TERM_ID_FK + "=?",
                new String[]{subjectsName, String.valueOf(termId)});

        if (cursor.moveToFirst()) {
            subjectsId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return subjectsId;
    }

// ------ SUBJECT PAGE ------
// ------ CATEGORIES ------
// Add a new category
    public boolean addCategory(String categoryName, double weight, int subjectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, categoryName);
        values.put(COLUMN_CATEGORY_WEIGHT, weight);
        values.put(COLUMN_CATEGORY_SUBJECT_ID, subjectId);

        long result = db.insert(TABLE_CATEGORIES, null, values);
        db.close();
        return result != -1;  // Return true if insertion was successful
    }

    // Get all categories for a subject
    public ArrayList<String> getCategories(int subjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> categoriesList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CATEGORY_NAME + " FROM " + TABLE_CATEGORIES +
                " WHERE " + COLUMN_CATEGORY_SUBJECT_ID + "=?", new String[]{String.valueOf(subjectId)});

        if (cursor.moveToFirst()) {
            do {
                categoriesList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categoriesList;
    }


    // Delete a category
    public boolean deleteCategory(String categoryName, int subjectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CATEGORIES, COLUMN_CATEGORY_NAME + "=? AND " + COLUMN_CATEGORY_SUBJECT_ID + "=?",
                new String[]{categoryName, String.valueOf(subjectId)});
        db.close();
        return result > 0;
    }


    // ------ GRADES ------
    // Add a new grade
    public boolean addGrade(String title, double grade, double items, int subjectId, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GRADE_TITLE, title);
        values.put(COLUMN_GRADE_SCORE, grade);
        values.put(COLUMN_GRADE_ITEMS, items);
        values.put(COLUMN_GRADE_SUBJECT_ID, subjectId);
        values.put(COLUMN_GRADE_CATEGORY, category);

        long result = db.insert(TABLE_GRADES, null, values);
        db.close();
        return result != -1;  // Return true if insertion was successful
    }

    // Get all grades for a subject
    public ArrayList<String> getGrades(int subjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> gradesList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_GRADE_TITLE + ", " + COLUMN_GRADE_SCORE + ", " +
                COLUMN_GRADE_ITEMS + ", " + COLUMN_GRADE_CATEGORY + " FROM " + TABLE_GRADES +
                " WHERE " + COLUMN_GRADE_SUBJECT_ID + "=?", new String[]{String.valueOf(subjectId)});

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(0);
                double grade = cursor.getDouble(1);
                double items = cursor.getDouble(2);
                String category = cursor.getString(3);
                gradesList.add(title + " (" + category + "): " + grade + "/" + items);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return gradesList;
    }


    // Delete a grade
    public boolean deleteGrade(String title, int subjectId, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_GRADES, COLUMN_GRADE_TITLE + "=? AND " + COLUMN_GRADE_SUBJECT_ID + "=? AND " +
                COLUMN_GRADE_CATEGORY + "=?", new String[]{title, String.valueOf(subjectId), category});
        db.close();
        return result > 0;
    }

}