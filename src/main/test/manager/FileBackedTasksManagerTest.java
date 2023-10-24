package manager;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    public static final String pathSave = "src/main/resources/save.cvs";
    public static final String pathLoad = "src/main/resources/load.cvs";
    public static final String path = "src/main/resources/file.cvs";
    File fileSave = new File(pathSave);
    File fileLoad = new File(pathLoad);
    File file = new File(path);
    public FileBackedTasksManagerTest() {
        manager = new FileBackedTasksManager();
    }

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager(file);
    }

    @Test
    protected void shouldWriteTask() throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager(fileSave);
        Epic epic = manager.saveEpic(new Epic("Epic", "Desc"));
        manager.save();
        List<String> strings = Files.readAllLines(Path.of(pathSave));
        assertEquals(FileBackedTasksManager.FIRST_STRING, strings.get(0));
        String expected = epic.getId() + "," + epic.getTaskType() + "," + epic.getName() + "," + epic.getStatus() +
                "," + epic.getDescription() + "," + epic.getStartTime() + "," + epic.getDuration() + ",";
        assertEquals(expected, strings.get(1));
    }

    @Test
    protected void shouldBackedTask() {
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(fileLoad);

        assertEquals(new Epic(0,
                "Epic",
                "Desc",
                Status.NEW,
                null,
                null), manager.getEpic(0));

        assertEquals(new Task(2,
                        "Task",
                        "Desc",
                        Status.NEW,
                        LocalDateTime.of(2020, 01, 01, 1, 1), 50L),
                manager.getTask(2));

        assertEquals(new Subtask(1,
                "Subtask",
                "Desc",
                Status.NEW,
                null,
                null,
                0), manager.getSubtask(1));
    }

    @Test
    protected void shouldBackedHistoryTask() {
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(fileLoad);
        List<Integer> idTaskHistory = manager.getHistory().stream()
                .map(Task::getId)
                .toList();
        assertEquals(List.of(2, 0, 1), idTaskHistory);
    }
}