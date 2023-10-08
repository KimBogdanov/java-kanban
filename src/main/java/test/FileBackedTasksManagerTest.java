package test;

import manager.FileBackedTasksManager;
import models.Task;

import java.io.File;
import java.io.IOException;


public class FileBackedTasksManagerTest {
    public static void main(String[] args) throws IOException {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(new File("src/main/resources/save.cvs"));
        Task task1 = fileManager.saveTask(new Task("11", "описание1"));
        Task task2 = fileManager.saveTask(new Task("22", "описание2"));
        Task task3 = fileManager.saveTask(new Task("33", "описание3"));
        Task task4 = fileManager.saveTask(new Task("44", "описание4"));
        Task task5 = fileManager.saveTask(new Task("55", "описание5"));
        Task task6 = fileManager.saveTask(new Task("66", "описание6"));

        fileManager.getTask(task1.getId());
        fileManager.getTask(task2.getId());
        fileManager.getTask(task3.getId());
        fileManager.getTask(task4.getId());
        fileManager.getTask(task5.getId());
        fileManager.getTask(task1.getId());
        Task task7 = fileManager.saveTask(new Task("66", "1"));
        fileManager.getHistoryManager().getHistory().forEach(System.out::println);
    }
}
