package com.example.zakled.sql_practice;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.DropBoxManager;
import android.os.strictmode.SqliteObjectLeakedViolation;


import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "logsDB.db";

    public static final String TABLE_NAME = "ApplicationTable";
    public static final String COLUMN_PRIMARY_KEY_APP = "_id";
    public static final String COLUMN_USERNAME = "Username";
    public static final String COLUMN_PASSWORD = "Password";

    public static final String FOOD_TABLE = "FoodLogs";
    public static final String COLUMN_PRIMARY_KEY_FOOD_LOGS = "_id";
    public static final String COLUMN_FOOD_NAME = "Food";
    public static final String COLUMN_FOOD_CALORIES = "Calories";

    public static final String FOOD_LOG = "FoodLog";
    public static final String COLUMN_PRIMARY_KEY_FOOD_LOG = "_id";
    public static final String COLUMN_FOODS = "Foods";


    public MyDatabaseHelper (Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_APPLICATION_TABLE = "CREATE TABLE " +
                TABLE_NAME + "(" +
                COLUMN_PRIMARY_KEY_APP +
                " INTEGER PRIMARY KEY," + COLUMN_USERNAME +
                " TEXT," + COLUMN_PASSWORD +
                " TEXT " +
                ")";

        db.execSQL(CREATE_USER_APPLICATION_TABLE);


        String CREATE_FOOD_LOGS_TABLE = "CREATE TABLE " +
                FOOD_TABLE + "(" +
                COLUMN_PRIMARY_KEY_FOOD_LOGS +
                "INTEGER PRIMARY KEY, " +
                COLUMN_FOOD_NAME + " TEXT, " +
                COLUMN_FOOD_CALORIES + " REAL " +
                ")";

        db.execSQL(CREATE_FOOD_LOGS_TABLE);


        String CREATE_FOOD_LOG_TABLE = "CREATE TABLE " +
                FOOD_LOG + "( " +
                COLUMN_PRIMARY_KEY_FOOD_LOG + " INTEGER PRIMARY KEY, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_FOODS + " BLOB, " +
                " FOREIGN KEY(" + COLUMN_USERNAME + ")" +
                " REFERENCES " + TABLE_NAME + "(" + COLUMN_PRIMARY_KEY_APP + ")" +
                ")";

        db.execSQL(CREATE_FOOD_LOG_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FOOD_TABLE);
        onCreate(db);

    }


    /**
     * Method to search through db in order to find password associated to account
     * for login credential validation
     *
     * @param username username to find password for
     * @return String representing password saved in db
     */
    public String findPassword(String username) {
        SQLiteDatabase db =  this.getReadableDatabase();
        String query = "SELECT " + COLUMN_PASSWORD  + " FROM "  +
                TABLE_NAME + " WHERE " + COLUMN_USERNAME
                + " = \"" + username + "\"";

        Cursor cursor = db.rawQuery(query, null);
        String passwordSaved = "";
        if (cursor.moveToFirst()) {
            passwordSaved = cursor.getString(0);
            cursor.close();
        }
        db.close();
        return passwordSaved;

    }

    /**
     * Searches through db to make sure that the new username is not already in use
     *
     * @param username: value we wish to search in db
     * @return true if the username exists and false otherwise
     */
    public boolean usernameExist(String username) {
        boolean result = false;
        SQLiteDatabase db =  this.getReadableDatabase();

        String query = "Select * FROM "  +
                TABLE_NAME + " WHERE " + COLUMN_USERNAME
                + " = \"" + username + "\"";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            result = true;
        }
        cursor.close();
        db.close();
        return result;

    }

    /**
     * Inserting user account information in the database
     *
     * @param username username of account created
     * @param password password for account login validation
     *                    1 represents an Admin account,
     *                    2 represents a HomeOwner and
     *                    3 represents a ServiceProvider
     *
     *                    NOTE: Will most likely either create a Enum type or add a new table
     *                    in the database for references used
     */
    public void addUserAccount(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            password = Sha1.hash(password);
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_PASSWORD, password);

            db.insert(TABLE_NAME, null, values);
        } catch (UnsupportedEncodingException e) {
            db.close();
        }
    }

    public void addFood (String foodName, int caloriesNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_FOOD_NAME, foodName);
        values.put(COLUMN_FOOD_CALORIES, caloriesNumber);

        db.insert(FOOD_TABLE, null, values);
        db.close();
    }

    public void removeService(String foodName, int caloriesNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT " + COLUMN_FOOD_CALORIES +  " FROM " + FOOD_TABLE +
                " WHERE " + COLUMN_FOOD_NAME + " = \"" + foodName + "\"" +
                " AND " + COLUMN_FOOD_CALORIES + " = " + caloriesNumber;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            db.delete(FOOD_TABLE, COLUMN_FOOD_NAME + " =? AND " + COLUMN_FOOD_CALORIES + " = ?" , new String[]{foodName, String.valueOf(caloriesNumber)} );
        }
        cursor.close();
        db.close();

    }

    public void updateFoodInfo(String oldFoodName, int oldCaloriesNumber, String newFoodName, int newCaloriesNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put(COLUMN_FOOD_NAME, newFoodName);
        values.put(COLUMN_FOOD_CALORIES, newCaloriesNumber);

        db.update(FOOD_TABLE, values, COLUMN_FOOD_NAME + " =? AND " + COLUMN_FOOD_CALORIES + " = ?" , new String[]{oldFoodName, String.valueOf(oldCaloriesNumber)});

        db.close();
    }

    public ArrayList<Food> getFoods() {
        ArrayList<Food> foods = new ArrayList<Food>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_FOOD_NAME + ", " + COLUMN_FOOD_CALORIES +
                " FROM " + FOOD_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String food = cursor.getString(0);
                int calories = Integer.parseInt(cursor.getString(1));
                Food aFood = new Food(food, calories);
                foods.add(aFood);
            } while (cursor.moveToNext());
        }

        return foods;

    }

    @SuppressWarnings("unchecked")
    public ArrayList<Food> getFoods(String username) {
        ArrayList<Food> services = new ArrayList<Food>();
        ArrayList<Food> tmp;


        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_FOODS + " FROM " + FOOD_TABLE +
                " WHERE " + COLUMN_USERNAME
                + " = \"" + username + "\"";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            byte[] value = cursor.getBlob(0);
            if (value != null) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(value));
                    try {
                        services = (ArrayList<Food>) ois.readObject();
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }
        }
        cursor.close();
        db.close();
        return services;
    }

    public boolean addFood(String username, Food food, Context c) {
        ArrayList<Food> foods = this.getFoods(username);
        foods.add(food);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try{

            // Serialize data object to a file
            File f = new File( c.getFilesDir(), "foods.ser" );

            f.createNewFile();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f, true));

            out.writeObject(foods);
            out.close();

            // Serialize data object to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
            out = new ObjectOutputStream(bos) ;
            out.writeObject(foods);
            out.close();

            // Get the bytes of the serialized object
            byte[] data = bos.toByteArray();
            values.put(COLUMN_FOODS, data);

            db.update(FOOD_LOG, values, COLUMN_USERNAME + "=?", new String[]{username});
        } catch (IOException e) {
            return false;
        }

        db.close();
        return true;
    }

    public boolean removeFood(String username, Food food) {
        ArrayList<Food> foods = this.getFoods(username);
        boolean val = false;
        for (int i = 0; i < foods.size(); i ++){
            if (foods.get(i).getName().equals(food.getName()) && foods.get(i).getCalories() == food.getCalories() && !val){
                foods.remove(i);
                val = true;
            }
        }


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        byte[] data = SerializationUtils.serialize(foods);

        values.put(COLUMN_FOODS, data);

        db.update(FOOD_LOG, values, COLUMN_USERNAME + "=?", new String[]{username});

        db.close();

        return val;
    }


}
