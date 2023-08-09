import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("Сохранение!");
        Task task = new Task("Таск1", "Тестовый таск");
        taskManager.saveTask(task);
        Epic epic = new Epic("Эпик1", "Тестовый Эпик");
        taskManager.saveEpic(epic);
        Subtask subtask = new Subtask("Сабтаск1", "Сабтаск к Эпик1", epic.getId());
        taskManager.saveSubtask(subtask);
        System.out.println();

//        System.out.println("Получение тасков!");
//        System.out.println(taskManager.getTask(task.getId()));
//        System.out.println(taskManager.getSubtask(subtask.getId()));
//        System.out.println(taskManager.getEpic(epic.getId()));
//        System.out.println();
//
//        System.out.println("Получение всех задач!");
//        System.out.println(taskManager.getAllTasks());
//        System.out.println(taskManager.getAllSubtasks());
//        System.out.println(taskManager.getAllEpics());
//        System.out.println();
//
//        System.out.println("Удаление всех задач");
//        taskManager.deleteAllTasks();
//        System.out.println(taskManager.getAllTasks().isEmpty());
//        taskManager.deleteAllSubtask();
//        System.out.println(taskManager.getAllSubtasks().isEmpty());
//        taskManager.deleteAllEpic();
//        System.out.println(taskManager.getAllEpics().isEmpty());
//
//        taskManager.saveTask(task);
//        taskManager.saveEpic(epic);
//        taskManager.saveSubtask(subtask);


//        System.out.println("Получение всех задач после удаления");
//        System.out.println(taskManager.getAllTasks());
//        System.out.println(taskManager.getAllSubtasks());
//        System.out.println(taskManager.getAllEpics());
//        System.out.println();

        System.out.println("Удаление по id");
        taskManager.deleteTask(task.getId());
        taskManager.deleteSubtask(subtask.getId());
        taskManager.deleteEpic(epic.getId());
        System.out.println(taskManager.getAllTasks().isEmpty());
        System.out.println(taskManager.getAllSubtasks().isEmpty());
        System.out.println(taskManager.getAllEpics().isEmpty());

        System.out.println("Удаление сабтасков вместе с Эпиком");
        taskManager.saveEpic(epic);
        taskManager.saveSubtask(subtask);
        taskManager.deleteEpic(epic.getId());

        System.out.println(taskManager.getAllSubtasks().isEmpty());
        System.out.println(taskManager.getAllEpics().isEmpty());

        taskManager.saveTask(task);
        taskManager.saveEpic(epic);
        taskManager.saveSubtask(subtask);

        System.out.println("Получение всех задач!");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println();

        System.out.println("Получение всех задач!");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println();

        System.out.println("Обновление тасков");
        Task taskNew = taskManager.getTask(task.getId());
        taskNew.setName("Обновленный таск");
        taskNew.setDescription("Обновление описания таска");
        taskManager.updateTask(taskNew);
        System.out.println(taskManager.getTask(taskNew.getId()).getName());
        System.out.println(taskManager.getTask(taskNew.getId()).getDescription());
        System.out.println();

        System.out.println("Обновление эпика");
        Epic epicNew = taskManager.getEpic(epic.getId());
        epicNew.setName("Обновленный эпик");
        epicNew.setDescription("Обновление описания Эпика");
        taskManager.updateEpic(epicNew);
        System.out.println(taskManager.getEpic(epicNew.getId()).getName());
        System.out.println(taskManager.getEpic(epicNew.getId()).getDescription());
        System.out.println(taskManager.getEpic(epicNew.getId()).getSubtaskList());

        Subtask subtaskNew = taskManager.getSubtask(subtask.getId());
        subtaskNew.setName("Обновленный сабтаск");
        subtaskNew.setDescription("Обновление описания Сабтаска");
        taskManager.updateSubtask(subtaskNew);
        System.out.println(taskManager.getSubtask(subtaskNew.getId()).getName());
        System.out.println(taskManager.getSubtask(subtaskNew.getId()).getDescription());
        System.out.println(taskManager.getSubtask(subtaskNew.getId()).getEpicId());
        System.out.println();

        System.out.println("Получение субтасков эпика");
        List<Subtask> subtasksForEpic = taskManager.getSubtaskForEpic(epicNew);
        System.out.println(subtasksForEpic);

        System.out.println("Обновление статусов");
    }
}
