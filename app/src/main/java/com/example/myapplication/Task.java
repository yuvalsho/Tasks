package com.example.myapplication;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;

@Entity (tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="id")
    @NonNull
    protected int id;
    @ColumnInfo(name="dayOfWeek")
    private int dayOfWeek;
    @ColumnInfo(name = "header")
    protected String task;
    @ColumnInfo(name = "order")
    private int order;
    String description;
    ArrayList<String> subTask;

    public  Task(String task,int dayOfWeek,int order)
    {
        this.order=order+2;
        this.task=task;
        this.dayOfWeek=dayOfWeek;
        subTask=new ArrayList<>();
        description="";
    }

    public void setOrder(int order) {
        this.order = order;
    }
    public int getOrder() {
        return order;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTask() {
        return task;
    }
    public void setTask(String task) {
        this.task = task;
    }
    public int getId(){
        return id;
    }

    public String getDescription() {
        if (description==null)
            description="";
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }

    int getDayOfWeek(){
        return dayOfWeek;
    }
    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    public String toString(){
        return task;
    }
}