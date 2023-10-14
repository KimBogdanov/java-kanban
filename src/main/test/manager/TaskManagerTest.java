package manager;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @BeforeEach
    protected void createManager() {
        manager = (T) Managers.getDefault();
    }

    protected Task createTask() {
        return new Task("Name",
                "Description",
                LocalDateTime.of(2023, 10, 10, 10, 10),
                60);
    }

    protected Subtask createSubtaskAndParentEpic() {
        Epic epic = new Epic(555, "Name", "Description", Status.NEW,
                LocalDateTime.of(2023, 10, 10, 10, 10),
                60);
        return new Subtask(65,"Name", "Description", Status.NEW,
                LocalDateTime.of(2023, 10, 10, 10, 10),
                60, 555);
    }

    protected Epic createEpic() {
        return new Epic(555, "Name", "Description", Status.NEW,
                LocalDateTime.of(2023, 10, 10, 10, 10),
                60);
    }

    @Test
    protected void shouldReturnEndTime() {
        Task saveTask = manager.saveTask(createTask());
        System.out.println(saveTask.getStartTime());
        System.out.println(saveTask.getEndTime());
    }

    @Test
    protected void shouldSaveAndGetTask() {
        Task taskSave = manager.saveTask(createTask());
        assertEquals(List.of(taskSave), manager.getAllTasks());
        Task taskGet = manager.getTask(taskSave.getId());
        assertEquals(taskSave, taskGet);
    }

    @Test
    protected void shouldSaveAndGetTaskIncorrectNull() {
        Task taskSave = manager.saveTask(null);
        assertNull(taskSave);
        assertTrue(manager.getAllTasks().isEmpty());
        Task taskGet = manager.getTask(555);
        assertNull(taskGet);
    }

    @Test
    protected void shouldGetAllTaskAndNull() {
        assertTrue(manager.getAllTasks().isEmpty());
        for (int i = 1; i < 5; i++) {
            manager.saveTask(createTask());
            assertEquals(i, manager.getAllTasks().size());
        }
    }

    @Test
    protected void shouldNotSaveSameTask() {
        Task taskSave = manager.saveTask(createTask());
        manager.saveTask(taskSave);
        manager.saveTask(taskSave);
        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    protected void shouldDeleteTaskIncorrectIdAndNull() {
        Task taskSave = manager.saveTask(createTask());
        manager.deleteTask(taskSave.getId());
        manager.deleteTask(999);
        manager.deleteTask(null);
        assertFalse(manager.getAllTasks().contains(taskSave));
    }

    @Test
    protected void shouldDeleteAllTask() {
        for (int i = 0; i < 6; i++) {
            manager.saveTask(createTask());
        }
        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    protected void shouldSaveAndGetEpicIncorrectNull() {
        Epic epicSave = manager.saveEpic(null);
        assertNull(epicSave);
        assertTrue(manager.getAllEpics().isEmpty());
        Epic epicGet = manager.getEpic(555);
        assertNull(epicGet);
    }

    @Test
    protected void shouldDeleteEpics() {
        for (int i = 0; i < 6; i++) {
            manager.saveEpic(createEpic());
        }
        manager.deleteAllEpic();
        assertTrue(manager.getAllEpics().isEmpty());
    }
}