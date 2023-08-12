import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        System.out.println("Сохранение!");
        Task task = new Task("Таск1", "Тестовый таск");
        taskManager.saveTask(task);
        taskManager.saveTask(new Task("Таск2", "Тестовый такс2"));
        taskManager.saveTask(new Task("Таск3", "Тестовый такс2"));
        taskManager.saveTask(new Task("Таск4", "Тестовый такс2"));
        Epic epic = new Epic("Эпик1", "Тестовый Эпик");
        taskManager.saveEpic(epic);
        Subtask subtask = new Subtask("Сабтаск1", "Сабтаск к Эпик1", epic.getId());
        taskManager.saveSubtask(subtask);
        System.out.println();

        System.out.println("Получение истории");
        taskManager.getTask(0);
        System.out.println(taskManager.getHistory().size() == 1 ? "Таск сохранился в истории" : "Таск не сохранился");
        taskManager.getEpic(4);
        System.out.println(taskManager.getHistory().size() == 2 ? "Эпик сохранился в истории" : "Эпик не сохранился");
        taskManager.getSubtask(5);
        System.out.println(taskManager.getHistory().size() == 3
                ? "Сабтаск сохранился в истории" : "Сабтаск не сохранился");
        for (int i = 0; i < 15; i++) {
            taskManager.getTask(0);
        }


        System.out.println(taskManager.getHistory().size() < 11);
    }
}
