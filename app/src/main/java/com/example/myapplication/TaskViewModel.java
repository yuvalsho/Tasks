package com.example.myapplication;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TaskViewModel extends AndroidViewModel {
        private TaskRepository taskRepository;

        public TaskViewModel (Application application) {
            super(application);
            taskRepository = new TaskRepository(application);
        }
        LiveData<List<Task>> getDaily(int day){
            return taskRepository.DailyTasks(day);
        }

        Task get(int id) throws ExecutionException, InterruptedException {
            return taskRepository.get(id);
        }

        void delete(Task task){
            taskRepository.delete(task);
        }

        public void update(Task task){
            taskRepository.update(task);
        }

        public void updateAll(List<Task> tasks){
            taskRepository.updateAll(tasks);
        }

        void insert(Task task) { taskRepository.insert(task); }

        public int getMax() throws ExecutionException, InterruptedException {
           return taskRepository.getMax();
        }
}
