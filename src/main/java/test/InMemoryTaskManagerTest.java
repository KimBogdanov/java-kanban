package test;

import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;

public class InMemoryTaskManagerTest {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        System.out.println("Сохранение тасок!");
        Task task = taskManager.saveTask(new Task("Таск1", "Тестовый таск"));
        Task task2 = taskManager.saveTask(new Task("Таск2", "Тестовый таск2"));
        Task task3 = taskManager.saveTask(new Task("Таск3", "Тестовый таск3"));
        Task task4 = taskManager.saveTask(new Task("Таск4", "Тестовый таск4"));
        System.out.println();

        System.out.println("Получение тасков!");
        System.out.println(taskManager.getTask(task.getId()));
        System.out.println(taskManager.getTask(task2.getId()));
        System.out.println(taskManager.getTask(task4.getId()));

        System.out.println("Проверка в истории");
        System.out.println(taskManager.getHistory());
        System.out.println();

        System.out.println("Удаление таски по id");
        System.out.println("Удаляем таску с id: " + task.getId());
        taskManager.deleteTask(task.getId());
        System.out.println(taskManager.getAllTasks());
        System.out.println("Удаляем таску с id: " + task2.getId());
        taskManager.deleteTask(task2.getId());
        System.out.println(taskManager.getAllTasks());
        System.out.println("Удаляем таску с id: " + task3.getId());
        taskManager.deleteTask(task3.getId());
        System.out.println(taskManager.getAllTasks());

        System.out.println("Проверка в истории");
        System.out.println(taskManager.getHistory());
        taskManager.deleteTask(task4.getId());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getHistory());
        System.out.println();

        System.out.println("Восстанавливаем состояние");
        Task task44 = taskManager.saveTask(new Task("Таск1", "Тестовый таск"));
        Task task5 = taskManager.saveTask(new Task("Таск2", "Тестовый таск"));
        Task task6 = taskManager.saveTask(new Task("Таск3", "Тестовый таск"));
        taskManager.saveTask(new Task("Таск4", "Тестовый таск"));
        System.out.println(taskManager.getTask(task44.getId()));
        System.out.println(taskManager.getTask(task5.getId()));
        System.out.println(taskManager.getTask(task6.getId()));
        System.out.println(taskManager.getHistory());
        System.out.println();

        System.out.println("Проверка deleteAllTasks");
        taskManager.deleteAllTasks();
        System.out.println(taskManager.getHistory());
        taskManager.deleteAllTasks();

        System.out.println("Проверяем Эпики и сабтаски");
        Epic epic1 = taskManager.saveEpic(new Epic("Epic1", "Description epic1"));
        Epic epic2 = taskManager.saveEpic(new Epic("Epic2", "Description epic2"));
        Epic epic3 = taskManager.saveEpic(new Epic("Epic3", "Description epic3"));
        System.out.println();

        System.out.println("Test getEpic");
        System.out.println(taskManager.getEpic(epic1.getId()));
        System.out.println(taskManager.getEpic(epic2.getId()));
        System.out.println(taskManager.getEpic(epic3.getId()));

        System.out.println("Проверка в истории");
        System.out.println(taskManager.getHistory());
        System.out.println();

        System.out.println("Удаление эпика по id");
        System.out.println("Удаляем эпика с id: " + epic1.getId());
        taskManager.deleteEpic(epic1.getId());
        System.out.println(taskManager.getAllEpics());
        System.out.println("Удаляем эпика с id: " + epic2.getId());
        taskManager.deleteEpic(epic2.getId());
        System.out.println(taskManager.getAllEpics());
        System.out.println("Удаляем эпика с id: " + epic3.getId());
        taskManager.deleteEpic(epic3.getId());
        System.out.println("Удаляем эпика с id: " + epic3.getId());
        System.out.println(taskManager.getAllEpics());
        System.out.println("Check getHistory");
        System.out.println(taskManager.getHistory());
        System.out.println();

        System.out.println("Check deleteAllEpic");
        Epic epic4 = taskManager.saveEpic(new Epic("Epic4", "Description epic4"));
        Epic epic5 = taskManager.saveEpic(new Epic("Epic5", "Description epic5"));
        Epic epic6 = taskManager.saveEpic(new Epic("Epic6", "Description epic6"));
        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getEpic(epic3.getId());

        taskManager.deleteAllEpic();
        taskManager.deleteAllEpic();
        System.out.println();

        System.out.println("Test subtask");
        Epic epic10 = taskManager.saveEpic(new Epic("Epic10", "Description epic10"));
        Subtask subtask = taskManager.saveSubtask(
                new Subtask("Subtask0", "description subtask0", epic10.getId()));
        Subtask subtask1 = taskManager.saveSubtask(
                new Subtask("Subtask1", "description subtask1", epic10.getId()));
        Subtask subtask2 = taskManager.saveSubtask(
                new Subtask("Subtask2", "description subtask2", epic10.getId()));
        Subtask subtask3 = taskManager.saveSubtask(
                new Subtask("Subtask3", "description subtask3", epic10.getId()));
        System.out.println();

//        System.out.println("Test deleteEpic"); //работает
//        taskManager.deleteEpic(epic10.getId());
//        taskManager.deleteAllEpic();

//        System.out.println("Test deleteSubtask"); //работает
//        taskManager.deleteTask(subtask2.getEpicId());
//        taskManager.deleteAllSubtask();

    }
}
