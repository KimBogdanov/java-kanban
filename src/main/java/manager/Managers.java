package manager;

import java.io.IOException;

public class Managers {
    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
    public static HttpTaskManager getDefault() {
        return new HttpTaskManager();
    }
}