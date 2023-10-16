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

    protected Task createTask() {
        return new Task("Name",
                "Description",
                LocalDateTime.of(2023, 10, 10, 10, 10),
                60);
    }

    protected Subtask createSubtaskAndParentEpic() {
        Epic epic = new Epic(555, "Name", "Description", Status.NEW,
                LocalDateTime.of(2023, 10, 10, 10, 10),
                60L);
        return new Subtask(65, "Name", "Description", Status.NEW,
                LocalDateTime.of(2023, 10, 10, 10, 10),
                60L, 555);
    }

    protected Subtask createSubtask() {
        return new Subtask(null, "Name", "Description", Status.NEW,
                LocalDateTime.now(), 40L, 555);
    }

    protected Epic createEpic() {
        return new Epic(null, "Name", "Description", Status.NEW,
                null, null);
    }

    @Test
    protected void shouldCalculateTimeForEpicIfNoSubtask() {
        Epic epic = manager.saveEpic(createEpic());
        manager.calculateEpicTime(epic);
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertNull(epic.getDuration());
    }

    @Test
    protected void shouldCalculateTimeForEpicIfSubtaskNoTime() {
        Epic epic = manager.saveEpic(createEpic());
        Subtask subtask = createSubtask();
        subtask.setEpicId(epic.getId());
        subtask.setStartTime(null);
        subtask.setDuration(null);

        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertNull(epic.getDuration());
    }

    @Test
    protected void shouldCalculateTimeForEpicOneSubtask() {
        Epic epic = manager.saveEpic(createEpic());
        Subtask subtask = createSubtask();
        subtask.setEpicId(epic.getId());
        manager.saveSubtask(subtask);
        assertEquals(subtask.getStartTime(), epic.getStartTime());
        assertEquals(subtask.getEndTime(), epic.getEndTime());
        assertEquals(subtask.getDuration(), epic.getDuration());
    }

    @Test
    protected void shouldCalculateTimeForEpicMoreSubtask() {
        Epic epic = manager.saveEpic(createEpic());
        Subtask subtask = manager.saveSubtask(new Subtask(null, "Name", "Description", Status.NEW,
                LocalDateTime.of(2008, 1, 1, 1, 1), 40L, epic.getId()));

        Subtask subtask1 = manager.saveSubtask(new Subtask(null, "Name", "Description", Status.NEW,
                LocalDateTime.of(2030, 1, 1, 1, 1), 40L, epic.getId()));

        Subtask subtask2 = manager.saveSubtask(new Subtask(null, "Name", "Description", Status.NEW,
                LocalDateTime.of(2025, 1, 1, 1, 1), 40L, epic.getId()));

        assertEquals(subtask.getStartTime(), epic.getStartTime());
        assertEquals(subtask1.getEndTime(), epic.getEndTime());
        assertEquals(subtask.getDuration()
                + subtask1.getDuration()
                + subtask2.getDuration(), epic.getDuration());
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