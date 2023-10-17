package manager;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;

public interface TaskManager {
    Task saveTask(Task task);

    Subtask saveSubtask(Subtask subtask);

    Epic saveEpic(Epic epic);

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    void deleteAllTasks();

    void deleteAllSubtask();

    void deleteAllEpic();

    void deleteTask(Integer id);

    void deleteSubtask(Integer id);

    void deleteEpic(Integer id);

    void updateTask(Task taskNew);

    void updateEpic(Epic epicNew);

    void updateSubtask(Subtask subtaskNew);

    List<Subtask> getSubtaskForEpic(Epic epic);

    void updateStatusEpic(int epicId);

    List<Task> getHistory();

    void calculateEpicTime(Epic epic);

    List<Task> getPrioritizedTasks();
}
