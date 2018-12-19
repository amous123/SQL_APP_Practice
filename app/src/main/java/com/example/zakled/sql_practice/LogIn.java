package com.example.zakled.sql_practice;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void btnValidateLoginClick(View view) throws UnsupportedEncodingException {
        String username=((EditText) findViewById(R.id.username)).getText().toString();
        String password=((EditText) findViewById(R.id.password)).getText().toString();


        //search database for username entered ***HERE FEMALE
        MyDatabaseHelper myDBHelper = new MyDatabaseHelper(this);
        if (!myDBHelper.usernameExist(username)) {
            Context context = getApplicationContext();
            CharSequence incorrectPassword = "There is no account with that username in our records. Please try again!";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, incorrectPassword, duration);
            toast.show();

        }
        if (myDBHelper.usernameExist(username)) {
            String passwordFromDB = myDBHelper.findPassword(username);
            String tempPassword = Sha1.hash(password);
            if (!tempPassword.equals(passwordFromDB)) {
                Context context = getApplicationContext();
                CharSequence incorrectPassword = "Your password is incorrect!";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, incorrectPassword, duration);
                toast.show();


            }
            else {
                Intent i = new Intent(LogIn.this, LogsActivity.class);
                i.putExtra("username", username);
                startActivity(i);
            }

        }
    }
}