package manager;

import models.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    public static final String path = "src/main/resources/save.cvs";
    File file = new File(path);

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager(file);
    }

//    @AfterEach
//    public void afterEach() {
//        try {
//            Files.delete(path);
//        } catch (IOException exception) {
//            System.out.println(exception.getMessage());
//        }
//    }

    @Test
    protected void shouldWriteTask() throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        Epic epic = manager.saveEpic(new Epic("Epic", "Desc"));
        manager.save();
        List<String> strings = Files.readAllLines(Path.of(path));
        assertEquals(FileBackedTasksManager.FIRST_STRING, strings.get(0));
        String expected = epic.getId() + "," + epic.getTaskType() + "," + epic.getName() + "," + epic.getStatus() +
                "," + epic.getDescription() + "," + epic.getStartTime() + "," + epic.getDuration() + ",";
        System.out.println(expected);
        assertEquals(expected, strings.get(1));
    }

    @Test
    protected void shouldBackedTask() {
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.
                loadFromFile(new File("src/main/resources/save.cvs"));
        assertFalse(fileBackedTasksManager.priorityTasks.isEmpty());
    }
}