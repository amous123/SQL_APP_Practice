package com.example.zakled.sql_practice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class LogsAdapter extends ArrayAdapter {
    private final Context context;
    private final ArrayList<Food> myFoods;

    public LogsAdapter(Context context, ArrayList<Food> myFoods){
        super(context, R.layout.food_item_layout, myFoods);
        this.context = context;
        this.myFoods = myFoods;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.food_item_layout, parent, false);

        TextView foodName = (TextView) rowView.findViewById(R.id.foodName);
        TextView caloriesNumber = (TextView) rowView.findViewById(R.id.caloriesNumber);

        Food a = myFoods.get(position);
        foodName.setText(a.getName());
        caloriesNumber.setText(String.valueOf(myFoods.get(position).getCalories()));
        return rowView;

    }
}
