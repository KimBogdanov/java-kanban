package manager;

import exception.ManagerSaveException;
import models.Epic;
import models.Subtask;
import models.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public static FileBackedTasksManager loadFromFile(File file) {
        return null;
    }

    public FileBackedTasksManager(File file) {
        this.file = file;
    }


    @Override
    public Task saveTask(Task task) {
        super.saveTask(task);
        save();
        return task;
    }

    @Override
    public Subtask saveSubtask(Subtask subtask) {
        super.saveSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Epic saveEpic(Epic epic) {
        super.saveEpic(epic);
        save();
        return epic;
    }

    private void save() {
        List<String> allTasks = Stream.of(taskDao.values(), epicDao.values(), subtaskDao.values())
                .flatMap(Collection::stream)
                .map(CSVFormatter::toString)
                .toList();
        String allString = String.join("/n", allTasks);
        String history = CSVFormatter.historyToString(getHistoryManager());

        try (Writer writer = new FileWriter(file)) {
            StringBuilder sb = new StringBuilder();
            sb.append(allString).append("\n").append(history);
            writer.write(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Полундра, все пропало!");

        }
    }
}
