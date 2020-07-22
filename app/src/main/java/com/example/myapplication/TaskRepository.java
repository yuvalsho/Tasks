package com.example.myapplication;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import java.util.List;
import java.util.concurrent.ExecutionException;

class TaskRepository {
    private AppDatabase appDatabase;
    int max=0;
    Task task;

        TaskRepository(Context context) {
            String DB_NAME = "AppDb";
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).fallbackToDestructiveMigration().build();
        }
        void insert(Task task){
            insertTask(task);
        }
        void update(Task task){
            updateTask(task);
        }
        void updateAll(List<Task> tasks){
            updateAllTasks(tasks);
        }
        void delete(Task task){
            deleteTask(task);
        }
        Task get(int id) throws ExecutionException, InterruptedException {return getTask(id); }

        LiveData<List<Task>> DailyTasks(int day) {
            return getDaily(day);
        }
        int getMax() throws ExecutionException, InterruptedException {
            return getMaximum();
        }

        @SuppressLint("StaticFieldLeak")
        private void insertTask(final Task task) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    appDatabase.taskDAO().insert(task);
                    return null;
                }
            }.execute();
        }

        @SuppressLint("StaticFieldLeak")
        private void updateTask(final Task task) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    appDatabase.taskDAO().update(task);
                    return null;
                }
            }.execute();
        }
    @SuppressLint("StaticFieldLeak")
    private void updateAllTasks(final List<Task> tasks){
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    appDatabase.taskDAO().updateAll(tasks);
                    return null;
                }
            }.execute();
        }

        @SuppressLint("StaticFieldLeak")
        private void deleteTask(final Task task) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    appDatabase.taskDAO().delete(task);
                    return null;
                }
            }.execute();
        }
        private LiveData<List<Task>> getDaily(int day){return appDatabase.taskDAO().getTasksForDay(day);}

        @SuppressLint("StaticFieldLeak")
        private Task getTask(final int id) throws ExecutionException, InterruptedException {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    task=appDatabase.taskDAO().get(id);
                    return null;
                }
            }.execute().get();
            return task;
        }

        @SuppressLint("StaticFieldLeak")
        private int getMaximum() throws ExecutionException, InterruptedException {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    max=appDatabase.taskDAO().getMax();
                    return null;
                }
            }.execute().get();
            return max;
        }
}
