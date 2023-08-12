package manager;

import models.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int LIMIT = 10;
    private final Queue<Task> history = new LinkedList<>();

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        history.add(task);
        if (history.size() > LIMIT) {
            history.remove();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
