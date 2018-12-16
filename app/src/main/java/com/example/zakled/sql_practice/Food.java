package com.example.zakled.sql_practice;

public class Food {
    private String name;
    private int calories;

    public Food(String name, int calories){
        this.name = name;
        this.calories = calories;
    }

    public int getCalories() {
        return calories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}
