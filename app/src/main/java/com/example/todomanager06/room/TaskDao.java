package com.example.todomanager06.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.todomanager06.model.TaskModel;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(TaskModel taskModel);

    @Delete
    void delete(TaskModel model);

    @Query("SELECT * FROM task_model")
    LiveData<List<TaskModel>> getData();
}
