package com.ernokun.sizetracker.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ernokun.sizetracker.entities.Weight;

import java.util.List;

@Dao
public interface WeightDao {

    @Insert
    void insert(Weight weight);

    @Update
    void update(Weight weight);

    @Delete
    void delete(Weight weight);

    @Query("SELECT * FROM weight_table ORDER BY date DESC")
    LiveData<List<Weight>> getAllWeights();
}
