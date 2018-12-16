package com.example.zakled.sql_practice;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LogsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        MyDatabaseHelper myDBHelper = new MyDatabaseHelper(this);



        final ArrayList<Food> itemsList = myDBHelper.getFoods();

        //you can add service like this from db
        //LOAD all FROM DB
        ListView listView = findViewById(R.id.list);
        LogsAdapter adapter = new LogsAdapter(this,itemsList);
        listView.setAdapter(adapter);
        listView.setClickable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), AddOrDeleteFood.class);
                intent.putExtra("name", itemsList.get(i).getName());
                intent.putExtra("calories", itemsList.get(i).getCalories());
                startActivity(intent);

            }
        });


    }

//    public void btnCreateFoodClick(View view) {
//
//        Intent i = new Intent(LogsActivity.this, AdminCreateService.class);
//        startActivity(i);
//    }
    public void btnLogoutClick(View view) {
        Intent i = new Intent(LogsActivity.this, MainActivity.class);
        startActivity(i);
    }




}

