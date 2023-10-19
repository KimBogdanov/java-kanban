package manager;

import Server.KVServer;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    KVServer kvServer;

    @BeforeEach
    public void setUP() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterEach
    public void shutDown() {
        kvServer.stop();
    }

    @Test
    public void testLoadFromHttpServer() {
        HttpTaskManager manager = new HttpTaskManager();

        Task saveTask = manager.saveTask(new Task("Task", "Desc"
                ,LocalDateTime.of(2023, 1, 1, 1, 0), 60));
        Epic saveEpic = manager.saveEpic(new Epic("Epic", "Desc"));
        Subtask saveSubtask = manager.saveSubtask(new Subtask("Subtask", "Desc", saveEpic.getId()));

        manager.getTask(saveTask.getId());
        manager.getEpic(saveEpic.getId());
        manager.getSubtask(saveSubtask.getId());
        List<Task> history = manager.getHistory();

        HttpTaskManager managerLoad = new HttpTaskManager();
        Task loadTask = managerLoad.getTask(saveTask.getId());
        Epic loadEpic = managerLoad.getEpic(saveEpic.getId());
        Subtask loadSubtask = managerLoad.getSubtask(saveSubtask.getId());

        assertEquals(saveTask, loadTask);
        assertEquals(saveEpic, loadEpic);
        assertEquals(saveSubtask, loadSubtask);

        assertEquals(manager.getHistory(), managerLoad.getHistory());

        assertEquals(manager.getAllTasks(), managerLoad.getAllTasks());
        assertEquals(manager.getAllEpics(), managerLoad.getAllEpics());
        assertEquals(manager.getAllSubtasks(), managerLoad.getAllSubtasks());
    }
}