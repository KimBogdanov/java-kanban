package manager;

import models.Task;

import java.util.List;

public interface HistoryManager {
    void addTask(Task task);
    List<Task> getHistory();
    void remove(int id);
    boolean contains(Integer id);
}
