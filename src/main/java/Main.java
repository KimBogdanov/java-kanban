import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager defaultHistory = Managers.getDefaultHistory();

        System.out.println("Создание эпика");
        Epic epic = new Epic("Эпик без подзадач", "Эпик без подзадач");
        taskManager.saveEpic(epic);

        Epic epic2 = new Epic("Эпик с подзадачами", "Эпик с подзадачами");
        taskManager.saveEpic(epic2);

        Subtask subtask = new Subtask("Сабтаск 1", "Сабтаск, epic2", epic2.getId());
        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск, epic2", epic2.getId());
        Subtask subtask3 = new Subtask("Сабтаск 3", "Сабтаск, epic2", epic2.getId());

        taskManager.saveSubtask(subtask);
        taskManager.saveSubtask(subtask2);
        taskManager.saveSubtask(subtask3);
        Task task = new Task("Таск1", "Тестовый таск");
        taskManager.saveTask(task);


        System.out.println();

        System.out.println("Получение истории");
        taskManager.getEpic(epic2.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getTask(task.getId());
        System.out.println(taskManager.getHistory());

        taskManager.deleteTask(task.getId());
        System.out.println(taskManager.getHistory());

        taskManager.getSubtask(subtask.getId());
        System.out.println(taskManager.getHistory());

        taskManager.deleteSubtask(subtask.getId());
        System.out.println(taskManager.getHistory());

        taskManager.deleteEpic(epic2.getId());
        System.out.println(taskManager.getHistory());

    }
}
