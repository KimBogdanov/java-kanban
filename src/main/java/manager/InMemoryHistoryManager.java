package manager;

import models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int LIMIT = 10;
    private final List<Task> history = new ArrayList<>();

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        history.add(task);
        if (history.size() > LIMIT) {
            history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
