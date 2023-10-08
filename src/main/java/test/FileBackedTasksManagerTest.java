package test;

import manager.FileBackedTasksManager;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;

import java.io.File;
import java.io.IOException;


public class FileBackedTasksManagerTest {
    public static void main(String[] args) throws IOException {
        TaskManager fileManager = new FileBackedTasksManager(new File("src/main/resources/save.cvs"));
        Task task = fileManager.saveTask(new Task("Task1", "Description"));
        Task task1 = fileManager.saveTask(new Task("Task2", "Description2"));
        Epic epic = fileManager.saveEpic(new Epic("Epic", "EpicDescription"));
        Epic epic1 = fileManager.saveEpic(new Epic("Epic2", "EpicDescription2"));
        Subtask subtask = fileManager.saveSubtask(
                new Subtask("Subtask", "subtaskDescription", epic.getId()));

        fileManager.getTask(task.getId());
        fileManager.getEpic(epic1.getId());

        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.
                loadFromFile(new File("src/main/resources/save.cvs"));
        System.out.println(fileBackedTasksManager.getHistory());

    }
}
