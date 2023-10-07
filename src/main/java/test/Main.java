package test;

import manager.*;
import models.Epic;
import models.Subtask;
import models.Task;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

//        System.out.println("Создание эпика");
//        Epic epic = new Epic("Эпик без подзадач", "Эпик без подзадач");
//        taskManager.saveEpic(epic);

//        Epic epic2 = new Epic("Эпик с подзадачами", "Эпик с подзадачами");
//        taskManager.saveEpic(epic2);

//        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск epic2", epic2.getId());
//        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск epic2", epic2.getId());
//        Subtask subtask3 = new Subtask("Сабтаск 3", "Сабтаск epic2", epic2.getId());

//        taskManager.saveSubtask(subtask);
//        taskManager.saveSubtask(subtask2);
//        taskManager.saveSubtask(subtask3);
//        Task task = new Task("Таск1", "Тестовый таск");
//        taskManager.saveTask(task);
//        System.out.println();
//
//
//        System.out.println("Получение истории");
//        taskManager.getEpic(epic2.getId());
//        taskManager.getEpic(epic2.getId());
//        taskManager.getEpic(epic2.getId());
//        taskManager.getTask(task.getId());
//        taskManager.getSubtask(subtask.getId());
//        System.out.println(taskManager.getHistory());

//       taskManager.deleteTask(task.getId());
//        System.out.println(taskManager.getHistory());
//
//        taskManager.getSubtask(subtask.getId());
//        System.out.println(taskManager.getHistory());
//
//       taskManager.deleteSubtask(subtask.getId());
//        System.out.println(taskManager.getHistory());
//
//        taskManager.deleteEpic(epic2.getId());
//        System.out.println("Преобразуем таску в строку");
//        String taskStr = CSVFormatter.toString(task);
//        System.out.println(taskStr);
//        String subtaskStr = CSVFormatter.toString(subtask);
//        System.out.println(subtaskStr);
//        String epicStr = CSVFormatter.toString(epic);
//        System.out.println(epicStr);
//        System.out.println();
//
//        System.out.println("Получаем таску из строки");
//        System.out.println(CSVFormatter.fromString(taskStr));
//        System.out.println(CSVFormatter.fromString(subtaskStr));
//        System.out.println(CSVFormatter.fromString(epicStr));
//        System.out.println();
//
//        System.out.println("Пребразуем историю в строку");
//        System.out.println(CSVFormatter.historyToString(historyManager));
//        System.out.println();
//
//        System.out.println("Получаем лист Integer из строки");
//        System.out.println(CSVFormatter.historyFromString("1,2,13,45,56,-4"));

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(new File(""));
        Task task = fileBackedTasksManager.saveTask(new Task("Таск", "Тестовый таск"));
        Task task1 = fileBackedTasksManager.saveTask(new Task("Таск1", "Тестовый таск1"));

        System.out.println(task);
        System.out.println(task1);

    }
}
