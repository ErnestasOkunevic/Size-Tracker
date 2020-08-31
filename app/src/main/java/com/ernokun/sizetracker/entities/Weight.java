package com.ernokun.sizetracker.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weight_table")
public class Weight {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private double weight_kg;
    private double weight_lbs;


    /***
     * Dates are formatted like this:
     *      yyyy-mm-dd
     */
    private String date;


    public Weight(double weight_kg, double weight_lbs, String date) {
        this.weight_kg = weight_kg;
        this.weight_lbs = weight_lbs;
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double getWeight_kg() {
        return weight_kg;
    }

    public double getWeight_lbs() {
        return weight_lbs;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return date + " : (kg) " + getWeight_kg();
    }
}
