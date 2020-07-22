package com.example.myapplication;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TaskDAO {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAll(List<Task> tasks);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks ORDER BY `order`")
    LiveData<List<Task>> getTasks();

    @Query("SELECT * FROM tasks WHERE id= :id LIMIT 1")
    Task get(int id);

    @Query("SELECT * FROM tasks WHERE dayofweek = :dayOfTask ORDER BY `order`")
    LiveData<List<Task>> getTasksForDay(int dayOfTask);

    @Query("SELECT MAX(`order`) FROM tasks")
    int getMax();
}

